package com.sigat.springboot.app.controllers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.time.LocalDate;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sigat.springboot.app.dto.RecepcionDTO;
import com.sigat.springboot.app.models.entity.Profesional;
import com.sigat.springboot.app.models.entity.Sobreturno;
import com.sigat.springboot.app.models.entity.Turno;
import com.sigat.springboot.app.models.service.EmailService;
import com.sigat.springboot.app.models.service.IEstadoService;
import com.sigat.springboot.app.models.service.IPlanillaDetalleService;
import com.sigat.springboot.app.models.service.IProfesionalService;
import com.sigat.springboot.app.models.service.ISobreTurnoService;
import com.sigat.springboot.app.models.service.ITurnoService;

@Controller
public class RecepcionController {

	@Autowired
	private ITurnoService turnoService;
	@Autowired
	private ISobreTurnoService sobreturnoService;
	@Autowired
	private IEstadoService estadoService;
	@Autowired
	private IProfesionalService profesionalService;
	@Autowired
	private EmailService emailService;
	@Autowired
	IPlanillaDetalleService planilladetalleService;

	/**
	 * VISTA ÚNICA DE RECEPCIÓN (Central de Citas)
	 * Reemplaza funcionalmente a listarTurno y listarSobreturno para el día a día.
	 */
	@GetMapping("/recepcion")
	public String vistaRecepcion(Model model, org.springframework.security.core.Authentication auth) {
	    List<RecepcionDTO> consolidado = new ArrayList<>();

	    // 1. Carga de datos inicial
	    List<Turno> turnosRaw = turnoService.findAll();
	    List<Sobreturno> sobreturnosRaw = sobreturnoService.findAll();

	    // 2. Identificación de Rol y Médico
	    boolean esMedico = auth != null && auth.getAuthorities().stream()
	                        .anyMatch(a -> a.getAuthority().equals("ROLE_MEDICO"));
	    
	    Profesional medicoLogueado = null;
	    if (esMedico && auth != null) {
	        medicoLogueado = profesionalService.findByUsername(auth.getName());
	    }

	    final Profesional medicoFiltro = medicoLogueado;

		// --- CÁLCULO DE ANULADOS (ID 5 o 6) EXCLUSIVO PARA EL MÉDICO ---
		long totalAnuladosMedico = 0;
		if (esMedico && medicoFiltro != null) {
		    long tAnulados = (turnosRaw != null) ? turnosRaw.stream()
		        .filter(t -> t.getProfesional() != null && t.getProfesional().getId().equals(medicoFiltro.getId()))
		        .filter(t -> t.getEstado() != null && (t.getEstado().getId() == 5L || t.getEstado().getId() == 6L)).count() : 0;

		    long sAnulados = (sobreturnosRaw != null) ? sobreturnosRaw.stream()
		        .filter(s -> s.getProfesional() != null && s.getProfesional().getId().equals(medicoFiltro.getId()))
		        .filter(s -> s.getEstado() != null && (s.getEstado().getId() == 5L || s.getEstado().getId() == 6L)).count() : 0; // CORREGIDO: getEstado() directo
		    
		    totalAnuladosMedico = tAnulados + sAnulados;
		}
		model.addAttribute("anuladosMedicoBD", totalAnuladosMedico);

		// 3. CÁLCULO DE AGENDA TOTAL NETO DEL DÍA (Excluyendo ID 5 y 6)
		long totalAgendaMedico = 0;
		if (esMedico && medicoFiltro != null) {
		    long tCount = (turnosRaw != null) ? turnosRaw.stream()
		        .filter(t -> t.getProfesional() != null && t.getProfesional().getId().equals(medicoFiltro.getId()))
		        .filter(t -> t.getEstado() != null && t.getEstado().getId() != 5L && t.getEstado().getId() != 6L).count() : 0; 

		    long sCount = (sobreturnosRaw != null) ? sobreturnosRaw.stream()
		        .filter(s -> s.getProfesional() != null && s.getProfesional().getId().equals(medicoFiltro.getId()))
		        .filter(s -> s.getEstado() != null && s.getEstado().getId() != 5L && s.getEstado().getId() != 6L).count() : 0; // CORREGIDO: getEstado() directo
		    
		    totalAgendaMedico = tCount + sCount;
		}
		model.addAttribute("totalAgenda", totalAgendaMedico);

		// 4. Filtrado de Turnos
		if (turnosRaw != null) {
		    turnosRaw.stream()
		        .filter(t -> t.getEstado() != null)
		        // MODIFICACIÓN: Removemos la exclusión física para que los datos viajen y JS pueda computarlos
		        // Filtro por profesional (Solo aplica si es médico logueado)
		        .filter(t -> {
		            if (!esMedico || medicoFiltro == null) return true;
		            return t.getProfesional() != null && t.getProfesional().getId().equals(medicoFiltro.getId());
		        })
		        .forEach(t -> consolidado.add(mapToDTO(t)));
		    }

		// 5. Filtrado de Sobreturnos
		if (sobreturnosRaw != null) {
		    sobreturnosRaw.stream()
		        .filter(s -> s.getEstado() != null)
		        // Filtro por profesional (Solo aplica si es médico logueado)
		        .filter(s -> {
		            if (!esMedico || medicoFiltro == null) return true;
		            return s.getProfesional() != null && s.getProfesional().getId().equals(medicoFiltro.getId());
		        })
		        .forEach(s -> consolidado.add(mapToDTO(s)));
		}

	    // 6. Ordenamiento y Atributos Finales
	    consolidado.sort(Comparator.comparing(RecepcionDTO::getFechaHora));
	    
	    model.addAttribute("listado", consolidado);
	    model.addAttribute("titulo", esMedico ? "Mis Pacientes en Espera" : "Panel de Recepción General");
	    
	    return "recepcion/listarRecepcion";
	}
	

	/**
	 * LISTADO GENERAL (Archivo y Auditoría)
	 * En esta función quitamos los filtros para que aparezcan TODOS los estados (Atendidos, Anulados, etc.)
	 */
	@GetMapping("/turnos/listarGeneral")
	public String listarGeneral(Model model) {
	    List<RecepcionDTO> consolidado = new ArrayList<>();

	    // USAMOS EL MÉTODO QUE CREAMOS A LA MAÑANA
	    List<Turno> turnos = turnoService.findAllTotal(); 
	    List<Sobreturno> sobreturnos = sobreturnoService.findAll(); // Si tienes un findAllTotal para sobreturnos, mejor

	    // Mapeamos TODO sin excepciones
	    if (turnos != null) turnos.forEach(t -> consolidado.add(mapToDTO(t)));
	    if (sobreturnos != null) sobreturnos.forEach(s -> consolidado.add(mapToDTO(s)));

	    // Ordenamos: Lo más nuevo arriba
	    consolidado.sort(Comparator.comparing(RecepcionDTO::getFechaHora).reversed());

	    model.addAttribute("listado", consolidado);
	    model.addAttribute("titulo", "Historial Completo (Sin Filtros)");
	    
	    return "recepcion/listarRecepcion";
	}





	@PostMapping("/recepcion/dar-presente")
	@ResponseBody
	public String darPresente(@RequestParam Long id, @RequestParam String tipo) {
		try {
			if ("TURNO".equals(tipo)) {
				Turno t = turnoService.findOne(id);
				t.setEstado(estadoService.findById(10L)); // 10 = EN SALA
				turnoService.save(t);
			} else {
				Sobreturno s = sobreturnoService.findOne(id);
				s.setEstado(estadoService.findById(10L));
				sobreturnoService.save(s);
			}
			return "OK";
		} catch (Exception e) {
			return "ERROR";
		}
	}

	@PostMapping("/recepcion/finalizar-manual")
	@ResponseBody
	@Transactional
	public String finalizarManual(@RequestParam Long id, @RequestParam String tipo) {
	    try {
	        if ("TURNO".equals(tipo)) {
	            Turno t = turnoService.findOne(id);
	            if (t != null) {
	                // CORRECCIÓN: Cambiamos a 11L (SE RETIRÓ)
	                t.setEstado(estadoService.findById(11L)); 
	                turnoService.save(t);
	                
	                t.getPaciente().getApellido();
	                t.getProfesional().getApellido();

	                emailService.enviarMailRetiroVoluntario(t); 
	                System.out.println("DEBUG: Turno ID " + id + " marcado como SE RETIRÓ (11). Mail enviado.");
	            }
	        } else {
	            Sobreturno s = sobreturnoService.findOne(id);
	            if (s != null) {
	                // CORRECCIÓN: Cambiamos a 11L (SE RETIRÓ)
	                s.setEstado(estadoService.findById(11L)); 
	                sobreturnoService.save(s);
	                
	                s.getPaciente().getApellido();
	                s.getProfesional().getApellido();

	                emailService.enviarMailRetiroVoluntario(s); 
	                System.out.println("DEBUG: Sobreturno ID " + id + " marcado como SE RETIRÓ (11). Mail enviado.");
	            }
	        }
	        return "OK";
	    } catch (Exception e) { 
	        System.err.println("Error crítico en finalizarManual: " + e.getMessage());
	        return "ERROR"; 
	    }
	}




	// --- MÉTODOS DE MAPEO (Se agregaron IDs para funcionalidad de botones) ---

	private RecepcionDTO mapToDTO(Turno t) {
		RecepcionDTO dto = new RecepcionDTO();
		dto.setIdOriginal(t.getId());
		dto.setPacienteId(t.getPaciente().getId());
		dto.setFechaHora(t.getPlanilladetalle().getRangoFechaHora()); 
		dto.setPaciente(t.getPaciente().getApellido() + " " + t.getPaciente().getNombre());
		dto.setDni(t.getPaciente().getDni());
		dto.setMedico(t.getProfesional().getApellido());
		dto.setEspecialidad(t.getEspecialidad().getEspecialidadNombre());
		dto.setTipo("TURNO");
		dto.setEstadoId(t.getEstado().getId());
		dto.setEstadoNombre(t.getEstado().getNombre());
		
		// // AGREGADO: IDs necesarios para botones de "Editar/Volver"
		dto.setProfesionalId(t.getProfesional().getId());
		dto.setEspecialidadId(t.getEspecialidad().getId());
		
		// --- ASIGNACIÓN DE BOX DETECTADA DESDE LA PLANILLA ---
		if (t.getPlanilladetalle() != null) {
			dto.setBox(t.getPlanilladetalle().getBox());
		}
		
		return dto;
	}

	private RecepcionDTO mapToDTO(Sobreturno s) {
		RecepcionDTO dto = new RecepcionDTO();
		dto.setIdOriginal(s.getId());
		dto.setPacienteId(s.getPaciente().getId());
		if (s.getRangoFechaHora() != null) {
			dto.setFechaHora(Date.from(s.getRangoFechaHora()
					.atZone(ZoneId.systemDefault()).toInstant()));
		}
		dto.setPaciente(s.getPaciente().getApellido() + " " + s.getPaciente().getNombre());
		dto.setDni(s.getPaciente().getDni());
		dto.setMedico(s.getProfesional().getApellido());
		dto.setEspecialidad(s.getEspecialidad().getEspecialidadNombre());
		dto.setTipo("SOBRETURNO");
		dto.setEstadoId(s.getEstado().getId());
		dto.setEstadoNombre(s.getEstado().getNombre());

		dto.setProfesionalId(s.getProfesional().getId());
		dto.setEspecialidadId(s.getEspecialidad().getId());
		
		// --- LEEMOS EL BOX EMBAJADOR GUARDADO EN LA OBSERVACIÓN ---
		String obs = s.getObservacion() != null ? s.getObservacion() : "";
		if (obs.contains("[BOX: ")) {
		    // Cortamos el string para capturar el número exacto del box
		    String numeroBox = obs.substring(obs.indexOf("[BOX: ") + 6, obs.indexOf("]"));
		    dto.setBox(numeroBox);
		    // Limpiamos la observación para que en la tabla no se vea la marca técnica del box
		    dto.setObservacion(obs.replace("[BOX: " + numeroBox + "]", "").replace("[]", "").trim());
		} else {
		    dto.setObservacion(obs);
		}
		
		return dto;
	}




}


package com.sigat.springboot.app.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.sigat.springboot.app.models.entity.Especialidad;
import com.sigat.springboot.app.models.entity.Estado;
import com.sigat.springboot.app.models.entity.Paciente;
import com.sigat.springboot.app.models.entity.PlanillaCabecera;
import com.sigat.springboot.app.models.entity.PlanillaDetalle;
import com.sigat.springboot.app.models.entity.Profesional;
import com.sigat.springboot.app.models.entity.Sobreturno;
import com.sigat.springboot.app.models.service.BackupService;
import com.sigat.springboot.app.models.service.EmailService;
import com.sigat.springboot.app.models.service.IEspecialidadService;
import com.sigat.springboot.app.models.service.IPacienteService;
import com.sigat.springboot.app.models.service.IPlanillaDetalleService;
import com.sigat.springboot.app.models.service.IProfesionalService;
import com.sigat.springboot.app.models.service.ISobreTurnoService;
import com.sigat.springboot.app.util.paginator.PageRender;

@Controller
@RequestMapping("/sobreturnos")
public class SobreturnoController {

	protected final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private IProfesionalService profesionalService;
	@Autowired
	private IEspecialidadService especialidadService;
	@Autowired
	private IPacienteService pacienteService;
	@Autowired
	private ISobreTurnoService sobreturnoService;
	@Autowired
	private IPlanillaDetalleService planilladetalleService;
	@Autowired
	private MessageSource messageSource;
	@Autowired
	private BackupService backupService;

	// Metodo para abrir formulario (GET)
	@GetMapping("/formSobreturno")
	public String formSobreturno(Sobreturno sobreturno, Model model, 
			@RequestParam(name = "profId", required = false) Long profId,
			@RequestParam(name = "especId", required = false) Long especId) {

		model.addAttribute("titulo", "Generar Sobreturno Nuevo..");
		model.addAttribute("titulobtnConfirmar", "Continuar alta de Sobreturno");
		model.addAttribute("profesionales", profesionalService.findAll());
		
		// --- SOLUCIÓN ELITE: Buscamos el objeto completo si viene el ID ---
		//---  cuando el listTurno seleccionamos dar otro turno con el mismo profesional ---
		if (profId != null && profId > 0) {
	        Profesional prof = profesionalService.findOne(profId);
	        model.addAttribute("profesionalSeleccionado", prof);
	    }
	    // --------------------------------------------------------------------
		
		model.addAttribute("profIdPre", profId);
		model.addAttribute("especIdPre", especId);
				
		return "sobreturnos/formSobreturno";
	}

	// Metodo listar sin paginacion
	@GetMapping("/listarSobreturno")
	public String listarSobreturno(Model model, Authentication authentication, Locale locale) {

		if (authentication != null) {
			logger.info("Hola usuario autenticado: ".concat(authentication.getName()));
		}

		//List<Sobreturno> sobreturno = sobreturnoService.findAll();
		// CAMBIO AQUÍ: Usamos findAllActivos() en lugar de findAll().
	    List<Sobreturno> sobreturno = sobreturnoService.findAllActivosYAtendidos();
		model.addAttribute("titulo", messageSource.getMessage("text.sobreturno.listar.titulo", null, locale));
		model.addAttribute("sobreturnos", sobreturno);
		//return "sobreturnos/listarSobreturno";
		return "redirect:/recepcion";
	}

	// Metodo guardar procesando el formulario (POST) - CONFIGURACIÓN CON OBSERVACIÓN SEGURA
	@PostMapping("/formSobreturno")
	@ResponseBody 
	public ResponseEntity<?> saveSobreturno(Sobreturno sobreturno, Model model, 
	        @ModelAttribute("profesional") Long profesionalId,
	        @ModelAttribute("especialidad") Long especialidadId, 
	        @ModelAttribute("pacienteId") Long pacienteId,
	        @ModelAttribute("planillaCabId") String planillaCabId,
	        @RequestParam(value = "planillaDetId", required = false) String planillaDetIdStr, // <-- CAPTURAMOS EL INPUT HIDDEN
	        SessionStatus status, RedirectAttributes flash,
	        @RequestParam("rangoFechaHora") String rangoFechaHoraStr) {

	    // 1. CONVERSIÓN MANUAL (Soluciona el error de Java 17 y Reflection)
	    LocalDateTime rangoFechaHora = null;
	    try {
	        if (rangoFechaHoraStr != null && !rangoFechaHoraStr.isEmpty()) {
	            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	            rangoFechaHora = LocalDateTime.parse(rangoFechaHoraStr, formatter);
	        }
	    } catch (Exception e) {
	        return ResponseEntity.badRequest().body("{\"mensaje\": \"Formato de fecha inválido\"}");
	    }

	    // Seteo de datos para el objeto Sobreturno
	    Paciente p = new Paciente();
	    p.setId(pacienteId);
	    sobreturno.setPaciente(p);

	    Profesional prof = new Profesional();
	    prof.setId(profesionalId);
	    sobreturno.setProfesional(prof);

	    Especialidad esp = new Especialidad();
	    esp.setId(especialidadId);
	    sobreturno.setEspecialidad(esp);

	    Long planillaCabeceraId = Long.parseLong(planillaCabId);
	    PlanillaCabecera pc = new PlanillaCabecera();
	    pc.setId(planillaCabeceraId);
	    sobreturno.setPlanillacabecera(pc);
	    
	    sobreturno.setRangoFechaHora(rangoFechaHora);
	    
	    // --- EXTRAEMOS EL BOX ASOCIADO AL HIDDEN DETALLE Y LO AGREGAMOS A LA OBSERVACIÓN ---
	    try {
	        if (planillaDetIdStr != null && !planillaDetIdStr.isEmpty()) {
	            Long detId = Long.parseLong(planillaDetIdStr);
	            // Buscamos el detalle por su ID para extraer el número de Box real de ese casillero
	            var detalle = planilladetalleService.findOne(detId); // Asegurate de usar tu método (findOne o findById)
	            if (detalle != null && detalle.getBox() != null) {
	                // Traemos la observación que escribió la recepcionista (si escribió algo)
	                String obsActual = sobreturno.getObservacion() != null ? sobreturno.getObservacion() : "";
	                // Lo concatenamos al final de forma limpia
	                sobreturno.setObservacion(obsActual.trim() + " [BOX: " + detalle.getBox() + "]");
	            }
	        }
	    } catch (Exception e) {
	        // Si no encuentra el box por cualquier motivo, que no trabe el guardado general del sobreturno
	    }
	    // -------------------------------------------------------------------------------------
	    
	    try {
	        // EL GUARDADO REAL
	        sobreturnoService.registrarSobreturnoCompleto(sobreturno);
	        status.setComplete();

	        return ResponseEntity.ok().body("{\"status\":\"success\"}");

	    } catch (RuntimeException e) {
	        if ("EL_HORARIO_YA_ESTA_OCUPADO".equals(e.getMessage())) {
	            return ResponseEntity.status(HttpStatus.CONFLICT).body("{\"status\":\"error\", \"mensaje\":\"OCUPADO\"}");
	        }
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"status\":\"error\"}");
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"status\":\"error\"}");
	    }
	}




	/*
	 * //ELIMINAR INDIVIDUAL
	 * 
	 * @GetMapping("/eliminar/{id}") public String eliminar(@PathVariable(value =
	 * "id") Long id, RedirectAttributes flash) { if (id > 0) { // Obtenemos el
	 * objeto sobreturno antes de eliminarlo Sobreturno sobreturno =
	 * sobreturnoService.findOne(id); if (sobreturno != null) { // Eliminamos el
	 * sobreturno x id. sobreturnoService.delete(id);
	 * flash.addFlashAttribute("success", "Sobreturno eliminado con exito."); } else
	 * { flash.addFlashAttribute("danger", "No se pudo eliminar el sobreturno."); }
	 * } return "redirect:/sobreturnos/listarSobreturno"; }
	 */
	
	//Ahora recibirá el ID por parámetro de formulario, no por la URL.
	@PostMapping("/eliminar") 
	public String eliminar(@RequestParam("id") Long id, RedirectAttributes flash) {
	    if (id > 0) {
	        try {
	            // 1. BACKUP DE SEGURIDAD (Usando el nuevo sistema de carpetas)
	            backupService.ejecutarBackup("Sobreturnos");

	            Sobreturno sobreturno = sobreturnoService.findOne(id);
	            if (sobreturno != null) {
	                // 2. CANCELAMOS (Estado 6)
	                Estado estadoCancelado = new Estado();
	                estadoCancelado.setId(6L);
	                sobreturno.setEstado(estadoCancelado);
	                sobreturnoService.save(sobreturno);

	                // 3. GENERAR EL PDF INDIVIDUAL
	                List<Sobreturno> listaUno = new ArrayList<>();
	                listaUno.add(sobreturno);
	                planilladetalleService.generarPdfLlamadosElite(
	                    new ArrayList<>(), listaUno, 
	                    sobreturno.getProfesional(), sobreturno.getEspecialidad(), 
	                    "MANUAL", "INDIVIDUAL", "Sobreturnos"
	                );

	                flash.addFlashAttribute("success", "Sobreturno cancelado y backup realizado correctamente.");
	            }
	        } catch (Exception e) {
	            flash.addFlashAttribute("danger", "Error al cancelar: " + e.getMessage());
	        }
	    }
	    //return "redirect:/sobreturnos/listarSobreturno";
	    return "redirect:/recepcion";
	}

/*
	//ELIMINAR MASIVAMENTE SELECCIONANDO VARIOS CHECKS
	@PostMapping("/cancelar-multiple")
	@ResponseBody
	public String cancelarMultiple(@RequestParam("ids") List<Long> ids) {
	    try {
	    	
	    	// 1. DISPARAR BACKUP
	    	backupService.ejecutarBackup("Sobreturnos");
	        
	        List<Sobreturno> listaParaPdf = new ArrayList<>();
	        
	        // 1. Buscamos y cancelamos uno por uno los seleccionados
	        for (Long id : ids) {
	            Sobreturno s = sobreturnoService.findOne(id);
	            if (s != null) {
	                Estado cancelado = new Estado();
	                cancelado.setId(6L);
	                s.setEstado(cancelado);
	                sobreturnoService.save(s);
	                listaParaPdf.add(s);
	            }
	        }

	        // 2. Llamamos a tu función de PDF (la que está dentro de PlanillaDetalleService)
	        if (!listaParaPdf.isEmpty()) {
	            planilladetalleService.generarPdfLlamadosElite(
	                new ArrayList<>(), // Lista de turnos vacía
	                listaParaPdf,      // Nuestra lista de seleccionados
	                listaParaPdf.get(0).getProfesional(), 
	                listaParaPdf.get(0).getEspecialidad(), 
	                "MANUAL", "MANUAL", 
	                "Sobreturnos"
	            );
	        }
	        return "OK";
	    } catch (Exception e) {
	        return "Error: " + e.getMessage();
	    }
	}
	
	*/
	
	// ELIMINAR MASIVAMENTE SELECCIONANDO VARIOS CHECKS
	@PostMapping("/cancelar-multiple")
	// // 1. ELIMINAMOS @ResponseBody para que Spring pueda redireccionar la página
	public String cancelarMultiple(@RequestParam("ids") List<Long> ids, RedirectAttributes flash) {
	    try {
	        // 1. DISPARAR BACKUP
	        backupService.ejecutarBackup("Sobreturnos");
	        
	        List<Sobreturno> listaParaPdf = new ArrayList<>();
	        
	        // Buscamos y cancelamos uno por uno los seleccionados
	        for (Long id : ids) {
	            Sobreturno s = sobreturnoService.findOne(id);
	            if (s != null) {
	                Estado cancelado = new Estado();
	                cancelado.setId(6L);
	                s.setEstado(cancelado);
	                sobreturnoService.save(s);
	                listaParaPdf.add(s);
	            }
	        }

	        // 2. Llamamos a tu función de PDF Elite
	        if (!listaParaPdf.isEmpty()) {
	            planilladetalleService.generarPdfLlamadosElite(
	                new ArrayList<>(), 
	                listaParaPdf,      
	                listaParaPdf.get(0).getProfesional(), 
	                listaParaPdf.get(0).getEspecialidad(), 
	                "MANUAL", "MANUAL", 
	                "Sobreturnos"
	            );
	        }
	        
	        flash.addFlashAttribute("success", "Se anularon " + ids.size() + " sobreturnos y se generó el backup/PDF.");

	    } catch (Exception e) {
	        flash.addFlashAttribute("danger", "Error: " + e.getMessage());
	    }

	    // // 2. CAMBIAMOS EL "OK" por la redirección a la Planilla Unificada
	    return "redirect:/recepcion";
	}



	// PARA cargar especialidades vinculadas por idProf en JS
	// (load-comboboc-vinculacion.html).
	@GetMapping(value = "/cargar-especialidades/{term}", produces = { "application/json" })
	public @ResponseBody List<Especialidad> findByIdProfInVinculacion(@PathVariable Long term) {
		return especialidadService.findByIdProfInVinculacion(term);
	}

	// Autocomplete Paciente por nombre
	@GetMapping(value = "/cargar-pacientes/{term}", produces = { "application/json" })
	public @ResponseBody List<Paciente> cargarPacientes(@PathVariable String term) {
		return pacienteService.findByNombre(term);
	}

	// Autocomplete Paciente por dni
	@GetMapping(value = "/cargar-pacientes-dni/{term}", produces = { "application/json" })
	public @ResponseBody List<Paciente> cargarPacientesDni(@PathVariable String term) {
		return pacienteService.findByDni(term);
	}

	// PARA verificar si existe planilla Activa. ????
	@GetMapping(value = "/existe-planilla/{profId}/{EspecId}", produces = { "application/json" })
	public @ResponseBody String[] buscarPlanillaActiva(@PathVariable long profId, @PathVariable long EspecId) {
		return sobreturnoService.buscarPlanillaActiva(profId, EspecId);
	}

	// OBTENIENDO ROLES DESDE CONTROLLADOR (EN UN FUTURO PASARLO AL CONTROLADOR
	// PRINCIPAL).
	private boolean hasRole(String role) {
		SecurityContext context = SecurityContextHolder.getContext(); // Estaticamente

		if (context == null) {
			return false;
		}

		Authentication auth = context.getAuthentication();

		if (auth == null) {
			return false;
		}
		Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
		// forma resumida que retorna true o false
		return authorities.contains(new SimpleGrantedAuthority(role));

		/*
		 * for(GrantedAuthority authority: authorities) {
		 * if(role.equals(authority.getAuthority())) {
		 * logger.info("Hola usuario ".concat(auth.getName()).concat(" tu role es: "
		 * .concat(authority.getAuthority()))); return true; } } return false;
		 */
	}
	
	// ALTA EXPRÉS PARTICULAR - PERFECCIONADO CON ATRIBUTOS EN TEXTO PLANO
    @PostMapping("/formPacienteExpress")
    @ResponseBody 
    public ResponseEntity<?> altaExpresParticular(
            @RequestParam("dni") String dni,
            @RequestParam("apellido") String apellido,
            @RequestParam("nombre") String nombre,
            @RequestParam("email") String email) {
        
        try {
            // 1. Verificamos duplicados con tu método del servicio de pacientes
            Paciente existeDni = pacienteService.findByPacienteDni(dni.trim()); 
            if (existeDni != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("{\"status\":\"error\", \"mensaje\":\"EXISTE_DNI\"}");
            }

            // 2. Instanciamos el objeto particular
            Paciente p = new Paciente();
            p.setDni(dni.trim());
            p.setApellido(apellido.toUpperCase().trim());
            p.setNombre(nombre.toUpperCase().trim());
            p.setEmail(email.toLowerCase().trim());
            p.setNumeroSocio("PARTICULAR"); // Tu VARCHAR(255)
            p.setFoto(""); 
            p.setCreateAt(new java.util.Date()); // Cubre la fecha obligatoria NOT NULL

            // --- RELLENO CLÍNICO HOMOLOGADO CON "PARTICULAR" ---
            p.setDomicilio("PARTICULAR");
            p.setTelefono("PARTICULAR");
            p.setEstado("PARTICULAR");
            p.setLocalidad("PARTICULAR");
            // ----------------------------------------------------

            // 3. Guardado en la base de datos usando tu método de confianza
            pacienteService.save(p); 

            // Buscamos el ID que generó automáticamente MySQL
            Paciente guardado = pacienteService.findByPacienteDni(p.getDni());
            Long idGenerado = (guardado != null) ? guardado.getId() : null;

            return ResponseEntity.ok().body("{\"status\":\"success\", \"id\":" + idGenerado + "}");
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"status\":\"error\", \"mensaje\":\"" + e.getMessage() + "\"}");
        }
    }
}

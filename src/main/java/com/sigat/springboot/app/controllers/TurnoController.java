package com.sigat.springboot.app.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.sigat.springboot.app.models.entity.Especialidad;
import com.sigat.springboot.app.models.entity.Paciente;
import com.sigat.springboot.app.models.entity.PlanillaCabecera;
import com.sigat.springboot.app.models.entity.PlanillaDetalle;
import com.sigat.springboot.app.models.entity.Profesional;
import com.sigat.springboot.app.models.entity.Turno;
import com.sigat.springboot.app.models.service.IEspecialidadService;
import com.sigat.springboot.app.models.service.IPacienteService;
import com.sigat.springboot.app.models.service.IPlanillaDetalleService;
import com.sigat.springboot.app.models.service.IProfesionalService;
import com.sigat.springboot.app.models.service.ITurnoService;

@Controller
@RequestMapping("/turnos")
public class TurnoController {

	protected final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private IProfesionalService profesionalService;
	@Autowired
	private IEspecialidadService especialidadService;
	@Autowired
	private IPacienteService pacienteService;
	@Autowired
	private ITurnoService turnoService;
	@Autowired
	private IPlanillaDetalleService planilladetalleService;
	@Autowired
	private MessageSource messageSource;
	
	
	// Metodo para abrir formulario modal Profesional y Especialidad
	@GetMapping("/formTurno")
	public String openModal(Turno turno, @RequestParam(name = "profId", required = false) Long profId,
			@RequestParam(name = "especId", required = false) Long especId, Model model) {

		model.addAttribute("titulo", "Generar Turno Nuevo..");
		model.addAttribute("titulobtnConfirmar", "Continuar alta de	Turno");
		model.addAttribute("profesionales", profesionalService.findAll());

		// MODIFICACIÓN: Pasamos los IDs para que el JS los use
		model.addAttribute("profIdPre", profId);
		model.addAttribute("especIdPre", especId);

		return "turnos/formTurno";
	}

	// Metodo listar sin paginacion
	@GetMapping("/listarTurno")
	public String listarTurno(Model model, Authentication authentication, Locale locale) {

		if (authentication != null) {
			logger.info("Hola usuario autenticado, tu username es: ".concat(authentication.getName()));
		}

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth != null) {
			logger.info("Utilizando forma estática SecurityContextHolder.getContext().getAuthentication(): "
					+ "Usuario autenticado: ".concat(auth.getName()));
		}

		if (hasRole("ROLE_ADMIN")) {
			logger.info("Hola ".concat(auth.getName()).concat(" tienes acceso!"));
		} else {
			logger.info("Hola ".concat(auth.getName()).concat(" NO tienes acceso!"));
		}

		List<Turno> turno = turnoService.findAll();
		model.addAttribute("titulo", messageSource.getMessage("text.turno.listar.titulo", null, locale));
		model.addAttribute("turnos", turno);
		return "turnos/listarTurno";
	}

	// Metodo guardar procesando el formulario
	@PostMapping("/formTurno")
	public String saveTurno(Turno turno, Model model, @ModelAttribute("profesional") Long profesionalId,
			@ModelAttribute("especialidad") Long especialidadId, @ModelAttribute("pacienteId") Long pacienteId,
			@ModelAttribute("planillaDetId") String planillaDetId,
			@ModelAttribute("planillaCabId") String planillaCabId,
			@ModelAttribute("rangoFechaHora") String rangoFechaHora, SessionStatus status, RedirectAttributes flash) {

		// Define un formateador para convertir la fecha a LocalDateTime.
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE dd/MM/yyyy HH:mm");
		String formattedDateTime = now.format(formatter);

		System.out.println("ProfId: " + profesionalId);
		System.out.println("EspecId: " + especialidadId);
		System.out.println("PacienteId: " + pacienteId);
		System.out.println("planillaDetId: " + planillaDetId);
		System.out.println("planillaCabId: " + planillaCabId);
		System.out.println("rangoFechaHora: " + rangoFechaHora);

		// Obtenemos los Objetos(datos) de acuerdo al ID para mostrarlos en la vista en caso de error
		Profesional profesional = profesionalService.findOne(profesionalId);
		Especialidad especialidad = especialidadService.findOne(especialidadId);
		Paciente paciente = pacienteService.findOne(pacienteId);

		model.addAttribute("fechaHoraActual", formattedDateTime.toUpperCase());
		model.addAttribute("titulo", "Resumen del turno.");
		model.addAttribute("profesional", profesional);
		model.addAttribute("especialidad", especialidad);
		model.addAttribute("paciente", paciente);
		model.addAttribute("rangoFechaHora", rangoFechaHora);

		// Seteo de IDs básicos (Tu lógica original)
		PlanillaDetalle id_planilladetalle = new PlanillaDetalle();
		id_planilladetalle.setId(Long.parseLong(planillaDetId));
		turno.setPlanilladetalle(id_planilladetalle);

		PlanillaCabecera id_planillacabecera = new PlanillaCabecera();
		id_planillacabecera.setId(Long.parseLong(planillaCabId));
		turno.setPlanillacabecera(id_planillacabecera);

		Paciente id_paciente = new Paciente();
		id_paciente.setId(pacienteId);
		turno.setPaciente(id_paciente);

		Profesional id_profesional = new Profesional();
		id_profesional.setId(profesionalId);
		turno.setProfesional(id_profesional);

		Especialidad id_especialidad = new Especialidad();
		id_especialidad.setId(especialidadId);
		turno.setEspecialidad(id_especialidad);

		try {
			String messageFlash = (turno.getId() != null) ? "Turno editado con exito."
					: "Turno guardado y mail de Confirmacion enviado.";

			// --- LA MEJORA DEFINITIVA ---
			// Llamamos al método orquestador. 
			// Este se encarga de: Save, Update Planilla y Enviar Mail (con datos curados)
			turnoService.registrarTurnoCompleto(turno, planillaDetId);

			// Cerramos la sesión del objeto
			status.setComplete();
			
			flash.addFlashAttribute("success", messageFlash);
			return "redirect:/turnos/listarTurno";

		} catch (Exception e) {
			// En caso de error, liberamos la planilla
			planilladetalleService.updatePlanillaDetalleTurnoLibre(planillaDetId);
			System.err.println("Error al procesar el turno: " + e.getMessage());
			flash.addFlashAttribute("error", "No se pudo procesar el turno.");
		}
		return "redirect:/turnos/listarTurno";
	}
	
	
	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash) {
	    if (id > 0) {
	        Turno turno = turnoService.findOne(id);
	        
	        if(turno != null){
	            // Esto ahora hará un "Update" a estado Cancelado y auditará
	            turnoService.delete(id); 
	            
	            flash.addFlashAttribute("success", "Turno cancelado y auditado con éxito.");
	            
	            // Liberamos el horario en la planilla detalle (Estado LIBRE - 3)
	            long plaDet_id = turno.getPlanilladetalle().getId();
	            planilladetalleService.updatePlanillaDetalleTurnoLibre(String.valueOf(plaDet_id));
	        } else {
	            flash.addFlashAttribute("danger", "No se encontró el turno para cancelar.");
	        }
	    }
	    return "redirect:/turnos/listarTurno";
	}

	// PARA cargar especialidades vinculadas por idProf en JS (load-comboboc-vinculacion.html).
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

	// PARA verificar si existe planilla Activa.
	@GetMapping(value = "/existe-planilla/{profId}/{EspecId}", produces = { "application/json" })
	public @ResponseBody String[] buscarPlanillaActiva(@PathVariable long profId, @PathVariable long EspecId) {
		return turnoService.buscarPlanillaActiva(profId, EspecId);
	}

	// PARA mostrar de la base de datos la PlanillaDetalle (turnos libres todos)
	/*
	 * @GetMapping(value = "/mostrar-turnosLibres-todos/{profId}/{EspecId}",
	 * produces = { "application/json" }) public @ResponseBody List<PlanillaDetalle>
	 * mostrarTurnosLibresTodos(@PathVariable Long profId,
	 * 
	 * @PathVariable Long EspecId) { return
	 * planillaDetalleService.mostrarTurnosLibresTodos(profId, EspecId); }
	 */
	
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
}
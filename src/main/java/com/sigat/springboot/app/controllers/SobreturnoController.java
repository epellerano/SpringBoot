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
import com.sigat.springboot.app.models.entity.Paciente;
import com.sigat.springboot.app.models.entity.PlanillaCabecera;
import com.sigat.springboot.app.models.entity.PlanillaDetalle;
import com.sigat.springboot.app.models.entity.Profesional;
import com.sigat.springboot.app.models.entity.Sobreturno;
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
	private MessageSource messageSource;

	// Metodo para abrir formulario (GET)
	@GetMapping("/formSobreturno")
	public String formSobreturno(Sobreturno sobreturno, Model model, 
			@RequestParam(name = "profId", required = false) Long profId,
			@RequestParam(name = "especId", required = false) Long especId) {

		model.addAttribute("titulo", "Generar Sobreturno Nuevo..");
		model.addAttribute("titulobtnConfirmar", "Continuar alta de Sobreturno");
		model.addAttribute("profesionales", profesionalService.findAll());
		
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

		List<Sobreturno> sobreturno = sobreturnoService.findAll();
		model.addAttribute("titulo", messageSource.getMessage("text.sobreturno.listar.titulo", null, locale));
		model.addAttribute("sobreturnos", sobreturno);
		return "sobreturnos/listarSobreturno";
	}

	// Metodo guardar procesando el formulario (POST)
	@PostMapping("/formSobreturno")
	@ResponseBody // Agrega esto para que Spring no busque una página HTML
	public ResponseEntity<?> saveSobreturno(Sobreturno sobreturno, Model model, 
	        @ModelAttribute("profesional") Long profesionalId,
	        @ModelAttribute("especialidad") Long especialidadId, 
	        @ModelAttribute("pacienteId") Long pacienteId,
	        @ModelAttribute("planillaCabId") String planillaCabId,
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
	        // En lugar de redirect, devolvemos un error 400 (Bad Request)
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
	    
	    try {
	        // EL GUARDADO REAL
	        sobreturnoService.registrarSobreturnoCompleto(sobreturno);
	        status.setComplete();

	        // Si todo sale bien, devolvemos un estado 200 (OK)
	        return ResponseEntity.ok().body("{\"status\":\"success\"}");

	    } catch (RuntimeException e) {
	        if ("EL_HORARIO_YA_ESTA_OCUPADO".equals(e.getMessage())) {
	            // Devolvemos un estado 409 (Conflicto) para que el AJAX detecte el error
	            return ResponseEntity.status(HttpStatus.CONFLICT).body("{\"status\":\"error\", \"mensaje\":\"OCUPADO\"}");
	        }
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"status\":\"error\"}");
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"status\":\"error\"}");
	    }

	}



	
	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash) {
		if (id > 0) {
			// Obtenemos el objeto sobreturno antes de eliminarlo
			Sobreturno sobreturno = sobreturnoService.findOne(id);
			if (sobreturno != null) {
				// Eliminamos el sobreturno x id.
				sobreturnoService.delete(id);
				flash.addFlashAttribute("success", "Sobreturno eliminado con exito.");
			} else {
				flash.addFlashAttribute("danger", "No se pudo eliminar el sobreturno.");
			}
		}
		return "redirect:/sobreturnos/listarSobreturno";
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
}

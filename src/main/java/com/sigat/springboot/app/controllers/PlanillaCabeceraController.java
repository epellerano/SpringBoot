package com.sigat.springboot.app.controllers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.sigat.springboot.app.models.entity.Especialidad;
import com.sigat.springboot.app.models.entity.Estado;
import com.sigat.springboot.app.models.entity.PlanillaCabecera;
import com.sigat.springboot.app.models.entity.PlanillaDetalle;
import com.sigat.springboot.app.models.entity.Profesional;
import com.sigat.springboot.app.models.entity.Turno;
import com.sigat.springboot.app.models.service.IDiaService;
import com.sigat.springboot.app.models.service.IEspecialidadService;
import com.sigat.springboot.app.models.service.IEstadoService;
import com.sigat.springboot.app.models.service.IPlanillaCabeceraService;
import com.sigat.springboot.app.models.service.IPlanillaDetalleService;
import com.sigat.springboot.app.models.service.IProfesionalService;
import com.sigat.springboot.app.models.service.ITurnoService;
import com.sigat.springboot.app.util.paginator.PageRender;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Controller
@RequestMapping("/planillacabecera")
@SessionAttributes("planillacabecera") // Para el id al Guardar o editar
public class PlanillaCabeceraController {

	public List<String> listaRango = new ArrayList<>(); // = new ArrayList<>(); // Declaración de la lista pública
	// public List<Object[]> listaRangoPersonalizado = new ArrayList<>();

	protected final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private ITurnoService turnoService;
	@Autowired
	private IPlanillaCabeceraService planillaCabeceraService;
	@Autowired
	private IPlanillaDetalleService planillaDetalleService;
	@Autowired
	private IProfesionalService profesionalService;
	@Autowired
	private IEspecialidadService especialidadService;
	@Autowired
	private IEstadoService estadoService;
	@Autowired
	private IDiaService diaService;
	@Autowired
	private MessageSource messageSource;

	public static Date convertStringToDate(String dateString) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = formatter.parse(dateString);
		} catch (ParseException e) {
			System.err.println("Error parsing date string: " + e.getMessage());
		}
		return date;
	}

	// Metodo listar Cancelacion Planilla
	@GetMapping("/listarPlanillasCancelacion")
	public String listarPlanillasCancelacion(Model model, Authentication authentication, Locale locale) {

		// Autenticacion de usuarios. PERO PASAR EN EL FUTURO A LA PAGINA
		// PRINCIPAL-------------------
		if (authentication != null) { // Opcion 1: por inyeccion de dependencia
			logger.info("Hola usuario autenticado, tu username es: ".concat(authentication.getName()));
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) { // Opcion 2: de manera estatica
			logger.info("Utilizando forma estática SecurityContextHolder.getContext().getAuthentication(): "
					+ "Usuario autenticado: ".concat(auth.getName()));
		}
		// utilizar funcion hasRole() para ver si tiene acceso al sistema.
		if (hasRole("ROLE_ADMIN")) {
			logger.info("Hola ".concat(auth.getName()).concat(" tienes acceso!"));
		} else {
			logger.info("Hola ".concat(auth.getName()).concat(" NO tienes acceso!"));
		}

		List<PlanillaCabecera> planillas = planillaCabeceraService.findAll();
		List<Profesional> profesionales = profesionalService.findAll();
		// model.addAttribute("titulo",
		// messageSource.getMessage("text.turno.listar.titulo", null, locale));
		model.addAttribute("titulo", "Cancelacion de Planillas Generadas");
		model.addAttribute("planillas", planillas);
		model.addAttribute("profesionales", profesionales);
		// model.addAttribute("page", pageRender);
		return "planillas/formCancelarPlanilla";
	}

	// Metodo listar con paginacion
	@GetMapping("/listarPlanillaCabecera")
	public String listarPlanillaCabecera(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {

		// planillacabecera = new PlanillaCabecera();
		// 5 items por pagina
		PageRequest pageRequest = PageRequest.of(page, 5);
		// Buscamos todas las vinculaciones y lo cargamos en un page
		Page<PlanillaCabecera> planillaCabecera = planillaCabeceraService.findAll(pageRequest);
		// invocamos a la funcion PageRender del package paginator y lo mostramos en la
		// vista.
		PageRender<PlanillaCabecera> pageRender = new PageRender<>("/planillas/listarPlanilla", planillaCabecera);

		model.addAttribute("titulo", "Listado de Planillas del Profesional");
		model.addAttribute("planillacabecera", planillaCabecera);
		model.addAttribute("page", pageRender);
		return "planillas/listarPlanilla";
	}

	// Metodo guardar primera fase (mostrar el formulario)
	@GetMapping("/formPlanillaCabecera")
	public String crearPlanillacabecera(PlanillaCabecera planillacabecera, Model model) throws ParseException {
		// planillacabecera = new PlanillaCabecera();
		model.addAttribute("planillacabecera", planillacabecera);
		model.addAttribute("titulo", "Crear Planilla Horaria del Profesional");
		model.addAttribute("profesionales", profesionalService.findAll());
		String activo = "ACTIVO";
		// model.addAttribute("especialidades", especialidadService.findAll());
		model.addAttribute("dias", diaService.findAll());
		model.addAttribute("estados", estadoService.findByEstadoActivo(activo));
		String fechaString = "25/05/2026";
		SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
		planillacabecera.setFechaInicio(formato.parse(fechaString));
		planillacabecera.setFechaFinal(formato.parse(fechaString));
		planillacabecera.setHoraInicialR1("09:00");
		planillacabecera.setHoraFinalR1("11:30");
		planillacabecera.setHoraInicialR2("00:00");
		planillacabecera.setHoraFinalR2("00:00");
		// logger.info("Datos planillaDetalle: " +
		// planillaDetalleService.ListarRangoPlanillaDet(1,1));
		return "planillas/formPlanilla";
	}

	// Metodo guardar segunda fase (post)
	@PostMapping("/formPlanillaCabecera")
	public String guardarPlanillacabecera(@Valid @ModelAttribute("planillacabecera") PlanillaCabecera planillacabecera,
	        BindingResult result, RedirectAttributes flash, SessionStatus status,
	        @RequestParam(value = "isCheckMañana", required = false) Boolean isCheckMañana,
	        @RequestParam(value = "isCheckTarde", required = false) Boolean isCheckTarde,
	        @RequestParam(value = "isCheckBoxes", required = false) Boolean isCheckBoxes,
	        @ModelAttribute("optionBox") String cantBoxes, Model model) throws ParseException {

	    if (result.hasErrors()) {
	        model.addAttribute("titulo", "Crear Planilla Horaria del Profesional");
	        model.addAttribute("profesionales", profesionalService.findAll());
	        model.addAttribute("especialidades", especialidadService.findByIdProfInVinculacion(planillacabecera.getProfesional().getId()));
	        model.addAttribute("dias", diaService.findAll());
	        String activo = "ACTIVO";
	        model.addAttribute("estados", estadoService.findByEstadoActivo(activo));
	        planillacabecera.setHoraInicialR1("00:00");
	        planillacabecera.setHoraFinalR1("00:00");
	        planillacabecera.setHoraInicialR2("00:00");
	        planillacabecera.setHoraFinalR2("00:00");
	        return "planillas/listarPlanilla";
	    }

	    // Validamos los Intervalos
	    if (planillacabecera.getIntervaloR1() == null) {
	        planillacabecera.setHoraInicialR1("--:--");
	        planillacabecera.setHoraFinalR1("--:--");
	        planillacabecera.setIntervaloR1(0);
	    }
	    if (planillacabecera.getIntervaloR2() == null) {
	        planillacabecera.setHoraInicialR2("--:--");
	        planillacabecera.setHoraFinalR2("--:--");
	        planillacabecera.setIntervaloR2(0);
	    }

	    // Formateador de la fecha
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	    String FechaInicio = formatter.format(planillacabecera.getFechaInicio());
	    String FechaFin = formatter.format(planillacabecera.getFechaFinal());

	    // Variables de rango
	    Long dia_id = planillacabecera.getDia().getId();
	    String FechaHoraInicio = FechaInicio + " " + planillacabecera.getHoraInicialR1() + ":00";
	    String FechaHoraFin = FechaFin + " " + planillacabecera.getHoraFinalR1() + ":00";
	    String FechaHoraInicioR2 = FechaInicio + " " + planillacabecera.getHoraInicialR2() + ":00";
	    String FechaHoraFinR2 = FechaFin + " " + planillacabecera.getHoraFinalR2() + ":00";

	    boolean checkValueMañana = (isCheckMañana != null) && isCheckMañana;
	    boolean checkValueTarde = (isCheckTarde != null) && isCheckTarde;
	    boolean checkValueBox = (isCheckBoxes != null) && isCheckBoxes;

	    // Generamos la lista de rangos
	    if (!checkValueBox) {
	        if (checkValueMañana && !checkValueTarde) {
	            listaRango = planillaDetalleService.generarRangoHorario(FechaHoraInicio, FechaHoraFin, planillacabecera.getIntervaloR1(), dia_id);
	        } else if (checkValueTarde && !checkValueMañana) {
	            listaRango = planillaDetalleService.generarRangoHorario(FechaHoraInicioR2, FechaHoraFinR2, planillacabecera.getIntervaloR2(), dia_id);
	        } else if (checkValueMañana && checkValueTarde) {
	            listaRango = planillaDetalleService.generarRangoHorarioCompleto(FechaHoraInicio, FechaHoraFin, planillacabecera.getIntervaloR1(), dia_id, FechaHoraInicioR2, FechaHoraFinR2, planillacabecera.getIntervaloR2());
	        }
	    }

	    // Validación de seguridad
	    if ((listaRango == null || listaRango.isEmpty()) && !checkValueBox) {
	        flash.addFlashAttribute("danger", "No se generaron rangos horarios. Revise los intervalos y las horas.");
	        return "redirect:/planillacabecera/formPlanillaCabecera";
	    }

	    // Seteamos el estado LIBRE (ID 3)
	    Estado estadoLibre = new Estado();
	    estadoLibre.setId(3L);

	    // PROCESAMIENTO DE DETALLES
	    if (cantBoxes.length() == 0) {
	        for (int i = 0; i < listaRango.size(); i++) {
	            PlanillaDetalle planilladetalle = new PlanillaDetalle(convertStringToDate(listaRango.get(i)), "PLANILLA GENERADA", "");
	            planilladetalle.setEstado(estadoLibre);
	            // VÍNCULO CRÍTICO: Indica a qué cabecera pertenece este detalle
	            planilladetalle.setPlanillacabecera(planillacabecera);
	            planillacabecera.addPlanillaDetalle(planilladetalle);
	        }
	    } else {
	        List<Object[]> listaRangoPersonalizado = planillaDetalleService.generarRangoHorarioPersonalizado(FechaHoraInicio, FechaHoraFin, dia_id, cantBoxes);
	        for (Object[] fila : listaRangoPersonalizado) {
	            String fecha = (String) fila[0];
	            String observ = (String) fila[1];
	            Integer box = Integer.valueOf(fila[2].toString());
	            PlanillaDetalle planilladetalle = new PlanillaDetalle(convertStringToDate(fecha), observ, String.valueOf(box));
	            planilladetalle.setEstado(estadoLibre);
	            // VÍNCULO CRÍTICO: Indica a qué cabecera pertenece este detalle
	            planilladetalle.setPlanillacabecera(planillacabecera);
	            planillacabecera.addPlanillaDetalle(planilladetalle);
	        }
	    }

	    // Configuración de la Cabecera
	    Estado estadoPlanillaCab = new Estado();
	    estadoPlanillaCab.setId(2L);
	    planillacabecera.setEstado(estadoPlanillaCab);
	    planillacabecera.setObservacion("PLANILLA ACTIVA");

	    // Lógica de expiración
	    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	    SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
	    String formattedFechaFinal = formatDate.format(planillacabecera.getFechaFinal());
	    String horaFinal = (planillacabecera.getHoraFinalR2() == null || planillacabecera.getHoraFinalR2().equals("00:00") || planillacabecera.getHoraFinalR2().equals("--:--")) 
	                       ? planillacabecera.getHoraFinalR1() : planillacabecera.getHoraFinalR2();

	    planillacabecera.setFechaExpira(LocalDateTime.parse(formattedFechaFinal + " " + horaFinal, dtf));

	    // GUARDADO FINAL EN CASCADA
	    String messageFlash = (planillacabecera.getId() != null) ? "Planilla editada con éxito." : "Planilla guardada con éxito.";
	    planillaCabeceraService.save(planillacabecera);
	    
	    status.setComplete();
	    flash.addFlashAttribute("success", messageFlash);
	    return "redirect:/planillacabecera/listarPlanillaCabecera";
	}

	@GetMapping("/eliminarPlanillaCabecera/{id}")
	public String eliminarPlanillaCabecera(@PathVariable(value = "id") Long id, RedirectAttributes flash) {
		// PlanillaCabecera planillacabecera = null;
		if (id > 0) {

			// creamos el Optional para almacenar la Planilla Cabecera.
			Optional<PlanillaCabecera> optionalplanillacabDb = planillaCabeceraService.findWithDetalle(id);
			optionalplanillacabDb.ifPresent(planillaCab -> {

				// Antes de eliminar Creamos un PDF con los turnos ocupados para reprogramar.

				// Eliminamos la planilla cabecera y sus detalles por la relacion en cascada.
				planillaCabeceraService.delete(id);
				flash.addFlashAttribute("success", "Planilla Cabecera y sus detalles eliminados con exito.");
			});
		}
		return "redirect:/planillacabecera/listarPlanillaCabecera";
	}

	// PARA cargar especialidades vinculadas por idProf en JS
	// (load-comboboc-vinculacion.html)
	@GetMapping(value = "/cargar-especialidades/{term}", produces = { "application/json" })
	public @ResponseBody List<Especialidad> findByIdProfInVinculacion(@PathVariable Long term) {
		return especialidadService.findByIdProfInVinculacion(term);
	}
	/*
	 * //PARA listar de la base de datos la PlanillaDetalle
	 * 
	 * @GetMapping(value = "/mostrar-planilla-detalle/{profId}/{EspecId}", produces
	 * = { "application/json" }) public @ResponseBody List<PlanillaDetalle>
	 * ListarRangoPlanillaDet(@PathVariable long profId, @PathVariable long
	 * EspecId){ return planillaDetalleService.ListarRangoPlanillaDet(profId,
	 * EspecId); }
	 */

	// PARA listar de la base de datos la PlanillaDetalle
	@GetMapping(value = "/mostrar-planilla-detalle/{profId}/{EspecId}", produces = { "application/json" })
	public @ResponseBody String[] ListarRangoPlanillaDet(@PathVariable long profId, @PathVariable long EspecId) {
		return planillaDetalleService.ListarRangoPlanillaDet(profId, EspecId);
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

package com.sigat.springboot.app.controllers;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.sigat.springboot.app.models.entity.Especialidad;
import com.sigat.springboot.app.models.entity.PlanillaCabecera;
import com.sigat.springboot.app.models.entity.PlanillaDetalle;
import com.sigat.springboot.app.models.service.IDiaService;
import com.sigat.springboot.app.models.service.IEspecialidadService;
import com.sigat.springboot.app.models.service.IEstadoService;
import com.sigat.springboot.app.models.service.IPlanillaCabeceraService;
import com.sigat.springboot.app.models.service.IPlanillaDetalleService;
import com.sigat.springboot.app.models.service.IProfesionalService;
import com.sigat.springboot.app.util.paginator.PageRender;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/planillacabecera")
@SessionAttributes("planillacabecera") // Para el id al Guardar o editar
public class PlanillaCabeceraController {
	
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
	
	// Metodo listar con paginacion
		@RequestMapping(value = "/listarPlanillaCabecera", method = RequestMethod.GET)
		public String listarPlanillaCabecera(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {
			
			//planillacabecera = new PlanillaCabecera();
			// 5 items por pagina
			PageRequest pageRequest = PageRequest.of(page, 5);
			// Buscamos todas las vinculaciones y lo cargamos en un page
			Page<PlanillaCabecera> planillaCabecera = planillaCabeceraService.findAll(pageRequest);
			// invocamos a la funcion PageRender del package paginator y lo mostramos en la vista.			
			PageRender<PlanillaCabecera> pageRender = new PageRender<>("/planillas/listarPlanilla", planillaCabecera);
			 
			model.addAttribute("titulo", "Listado de Planillas del Profesional");
			model.addAttribute("planillacabecera", planillaCabecera);
			model.addAttribute("page", pageRender);
			return "planillas/listarPlanilla";
		}
		
		// Metodo crear Rango Horario
		@RequestMapping(value = "/crearRangoTurnoMañana", method = RequestMethod.GET)
		public String crearRangoTurnoMañana(PlanillaDetalle planilladetalle ,Model model) throws ParseException{
			
			//Formateamos la Fecha Hora  
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
			  String fechaInicio = "2024-09-29 10:00:00"; 
			  String  fechaFin = "2024-10-29 12:00:00"; 
			  //Convertimos a Date
			  Date fechaHoraInicio =formatter.parse(fechaInicio); 
			  Date fechaHoraFin = formatter.parse(fechaFin);
			 			
			
			//fecha Hora Inicio
			//Date fechaHoraInicio= "2024-09-28 10:00:00";/*planillacabecera.getFechaInicio() + " " + planillacabecera.getHoraInicialR1();*/
			//Date fechaHoraFin="2024-10-28 12:00:00";/*planillacabecera.getFechaFinal() + " " + planillacabecera.getHoraFinalR1();*/
			Integer IntervaloR1=30;/*planillacabecera.getIntervaloR1().toString();*/
			Integer dia=2;/*planillacabecera.getDia().getId().toString();*/
			//List<PlanillaDetalle> crearRangoTurnoMañana = planillaDetalleService.crearRangoTurnoMañana(fechaHoraInicio, fechaHoraFin, IntervaloR1, dia);
			List<PlanillaCabecera> buscarPlanilla = planillaDetalleService.findplanillaCabecera((long) 1);
			//model.addAttribute("titulo", "Listado de Movimientos del Profesional-Especialidad");
			model.addAttribute("rangoFechasHoras",buscarPlanilla) ;
			return "planillas/formPlanilla";
		}
		
		// Metodo guardar primera fase (mostrar el formulario)
		@RequestMapping(value = "/formPlanillaCabecera", method = RequestMethod.GET)
		public String crearPlanillacabecera(PlanillaCabecera planillacabecera, Model model) {
			planillacabecera = new PlanillaCabecera();
			model.addAttribute("planillacabecera", planillacabecera);
			model.addAttribute("titulo", "Crear Planilla Horaria del Profesional");
			model.addAttribute("profesionales", profesionalService.findAll());		
			//model.addAttribute("especialidades", especialidadService.findAll());
			model.addAttribute("dias",diaService.findAll());
			model.addAttribute("estados",estadoService.findAll());
			return "planillas/formPlanilla";
		}
		
		// Metodo guardar segunda fase (post)
		@RequestMapping(value = "/formPlanillaCabecera", method = RequestMethod.POST)
		public String guardarMovimiento(@Valid @ModelAttribute("planillacabecera")PlanillaCabecera planillacabecera, BindingResult result, Model model,
				RedirectAttributes flash, SessionStatus status) {
			if (result.hasErrors()) {
				model.addAttribute("titulo", "Crear Planilla Horaria del Profesional");	
				 model.addAttribute("profesionales", profesionalService.findAll());
				 model.addAttribute("especialidades", especialidadService.findByIdProfInVinculacion(planillacabecera.getProfesional().getId()));
				 model.addAttribute("dias",diaService.findAll());
				 model.addAttribute("estados",estadoService.findAll());
				return "planillas/formPlanilla";
			}

			/*
			 * // Verificamos Duplicados y si existe. if
			 * (movimientoService.findByIdEspecialidadAndIdProfesional(movimiento.
			 * getEspecialidad().getId(), movimiento.getProfesional().getId()) != null) {
			 * flash.addFlashAttribute("error",
			 * "Movimiento duplicado en la Base de datos. Verifique nuevamente!"); return
			 * "redirect:/movimientos/listarMovimiento"; }
			 */
			// Si no esta continuamos
			String messageFlash = (planillacabecera.getId() != null) ? "Planilla Cabecera editado con exito."
					: "Planilla Cabecera guardada con exito.";
			planillaCabeceraService.save(planillacabecera);

			// Elimina el objeto PlanillaCabecera de la sesion con todos sus datos incluyendo el
			// id.
			status.setComplete();
			flash.addFlashAttribute("success", messageFlash);
			return "redirect:/planillacabecera/listarPlanillaCabecera";
		}
		
		// PARA cargar especialidades vinculadas por idProf en JS (load-comboboc-vinculacion.html)
		@GetMapping(value = "/cargar-especialidades/{term}", produces = { "application/json" })
		public @ResponseBody List<Especialidad> findByIdProfInVinculacion(@PathVariable Long term) {
			return especialidadService.findByIdProfInVinculacion(term);
		}

}

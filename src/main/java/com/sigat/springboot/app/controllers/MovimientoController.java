package com.sigat.springboot.app.controllers;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.sigat.springboot.app.models.entity.Movimiento;
import com.sigat.springboot.app.models.entity.Profesional;
import com.sigat.springboot.app.models.service.IEspecialidadService;
import com.sigat.springboot.app.models.service.IMovimientoService;
import com.sigat.springboot.app.models.service.IProfesionalService;
import com.sigat.springboot.app.util.paginator.PageRender;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/movimientos")
@SessionAttributes("movimiento") // Para el id al Guardar o editar
public class MovimientoController {
	
	// Get the SLF4J logger interface, default Logback, a SLF4J implementation
    private static final Logger logger = LoggerFactory.getLogger(MovimientoController.class);

	@Autowired
	private IMovimientoService movimientoService;

	@Autowired
	private IProfesionalService profesionalService;

	@Autowired
	private IEspecialidadService especialidadService;

	// Metodo handler para ver el detalle del movimiento a travez del id.
	@GetMapping(value = "/verMovimiento/{id}")
	public String verMovimiento(@PathVariable(value = "id") Long id, Map<String, Object> model,
			RedirectAttributes flash) {
		Movimiento movimiento = movimientoService.findOne(id);
		if (movimiento == null) {
			flash.addFlashAttribute("error", "El Movimiento no existe en la base de datos.");
			return "redirect:/movimientos/listarMovimiento";
		}
		model.put("movimiento", movimiento);
		model.put("titulo", "Detalle del movimiento"); // + movimiento.getApellido() + " " + movimiento.getNombre());
		return "movimientos/verMovimiento";
	}

	// Metodo listar con paginacion
	@RequestMapping(value = "/listarMovimiento", method = RequestMethod.GET)
	public String listarMovimiento(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {
		// 5 items por pagina
		PageRequest pageRequest = PageRequest.of(page, 5);
		// Buscamos todas las vinculaciones y lo cargamos en un page
		Page<Movimiento> movimiento = movimientoService.findAll(pageRequest);
		// invocamos a la funcion PageRender del package paginator y lo mostramos en la
		// vista.
		PageRender<Movimiento> pageRender = new PageRender<>("/movimientos/listarMovimiento", movimiento);
		model.addAttribute("titulo", "Listado de Movimientos del Profesional-Especialidad");
		model.addAttribute("movimientos", movimiento);
		model.addAttribute("page", pageRender);
		model.addAttribute("profesionales", profesionalService.findAll());
		model.addAttribute("especialidades", especialidadService.findAll());
		return "movimientos/listarMovimiento";
	}

	// Metodo guardar primera fase (mostrar el formulario)
	@RequestMapping(value = "/formMovimiento", method = RequestMethod.GET)
	public String crearMovimiento(Movimiento movimiento, Model model) {
		movimiento = new Movimiento();
		model.addAttribute("movimiento", movimiento);
		model.addAttribute("titulo", "Crear Movimiento Horario del Profesional");
		model.addAttribute("profesionales", profesionalService.findAll());		
		//model.addAttribute("especialidades", especialidadService.findAll());
		// model.addAttribute("especialidades",especialidadService.findByIdProfInVinculacion((long)
		// 1));
		return "movimientos/formMovimiento";
	}

	// Metodo editar movimiento
	@RequestMapping(value = "/editarMovimiento/{id}")
	public String editarMovimiento(@PathVariable(value = "id") Long id, Model model, RedirectAttributes flash) {
		Movimiento movimiento = null;
		if (id > 0) {
			movimiento = movimientoService.findOne(id);
			if (movimiento == null) {
				flash.addFlashAttribute("error", "El ID del movimiento no existe en la BBDD.");
				return "redirect:/movimientos/listarMovimiento";
			}
		} else {
			flash.addFlashAttribute("error", "El Id del movimiento no puede ser cero.");
			return "redirect:/movimientos/listarMovimiento";
		}
		model.addAttribute("movimiento", movimiento);
		model.addAttribute("titulo", "Editar Movimiento");
		model.addAttribute("profesionales", profesionalService.findOne(movimiento.getEspecialidad().getId()));
		model.addAttribute("especialidades", especialidadService.findOne(movimiento.getEspecialidad().getId()));
		//logger.info("espId: " + especialidadService.findOne(movimiento.getEspecialidad().getId()));
		return "movimientos/editarMovimiento";
	}

	@RequestMapping(value = "/eliminarMovimiento/{id}")
	public String eliminarMovimiento(@PathVariable(value = "id") Long id, RedirectAttributes flash) {
		if (id > 0) {
			// Obtenemos el objeto Movimiento antes de eliminarlo
			// Movimiento movimiento = movimientoService.findOne(id);
			// Eliminamos el movimiento x id.
			movimientoService.delete(id);
			flash.addFlashAttribute("success", "Movimiento eliminada con exito.");
		}
		return "redirect:/movimientos/listarMovimiento";
	}

	// Metodo guardar segunda fase (post)
	@RequestMapping(value = "/formMovimiento", method = RequestMethod.POST)
	public String guardarMovimiento(@Valid @ModelAttribute("movimiento") Movimiento movimiento, BindingResult result, Model model,
			RedirectAttributes flash, SessionStatus status) {
		if (result.hasErrors()) {
			model.addAttribute("titulo", "Formulario de Movimientos del Profesional");
			model.addAttribute("profesionales", profesionalService.findAll());
			model.addAttribute("especialidades", especialidadService.findAll());
			return "movimientos/formMovimiento";
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
		String messageFlash = (movimiento.getId() != null) ? "Movimiento editado con exito."
				: "Movimiento guardado con exito.";
		movimientoService.save(movimiento);

		// Elimina el objeto Vinculacion de la sesion con todos sus datos incluyendo el
		// id.
		status.setComplete();
		flash.addFlashAttribute("success", messageFlash);
		return "redirect:/movimientos/listarMovimiento";
	}
	
	// Metodo guardar editado fase (post)
		@RequestMapping(value = "/editarMovimiento", method = RequestMethod.POST)
		public String guardarMovimientoEditado(@Valid Movimiento movimiento, BindingResult result, Model model,
				RedirectAttributes flash, SessionStatus status) {
			if (result.hasErrors()) {
				model.addAttribute("titulo", "Formulario de Movimientos del Profesional");
				model.addAttribute("profesionales", profesionalService.findAll());
				model.addAttribute("especialidades", especialidadService.findAll());
				return "movimientos/formMovimiento";
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
			String messageFlash = (movimiento.getId() != null) ? "Movimiento editado con exito."
					: "Movimiento guardado con exito.";
			movimientoService.save(movimiento);

			// Elimina el objeto Vinculacion de la sesion con todos sus datos incluyendo el
			// id.
			status.setComplete();
			flash.addFlashAttribute("success", messageFlash);
			return "redirect:/movimientos/listarMovimiento";
		}
	
	// PARA cargar especialidades vinculadas por idProf
		@GetMapping(value = "/cargar-especialidades/{term}", produces = { "application/json" })
		public @ResponseBody List<Especialidad> findByIdProfInVinculacion(@PathVariable Long term) {
			return especialidadService.findByIdProfInVinculacion(term);
		}

}
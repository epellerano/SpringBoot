package com.sigat.springboot.app.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.sigat.springboot.app.models.entity.Profesional;
import com.sigat.springboot.app.models.entity.Vinculacion;
import com.sigat.springboot.app.models.service.IEspecialidadService;
import com.sigat.springboot.app.models.service.IMovimientoService;
import com.sigat.springboot.app.models.service.IPlanillaCabeceraService;
import com.sigat.springboot.app.models.service.IProfesionalService;
import com.sigat.springboot.app.models.service.IVinculacionService;
import com.sigat.springboot.app.util.paginator.PageRender;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/vinculaciones")
@SessionAttributes("vinculacion") // Para el id al Guardar o editar
public class VinculacionController {

	@Autowired
	private IVinculacionService vinculacionService;

	@Autowired
	private IProfesionalService profesionalService;

	@Autowired
	private IEspecialidadService especialidadService;

	@Autowired
	private IPlanillaCabeceraService planillaService;
	
	@Autowired
	private IMovimientoService movimientoService;

	// Metodo handler para ver el detalle de la vinculacion a travez del id.
	@GetMapping(value = "/verVinculacion/{id}")
	public String verVinculacion(@PathVariable(value = "id") Long id, Map<String, Object> model,
			RedirectAttributes flash) {
		Vinculacion vinculacion = vinculacionService.findOne(id);
		if (vinculacion == null) {
			flash.addFlashAttribute("error", "La Vinculacion no existe en la base de datos.");
			return "redirect:/vinculaciones/listarVinculacion";
		}
		model.put("vinculacion", vinculacion);
		model.put("titulo", "Detalle de la Vinculacion");
		return "vinculaciones/verVinculacion";
	}

	// MOSTRAR PDF x id EN UNA NUEVA PESTAÑA
	@GetMapping(value = "/reporteVinculacion/{id}")
	public String reporteVinculacion(@PathVariable(value = "id") Long id, Map<String, Object> model,
			RedirectAttributes flash) {
		// mostramos el .pdf mediante el button 'Imprimir en PDF' de la vista
		// verVinculacion.html
		// th:href="@{'/vinculaciones/reporteVinculacion/' +
		// ${vinculacion.id}(format=pdf)}"
		// hacemos referencia a:
		// (VinculacionPdfView.java) anotado con
		// @Component("vinculaciones/reporteVinculacion")
		return "vinculaciones/reporteVinculacion";
	}

	// Metodo listar con paginacion
	@GetMapping("/listarVinculacion")
	public String listarVinculacion(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {
		// 5 items por pagina
		PageRequest pageRequest = PageRequest.of(page, 5);
		// Buscamos todas las vinculaciones y lo cargamos en un page
		Page<Vinculacion> vinculacion = vinculacionService.findAll(pageRequest);
		// invocamos a la funcion PageRender del package paginator y lo mostramos en la
		// vista.
		PageRender<Vinculacion> pageRender = new PageRender<>("/vinculaciones/listarVinculacion", vinculacion);
		model.addAttribute("titulo", "Listado de Vinculaciones Profesionales por Especialidad");
		model.addAttribute("vinculaciones", vinculacion);
		model.addAttribute("page", pageRender);
		model.addAttribute("profesionales", profesionalService.findAll());
		model.addAttribute("especialidades", especialidadService.findAll());
		return "vinculaciones/listarVinculacion";
	}

	/// Metodo guardar primera fase (mostrar el formulario)
	@GetMapping("/formVinculacion")
	public String crearVinculacion(Vinculacion vinculacion, Model model) {
		vinculacion = new Vinculacion();
		model.addAttribute("vinculacion", vinculacion);
		model.addAttribute("titulo", "Formulario de Vinculacion Profesional-Especialidad");
		model.addAttribute("profesionales", profesionalService.findAll());
		model.addAttribute("especialidades", especialidadService.findAll());

		return "vinculaciones/formVinculacion";
	}

	// Metodo editar vinculacion pero detectar si tiene turnos, sobreturnos o
	// planillas creadas
	@GetMapping("/formVinculacion/{id}")
	public String editarVinculacion(@PathVariable(value = "id") Long id, Model model, RedirectAttributes flash) {
		Vinculacion vinculacion = null;
		if (id > 0) {
			vinculacion = vinculacionService.findOne(id);
			if (vinculacion == null) {
				flash.addFlashAttribute("error", "El ID de la vinculacion no existe en la BBDD.");
				return "redirect:/vinculaciones/listarVinculacion";
			}
		} else {
			flash.addFlashAttribute("error", "El Id de la vinculacion no puede ser cero.");
			return "redirect:/vinculaciones/listarVinculacion";
		}
		model.addAttribute("vinculacion", vinculacion);
		model.addAttribute("titulo", "Editar Vinculacion");
		// model.addAttribute("profesionales", profesionalService.findAll());
		// model.addAttribute("especialidades", especialidadService.findAll());
		model.addAttribute("profesionales", profesionalService.findOne(vinculacion.getProfesional().getId()));
		model.addAttribute("especialidades", especialidadService.findOne(vinculacion.getEspecialidad().getId()));
		return "vinculaciones/formVinculacion";
	}

	// Metodo guardar segunda fase (post)
	@PostMapping("/formVinculacion")
	public String guardarVinculacion(@Valid @ModelAttribute("vinculacion") Vinculacion vinculacion,
			BindingResult result, Model model, RedirectAttributes flash, SessionStatus status) {
		if (result.hasErrors()) {
			model.addAttribute("titulo", "Formulario de Vinculacion Profesional - Especialidad");
			model.addAttribute("profesionales", profesionalService.findAll());
			model.addAttribute("especialidades", especialidadService.findAll());
			return "vinculaciones/formVinculacion";
		}
		// Si el objeto vinculacion viene vacio: entonces verificamos duplicados en la
		// BD.
		if (vinculacion.getId() == null) {
			// Verificamos si la vinculacion esta Duplicada
			if (vinculacionService.findByIdEspecialidadAndIdProfesional(vinculacion.getEspecialidad().getId(),
					vinculacion.getProfesional().getId()) != null) {
				flash.addFlashAttribute("error", "Vinculacion duplicada en la Base de datos. Verifique nuevamente!");
				return "redirect:/vinculaciones/formVinculacion";
			}
		}
		// Guardamos
		String messageFlash = (vinculacion.getId() != null) ? "Vinculacion editada con exito."
				: "Vinculacion guardada con exito.";
		vinculacionService.save(vinculacion);

		// Elimina el objeto Vinculacion de la sesion con todos sus datos incluyendo el
		// id.
		status.setComplete();
		flash.addFlashAttribute("success", messageFlash);
		return "redirect:/vinculaciones/listarVinculacion";

	}

	@GetMapping("/eliminarVinculacion/{id}")
	public String eliminarVinculacion(@PathVariable(value = "id") Long id, RedirectAttributes flash) {
		if (id > 0) {
			// Obtenemos el objeto vinculacion antes de eliminarlo
			Vinculacion vinculacion = vinculacionService.findOne(id);

			// VERIFICAR SI TIENE PLANILLAS CREADAS

			if (planillaService.ExisteVinculacionEnPanillaCabecera(vinculacion.getProfesional().getId(),
					vinculacion.getEspecialidad().getId()) != null
					&& !planillaService.ExisteVinculacionEnPanillaCabecera(vinculacion.getProfesional().getId(),
							vinculacion.getEspecialidad().getId()).isEmpty()) {
				flash.addFlashAttribute("error",
						"ERROR - La Vinculacion posee Planillas Activas o Inactivas!. Verifique..");
				return "redirect:/vinculaciones/listarVinculacion";
			}

			// VERIFICAR SI TIENE MOVIMIENTOS CREADOS
			if (movimientoService.ExisteVinculacionEnMovimientos(vinculacion.getProfesional().getId(),
					vinculacion.getEspecialidad().getId()) != null
					&& !movimientoService.ExisteVinculacionEnMovimientos(vinculacion.getProfesional().getId(),
							vinculacion.getEspecialidad().getId()).isEmpty()) {
				flash.addFlashAttribute("error",
						"ERROR - La Vinculacion posee Movimientos creados!. Verifique..");
				return "redirect:/vinculaciones/listarVinculacion";
			}
			

			// Eliminamos el profesional x id.
			vinculacionService.delete(id);
			flash.addFlashAttribute("success", "Vinculacion eliminada con exito.");
		}
		return "redirect:/vinculaciones/listarVinculacion";
	}

	// PARA AUTOCOMPLETE
	@GetMapping(value = "/cargar-profesionales/{term}", produces = { "application/json" })
	public @ResponseBody List<Profesional> cargarProfesionales(@PathVariable String term) {
		return vinculacionService.findByNombre(term);
	}
}
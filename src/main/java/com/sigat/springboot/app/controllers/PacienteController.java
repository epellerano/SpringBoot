package com.sigat.springboot.app.controllers;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.sigat.springboot.app.models.entity.Paciente;
import com.sigat.springboot.app.models.service.IPacienteService;
import com.sigat.springboot.app.models.service.ISobreTurnoService;
import com.sigat.springboot.app.models.service.ITurnoService;
import com.sigat.springboot.app.models.service.IUploadFileService;
import com.sigat.springboot.app.util.paginator.PageRender;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/pacientes")
@SessionAttributes("paciente") // Para el id al Guardar o editar
public class PacienteController {

	@Autowired
	private IPacienteService pacienteService;

	@Autowired
	private IUploadFileService uploadFileService; // foto

	@Autowired
	private ITurnoService turnoService;
	
	@Autowired
	private ISobreTurnoService sobreturnoService;

	/*
	 * //Metodo listar sin paginacion
	 * 
	 * @RequestMapping(value="/listar", method=RequestMethod.GET) public String
	 * listar(Model model) { model.addAttribute("titulo","Listado de Pacientes.");
	 * model.addAttribute("pacientes",pacienteService.findAll()); return "listar"; }
	 */

	// Metodo handler para ver el detalle del paciente a travez del id.
	@GetMapping(value = "/verPaciente/{id}")
	public String verPaciente(@PathVariable(value = "id") Long id, Map<String, Object> model,
			RedirectAttributes flash) {
		Paciente paciente = pacienteService.findOne(id);
		if (paciente == null) {
			flash.addFlashAttribute("error", "El paciente no existe en la base de datos.");
			return "redirect:/pacientes/listarPaciente";
		}
		model.put("paciente", paciente);
		model.put("titulo", "Detalle del Paciente"); /* + paciente.getApellido() + " " + paciente.getNombre()); */
		return "pacientes/verPaciente";
	}

	// Metodo listar con paginacion
	@GetMapping("/listarPaciente")
	public String listarPaciente(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {

		// 5 items por pagina
		PageRequest pageRequest = PageRequest.of(page, 5);
		// Buscamos todos los pacientes y lo cargamos en un page
		Page<Paciente> paciente = pacienteService.findAll(pageRequest);
		// invocamos a la funcion PageRender del pacckage paginator
		PageRender<Paciente> pageRender = new PageRender<>("/pacientes/listarPaciente", paciente);
		model.addAttribute("titulo", "Listado de Pacientes");
		model.addAttribute("pacientes", paciente);
		model.addAttribute("page", pageRender);
		return "pacientes/listarPaciente";
	}

	// Metodo guardar primera fase (mostrar el formulario)
	@GetMapping("/formPaciente")
	public String crearPaciente(Map<String, Object> model) {
		Paciente paciente = new Paciente();
		model.put("paciente", paciente);
		model.put("titulo", "Formulario de Pacientes");
		return "pacientes/formPaciente";
	}

	// Metodo editar Paciente
	@GetMapping("/formPaciente/{id}")
	public String editarPaciente(@PathVariable(value = "id") Long id, Map<String, Object> model,
			RedirectAttributes flash) {
		Paciente paciente = null;
		if (id > 0) {
			paciente = pacienteService.findOne(id);
			if (paciente == null) {
				flash.addFlashAttribute("error", "El ID del Paciente no existe en la BBDD.");
				return "redirect:/pacientes/listarPaciente";
			}
		} else {
			flash.addFlashAttribute("error", "El Id del Profesional no puede ser cero.");
			return "redirect:/pacientes/listarPaciente";
		}
		model.put("paciente", paciente);
		model.put("titulo", "Editar Paciente");
		return "pacientes/formPaciente";
	}

	// Metodo guardar segunda fase (post)
	@PostMapping("/formPaciente")
	public String guardarPaciente(@Valid @ModelAttribute("paciente") Paciente paciente, BindingResult result,
			Model model, @RequestParam("file") MultipartFile foto, RedirectAttributes flash, SessionStatus status) {
		if (result.hasErrors()) {
			model.addAttribute("titulo", "Formulario de Pacientes");
			return "pacientes/formPaciente";
		}
		// Modulo para la foto
		// --------------------------------------------------------------------------------------
		if (!foto.isEmpty()) {
			if (paciente.getId() != null && paciente.getId() > 0 && paciente.getFoto() != null
					&& paciente.getFoto().length() > 0) {
				// Obtenemos la ruta de la imagen para eliminarla
				uploadFileService.delete(paciente.getFoto());
			}

			String uniqueFilename = null;
			try {
				// Movemos la imagen
				uniqueFilename = uploadFileService.copy(foto);
			} catch (IOException e) {
				e.printStackTrace();
			}
			flash.addFlashAttribute("info", "Has subido correctamente '" + uniqueFilename + "'");
			paciente.setFoto(uniqueFilename);
		} else {
			paciente.setFoto("");
		}
		// Fin modulo de foto
		// -------------------------------------------------------------------------------------------

		// Si el objeto paciente viene vacio, verificamos duplicados en la BD al
		// guardar.
		if (paciente.getId() == null) {
			// Verificamos si el dni del paciente esta Duplicado.
			if (pacienteService.findByPacienteDni(paciente.getDni()) != null) {
				flash.addFlashAttribute("error",
						"El Dni del paciente ya esta registrado en la Base de datos. Verifique..");
				return "redirect:/pacientes/listarPaciente";
			}

			// Verificamos si el nro. socio del paciente esta Duplicado.
			if (pacienteService.findByPacienteNsocio(paciente.getNumeroSocio()) != null) {
				flash.addFlashAttribute("error",
						"El Nº de Socio del paciente ya esta registrado en la Base de datos. Verifique..");
				return "redirect:/pacientes/listarPaciente";
			}
		}

		// Si el objeto paciente viene lleno, verificamos duplicados en la BD al
		// Actualizar.
		if (paciente.getId() != null) {
			// Verificamos si el dni del paciente esta Duplicado menos en este paciente_id.
			if (pacienteService.findByPacienteDniUpdate(paciente.getDni(), paciente.getId()) != null) {
				flash.addFlashAttribute("error", "ERROR - Existe un paciente con este numero de Dni!. Verifique..");
				return "redirect:/pacientes/listarPaciente";
			}

			// Verificamos si el nro. socio del paciente esta Duplicado menos en este
			// paciente_id.
			if (pacienteService.findByPacienteNsocioUpdate(paciente.getNumeroSocio(), paciente.getId()) != null) {
				flash.addFlashAttribute("error", "ERROR - Existe un paciente con este numero de Socio!. Verifique..");
				return "redirect:/pacientes/listarPaciente";
			}
		}

		String messageFlash = (paciente.getId() != null) ? "Paciente editado con exito." : "Paciente creado con exito.";
		pacienteService.save(paciente);
		// Elimina el objeto Profesional de la sesion con todos sus datos incluyendo el
		// id.
		status.setComplete();
		flash.addFlashAttribute("success", messageFlash);
		return "redirect:/pacientes/listarPaciente";
	}

	@GetMapping("/eliminarPaciente/{id}")
	public String eliminarPaciente(@PathVariable(value = "id") Long id, RedirectAttributes flash) {
		if (id > 0) {
			// Obtenemos el objeto profesional antes de eliminarlo
			Paciente paciente = pacienteService.findOne(id);

			// Verificamos si tiene turnos Activos.
			if (turnoService.pacientePoseeTurnosActivos(id) != null
					&& !turnoService.pacientePoseeTurnosActivos(id).isEmpty()) {
				flash.addFlashAttribute("error", "ERROR - El Paciente posee Turnos Activos!. Verifique..");
				return "redirect:/pacientes/listarPaciente";
			}

			// Verificamos si tiene turnos Activos.

			if (sobreturnoService.pacientePoseeSobreturnosActivos(id) != null
					&& !sobreturnoService.pacientePoseeSobreturnosActivos(id).isEmpty()) {
				flash.addFlashAttribute("error", "ERROR - El Paciente posee Sobreturnos Activos!. Verifique..");
				return "redirect:/pacientes/listarPaciente";
			}

			// Eliminamos el profesional x id.
			pacienteService.delete(id);
			flash.addFlashAttribute("success", "Paciente eliminado con exito.");
			if (uploadFileService.delete(paciente.getFoto())) {
				flash.addFlashAttribute("info", "Foto " + paciente.getFoto() + " eliminada con exito.");
			}
		}
		return "redirect:/pacientes/listarPaciente";
	}
}

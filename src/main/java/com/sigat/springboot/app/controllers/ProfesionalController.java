package com.sigat.springboot.app.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.Optional;
import java.util.Collection;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sigat.springboot.app.models.entity.PlanillaCabecera;
import com.sigat.springboot.app.models.entity.Profesional;
import com.sigat.springboot.app.models.service.IPlanillaCabeceraService;
import com.sigat.springboot.app.models.service.IProfesionalService;
import com.sigat.springboot.app.models.service.IUploadFileService;
import com.sigat.springboot.app.util.paginator.PageRender;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/")
@SessionAttributes("profesional") // Para el id al Guardar o editar
public class ProfesionalController {
	protected final Log logger = LogFactory.getLog(this.getClass());
	@Autowired
	private IProfesionalService profesionalService;

	@Autowired
	private IPlanillaCabeceraService planillaService;

	@Autowired
	private IUploadFileService uploadFileService;

	@Autowired
	private MessageSource messageSource;

	/*
	 * //Metodo listar sin paginacion
	 * 
	 * @RequestMapping(value="/listar", method=RequestMethod.GET) public String
	 * listar(Model model) {
	 * model.addAttribute("titulo","Listado de Profesionales");
	 * model.addAttribute("profesionales",profesionalService.findAll()); return
	 * "listar"; }
	 */

	@GetMapping(value = "/uploads/{filename:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String filename) {
		Resource recurso = null;
		try {
			recurso = uploadFileService.load(filename);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"")
				.body(recurso);
	}

	// Metodo handler para ver el detalle del profesional a travez del id.
	@GetMapping(value = "/ver/{id}")
	public String ver(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {
		Profesional profesional = profesionalService.findOne(id);
		if (profesional == null) {
			flash.addFlashAttribute("error", "El profesional no existe en la base de datos.");
			return "redirect:/listar";
		}
		model.put("profesional", profesional);
		model.put("titulo", "Detalle del Profesional");
		return "profesionales/ver";
	}

	// Metodo listar con paginacion y BUSCADOR GLOBAL
	@GetMapping({ "/listar"})
	public String listar(@RequestParam(name = "page", defaultValue = "0") int page, 
	                     @RequestParam(name = "term", required = false) String term, 
	                     Model model, Locale locale) {

	    Page<Profesional> profesional;

	    //para el search anulando el paginator
	    if (term != null && !term.isEmpty()) {
	        // .trim() limpia espacios accidentales adelante o atrás
	        String termBusqueda = term.trim(); 
	        PageRequest pageRequestBusqueda = PageRequest.of(0, 1000);
	        profesional = profesionalService.findByNombreOrApellidoOrCodigo(termBusqueda, pageRequestBusqueda);
	    } else {
	        // Listado normal paginado (de a 5 como tenías)
	        PageRequest pageRequestNormal = PageRequest.of(page, 5);
	        profesional = profesionalService.findAll(pageRequestNormal);
	    }

	    PageRender<Profesional> pageRender = new PageRender<>("/listar", profesional);
	    
	    model.addAttribute("titulo", messageSource.getMessage("text.profesional.listar.titulo", null, locale));
	    model.addAttribute("profesionales", profesional);
	    model.addAttribute("page", pageRender);
	    model.addAttribute("term", term); // Para que el input mantenga el texto escrito
	    
	    return "profesionales/listar";
	}

	// Metodo guardar primera fase (mostrar el formulario)
	@GetMapping("/form")
	public String crear(Map<String, Object> model) {
		Profesional profesional = new Profesional();
		model.put("profesional", profesional);
		model.put("titulo", "Formulario de Profesional");
		return "profesionales/form";
	}

	@GetMapping("/form/{id}")
	public String editar(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {
		Profesional profesional = null;
		if (id > 0) {
			profesional = profesionalService.findOne(id);
			if (profesional == null) {
				flash.addFlashAttribute("error", "El ID del Profesional no existe en la BBDD.");
				return "redirect:/listar";
			}
		} else {
			flash.addFlashAttribute("error", "El Id del Profesional no puede ser cero.");
			return "redirect:/listar";
		}
		model.put("profesional", profesional);
		model.put("titulo", "Editar Profesional");
		return "profesionales/form";
	}

	// Metodo guardar segunda fase (post)
	@PostMapping("/form")
	public String guardar(@Valid @ModelAttribute("profesional") Profesional profesional, BindingResult result,
			Model model, @RequestParam("file") MultipartFile foto, RedirectAttributes flash, SessionStatus status,
			@RequestParam("codigoProfRollback") String codigoProfRollback) {
		if (result.hasErrors()) {
			model.addAttribute("titulo", "Formulario de Profesional");
			return "profesionales/form";
		}
		// Modulo para la foto ----------------------------------------------------
		if (!foto.isEmpty()) {
			// Si el profesional ya tiene una foto, la borramos para reemplazarla
			if (profesional.getId() != null && profesional.getId() > 0 && profesional.getFoto() != null
					&& profesional.getFoto().length() > 0) {
				uploadFileService.delete(profesional.getFoto());
			}

			String uniqueFilename = null;
			try {
				uniqueFilename = uploadFileService.copy(foto);
			} catch (IOException e) {
				e.printStackTrace();
			}
			flash.addFlashAttribute("info", "Has subido correctamente '" + uniqueFilename + "'");
			profesional.setFoto(uniqueFilename);
		}
		// Fin modulo de foto-----------------------------------------------

		// Si el profesional_id viene vacio,verificamos duplicados en la BD al insertar.
		if (profesional.getId() == null) {
			// Verificamos si el dni del profesional esta Duplicado.
			if (profesionalService.findByProfesionalDni(profesional.getDni()) != null) {
				flash.addFlashAttribute("error",
						"El Dni del profesional ya esta registrado en la Base de datos. Verifique..");
				return "redirect:/listar";
			}

			// Verificamos si el codigo del profesional esta Duplicado.
			if (profesionalService.findByProfesionalCodigo(profesional.getCodigo()) != null) {
				flash.addFlashAttribute("error",
						"El Codigo del profesional ya esta registrado en la Base de datos. Verifique..");
				return "redirect:/listar";
			}

			// Verificamos si la matricula del profesional esta Duplicado.
			if (profesionalService.findByProfesionalMatricula(profesional.getMatricula()) != null) {
				flash.addFlashAttribute("error",
						"La Matricula del profesional ya esta registrado en la Base de datos. Verifique..");
				return "redirect:/listar";
			}
		}

		// Si el Profesional_id viene lleno, validamos los datos porque va a editar.
		if (profesional.getId() != null) {

			// Verificamos si el dni del profesional esta Duplicado menos este prof_id.
			if (profesionalService.findByProfesionalDniUpdate(profesional.getDni(), profesional.getId()) != null) {
				flash.addFlashAttribute("error", "ERROR - Existe un profesional con este numero de Dni!. Verifique..");
				return "redirect:/listar";
			}

			// Primero verificamos si tiene planillas creadas y luego el Codigo *****
			if (planillaService.ExisteProfesionalEnPanillaCabecera(profesional.getId()) == null
					|| planillaService.ExisteProfesionalEnPanillaCabecera(profesional.getId()).isEmpty()) {
				// Verificamos si el codigo del profesional esta Duplicado menos este prof_id.
				if (profesionalService.findByProfesionalCodigoUpdate(profesional.getCodigo(),
						profesional.getId()) != null) {
					flash.addFlashAttribute("error",
							"ERROR - Existe un profesional con este numero de Codigo!. Verifique..");
					return "redirect:/listar";
				}
			} else {
				flash.addFlashAttribute("warning",
						"El Profesional Tiene planillas creadas y su codigo no fue modificado!");
				// Rollback Backup CODIGO DEL PROEFESIONAL
				profesional.setCodigo(codigoProfRollback);
			}
			// **********************************************************************

			// Verificamos si matricula del profesional esta Duplicado menos este prof_id.
			if (profesionalService.findByProfesionalMatriculaUpdate(profesional.getMatricula(),
					profesional.getId()) != null) {
				flash.addFlashAttribute("error",
						"ERROR - Existe un profesional con este numero de Matricula!. Verifique..");
				return "redirect:/listar";
			}
		}

		String messageFlash = (profesional.getId() != null) ? "Profesional editado con exito"
				: "Profesional creado con exito";
		profesionalService.save(profesional);
		// Elimina el objeto Profesional de la sesion y sus datos incluyendo el id.
		status.setComplete();
		flash.addFlashAttribute("success", messageFlash);
		return "redirect:/listar";
	}

	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash) {
		if (id > 0) {
			// Obtenemos el objeto profesional antes de eliminarlo
			Profesional profesional = profesionalService.findOne(id);
			// verificamos si el profesional tiene planillas activas
			if (planillaService.ExisteProfesionalEnPanillaCabecera(id) != null
					&& !planillaService.ExisteProfesionalEnPanillaCabecera(id).isEmpty()) {
				flash.addFlashAttribute("error",
						"ERROR - El profesional posee Planillas Activas o Inactivas!. Verifique..");
				return "redirect:/listar";
			}

			/*
			 * Nota: al eliminar la/s planillas cabecera para poder eliminar el profesional
			 * los turnos y sobreturnos que tenga activos se generaran automaticamente en un
			 * archivo .pdf
			 */

			// Eliminamos el profesional x id.
			profesionalService.delete(id);
			flash.addFlashAttribute("success", "Profesional eliminado con exito.");
			if (uploadFileService.delete(profesional.getFoto())) {
				flash.addFlashAttribute("info", "Foto " + profesional.getFoto() + " eliminada con exito.");
			}
		}
		return "redirect:/listar";
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

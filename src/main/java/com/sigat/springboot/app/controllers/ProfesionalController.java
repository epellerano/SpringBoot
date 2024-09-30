package com.sigat.springboot.app.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.sigat.springboot.app.models.entity.Profesional;
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
		model.put("titulo",
				"Detalle del Profesional"); /* + profesional.getApellido() + " " + profesional.getNombre()); */
		return "profesionales/ver";
	}

	// Metodo listar con paginacion
	@RequestMapping(value = { "/listar", "/" }, method = RequestMethod.GET)
	public String listar(@RequestParam(name = "page", defaultValue = "0") int page, Model model,
			Authentication authentication, Locale locale) {

		// Autenticacion de usuarios. PERO PASAR EN EL FUTURO A LA PAGINA PRINCIPAL --------------------
		if (authentication != null) { //Opcion 1: por inyeccion de dependencia
			logger.info("Hola usuario autenticado, tu username es: ".concat(authentication.getName()));
		}

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth != null) { //Opcion 2: de manera estatica
			logger.info("Utilizando forma estática SecurityContextHolder.getContext().getAuthentication(): "
					+ "Usuario autenticado: ".concat(auth.getName()));
		}
		
		//utilizar funcion hasRole() para ver si tiene acceso al sistema.
		if(hasRole("ROLE_ADMIN")) {
			logger.info("Hola ".concat(auth.getName()).concat(" tienes acceso!"));
		} else {
			logger.info("Hola ".concat(auth.getName()).concat(" NO tienes acceso!"));
		}
		// fin Autenticacion --------------------------------------------------------------------------------------
		
		PageRequest pageRequest = PageRequest.of(page, 5);

		Page<Profesional> profesional = profesionalService.findAll(pageRequest);

		PageRender<Profesional> pageRender = new PageRender<>("/listar", profesional);
		model.addAttribute("titulo", messageSource.getMessage("text.profesional.listar.titulo", null, locale));
		model.addAttribute("profesionales", profesional);
		model.addAttribute("page", pageRender);
		return "profesionales/listar";
	}

	// Metodo guardar primera fase (mostrar el formulario)
	@RequestMapping(value = "/form")
	public String crear(Map<String, Object> model) {
		Profesional profesional = new Profesional();
		model.put("profesional", profesional);
		model.put("titulo", "Formulario de Profesional");
		return "profesionales/form";
	}

	@RequestMapping(value = "/form/{id}")
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
	@RequestMapping(value = "/form", method = RequestMethod.POST)
	public String guardar(@Valid @ModelAttribute("profesional") Profesional profesional, BindingResult result, Model model,
			@RequestParam("file") MultipartFile foto, RedirectAttributes flash, SessionStatus status) {
		if (result.hasErrors()) {
			model.addAttribute("titulo", "Formulario de Profesional");
			return "profesionales/form";
		}
		// Modulo para la foto
		// --------------------------------------------------------------------------------------
		if (!foto.isEmpty()) {
			if (profesional.getId() != null && profesional.getId() > 0 && profesional.getFoto() != null
					&& profesional.getFoto().length() > 0) {
				// Obtenemos la ruta de la imagen para eliminarla
				uploadFileService.delete(profesional.getFoto());
			}

			String uniqueFilename = null;
			try {
				// Movemos la imagen
				uniqueFilename = uploadFileService.copy(foto);
			} catch (IOException e) {
				e.printStackTrace();
			}
			flash.addFlashAttribute("info", "Has subido correctamente '" + uniqueFilename + "'");
			profesional.setFoto(uniqueFilename);
		}
		// Fin modulo de foto
		// -------------------------------------------------------------------------------------------

		String messageFlash = (profesional.getId() != null) ? "Profesional editado con exito"
				: "Profesional creado con exito";
		profesionalService.save(profesional);
		// Elimina el objeto Profesional de la sesion con todos sus datos incluyendo el
		// id.
		status.setComplete();
		flash.addFlashAttribute("success", messageFlash);
		return "redirect:/listar";
	}

	@RequestMapping(value = "/eliminar/{id}")
	public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash) {
		if (id > 0) {
			// Obtenemos el objeto profesional antes de eliminarlo
			Profesional profesional = profesionalService.findOne(id);
			// Eliminamos el profesional x id.
			profesionalService.delete(id);
			flash.addFlashAttribute("success", "Profesional eliminado con exito.");
			if (uploadFileService.delete(profesional.getFoto())) {
				flash.addFlashAttribute("info", "Foto " + profesional.getFoto() + " eliminada con exito.");
			}
		}
		return "redirect:/listar";
	}
	
	//OBTENIENDO ROLES DESDE CONTROLLADOR (EN UN FUTURO PASARLO AL CONTROLADOR PRINCIPAL).
private boolean hasRole(String role) {
		
		SecurityContext context = SecurityContextHolder.getContext(); // Estaticamente
		
		if(context == null) {
			return false;
		}
		
		Authentication auth = context.getAuthentication();
		
		if(auth == null) {
			return false;
		}
		
		Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
		
		//forma resumida que retorna true o false
		return authorities.contains(new SimpleGrantedAuthority(role));
		
		 /*for(GrantedAuthority authority: authorities) {
			if(role.equals(authority.getAuthority())) {
				logger.info("Hola usuario ".concat(auth.getName()).concat(" tu role es: ".concat(authority.getAuthority())));
				return true;
			}
		}
		return false;*/
	}

}

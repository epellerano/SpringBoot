package com.sigat.springboot.app.controllers;


import java.util.Map;
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
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.sigat.springboot.app.models.entity.Especialidad;
import com.sigat.springboot.app.models.service.IEspecialidadService;
import com.sigat.springboot.app.util.paginator.PageRender;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/especialidades")
@SessionAttributes("especialidad") //Para el id al Guardar o editar
public class EspecialidadController {
	
	@Autowired 
	private IEspecialidadService especialidadService;
	
	
	//Metodo handler para ver el detalle de la especialidad a travez del id.
		@GetMapping(value="/verEspecialidad/{id}")
		public String verEspecialidad(@PathVariable(value="id") Long id ,Map<String, Object> model, RedirectAttributes flash)
		{
			Especialidad especialidad = especialidadService.findOne(id);
			if(especialidad == null)
			{
				flash.addFlashAttribute("error", "La Especialidad no existe en la base de datos.");
				return "redirect:/especialidades/listarEspecialidad";
			}		
			model.put("especialidad", especialidad);
			model.put("titulo","Detalle de la Especialidad"); /* + especialidad.getApellido() + " " + especialidad.getNombre()); */
			return "especialidades/verEspecialidad";
		}
		
		//Metodo listar con paginacion
		@RequestMapping(value="/listarEspecialidad", method=RequestMethod.GET)
		public String listarEspecialidad(@RequestParam(name="page", defaultValue="0") int page, Model model) {	
			//5 items por pagina		
			PageRequest pageRequest = PageRequest.of(page,5);
			//Buscamos todos los pacientes y lo cargamos en un page		
			Page<Especialidad> especialidad = especialidadService.findAll(pageRequest);
			//invocamos a la funcion PageRender del package paginator y lo mostramos en la vista.	
			PageRender<Especialidad>pageRender=new PageRender<>("/especialidades/listarEspecialidad",especialidad);
			model.addAttribute("titulo","Listado de Especialidades");
			model.addAttribute("especialidades",especialidad);
			model.addAttribute("page",pageRender);
			return "especialidades/listarEspecialidad";
		}
		
		//Metodo guardar primera fase (mostrar el formulario)
			@RequestMapping(value="/formEspecialidad")
			public String crearEspecialidad(Map<String, Object> model)
			{
				Especialidad especialidad= new Especialidad();
				model.put("especialidad", especialidad);
				model.put("titulo", "Formulario de Especialidades");
				return"especialidades/formEspecialidad";
			}
			
		//Metodo editar Paciente		
			@RequestMapping(value="/formEspecialidad/{id}")
			public String editarEspecialidad(@PathVariable(value="id") Long id ,Map<String, Object> model, RedirectAttributes flash )
			{
				Especialidad especialidad=null;
				if(id > 0)
				{
					especialidad= especialidadService.findOne(id);	
					if(especialidad == null)
					{
						flash.addFlashAttribute("error","El ID de la especialidad no existe en la BBDD.");
						return "redirect:/especialidades/listarEspecialidad";
					}
				}
				else
				{
					flash.addFlashAttribute("error","El Id de la especialidad no puede ser cero.");
					return "redirect:/especialidades/listarEspecialidad";
				}
				model.put("especialidad", especialidad);
				model.put("titulo", "Editar Especialidad");
				return "especialidades/formEspecialidad";
			}
			
			
			//Metodo guardar segunda fase (post)
			@RequestMapping(value="/formEspecialidad", method=RequestMethod.POST)
			public String guardarEspecialidad(@Valid @ModelAttribute("especialidad") Especialidad especialidad, BindingResult result, Model model ,RedirectAttributes flash ,SessionStatus status)
			{
				if(result.hasErrors())
				{
					model.addAttribute("titulo", "Formulario de Especialidades");
					return"especialidades/formEspecialidad";
				}
				// Si el objeto especialidad viene vacio: entonces verificamos duplicados en la BD.
				if (especialidad.getId() == null) {
					// Verificamos si el codigo d ela especialidad esta Duplicada
					if (especialidadService.findByEspecialidadCodigo(especialidad.getEspecialidadCodigo()) != null) {
						flash.addFlashAttribute("error", "El codigo de la especialidad ya esta registrado en la Base de datos. Verifique..");
						return "redirect:/especialidades/listarEspecialidad";
					}
				}
				
				String messageFlash=(especialidad.getId()!= null)?"Especialidad editada con exito.":"Especialidad creada con exito.";
				especialidadService.save(especialidad);
				//Elimina el objeto Especialidad de la sesion con todos sus datos incluyendo el id.
				status.setComplete();
				flash.addFlashAttribute("success",messageFlash);
				return "redirect:/especialidades/listarEspecialidad";
			}

			@RequestMapping(value="/eliminarEspecialidad/{id}")
			public String eliminarEspecialidad(@PathVariable(value="id") Long id, RedirectAttributes flash )
			{
				if(id > 0)
				{
					//Obtenemos el objeto profesional antes de eliminarlo
					//Especialidad especialidad = especialidadService.findOne(id);
					// Eliminamos el profesional x id.
					especialidadService.delete(id);
					flash.addFlashAttribute("success","Especialidad eliminada con exito.");					
				}
				return "redirect:/especialidades/listarEspecialidad";	
			}
	
	

}

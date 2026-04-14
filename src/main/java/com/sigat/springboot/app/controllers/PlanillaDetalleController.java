package com.sigat.springboot.app.controllers;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sigat.springboot.app.models.entity.Especialidad;
import com.sigat.springboot.app.models.entity.PlanillaDetalle;
import com.sigat.springboot.app.models.entity.Profesional;
import com.sigat.springboot.app.models.service.IPlanillaDetalleService;


@Controller
@RequestMapping("/planilladetalle")
@SessionAttributes("planilladetalle") // Para el id al Guardar o editar
public class PlanillaDetalleController {

	@Autowired
	private IPlanillaDetalleService planillaDetalleService;
	
	// Metodo handler para ver el detalle de la planilla del profesional a travez
	// del id.
	@GetMapping(value = "/verPlanillaDetalle/{id}")
	public String verPlanillaDetalle(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {		
		
		List<PlanillaDetalle> planilladetalle = planillaDetalleService.verPlanillaDetalle(id);
		
		//List<String> profyespecialidad = planillaDetalleService.verPlanillaDetalleProfyEspecialidadDistinct(id);        
		List<PlanillaDetalle> planillaDetCompleta =planillaDetalleService.verPlanillaDetalleProfyEspecialidadDistinct(id);
		 
		  System.out.println("El valor en el índice 0 es: " + planillaDetCompleta);
		
		if (planilladetalle == null) {
			flash.addFlashAttribute("error", "No existen Planillas detalle para el profesional seleccionado!");
			return "redirect:planillas/listarPlanilla";
		}
		model.put("planilladetalle", planilladetalle);
		
		model.put("planillaDetCompleta", planillaDetCompleta);
		//model.put("profNombre", profyespecialidad.get(1).toString());
		//model.put("espNombre", profyespecialidad.get(1).toString());
		 
		
		model.put("titulo", "Detalle de planillas del Profesional"); // + profesional.getApellido() + " " +
																				// profesional.getNombre());
		return "planillas/verPlanillaDetalle";
	}
}

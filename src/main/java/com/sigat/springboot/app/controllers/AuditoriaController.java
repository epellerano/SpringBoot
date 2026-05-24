package com.sigat.springboot.app.controllers;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sigat.springboot.app.models.entity.PlanillaDetalle;
import com.sigat.springboot.app.models.entity.Sobreturno;
import com.sigat.springboot.app.models.entity.Turno;
import com.sigat.springboot.app.models.service.IAuditoriaService;
import com.sigat.springboot.app.models.service.IPlanillaDetalleService;
import com.sigat.springboot.app.models.service.IProfesionalService;
import com.sigat.springboot.app.models.service.ISobreTurnoService;
import com.sigat.springboot.app.models.service.ITurnoService;

@Controller
@RequestMapping("/auditoria")
public class AuditoriaController {

	@Autowired
	private ISobreTurnoService sobreturnoService;

	@Autowired
	private ITurnoService turnoService;

	@Autowired
	private IPlanillaDetalleService planilladetalleService;
	
	@Autowired
	private IProfesionalService profesionalService;
	
	@Autowired
	private IAuditoriaService auditoriaService;
	
	@GetMapping("/listarMonitor")
	public String mostrarMonitorAuditoria(Model model) {
	    
	    List<Turno> turnos = auditoriaService.listarMonitorTurnos();
	    List<Sobreturno> sobreturnos = auditoriaService.listarMonitorSobreturnos();
	    List<PlanillaDetalle> planillas = auditoriaService.listarMonitorPlanillas();

	    model.addAttribute("totalTurnos", turnos.size());
	    model.addAttribute("totalSobreturnos", sobreturnos.size());
	    model.addAttribute("totalPlanillas", planillas.size());

	    // Pasamos las listas para la tabla
	    model.addAttribute("turnos", turnos);
	    model.addAttribute("sobreturnos", sobreturnos);
	    model.addAttribute("planillas", planillas);

	    return "auditoria/monitor";
	}

	// Carga la página principal
	@GetMapping("/centro-recuperacion")
	public String centroRecuperacion(
	        @RequestParam(required = false) String tipo,
	        @RequestParam(required = false) Long profId,
	        @RequestParam(required = false) Long especId,
	        Model model) {
	    
	    // Pasamos los parámetros al modelo para que el JS los tome al cargar
	    model.addAttribute("tipoPreCargado", tipo);
	    model.addAttribute("profIdPreCargado", profId);
	    model.addAttribute("especIdPreCargado", especId);
	    
	    model.addAttribute("profesionales", profesionalService.findAll());
	    return "auditoria/centro-recuperacion";
	}

	// Procesa la búsqueda dinámica
	@GetMapping("/buscar")
	public String buscar(@RequestParam String tipo, 
	                     @RequestParam(required = false) Long profId,
	                     @RequestParam(required = false) Long especId, // Este ya lo tenés recibido
	                     @RequestParam(required = false) String desde,
	                     @RequestParam(required = false) String hasta,
	                     Model model) {
	    
	    if ("SOBRETURNOS".equals(tipo)) {
	        // Asegurate de pasar los 4 parámetros:
	        model.addAttribute("cancelados", sobreturnoService.findCancelados(profId, especId, desde, hasta));
	        return "auditoria/centro-recuperacion :: tablaSobreturnos";
	    } 
	    else if ("TURNOS".equals(tipo)) {
	        // CAMBIO: Antes tenías (profId, desde, hasta), ahora agregamos especId
	        model.addAttribute("cancelados", turnoService.findCancelados(profId, especId, desde, hasta));
	        return "auditoria/centro-recuperacion :: tablaTurnos";
	    }
	    else if ("PLANILLAS".equals(tipo)) {
	        model.addAttribute("cancelados", planilladetalleService.findPlanillasCanceladas(profId, especId, desde, hasta));
	        return "auditoria/centro-recuperacion :: tablaPlanillas";
	    }
	    
	    return "auditoria/centro-recuperacion :: tablaVacia";
	}

	// RESTAURAR TURNOS
	@PostMapping("/restaurar-turno")
	@ResponseBody
	public String restaurarTurno(@RequestParam Long id) {
	    // CAMBIO CLAVE: Cambiamos 'turnoService' por 'auditoriaService'
	    // Y usamos el método que tiene el "cerebro" nuevo
	    return auditoriaService.restaurarTurnoIndividual(id);
	}

	// RESTAURAR SOBRETURNOS
	@PostMapping("/restaurar-sobreturno")
	@ResponseBody
	public String restaurarSobreturno(@RequestParam Long id) {
		// Controller llama a Service
		return sobreturnoService.restaurarSobreturnoIndividual(id);
	}

	// RESTAURAR PLANILLAS
	@PostMapping("/restaurar-planilla")
	@ResponseBody
	public String restaurarPlanilla(@RequestParam Long profId, 
	                                @RequestParam Long especId, // Agregá este
	                                @RequestParam String desde, 
	                                @RequestParam String hasta) {
	    return planilladetalleService.restaurarPlanillaMasiva(profId, especId, desde, hasta);
	}
	
	//MOSTRAMOS EL DETALLE DE LO QUE SE VA A RESTAURAR (icono de ojo)
	@GetMapping("/detalle-recorte/{id}")
	@ResponseBody
	public List<String> obtenerDetalleRecorte(@PathVariable Long id) {
	    // Buscamos los detalles de esa planilla que están en Estado 6
	    List<PlanillaDetalle> detalles = planilladetalleService.findByPlanillaCabeceraIdAndEstadoId(id, 6L);
	    
	    return detalles.stream()
	        .map(d -> {
	            String dia = d.getPlanillacabecera().getDia().getNombre();
	            String fecha = new SimpleDateFormat("dd/MM/yyyy").format(d.getRangoFechaHora());
	            String hora = new SimpleDateFormat("HH:mm").format(d.getRangoFechaHora());
	            return "<li><b>" + dia + " " + fecha + "</b> - " + hora + " hs</li>";
	        })
	        .collect(Collectors.toList());
	}

}

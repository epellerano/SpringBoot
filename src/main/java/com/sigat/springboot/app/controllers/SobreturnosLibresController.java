package com.sigat.springboot.app.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sigat.springboot.app.models.entity.PlanillaCabecera;
import com.sigat.springboot.app.models.entity.PlanillaDetalle;
import com.sigat.springboot.app.models.entity.Sobreturno;
import com.sigat.springboot.app.models.entity.Turno;
import com.sigat.springboot.app.models.entity.Vinculacion;
import com.sigat.springboot.app.models.service.IPlanillaCabeceraService;
import com.sigat.springboot.app.models.service.IPlanillaDetalleService;
import com.sigat.springboot.app.models.service.IProfesionalService;
import com.sigat.springboot.app.models.service.ISobreTurnoService;
import com.sigat.springboot.app.models.service.ITurnoService;
import com.sigat.springboot.app.models.service.IVinculacionService;

@RestController
@RequestMapping("api/sobreturnosLibresController")
public class SobreturnosLibresController {
	
	@Autowired
	private IPlanillaDetalleService planillaDetalleService;
	@Autowired
	private IPlanillaCabeceraService planillacabeceraService;
	@Autowired
	private IVinculacionService vinculacionService;
	@Autowired
	private IProfesionalService profesionalService;
	@Autowired
	private ISobreTurnoService sobreturnoService;

	@GetMapping("/between/{profId}/{EspecId}")
	public List<PlanillaDetalle> obtenerTurnosLibresTodos(@PathVariable Long profId, @PathVariable Long EspecId) {
		return planillaDetalleService.mostrarTurnosLibresTodos(profId, EspecId);
	}
	
	/*
	 * @GetMapping("/between-sobreturnos/{profId}/{EspecId}") public
	 * List<PlanillaDetalle> mostrarHorariosParaSobreturno(@PathVariable Long
	 * profId, @PathVariable Long EspecId) { return
	 * planillaDetalleService.mostrarHorariosParaSobreturno(profId, EspecId); }
	 */
	
	@GetMapping("/between-dia-id/{profId}/{EspecId}/{diaId}")
	public List<PlanillaDetalle> mostrarTurnosLibresTodosByDiaId(@PathVariable Long profId, @PathVariable Long EspecId, @PathVariable Long diaId) {
		return planillaDetalleService.mostrarTurnosLibresTodosByDiaId(profId, EspecId, diaId);
	}

	@GetMapping("/horarios/{profId}/{EspecId}")
	public List<PlanillaCabecera> obtenerHorariosAtencion(@PathVariable Long profId, @PathVariable Long EspecId) {
		return planillacabeceraService.findHorariosByProfIdyEspId(profId, EspecId);
	}
	
	@GetMapping("/horarios-dia-id/{profId}/{EspecId}/{diaId}")
	public List<PlanillaCabecera> obtenerHorariosAtencionByDiaId(@PathVariable Long profId, @PathVariable Long EspecId, @PathVariable Long diaId) {
		return planillacabeceraService.findHorariosByProfIdyEspIdAndDiaId(profId, EspecId, diaId);
	}

	@GetMapping("/obtener-observaciones/{profId}/{EspecId}")
	public List<Vinculacion> obtenerObservaciones(@PathVariable Long profId, @PathVariable Long EspecId) {
		return vinculacionService.findObservacionByProfIdyEspId(profId, EspecId);

	}

	@GetMapping("/sobreturnos-ocupados/{profId}/{EspecId}")
	public List<Sobreturno> obtenerSobreTurnosOcupados(@PathVariable Long profId, @PathVariable Long EspecId) {
		return sobreturnoService.findSobreTurnosOcupadosByProfIdyEspId(profId, EspecId);
	}
	
	//Muestra los SobreTurnos Ocupados por el Profesional, Especialidad y paciente por sobreturno_id.
	//alhacer click en la lupa.
	@GetMapping("/obtener-sobreturno-id/{sobreturnoId}") 
	public List<Sobreturno>	findSobreTurnoClickById(@PathVariable Long sobreturnoId) {
		return sobreturnoService.findSobreTurnoClickById(sobreturnoId); 
	}
	 

	// Obtenemos la imagen del profesional para mostrarrla al seleccionar
	// profesional en Turnos Alta.
	@GetMapping("/obtener-imagen/{profId}")
	public String obtenerImagen(@PathVariable Long profId) {
		return profesionalService.obtenerNombreArchivo(profId);
	}

	@GetMapping("/actualizar-planilladetalle-expirado/{estadoId}")
	public void actualizarPlanillaDetalleExpirado(@PathVariable String estadoId) {
		planillaDetalleService.updatePlanillaDetalleTurnoExpirado(estadoId);
	}

	@GetMapping("/actualizar-planillacabecera-inactivo/{estadoId}")
	public void actualizarPlanillaCabeceraInactivo(@PathVariable String estadoId) {
		planillaDetalleService.updatePlanillaCabeceraInactivo(estadoId);
	}	
	
	//ELIMINAR SOBRETURNO MEDIANTE AJAX y JAVASCRIPT.
	@GetMapping(value = "/eliminar-sobreturno/{id}")
    public void deleteSobreturno(@PathVariable Long id) {
		// Obtenemos el objeto sobreturno antes de eliminarlo.
		Sobreturno sobreturno = sobreturnoService.findOne(id);
		System.out.println("objeto sobreturno: "+sobreturno);
		if (sobreturno != null) {		
			// Eliminamos el sobreturno x id.
			sobreturnoService.delete(id);
		}
    }
	
	//VERIFICAR SI EXISTE FECHA_HORA DEL SOBRETURNO EN PLANILLA DETALLE.
	@GetMapping("/existe-sobreturno-pladet/{profId}/{EspecId}/{fechaHora}")
	public List<String> existeSobreturnoEnPlanillaDetalle(@PathVariable Long profId, @PathVariable Long EspecId, @PathVariable String fechaHora) {
		return planillaDetalleService.existeSobreturnoEnPlanillaDetalle(profId, EspecId, fechaHora);
	}
}

package com.sigat.springboot.app.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sigat.springboot.app.models.entity.Movimiento;
import com.sigat.springboot.app.models.entity.PlanillaCabecera;
import com.sigat.springboot.app.models.entity.PlanillaDetalle;
import com.sigat.springboot.app.models.entity.Profesional;
import com.sigat.springboot.app.models.entity.Sobreturno;
import com.sigat.springboot.app.models.entity.Turno;
import com.sigat.springboot.app.models.entity.Vinculacion;
import com.sigat.springboot.app.models.service.IMovimientoService;
import com.sigat.springboot.app.models.service.IPlanillaCabeceraService;
import com.sigat.springboot.app.models.service.IPlanillaDetalleService;
import com.sigat.springboot.app.models.service.IProfesionalService;
import com.sigat.springboot.app.models.service.ITurnoService;
import com.sigat.springboot.app.models.service.IVinculacionService;

@RestController
@RequestMapping("api/reportesRestController")
public class ReportesRestController {
	protected final Log logger = LogFactory.getLog(this.getClass());
	
	@Autowired
	private ITurnoService turnoService;

	/*@Autowired
	private IPlanillaDetalleService planillaDetalleService;*/
	@Autowired
	private IPlanillaCabeceraService planillacabeceraService;
	
	@Autowired
	private IMovimientoService movimientoService;
	
	@Autowired
	private IVinculacionService vinculacionService;
	
	/*
	@GetMapping("/between/{profId}/{EspecId}")
	public List<PlanillaDetalle> obtenerTurnosLibresTodos(@PathVariable Long profId, @PathVariable Long EspecId) {
		return planillaDetalleService.mostrarTurnosLibresTodos(profId, EspecId);
	}
	
	@GetMapping("/between-dia-id/{profId}/{EspecId}/{diaId}")
	public List<PlanillaDetalle> mostrarTurnosLibresTodosByDiaId(@PathVariable Long profId, @PathVariable Long EspecId, @PathVariable Long diaId) {
		return planillaDetalleService.mostrarTurnosLibresTodosByDiaId(profId, EspecId, diaId);
	}
	
	@GetMapping("/horarios-dia-id/{profId}/{EspecId}/{diaId}")
	public List<PlanillaCabecera> obtenerHorariosAtencionByDiaId(@PathVariable Long profId, @PathVariable Long EspecId, @PathVariable Long diaId) {
		return planillacabeceraService.findHorariosByProfIdyEspIdAndDiaId(profId, EspecId, diaId);
	}
	*/

	
	// reporte Turnos Ocupados por idProfesional e id_Especialidad y fechas.
	@GetMapping("/reporte-turnos-ocupados-fechas/{profId}/{EspecId}/{fechaIni}/{fechaFin}")
	public List<Turno> reporteTurnosOcupadosFechas(@PathVariable Long profId, @PathVariable Long EspecId, @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaIni, @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaFin) {		
		return turnoService.reporteTurnosOcupadosFechas(profId, EspecId, fechaIni, fechaFin);	
	}
	
	// reporte Sobreturnos Ocupados por idProfesional e id_Especialidad y fechas.
	@GetMapping("/reporte-sobreturnos-ocupados-fechas/{profId}/{EspecId}/{fechaIni}/{fechaFin}")
	public List<Sobreturno> reporteSobreTurnosOcupadosFechas(@PathVariable Long profId, @PathVariable Long EspecId, @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaIni, @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaFin) {		
		return turnoService.reporteSobreTurnosOcupadosFechas(profId, EspecId, fechaIni, fechaFin);	
	}
	
	// reporte Planillas Activas por idProfesional e id_Especialidad y fechas.
	@GetMapping("/reporte-planillas-activas-fechas/{profId}/{EspecId}/{fechaIni}/{fechaFin}")
	public List<PlanillaCabecera> reportePlanillasActivasFechas(@PathVariable Long profId, @PathVariable Long EspecId, @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaIni, @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaFin) {		
		return planillacabeceraService.reportePlanillasActivasFechas(profId, EspecId, fechaIni, fechaFin);	
	}
	
	// reporte Movimientos por idProfesional e id_Especialidad y fechas.
	@GetMapping("/reporte-movimientos-fechas/{profId}/{EspecId}/{fechaIni}/{fechaFin}")
	public List<Movimiento> reporteMovimientosFechas(@PathVariable Long profId, @PathVariable Long EspecId, @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaIni, @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaFin) {		
		return movimientoService.reporteMovimientosFechas(profId, EspecId, fechaIni, fechaFin);	
	}
	
	// reporte Vinculacion por idProfesional.
	@GetMapping("/reporte-vinculacion/{profId}")
	public List<Vinculacion> reporteVinculacion(@PathVariable Long profId) {		
		return vinculacionService.reporteVinculacion(profId);	
	}
	
	
	
}

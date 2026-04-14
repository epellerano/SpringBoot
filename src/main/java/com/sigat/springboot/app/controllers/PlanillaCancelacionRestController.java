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
import com.sigat.springboot.app.models.service.ISobreTurnoService;
import com.sigat.springboot.app.models.service.ITurnoService;
import com.sigat.springboot.app.models.service.IVinculacionService;

@RestController
@RequestMapping("api/planillaCancelacionRestController")
public class PlanillaCancelacionRestController {
	protected final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private ITurnoService turnoService;
	@Autowired
	private IPlanillaDetalleService planillaDetalleService;
	@Autowired
	private ISobreTurnoService sobreturnoService;

	// Buscar Turnos Libres por idProfesional e id_Especialidad y fechas.
	// (cancelacion Planilla)
	@GetMapping("/buscar-turnos-libres-fechas/{profId}/{EspecId}/{fechaIni}/{fechaFin}")
	public List<PlanillaDetalle> buscarTurnosLibresFechas(@PathVariable Long profId, @PathVariable Long EspecId,
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaIni,
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaFin) {
		return planillaDetalleService.buscarTurnosLibresFechas(profId, EspecId, fechaIni, fechaFin);
	}

	// Buscar Turnos Ocupados por idProfesional e id_Especialidad y fechas.
	// (cancelacion Planilla)
	@GetMapping("/buscar-turnos-ocupados-fechas/{profId}/{EspecId}/{fechaIni}/{fechaFin}")
	public List<Turno> buscarTurnosOcupadosFechas(@PathVariable Long profId, @PathVariable Long EspecId,
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaIni,
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaFin) {
		return turnoService.buscarTurnosOcupadosFechas(profId, EspecId, fechaIni, fechaFin);
	}

	// Buscar Turnos Ocupados por idProfesional e id_Especialidad y fechas.
	// (cancelacion Planilla)
	@GetMapping("/buscar-sobreturnos-ocupados-fechas/{profId}/{EspecId}/{fechaIni}/{fechaFin}")
	public List<Sobreturno> buscarSobreturnosOcupadosFechas(@PathVariable Long profId, @PathVariable Long EspecId,
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaIni,
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaFin) {
		return sobreturnoService.buscarSobreturnosOcupadosFechas(profId, EspecId, fechaIni, fechaFin);
	}

	// Buscar Turnos Libres por idProfesional e id_Especialidad y fechas Horas.
	// (cancelacion Planilla)
	@GetMapping("/buscar-turnos-libres-fechashoras/{profId}/{EspecId}/{fechaHoraIni}/{fechaHoraFin}")
	public List<PlanillaDetalle> buscarTurnosLibresFechasHoras(@PathVariable Long profId, @PathVariable Long EspecId,
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date fechaHoraIni,
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date fechaHoraFin) {
		return planillaDetalleService.buscarTurnosLibresFechasHoras(profId, EspecId, fechaHoraIni, fechaHoraFin);
	}

	// Buscar Turnos Ocupados por idProfesional e id_Especialidad y fechas horas.
	// (cancelacion Planilla)
	@GetMapping("/buscar-turnos-ocupados-fechashoras/{profId}/{EspecId}/{fechaHoraIni}/{fechaHoraFin}")
	public List<Turno> buscarTurnosOcupadosFechasHoras(@PathVariable Long profId, @PathVariable Long EspecId,
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date fechaHoraIni,
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date fechaHoraFin) {
		return turnoService.buscarTurnosOcupadosFechasHoras(profId, EspecId, fechaHoraIni, fechaHoraFin);
	}

	// Buscar Turnos Ocupados por idProfesional e id_Especialidad y fechas horas.
	// (cancelacion Planilla)
	@GetMapping("/buscar-sobreturnos-ocupados-fechashoras/{profId}/{EspecId}/{fechaHoraIni}/{fechaHoraFin}")
	public List<Sobreturno> buscarSobreturnosOcupadosFechasHoras(@PathVariable Long profId, @PathVariable Long EspecId,
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date fechaHoraIni,
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date fechaHoraFin) {
		return sobreturnoService.buscarSobreturnosOcupadosFechasHoras(profId, EspecId, fechaHoraIni, fechaHoraFin);
	}
	
	@GetMapping("/actualizar-planilladetalle-cancelado-fechas-tl/{profId}/{EspecId}/{fechaIni}/{fechaFin}/{estadoId}/{motivo}")
	public void actualizarPlanillaDetalleCanceladoFechasTL(@PathVariable Long profId, @PathVariable Long EspecId,
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaIni,
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaFin , @PathVariable String estadoId, @PathVariable String motivo) {
		planillaDetalleService.actualizarPlanillaDetalleCanceladoFechasTL(profId, EspecId, fechaIni, fechaFin ,estadoId, motivo);
	}
	
	@GetMapping("/actualizar-planilladetalle-cancelado-fechas-horas-tl/{profId}/{EspecId}/{fechaHoraIni}/{fechaHoraFin}/{estadoId}/{motivo}")
	public void actualizarPlanillaDetalleCanceladoFechasHorasTL(@PathVariable Long profId, @PathVariable Long EspecId,
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date fechaHoraIni,
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date fechaHoraFin , @PathVariable String estadoId, @PathVariable String motivo) {
		planillaDetalleService.actualizarPlanillaDetalleCanceladoFechasHorasTL(profId, EspecId, fechaHoraIni, fechaHoraFin ,estadoId, motivo);
	}
	
	@GetMapping("/actualizar-planilladetalle-cancelado-fechas-to/{profId}/{EspecId}/{fechaIni}/{fechaFin}/{estadoId}/{motivo}")
	public void actualizarPlanillaDetalleCanceladoFechasTO(@PathVariable Long profId, @PathVariable Long EspecId,
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaIni,
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaFin , @PathVariable String estadoId, @PathVariable String motivo) {
		planillaDetalleService.actualizarPlanillaDetalleCanceladoFechasTO(profId, EspecId, fechaIni, fechaFin ,estadoId, motivo);
	}
	
	@GetMapping("/actualizar-planilladetalle-cancelado-fechas-horas-to/{profId}/{EspecId}/{fechaHoraIni}/{fechaHoraFin}/{estadoId}/{motivo}")
	public void actualizarPlanillaDetalleCanceladoFechasHorasTO(@PathVariable Long profId, @PathVariable Long EspecId,
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date fechaHoraIni,
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date fechaHoraFin , @PathVariable String estadoId, @PathVariable String motivo) {
		planillaDetalleService.actualizarPlanillaDetalleCanceladoFechasHorasTO(profId, EspecId, fechaHoraIni, fechaHoraFin ,estadoId, motivo);
	}
	
	@GetMapping("/desactivar-safe-updates")
	public void desactivarSafeUpdates() {
		turnoService.desactivarSafeUpdates();
	}
	
	@GetMapping("/eliminar-turnos-ocupados-fechas/{profId}/{EspecId}/{fechaIni}/{fechaFin}")
	public void eliminarTurnosOcupadosFechas(@PathVariable Long profId, @PathVariable Long EspecId,
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaIni,
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaFin) {
		turnoService.eliminarTurnosOcupadosFechas(profId, EspecId, fechaIni, fechaFin);
	}
	
	@GetMapping("/eliminar-turnos-ocupados-fechas-horas/{profId}/{EspecId}/{fechaHoraIni}/{fechaHoraFin}")
	public void eliminarTurnosOcupadosFechasHoras(@PathVariable Long profId, @PathVariable Long EspecId,
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date fechaHoraIni,
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date fechaHoraFin) {
		turnoService.eliminarTurnosOcupadosFechasHoras(profId, EspecId, fechaHoraIni, fechaHoraFin);
	}
	
	@GetMapping("/eliminar-sobreturnos-ocupados-fechas/{profId}/{EspecId}/{fechaIni}/{fechaFin}")
	public void eliminarSobreturnosOcupadosFechas(@PathVariable Long profId, @PathVariable Long EspecId,
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaIni,
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaFin) {
		sobreturnoService.eliminarSobreturnosOcupadosFechas(profId, EspecId, fechaIni, fechaFin);
	}
	
	@GetMapping("/eliminar-sobreturnos-ocupados-fechas-horas/{profId}/{EspecId}/{fechaHoraIni}/{fechaHoraFin}")
	public void eliminarSobreturnosOcupadosFechasHoras(@PathVariable Long profId, @PathVariable Long EspecId,
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date fechaHoraIni,
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date fechaHoraFin) {
		sobreturnoService.eliminarSobreturnosOcupadosFechasHoras(profId, EspecId, fechaHoraIni, fechaHoraFin);
	}

}

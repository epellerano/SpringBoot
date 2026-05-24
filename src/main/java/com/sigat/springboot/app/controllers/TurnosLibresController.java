package com.sigat.springboot.app.controllers;


import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sigat.springboot.app.models.entity.PlanillaCabecera;
import com.sigat.springboot.app.models.entity.PlanillaDetalle;
import com.sigat.springboot.app.models.entity.Turno;
import com.sigat.springboot.app.models.entity.Vinculacion;
import com.sigat.springboot.app.models.service.IPlanillaCabeceraService;
import com.sigat.springboot.app.models.service.IPlanillaDetalleService;
import com.sigat.springboot.app.models.service.IProfesionalService;
import com.sigat.springboot.app.models.service.ISobreTurnoService;
import com.sigat.springboot.app.models.service.ITurnoService;
import com.sigat.springboot.app.models.service.IVinculacionService;

@RestController
@RequestMapping("api/turnosLibresController")
public class TurnosLibresController {

	@Autowired
	private IPlanillaDetalleService planillaDetalleService;
	@Autowired
	private IPlanillaCabeceraService planillacabeceraService;
	@Autowired
	private IVinculacionService vinculacionService;
	@Autowired
	private IProfesionalService profesionalService;
	@Autowired
	private ITurnoService turnoService;
	@Autowired
	private ISobreTurnoService sobreturnoService;

	@GetMapping("/between/{profId}/{EspecId}")
	public List<PlanillaDetalle> obtenerTurnosLibresTodos(@PathVariable Long profId, @PathVariable Long EspecId) {
		// Esta es la versión de tu backup que no fallaba
		return planillaDetalleService.mostrarTurnosLibresTodos(profId, EspecId);
	}

	@GetMapping("/between-dia-id/{profId}/{EspecId}/{diaId}")
	public List<PlanillaDetalle> mostrarTurnosLibresTodosByDiaId(@PathVariable Long profId, @PathVariable Long EspecId,
			@PathVariable Long diaId) {
		return planillaDetalleService.mostrarTurnosLibresTodosByDiaId(profId, EspecId, diaId);
	}
	
	//PARA TRABAJAR EN SOBRETURNOS Y LISTAR LOS TURNOS LIBRES
	@GetMapping("/betweensobreturnos/{profId}/{EspecId}")
	public List<PlanillaDetalle> mostrarHorariosParaSobreturnosTodos(@PathVariable Long profId, @PathVariable Long EspecId) {
		// Esta es la versión de tu backup que no fallaba
		return planillaDetalleService.mostrarHorariosParaSobreturnosTodos(profId, EspecId);
	}

	//PARA TRABAJAR EN SOBRETURNOS Y LISTAR LOS TURNOS LIBRES por dia
	@GetMapping("/betweensobreturnos-dia-id/{profId}/{EspecId}/{diaId}")
	public List<PlanillaDetalle> mostrarHorariosParaSobreturnosByDiaId(@PathVariable Long profId, @PathVariable Long EspecId,
			@PathVariable Long diaId) {
		return planillaDetalleService.mostrarHorariosParaSobreturnosByDiaId(profId, EspecId, diaId);
	}

	@GetMapping("/horarios/{profId}/{EspecId}")
	public List<PlanillaCabecera> obtenerHorariosAtencion(@PathVariable Long profId, @PathVariable Long EspecId) {
		return planillacabeceraService.findHorariosByProfIdyEspId(profId, EspecId);
	}

	@GetMapping("/horarios-dia-id/{profId}/{EspecId}/{diaId}")
	public List<PlanillaCabecera> obtenerHorariosAtencionByDiaId(@PathVariable Long profId, @PathVariable Long EspecId,
			@PathVariable Long diaId) {
		return planillacabeceraService.findHorariosByProfIdyEspIdAndDiaId(profId, EspecId, diaId);
	}

	@GetMapping("/obtener-observaciones/{profId}/{EspecId}")
	public List<Vinculacion> obtenerObservaciones(@PathVariable Long profId, @PathVariable Long EspecId) {
		return vinculacionService.findObservacionByProfIdyEspId(profId, EspecId);

	}

	@GetMapping("/turnos-ocupados/{profId}/{EspecId}")
	public List<Turno> obtenerTurnosOcupados(@PathVariable Long profId, @PathVariable Long EspecId) {
		return turnoService.findTurnosOcupadosByProfIdyEspId(profId, EspecId);
	}

	/*
	 * @GetMapping("/turnos-ocupados/{profId}/{EspecId}")
	 * 
	 * @ResponseBody public List<AgendaOcupadaDTO> obtenerTurnosOcupados(
	 * 
	 * @PathVariable Long profId,
	 * 
	 * @PathVariable Long EspecId,
	 * 
	 * @RequestParam(name = "incluirSobreturnos", defaultValue = "false") boolean
	 * incluirSobreturnos) {
	 * 
	 * List<AgendaOcupadaDTO> listaFinal = new ArrayList<>();
	 * 
	 * // 1. Turnos Ocupados normales List<Turno> turnos =
	 * turnoService.findTurnosOcupadosByProfIdyEspId(profId, EspecId); for (Turno t
	 * : turnos) { AgendaOcupadaDTO dto = new AgendaOcupadaDTO();
	 * dto.setId(t.getId()); dto.setPacienteNombre(t.getPaciente().getApellido() +
	 * " " + t.getPaciente().getNombre());
	 * dto.setFechaHora(t.getPlanilladetalle().getRangoFechaHora()); // Box original
	 * (1, 2, 3...) dto.setBox(t.getPlanilladetalle().getBox() != null ?
	 * String.valueOf(t.getPlanilladetalle().getBox()) : "1");
	 * dto.setEsSobreturno(false); listaFinal.add(dto); }
	 * 
	 * // 2. Si el check está activo, sumamos los Sobreturnos if
	 * (incluirSobreturnos) { List<Sobreturno> sobreturnos =
	 * sobreturnoService.findSobreTurnosOcupadosByProfIdyEspId(profId, EspecId); for
	 * (Sobreturno s : sobreturnos) { AgendaOcupadaDTO dto = new AgendaOcupadaDTO();
	 * dto.setId(s.getId()); dto.setPacienteNombre(s.getPaciente().getApellido() +
	 * " " + s.getPaciente().getNombre()); // Convertimos de LocalDateTime a Date
	 * para que el DTO sea feliz if (s.getRangoFechaHora() != null) {
	 * dto.setFechaHora(java.sql.Timestamp.valueOf(s.getRangoFechaHora())); }
	 * 
	 * // CLAVE: Forzamos el texto SBT para la columna BOX dto.setBox("SBT");
	 * 
	 * dto.setEsSobreturno(true); listaFinal.add(dto); } }
	 * 
	 * // 3. Ordenamos para que el sobreturno aparezca debajo del turno que lo
	 * generó listaFinal.sort(Comparator.comparing(AgendaOcupadaDTO::getFechaHora));
	 * 
	 * return listaFinal; }
	 */

	@GetMapping("/obtener-turno-id/{turnoId}")
	public List<Turno> findTurnoClickById(@PathVariable Long turnoId) {
		return turnoService.findTurnoClickById(turnoId);
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

	// ACTUALIZAMOS en Planilladetalle el estado_id=8 (EXPIRADO).
	/*
	 * @PostMapping("/actualizar-planilladetalle-expirad") public String
	 * actualizarPlanillaDetalleExpirado() { try { // ACTUALIZAMOS
	 * planillaDetalleService.updatePlanillaDetalleTurnoExpirado(); return
	 * "Actualización exitosa"; } catch (Exception e) { return
	 * "Error en la actualización: " + e.getMessage(); } }
	 */

	/*
	 * @GetMapping("/horarios/{profId}/{EspecId}") public
	 * ResponseEntity<PlanillaCabecera> obtenerHorariosAtencion(@PathVariable Long
	 * profId,@PathVariable Long EspecId) { List<PlanillaCabecera> planilla =
	 * planillacabeceraService.findByProfIdyEspId(profId, EspecId);
	 * //planillaRepository.findById(id).orElse(null); if (planilla == null) {
	 * return new ResponseEntity<>(HttpStatus.NOT_FOUND); } return new
	 * ResponseEntity<>(planilla, HttpStatus.OK); }
	 */

}

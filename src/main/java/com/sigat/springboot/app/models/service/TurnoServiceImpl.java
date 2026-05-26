package com.sigat.springboot.app.models.service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sigat.springboot.app.models.dao.IEspecialidadDao;
import com.sigat.springboot.app.models.dao.IEstadoDao;
import com.sigat.springboot.app.models.dao.IPacienteDao;
import com.sigat.springboot.app.models.dao.IProfesionalDao;
import com.sigat.springboot.app.models.dao.ITurnosDao;
import com.sigat.springboot.app.models.entity.Especialidad;
import com.sigat.springboot.app.models.entity.Estado;
import com.sigat.springboot.app.models.entity.Paciente;
import com.sigat.springboot.app.models.entity.PlanillaCabecera;
import com.sigat.springboot.app.models.entity.PlanillaDetalle;
import com.sigat.springboot.app.models.entity.Profesional;
import com.sigat.springboot.app.models.entity.Sobreturno;
import com.sigat.springboot.app.models.entity.Turno;

@Service
public class TurnoServiceImpl implements ITurnoService {

	@Autowired
	private ITurnosDao turnoDao;
	@Autowired
	private EmailService emailService;
	@Autowired
	private IPacienteService pacienteService;
	@Autowired
	@Lazy // <-- Agregá esto
	private IPlanillaDetalleService planilladetalleService;
	@Autowired
	private IProfesionalService profesionalService;
	@Autowired
	private IEspecialidadService especialidadService;
	@Autowired
	private IEstadoDao estadoDao;

	@Override
	@Transactional(readOnly = true)
	public List<Turno> findAll() {
		return (List<Turno>) turnoDao.findAll();
	}

	@Override
	@Transactional
	public void save(Turno turno) {
		turnoDao.save(turno);
	}

	@Override
	@Transactional
	public void registrarTurnoCompleto(Turno turno, String planillaDetId) {

		// 1. ASIGNAMOS EL ESTADO "OCUPADO" (ID 4) AL TURNO
		// Esto garantiza que aparezca en el listado filtrado
		Estado estadoOcupado = new Estado();
		estadoOcupado.setId(4L);
		turno.setEstado(estadoOcupado);

		// 2. Guardamos el turno (Ahora ya tiene el estado_id = 4)
		turnoDao.save(turno);

		// 3. Marcamos la planilla detalle como OCUPADA (Estado de la agenda)
		planilladetalleService.updatePlanillaDetalleTurnoOcupado(planillaDetId);

		// 4. RELLENAMOS TODOS LOS DATOS FALTANTES (Tu lógica de "curación" para el
		// Mail)
		PlanillaDetalle pd = planilladetalleService.findOne(Long.parseLong(planillaDetId));
		Paciente pacienteCompleto = pacienteService.findOne(turno.getPaciente().getId());
		Profesional profesionalCompleto = profesionalService.findOne(turno.getProfesional().getId());
		Especialidad especialidadCompleta = especialidadService.findOne(turno.getEspecialidad().getId());

		// Inyectamos los objetos con datos reales
		turno.setPlanilladetalle(pd);
		turno.setPaciente(pacienteCompleto);
		turno.setProfesional(profesionalCompleto);
		turno.setEspecialidad(especialidadCompleta);

		// 5. Enviamos el mail
		emailService.enviarMailConfirmacion(turno);
	}
	
	@Override
	@Transactional
	public void registrarTurnoDesdeWeb(Long planillaDetId, Long planillaCabId, Long pacienteId, String observacion) {

	    // 1. Buscamos y recuperamos las entidades raíz desde sus servicios de fábrica
	    PlanillaDetalle pd = planilladetalleService.findOne(planillaDetId);
	    Paciente pacienteCompleto = pacienteService.findOne(pacienteId);
	    
	    Long profesionalId = pd.getPlanillacabecera().getProfesional().getId();
	    Long especialidadId = pd.getPlanillacabecera().getEspecialidad().getId();
	    
	    Profesional profesionalCompleto = profesionalService.findOne(profesionalId);
	    Especialidad especialidadCompleta = especialidadService.findOne(especialidadId);

	    // 2. Instanciamos el Turno nuevo en caliente
	    Turno nuevoTurno = new Turno();
	    nuevoTurno.setObservacion(observacion != null ? observacion : "TURNO PARTICULAR");
	    
	    Estado estadoOcupado = new Estado();
	    estadoOcupado.setId(4L); // ID 4 = OCUPADO
	    nuevoTurno.setEstado(estadoOcupado);
	    
	    // Inyección de la Cabecera (Sana el INNER JOIN de tu query findTurnosOcupados)
	    if (pd.getPlanillacabecera() != null) {
	        nuevoTurno.setPlanillacabecera(pd.getPlanillacabecera());
	    }

	    // VINCULACIÓN DE RELACIÓN CON PLANILLA DETALLE (Aquí viaja la fecha de forma relacional)
	    nuevoTurno.setPlanilladetalle(pd);
	    
	    // Hidratamos el resto de las relaciones obligatorias de tu entidad Turno
	    nuevoTurno.setPaciente(pacienteCompleto);
	    nuevoTurno.setProfesional(profesionalCompleto);
	    nuevoTurno.setEspecialidad(especialidadCompleta);

	    // Sincronizamos la colección bidireccional One-To-Many de la planilla
	    if (pd.getTurno() == null) {
	        pd.setTurno(new java.util.ArrayList<>());
	    }
	    pd.getTurno().add(nuevoTurno);

	    // 3. Persistimos el Turno en tu tabla nativa de MySQL
	    turnoDao.save(nuevoTurno);

	    // 4. Marcamos la agenda del médico como ocupada (pd.estado_id = 4)
	    planilladetalleService.updatePlanillaDetalleTurnoOcupado(String.valueOf(planillaDetId));

	    // 5. El mail vuela de forma asíncrona mediante el hilo @Async
	    emailService.enviarMailConfirmacion(nuevoTurno);
	}




	@Override
	@Transactional(readOnly = true)
	public Turno findOne(Long id) {
		return turnoDao.findById(id).orElse(null);
	}

	/*
	 * @Override
	 * 
	 * @Transactional public void delete(Long id) { turnoDao.deleteById(id); }
	 */

	@Override
	@Transactional
	public void delete(Long id) {
		// 1. Buscamos el turno completo
		Turno turno = turnoDao.findById(id).orElse(null);

		if (turno != null) {
			// 2. Buscamos el objeto Estado 'CANCELADO' (ID 6)
			Estado estadoCancelado = estadoDao.findById(6L).orElse(null);

			// 3. CAMBIO DE ESTADO en lugar de delete físico
			turno.setEstado(estadoCancelado);

			// 4. GUARDAR: Aquí se dispara la auditoría automática
			turnoDao.save(turno);
		}
		// IMPORTANTE: Ya NO usamos turnoDao.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public String[] buscarPlanillaActiva(long profId, long EspecId) {
		// TODO Auto-generated method stub
		return turnoDao.buscarPlanillaActiva(profId, EspecId);
	}

	// Muestra la Planilla Cabecera en Turnos por idProfesional y id_Especialidad.
	@Override
	@Transactional(readOnly = true)
	public List<Turno> findTurnosOcupadosByProfIdyEspId(Long profId, Long EspecId) {
		return turnoDao.findTurnosOcupadosByProfIdyEspId(profId, EspecId);
	}

	// Muestra la Planilla Cabecera en Turnos por idProfesional, id_Especialidad y
	// paciente_id.
	@Override
	@Transactional(readOnly = true)
	public List<Turno> findTurnoClickById(Long turnoId) {
		return turnoDao.findTurnoClickById(turnoId);
	}

	// buscar turnos ocupados fechas (vista: Cancelar Planillas)
	@Override
	@Transactional(readOnly = true)
	public List<Turno> buscarTurnosOcupadosFechas(Long profId, Long EspecId, Date fechaIni, Date fechaFin) {
		return turnoDao.buscarTurnosOcupadosFechas(profId, EspecId, fechaIni, fechaFin);
	}

	// buscar turnos ocupados fechas y horas (vista: Cancelar Planillas)
	@Override
	@Transactional(readOnly = true)
	public List<Turno> buscarTurnosOcupadosFechasHoras(Long profId, Long EspecId, Date fechaHoraIni,
			Date fechaHoraFin) {
		return turnoDao.buscarTurnosOcupadosFechasHoras(profId, EspecId, fechaHoraIni, fechaHoraFin);
	}

	// Eliminar "Turnos Ocupados" entre fechas (vista: Cancelar Planillas)
	@Override
	@Transactional
	public void desactivarSafeUpdates() {
		turnoDao.desactivarSafeUpdates();
	}

	// CANCELACION MASIVA DE TURNOS (Estado CANCELADO - 6)
	@Override
	@Transactional
	public void cancelarTurnosSeleccionados(List<Long> idsTurnos) {

		List<Long> idsPlanillas = new ArrayList<>();

		// 1. Buscamos los turnos para obtener sus IDs de planilla
		for (Long id : idsTurnos) {
			Turno turno = turnoDao.findById(id).orElse(null);
			if (turno != null && turno.getPlanilladetalle() != null) {
				idsPlanillas.add(turno.getPlanilladetalle().getId());
			}
		}

		// 2. Usamos el DAO de Turnos para cambiar estado a 6 (CANCELADO)
		turnoDao.cancelarVariosTurnosPorId(idsTurnos);

		// 3. Usamos el SERVICE de PlanillaDetalle para liberar los horarios (Estado 3)
		if (!idsPlanillas.isEmpty()) {
			planilladetalleService.updatePlanillaDetalleMasivoLibre(idsPlanillas);
		}
	}

	// CANCELACION INDIVIDUAL DE TURNOS (Estado CANCELADO - 6)
	@Override
	@Transactional
	public void eliminar(Long id) {
		// 1. Buscamos el turno para obtener la referencia de la planilla
		Turno turno = turnoDao.findById(id).orElse(null);

		if (turno != null) {
			// 2. Cambiamos el estado del turno a 6 (CANCELADO) a través del DAO
			turnoDao.cancelarTurnoIndividual(id);

			// 3. IMPORTANTE: Liberamos la planilla detalle (Estado 3 - LIBRE)
			// Usamos el service de planilla detalle para respetar la arquitectura
			if (turno.getPlanilladetalle() != null) {
				planilladetalleService
						.updatePlanillaDetalleTurnoLibre(String.valueOf(turno.getPlanilladetalle().getId()));
			}
		}
	}

	// LISTAR TURNOS SOLO CON ID=4
	@Override
	@Transactional(readOnly = true)
	public List<Turno> findAllOcupados() {
		return (List<Turno>) turnoDao.findAllOcupados();
	}

	// CANCELACION MASIVA NUEVA
	@Override
	@Transactional
	public void eliminarTurnosOcupadosFechas(Long profId, Long especId, LocalDateTime fechaIni,
			LocalDateTime fechaFin) {
		turnoDao.eliminarTurnosOcupadosFechas(profId, especId, fechaIni, fechaFin);
	}

	@Override
	@Transactional
	public void eliminarTurnosOcupadosFechasHoras(Long profId, Long especId, LocalDateTime inicio, LocalDateTime fin) {
		turnoDao.eliminarTurnosOcupadosFechasHoras(profId, especId, inicio, fin);
	}

	// PARA CANCELACION DE PLANILLAS NUEVO
	@Override
	@Transactional(readOnly = true)
	public List<Turno> buscarTurnosOcupadosParaBajaSoloFecha(Long profId, Long especId, String inicio, String fin) {
		return turnoDao.buscarTurnosOcupadosParaBajaSoloFecha(profId, especId, inicio, fin);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Turno> buscarTurnosOcupadosParaBajaFechaHora(Long profId, Long especId, LocalDateTime inicio,
			LocalDateTime fin) {
		return turnoDao.buscarTurnosOcupadosParaBajaFechaHora(profId, especId, inicio, fin);
	}

	// PARA RESTAURACION (AUDITORIA)
	@Override
	@Transactional(readOnly = true)
	public List<Turno> findCancelados(Long profId, Long especId, String desde, String hasta) {
	    java.util.Date fechaD = null;
	    java.util.Date fechaH = null;
	    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");

	    try {
	        if (desde != null && !desde.isEmpty()) {
	            fechaD = sdf.parse(desde);
	        }
	        if (hasta != null && !hasta.isEmpty()) {
	            // Parseamos la fecha y le sumamos 23h 59m 59s para cubrir el día completo
	            fechaH = sdf.parse(hasta);
	            long unDiaEnMilis = (24 * 60 * 60 * 1000) - 1000; 
	            fechaH = new java.util.Date(fechaH.getTime() + unDiaEnMilis);
	        }
	    } catch (Exception e) {
	        System.err.println("Error parseando fechas en Turnos: " + e.getMessage());
	    }

	    return turnoDao.findCanceladosConFiltro(profId, especId, fechaD, fechaH);
	}
	
	//HISTORIA CLINICA
	@Override
	@Transactional(readOnly = true)
	public Turno findById(Long id) {
	    return turnoDao.findById(id).orElse(null);
	}
	
	//click en finalizarAtencion, paciente se retira por su cuenta
	@Override
	@Transactional(readOnly = true)
	public List<Turno> findAllTotal() {
	    // El método del Dao (Repository) es el que tiene la verdad absoluta de la BD
	    return (List<Turno>) turnoDao.findAll(); 
	}


	/*
	 * @Override
	 * 
	 * @Transactional public String restaurarTurnoCancelado(Long id) { return
	 * restaurarTurnoIndividual(id); }
	 * 
	 * // 2. Este es el método que tiene el "cerebro" de la operación // Asegúrate
	 * de que el nombre sea EXACTAMENTE este: public String
	 * restaurarTurnoIndividual(Long turnoId) { Turno turno =
	 * turnoDao.findById(turnoId).orElse(null); if (turno == null) return
	 * "Error: Turno no encontrado.";
	 * 
	 * try { Date fechaTurnoDate = turno.getPlanilladetalle().getRangoFechaHora();
	 * Date ahora = new Date();
	 * 
	 * if (fechaTurnoDate.before(ahora)) { SimpleDateFormat sdf = new
	 * SimpleDateFormat("dd/MM/yyyy HH:mm"); return
	 * "No se puede restaurar: El horario (" + sdf.format(fechaTurnoDate) +
	 * ") ya pasó."; }
	 * 
	 * if (turno.getPlanilladetalle().getEstado().getId() != 3L) { return
	 * "No se puede restaurar: El horario ya ha sido ocupado o bloqueado."; }
	 * 
	 * Estado ocupado = new Estado(); ocupado.setId(4L); turno.setEstado(ocupado);
	 * turno.getPlanilladetalle().setEstado(ocupado);
	 * 
	 * turnoDao.save(turno); return "OK"; } catch (Exception e) { return
	 * "Error crítico: " + e.getMessage(); } }
	 */

	// SECCION REPORTES-----------------------------------

	@Override
	@Transactional(readOnly = true)
	public List<Turno> reporteTurnosOcupadosFechas(Long profId, Long EspecId, Date fechaIni, Date fechaFin) {
		return turnoDao.reporteTurnosOcupadosFechas(profId, EspecId, fechaIni, fechaFin);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Sobreturno> reporteSobreTurnosOcupadosFechas(Long profId, Long EspecId, Date fechaIni, Date fechaFin) {
		return turnoDao.reporteSobreTurnosOcupadosFechas(profId, EspecId, fechaIni, fechaFin);
	}

	// buscar paciente en Turnos Activos antes de eliminar.
	@Override
	@Transactional(readOnly = true)
	public List<Turno> pacientePoseeTurnosActivos(Long pacId) {
		return turnoDao.pacientePoseeTurnosActivos(pacId);
	}

	// SECCION REPORTES PARA PDF

	@Override
	@Transactional(readOnly = true)
	public List<Turno> reporteTurnosOcupadosFechasPdf(Long profId, Long EspecId, String fechaIni, String fechaFin) {
		return turnoDao.reporteTurnosOcupadosFechasPdf(profId, EspecId, fechaIni, fechaFin);
	}

}

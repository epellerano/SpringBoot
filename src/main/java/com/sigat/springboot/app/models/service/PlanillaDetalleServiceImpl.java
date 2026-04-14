package com.sigat.springboot.app.models.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sigat.springboot.app.models.dao.IPlanillaDetalleDao;
import com.sigat.springboot.app.models.entity.PlanillaDetalle;
import com.sigat.springboot.app.models.entity.Turno;
import com.sigat.springboot.app.models.entity.Vinculacion;

@Service
public class PlanillaDetalleServiceImpl implements IPlanillaDetalleService {

	@Autowired
	private IPlanillaDetalleDao planilladetalleDao;

	@Override
	@Transactional(readOnly = true)
	public long ExistePlanillaDetalleActiva(long term1, long term2, long term3, long term4) {
		return planilladetalleDao.ExistePlanillaDetalleActiva(term1, term2, term3, term4);
	}

	@Override
	@Transactional(readOnly = true)
	public String[] ListarRangoPlanillaDet(long profId, long EspecId) {
		return planilladetalleDao.ListarRangoPlanillaDet(profId, EspecId);
	}

	@Override
	public void save(PlanillaDetalle planilladetalle) {
		planilladetalleDao.save(planilladetalle);

	}

	@Override
	public void saveAll(List<PlanillaDetalle> detalleList) {
		planilladetalleDao.saveAll(detalleList);

	}

	@Override
	@Transactional(readOnly = true)
	public List<String> generarRangoHorario(String fechaHoraInicial, String fechaHoraFinal, long intervalo, long dia) {
		return planilladetalleDao.generarRangoHorario(fechaHoraInicial, fechaHoraFinal, intervalo, dia);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Object[]> generarRangoHorarioPersonalizado(String fechaHoraInicial, String fechaHoraFinal, long dia,
			String box) {
		return planilladetalleDao.generarRangoHorarioPersonalizado(fechaHoraInicial, fechaHoraFinal, dia, box);
	}

	@Override
	@Transactional(readOnly = true)
	public List<String> generarRangoHorarioCompleto(String fechaHoraInicial, String fechaHoraFinal, long intervalo,
			long dia, String fechaHoraInicialR2, String fechaHoraFinalR2, long intervaloR2) {
		return planilladetalleDao.generarRangoHorarioCompleto(fechaHoraInicial, fechaHoraFinal, intervalo, dia,
				fechaHoraInicialR2, fechaHoraFinalR2, intervaloR2);
	}

	// Metodo para mostrar los detalles de acuerdo a la planillacabeceraId.
	@Override
	@Transactional(readOnly = true)
	public List<PlanillaDetalle> verPlanillaDetalle(long idPlanillacab) {
		return planilladetalleDao.verPlanillaDetalle(idPlanillacab);
	}

	// Metodo para mostrar los datos del Profesional y Especialidad acuerdo a la
	// planillacabeceraId.
	@Override
	@Transactional(readOnly = true)
	public List<PlanillaDetalle> verPlanillaDetalleProfyEspecialidadDistinct(long idPlanillacab) {
		return planilladetalleDao.verPlanillaDetalleProfyEspecialidadDistinct(idPlanillacab);
	}

	// muestra los turnos libres (de todos los Dias)de acuerdo a la planilla detalle
	// por ProfId y EspecialidadID.
	@Override
	@Transactional(readOnly = true)
	public List<PlanillaDetalle> mostrarTurnosLibresTodos(Long profId, Long EspecId) {
		return planilladetalleDao.mostrarTurnosLibresTodos(profId, EspecId);
	}

	// muestra los turnos libres (de todos los Dias)de acuerdo a la planilla detalle
	// por ProfId y EspecialidadID.
	/*
	 * @Override
	 * 
	 * @Transactional(readOnly = true) public List<PlanillaDetalle>
	 * mostrarHorariosParaSobreturno(Long profId, Long EspecId) { return
	 * planilladetalleDao.mostrarHorariosParaSobreturno(profId, EspecId); }
	 */
	
	// muestra los turnos libres (de todos los Dias)de acuerdo a la planilla detalle
	// por ProfId, EspecialidadID y diaId.
	@Override
	@Transactional(readOnly = true)
	public List<PlanillaDetalle> mostrarTurnosLibresTodosByDiaId(Long profId, Long EspecId, Long diaId) {
		return planilladetalleDao.mostrarTurnosLibresTodosByDiaId(profId, EspecId, diaId);
	}

	// ACTUALIZAMOS en Planilladetalle el estado_id=4 (OCUPADO).
	@Override
	@Transactional
	public void updatePlanillaDetalleTurnoOcupado(String planillaDetId) {
		planilladetalleDao.updatePlanillaDetalleTurnoOcupado(planillaDetId);
		;
	}

	// ACTUALIZAMOS en Planilladetalle el estado_id=3 (LIBRE).
	@Override
	@Transactional
	public void updatePlanillaDetalleTurnoLibre(String planillaDetId) {
		planilladetalleDao.updatePlanillaDetalleTurnoLibre(planillaDetId);
	}

	// ACTUALIZAMOS en Planilladetalle el estado_id=8 (EXPIRADO).
	@Override
	@Transactional
	public void updatePlanillaDetalleTurnoExpirado(String estadoId) {
		planilladetalleDao.updatePlanillaDetalleTurnoExpirado(estadoId);
	}

	// ACTUALIZAMOS en PlanillaCabecera el id_estado=1 (INACTIVO).
	@Override
	@Transactional
	public void updatePlanillaCabeceraInactivo(String estadoId) {
		planilladetalleDao.updatePlanillaCabeceraInactivo(estadoId);
	}

	// VERIFICAR SI EXISTE FECHA_HORA DEL SOBRETURNO EN PLANILLA DETALLE.
	@Override
	@Transactional(readOnly = true)
	public List<String> existeSobreturnoEnPlanillaDetalle(Long profId, Long EspecId, String fechaHora) {
		return planilladetalleDao.existeSobreturnoEnPlanillaDetalle(profId, EspecId, fechaHora);
	}
	
	//buscar turnos libres fechas (vista: Cancelar Planillas)
	@Override
	@Transactional(readOnly=true)
	public List<PlanillaDetalle> buscarTurnosLibresFechas(Long profId, Long EspecId, Date fechaIni, Date fechaFin) {
		return planilladetalleDao.buscarTurnosLibresFechas(profId, EspecId, fechaIni, fechaFin);
	}
	
	//buscar turnos libres fechas y horas(vista: Cancelar Planillas)
	@Override
	@Transactional(readOnly=true)
	public List<PlanillaDetalle> buscarTurnosLibresFechasHoras(Long profId, Long EspecId, Date fechaHoraIni, Date fechaHoraFin) {
		return planilladetalleDao.buscarTurnosLibresFechasHoras(profId, EspecId, fechaHoraIni, fechaHoraFin);
	}
	
	// ACTUALIZAMOS en Planilladetalle el estado_id=6 (CANCELADO). (vista: Cancelar Planillas) Turnos Libres
	@Override
	@Transactional
	public void actualizarPlanillaDetalleCanceladoFechasTL(Long profId, Long EspecId, Date fechaIni, Date fechaFin ,String estadoId, String motivo) {
		planilladetalleDao.actualizarPlanillaDetalleCanceladoFechasTL(profId, EspecId, fechaIni, fechaFin ,estadoId, motivo);
	}
	
	// ACTUALIZAMOS en Planilladetalle el estado_id=6 (CANCELADO). (vista: Cancelar Planillas) Turnos Libres
	@Override
	@Transactional
	public void actualizarPlanillaDetalleCanceladoFechasHorasTL(Long profId, Long EspecId, Date fechaHoraIni, Date fechaHoraFin ,String estadoId, String motivo) {
		planilladetalleDao.actualizarPlanillaDetalleCanceladoFechasHorasTL(profId, EspecId, fechaHoraIni, fechaHoraFin ,estadoId, motivo);
	}
	
	// ACTUALIZAMOS en Planilladetalle el estado_id=6 (CANCELADO). (vista: Cancelar Planillas) Turnos Ocupados
	@Override
	@Transactional
	public void actualizarPlanillaDetalleCanceladoFechasTO(Long profId, Long EspecId, Date fechaIni, Date fechaFin ,String estadoId, String motivo) {
		planilladetalleDao.actualizarPlanillaDetalleCanceladoFechasTO(profId, EspecId, fechaIni, fechaFin ,estadoId, motivo);
	}
	
	// ACTUALIZAMOS en Planilladetalle el estado_id=6 (CANCELADO). (vista: Cancelar Planillas) Turnos Ocupados
	@Override
	@Transactional
	public void actualizarPlanillaDetalleCanceladoFechasHorasTO(Long profId, Long EspecId, Date fechaHoraIni, Date fechaHoraFin ,String estadoId, String motivo) {
		planilladetalleDao.actualizarPlanillaDetalleCanceladoFechasHorasTO(profId, EspecId, fechaHoraIni, fechaHoraFin ,estadoId, motivo);
	}

	 @Override
	 @Transactional(readOnly = true)
	 public PlanillaDetalle findOne(Long id) {
	     // Buscamos la planilla por ID. Si no existe, devolvemos null.
	     // Esto recupera el objeto con el campo 'rangoFechaHora' lleno.
	     return planilladetalleDao.findById(id).orElse(null);
	 }
}

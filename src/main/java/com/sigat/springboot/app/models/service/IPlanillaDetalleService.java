package com.sigat.springboot.app.models.service;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.sigat.springboot.app.models.entity.PlanillaDetalle;
import com.sigat.springboot.app.models.entity.Turno;

public interface IPlanillaDetalleService {
	
	// ... tus otros métodos (como el de actualizar estado) ...
    public PlanillaDetalle findOne(Long id);

	// Metodo verificar si Existe PlanillaDetalle Activa
	public long ExistePlanillaDetalleActiva(long term1, long term2, long term3, long term4);

	// Metodo para Listar Dia y Rango Fecha Hora de Planilladetalle
	public String[] ListarRangoPlanillaDet(long profId, long EspecId);

	// Metodo para Listar Rango Fecha Hora de Planilladetalle
	public List<String> generarRangoHorario(String fechaHoraInicial, String fechaHoraFinal, long intervalo, long dia);
	// Metodo para Listar Rango Fecha Hora de Planilladetalle personalizado
	public List<Object[]> generarRangoHorarioPersonalizado(String fechaHoraInicial, String fechaHoraFinal, long dia, String box);

	// Metodo para Listar Rango Fecha Hora completo de Planilladetalle
	public List<String> generarRangoHorarioCompleto(String fechaHoraInicial, String fechaHoraFinal, long intervalo,
			long dia, String fechaHoraInicialR2, String fechaHoraFinalR2, long intervaloR2);
	
	// Metodo para mostrar los detalles de acuerdo a la planillacabeceraId.
	public List<PlanillaDetalle> verPlanillaDetalle(long idPlanillacab); 

	// Metodo para mostrar los detalles de medico y especialidad para que no se repitan
	public List<PlanillaDetalle> verPlanillaDetalleProfyEspecialidadDistinct(long idPlanillacab);
		
	//muestra los turnos libres (de todos los Dias)de acuerdo a la planilla detalle por ProfId y EspecialidadID.
	public List<PlanillaDetalle> mostrarTurnosLibresTodos(Long profId, Long EspecId);
	
	//muestra los turnos libres (de todos los Dias)de acuerdo a la planilla detalle por ProfId y EspecialidadID.
	//public List<PlanillaDetalle> mostrarHorariosParaSobreturno(Long profId, Long EspecId);
	
	
	
	//muestra los turnos libres de acuerdo a la planilla detalle por ProfId, EspecialidadID y diaId.
	public List<PlanillaDetalle> mostrarTurnosLibresTodosByDiaId(Long profId, Long EspecId, Long diaId);
	
	// Metodo guardar - editar
	public void save(PlanillaDetalle planilladetalle);

	// metodo saveAll
	public void saveAll(List<PlanillaDetalle> detalleList);
	
	//ACTUALIZAMOS en Planilladetalle el estado_id=4 (OCUPADO).
	public void updatePlanillaDetalleTurnoOcupado(String planillaDetId);
	
	//ACTUALIZAMOS en Planilladetalle el estado_id=3 (LIBRE).
	public void updatePlanillaDetalleTurnoLibre(String planillaDetId);
	
	//ACTUALIZAMOS en Planilladetalle el estado_id=8 (EXPIRADO).
	public void updatePlanillaDetalleTurnoExpirado(String estadoId);
	
	//ACTUALIZAMOS en PlanillaCabecera el id_estado=1 (INACTIVO).
	public void updatePlanillaCabeceraInactivo(String estadoId);
	
	//VERIFICAR SI EXISTE FECHA_HORA DEL SOBRETURNO EN PLANILLA DETALLE.
	public List<String> existeSobreturnoEnPlanillaDetalle(Long profId, Long EspecId, String fechaHora);
	
	//buscar turnos libres fechas (vista: Cancelar Planillas)
	public List<PlanillaDetalle> buscarTurnosLibresFechas(Long profId, Long EspecId, Date fechaIni, Date fechaFin);
	
	//buscar turnos libres fechas y horas(vista: Cancelar Planillas)
	public List<PlanillaDetalle> buscarTurnosLibresFechasHoras(Long profId, Long EspecId, Date fechaHoraIni, Date fechaHoraFin);
	
	//ACTUALIZAMOS en Planilladetalle el estado_id=6 (CANCELADO). X Fechas. (vista: Cancelar Planillas) Turnos Libres
	public void actualizarPlanillaDetalleCanceladoFechasTL(Long profId, Long EspecId, Date fechaIni, Date fechaFin ,String estadoId, String motivo);
	
	//ACTUALIZAMOS en Planilladetalle el estado_id=6 (CANCELADO). X Fechas y Horas. (vista: Cancelar Planillas) Turnos Libres
	public void actualizarPlanillaDetalleCanceladoFechasHorasTL(Long profId, Long EspecId, Date fechaHoraIni, Date fechaHoraFin ,String estadoId, String motivo);
	
	//ACTUALIZAMOS en Planilladetalle el estado_id=6 (CANCELADO). X Fechas. (vista: Cancelar Planillas) Turnos Ocupados
	public void actualizarPlanillaDetalleCanceladoFechasTO(Long profId, Long EspecId, Date fechaIni, Date fechaFin ,String estadoId, String motivo);
	
	//ACTUALIZAMOS en Planilladetalle el estado_id=6 (CANCELADO). X Fechas horas. (vista: Cancelar Planillas) Turnos Ocupados
	public void actualizarPlanillaDetalleCanceladoFechasHorasTO(Long profId, Long EspecId, Date fechaHoraIni, Date fechaHoraFin ,String estadoId, String motivo);
}

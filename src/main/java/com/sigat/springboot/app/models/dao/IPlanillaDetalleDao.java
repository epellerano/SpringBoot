package com.sigat.springboot.app.models.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.sigat.springboot.app.models.entity.PlanillaDetalle;

import jakarta.transaction.Transactional;

public interface IPlanillaDetalleDao
		extends JpaRepository<PlanillaDetalle, Long>, CrudRepository<PlanillaDetalle, Long> {

	// Metodo que verifica si existe una planillaDet Activa (se usa en
	// PlanillaCabeceraController)
	@Query("select count(pd) from PlanillaDetalle pd "
			+ "inner join PlanillaCabecera pc on pc.id = pd.planillacabecera.id "
			+ "where pc.profesional.id = ?1 and pc.especialidad.id = ?2 and pd.planillacabecera.id = ?3 and pd.estado = ?4 ")
	long ExistePlanillaDetalleActiva(long profId, long EspecId, long idPlanillacab, long idEstado);

	// Listar Dia y Rango Fecha Hora de Planilladetalle mediante BD(se usa en
	// planillaCabeceraController)
	/* @Query(value="SET lc_time_names = 'es_ES'; " */
	@Query(value = "select DATE_FORMAT(pd.rango_fecha_hora, '%d/%m/%Y %H:%i') as FECHA from PlanillaDetalle pd "
			+ "inner join planillacabecera pc on pc.id = pd.planillacabecera_id "
			+ "where pc.id_profesional = ?1  and pc.id_especialidad = ?2 and pc.id_estado = 2 and pd.estado_id in (3,4) order by pd.rango_fecha_hora", nativeQuery = true)
	public String[] ListarRangoPlanillaDet(long profId, long EspecId);

	// Recursive query que genera rango horario y lo almacena en un LIST.
	@Query(value = "WITH RECURSIVE FECHAS AS (SELECT :fechaHoraInicial AS fecha " 
			+ "UNION ALL "
			+ "SELECT fecha + INTERVAL :intervalo MINUTE FROM FECHAS WHERE fecha + INTERVAL :intervalo MINUTE <= :fechaHoraFinal) "
			+ "SELECT fecha FROM FECHAS WHERE TIME(fecha) BETWEEN TIME(:fechaHoraInicial) "
			+ "AND TIME(:fechaHoraFinal) " 
			+ "AND DAYOFWEEK(fecha) = :dia " + "ORDER BY fecha asc", nativeQuery = true)
	public List<String> generarRangoHorario(String fechaHoraInicial, String fechaHoraFinal, long intervalo, long dia);

	// Recursive query que genera rango horario en grupos de 1 minuto y lo almacena
	// en un LIST.
	@Query(value = "WITH RECURSIVE FECHAS AS (SELECT :fechaHoraInicial AS fecha " + "UNION ALL "
			+ "SELECT fecha + INTERVAL 1 MINUTE FROM FECHAS WHERE fecha <= :fechaHoraFinal) "
			+ "SELECT fecha, 'PLANILLA PERSONALIZADA', DENSE_RANK() OVER (ORDER BY MINUTE(fecha) desc) FROM FECHAS WHERE TIME(fecha) "
			+ "BETWEEN TIME(:fechaHoraInicial) AND TIME(:fechaHoraFinal) " + "AND DAYOFWEEK(fecha) = :dia "
			+ "AND MINUTE(fecha) < :box +2 " + "and MINUTE(fecha) != 00 " + "and MINUTE(fecha) != 05 "
			+ "ORDER BY fecha asc", nativeQuery = true)
	public List<Object[]> generarRangoHorarioPersonalizado(String fechaHoraInicial, String fechaHoraFinal, long dia,
			String box);

	// Recursive query que genera rango horario completo y lo almacena en un LIST.
	@Query(value = "WITH RECURSIVE FECHAS AS ( " + "    SELECT :fechaHoraInicial AS fecha " + "    UNION ALL "
			+ "    SELECT fecha + INTERVAL :intervalo MINUTE " + "    FROM FECHAS"
			+ "    WHERE fecha + INTERVAL :intervalo MINUTE <= :fechaHoraFinal " + "), " + "FECHAS2 AS ( "
			+ "    SELECT :fechaHoraInicialR2 AS fecha " + "    UNION ALL "
			+ "    SELECT fecha + INTERVAL :intervaloR2 MINUTE " + "    FROM FECHAS2 "
			+ "    WHERE fecha + INTERVAL :intervaloR2 MINUTE <= :fechaHoraFinalR2 " + ") " + "SELECT fecha "
			+ "FROM FECHAS " + "WHERE TIME(fecha) BETWEEN TIME(:fechaHoraInicial) AND TIME(:fechaHoraFinal) "
			+ "AND DAYOFWEEK(fecha) = :dia " + "UNION ALL " + "SELECT fecha " + "FROM FECHAS2 "
			+ "WHERE TIME(fecha) BETWEEN TIME(:fechaHoraInicialR2) AND TIME(:fechaHoraFinalR2) "
			+ "AND DAYOFWEEK(fecha) = :dia " + "ORDER BY fecha asc", nativeQuery = true)
	public List<String> generarRangoHorarioCompleto(String fechaHoraInicial, String fechaHoraFinal, long intervalo,
			long dia, String fechaHoraInicialR2, String fechaHoraFinalR2, long intervaloR2);

	// muestra las planillas detalle de acuerdo a la planillacabeceraId.
	@Query("select pd from PlanillaDetalle pd " + "inner join PlanillaCabecera pc on pc.id = pd.planillacabecera.id "
			+ "inner join Profesional prof on prof.id = pc.profesional.id "
			+ "inner join Especialidad esp on esp.id = pc.especialidad.id "
			+ "where pc.id = :idPlanillacab order by pd.rangoFechaHora asc")
	public List<PlanillaDetalle> verPlanillaDetalle(long idPlanillacab);

	@Query(value = "select pd.id, prof.apellido, prof.nombre, pd.observacion, pd.estado_id, "
	        + "pd.planillacabecera_id, pd.rango_fecha_hora, est.nombre as nombre_estado, pd.box, "
	        + "pd.creado_por, pd.fecha_creacion, pd.modificado_por, pd.fecha_modificacion " // <--- AGREGADOS
	        + "from planilladetalle pd " 
	        + "inner join planillacabecera pc on pc.id = pd.planillacabecera_id "
	        + "inner join profesionales prof on prof.id = pc.id_profesional "
	        + "inner join estado est on est.id = pd.estado_id "
	        + "where pc.id = :idPlanillacab LIMIT 1", nativeQuery = true)
	public List<PlanillaDetalle> verPlanillaDetalleProfyEspecialidadDistinct(long idPlanillacab);

	// muestra los turnos libres (de todos los Dias)de acuerdo a la planilla
		// detalle por ProfId y EspecialidadID.
		@Query("select pd from PlanillaDetalle pd " + "inner join PlanillaCabecera pc on pc.id = pd.planillacabecera.id "
				+ "inner join Estado e on e.id = pd.estado.id "
				+ "where pc.profesional.id = :profId and pc.especialidad.id = :EspecId " + "and pc.estado.id = 2 "
				+ "and pd.estado.id IN (3, 6) " // <--- TRAE AMBOS 
				+ "and DATE_FORMAT(pd.rangoFechaHora,'%Y-%m-%d %H:%i') >= CURRENT_TIMESTAMP "
				+ "order by pd.rangoFechaHora asc")
		public List<PlanillaDetalle> mostrarTurnosLibresTodos(Long profId, Long EspecId);
		
	@Query("select pd from PlanillaDetalle pd " + "inner join PlanillaCabecera pc on pc.id = pd.planillacabecera.id "
			+ "inner join Estado e on e.id = pd.estado.id "
			+ "where pc.profesional.id = :profId and pc.especialidad.id = :EspecId " + "and pc.estado.id = 2 "
			+ "and pd.estado.id IN (3, 6) " // <--- TRAE AMBOS 
			+ "and pc.dia.id = :diaId "
			+ "and DATE_FORMAT(pd.rangoFechaHora,'%Y-%m-%d %H:%i') >= CURRENT_TIMESTAMP "
			+ "order by pd.rangoFechaHora asc")
	public List<PlanillaDetalle> mostrarTurnosLibresTodosByDiaId(Long profId, Long EspecId, Long diaId);

	// ACTUALIZAMOS en Planilladetalle el estado_id=4 (OCUPADO).
	@Modifying
	@Transactional
	@Query(value = "update planilladetalle pd set pd.estado_id=4 " + "where pd.id = :planillaDetId "
			+ "and pd.estado_id = 3", nativeQuery = true)
	public void updatePlanillaDetalleTurnoOcupado(@Param("planillaDetId") String planillaDetId);

	// ACTUALIZAMOS en Planilladetalle el estado_id=3 (LIBRE).
	@Modifying
	@Transactional
	@Query(value = "update planilladetalle pd set pd.estado_id=3 " + "where pd.id = :planillaDetId "
			+ "and pd.estado_id = 4", nativeQuery = true)
	public void updatePlanillaDetalleTurnoLibre(@Param("planillaDetId") String planillaDetId);

	// ACTUALIZAMOS en Planilladetalle el estado_id=8 (EXPIRADO).
	@Modifying
	@Transactional
	@Query(value = "update planilladetalle pd set pd.estado_id = :estadoId  "
			+ "where DATE_FORMAT(pd.rango_fecha_hora,'%Y-%m-%d %H:%i') < DATE_FORMAT(NOW(3), '%Y-%m-%d %H:%i') "
			+ "and pd.estado_id = 3", nativeQuery = true)
	public void updatePlanillaDetalleTurnoExpirado(@Param("estadoId") String estadoId);

	// ACTUALIZAMOS en PlanillaCabecera el estado_id=1 (INACTIVO).
	@Modifying
	@Transactional
	@Query(value = "update planillacabecera pc set pc.id_estado = :estadoId, pc.observacion='PLANILLA INACTIVA' "
			+ "where DATE_FORMAT(pc.fecha_expira,'%Y-%m-%d %H:%i') < DATE_FORMAT(NOW(3), '%Y-%m-%d %H:%i')", nativeQuery = true)
	public void updatePlanillaCabeceraInactivo(@Param("estadoId") String estadoId);

	// VERIFICAR SI EXISTE FECHA_HORA DEL SOBRETURNO EN PLANILLA DETALLE o
	// SOBRETURNOS.
	@Query(value = "SELECT rango_fecha_hora as 'RANGO DUPLICADO', COUNT(*) as 'CANT' " + " FROM ( "
			+ " select pd.rango_fecha_hora from planilladetalle pd "
			+ "	inner join planillacabecera pc on pc.id = pd.planillacabecera_id "
			+ "	inner join estado on estado.id = pd.estado_id "
			+ "	where pc.id_profesional = :profId and pc.id_especialidad = :EspecId " + "	and pc.id_estado = 2 "
			+ "	and pd.estado_id = 3 " + " UNION ALL " + " SELECT s.rango_fecha_hora FROM sobreturnos s "
			+ " where s.profesional_id = 1 and s.especialidad_id = 1 "
			+ " and DATE_FORMAT(s.rango_fecha_hora,'%Y-%m-%d %H:%i') >= DATE_FORMAT(NOW(3), '%Y-%m-%d %H:%i') "
			+ ") AS tabla_unida "
			+ "WHERE DATE_FORMAT(rango_fecha_hora,'%Y-%m-%d %H:%i') = STR_TO_DATE(:fechaHora, '%Y-%m-%d %H:%i') "
			+ "GROUP BY rango_fecha_hora " + "HAVING COUNT(*) > 0", nativeQuery = true)
	public List<String> existeSobreturnoEnPlanillaDetalle(Long profId, Long EspecId, String fechaHora);

	// buscar turnos libres fechas.
	@Query("select pd from PlanillaDetalle pd " + "inner join PlanillaCabecera pc on pc.id = pd.planillacabecera.id "
			+ "inner join Estado e on e.id = pd.estado.id "
			+ "where pc.profesional.id = :profId and pc.especialidad.id = :EspecId " + "and pc.estado.id = 2 "
			+ "and pd.estado.id = 3 " + "and DATE_FORMAT(pd.rangoFechaHora,'%Y-%m-%d %H:%i') >= CURRENT_TIMESTAMP "
			+ "and DATE_FORMAT(pd.rangoFechaHora,'%Y-%m-%d') between DATE_FORMAT(:fechaIni, '%Y-%m-%d') and DATE_FORMAT(:fechaFin, '%Y-%m-%d') "
			+ "order by pd.rangoFechaHora asc")
	public List<PlanillaDetalle> buscarTurnosLibresFechas(Long profId, Long EspecId, Date fechaIni, Date fechaFin);

	// buscar turnos libres fechas horas.
	@Query("select pd from PlanillaDetalle pd " + "inner join PlanillaCabecera pc on pc.id = pd.planillacabecera.id "
			+ "inner join Estado e on e.id = pd.estado.id "
			+ "where pc.profesional.id = :profId and pc.especialidad.id = :EspecId " + "and pc.estado.id = 2 "
			+ "and pd.estado.id = 3 " + "and DATE_FORMAT(pd.rangoFechaHora,'%Y-%m-%d %H:%i') >= CURRENT_TIMESTAMP "
			+ "and DATE_FORMAT(pd.rangoFechaHora,'%Y-%m-%d %H:%i') between DATE_FORMAT(:fechaHoraIni, '%Y-%m-%d %H:%i') and DATE_FORMAT(:fechaHoraFin, '%Y-%m-%d %H:%i') "
			+ "order by pd.rangoFechaHora asc")
	public List<PlanillaDetalle> buscarTurnosLibresFechasHoras(Long profId, Long EspecId, Date fechaHoraIni,
			Date fechaHoraFin);

	// Actualizamos en Planilladetalle el estado_id=6 (CANCELADO). (vista: Cancelar
	// Planillas) Turnos Libres
	@Modifying
	@Transactional
	@Query(value = "update planilladetalle pd " + "inner join planillacabecera pc on pc.id = pd.planillacabecera_id "
			+ "set pd.estado_id = :estadoId, pd.observacion = :motivo "
			+ "where pc.id_profesional = :profId and pc.id_especialidad = :EspecId and pd.estado_id = 3 "
			+ "and DATE_FORMAT(pd.rango_fecha_hora,'%Y-%m-%d') >= DATE_FORMAT(NOW(3), '%Y-%m-%d') "
			+ "and DATE_FORMAT(pd.rango_fecha_hora,'%Y-%m-%d') between DATE_FORMAT(:fechaIni, '%Y-%m-%d') and DATE_FORMAT(:fechaFin, '%Y-%m-%d')", nativeQuery = true)
	public void actualizarPlanillaDetalleCanceladoFechasTL(@Param("profId") Long profId, @Param("EspecId") Long EspecId,
			@Param("fechaIni") Date fechaIni, @Param("fechaFin") Date fechaFin, @Param("estadoId") String estadoId,
			@Param("motivo") String motivo);

	// Actualizamos en Planilladetalle el estado_id=6 (CANCELADO). (vista: Cancelar
	// Planillas) Turnos Libres
	@Modifying
	@Transactional
	@Query(value = "update planilladetalle pd " + "inner join planillacabecera pc on pc.id = pd.planillacabecera_id "
			+ "set pd.estado_id = :estadoId, pd.observacion = :motivo "
			+ "where pc.id_profesional = :profId and pc.id_especialidad = :EspecId and pd.estado_id = 3 "
			+ "and DATE_FORMAT(pd.rango_fecha_hora,'%Y-%m-%d %H:%i') >= DATE_FORMAT(NOW(3), '%Y-%m-%d %H:%i') "
			+ "and DATE_FORMAT(pd.rango_fecha_hora,'%Y-%m-%d %H:%i') between DATE_FORMAT(:fechaHoraIni, '%Y-%m-%d %H:%i') and DATE_FORMAT(:fechaHoraFin, '%Y-%m-%d %H:%i')", nativeQuery = true)
	public void actualizarPlanillaDetalleCanceladoFechasHorasTL(@Param("profId") Long profId,
			@Param("EspecId") Long EspecId, @Param("fechaHoraIni") Date fechaHoraIni,
			@Param("fechaHoraFin") Date fechaHoraFin, @Param("estadoId") String estadoId,
			@Param("motivo") String motivo);

	// Actualizamos en Planilladetalle el estado_id=6 (CANCELADO). (vista: Cancelar
	// Planillas) Turnos Ocupados
	@Modifying
	@Transactional
	@Query(value = "update planilladetalle pd " + "inner join planillacabecera pc on pc.id = pd.planillacabecera_id "
			+ "set pd.estado_id = :estadoId, pd.observacion = :motivo "
			+ "where pc.id_profesional = :profId and pc.id_especialidad = :EspecId and pd.estado_id = 4 "
			+ "and DATE_FORMAT(pd.rango_fecha_hora,'%Y-%m-%d') >= DATE_FORMAT(NOW(3), '%Y-%m-%d') "
			+ "and DATE_FORMAT(pd.rango_fecha_hora,'%Y-%m-%d') between DATE_FORMAT(:fechaIni, '%Y-%m-%d') and DATE_FORMAT(:fechaFin, '%Y-%m-%d') ", nativeQuery = true)
	public void actualizarPlanillaDetalleCanceladoFechasTO(@Param("profId") Long profId, @Param("EspecId") Long EspecId,
			@Param("fechaIni") Date fechaIni, @Param("fechaFin") Date fechaFin, @Param("estadoId") String estadoId,
			@Param("motivo") String motivo);

	// Actualizamos en Planilladetalle el estado_id=6 (CANCELADO). (vista: Cancelar
	// Planillas) Turnos Ocupados
	@Modifying
	@Transactional
	@Query(value = "update planilladetalle pd " + "inner join planillacabecera pc on pc.id = pd.planillacabecera_id "
			+ "set pd.estado_id = :estadoId, pd.observacion = :motivo "
			+ "where pc.id_profesional = :profId and pc.id_especialidad = :EspecId and pd.estado_id = 4 "
			+ "and DATE_FORMAT(pd.rango_fecha_hora,'%Y-%m-%d %H:%i') >= DATE_FORMAT(NOW(3), '%Y-%m-%d %H:%i') "
			+ "and DATE_FORMAT(pd.rango_fecha_hora,'%Y-%m-%d %H:%i') between DATE_FORMAT(:fechaHoraIni, '%Y-%m-%d %H:%i') and DATE_FORMAT(:fechaHoraFin, '%Y-%m-%d %H:%i') ", nativeQuery = true)
	public void actualizarPlanillaDetalleCanceladoFechasHorasTO(@Param("profId") Long profId,
			@Param("EspecId") Long EspecId, @Param("fechaHoraIni") Date fechaHoraIni, @Param("fechaHoraFin") Date fechaHoraFin,
			@Param("estadoId") String estadoId, @Param("motivo") String motivo);
}

package com.sigat.springboot.app.models.dao;


import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import com.sigat.springboot.app.models.entity.Sobreturno;
import com.sigat.springboot.app.models.entity.Turno;

public interface ITurnosDao extends JpaRepository<Turno, Long>, CrudRepository<Turno, Long> {

	// funcion llamada desde TurnoServiceImpl. para los Mails.
	@Query("SELECT t FROM Turno t " + "JOIN FETCH t.paciente " + "JOIN FETCH t.profesional "
			+ "JOIN FETCH t.planilladetalle " + "JOIN FETCH t.especialidad " + "WHERE t.id = :id")
	public Turno obtenerTurnoCompletoMail(@Param("id") Long id);

	// Listar Planillas activas por profesional y especialidad que sean >= a la
	// fecha actual del sistema.
	@Query(value = "select pd.id, pd.rango_fecha_hora from planilladetalle pd "
			+ "inner join planillacabecera pc on pc.id=pd.planillacabecera_id " + "where pc.id_profesional = ?1 "
			+ "and pc.id_especialidad = ?2 " + "and pc.id_estado IN (2, 6) " + "and pd.estado_id = 3 "
			+ "and DATE_FORMAT(pd.rango_fecha_hora,'%Y-%m-%d %H:%i') >= DATE_FORMAT(NOW(3), '%Y-%m-%d %H:%i')"
			+ "order by pd.rango_fecha_hora asc", nativeQuery = true)
	public String[] buscarPlanillaActiva(long profId, long EspecId);

	// Muestra los Turnos Ocupados por idProfesional y id_Especialidad
	@Query("select t from Turno t " + "inner join PlanillaCabecera pc on pc.id = t.planillacabecera.id "
			+ "inner join PlanillaDetalle pd on pd.id = t.planilladetalle.id "
			+ "inner join Profesional prof on prof.id = t.profesional.id "
			+ "inner join Especialidad esp on esp.id = t.especialidad.id " + "where pc.especialidad.id = ?2 "
			+ "and pc.profesional.id = ?1 " + "and pd.estado.id = 4 "
			+ "and DATE_FORMAT(pd.rangoFechaHora,'%Y-%m-%d %H:%i') >= CURRENT_TIMESTAMP "
			+ "order by pd.rangoFechaHora asc")
	public List<Turno> findTurnosOcupadosByProfIdyEspId(Long profId, Long EspecId);

	// Muestra los Turnos Ocupados por idProfesional, id_Especialidad y paciente_id.
	@Query("select t from Turno t " + "inner join PlanillaCabecera pc on pc.id = t.planillacabecera.id "
			+ "inner join PlanillaDetalle pd on pd.id = t.planilladetalle.id "
			+ "inner join Profesional prof on prof.id = t.profesional.id "
			+ "inner join Especialidad esp on esp.id = t.especialidad.id " + "where t.id = ?1 ")
	public List<Turno> findTurnoClickById(Long turnoId);

	// buscar paciente en Turnos Activos antes de eliminar.
	@Query("select t from Turno t " + "inner join Paciente p on p.id = t.paciente.id "
			+ "inner join PlanillaDetalle pd on pd.id = t.planilladetalle.id "
			+ "inner join Estado est on est.id = pd.estado.id " + "where t.paciente.id = :pacId "
			+ "and pd.estado.id = 4 " + "and DATE_FORMAT(pd.rangoFechaHora,'%Y-%m-%d %H:%i') >= CURRENT_TIMESTAMP "
			+ "order by pd.rangoFechaHora asc")
	public List<Turno> pacientePoseeTurnosActivos(Long pacId);

	// buscar Turnos Ocupados fechas. (vista: Cancelar Planillas)
	@Query("select t from Turno t " + "inner join PlanillaCabecera pc on pc.id = t.planillacabecera.id "
			+ "inner join PlanillaDetalle pd on pd.id = t.planilladetalle.id "
			+ "inner join Profesional prof on prof.id = t.profesional.id "
			+ "inner join Especialidad esp on esp.id = t.especialidad.id " + "where pc.profesional.id = :profId "
			+ "and pc.especialidad.id = :EspecId " + "and pd.estado.id = 4 "
			+ "and DATE_FORMAT(pd.rangoFechaHora,'%Y-%m-%d %H:%i') >= CURRENT_TIMESTAMP "
			+ "and DATE_FORMAT(pd.rangoFechaHora,'%Y-%m-%d') between "
			+ "DATE_FORMAT(:fechaIni,'%Y-%m-%d') AND DATE_FORMAT(:fechaFin,'%Y-%m-%d') "
			+ "order by pd.rangoFechaHora asc")
	public List<Turno> buscarTurnosOcupadosFechas(long profId, long EspecId, Date fechaIni, Date fechaFin);

	// buscar Turnos Ocupados fechas Horas. (vista: Cancelar Planillas)
	@Query("select t from Turno t " + "inner join PlanillaCabecera pc on pc.id = t.planillacabecera.id "
			+ "inner join PlanillaDetalle pd on pd.id = t.planilladetalle.id "
			+ "inner join Profesional prof on prof.id = t.profesional.id "
			+ "inner join Especialidad esp on esp.id = t.especialidad.id " + "where pc.profesional.id = :profId "
			+ "and pc.especialidad.id = :EspecId " + "and pd.estado.id = 4 "
			+ "and DATE_FORMAT(pd.rangoFechaHora,'%Y-%m-%d %H:%i') >= CURRENT_TIMESTAMP "
			+ "and DATE_FORMAT(pd.rangoFechaHora,'%Y-%m-%d %H:%i') between "
			+ "DATE_FORMAT(:fechaHoraIni,'%Y-%m-%d %H:%i') AND DATE_FORMAT(:fechaHoraFin,'%Y-%m-%d %H:%i') "
			+ "order by pd.rangoFechaHora asc")
	public List<Turno> buscarTurnosOcupadosFechasHoras(long profId, long EspecId, Date fechaHoraIni, Date fechaHoraFin);

	// DESACTIVAMOS EL MODO SEGURO DE MYSQL.
	@Modifying
	@Transactional
	@Query(value = "SET SQL_SAFE_UPDATES = 0", nativeQuery = true)
	public void desactivarSafeUpdates();

	@Modifying
	@Transactional
	@Query(value = "UPDATE turnos t " + "INNER JOIN planilladetalle pd ON t.planilladetalle_id = pd.id "
			+ "INNER JOIN planillacabecera pc ON pd.planillacabecera_id = pc.id " + "SET t.estado_id = 6 "
			+ "WHERE pc.id_profesional = :profId AND pc.id_especialidad = :especId "
			+ "AND DATE(pd.rango_fecha_hora) BETWEEN DATE(:fechaIni) AND DATE(:fechaFin) "
			+ "AND pc.id_estado = 2", nativeQuery = true)
	void eliminarTurnosOcupadosFechas(@Param("profId") Long profId, @Param("especId") Long especId,
			@Param("fechaIni") LocalDateTime fechaIni, @Param("fechaFin") LocalDateTime fechaFin);

	@Modifying
	@Transactional
	@Query(value = "UPDATE turnos t " + "INNER JOIN planilladetalle pd ON t.planilladetalle_id = pd.id "
			+ "SET t.estado_id = 6 " + "WHERE t.profesional_id = :profId AND t.especialidad_id = :especId "
			+ "AND pd.rango_fecha_hora BETWEEN :inicio AND :fin", nativeQuery = true)
	void eliminarTurnosOcupadosFechasHoras(@Param("profId") Long profId, @Param("especId") Long especId,
			@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

	// CANCELACION MASIVA DE TURNOS (Estado CANCELADO - 6)
	@Modifying
	@Transactional
	@Query("UPDATE Turno t SET t.estado.id = 6 WHERE t.id IN :ids")
	void cancelarVariosTurnosPorId(@Param("ids") List<Long> ids);

	// CANCELACION INDIVIDUAL DE TURNOS (Estado CANCELADO - 6)
	@Modifying
	@Transactional
	@Query("UPDATE Turno t SET t.estado.id = 6 WHERE t.id = :id")
	void cancelarTurnoIndividual(@Param("id") Long id);

	// LISTAR TURNOS SOLO CON ID=4 ( tambien para Historia Clinica)
	
	  @Query("select t from Turno t " +
		       "inner join PlanillaDetalle pd on pd.id = t.planilladetalle.id " +
		       "where t.estado.id in (4, 9)") // <--- Agregamos el 9 aquí 
	  public List<Turno> findAllOcupados();
	 
	
	// LISTAR TURNOS SOLO CON ID=4 y ID=9 TRAENDO SU DETALLE ASOCIADO
	/*
	 * @Query("select t from Turno t join fetch t.planilladetalle pd where t.estado.id in (4, 9)"
	 * ) public List<Turno> findAllOcupados();
	 */

	// NUEVO PROCEDIMIENTO PARA CANCELACION MASIVA DE PLANILLAS*****************
	@Modifying
	@Transactional
	@Query(value = "UPDATE turnos t SET t.estado_id = 6 WHERE t.profesional_id = :profId "
			+ "AND t.especialidad_id = :especId " + "AND t.id IN (SELECT id FROM (SELECT t2.id FROM turnos t2 "
			+ "INNER JOIN planilladetalle pd ON t2.planilladetalle_id = pd.id "
			+ "WHERE pd.rango_fecha_hora BETWEEN :inicio AND :fin) tmp)", nativeQuery = true)
	void cancelarTurnosPorBajaMedica(@Param("profId") Long profId, @Param("especId") Long especId,
			@Param("inicio") Date inicio, @Param("fin") Date fin);

	// Búsqueda por FECHA (String)
	@Query(value = "SELECT t.* FROM turnos t " + "INNER JOIN planilladetalle pd ON pd.id = t.planilladetalle_id "
			+ "WHERE t.profesional_id = :profId AND t.especialidad_id = :especId " + "AND pd.estado_id = 4 "
			+ "AND DATE_FORMAT(pd.rango_fecha_hora, '%Y-%m-%d') BETWEEN :inicio AND :fin", nativeQuery = true)
	List<Turno> buscarTurnosOcupadosParaBajaSoloFecha(@Param("profId") Long profId, @Param("especId") Long especId,
			@Param("inicio") String inicio, @Param("fin") String fin);

	// Búsqueda por FECHA Y HORA (LocalDateTime)
	@Query(value = "SELECT t.* FROM turnos t " + "INNER JOIN planilladetalle pd ON pd.id = t.planilladetalle_id "
			+ "WHERE t.profesional_id = :profId AND t.especialidad_id = :especId " + "AND pd.estado_id = 4 "
			+ "AND pd.rango_fecha_hora BETWEEN :inicio AND :fin", nativeQuery = true)
	List<Turno> buscarTurnosOcupadosParaBajaFechaHora(@Param("profId") Long profId, @Param("especId") Long especId,
			@Param("inicio") java.time.LocalDateTime inicio, @Param("fin") java.time.LocalDateTime fin);

	// SECCION RESTAURAR (AUDITORIA)
	@Query("select t from Turno t where t.estado.id = 6 " +
		       "and (:profId is null or t.profesional.id = :profId) " +
		       "and (:especId is null or t.especialidad.id = :especId) " +
		       "and (cast(:desde as date) is null or date(t.planilladetalle.rangoFechaHora) >= :desde) " +
		       "and (cast(:hasta as date) is null or date(t.planilladetalle.rangoFechaHora) <= :hasta)")
		public List<Turno> findCanceladosConFiltro(
		    @Param("profId") Long profId, 
		    @Param("especId") Long especId, 
		    @Param("desde") Date desde, 
		    @Param("hasta") Date hasta);
	
	@Query("select t from Turno t where t.planilladetalle.id = :id")
	Turno findByPlanilladetalleId(@Param("id") Long id);
	
	@Query("select t from Turno t where t.estado.id = 6L")
	public List<Turno> listarMonitor();

	// SECCION PARA LOS REPORTES------------------------------------------------

	// reporte Turnos Ocupados por idProfesional e id_Especialidad y fechas.
	@Query("select t from Turno t " + "inner join PlanillaCabecera pc on pc.id = t.planillacabecera.id "
			+ "inner join PlanillaDetalle pd on pd.id = t.planilladetalle.id "
			+ "inner join Profesional prof on prof.id = t.profesional.id "
			+ "inner join Especialidad esp on esp.id = t.especialidad.id " + "where pc.profesional.id = ?1 "
			+ "and pc.especialidad.id = ?2 " + "and pd.estado.id = 4 "
			+ "and DATE_FORMAT(pd.rangoFechaHora,'%Y-%m-%d') between "
			+ "DATE_FORMAT(?3,'%Y-%m-%d') AND DATE_FORMAT(?4,'%Y-%m-%d') " + "order by pd.rangoFechaHora asc")
	public List<Turno> reporteTurnosOcupadosFechas(long profId, long EspecId, Date fechaIni, Date fechaFin);

	// reporte Sobreturnos Ocupados por idProfesional e id_Especialidad y fechas.
	@Query("select s from Sobreturno s "
			// + "inner join PlanillaCabecera pc on pc.id = t.planillacabecera.id "
			// + "inner join PlanillaDetalle pd on pd.id = t.planilladetalle.id "
			+ "inner join Paciente pac on pac.id = s.profesional.id "
			+ "inner join Profesional prof on prof.id = s.profesional.id "
			+ "inner join Especialidad esp on esp.id = s.especialidad.id " + "where s.profesional.id = :profId "
			+ "and s.especialidad.id = :EspecId "
			// + "and pd.estado.id = 4 "
			+ "and DATE_FORMAT(s.rangoFechaHora,'%Y-%m-%d') between "
			+ "DATE_FORMAT(:fechaIni,'%Y-%m-%d') AND DATE_FORMAT(:fechaFin,'%Y-%m-%d') "
			+ "order by s.rangoFechaHora asc")
	public List<Sobreturno> reporteSobreTurnosOcupadosFechas(long profId, long EspecId, Date fechaIni, Date fechaFin);

	// SECCION REPORTES PARA PDF

	// reporte Pdf Turnos Ocupados por idProfesional e id_Especialidad y fechas.
	@Query("select t from Turno t " + "inner join PlanillaCabecera pc on pc.id = t.planillacabecera.id "
			+ "inner join PlanillaDetalle pd on pd.id = t.planilladetalle.id "
			+ "inner join Profesional prof on prof.id = t.profesional.id "
			+ "inner join Especialidad esp on esp.id = t.especialidad.id "
			+ "inner join Paciente pac on pac.id = t.paciente.id " + "where pc.profesional.id = :profId "
			+ "and pc.especialidad.id = :EspecId " + "and pd.estado.id = 4 "
			+ "and DATE_FORMAT(pd.rangoFechaHora,'%Y-%m-%d') between "
			+ "DATE_FORMAT(:fechaIni,'%Y-%m-%d') AND DATE_FORMAT(:fechaFin,'%Y-%m-%d') "
			+ "order by pd.rangoFechaHora asc")
	public List<Turno> reporteTurnosOcupadosFechasPdf(long profId, long EspecId, String fechaIni, String fechaFin);

}

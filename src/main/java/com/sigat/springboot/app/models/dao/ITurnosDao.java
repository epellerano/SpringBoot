package com.sigat.springboot.app.models.dao;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.sigat.springboot.app.models.entity.PlanillaCabecera;
import com.sigat.springboot.app.models.entity.PlanillaDetalle;
import com.sigat.springboot.app.models.entity.Sobreturno;
import com.sigat.springboot.app.models.entity.Turno;

import jakarta.transaction.Transactional;

public interface ITurnosDao extends JpaRepository<Turno, Long>, CrudRepository<Turno, Long> {

	//funcion llamada desde TurnoServiceImpl. para los Mails.
	@Query("SELECT t FROM Turno t " +
	           "JOIN FETCH t.paciente " +
	           "JOIN FETCH t.profesional " +
	           "JOIN FETCH t.planilladetalle " +
	           "JOIN FETCH t.especialidad " +
	           "WHERE t.id = :id")
	    public Turno obtenerTurnoCompletoMail(@Param("id") Long id);
	
	
	// Listar Planillas activas por profesional y especialidad que sean >= a la
	// fecha actual del sistema.
	@Query(value = "select pd.id, pd.rango_fecha_hora from planilladetalle pd "
			+ "inner join planillacabecera pc on pc.id=pd.planillacabecera_id " + "where pc.id_profesional = ?1 "
			+ "and pc.id_especialidad = ?2 " + "and pc.id_estado = 2 " + "and pd.estado_id = 3 "
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

	// Eliminar "Turnos Ocupados" entre fechas (vista: Cancelar Planillas)
	@Modifying
	@Transactional
	@Query(value = "delete t from turnos t " + "inner join planilladetalle pd on t.planilladetalle_id = pd.id "
			+ "inner join planillacabecera pc on pd.planillacabecera_id = pc.id "
			+ "where pc.id_profesional = :profId and pc.id_especialidad = :EspecId "
			+ "and DATE_FORMAT(pd.rango_fecha_hora,'%Y-%m-%d') >= DATE_FORMAT(NOW(3), '%Y-%m-%d') "
			+ "and DATE_FORMAT(pd.rango_fecha_hora,'%Y-%m-%d') between DATE_FORMAT(:fechaIni, '%Y-%m-%d') and DATE_FORMAT(:fechaFin, '%Y-%m-%d') "
			+ "and pc.id_estado = 2", nativeQuery = true)
	public void eliminarTurnosOcupadosFechas(@Param("profId") Long profId, @Param("EspecId") Long EspecId,
			@Param("fechaIni") Date fechaIni, @Param("fechaFin") Date fechaFin);
	
	
	
	
	// Eliminar "Turnos Ocupados" entre fechas (vista: Cancelar Planillas)
		@Modifying
		@Transactional
		@Query(value = "delete t from turnos t " + "inner join planilladetalle pd on t.planilladetalle_id = pd.id "
				+ "inner join planillacabecera pc on pd.planillacabecera_id = pc.id "
				+ "where pc.id_profesional = :profId and pc.id_especialidad = :EspecId "
				+ "and DATE_FORMAT(pd.rango_fecha_hora,'%Y-%m-%d %H:%i') >= DATE_FORMAT(NOW(3), '%Y-%m-%d %H:%i') "
				+ "and DATE_FORMAT(pd.rango_fecha_hora,'%Y-%m-%d %H:%i') between DATE_FORMAT(:fechaHoraIni, '%Y-%m-%d %H:%i') and DATE_FORMAT(:fechaHoraFin, '%Y-%m-%d %H:%i') "
				+ "and pc.id_estado = 2", nativeQuery = true)
		public void eliminarTurnosOcupadosFechasHoras(@Param("profId") Long profId, @Param("EspecId") Long EspecId,
				@Param("fechaHoraIni") Date fechaHoraIni, @Param("fechaHoraFin") Date fechaHoraFin);
	

	// SECCION PARA LOS REPORTES
	// ---------------------------------------------------------

	// reporte Turnos Ocupados por idProfesional e id_Especialidad y fechas.
	/*
	 * @Query(
	 * value="select t.fecha_creacion, CONCAT(p.apellido,' ', p.nombre), e.especialidad_nombre, "
	 * +
	 * "CONCAT(pac.apellido,' ', pac.nombre), pac.dni, DATE_FORMAT(pd.rango_fecha_hora, '%d/%m/%Y %H:%i') from turnos t "
	 * + "inner join profesionales p on p.id = t.profesional_id " +
	 * "inner join especialidades e on e.id = t.especialidad_id " +
	 * "inner join pacientes pac on pac.id = t.paciente_id " +
	 * "inner join planilladetalle pd on pd.id = t.planilladetalle_id " +
	 * "where t.profesional_id = :profId " + "and t.especialidad_id = :EspecId " +
	 * "and pd.estado_id = 4 " +
	 * "and DATE_FORMAT(pd.rango_fecha_hora,'%Y-%m-%d %H:%i') between " +
	 * "STR_TO_DATE(:fechaIni, '%Y-%m-%d') and STR_TO_DATE(:fechaFin, '%Y-%m-%d')" +
	 * "order by pd.rango_fecha_hora asc", nativeQuery=true) public List<Turno>
	 * reporteTurnosOcupadosFechas(long profId, long EspecId, String fechaIni,
	 * String fechaFin);
	 */

	// reporte Turnos Ocupados por idProfesional e id_Especialidad y fechas.
	@Query("select t from Turno t " + "inner join PlanillaCabecera pc on pc.id = t.planillacabecera.id "
			+ "inner join PlanillaDetalle pd on pd.id = t.planilladetalle.id "
			+ "inner join Profesional prof on prof.id = t.profesional.id "
			+ "inner join Especialidad esp on esp.id = t.especialidad.id " + "where pc.profesional.id = :profId "
			+ "and pc.especialidad.id = :EspecId " + "and pd.estado.id = 4 "
			+ "and DATE_FORMAT(pd.rangoFechaHora,'%Y-%m-%d') between "
			+ "DATE_FORMAT(:fechaIni,'%Y-%m-%d') AND DATE_FORMAT(:fechaFin,'%Y-%m-%d') "
			+ "order by pd.rangoFechaHora asc")
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

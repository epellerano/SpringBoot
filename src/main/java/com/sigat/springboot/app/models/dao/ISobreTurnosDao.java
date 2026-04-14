package com.sigat.springboot.app.models.dao;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.sigat.springboot.app.models.entity.Paciente;
import com.sigat.springboot.app.models.entity.Sobreturno;
import com.sigat.springboot.app.models.entity.Turno;

import jakarta.transaction.Transactional;

public interface ISobreTurnosDao extends JpaRepository<Sobreturno, Long>, CrudRepository<Sobreturno, Long> {
	
	// Spring genera la lógica solo con este nombre:
	//no necesita los Services.
    boolean existsByProfesionalIdAndEspecialidadIdAndRangoFechaHora(
        Long profesionalId, 
        Long especialidadId, 
        LocalDateTime rangoFechaHora
    );
	
	//funcion llamada desde SobreTurnoServiceImpl. para los Mails.
		@Query("SELECT s FROM Sobreturno s " +
		           "JOIN FETCH s.paciente " +
		           "JOIN FETCH s.profesional " +
		           "JOIN FETCH s.especialidad " +
		           "WHERE s.id = :id")
		    public Sobreturno obtenerSobreturnoCompletoMail(@Param("id") Long id);

	// Listar Planillas activas por profesional y especialidad que sean >= a la
	// fecha actual del sistema.

	@Query(value = "select pd.id, pd.rango_fecha_hora from planilladetalle pd "
			+ "inner join planillacabecera pc on pc.id=pd.planillacabecera_id " + "where pc.id_profesional = ?1 "
			+ "and pc.id_especialidad = ?2 " + "and pc.id_estado = 2 " + "and pd.estado_id = 3 "
			+ "and DATE_FORMAT(pd.rango_fecha_hora,'%Y-%m-%d %H:%i') >= DATE_FORMAT(NOW(3), '%Y-%m-%d %H:%i')"
			+ "order by pd.rango_fecha_hora asc", nativeQuery = true)
	public String[] buscarPlanillaActiva(long profId, long EspecId);

	// Muestra los Sobreturnos Ocupados por idProfesional y id_Especialidad
	@Query("select s from Sobreturno s " + "inner join Profesional prof on prof.id = s.profesional.id "
			+ "inner join Especialidad esp on esp.id = s.especialidad.id "
			+ "inner join Paciente pac on pac.id = s.paciente.id " + "where s.especialidad.id = ?2 "
			+ "and s.profesional.id = ?1 "
			// + "and pd.estado.id = 4 "
			+ "and s.rangoFechaHora >= NOW() " + "order by s.rangoFechaHora asc")
	public List<Sobreturno> findSobreTurnosOcupadosByProfIdyEspId(Long profId, Long EspecId);

	// Muestra los SobreTurnos Ocupados por el Profesional, Especialidad y paciente
	// por sobreturno_id.
	// alhacer click en la lupa.
	@Query("select s from Sobreturno s " +
	// "inner join PlanillaCabecera pc on pc.id = s.planillacabecera.id " +
	// "inner join PlanillaDetalle pd on pd.id = s.planilladetalle.id " +
			"inner join Profesional prof on prof.id = s.profesional.id "
			+ "inner join Especialidad esp on esp.id = s.especialidad.id "
			+ "inner join Paciente pac on pac.id = s.paciente.id " + "where s.id = ?1 ")
	public List<Sobreturno> findSobreTurnoClickById(Long sobreturnoId);

	// buscar paciente en Sobreturnos Activos antes de eliminar.
	@Query("select s from Sobreturno s " + "inner join Paciente p on p.id = s.paciente.id "
			+ "where s.paciente.id = :pacId "
			+ "and DATE_FORMAT(s.rangoFechaHora,'%Y-%m-%d %H:%i') >= CURRENT_TIMESTAMP "
			+ "order by s.rangoFechaHora asc")
	public List<Sobreturno> pacientePoseeSobreturnosActivos(Long pacId);

	// buscar Sobreturnos Ocupados fechas. (vista: Cancelar Planillas)
	@Query("select s from Sobreturno s " + "inner join Profesional prof on prof.id = s.profesional.id "
			+ "inner join Especialidad esp on esp.id = s.especialidad.id "
			+ "inner join Paciente pac on pac.id = s.paciente.id " + "where s.profesional.id = :profId "
			+ "and s.especialidad.id = :EspecId " + "and DATE_FORMAT(s.rangoFechaHora,'%Y-%m-%d') between "
			+ "DATE_FORMAT(:fechaIni,'%Y-%m-%d') AND DATE_FORMAT(:fechaFin,'%Y-%m-%d') "
			+ "order by s.rangoFechaHora asc")
	public List<Sobreturno> buscarSobreturnosOcupadosFechas(long profId, long EspecId, Date fechaIni, Date fechaFin);

	// buscar Sobreturnos Ocupados fechas y horas. (vista: Cancelar Planillas)
	@Query("select s from Sobreturno s " + "inner join Profesional prof on prof.id = s.profesional.id "
			+ "inner join Especialidad esp on esp.id = s.especialidad.id "
			+ "inner join Paciente pac on pac.id = s.paciente.id " + "where s.profesional.id = :profId "
			+ "and s.especialidad.id = :EspecId " + "and DATE_FORMAT(s.rangoFechaHora,'%Y-%m-%d %H:%i') between "
			+ "DATE_FORMAT(:fechaHoraIni,'%Y-%m-%d %H:%i') AND DATE_FORMAT(:fechaHoraFin,'%Y-%m-%d %H:%i') "
			+ "order by s.rangoFechaHora asc")
	public List<Sobreturno> buscarSobreturnosOcupadosFechasHoras(long profId, long EspecId, Date fechaHoraIni,
			Date fechaHoraFin);

	// Eliminar "Sobreturnos Ocupados" entre fechas (vista: Cancelar Planillas)
	@Modifying
	@Transactional
	@Query(value = "delete s from sobreturnos s " 
			+ "where s.profesional_id = :profId and s.especialidad_id = :EspecId "
			+ "and DATE_FORMAT(s.rango_fecha_hora,'%Y-%m-%d') between DATE_FORMAT(:fechaIni, '%Y-%m-%d') "
			+ "and DATE_FORMAT(:fechaFin, '%Y-%m-%d')", nativeQuery = true)
	public void eliminarSobreturnosOcupadosFechas(@Param("profId") Long profId, @Param("EspecId") Long EspecId,
			@Param("fechaIni") Date fechaIni, @Param("fechaFin") Date fechaFin);
	
	// Eliminar "Sobreturnos Ocupados" entre fechas y horas(vista: Cancelar Planillas)
		@Modifying
		@Transactional
		@Query(value = "delete s from sobreturnos s " 
				+ "where s.profesional_id = :profId and s.especialidad_id = :EspecId "
				+ "and DATE_FORMAT(s.rango_fecha_hora,'%Y-%m-%d %H:%i') between DATE_FORMAT(:fechaHoraIni, '%Y-%m-%d %H:%i') "
				+ "and DATE_FORMAT(:fechaHoraFin, '%Y-%m-%d %H:%i')", nativeQuery = true)
		public void eliminarSobreturnosOcupadosFechasHoras(@Param("profId") Long profId, @Param("EspecId") Long EspecId,
				@Param("fechaHoraIni") Date fechaHoraIni, @Param("fechaHoraFin") Date fechaHoraFin);

	// SECCION REPORTES PARA PDF

	// reporte Pdf Sobreturnos Ocupados por idProfesional e id_Especialidad y
	// fechas.
	@Query("select s from Sobreturno s " + "inner join Profesional prof on prof.id = s.profesional.id "
			+ "inner join Especialidad esp on esp.id = s.especialidad.id "
			+ "inner join Paciente pac on pac.id = s.paciente.id " + "where s.profesional.id = :profId "
			+ "and s.especialidad.id = :EspecId " + "and DATE_FORMAT(s.rangoFechaHora,'%Y-%m-%d') between "
			+ "DATE_FORMAT(:fechaIni,'%Y-%m-%d') AND DATE_FORMAT(:fechaFin,'%Y-%m-%d') "
			+ "order by s.rangoFechaHora asc")
	public List<Sobreturno> reporteSobreturnosOcupadosFechasPdf(long profId, long EspecId, String fechaIni,
			String fechaFin);

}

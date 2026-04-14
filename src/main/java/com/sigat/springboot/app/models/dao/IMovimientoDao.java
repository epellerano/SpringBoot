package com.sigat.springboot.app.models.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import com.sigat.springboot.app.models.entity.Movimiento;
import com.sigat.springboot.app.models.entity.PlanillaCabecera;

public interface IMovimientoDao extends JpaRepository<Movimiento, Long>, CrudRepository<Movimiento, Long>,
		PagingAndSortingRepository<Movimiento, Long> {

	// buscar vinculacion en Movimientos antes de eliminar.
	@Query("select m from Movimiento m " + "inner join Profesional p on p.id = m.profesional.id "
			+ "inner join Especialidad e on e.id = m.especialidad.id "
			+ "where m.profesional.id = :profId and m.especialidad.id = :EspecId")
	public List<Movimiento> ExisteVinculacionEnMovimientos(long profId, long EspecId);

	// SECCION REPORTES

	// reporte Movimientos por idProfesional e id_Especialidad y fechas.
	@Query("select m from Movimiento m " 
			+ "inner join Profesional prof on prof.id = m.profesional.id "
			+ "inner join Especialidad esp on esp.id = m.especialidad.id " 
			+ "where m.profesional.id = :profId "
			+ "and m.especialidad.id = :EspecId " 
			+ "and DATE_FORMAT(m.createAt,'%Y-%m-%d') between "
			+ "DATE_FORMAT(:fechaIni,'%Y-%m-%d') AND DATE_FORMAT(:fechaFin,'%Y-%m-%d') " 
			+ "order by m.createAt asc")
	public List<Movimiento> reporteMovimientosFechas(long profId, long EspecId, Date fechaIni, Date fechaFin);

	// SECCION REPORTES PARA PDF

	// reporte Pdf Planillas Activas por idProfesional e id_Especialidad y fechas.
	@Query("select m from Movimiento m " 
			+ "inner join Profesional prof on prof.id = m.profesional.id "
			+ "inner join Especialidad esp on esp.id = m.especialidad.id " 
			+ "where m.profesional.id = :profId "
			+ "and m.especialidad.id = :EspecId " 
			+ "and DATE_FORMAT(m.createAt,'%Y-%m-%d') between "
			+ "DATE_FORMAT(:fechaIni,'%Y-%m-%d') AND DATE_FORMAT(:fechaFin,'%Y-%m-%d') " 
			+ "order by m.createAt asc")
	public List<Movimiento> reporteMovimientosFechasPdf(long profId, long EspecId, String fechaIni,
			String fechaFin);

}

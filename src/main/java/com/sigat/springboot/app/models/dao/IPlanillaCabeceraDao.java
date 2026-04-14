package com.sigat.springboot.app.models.dao;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import com.sigat.springboot.app.models.entity.PlanillaCabecera;
import com.sigat.springboot.app.models.entity.Sobreturno;

public interface IPlanillaCabeceraDao
		extends JpaRepository<PlanillaCabecera, Long>, CrudRepository<PlanillaCabecera, Long>, PagingAndSortingRepository<PlanillaCabecera, Long>  {

	
	/*
	 * @Query("select e, e.especialidadNombre from Vinculacion v " +
	 * "inner join Especialidad e on e.id = v.especialidad.id " +
	 * "inner join Profesional p on p.id = v.profesional.id " +
	 * "where v.profesional.id = ?1") public
	 * List<Especialidad>findByIdProfInVinculacion(Long idProf);
	 */
	
	@Query(value="select * from Estado e where e.nombre IN(?1, ?2) ORDER BY id desc;", nativeQuery=true) 
	  public List<PlanillaCabecera>ExistePlanillaCabecera(String term, String term1);
	
	//buscar profesional en Planilla Cabecera antes de eliminar el codigo.
	@Query("select pc from PlanillaCabecera pc "
			+ "inner join Profesional p on p.id = pc.profesional.id "
			+ "where pc.profesional.id = ?1") 
	  public List<PlanillaCabecera>ExisteProfesionalEnPanillaCabecera(long profId);
	
	//buscar especialidad en Planilla Cabecera antes de eliminar el codigo.
		@Query("select pc from PlanillaCabecera pc "
				+ "inner join Especialidad e on e.id = pc.especialidad.id "
				+ "where pc.especialidad.id = ?1") 
		  public List<PlanillaCabecera>ExisteEspecialidadEnPanillaCabecera(long EspecId);
	
	//buscar vinculacion en Planilla Cabecera antes de eliminar el codigo.
	@Query("select pc from PlanillaCabecera pc "
			+ "inner join Profesional p on p.id = pc.profesional.id "
			+ "inner join Especialidad e on e.id = pc.especialidad.id "
			+ "where pc.profesional.id = :profId and pc.especialidad.id = :EspecId") 
	 public List<PlanillaCabecera>ExisteVinculacionEnPanillaCabecera(long profId, long EspecId);
	
	
	//Obtener el Ultimo ID autogenerado..
	  @Query(value="SELECT MAX(pc.id) FROM planillacabecera pc "
	  		+ "where pc.id_profesional = ?1 and pc.id_especialidad = ?2 and pc.id_estado = 2; ", nativeQuery=true) 
	  long recuperarUltimoId(long profId, long EspecId);
	  
	//esto se utiliza con Set en PlanillaCabecera y trae Planilla Cabecera, planillaDetalles.
	  @Query("select pc from PlanillaCabecera pc left join fetch pc.detalleList where pc.id=?1") //left join trae la Planilla cabecera tenga o no detalle de planillas.
	  Optional<PlanillaCabecera> findWithDetalle(Long id);
	  
	//Muestra la Planilla Cabecera en Turnos por idProfesional y id_Especialidad PARA VER: HORARIOS ATENCION, DIA, ETC.
	@Query("select pc from PlanillaCabecera pc " +
	       //"inner join Dia d on d.id = pc.dia.id  " +
		   "where pc.estado.id = 2 and pc.profesional.id = ?1 and pc.especialidad.id = ?2")	
    public List<PlanillaCabecera> findHorariosByProfIdyEspId(long profId, long EspecId);
	
	//Muestra la Planilla Cabecera en Turnos por idProfesional, id_Especialidad y dia_Id PARA VER: HORARIOS ATENCION, DIA, ETC.
	@Query("select pc from PlanillaCabecera pc " +
		   //"inner join Dia d on d.id = pc.dia.id  " +
		   "where pc.estado.id = 2 "
		   + "and pc.profesional.id = ?1 "
		   + "and pc.especialidad.id = ?2 "
		   + "and pc.dia.id = ?3")	
	public List<PlanillaCabecera> findHorariosByProfIdyEspIdAndDiaId(long profId, long EspecId, long diaId);
	
	//SECCION REPORTES
	
	// reporte Planillas Activas por idProfesional e id_Especialidad y fechas.
	  @Query("select pc from PlanillaCabecera pc "
			  + "inner join Profesional prof on prof.id = pc.profesional.id "
			  + "inner join Especialidad esp on esp.id = pc.especialidad.id "
			  + "inner join Dia d on d.id = pc.dia.id "
			  + "inner join Estado est on est.id = pc.estado.id "
		  	  + "where pc.profesional.id = :profId "
		  	  + "and pc.especialidad.id = :EspecId "
		  	  + "and pc.estado.id = 2 "
		  	  + "and DATE_FORMAT(pc.fechaInicio,'%Y-%m-%d') >=  " 
		  	  + "DATE_FORMAT(:fechaIni,'%Y-%m-%d') AND DATE_FORMAT(pc.fechaFinal,'%Y-%m-%d') <= DATE_FORMAT(:fechaFin,'%Y-%m-%d') "
		  	  + "order by pc.fechaInicio asc")
	  public List<PlanillaCabecera> reportePlanillasActivasFechas(long profId, long EspecId, Date fechaIni, Date fechaFin);
	  
	  
	// SECCION REPORTES PARA PDF

	// reporte Pdf Planillas Activas por idProfesional e id_Especialidad y fechas.
	@Query("select pc from PlanillaCabecera pc "
			  + "inner join Profesional prof on prof.id = pc.profesional.id "
			  + "inner join Especialidad esp on esp.id = pc.especialidad.id "
			  + "inner join Dia d on d.id = pc.dia.id "
			  + "inner join Estado est on est.id = pc.estado.id "
		  	  + "where pc.profesional.id = :profId "
		  	  + "and pc.especialidad.id = :EspecId "
		  	  + "and pc.estado.id = 2 "
		  	  + "and DATE_FORMAT(pc.fechaInicio,'%Y-%m-%d') >=  " 
		  	  + "DATE_FORMAT(:fechaIni,'%Y-%m-%d') AND DATE_FORMAT(pc.fechaFinal,'%Y-%m-%d') <= DATE_FORMAT(:fechaFin,'%Y-%m-%d') "
		  	  + "order by pc.fechaInicio asc")
	public List<PlanillaCabecera> reportePlanillasActivasFechasPdf(long profId, long EspecId, String fechaIni, String fechaFin);

} 

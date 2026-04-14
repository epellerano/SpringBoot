package com.sigat.springboot.app.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import com.sigat.springboot.app.models.entity.Especialidad;
import com.sigat.springboot.app.models.entity.Vinculacion;

public interface IEspecialidadDao extends CrudRepository<Especialidad, Long>, JpaRepository<Especialidad, Long>,
		PagingAndSortingRepository<Especialidad, Long> {

	@Query("select e from Especialidad e where e.especialidadCodigo = ?1")
	Especialidad findByEspecialidadCodigo(String codEsp);

	
	  /*PARA MOSTRAR LA ESPECIALIDAD DE ACUERDO A LA VINCULACION PROFESIONAL/ESPECIALIDAD*/ 
		
		
		/*
		 * @Query("select e, e.especialidadNombre from Vinculacion v " +
		 * "inner join Especialidad e on e.id = v.especialidad.id " +
		 * "inner join Profesional p on p.id = v.profesional.id " +
		 * "where v.profesional.id = ?1") public
		 * List<Especialidad>findByIdProfInVinculacion(Long idProf);
		 */
		 
		  
	  
		
		  @Query("select e from Especialidad e " +
		  "inner join Vinculacion v on v.especialidad.id = e.id " +
		  "inner join Profesional p on p.id = v.profesional.id " +
		  "where v.profesional.id =?1") public
		  List<Especialidad>findByIdProfInVinculacion(Long idProf);
		  

	/* PARA AUTOCOMPLETE */
	@Query("select e from Especialidad e where e.especialidadNombre like %?1%")
	public List<Especialidad> findByNombre(String term);
}

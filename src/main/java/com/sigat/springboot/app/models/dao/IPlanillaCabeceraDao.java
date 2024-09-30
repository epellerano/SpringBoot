package com.sigat.springboot.app.models.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import com.sigat.springboot.app.models.entity.PlanillaCabecera;

public interface IPlanillaCabeceraDao
		extends JpaRepository<PlanillaCabecera, Long>, CrudRepository<PlanillaCabecera, Long>, PagingAndSortingRepository<PlanillaCabecera, Long>  {

	
	/*
	 * @Query("select e, e.especialidadNombre from Vinculacion v " +
	 * "inner join Especialidad e on e.id = v.especialidad.id " +
	 * "inner join Profesional p on p.id = v.profesional.id " +
	 * "where v.profesional.id = ?1") public
	 * List<Especialidad>findByIdProfInVinculacion(Long idProf);
	 */
	 
		

} 

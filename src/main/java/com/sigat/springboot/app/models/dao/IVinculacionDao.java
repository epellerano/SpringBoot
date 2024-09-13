package com.sigat.springboot.app.models.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.sigat.springboot.app.models.entity.Vinculacion;

public interface IVinculacionDao
		extends CrudRepository<Vinculacion, Long>, PagingAndSortingRepository<Vinculacion, Long> {

	@Query("select v from Vinculacion v " 
	        + "inner join Especialidad e on e.id = v.especialidad.id "
			+ "inner join Profesional p on p.id = v.profesional.id "
			+ "where v.especialidad.id = ?1 and v.profesional.id = ?2")
	Vinculacion findByIdEspecialidadAndIdProfesional(Long idEsp, Long idProf);

}

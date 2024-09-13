package com.sigat.springboot.app.models.dao;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.sigat.springboot.app.models.entity.Profesional;

public interface IProfesionalDao extends CrudRepository<Profesional, Long>, JpaRepository<Profesional, Long> ,PagingAndSortingRepository<Profesional, Long>{	
	
	/* PARA AUTOCOMPLETE */
	@Query("select p from Profesional p where p.apellido like %?1%")
	public List<Profesional> findByNombre(String term);
	
	//public List<Producto> findByNombreLikeIgnoreCase(String term);
}

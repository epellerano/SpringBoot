package com.sigat.springboot.app.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import com.sigat.springboot.app.models.entity.Estado;

public interface IEstadoDao extends CrudRepository<Estado, Long>,  JpaRepository<Estado, Long>{
	
	
	  @Query(value="select * from Estado e where e.nombre IN(?1, ?2) ORDER BY id desc;", nativeQuery=true) 
	  public List<Estado>findByEstadoActivoInactivo(String term, String term1);
	  
	  @Query(value="select * from Estado e where e.nombre = ?1", nativeQuery=true) 
	  public List<Estado>findByEstadoActivo(String term);
	 
}

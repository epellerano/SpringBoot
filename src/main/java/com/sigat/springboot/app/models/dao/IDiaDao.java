package com.sigat.springboot.app.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import com.sigat.springboot.app.models.entity.Dia;

public interface IDiaDao extends JpaRepository<Dia, Long>, CrudRepository<Dia, Long>, PagingAndSortingRepository<Dia, Long>  {
	
	@Query(value="select * from dia order by id asc;", nativeQuery=true) 
	  public List<Dia>findAllDias();
	
}

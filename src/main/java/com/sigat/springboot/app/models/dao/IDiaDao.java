package com.sigat.springboot.app.models.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import com.sigat.springboot.app.models.entity.Dia;

public interface IDiaDao extends CrudRepository<Dia, Long>, PagingAndSortingRepository<Dia, Long>  {
	
}

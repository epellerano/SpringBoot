package com.sigat.springboot.app.models.dao;

import org.springframework.data.repository.CrudRepository;
import com.sigat.springboot.app.models.entity.Estado;

public interface IEstadoDao extends CrudRepository<Estado, Long>{
	
}

package com.sigat.springboot.app.models.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sigat.springboot.app.models.entity.Profesional;

public interface IProfesionalService {

	public List<Profesional> findAll();	
	
	//para la paginacion
	public Page<Profesional> findAll(Pageable pageable);

	public void save(Profesional profesional);

	public Profesional findOne(Long id);

	public void delete(Long id);	

}

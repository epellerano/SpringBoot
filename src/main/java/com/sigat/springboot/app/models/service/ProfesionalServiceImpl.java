package com.sigat.springboot.app.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sigat.springboot.app.models.dao.IProfesionalDao;
import com.sigat.springboot.app.models.entity.Profesional;

@Service
public class ProfesionalServiceImpl implements IProfesionalService{

	@Autowired
	private IProfesionalDao profesionalDao;
	
	@Override
	@Transactional(readOnly=true)
	public List<Profesional> findAll() {
		return (List<Profesional>) profesionalDao.findAll();
	}

	@Override
	@Transactional
	public void save(Profesional profesional) {
		profesionalDao.save(profesional);
	}

	@Override
	@Transactional(readOnly=true)
	public Profesional findOne(Long id) {
		return profesionalDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		profesionalDao.deleteById(id);		
	}

	@Override
	@Transactional(readOnly=true)
	public Page<Profesional> findAll(Pageable pageable) {
		return profesionalDao.findAll(pageable);
	}

}

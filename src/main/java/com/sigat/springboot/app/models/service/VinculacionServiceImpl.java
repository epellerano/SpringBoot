package com.sigat.springboot.app.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sigat.springboot.app.models.dao.IProfesionalDao;
import com.sigat.springboot.app.models.dao.IVinculacionDao;
import com.sigat.springboot.app.models.entity.Profesional;
import com.sigat.springboot.app.models.entity.Vinculacion;

@Service
public class VinculacionServiceImpl implements IVinculacionService {

	@Autowired
	private IVinculacionDao vinculacionDao;

	@Autowired
	private IProfesionalDao profesionalDao; /* PARA AUTOCOMPLETE */

	@Override
	@Transactional(readOnly = true)
	public List<Vinculacion> findAll() {
		return (List<Vinculacion>) vinculacionDao.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Vinculacion> findAll(Pageable pageable) {
		return vinculacionDao.findAll(pageable);
	}

	@Override
	@Transactional
	public void save(Vinculacion vinculacion) {
		vinculacionDao.save(vinculacion);
	}

	@Override
	@Transactional(readOnly = true)
	public Vinculacion findOne(Long id) {
		return vinculacionDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		vinculacionDao.deleteById(id);
	}

	/* PARA AUTOCOMPLETE */
	@Override
	@Transactional(readOnly = true)
	public List<Profesional> findByNombre(String term) {
		return profesionalDao.findByNombre(term);
	}

	@Override
	@Transactional(readOnly = true)
	public Vinculacion findByIdEspecialidadAndIdProfesional(long term1, long term2) {
		return vinculacionDao.findByIdEspecialidadAndIdProfesional(term1, term2);
	}
}

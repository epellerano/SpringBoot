package com.sigat.springboot.app.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sigat.springboot.app.models.dao.IEspecialidadDao;
import com.sigat.springboot.app.models.entity.Especialidad;

@Service
public class EspecialidadServiceImpl implements IEspecialidadService {

	@Autowired
	private IEspecialidadDao especialidadDao;
	
	@Override
	@Transactional(readOnly=true)
	public List<Especialidad> findAll() {
		return (List<Especialidad>) especialidadDao.findAll();
	}

	@Override
	@Transactional(readOnly=true)
	public Page<Especialidad> findAll(Pageable pageable) {		
		return especialidadDao.findAll(pageable);
	}

	@Override
	@Transactional
	public void save(Especialidad especialidad) {		
		especialidadDao.save(especialidad);
	}

	@Override
	@Transactional(readOnly=true)
	public Especialidad findOne(Long id) {		
		return especialidadDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		especialidadDao.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public Especialidad findByEspecialidadCodigo(String codEsp) {
		return especialidadDao.findByEspecialidadCodigo(codEsp);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Especialidad> findByIdProfInVinculacion(Long idProf) {
		return especialidadDao.findByIdProfInVinculacion(idProf);
	}
	
}

package com.sigat.springboot.app.models.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sigat.springboot.app.models.dao.IEspecialidadDao;
import com.sigat.springboot.app.models.dao.IPlanillaCabeceraDao;
import com.sigat.springboot.app.models.dao.IProfesionalDao;
import com.sigat.springboot.app.models.entity.Especialidad;
import com.sigat.springboot.app.models.entity.PlanillaCabecera;
import com.sigat.springboot.app.models.entity.Profesional;

@Service
public class PlanillaCabeceraServiceImpl implements IPlanillaCabeceraService {
	
	@Autowired
	private IPlanillaCabeceraDao planillacabeceraDao;	
	@Autowired
	private IProfesionalDao profesionalDao;	/* PARA AUTOCOMPLETE PROF */
	@Autowired
	private IEspecialidadDao especialidadDao; /* PARA AUTOCOMPLETE ESPECIALIDAD */

	@Override
	@Transactional(readOnly = true)
	public List<PlanillaCabecera> findAll() {
		return (List<PlanillaCabecera>) planillacabeceraDao.findAll();
	}

	
	  @Override	  
	  @Transactional(readOnly = true) 
	  public Page<PlanillaCabecera>findAll(Pageable pageable) { 
		  return planillacabeceraDao.findAll(pageable); }
	 

	@Override
	@Transactional
	public void save(PlanillaCabecera planillacabecera) {
		planillacabeceraDao.save(planillacabecera);
	}

	@Override
	@Transactional(readOnly = true)
	public PlanillaCabecera findOne(Long id) {
		return planillacabeceraDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		planillacabeceraDao.deleteById(id);
	}

	/* PARA AUTOCOMPLETE PROFESIONAL */
	@Override
	@Transactional(readOnly = true)
	public List<Profesional> findByNombreProf(String term) {
		return profesionalDao.findByNombre(term);
	}

	/* PARA AUTOCOMPLETE ESPECIALIDAD */
	@Override
	@Transactional(readOnly = true)
	public List<Especialidad> findByNombreEsp(String term) {
		return especialidadDao.findByNombre(term);
	}
}

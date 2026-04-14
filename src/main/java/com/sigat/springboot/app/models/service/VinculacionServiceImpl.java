package com.sigat.springboot.app.models.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sigat.springboot.app.models.dao.IProfesionalDao;
import com.sigat.springboot.app.models.dao.IVinculacionDao;
import com.sigat.springboot.app.models.entity.Movimiento;
import com.sigat.springboot.app.models.entity.PlanillaCabecera;
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

	/* PARA OBTENER LAS OBSERVACIONES DEL PROFESIONAL */
	@Override
	@Transactional(readOnly = true)
	public List<Vinculacion> findObservacionByProfIdyEspId(long profId, long EspecId) {
		return (List<Vinculacion>) vinculacionDao.findObservacionByProfIdyEspId(profId, EspecId);
	}
	
	//SECCION REPORTES ----------------------------------------------------------------------------
	
	// REPORTE Vinculacion por idProfesional.
	@Override
	@Transactional(readOnly=true)
	public List<Vinculacion> reporteVinculacion(Long profId) {
		return vinculacionDao.reporteVinculacion(profId);
	}
	
	
	// SECCION REPORTES PARA PDF

	// REPORTE PDF Movimientos por idProfesional e id_Especialidad Y FECHAS.
	@Override
	@Transactional(readOnly = true)
	public List<Vinculacion> reporteVinculacionesByProfPdf(Long profId) {
		return vinculacionDao.reporteVinculacionesByProfPdf(profId);
	}
}

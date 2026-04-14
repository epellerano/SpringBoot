package com.sigat.springboot.app.models.service;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sigat.springboot.app.models.dao.IEspecialidadDao;
import com.sigat.springboot.app.models.dao.IMovimientoDao;
import com.sigat.springboot.app.models.dao.IProfesionalDao;
import com.sigat.springboot.app.models.entity.Especialidad;
import com.sigat.springboot.app.models.entity.Movimiento;
import com.sigat.springboot.app.models.entity.PlanillaCabecera;
import com.sigat.springboot.app.models.entity.Profesional;

@Service
public class MovimientoServiceImpl implements IMovimientoService {
	
	@Autowired
	private IMovimientoDao movimientoDao;	
	@Autowired
	private IProfesionalDao profesionalDao;	/* PARA AUTOCOMPLETE */
	@Autowired
	private IEspecialidadDao especialidadDao; /* PARA AUTOCOMPLETE */
	

	@Override
	@Transactional(readOnly = true)
	public List<Movimiento> findAll() {
		return (List<Movimiento>) movimientoDao.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Movimiento> findAll(Pageable pageable) {
		return movimientoDao.findAll(pageable);
	}

	@Override
	@Transactional
	public void save(Movimiento movimiento) {
		movimientoDao.save(movimiento);
	}

	@Override
	@Transactional(readOnly = true)
	public Movimiento findOne(Long id) {		
		return movimientoDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		movimientoDao.deleteById(id);
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
	
	//buscar vinculacion en Movimientos antes de eliminar.
	@Override
	public List<Movimiento> ExisteVinculacionEnMovimientos(Long profId, Long EspecId) {
		return movimientoDao.ExisteVinculacionEnMovimientos(profId, EspecId);
	}
	
	//SECCION REPORTES ----------------------------------------------------------------------------
	
	// REPORTE Movimientos por idProfesional e id_Especialidad Y FECHAS.
	@Override
	@Transactional(readOnly=true)
	public List<Movimiento> reporteMovimientosFechas(Long profId, Long EspecId, Date fechaIni, Date fechaFin) {
		return movimientoDao.reporteMovimientosFechas(profId, EspecId, fechaIni, fechaFin);
	}
	
	
	// SECCION REPORTES PARA PDF

	// REPORTE PDF Movimientos por idProfesional e id_Especialidad Y FECHAS.
	@Override
	@Transactional(readOnly = true)
	public List<Movimiento> reporteMovimientosFechasPdf(Long profId, Long EspecId, String fechaIni, String fechaFin) {
		return movimientoDao.reporteMovimientosFechasPdf(profId, EspecId, fechaIni, fechaFin);
	}
}

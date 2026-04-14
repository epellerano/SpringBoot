package com.sigat.springboot.app.models.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

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
import com.sigat.springboot.app.models.entity.Sobreturno;

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

	/* PARA VERIFCAR DUPLICADOS DE PLANILLA CABECERA */
	@Override
	public List<PlanillaCabecera> ExistePlanillaCabecera(String term, String term1) {		
		return planillacabeceraDao.ExistePlanillaCabecera(term, term1);
	}

	/* PARA RECUPERAR EL ULTIMO ID AUTOGENERADO */
	@Override
	public long recuperarUltimoId(long profId, long EspecId) {
		return planillacabeceraDao.recuperarUltimoId(profId, EspecId);
	}

	//metodo que trae la planilla cabecera segun su id y sus Planillas detalles relacionadas las tenga o no.
	@Override
	public Optional<PlanillaCabecera> findWithDetalle(Long id) {
		return planillacabeceraDao.findWithDetalle(id);
	}

	//Muestra la Planilla Cabecera en Turnos por idProfesional y id_Especialidad.		
	@Override
	@Transactional(readOnly=true)
	public List<PlanillaCabecera> findHorariosByProfIdyEspId(Long profId, Long EspecId) {
		return planillacabeceraDao.findHorariosByProfIdyEspId(profId, EspecId);
	}
	
	//Muestra la Planilla Cabecera en Turnos por idProfesional, id_Especialidad y id_Dia.		
	@Override
	@Transactional(readOnly=true)
	public List<PlanillaCabecera> findHorariosByProfIdyEspIdAndDiaId(Long profId, Long EspecId, Long diaId) {
		return planillacabeceraDao.findHorariosByProfIdyEspIdAndDiaId(profId, EspecId, diaId);
	}

	//buscar profesional en Planilla Cabecera antes de eliminar el codigo.
	@Override
	public List<PlanillaCabecera> ExisteProfesionalEnPanillaCabecera(Long profId) {
		return planillacabeceraDao.ExisteProfesionalEnPanillaCabecera(profId);
	}
	
	//buscar especialidad en Planilla Cabecera antes de eliminar el codigo.
	@Override
	public List<PlanillaCabecera> ExisteEspecialidadEnPanillaCabecera(Long EspecId) {
		return planillacabeceraDao.ExisteEspecialidadEnPanillaCabecera(EspecId);
	}
	
	//buscar vinculacion en Planilla Cabecera antes de eliminar.
	@Override
	public List<PlanillaCabecera> ExisteVinculacionEnPanillaCabecera(Long profId, Long EspecId) {
		return planillacabeceraDao.ExisteVinculacionEnPanillaCabecera(profId, EspecId);
	}
	
	
	
	//SECCION REPORTES ----------------------------------------------------------------------------
	
	// REPORTE Planillas Activas por idProfesional e id_Especialidad Y FECHAS.
	@Override
	@Transactional(readOnly=true)
	public List<PlanillaCabecera> reportePlanillasActivasFechas(Long profId, Long EspecId, Date fechaIni, Date fechaFin) {
		return planillacabeceraDao.reportePlanillasActivasFechas(profId, EspecId, fechaIni, fechaFin);
	}
	
	
	// SECCION REPORTES PARA PDF

	// REPORTE PDF Planillas Activas por idProfesional e id_Especialidad Y FECHAS.
	@Override
	@Transactional(readOnly = true)
	public List<PlanillaCabecera> reportePlanillasActivasFechasPdf(Long profId, Long EspecId, String fechaIni, String fechaFin) {
		return planillacabeceraDao.reportePlanillasActivasFechasPdf(profId, EspecId, fechaIni, fechaFin);
	}
	
}

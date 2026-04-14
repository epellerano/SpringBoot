package com.sigat.springboot.app.models.service;

import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sigat.springboot.app.models.entity.Movimiento;
import com.sigat.springboot.app.models.entity.PlanillaCabecera;
import com.sigat.springboot.app.models.entity.Profesional;
import com.sigat.springboot.app.models.entity.Vinculacion;

public interface IVinculacionService {

	// listar sin paginacion
		public List<Vinculacion> findAll();

		// para listar con paginacion
		public Page<Vinculacion> findAll(Pageable pageable);

		// guardar - editar
		public void save(Vinculacion vinculacion);

		// buscar por id
		public Vinculacion findOne(Long id);

		// borrar
		public void delete(Long id);
		
		//metodo para autocomplete-profesionales
		public List<Profesional> findByNombre(String term);
		
		//metodo Existe Especialidad y Profesional
		public Vinculacion findByIdEspecialidadAndIdProfesional(long term1, long term2);
		
		//OBTENEMOS LAS OBSERVACIONES DEL PROFESIONAL
		public List<Vinculacion> findObservacionByProfIdyEspId(long profId, long EspecId);
		
		//SECCION REPORTES --------------------------------------------------------------
		
		// REPORTE Vinculacion por idProfesional.
		public List<Vinculacion> reporteVinculacion(Long profId);
		
		//SECCION REPORTES PARA PDF
		
		// REPORTE PDF Vinculaciones por idProfesional.
		public List<Vinculacion> reporteVinculacionesByProfPdf(Long profId);
}

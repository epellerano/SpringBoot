package com.sigat.springboot.app.models.service;




import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sigat.springboot.app.models.entity.Especialidad;
import com.sigat.springboot.app.models.entity.Estado;
import com.sigat.springboot.app.models.entity.PlanillaCabecera;
import com.sigat.springboot.app.models.entity.Profesional;
import com.sigat.springboot.app.models.entity.Sobreturno;
import com.sigat.springboot.app.models.entity.Vinculacion;

public interface IPlanillaCabeceraService {
	
	    // listar sin paginacion
		public List<PlanillaCabecera> findAll();
		
		//para listar con paginacion 
		public Page<PlanillaCabecera> findAll(Pageable pageable);
		 

		// guardar - editar
		public void save(PlanillaCabecera planillacabecera);

		// buscar por id
		public PlanillaCabecera findOne(Long id);

		// borrar
		public void delete(Long id);
		
		// metodo para autocomplete-profesionales
		public List<Profesional> findByNombreProf(String term);

		// metodo para autocomplete-especialidades
		public List<Especialidad> findByNombreEsp(String term);
		
		// Existe PlanillaCabecera duplicada
		public List<PlanillaCabecera> ExistePlanillaCabecera(String term, String term1);
		
		//metodo recuperar ultimo IdAutogenerado
		public long recuperarUltimoId(long profId, long EspecId);

		//metodo que trae la planilla cabecera segun su id y sus Planillas detalles relacionadas las tenga o no.
		public Optional<PlanillaCabecera> findWithDetalle(Long id);
		
		//Muestra la Planilla Cabecera en Turnos por idProfesional y id_Especialidad.
		public List<PlanillaCabecera> findHorariosByProfIdyEspId(Long profId, Long EspecId);
		
		//Muestra la Planilla Cabecera en Turnos por idProfesional, id_Especialidad y id_Dia.
		public List<PlanillaCabecera> findHorariosByProfIdyEspIdAndDiaId(Long profId, Long EspecId, Long diaId);
		
		//buscar profesional en Planilla Cabecera antes de eliminar el codigo.
		public List<PlanillaCabecera> ExisteProfesionalEnPanillaCabecera(Long profId);
		
		//buscar especialidad en Planilla Cabecera antes de eliminar el codigo.
		public List<PlanillaCabecera> ExisteEspecialidadEnPanillaCabecera(Long EspecId);
		
		//buscar vinculacion en Planilla Cabecera antes de eliminar.
		public List<PlanillaCabecera> ExisteVinculacionEnPanillaCabecera(Long profId, Long EspecId);
		
		//SECCION REPORTES --------------------------------------------------------------
		
		// REPORTE Planillas Activas por idProfesional e id_Especialidad Y FECHAS.
		public List<PlanillaCabecera> reportePlanillasActivasFechas(Long profId, Long EspecId, Date fechaIni, Date fechaFin);
		
		
		//SECCION REPORTES PARA PDF
		
		// REPORTE PDF Planillas Activas por idProfesional e id_Especialidad Y FECHAS.
		public List<PlanillaCabecera> reportePlanillasActivasFechasPdf(Long profId, Long EspecId, String fechaIni, String fechaFin);
 
}

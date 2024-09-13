package com.sigat.springboot.app.models.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.sigat.springboot.app.models.entity.Especialidad;
import com.sigat.springboot.app.models.entity.PlanillaCabecera;
import com.sigat.springboot.app.models.entity.Profesional;

public interface IPlanillaCabeceraService {
	
	// listar sin paginacion
		public List<PlanillaCabecera> findAll();

		// para listar con paginacion
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

}

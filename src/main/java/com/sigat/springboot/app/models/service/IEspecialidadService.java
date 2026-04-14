package com.sigat.springboot.app.models.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.sigat.springboot.app.models.entity.Especialidad;

public interface IEspecialidadService {

	// listar sin paginacion
	public List<Especialidad> findAll();

	// para listar con paginacion
	public Page<Especialidad> findAll(Pageable pageable);

	// guardar - editar
	public void save(Especialidad especialidad);

	// buscar por id
	public Especialidad findOne(Long id);

	// borrar
	public void delete(Long id);	

	//metodo Existe Especialidad codigo
	public Especialidad findByEspecialidadCodigo(String codEsp);
	
	
	//MOSTRAR LA ESPECIALIDAD DE ACUERDO AL PROFESIONAL DE LA VINCULACION
	public List<Especialidad> findByIdProfInVinculacion(Long idProf);
}

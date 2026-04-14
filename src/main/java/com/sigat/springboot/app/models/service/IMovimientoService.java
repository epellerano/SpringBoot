package com.sigat.springboot.app.models.service;

import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.sigat.springboot.app.models.entity.Especialidad;
import com.sigat.springboot.app.models.entity.Movimiento;
import com.sigat.springboot.app.models.entity.PlanillaCabecera;
import com.sigat.springboot.app.models.entity.Profesional;

public interface IMovimientoService {

	// listar sin paginacion
	public List<Movimiento> findAll();

	// para listar con paginacion
	public Page<Movimiento> findAll(Pageable pageable);

	// guardar - editar
	public void save(Movimiento movimiento);

	// buscar por id
	public Movimiento findOne(Long id);

	// borrar
	public void delete(Long id);

	// metodo para autocomplete-profesionales
	public List<Profesional> findByNombreProf(String term);

	// metodo para autocomplete-especialidades
	public List<Especialidad> findByNombreEsp(String term);
	
	//buscar vinculacion en Movimientos antes de eliminar.
	public List<Movimiento> ExisteVinculacionEnMovimientos(Long profId, Long EspecId);
	
	//SECCION REPORTES --------------------------------------------------------------
	
	// REPORTE Movimientos por idProfesional e id_Especialidad Y FECHAS.
	public List<Movimiento> reporteMovimientosFechas(Long profId, Long EspecId, Date fechaIni, Date fechaFin);
	
	
	//SECCION REPORTES PARA PDF
	
	// REPORTE PDF Movimientos por idProfesional e id_Especialidad Y FECHAS.
	public List<Movimiento> reporteMovimientosFechasPdf(Long profId, Long EspecId, String fechaIni, String fechaFin);
	
			
	
}

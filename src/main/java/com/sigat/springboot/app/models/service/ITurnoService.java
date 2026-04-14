package com.sigat.springboot.app.models.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.sigat.springboot.app.models.entity.Paciente;
import com.sigat.springboot.app.models.entity.PlanillaCabecera;
import com.sigat.springboot.app.models.entity.Profesional;
import com.sigat.springboot.app.models.entity.Sobreturno;
import com.sigat.springboot.app.models.entity.Turno;

public interface ITurnoService {

	public List<Turno> findAll();

	public void save(Turno turno);
	
	// Agrega esta línea exactamente así:
    void registrarTurnoCompleto(Turno turno, String planillaDetId);

	public Turno findOne(Long id);

	public void delete(Long id);

	// Metodo para Listar Dia y Rango Fecha Hora de Planilladetalle
	public String[] buscarPlanillaActiva(long profId, long EspecId);

	// Muestra llos Turnos Ocupados por idProfesional e id_Especialidad.
	public List<Turno> findTurnosOcupadosByProfIdyEspId(Long profId, Long EspecId);

	// Muestra los Turnos Ocupados por id del Turno.
	public List<Turno> findTurnoClickById(Long turnoId);
	
	//buscar paciente en Turnos Activos antes de eliminar.
	public List<Turno> pacientePoseeTurnosActivos(Long pacId);
		
	//buscar turnos ocupados fechas (vista: Cancelar Planillas) 
	public List<Turno>buscarTurnosOcupadosFechas(Long profId, Long EspecId, Date fechaIni, Date  fechaFin);
	
	//buscar turnos ocupados fechas y horas (vista: Cancelar Planillas) 
	public List<Turno>buscarTurnosOcupadosFechasHoras(Long profId, Long EspecId, Date fechaHoraIni, Date  fechaHoraFin);
	
	// DESACTIVAMOS EL MODO SEGURO DE MYSQL. (vista: Cancelar Planillas)
	public void desactivarSafeUpdates();
	
	// Eliminar "Turnos Ocupados" entre fechas (vista: Cancelar Planillas)
	public void eliminarTurnosOcupadosFechas(Long profId, Long EspecId, Date fechaIni, Date fechaFin);
	
	// Eliminar "Turnos Ocupados" entre fechas y horas (vista: Cancelar Planillas)
	public void eliminarTurnosOcupadosFechasHoras(Long profId, Long EspecId, Date fechaHoraIni, Date fechaHoraFin);
	
	
	//SECCION REPORTES --------------------------------------------------------------
	
	// REPORTE Turnos Ocupados por idProfesional e id_Especialidad Y FECHAS. 
	public List<Turno> reporteTurnosOcupadosFechas(Long profId, Long EspecId, Date fechaIni, Date fechaFin);
	
	// REPORTE Sobreturnos Ocupados por idProfesional e id_Especialidad Y FECHAS.
	public List<Sobreturno> reporteSobreTurnosOcupadosFechas(Long profId, Long EspecId, Date fechaIni, Date fechaFin);
	
	
	//SECCION REPORTES PARA PDF ------------------------------------------------------
	
	// REPORTE PDF Turnos Ocupados por idProfesional e id_Especialidad Y FECHAS.
		public List<Turno> reporteTurnosOcupadosFechasPdf(Long profId, Long EspecId, String fechaIni, String fechaFin);
	

	
	
	
}

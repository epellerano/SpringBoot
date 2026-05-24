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
	
	// CANCELACION MASIVA DE TURNOS (Estado CANCELADO - 6)
	public void cancelarTurnosSeleccionados(List<Long> ids);
	
	// CANCELACION INDIVIDUAL DE TURNOS (Estado CANCELADO - 6)
	public void eliminar(Long id);
	
	//LISTAR TURNOS SOLO CON ID=4
	public List<Turno> findAllOcupados();
	
	//click en finalizarAtencion, paciente se retira por su cuenta
	public List<Turno> findAllTotal();
	
	
	//CANCELACION MASIVA NUEVA
	void eliminarTurnosOcupadosFechas(Long profId, Long especId, LocalDateTime fechaIni, LocalDateTime fechaFin);
    
    void eliminarTurnosOcupadosFechasHoras(Long profId, Long especId, LocalDateTime inicio, LocalDateTime fin);

    List<Turno> buscarTurnosOcupadosParaBajaSoloFecha(Long profId, Long especId, String inicio, String fin);
    List<Turno> buscarTurnosOcupadosParaBajaFechaHora(Long profId, Long especId, java.time.LocalDateTime inicio, java.time.LocalDateTime fin);

	//PARA RESTAURACIION (AUDITORIA)
    List<Turno> findCancelados(Long profId, Long especId, String desde, String hasta);

    //HISTORIA CLINICA
    public Turno findById(Long id);
	
	//SECCION REPORTES --------------------------------------------------------------
	
	// REPORTE Turnos Ocupados por idProfesional e id_Especialidad Y FECHAS. 
	public List<Turno> reporteTurnosOcupadosFechas(Long profId, Long EspecId, Date fechaIni, Date fechaFin);
	
	// REPORTE Sobreturnos Ocupados por idProfesional e id_Especialidad Y FECHAS.
	public List<Sobreturno> reporteSobreTurnosOcupadosFechas(Long profId, Long EspecId, Date fechaIni, Date fechaFin);
	
	
	//SECCION REPORTES PARA PDF ------------------------------------------------------
	
	// REPORTE PDF Turnos Ocupados por idProfesional e id_Especialidad Y FECHAS.
	public List<Turno> reporteTurnosOcupadosFechasPdf(Long profId, Long EspecId, String fechaIni, String fechaFin);
	

	
	
	
}

package com.sigat.springboot.app.models.service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sigat.springboot.app.models.entity.Paciente;
import com.sigat.springboot.app.models.entity.Profesional;
import com.sigat.springboot.app.models.entity.Sobreturno;
import com.sigat.springboot.app.models.entity.Turno;

public interface ISobreTurnoService {

	//para la paginacion
	public Page<Sobreturno> findAll(Pageable pageable);
		
	public List<Sobreturno> findAll();

	public void save(Sobreturno sobreturno);

	public Sobreturno findOne(Long id);

	public void delete(Long id);

	// Metodo para Listar Dia y Rango Fecha Hora de Planilladetalle
	public String[] buscarPlanillaActiva(long profId, long EspecId);

	// Muestra llos SobreTurnos Ocupados por idProfesional e id_Especialidad.*
	public List<Sobreturno> findSobreTurnosOcupadosByProfIdyEspId(Long profId, Long EspecId);

	//Muestra los SobreTurnos Ocupados por el Profesional, Especialidad y paciente por sobreturno_id.
	//alhacer click en la lupa.
	public List<Sobreturno> findSobreTurnoClickById(Long sobreturnoId);
	
	//buscar paciente en Sobreturnos Activos antes de eliminar.
	public List<Sobreturno> pacientePoseeSobreturnosActivos(Long pacId);
	
	//buscar sobreturnos ocupados fechas (vista: Cancelar Planillas) 
	public List<Sobreturno>buscarSobreturnosOcupadosFechas(Long profId, Long EspecId, Date fechaIni, Date  fechaFin);
	
	//buscar sobreturnos ocupados fechas Horas (vista: Cancelar Planillas) 
	public List<Sobreturno>buscarSobreturnosOcupadosFechasHoras(Long profId, Long EspecId, Date fechaHoraIni, Date  fechaHoraFin);
	
	//para los Mails
	public void registrarSobreturnoCompleto(Sobreturno sobreturno);
	
	//PARA CANCELACION DE PLANILLAS NUEVO
	 // Para el PDF usamos Date
	List<Sobreturno> buscarSobreturnosParaBajaSoloFecha(Long profId, Long especId, String inicio, String fin);
    List<Sobreturno> buscarSobreturnosParaBajaFechaHora(Long profId, Long especId, LocalDateTime inicio, LocalDateTime fin);
    
    // Para la cancelación masiva usamos LocalDateTime
    void eliminarSobreturnosOcupadosFechas(Long profId, Long especId, LocalDateTime inicio, LocalDateTime fin);
    void eliminarSobreturnosOcupadosFechasHoras(Long profId, Long especId, LocalDateTime inicio, LocalDateTime fin);
    
	// En ISobreTurnoService
    void cancelarSobreturnosMasivo(Long profId, Long especId, LocalDateTime inicio, LocalDateTime fin);
    
    // Para la selección manual por Checkbox (listarSobreturno)
    void cancelarSobreturnosPorLista(List<Long> ids);
    
    List<Sobreturno> findAllActivosYAtendidos();
    
    //SECCION RESTAURACION (AUDITORIA)
    String restaurarSobreturnoIndividual(Long id);
    List<Profesional> findAllProfesionales();
    List<Sobreturno> findCancelados(Long profId, Long especId, String desde, String hasta);
    
    //HISTORIA CLINICA
    public Sobreturno findById(Long id);

	
	//SECCION REPORTES PARA PDF
	
	// REPORTE PDF Sobreturnos Ocupados por idProfesional e id_Especialidad Y FECHAS.
	public List<Sobreturno> reporteSobreturnosOcupadosFechasPdf(Long profId, Long EspecId, String fechaIni, String fechaFin);

}

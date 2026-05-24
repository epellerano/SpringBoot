package com.sigat.springboot.app.models.service;

import java.util.List;

import com.sigat.springboot.app.models.entity.Dia;
import com.sigat.springboot.app.models.entity.HistoriaClinica;

public interface IHistoriaClinicaService {

	public void guardarHistoria(HistoriaClinica historiaClinica);
	public List<HistoriaClinica> obtenerHistorialPorPaciente(Long pacienteId);
	public HistoriaClinica obtenerUltimaConsulta(Long pacienteId);
	
	public HistoriaClinica findById(Long id);

}

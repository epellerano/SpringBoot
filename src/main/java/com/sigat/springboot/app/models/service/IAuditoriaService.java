package com.sigat.springboot.app.models.service;

import java.util.List;

import com.sigat.springboot.app.models.entity.PlanillaDetalle;
import com.sigat.springboot.app.models.entity.Sobreturno;
import com.sigat.springboot.app.models.entity.Turno;

public interface IAuditoriaService {

	// Asegúrate de que el nombre coincida exactamente
	String restaurarTurnoIndividual(Long turnoId);
	
	public List<Turno> listarMonitorTurnos();
	public List<Sobreturno> listarMonitorSobreturnos();
	public List<PlanillaDetalle> listarMonitorPlanillas();

}

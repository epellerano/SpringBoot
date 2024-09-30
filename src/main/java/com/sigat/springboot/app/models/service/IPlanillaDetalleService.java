package com.sigat.springboot.app.models.service;

import java.util.Date;
import java.util.List;

import com.sigat.springboot.app.models.entity.PlanillaCabecera;
import com.sigat.springboot.app.models.entity.PlanillaDetalle;

public interface IPlanillaDetalleService {
	
	// listar sin paginacion
    //public List<PlanillaDetalle> crearRangoTurnoMañana(Date fechahorainicio, Date fechahorafin, Integer intervalo, Integer iddia);

 // //prueba (despues borrar)
    public List<PlanillaCabecera> findplanillaCabecera(Long id);
}

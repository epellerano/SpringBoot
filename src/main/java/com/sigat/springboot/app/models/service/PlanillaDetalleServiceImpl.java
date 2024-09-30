package com.sigat.springboot.app.models.service;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sigat.springboot.app.models.dao.IPlanillaCabeceraDao;
import com.sigat.springboot.app.models.dao.IPlanillaDetalleDao;
import com.sigat.springboot.app.models.entity.PlanillaCabecera;
import com.sigat.springboot.app.models.entity.PlanillaDetalle;

@Service
public class PlanillaDetalleServiceImpl implements IPlanillaDetalleService {
	
	@Autowired
	private IPlanillaDetalleDao planilladetalleDao;
	@Autowired
	private IPlanillaCabeceraDao planillacabeceraDao;

	/*
	 * @Override
	 * 
	 * @Transactional(readOnly = true) public List<PlanillaDetalle>
	 * crearRangoTurnoMañana(Date fechahorainicio, Date fechahorafin, Integer
	 * intervalo, Integer iddia) { return
	 * planilladetalleDao.crearRangoTurnoMañana(fechahorainicio, fechahorafin,
	 * intervalo, iddia); }
	 */

	@Override
	@Transactional(readOnly = true)
	public List<PlanillaCabecera> findplanillaCabecera(Long id) {
		return planilladetalleDao.findplanillaCabecera(id);
	}

}

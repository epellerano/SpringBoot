package com.sigat.springboot.app.models.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sigat.springboot.app.models.dao.IEstadoDao;
import com.sigat.springboot.app.models.entity.Estado;

@Service
public class EstadoServiceImpl implements IEstadoService {
	
	@Autowired
	private IEstadoDao estadoDao;

	@Override
	@Transactional(readOnly=true)
	public List<Estado> findAll() {
		// TODO Auto-generated method stub
		return (List<Estado>) estadoDao.findAll();
	}

	@Override
	public List<Estado> findByEstadoActivoInactivo(String term, String term1) {		
		return estadoDao.findByEstadoActivoInactivo(term, term1);
	}

	@Override	
	public List<Estado> findByEstadoActivo(String term) {
		return estadoDao.findByEstadoActivo(term);
	}

}

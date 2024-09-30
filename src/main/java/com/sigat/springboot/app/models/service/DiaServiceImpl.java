package com.sigat.springboot.app.models.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sigat.springboot.app.models.dao.IDiaDao;
import com.sigat.springboot.app.models.entity.Dia;

@Service
public class DiaServiceImpl implements IDiaService {
	
	@Autowired
	private IDiaDao diaDao;

	@Override
	@Transactional(readOnly=true)
	public List<Dia> findAll() {
		// TODO Auto-generated method stub
		return (List<Dia>) diaDao.findAll();
	}

}

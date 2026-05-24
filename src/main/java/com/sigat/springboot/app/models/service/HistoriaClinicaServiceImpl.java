package com.sigat.springboot.app.models.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sigat.springboot.app.models.dao.IDiaDao;
import com.sigat.springboot.app.models.dao.IHistoriaClinicaDao;
import com.sigat.springboot.app.models.entity.Dia;
import com.sigat.springboot.app.models.entity.HistoriaClinica;

@Service
public class HistoriaClinicaServiceImpl implements IHistoriaClinicaService {
	
	@Autowired
    private IHistoriaClinicaDao historiaDao;

    @Override
    @Transactional
    public void guardarHistoria(HistoriaClinica historiaClinica) {
        historiaDao.save(historiaClinica);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistoriaClinica> obtenerHistorialPorPaciente(Long pacienteId) {
        return historiaDao.findByPacienteId(pacienteId);
    }

    @Override
    @Transactional(readOnly = true)
    public HistoriaClinica obtenerUltimaConsulta(Long pacienteId) {
        List<HistoriaClinica> lista = historiaDao.findByPacienteId(pacienteId);
        // Retornamos la primera (la más nueva) o null si es la primera vez que viene
        return lista.isEmpty() ? null : lista.get(0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public HistoriaClinica findById(Long id) {
        // .orElse(null) es importante por si el ID no existe por algún motivo
        return historiaDao.findById(id).orElse(null);
    }

}

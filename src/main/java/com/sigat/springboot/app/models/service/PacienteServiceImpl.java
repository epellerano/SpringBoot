package com.sigat.springboot.app.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sigat.springboot.app.models.dao.IPacienteDao;
import com.sigat.springboot.app.models.entity.Paciente;
import com.sigat.springboot.app.models.entity.Profesional;

@Service
public class PacienteServiceImpl implements IPacienteService {

	@Autowired
	private IPacienteDao pacienteDao;

	@Override
	@Transactional(readOnly = true)
	public List<Paciente> findAll() {
		return (List<Paciente>) pacienteDao.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Paciente> findAll(Pageable pageable) {
		return pacienteDao.findAll(pageable);
	}

	@Override
	@Transactional
	public void save(Paciente paciente) {
		pacienteDao.save(paciente);
	}

	@Override
	@Transactional(readOnly = true)
	public Paciente findOne(Long id) {
		return pacienteDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		pacienteDao.deleteById(id);

	}

	// para autocomplete en turnos
	@Override
	public List<Paciente> findByNombre(String term) {
		// TODO Auto-generated method stub
		return pacienteDao.findByNombre(term);
	}

	// para autocomplete en turnos
	@Override
	public List<Paciente> findByDni(String term) {
		// TODO Auto-generated method stub
		return pacienteDao.findByDni(term);
	}

	// metodo Existe el Dni del paciente al insertar.
	@Override
	@Transactional(readOnly = true)
	public Paciente findByPacienteDni(String pacienteDni) {
		return pacienteDao.findByPacienteDni(pacienteDni);
	}

	// metodo Existe el Nº socio del paciente al insertar.
	@Override
	@Transactional(readOnly = true)
	public Paciente findByPacienteNsocio(String pacienteNsocio) {
		return pacienteDao.findByPacienteNsocio(pacienteNsocio);
	}
	
	// metodo Existe el Dni del paciente al actualizar.
		@Override
		@Transactional(readOnly = true)
		public Paciente findByPacienteDniUpdate(String pacienteDni, Long pacienteId) {
			return pacienteDao.findByPacienteDniUpdate(pacienteDni, pacienteId);
		}

		// metodo Existe el Nº socio del paciente al actualizar.
		@Override
		@Transactional(readOnly = true)
		public Paciente findByPacienteNsocioUpdate(String pacienteNsocio, Long pacienteId) {
			return pacienteDao.findByPacienteNsocioUpdate(pacienteNsocio, pacienteId);
		}
}

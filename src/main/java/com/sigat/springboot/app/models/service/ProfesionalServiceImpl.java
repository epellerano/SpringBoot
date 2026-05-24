package com.sigat.springboot.app.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sigat.springboot.app.models.dao.IProfesionalDao;
import com.sigat.springboot.app.models.entity.Especialidad;
import com.sigat.springboot.app.models.entity.Profesional;

@Service
public class ProfesionalServiceImpl implements IProfesionalService {

	@Autowired
	private IProfesionalDao profesionalDao;

	@Override
	@Transactional(readOnly = true)
	public List<Profesional> findAll() {
		return (List<Profesional>) profesionalDao.findAll();
	}

	@Override
	@Transactional
	public void save(Profesional profesional) {
		profesionalDao.save(profesional);
	}

	@Override
	@Transactional(readOnly = true)
	public Profesional findOne(Long id) {
		return profesionalDao.findById(id).orElse(null);
	}
	
	//Para el search buscador
	@Override
	public Page<Profesional> findByNombreOrApellidoOrCodigo(String term, Pageable pageable) {
	    return profesionalDao.findByNombreOrApellidoOrCodigo(term, pageable);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		profesionalDao.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Profesional> findAll(Pageable pageable) {
		return profesionalDao.findAll(pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public String obtenerNombreArchivo(Long profId) {
		return profesionalDao.findImagenBase64ById(profId);
	}

	// metodo Existe el Dni del profesional al insertar.
	@Override
	@Transactional(readOnly = true)
	public Profesional findByProfesionalDni(String profDni) {
		return profesionalDao.findByProfesionalDni(profDni);
	}

	// metodo Existe el Codigo del profesional al insertar.
	@Override
	@Transactional(readOnly = true)
	public Profesional findByProfesionalCodigo(String profCodigo) {
		return profesionalDao.findByProfesionalCodigo(profCodigo);
	}

	// metodo Existe la Matricula del profesional al insertar.
	@Override
	@Transactional(readOnly = true)
	public Profesional findByProfesionalMatricula(String profMatricula) {
		return profesionalDao.findByProfesionalMatricula(profMatricula); 
	}
	
	// metodo Existe el Dni del profesional al actualizar menos este prof_id.
		@Override
		@Transactional(readOnly = true)
		public Profesional findByProfesionalDniUpdate(String profDni, Long profId) {
			return profesionalDao.findByProfesionalDniUpdate(profDni, profId);
		}

		// metodo Existe el Codigo del profesional al actualizar menos este prof_id.
		@Override
		@Transactional(readOnly = true)
		public Profesional findByProfesionalCodigoUpdate(String profCodigo, Long profId) {
			return profesionalDao.findByProfesionalCodigoUpdate(profCodigo, profId);
		}

		// metodo Existe la Matricula del profesional al actualizar menos este prof_id.
		@Override
		@Transactional(readOnly = true)
		public Profesional findByProfesionalMatriculaUpdate(String profMatricula, Long profId) {
			return profesionalDao.findByProfesionalMatriculaUpdate(profMatricula, profId);
		}
		
		//Historia Clinica
		/*
		 * si por algún motivo el nombre de usuario tiene menos de 3 letras, el sistema
		 * podría dar un error
		 */
		@Override
		@Transactional(readOnly = true)
		public Profesional findByUsername(String username) {
		    if (username == null || username.length() < 3) {
		        return null;
		    }
		    // Extrae las primeras 3 letras y las pasa a Mayúsculas
		    String codigoBusqueda = username.substring(0, 3).toUpperCase();
		    return profesionalDao.findByCodigo(codigoBusqueda);
		}

}

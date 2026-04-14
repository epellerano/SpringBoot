package com.sigat.springboot.app.models.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sigat.springboot.app.models.dao.IPacienteDao;
import com.sigat.springboot.app.models.dao.ISobreTurnosDao;
import com.sigat.springboot.app.models.dao.ITurnosDao;
import com.sigat.springboot.app.models.entity.Especialidad;
import com.sigat.springboot.app.models.entity.Paciente;
import com.sigat.springboot.app.models.entity.PlanillaCabecera;
import com.sigat.springboot.app.models.entity.PlanillaDetalle;
import com.sigat.springboot.app.models.entity.Profesional;
import com.sigat.springboot.app.models.entity.Sobreturno;
import com.sigat.springboot.app.models.entity.Turno;

@Service
public class SobreTurnoServiceImpl implements ISobreTurnoService {

	@Autowired
	private ISobreTurnosDao sobreturnoDao;
	@Autowired
	private IPacienteService pacienteService;
	@Autowired
	private IProfesionalService profesionalService;
	@Autowired
	private IEspecialidadService especialidadService;
	@Autowired
	private EmailService emailService;
	
	@Override
	@Transactional
	public void registrarSobreturnoCompleto(Sobreturno sobreturno) {
	    
		// 1. Verificación de seguridad
	    boolean yaExiste = sobreturnoDao.existsByProfesionalIdAndEspecialidadIdAndRangoFechaHora(
	        sobreturno.getProfesional().getId(),
	        sobreturno.getEspecialidad().getId(),
	        sobreturno.getRangoFechaHora()
	    );

	    if (yaExiste) {
	        // Este mensaje es el que busca el Controller en el catch
	        throw new RuntimeException("EL_HORARIO_YA_ESTA_OCUPADO");
	    }

	    // 2. Si pasa la validación, guardamos
	    sobreturnoDao.save(sobreturno);

	    // 3. Completar objetos para el mail (Paciente, Profesional, Especialidad)
	    Paciente pacienteCompleto = pacienteService.findOne(sobreturno.getPaciente().getId());
	    Profesional profesionalCompleto = profesionalService.findOne(sobreturno.getProfesional().getId());
	    Especialidad especialidadCompleta = especialidadService.findOne(sobreturno.getEspecialidad().getId());
	    
	    sobreturno.setPaciente(pacienteCompleto);
	    sobreturno.setProfesional(profesionalCompleto);
	    sobreturno.setEspecialidad(especialidadCompleta);

	    // 4. Disparar mail
	    emailService.enviarMailConfirmacionSobreturno(sobreturno);
	}


	@Override
	@Transactional(readOnly = true)
	public List<Sobreturno> findAll() {
		return (List<Sobreturno>) sobreturnoDao.findAll();
	}
	
	@Override
	@Transactional(readOnly = true)
	public Page<Sobreturno> findAll(Pageable pageable) {
	    return sobreturnoDao.findAll(pageable);
	}

	@Override
	@Transactional
	public void save(Sobreturno sobreturno) {
		sobreturnoDao.save(sobreturno);
	}

	@Override
	@Transactional(readOnly = true)
	public Sobreturno findOne(Long id) {
		return sobreturnoDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		sobreturnoDao.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public String[] buscarPlanillaActiva(long profId, long EspecId) {
		// TODO Auto-generated method stub
		return sobreturnoDao.buscarPlanillaActiva(profId, EspecId);
	}

	// Muestra la Planilla Cabecera en Turnos por idProfesional y id_Especialidad.
	@Override
	@Transactional(readOnly = true)
	public List<Sobreturno> findSobreTurnosOcupadosByProfIdyEspId(Long profId, Long EspecId) {
		return sobreturnoDao.findSobreTurnosOcupadosByProfIdyEspId(profId, EspecId);
	}

	// Muestra los SobreTurnos Ocupados por el Profesional, Especialidad y paciente
	// por sobreturno_id.
	// alhacer click en la lupa.
	@Override
	@Transactional(readOnly = true)
	public List<Sobreturno> findSobreTurnoClickById(Long sobreturnoId) {
		return sobreturnoDao.findSobreTurnoClickById(sobreturnoId);
	}

	// buscar paciente en Sobreturnos Activos antes de eliminar.
	@Override
	@Transactional(readOnly = true)
	public List<Sobreturno> pacientePoseeSobreturnosActivos(Long pacId) {
		return sobreturnoDao.pacientePoseeSobreturnosActivos(pacId);
	}

	// buscar sobreturnos ocupados fechas (vista: Cancelar Planillas)
	@Override
	@Transactional(readOnly = true)
	public List<Sobreturno> buscarSobreturnosOcupadosFechas(Long profId, Long EspecId, Date fechaIni, Date fechaFin) {
		return sobreturnoDao.buscarSobreturnosOcupadosFechas(profId, EspecId, fechaIni, fechaFin);
	}

	// buscar sobreturnos ocupados fechas y horas (vista: Cancelar Planillas)
	@Override
	@Transactional(readOnly = true)
	public List<Sobreturno> buscarSobreturnosOcupadosFechasHoras(Long profId, Long EspecId, Date fechaHoraIni,
			Date fechaHoraFin) {
		return sobreturnoDao.buscarSobreturnosOcupadosFechasHoras(profId, EspecId, fechaHoraIni, fechaHoraFin);
	}

	// Eliminar "Sobreturnos Ocupados" entre fechas (vista: Cancelar Planillas)
	@Override
	@Transactional
	public void eliminarSobreturnosOcupadosFechas(Long profId, Long EspecId, Date fechaIni, Date fechaFin) {
		sobreturnoDao.eliminarSobreturnosOcupadosFechas(profId, EspecId, fechaIni, fechaFin);
	}

	// Eliminar "Sobreturnos Ocupados" entre fechas y horas (vista: Cancelar Planillas)
	@Override
	@Transactional
	public void eliminarSobreturnosOcupadosFechasHoras(Long profId, Long EspecId, Date fechaHoraIni, Date fechaHoraFin) {
		sobreturnoDao.eliminarSobreturnosOcupadosFechasHoras(profId, EspecId, fechaHoraIni, fechaHoraFin);
	}

	// SECCION REPORTES PARA PDF

	// REPORTE PDF Sobreturnos Ocupados por idProfesional e id_Especialidad Y
	// FECHAS.
	@Override
	@Transactional(readOnly = true)
	public List<Sobreturno> reporteSobreturnosOcupadosFechasPdf(Long profId, Long EspecId, String fechaIni,
			String fechaFin) {
		return sobreturnoDao.reporteSobreturnosOcupadosFechasPdf(profId, EspecId, fechaIni, fechaFin);
	}
}

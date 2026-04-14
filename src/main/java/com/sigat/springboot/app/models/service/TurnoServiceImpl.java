package com.sigat.springboot.app.models.service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sigat.springboot.app.models.dao.IEspecialidadDao;
import com.sigat.springboot.app.models.dao.IEstadoDao;
import com.sigat.springboot.app.models.dao.IPacienteDao;
import com.sigat.springboot.app.models.dao.IProfesionalDao;
import com.sigat.springboot.app.models.dao.ITurnosDao;
import com.sigat.springboot.app.models.entity.Especialidad;
import com.sigat.springboot.app.models.entity.Estado;
import com.sigat.springboot.app.models.entity.Paciente;
import com.sigat.springboot.app.models.entity.PlanillaCabecera;
import com.sigat.springboot.app.models.entity.PlanillaDetalle;
import com.sigat.springboot.app.models.entity.Profesional;
import com.sigat.springboot.app.models.entity.Sobreturno;
import com.sigat.springboot.app.models.entity.Turno;

@Service
public class TurnoServiceImpl implements ITurnoService {

	@Autowired
	private ITurnosDao turnoDao;	
	@Autowired
	private EmailService emailService;	
	@Autowired
	private IPacienteService pacienteService;	
	@Autowired
	private IPlanillaDetalleService planilladetalleService;	
	@Autowired
	private IProfesionalService profesionalService;	
	@Autowired
	private IEspecialidadService especialidadService;
	@Autowired
	private IEstadoDao estadoDao;
	
	
	@Override
	@Transactional(readOnly = true)
	public List<Turno> findAll() {
		return (List<Turno>) turnoDao.findAll();
	}

	@Override
	@Transactional
	public void save(Turno turno) {
		turnoDao.save(turno);
	}	
	
	@Override
	@Transactional
	public void registrarTurnoCompleto(Turno turno, String planillaDetId) {
	    // 1. Guardamos el turno (JPA genera el ID)
	    turnoDao.save(turno);

	    // 2. Marcamos la planilla como OCUPADA
	    planilladetalleService.updatePlanillaDetalleTurnoOcupado(planillaDetId);

	    // 3. RELLENAMOS TODOS LOS DATOS FALTANTES (Para evitar Nulls en el Mail)
	    
	    // Traemos la Planilla completa (para la FECHA) los findOne no requieren DAO.
	    PlanillaDetalle pd = planilladetalleService.findOne(Long.parseLong(planillaDetId));
	    
	    // Traemos el Paciente completo (para el EMAIL y NOMBRE)
	    Paciente pacienteCompleto = pacienteService.findOne(turno.getPaciente().getId());

	    // Traemos el Profesional completo (para NOMBRE y APELLIDO del médico)
	    Profesional profesionalCompleto = profesionalService.findOne(turno.getProfesional().getId());

	    // Traemos la Especialidad completa (para el NOMBRE de la especialidad)
	    Especialidad especialidadCompleta = especialidadService.findOne(turno.getEspecialidad().getId());
	    
	    // 4. "CURAMOS" el objeto turno inyectando los objetos con datos reales
	    turno.setPlanilladetalle(pd);
	    turno.setPaciente(pacienteCompleto);
	    turno.setProfesional(profesionalCompleto);
	    turno.setEspecialidad(especialidadCompleta);

	    // 5. Enviamos el mail (Ahora tiene ID, FECHA, EMAIL, MÉDICO y ESPECIALIDAD)
	    emailService.enviarMailConfirmacion(turno);
	}



	
	@Override
	@Transactional(readOnly = true)
	public Turno findOne(Long id) {
		return turnoDao.findById(id).orElse(null);
	}

	/*
	 * @Override
	 * 
	 * @Transactional public void delete(Long id) { turnoDao.deleteById(id); }
	 */
	@Override
	@Transactional
	public void delete(Long id) {
	    // 1. Buscamos el turno completo
	    Turno turno = turnoDao.findById(id).orElse(null);
	    
	    if (turno != null) {
	        // 2. Buscamos el objeto Estado 'CANCELADO' (ID 6)
	        Estado estadoCancelado = estadoDao.findById(6L).orElse(null);
	        
	        // 3. CAMBIO DE ESTADO en lugar de delete físico
	        turno.setEstado(estadoCancelado);
	        
	        // 4. GUARDAR: Aquí se dispara la auditoría automática
	        turnoDao.save(turno);
	    }
	    // IMPORTANTE: Ya NO usamos turnoDao.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public String[] buscarPlanillaActiva(long profId, long EspecId) {
		// TODO Auto-generated method stub
		return turnoDao.buscarPlanillaActiva(profId, EspecId);
	}

	// Muestra la Planilla Cabecera en Turnos por idProfesional y id_Especialidad.
	@Override
	@Transactional(readOnly = true)
	public List<Turno> findTurnosOcupadosByProfIdyEspId(Long profId, Long EspecId) {
		return turnoDao.findTurnosOcupadosByProfIdyEspId(profId, EspecId);
	}

	// Muestra la Planilla Cabecera en Turnos por idProfesional, id_Especialidad y
	// paciente_id.
	@Override
	@Transactional(readOnly = true)
	public List<Turno> findTurnoClickById(Long turnoId) {
		return turnoDao.findTurnoClickById(turnoId);
	}

	// buscar turnos ocupados fechas (vista: Cancelar Planillas)
	@Override
	@Transactional(readOnly = true)
	public List<Turno> buscarTurnosOcupadosFechas(Long profId, Long EspecId, Date fechaIni, Date fechaFin) {
		return turnoDao.buscarTurnosOcupadosFechas(profId, EspecId, fechaIni, fechaFin);
	}

	// buscar turnos ocupados fechas y horas (vista: Cancelar Planillas)
	@Override
	@Transactional(readOnly = true)
	public List<Turno> buscarTurnosOcupadosFechasHoras(Long profId, Long EspecId, Date fechaHoraIni,
			Date fechaHoraFin) {
		return turnoDao.buscarTurnosOcupadosFechasHoras(profId, EspecId, fechaHoraIni, fechaHoraFin);
	}

	// Eliminar "Turnos Ocupados" entre fechas (vista: Cancelar Planillas)
	@Override
	@Transactional
	public void desactivarSafeUpdates() {
		turnoDao.desactivarSafeUpdates();
	}

	// Eliminar "Turnos Ocupados" entre fechas (vista: Cancelar Planillas)
	@Override
	@Transactional
	public void eliminarTurnosOcupadosFechas(Long profId, Long EspecId, Date fechaIni, Date fechaFin) {
		turnoDao.eliminarTurnosOcupadosFechas(profId, EspecId, fechaIni, fechaFin);
	}

	// Eliminar "Turnos Ocupados" entre fechas y horas (vista: Cancelar Planillas)
	@Override
	@Transactional
	public void eliminarTurnosOcupadosFechasHoras(Long profId, Long EspecId, Date fechaHoraIni, Date fechaHoraFin) {
		turnoDao.eliminarTurnosOcupadosFechasHoras(profId, EspecId, fechaHoraIni, fechaHoraFin);
	}

	// SECCION REPORTES-----------------------------------

	@Override
	@Transactional(readOnly = true)
	public List<Turno> reporteTurnosOcupadosFechas(Long profId, Long EspecId, Date fechaIni, Date fechaFin) {
		return turnoDao.reporteTurnosOcupadosFechas(profId, EspecId, fechaIni, fechaFin);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Sobreturno> reporteSobreTurnosOcupadosFechas(Long profId, Long EspecId, Date fechaIni, Date fechaFin) {
		return turnoDao.reporteSobreTurnosOcupadosFechas(profId, EspecId, fechaIni, fechaFin);
	}

	// buscar paciente en Turnos Activos antes de eliminar.
	@Override
	@Transactional(readOnly = true)
	public List<Turno> pacientePoseeTurnosActivos(Long pacId) {
		return turnoDao.pacientePoseeTurnosActivos(pacId);
	}

	// SECCION REPORTES PARA PDF

	@Override
	@Transactional(readOnly = true)
	public List<Turno> reporteTurnosOcupadosFechasPdf(Long profId, Long EspecId, String fechaIni, String fechaFin) {
		return turnoDao.reporteTurnosOcupadosFechasPdf(profId, EspecId, fechaIni, fechaFin);
	}

}

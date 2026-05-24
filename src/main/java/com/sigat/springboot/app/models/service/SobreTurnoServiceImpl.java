package com.sigat.springboot.app.models.service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sigat.springboot.app.models.dao.IPacienteDao;
import com.sigat.springboot.app.models.dao.IProfesionalDao;
import com.sigat.springboot.app.models.dao.ISobreTurnosDao;
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
public class SobreTurnoServiceImpl implements ISobreTurnoService {

	@Autowired
	private ISobreTurnosDao sobreturnoDao;
	@Autowired
	private IProfesionalDao profesionalDao;
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
				sobreturno.getProfesional().getId(), sobreturno.getEspecialidad().getId(),
				sobreturno.getRangoFechaHora());

		if (yaExiste) {
			throw new RuntimeException("EL_HORARIO_YA_ESTA_OCUPADO");
		}

		// =========================================================================
		// 1.b ASIGNAMOS EL ESTADO "OCUPADO" (ID 4) <--- ESTO ES LO QUE FALTABA
		// =========================================================================
		Estado estadoOcupado = new Estado();
		estadoOcupado.setId(4L);
		sobreturno.setEstado(estadoOcupado);
		// =========================================================================

		// 2. Si pasa la validación, guardamos (Ahora ya tiene el ID 4 y no será NULL)
		sobreturnoDao.save(sobreturno);

		// 3. Completar objetos para el mail
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

	// PARA CANCELACION DE PLANILLAS NUEVO
	@Override
	@Transactional(readOnly = true)
	public List<Sobreturno> buscarSobreturnosParaBajaSoloFecha(Long profId, Long especId, String inicio, String fin) {
		return sobreturnoDao.buscarSobreturnosParaBajaSoloFecha(profId, especId, inicio, fin);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Sobreturno> buscarSobreturnosParaBajaFechaHora(Long profId, Long especId, LocalDateTime inicio,
			LocalDateTime fin) {
		return sobreturnoDao.buscarSobreturnosParaBajaFechaHora(profId, especId, inicio, fin);
	}

	@Override
	@Transactional
	public void eliminarSobreturnosOcupadosFechas(Long profId, Long especId, LocalDateTime inicio, LocalDateTime fin) {
		sobreturnoDao.eliminarSobreturnosOcupadosFechas(profId, especId, inicio, fin);
	}

	@Override
	@Transactional
	public void eliminarSobreturnosOcupadosFechasHoras(Long profId, Long especId, LocalDateTime inicio,
			LocalDateTime fin) {
		sobreturnoDao.eliminarSobreturnosOcupadosFechasHoras(profId, especId, inicio, fin);
	}

	@Override
	@Transactional
	public void cancelarSobreturnosMasivo(Long profId, Long especId, LocalDateTime inicio, LocalDateTime fin) {
		// Llama al método @Modifying del DAO para pasar a Estado 6
		sobreturnoDao.cancelarSobreturnosMasivo(profId, especId, inicio, fin);
	}

	// Para la selección manual por Checkbox (listarSobreturno)
	@Override
	@Transactional
	public void cancelarSobreturnosPorLista(List<Long> ids) {
		for (Long id : ids) {
			Sobreturno s = sobreturnoDao.findById(id).orElse(null);
			if (s != null) {
				Estado cancelado = new Estado();
				cancelado.setId(6L);
				s.setEstado(cancelado);
				sobreturnoDao.save(s);
			}
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<Sobreturno> findAllActivosYAtendidos() {
		return sobreturnoDao.findAllActivosYAtendidos();
	}

	// PARA MODULODE RESTAURACION (AUDITORIA)
	@Override
	@Transactional
	public String restaurarSobreturnoIndividual(Long id) {
		// 1. El Service usa el DAO para buscar (tal cual lo hablamos)
		Sobreturno sobreturno = sobreturnoDao.findById(id).orElse(null);

		if (sobreturno == null)
			return "Error: Sobreturno no encontrado.";

		try {
			// 2. Lógica de Negocio: Validar fecha
			Date fechaCita = java.sql.Timestamp.valueOf(sobreturno.getRangoFechaHora());
			Date ahora = new Date();

			if (fechaCita.before(ahora)) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
				return "No se puede restaurar: El horario (" + sdf.format(fechaCita) + ") ya pasó.";
			}

			// 3. Restauración: Pasar a Estado 4 (Ocupado)
			Estado ocupado = new Estado();
			ocupado.setId(4L);
			sobreturno.setEstado(ocupado);

			sobreturnoDao.save(sobreturno);
			return "OK";

		} catch (Exception e) {
			return "Error técnico: " + e.getMessage();
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Sobreturno> findCancelados(Long profId, Long especId, String desde, String hasta) {
	    java.util.Date fechaD = null;
	    java.util.Date fechaH = null;
	    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");

	    try {
	        if (desde != null && !desde.isEmpty()) {
	            fechaD = sdf.parse(desde);
	        }
	        if (hasta != null && !hasta.isEmpty()) {
	            fechaH = sdf.parse(hasta);
	            long unDiaEnMilis = (24 * 60 * 60 * 1000) - 1000;
	            fechaH = new java.util.Date(fechaH.getTime() + unDiaEnMilis);
	        }
	    } catch (Exception e) {
	        System.err.println("Error parseando fechas en Sobreturnos: " + e.getMessage());
	    }

	    return sobreturnoDao.findCanceladosConFiltro(profId, especId, fechaD, fechaH);
	}


	@Override
	@Transactional(readOnly = true)
	public List<Profesional> findAllProfesionales() {
		// Si tu DAO de profesionales se llama distinto, ajustalo (ej: profDao)
		return (List<Profesional>) profesionalDao.findAll();
	}
	
	//HISTORIA CLINICA
	@Override
	@Transactional(readOnly = true)
	public Sobreturno findById(Long id) {
	    // .orElse(null) es vital para que no explote si no lo encuentra
	    return sobreturnoDao.findById(id).orElse(null);
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

package com.sigat.springboot.app;

import java.util.Arrays;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import com.sigat.springboot.app.models.dao.*;
import com.sigat.springboot.app.models.entity.*;

@Component
public class DataInizializer implements CommandLineRunner {

	@Autowired private IUsuarioDao usuarioDao;
	@Autowired private IEstadoDao estadoDao;
	@Autowired private IDiaDao diaDao;
	@Autowired private IProfesionalDao profesionalDao;
	@Autowired private IEspecialidadDao especialidadDao;
	@Autowired private IVinculacionDao vinculacionDao;
	@Autowired private IPacienteDao pacienteDao; // Inyectamos el DAO de Pacientes
	@Autowired private BCryptPasswordEncoder passwordEncoder;

	@Override
	public void run(String... args) throws Exception {

		// 1. ESTADOS
		if (estadoDao.count() == 0) {
			String[] nombres = { "INACTIVO", "ACTIVO", "LIBRE", "OCUPADO", "ANULADO", "CANCELADO", "PARTICULAR", "EXPIRADO" };
			for (int i = 0; i < nombres.length; i++) {
				Estado e = new Estado();
				e.setId((long) (i + 1));
				e.setNombre(nombres[i]);
				estadoDao.save(e);
			}
		}

		// 2. DÍAS
		if (diaDao.count() == 0) {
			String[] nombresDias = { "DOMINGO", "LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO" };
			for (int i = 0; i < nombresDias.length; i++) {
				Dia d = new Dia();
				d.setId((long) (i + 1));
				d.setNombre(nombresDias[i]);
				diaDao.save(d);
			}
		}

		// 3. ESPECIALIDAD Y PROFESIONAL BASE
		if (profesionalDao.count() == 0) {
			Especialidad esp = new Especialidad();
			esp.setId(1L);
			esp.setEspecialidadCodigo("GIN");
			esp.setEspecialidadNombre("Ginecologia");
			especialidadDao.save(esp);

			Profesional prof = new Profesional();
			prof.setId(1L);
			prof.setNombre("Eduardo");
			prof.setApellido("Pellerano");
			prof.setEmail("edu@gmail.com");
			prof.setDni("8269979");
			prof.setTelefono("1149935694");
			prof.setCodigo("PEL");
			prof.setMatricula("MP 1234");
			prof.setCreateAt(new Date());
			profesionalDao.save(prof);

			Vinculacion vinc = new Vinculacion();
			vinc.setProfesional(prof);
			vinc.setEspecialidad(esp);
			vinc.setObservacion("VINCULACION AUTOMATICA");
			vinculacionDao.save(vinc);
		}

		// 4. PACIENTES (Los 6 originales del import)
		if (pacienteDao.count() == 0) {
			crearPaciente(1L, "Giordano", "Yanina Nadia", "gioryani@hotmail.com", "Luis Vernet 1745", "28011862", "55588");
			crearPaciente(2L, "Pellerano", "Maria Juliana", "majupelle@hotmail.com", "Luis Vernet 1741", "29546442", "55589");
			crearPaciente(3L, "Perez", "Mario Alberto", "mario@hotmail.com", "Mendoza 1256", "27011962", "55590");
			crearPaciente(4L, "Ruperto", "Jose Luis", "joserupe@hotmail.com", "Las Heras 2103", "21012362", "55591");
			crearPaciente(5L, "Loyola", "Raul Enrique", "raulich@hotmail.com", "Callao 1238", "29011112", "55592");
			crearPaciente(6L, "Mancilla", "Ernestina Juana", "ernetiju@hotmail.com", "Stoppler 9874", "29767331", "55593");
		}

		// 5. USUARIO ADMIN CON ROLES
		if (usuarioDao.findByUsername("admin") == null) {
			Usuario admin = new Usuario();
			admin.setUsername("admin");
			admin.setPassword(passwordEncoder.encode("12345"));
			admin.setEnabled(true);

			Role roleAdmin = new Role();
			roleAdmin.setAuthority("ROLE_ADMIN");
			Role roleUser = new Role();
			roleUser.setAuthority("ROLE_USER");

			admin.setRoles(Arrays.asList(roleAdmin, roleUser));
			usuarioDao.save(admin);
		}
		System.out.println(">>> DATA INICIAL CARGADA: Estados, Días, Médico vinculado, Pacientes y Admin.");
	}

	// Método auxiliar para Pacientes (evita errores de validación)
	private void crearPaciente(Long id, String ape, String nom, String mail, String dom, String dni, String socio) {
		Paciente p = new Paciente();
		p.setId(id);
		p.setApellido(ape);
		p.setNombre(nom);
		p.setEmail(mail);
		p.setDomicilio(dom);
		p.setDni(dni);
		p.setNumeroSocio(socio);
		p.setLocalidad("Grand Bourg");
		p.setTelefono("1166050039");
		p.setCreateAt(new Date());
		p.setEstado("Activo");
		pacienteDao.save(p);
	}
}

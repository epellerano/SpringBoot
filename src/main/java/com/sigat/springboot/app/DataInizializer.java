package com.sigat.springboot.app;

import java.util.Arrays;
import java.text.SimpleDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import com.sigat.springboot.app.models.dao.*;
import com.sigat.springboot.app.models.entity.*;
import com.sigat.springboot.app.models.service.IHistoriaClinicaService;

@Component
public class DataInizializer implements CommandLineRunner {

	@Autowired private IUsuarioDao usuarioDao;
	@Autowired private IEstadoDao estadoDao;
	@Autowired private IDiaDao diaDao;
	@Autowired private IProfesionalDao profesionalDao;
	@Autowired private IEspecialidadDao especialidadDao;
	@Autowired private IVinculacionDao vinculacionDao;
	@Autowired private IPacienteDao pacienteDao;
	@Autowired private IMovimientoDao movimientoDao;
	@Autowired	private IHistoriaClinicaService historiaService;
	@Autowired private BCryptPasswordEncoder passwordEncoder;

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
		@Override
		public void run(String... args) throws Exception {

		    // 1. ESTADOS
		    if (estadoDao.count() == 0) {
		    	String[] nombres = { "INACTIVO", "ACTIVO", "LIBRE", "OCUPADO", "ANULADO", "CANCELADO", "PARTICULAR", "EXPIRADO", "ATENDIDO", "EN SALA", "SE RETIRÓ" };
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

		    // 3. ESPECIALIDADES
		    if (especialidadDao.count() == 0) {
		        crearEspecialidad(1L, "GIN", "Ginecologia");
		        crearEspecialidad(2L, "DER", "Dermatologia");
		        crearEspecialidad(3L, "CLI", "Clinica Medica");
		        crearEspecialidad(4L, "KIN", "Kinesiologia");
		        crearEspecialidad(5L, "CAR", "Cardiologia");
		        crearEspecialidad(6L, "ORL", "Otorrino");
		        crearEspecialidad(7L, "ECO", "Ecografia");
		    }

		    // 4. PROFESIONALES (Los 27)
		    if (profesionalDao.count() == 0) {
		        crearProf(1L, "Eduardo", "Pellerano", "pellenorma@hotmail.com", "8269979", "1149935694", "PEL", "mp 1234", "1980-07-03");
		        crearProf(2L, "Maria", "Tallada", "talladamar@hotmail.com", "8111333", "1154253697", "TAL", "mp 2345", "1980-07-03");
		        crearProf(3L, "Analia", "Torren", "pellenorma@hotmail.com", "9563221", "1149935694", "ANA", "mp 3456", "1980-07-03");
		        crearProf(4L, "Pedro", "Baldez", "talladamar@hotmail.com", "8253669", "1154253697", "PED", "mp 4567", "1980-07-03");
		        crearProf(5L, "Esteban", "Manson", "pellenorma@hotmail.com", "9563225", "1149935694", "EST", "mp 5678", "1980-07-03");
		        crearProf(6L, "Luis", "Pontoriero", "talladamar@hotmail.com", "8965412", "1154253697", "LUI", "mp 6789", "1980-07-03");
		        crearProf(7L, "Saul", "Poison", "pellenorma@hotmail.com", "8505709", "1149935694", "SAU", "mp 7890", "1980-07-03");
		        crearProf(8L, "Rita", "Ridruejo", "talladamar@hotmail.com", "9223101", "1154253697", "RIT", "mp 7891", "1980-07-03");
		        crearProf(9L, "Juan", "Gonzalez", "pellenorma@hotmail.com", "9566777", "1149935694", "JUA", "mp 7892", "1980-07-03");
		        crearProf(10L, "Lucas", "Hanson", "talladamar@hotmail.com", "8509731", "1154253697", "LUC", "mp 7893", "1980-07-03");
		        crearProf(11L, "Anahi", "Altiery", "pellenorma@hotmail.com", "8989500", "1149935694", "ANH", "mp 7894", "1980-07-03");
		        crearProf(12L, "Solange", "Caseres", "talladamar@hotmail.com", "8111233", "1154253697", "SOL", "mp 7895", "1980-07-03");
		        crearProf(13L, "Bruno", "Mongodb", "pellenorma@hotmail.com", "8555362", "1149935694", "BRU", "mp 7896", "1980-07-03");
		        crearProf(14L, "Casandra", "Eclipse", "talladamar@hotmail.com", "7569997", "1154253697", "CAS", "mp 7897", "1980-07-03");
		        crearProf(15L, "Florencia", "Django", "pellenorma@hotmail.com", "9632555", "1149935694", "FLO", "mp 7898", "1980-07-03");
		        crearProf(16L, "Victoria", "Olivieri", "talladamar@hotmail.com", "8365587", "1154253697", "VIC", "mp 7899", "1980-07-03");
		        crearProf(17L, "Isabela", "Cardozo", "pellenorma@hotmail.com", "8526999", "1149935694", "ISA", "mp 7810", "1980-07-03");
		        crearProf(18L, "Maira", "Sultanes", "talladamar@hotmail.com", "31263325", "1154253697", "MAI", "mp 7811", "1980-07-03");
		        crearProf(19L, "Claudia", "Belgrano", "pellenorma@hotmail.com", "20563222", "1149935694", "CLA", "mp 7812", "1980-07-03");
		        crearProf(20L, "Mirna", "Posadas", "talladamar@hotmail.com", "29854111", "1154253697", "MIR", "mp 7813", "1980-07-03");
		        crearProf(21L, "Margarita", "Lijera", "pellenorma@hotmail.com", "25213002", "1149935694", "MAR", "mp 7814", "1980-07-03");
		        crearProf(22L, "Juliana", "Monzon", "talladamar@hotmail.com", "28521423", "1154253697", "JUL", "mp 7815", "1980-07-03");
		        crearProf(23L, "Ernestina", "Reuteman", "pellenorma@hotmail.com", "23569874", "1149935694", "ERN", "mp 7816", "1980-07-03");
		        crearProf(24L, "Thamara", "Junino", "talladamar@hotmail.com", "24653987", "1154253697", "THA", "mp 7817", "1980-07-03");
		        crearProf(25L, "Monica", "Tapia", "pellenorma@hotmail.com", "25269079", "1149935694", "MON", "mp 7818", "1980-07-03");
		        crearProf(26L, "Cintia", "Manetti", "pellenorma@hotmail.com", "8569279", "1149935694", "CIN", "mp 7819", "1980-07-03");
		        crearProf(27L, "Ludmila", "Luquetti", "pellenorma@hotmail.com", "8289974", "1149935694", "LUD", "mp 7820", "1980-07-03");
		    }

		    // 5. VINCULACIONES
		    if (vinculacionDao.count() == 0) {
		        String obsPelle = "PELLE ATIENDE A PACIENTES SOLO CON TURNOS / DIU $6000 / PAP Y COLSPO $20.000 / SABADOS NO TRABAJA";
		        String obsTallada = "TALLADA ATIENDE A PACIENTES SOLO CON TURNOS / DIU $6000 / PAP Y COLSPO $20.000 / SABADOS NO TRABAJA";
		        String obsEdu = "EDU ATIENDE A PACIENTES SOLO CON TURNOS / DIU $6000 / PAP Y COLSPO $20.000 / SABADOS NO TRABAJA";

		        crearVinc(1L, 1L, 1L, obsPelle);    
		        crearVinc(2L, 3L, 2L, obsTallada);  
		        crearVinc(3L, 3L, 1L, obsEdu);      
		    }

		    // 6. PACIENTES
		    if (pacienteDao.count() == 0) {
		        crearPaciente(1L, "Giordano", "Yanina Nadia", "gioryani@hotmail.com", "Luis Vernet 1745", "Grand Bourg", "1166050039", "55588", "28011862", "1980-07-03");
		        crearPaciente(2L, "Pellerano", "Maria Juliana", "majupelle@hotmail.com", "Luis Vernet 1741", "Grand Bourg", "11645656677", "55589", "29546442", "1986-03-01");
		        crearPaciente(3L, "Perez", "Mario Alberto", "mario@hotmail.com", "Mendoza 1256", "Tortuguitas", "1123566698", "55590", "27011962", "1976-05-11");
		        crearPaciente(4L, "Ruperto", "Jose Luis", "joserupe@hotmail.com", "Las Heras 2103", "Pablo Nogues", "1185253697", "55591", "21012362", "1967-10-10");
		        crearPaciente(5L, "Loyola", "Raul Enrique", "raulich@hotmail.com", "Callao 1238", "San Miguel", "1125068798", "55592", "29011112", "1974-12-09");
		        crearPaciente(6L, "Mancilla", "Ernestina Juana", "ernetiju@hotmail.com", "Stoppler 9874", "Tierras altas", "1195623335", "55593", "29767331", "1947-05-03");
		    }

		    // 7. MOVIMIENTOS
		    if (movimientoDao.count() == 0) {
		        Movimiento m = new Movimiento();
		        m.setId(1L);
		        m.setProfesional(profesionalDao.findById(1L).orElse(null));
		        m.setEspecialidad(especialidadDao.findById(1L).orElse(null));
		        m.setCreateAt(sdf.parse("2024-04-16"));
		        m.setHoraIniManana("09:00");
		        m.setHoraFinManana("12:00");
		        m.setTotalManana("03:00");
		        m.setHoraIniTarde("16:00");
		        m.setHoraFinTarde("20:00");
		        m.setTotalTarde("04:00");
		        m.setTotalGeneral("07:00");
		        movimientoDao.save(m);
		    }

		    // 8. USUARIO ADMIN
		    if (usuarioDao.findByUsername("admin") == null) {
		        Usuario admin = new Usuario();
		        admin.setUsername("admin");
		        admin.setPassword(passwordEncoder.encode("12345"));
		        admin.setEnabled(true);
		        
		        Role rA = new Role(); 
		        rA.setAuthority("ROLE_ADMIN"); 
		        rA.setCreadoPor("Setup"); // <--- Cambiado de setCreatedBy a setCreadoPor

		        Role rU = new Role(); 
		        rU.setAuthority("ROLE_USER"); 
		        rU.setCreadoPor("Setup"); // <--- Cambiado de setCreatedBy a setCreadoPor

		        
		        admin.setRoles(Arrays.asList(rA, rU));
		        usuarioDao.save(admin);
		    }

		    // 9. USUARIO MÉDICO (Para probar Historia Clínica)
		    if (usuarioDao.findByUsername("pelle") == null) {
		        Usuario medico = new Usuario();
		        medico.setUsername("pelle");
		        medico.setPassword(passwordEncoder.encode("12345"));
		        medico.setEnabled(true);

		        Role rM = new Role();
		        rM.setAuthority("ROLE_MEDICO"); // Texto exacto que pide SecurityConfig
		        rM.setCreadoPor("Setup");

		        Role rU = new Role();
		        rU.setAuthority("ROLE_USER");
		        rU.setCreadoPor("Setup");

		        medico.setRoles(Arrays.asList(rM, rU));
		        usuarioDao.save(medico);
		    }
		    
		 // 10. USUARIO PACIENTE DE PRUEBA (Inicializa el nuevo rol exclusivo)
		    if (usuarioDao.findByUsername("pacienteprueba") == null) {
		        Usuario pacienteTest = new Usuario();
		        pacienteTest.setUsername("pacienteprueba");
		        pacienteTest.setPassword(passwordEncoder.encode("12345"));
		        pacienteTest.setEnabled(true);

		        Role rP = new Role();
		        rP.setAuthority("ROLE_PACIENTE"); // <-- Nace tu nuevo rol exclusivo para autogestión
		        rP.setCreadoPor("Setup");

		        pacienteTest.setRoles(Arrays.asList(rP));
		        usuarioDao.save(pacienteTest);
		    }
		    
		 // 11. HISTORIAS CLÍNICAS DE PRUEBA (Para Yanina Giordano - Paciente ID 1)
		 // Solo se cargan si Yanina no tiene historial previo en la base
		 if (historiaService.obtenerHistorialPorPaciente(1L).isEmpty()) {
		     
		     Paciente yanina = pacienteDao.findById(1L).orElse(null);
		     Profesional pellerano = profesionalDao.findById(1L).orElse(null);

		     if (yanina != null && pellerano != null) {
		         
		         // --- EVOLUCIÓN 1: CONTROL ANUAL (La más antigua) ---
		         HistoriaClinica h1 = new HistoriaClinica();
		         h1.setPaciente(yanina);
		         h1.setProfesional(pellerano);
		         h1.setAlergias("PENICILINA (Reacción alérgica severa)");
		         h1.setAntecedentes("Asma infantil controlada. Cesárea en 2015.");
		         h1.setMotivoConsulta("Control ginecológico de rutina.");
		         h1.setExamenFisico("TA: 110/70. Peso: 62kg. Temp: 36.4. Palpación mamaria sin hallazgos.");
		         h1.setEvolucionClinica("Paciente refiere estar asintomática. Ciclos regulares. Se realiza toma de PAP y Colposcopía.");
		         h1.setDiagnostico("Examen médico preventivo (Z00.0)");
		         h1.setIndicaciones("Se solicitan estudios: Laboratorio completo y ecografía transvaginal.");
		         historiaService.guardarHistoria(h1);

					/*
					 * // --- EVOLUCIÓN 2: CUADRO RESPIRATORIO --- HistoriaClinica h2 = new
					 * HistoriaClinica(); h2.setPaciente(yanina); h2.setProfesional(pellerano);
					 * h2.setAlergias("PENICILINA (Reacción alérgica severa)");
					 * h2.setAntecedentes("Asma infantil controlada. Cesárea en 2015.");
					 * h2.setMotivoConsulta("Fiebre de 38.5 y dolor de garganta de 48hs."); h2.
					 * setExamenFisico("Fauces congestivas, amígdalas con placas pultáceas. Auscultación normal."
					 * ); h2.
					 * setEvolucionClinica("Se observa cuadro de amigdalitis bacteriana. Paciente refiere dificultad para deglutir."
					 * ); h2.setDiagnostico("Amigdalitis pultácea aguda (J03.9)"); h2.
					 * setIndicaciones("Claritromicina 500mg (1 cada 12hs x 7 días). Ibuprofeno 600mg ante dolor."
					 * ); historiaService.guardarHistoria(h2);
					 * 
					 * // --- EVOLUCIÓN 3: ALTA CLÍNICA (La más reciente) --- HistoriaClinica h3 =
					 * new HistoriaClinica(); h3.setPaciente(yanina); h3.setProfesional(pellerano);
					 * h3.setAlergias("PENICILINA (Reacción alérgica severa)");
					 * h3.setAntecedentes("Asma infantil controlada. Cesárea en 2015.");
					 * h3.setMotivoConsulta("Control evolutivo post tratamiento antibiótico.");
					 * h3.setExamenFisico("Apirética. Fauces normales. Sin adenopatías cervicales."
					 * ); h3.
					 * setEvolucionClinica("Paciente refiere mejoría clínica completa. Finalizó esquema de 7 días."
					 * ); h3.setDiagnostico("Alta médica - Resolución de cuadro infeccioso."); h3.
					 * setIndicaciones("Continuar con vida normal. Control ginecológico pendiente para retirar PAP."
					 * ); historiaService.guardarHistoria(h3);
					 * 
					 * System.out.
					 * println(">>> SIGAT: Historias Clínicas de prueba creadas para Yanina Giordano."
					 * );
					 */
		     }
		 }

		}

		private void crearEspecialidad(Long id, String cod, String nom) {
		    Especialidad e = new Especialidad();
		    e.setId(id);
		    e.setEspecialidadCodigo(cod);
		    e.setEspecialidadNombre(nom);
		    especialidadDao.save(e);
		}

		private void crearProf(Long id, String nom, String ape, String mail, String dni, String tel, String cod, String mat, String fecha) throws Exception {
		    Profesional p = new Profesional();
		    p.setId(id); p.setNombre(nom); p.setApellido(ape); p.setEmail(mail);
		    p.setDni(dni); p.setTelefono(tel); p.setCodigo(cod); p.setMatricula(mat);
		    p.setCreateAt(sdf.parse(fecha));
		    profesionalDao.save(p);
		}

		private void crearVinc(Long id, Long espId, Long profId, String obs) {
		    Vinculacion v = new Vinculacion();
		    v.setId(id);
		    v.setEspecialidad(especialidadDao.findById(espId).orElse(null));
		    v.setProfesional(profesionalDao.findById(profId).orElse(null));
		    v.setObservacion(obs);
		    vinculacionDao.save(v);
		}

		private void crearPaciente(Long id, String ape, String nom, String mail, String dom, String loc, String tel, String socio, String dni, String fecha) throws Exception {
		    Paciente p = new Paciente();
		    p.setId(id); p.setApellido(ape); p.setNombre(nom); p.setEmail(mail);
		    p.setDomicilio(dom); p.setLocalidad(loc); p.setTelefono(tel);
		    p.setNumeroSocio(socio); p.setDni(dni); p.setEstado("Activo");
		    p.setCreateAt(sdf.parse(fecha));
		    pacienteDao.save(p);
		}
}



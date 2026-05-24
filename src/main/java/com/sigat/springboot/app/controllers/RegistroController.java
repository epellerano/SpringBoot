package com.sigat.springboot.app.controllers;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sigat.springboot.app.models.entity.Paciente;
import com.sigat.springboot.app.models.entity.Profesional;
import com.sigat.springboot.app.models.entity.Role;
import com.sigat.springboot.app.models.entity.Usuario;
import com.sigat.springboot.app.models.service.EmailService;
import com.sigat.springboot.app.models.service.IPacienteService;
import com.sigat.springboot.app.models.service.IProfesionalService;
import com.sigat.springboot.app.models.service.IUsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/registro")
public class RegistroController {

    @Autowired
    private IPacienteService pacienteService;

    @Autowired
    private IProfesionalService profesionalService;

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private EmailService emailService;

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        return "registro";
    }

    // =========================================================================
    // PASO 1: VALIDAR REQUISITOS INSTITUCIONALES Y ENVIAR MAIL CON CAJA CELESTE
    // =========================================================================
    @PostMapping("/solicitar-codigo")
    @ResponseBody 
    public ResponseEntity<?> solicitarCodigo(
            @RequestParam String email, @RequestParam String dni, 
            @RequestParam String username, @RequestParam String password, 
            HttpSession session) {
        
        try {
            // 1. Tus validaciones nativas de formato (Email y Clave Robusta)
            if (!email.contains("@") || !email.contains(".")) {
                return ResponseEntity.badRequest().body("Por favor, ingresa un email válido.");
            }

            if (!password.matches("^(?=.*[A-Z]).*[!@#$%^&*]$")) {
                return ResponseEntity.badRequest().body("La clave debe tener una Mayúscula y terminar con un signo.");
            }
            
            // 2. Comprobamos duplicados en tu tabla global de Usuarios
            if (usuarioService.findByUsername(username.trim()) != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("El nombre de usuario ya está ocupado.");
            }

            // 3. REGLA EXCLUSIVA MÉDICOS: Si el DNI es de un médico, DEBE estar precargado
            Profesional profesional = profesionalService.findByProfesionalDni(dni.trim()); 
            
            // 4. GENERACIÓN DEL TOKEN OTP DE 6 DÍGITOS
            String codigoOtp = String.valueOf((int)(Math.random() * 900000) + 100000);

            // Guardamos transitoriamente los datos en la sesión segura de Spring
            session.setAttribute("otp_codigo", codigoOtp);
            session.setAttribute("otp_email", email.trim());
            session.setAttribute("otp_timestamp", System.currentTimeMillis());

            // 5. Despachamos el correo elegante con el box celeste clínico
            emailService.enviarCodigoRegistro(email.trim(), codigoOtp);

            return ResponseEntity.ok().body("{\"status\":\"success\"}");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error en el servidor: " + e.getMessage());
        }
    }

    // =========================================================================
    // PASO 2: VERIFICAR EL TOKEN E INSERTAR DE FORMA INTELIGENTE SIN DUPLICAR
    // =========================================================================
    @PostMapping("/verificar-y-guardar")
    @ResponseBody 
    public ResponseEntity<?> verificarYGuardar(
            @RequestParam String email, @RequestParam String dni, 
            @RequestParam String username, @RequestParam String password, 
            @RequestParam String codigo, HttpSession session) {

        try {
            String sessionCodigo = (String) session.getAttribute("otp_codigo");
            String sessionEmail = (String) session.getAttribute("otp_email");
            Long sessionTimestamp = (Long) session.getAttribute("otp_timestamp");

            if (sessionCodigo == null || sessionEmail == null || sessionTimestamp == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Solicitud vencida o no encontrada.");
            }

            if (!sessionCodigo.equals(codigo.trim()) || !sessionEmail.equalsIgnoreCase(email.trim())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Código de verificación incorrecto.");
            }

            if (System.currentTimeMillis() - sessionTimestamp > 15 * 60 * 1000) {
                session.invalidate();
                return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body("El código ha expirado.");
            }

            // --- FLUJO DE ASIGNACIÓN DE ROLES SIN DUPLICADOS EN BASE DE DATOS ---
            
            // CASO A: SI ES MÉDICO (Usa tu regla de negocio inamovible de antemano)
            Profesional profesional = profesionalService.findByProfesionalDni(dni.trim()); 
            if (profesional != null) {
                saveUser(username.trim(), password, profesional, null, "ROLE_MEDICO");
                session.invalidate();
                return ResponseEntity.ok().body("{\"status\":\"success\"}");
            }

            // CASO B: SI ES PACIENTE (Sincronización Inteligente)
            // Buscamos si la secretaria ya le creó la ficha física en el mostrador antes
            List<Paciente> listaPacientes = pacienteService.findByDni(dni.trim());
            Paciente pacienteExistente = (listaPacientes != null && !listaPacientes.isEmpty()) ? listaPacientes.get(0) : null;

            if (pacienteExistente != null) {
                // ESCENARIO 1: Ya existe la ficha de la secretaria. SÓLO le creamos el usuario asignándole ROLE_PACIENTE
                saveUser(username.trim(), password, null, pacienteExistente, "ROLE_PACIENTE");
                session.invalidate();
                return ResponseEntity.ok().body("{\"status\":\"success\"}");
            } else {
                // ESCENARIO 2: Es un paciente 100% nuevo de internet. Fabricamos la ficha física de cero en MySQL
                Paciente nuevoPaciente = new Paciente();
                nuevoPaciente.setDni(dni.trim());
                nuevoPaciente.setEmail(email.toLowerCase().trim());
                nuevoPaciente.setNumeroSocio("PARTICULAR"); 
                nuevoPaciente.setFoto("");
                nuevoPaciente.setApellido(username.toUpperCase().trim()); 
                nuevoPaciente.setNombre("NUEVO PACIENTE");
                nuevoPaciente.setCreateAt(new java.util.Date());
                
                // Rellenos VARCHAR exigidos por tus @NotEmpty de Hibernate
                nuevoPaciente.setDomicilio("PARTICULAR");
                nuevoPaciente.setTelefono("PARTICULAR");
                nuevoPaciente.setEstado("ACTIVO");
                nuevoPaciente.setLocalidad("PARTICULAR");

                // Guardamos de forma oficial la ficha del paciente
                pacienteService.save(nuevoPaciente);

                // Volvemos a morder la base de datos para recuperar el ID real autogenerado por MySQL
                List<Paciente> listaNuevos = pacienteService.findByDni(dni.trim());
                Paciente pacienteRecienGuardado = (listaNuevos != null && !listaNuevos.isEmpty()) ? listaNuevos.get(0) : null;

                if (pacienteRecienGuardado != null) {
                    // Creamos el usuario de acceso conectándolo al nuevo ROLE_PACIENTE
                    saveUser(username.trim(), password, null, pacienteRecienGuardado, "ROLE_PACIENTE");
                    session.invalidate();
                    return ResponseEntity.ok().body("{\"status\":\"success\"}");
                }
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error en el procesamiento de la ficha.");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    // TU HELPER PRIVADO DE CONFIANZA TOTALMENTE INTACTO
    private void saveUser(String user, String pass, Profesional prof, Paciente paci, String roleName) {
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsername(user);
        nuevoUsuario.setPassword(passwordEncoder.encode(pass));
        nuevoUsuario.setEnabled(true);
        
        nuevoUsuario.setProfesional(prof); 
        nuevoUsuario.setPaciente(paci);

        Role role = new Role();
        role.setAuthority(roleName);
        nuevoUsuario.setRoles(Arrays.asList(role));

        usuarioService.save(nuevoUsuario);
    }
}


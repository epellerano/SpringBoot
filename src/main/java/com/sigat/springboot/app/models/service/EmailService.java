package com.sigat.springboot.app.models.service;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.sigat.springboot.app.models.entity.Sobreturno;
import com.sigat.springboot.app.models.entity.Turno;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
	
	// 1. Inyectamos los valores del properties
    @Value("${spring.mail.properties.direccion}")
    private String direccionClinica;

    @Value("${spring.mail.properties.telefono}")
    private String telefonoClinica;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private TemplateEngine templateEngine; // Asegúrate de que sea org.thymeleaf.ITemplateEngine

	@Async
	public void enviarMailConfirmacion(Turno turno) {
	    try {
	        // 1. Verificación de seguridad para evitar errores de fecha
	        if (turno.getPlanilladetalle() == null || turno.getPlanilladetalle().getRangoFechaHora() == null) {
	            System.err.println("No se puede enviar mail: El turno no tiene fecha definida.");
	            return;
	        }

	        MimeMessage message = mailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

	        // 2. Cargamos el Contexto de Thymeleaf con todas las variables del HTML
	        Context context = new Context();
	        
	        // Formateo de Fecha y Hora (Día abreviado en español)
	        LocalDateTime dateTime = turno.getPlanilladetalle().getRangoFechaHora()
	                .toInstant()
	                .atZone(ZoneId.systemDefault())
	                .toLocalDateTime();

	        String fechaFormateada = dateTime.format(DateTimeFormatter.ofPattern("EEE dd/MM/yyyy", new Locale("es", "ES")));
	        String horaFormateada = dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
	        
	        // Datos del Paciente (Apellido y Nombre)
	        context.setVariable("pacienteNombre", turno.getPaciente().getApellido() + " " + turno.getPaciente().getNombre());	        
	        // Datos del Médico (Apellido y Nombre)
	        context.setVariable("medicoNombre", turno.getProfesional().getApellido() + " " + turno.getProfesional().getNombre());	        
	        // Especialidad
	        context.setVariable("especialidad", turno.getEspecialidad().getEspecialidadNombre());	   
	        context.setVariable("fecha", fechaFormateada);
	        context.setVariable("hora", horaFormateada);
	        // 4. Datos de la Clínica (Desde application.properties)
	        context.setVariable("direccion", direccionClinica);
	        context.setVariable("telefono", telefonoClinica);
	        // TIPO
	        context.setVariable("tipo", "TURNO NORMAL");
	        
	        // 3. Procesar el Template (Asegúrate de que la ruta sea correcta)
	        String html = templateEngine.process("mails/email-turno", context);

	        // 4. Configuración del envío
	        helper.setTo(turno.getPaciente().getEmail());
	        helper.setFrom("info@cosaludcentromedico.com", "Centro Médico Cosalud");
	        helper.setSubject("Confirmación de Turno Médico - Centro Médico Cosalud");
	        helper.setText(html, true);

	        mailSender.send(message);
	        System.out.println("Email de confirmación enviado con éxito a: " + turno.getPaciente().getEmail());

	    } catch (Exception e) {
	        System.err.println("Error enviando mail: " + e.getMessage());
	        e.printStackTrace();
	    }
	}
	
	@Async
	public void enviarMailConfirmacionSobreturno(Sobreturno sobreturno) {
	    try {
	        // 1. VALIDACIÓN DE SEGURIDAD
	        if (sobreturno.getRangoFechaHora() == null) {
	            System.err.println("No se puede enviar mail: El sobreturno no tiene fecha definida.");
	            return; 
	        }

	        MimeMessage message = mailSender.createMimeMessage(); 
	        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

	        

	        // 2. Preparamos el Contexto de Thymeleaf
	        Context context = new Context();
	        
	        //Obtenemos el LocalDateTime del objeto
	        LocalDateTime dateTime = sobreturno.getRangoFechaHora(); 
	        // Definimos los formateadores con el Locale en español para el día abreviado
	        DateTimeFormatter fmtFecha = DateTimeFormatter.ofPattern("EEE dd/MM/yyyy", new Locale("es", "ES"));
	        DateTimeFormatter fmtHora = DateTimeFormatter.ofPattern("HH:mm");
	        // Formateamos directamente el LocalDateTime
	        String fechaFormateada = dateTime.format(fmtFecha);
	        String horaFormateada = dateTime.format(fmtHora);
	        
	        context.setVariable("pacienteNombre", sobreturno.getPaciente().getApellido() +  " " + sobreturno.getPaciente().getNombre());
	        context.setVariable("medicoNombre", sobreturno.getProfesional().getApellido() + " " + sobreturno.getProfesional().getNombre());
	        context.setVariable("especialidad", sobreturno.getEspecialidad().getEspecialidadNombre());
	        context.setVariable("fecha", fechaFormateada);
	        context.setVariable("hora", horaFormateada);
	        
	        // Pasamos las variables globales de la clínica (asegúrate que estén inyectadas con @Value arriba)
	        context.setVariable("direccion", direccionClinica);
	        context.setVariable("telefono", telefonoClinica);
	        // TIPO
	        context.setVariable("tipo", "SOBRETURNO");

	        // 3. Procesar el Template (Asegúrate de que la ruta sea correcta)
	        String html = templateEngine.process("mails/email-sobreturno", context);

	        // 4. Configuración del envío
	        helper.setTo(sobreturno.getPaciente().getEmail());
	        helper.setSubject("Confirmación de Sobreturno Médico - Centro Medico Cosalud");
	        helper.setFrom("info@cosaludcentromedico.com", "Centro Medico Cosalud"); 
	        helper.setText(html, true);

	        mailSender.send(message);
	        System.out.println("Email de sobreturno enviado con éxito a: " + sobreturno.getPaciente().getEmail());
	        
	    } catch (Exception e) { 
	        System.err.println("Error enviando mail de sobreturno: " + e.getMessage());
	        e.printStackTrace(); 
	    }
	}

}

package com.sigat.springboot.app.models.service;

import java.io.ByteArrayOutputStream;
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

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.sigat.springboot.app.models.entity.Sobreturno;
import com.sigat.springboot.app.models.entity.Turno;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

	@Value("${spring.mail.properties.direccion}")
	private String direccionClinica;

	@Value("${spring.mail.properties.telefono}")
	private String telefonoClinica;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private TemplateEngine templateEngine;

	@Async
	public void enviarMailConfirmacion(Turno turno) {
		try {
			if (turno.getPlanilladetalle() == null || turno.getPlanilladetalle().getRangoFechaHora() == null) {
				System.err.println("No se puede enviar mail: El turno no tiene fecha definida.");
				return;
			}

			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			Context context = new Context();

			LocalDateTime dateTime = turno.getPlanilladetalle().getRangoFechaHora().toInstant()
					.atZone(ZoneId.systemDefault()).toLocalDateTime();

			// VARIABLES ENVIADAS AL HTML
			context.setVariable("pacienteNombre",
					turno.getPaciente().getApellido() + " " + turno.getPaciente().getNombre());
			context.setVariable("pacienteDni", turno.getPaciente().getDni()); // <-- LÍNEA CRÍTICA AGREGADA
			context.setVariable("medicoNombre",
					turno.getProfesional().getApellido() + " " + turno.getProfesional().getNombre());
			context.setVariable("especialidad", turno.getEspecialidad().getEspecialidadNombre());
			context.setVariable("fecha",
					dateTime.format(DateTimeFormatter.ofPattern("EEE dd/MM/yyyy", new Locale("es", "ES"))));
			context.setVariable("hora", dateTime.format(DateTimeFormatter.ofPattern("HH:mm")));
			context.setVariable("direccion", direccionClinica);
			context.setVariable("telefono", telefonoClinica);
			context.setVariable("tipo", "TURNO NORMAL");

			String html = templateEngine.process("mails/email-turno", context);

			helper.setTo(turno.getPaciente().getEmail());
			helper.setFrom("info@cosaludcentromedico.com", "Centro Médico Cosalud");
			helper.setSubject("Confirmación de Turno Médico - Centro Médico Cosalud");
			helper.setText(html, true);

			// Inyección del QR
			byte[] qrImagen = generarQR(turno.getPaciente().getDni());
			helper.addInline("qrCode", new org.springframework.core.io.ByteArrayResource(qrImagen), "image/png");

			mailSender.send(message);
			System.out.println("Email TURNO enviado con DNI: " + turno.getPaciente().getDni());

		} catch (Exception e) {
			System.err.println("Error enviando mail turno: " + e.getMessage());
		}
	}

	@Async
	public void enviarMailConfirmacionSobreturno(Sobreturno sobreturno) {
		try {
			if (sobreturno.getRangoFechaHora() == null) {
				System.err.println("No se puede enviar mail: El sobreturno no tiene fecha definida.");
				return;
			}

			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			Context context = new Context();

			LocalDateTime dateTime = sobreturno.getRangoFechaHora();

			// VARIABLES ENVIADAS AL HTML
			context.setVariable("pacienteNombre",
					sobreturno.getPaciente().getApellido() + " " + sobreturno.getPaciente().getNombre());

			// --- INYECCIÓN CRÍTICA PARA EL PUNTO D ---
			context.setVariable("pacienteDni", sobreturno.getPaciente().getDni());

			context.setVariable("medicoNombre",
					sobreturno.getProfesional().getApellido() + " " + sobreturno.getProfesional().getNombre());
			context.setVariable("especialidad", sobreturno.getEspecialidad().getEspecialidadNombre());
			context.setVariable("fecha",
					dateTime.format(DateTimeFormatter.ofPattern("EEE dd/MM/yyyy", new Locale("es", "ES"))));
			context.setVariable("hora", dateTime.format(DateTimeFormatter.ofPattern("HH:mm")));
			context.setVariable("direccion", direccionClinica);
			context.setVariable("telefono", telefonoClinica);
			context.setVariable("tipo", "SOBRETURNO");

			String html = templateEngine.process("mails/email-sobreturno", context);

			helper.setTo(sobreturno.getPaciente().getEmail());
			helper.setFrom("info@cosaludcentromedico.com", "Centro Medico Cosalud");
			helper.setSubject("Confirmación de Sobreturno Médico - Centro Medico Cosalud");
			helper.setText(html, true);

			// --- INYECCIÓN DE QR LOCAL ---
			byte[] qrImagen = generarQR(sobreturno.getPaciente().getDni());
			helper.addInline("qrCode", new org.springframework.core.io.ByteArrayResource(qrImagen), "image/png");

			mailSender.send(message);
			System.out.println("Email de sobreturno enviado con DNI: " + sobreturno.getPaciente().getDni());

		} catch (Exception e) {
			System.err.println("Error enviando mail de sobreturno: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Async
	public void enviarMailRetiroVoluntario(Turno turno) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			Context context = new Context();
			context.setVariable("pacienteNombre",
					turno.getPaciente().getApellido() + " " + turno.getPaciente().getNombre());
			context.setVariable("medicoNombre", turno.getProfesional().getApellido());
			context.setVariable("direccion", direccionClinica);
			context.setVariable("telefono", telefonoClinica);

			// Fecha y hora del momento exacto del retiro
			String fechaActual = new java.text.SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date());
			String horaActual = new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date());
			context.setVariable("fecha", fechaActual);
			context.setVariable("hora", horaActual);

			// Procesamos la nueva plantilla
			String html = templateEngine.process("mails/email-retiro", context);

			helper.setTo(turno.getPaciente().getEmail());
			helper.setFrom("info@cosaludcentromedico.com", "Centro Médico Cosalud");
			helper.setSubject("Constancia de Retiro de Clínica - Cosalud");
			helper.setText(html, true);

			mailSender.send(message);
			System.out.println("Email de retiro con diseño enviado a: " + turno.getPaciente().getEmail());

		} catch (Exception e) {
			System.err.println("Error enviando mail de retiro: " + e.getMessage());
		}
	}

	/* MÉTODO HERMANO CREADO PARA SOBRETURNOS */
	@Async
	public void enviarMailRetiroVoluntario(Sobreturno sobreturno) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			Context context = new Context();
			// Mapeamos los datos desde el objeto Sobreturno de forma exacta
			context.setVariable("pacienteNombre",
					sobreturno.getPaciente().getApellido() + " " + sobreturno.getPaciente().getNombre());
			context.setVariable("medicoNombre", sobreturno.getProfesional().getApellido());
			context.setVariable("direccion", direccionClinica);
			context.setVariable("telefono", telefonoClinica);

			String fechaActual = new java.text.SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date());
			String horaActual = new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date());
			context.setVariable("fecha", fechaActual);
			context.setVariable("hora", horaActual);

			// Reutilizamos el mismo archivo HTML (email-retiro.html) para mantener la
			// estética uniforme
			String html = templateEngine.process("mails/email-retiro", context);

			helper.setTo(sobreturno.getPaciente().getEmail());
			helper.setFrom("info@cosaludcentromedico.com", "Centro Médico Cosalud");
			helper.setSubject("Constancia de Retiro de Clínica - Cosalud");
			helper.setText(html, true);

			mailSender.send(message);
			System.out.println(
					"Email de retiro con diseño enviado para SOBRETURNO a: " + sobreturno.getPaciente().getEmail());

		} catch (Exception e) {
			System.err.println("Error enviando mail de retiro para sobreturno: " + e.getMessage());
		}
	}

//metodo para generar el QR
	private byte[] generarQR(String texto) throws Exception {
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		BitMatrix bitMatrix = qrCodeWriter.encode(texto, BarcodeFormat.QR_CODE, 200, 200);
		ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
		MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
		return pngOutputStream.toByteArray();
	}

	// codigo verificador de mails al paciente
	// =========================================================================
	// 1. DESPACHO DE CÓDIGO OTP CORPORATIVO ASÍNCRONO BLINDADO (CORREGIDO HTML)
	// =========================================================================
	@Async
	public void enviarCodigoRegistro(String emailDestino, String codigoOtp) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			// Forzamos el multipart y la codificación UTF-8 desde el constructor
			MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

			// MAQUETACIÓN HTML REESTRUCTURADA PARA MÁXIMA COMPATIBILIDAD DE CORREOS
			String cuerpoHtml = 
				"<!DOCTYPE html>" +
				"<html>" +
				"<head>" +
				"  <meta charset='UTF-8'>" +
				"  <title>Verificación de Seguridad</title>" +
				"</head>" +
				"<body style='margin: 0; padding: 0; background-color: #f7fafc;'>" +
				"  <table border='0' cellpadding='0' cellspacing='0' width='100%' style='table-layout: fixed; background-color: #f7fafc; padding: 40px 10px; font-family: \"Segoe UI\", Helvetica, Arial, sans-serif;'>" +
				"    <tr>" +
				"      <td align='center'>" +
				"        <table border='0' cellpadding='0' cellspacing='0' width='100%' style='max-width: 500px; background-color: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.05); border: 1px solid #e2e8f0;'>" +
				"          " +
				"          <!-- Cabecera oscura haciendo juego con el login.css -->" +
				"          <tr>" +
				"            <td align='center' style='background-color: #1a202c; padding: 25px;'>" +
				"              <h2 style='color: #ffffff; margin: 0; font-size: 18px; font-weight: 800; letter-spacing: 1px; text-transform: uppercase;'>Centro Médico Cosalud</h2>" +
				"            </td>" +
				"          </tr>" +
				"          " +
				"          <!-- Cuerpo del mensaje -->" +
				"          <tr>" +
				"            <td style='padding: 35px 25px; text-align: center;'>" +
				"              <h3 style='color: #1a202c; font-size: 20px; margin-top: 0; font-weight: 700; margin-bottom: 15px;'>Verificación de Seguridad</h3>" +
				"              <p style='color: #4a5568; font-size: 14px; line-height: 1.6; margin-bottom: 25px; margin-top: 0;'>Hola. Para completar tu registro de usuario y validar tu casilla de correo, ingresá el siguiente código de activación temporal en la pantalla del sistema:</p>" +
				"              " +
				"              <!-- BOX CELESTIAL CLÍNICO DE ÉLITE CON LOS DÍGITOS -->" +
				"              <table border='0' cellpadding='0' cellspacing='0' style='margin: 10px auto 25px auto;'>" +
				"                <tr>" +
				"                  <td align='center' style='background-color: #f0f9ff; border: 2px solid #0052cc; border-radius: 8px; padding: 15px 35px;'>" +
				"                    <span style='font-size: 32px; font-weight: 800; color: #0052cc; letter-spacing: 6px; font-family: monospace; display: block;'>" + codigoOtp + "</span>" +
				"                  </td>" +
				"                </tr>" +
				"              </table>" +
				"              " +
				"              <p style='color: #e53e3e; font-size: 12px; font-weight: bold; margin-top: 15px; margin-bottom: 0;'>Este código expira automáticamente en 15 minutos.</p>" +
				"            </td>" +
				"          </tr>" +
				"          " +
				"          <!-- Pie de página institucional -->" +
				"          <tr>" +
				"            <td align='center' style='background-color: #edf2f7; padding: 15px; border-top: 1px solid #e2e8f0;'>" +
				"              <p style='color: #718096; font-size: 11px; margin: 0;'>Por favor no respondas a este correo automático. Centro Médico Cosalud © 2026.</p>" +
				"            </td>" +
				"          </tr>" +
				"          " +
				"        </table>" +
				"      </td>" +
				"    </tr>" +
				"  </table>" +
				"</body>" +
				"</html>";

			helper.setTo(emailDestino);
			helper.setFrom("info@cosaludcentromedico.com", "Centro Médico Cosalud");
			helper.setSubject(codigoOtp + " es tu código de verificación - Centro Médico Cosalud");
			
			// Cambiamos el setText simple por el formato explícito de contenido HTML con codificación nativa
			message.setContent(cuerpoHtml, "text/html; charset=utf-8");

			mailSender.send(message);
			System.out.println("Email ACTIVACIÓN HTML enviado con éxito a: " + emailDestino);

		} catch (Exception e) {
			System.err.println("Error enviando mail activación HTML: " + e.getMessage());
		}
	}


}

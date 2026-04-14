package com.sigat.springboot.app.controllers;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.sigat.springboot.app.models.entity.Sobreturno;
import com.sigat.springboot.app.models.entity.Turno;
import com.sigat.springboot.app.models.service.ISobreTurnoService;
import com.sigat.springboot.app.models.service.ITurnoService;

@RestController
@RequestMapping("/api/backup")
public class BackupPdfRestController {

	@Autowired
	private ITurnoService turnoService;
	@Autowired
	private ISobreTurnoService sobreturnoService;

	@GetMapping("/backup-turnos-ocupados-fechas-pdf")
	public ResponseEntity<?> generarBackupTurnosOcupadosFechas(@RequestParam Long profId, @RequestParam Long especId,
			@RequestParam String fechaIni, @RequestParam String fechaFin) {
		try {
			List<Turno> turnos = turnoService.reporteTurnosOcupadosFechasPdf(profId, especId, fechaIni, fechaFin);

			if (turnos == null || turnos.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay datos.");
			}

			generarArchivoFisicoToFechas(turnos, fechaIni, fechaFin);

			return ResponseEntity.ok("{\"status\": \"OK\"}");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}
	
	@GetMapping("/backup-sobreturnos-ocupados-fechas-pdf")
	public ResponseEntity<?> generarBackupSobreturnosOcupadosFechas(@RequestParam Long profId, @RequestParam Long especId,
			@RequestParam String fechaIni, @RequestParam String fechaFin) {
		try {
			List<Sobreturno> turnos = sobreturnoService.reporteSobreturnosOcupadosFechasPdf(profId, especId, fechaIni, fechaFin);

			if (turnos == null || turnos.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay datos.");
			}

			generarArchivoFisicoSoFechas(turnos, fechaIni, fechaFin);

			return ResponseEntity.ok("{\"status\": \"OK\"}");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	@GetMapping("/backup-turnos-ocupados-fechas-horas-pdf")
	public ResponseEntity<?> generarBackupTurnosOcupadosFechasHoras(
	        @RequestParam Long profId,
	        @RequestParam Long especId,
	        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date fechaHoraIni,
	        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date fechaHoraFin) {
	    try {
	        List<Turno> turnos = turnoService.buscarTurnosOcupadosFechasHoras(profId, especId, fechaHoraIni, fechaHoraFin);

	        if (turnos == null || turnos.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"No hay turnos ocupados en ese rango.\"}");
	        }

	        // CORRECCIÓN: Para formatear un objeto 'Date' debes usar SimpleDateFormat
	        SimpleDateFormat sdfCompleto = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	        // Convertimos a String antes de pasar al método del archivo físico
	        String fechaHoraIniStr = sdfCompleto.format(fechaHoraIni);
	        String fechaHoraFinStr = sdfCompleto.format(fechaHoraFin);

	        // Llamadas al generador de archivos (ajustadas a los Strings formateados)
	        generarArchivoFisicoToFechasHoras(turnos, fechaHoraIniStr, fechaHoraFinStr);
	        // Si necesitas la segunda llamada solo con horas:
	        // generarArchivoFisicoToFechasHoras(turnos, horaIniStr, horaFinStr);

	        return ResponseEntity.ok("{\"status\": \"OK\"}");
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"" + e.getMessage() + "\"}");
	    }
	}
	
	@GetMapping("/backup-sobreturnos-ocupados-fechas-horas-pdf")
	public ResponseEntity<?> generarBackupSobreturnosOcupadosFechasHoras(
	        @RequestParam Long profId,
	        @RequestParam Long especId,
	        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date fechaHoraIni,
	        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date fechaHoraFin) {
	    try {
	        List<Sobreturno> turnos = sobreturnoService.buscarSobreturnosOcupadosFechasHoras(profId, especId, fechaHoraIni, fechaHoraFin);

	        if (turnos == null || turnos.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"No hay turnos ocupados en ese rango.\"}");
	        }

	        // CORRECCIÓN: Para formatear un objeto 'Date' debes usar SimpleDateFormat
	        SimpleDateFormat sdfCompleto = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	        // Convertimos a String antes de pasar al método del archivo físico
	        String fechaHoraIniStr = sdfCompleto.format(fechaHoraIni);
	        String fechaHoraFinStr = sdfCompleto.format(fechaHoraFin);

	        // Llamadas al generador de archivos (ajustadas a los Strings formateados)
	        generarArchivoFisicoSoFechasHoras(turnos, fechaHoraIniStr, fechaHoraFinStr);
	        // Si necesitas la segunda llamada solo con horas:
	        // generarArchivoFisicoToFechasHoras(turnos, horaIniStr, horaFinStr);

	        return ResponseEntity.ok("{\"status\": \"OK\"}");
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"" + e.getMessage() + "\"}");
	    }
	}

	private void generarArchivoFisicoToFechas(List<Turno> turnosocupadosfechas, String fIni, String fFin)
			throws Exception {
		// 1. RUTA Y CARPETA
		String fechaStr = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
		String profCodEsp = ""; // 1. La declaras fuera con un valor inicial
		for (int i = 0; i < turnosocupadosfechas.size(); i++) {
			Turno datos = turnosocupadosfechas.get(i);
			profCodEsp = datos.getProfesional().getApellido().toUpperCase() + "_" + datos.getEspecialidad().getEspecialidadCodigo().toUpperCase() + "_";			
		}		
		String nombreArchivo = profCodEsp  + fechaStr + ".pdf";
		String rutaDirectorio = "C:\\Users\\Public\\Documents\\BackupsTurnosPorCancelacionFechas";

		File directorio = new File(rutaDirectorio);
		if (!directorio.exists()) {
			directorio.mkdirs();
		}

		// 2. DOCUMENTO (Configurado en A4 Vertical con márgenes profesionales)
		Document document = new Document(PageSize.A4, 30, 30, 40, 40);

		PdfWriter writer = PdfWriter.getInstance(document,
				new FileOutputStream(rutaDirectorio + File.separator + nombreArchivo));

		document.open();

		// --- LÓGICA DE FECHAS ---
		LocalDate date = LocalDate.parse(fIni);
		DateTimeFormatter outputFormatterFechaDesde = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String outputDateStringFechaDesde = date.format(outputFormatterFechaDesde);

		LocalDate date1 = LocalDate.parse(fFin);
		DateTimeFormatter outputFormatterFechaHasta = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String outputDateStringFechaHasta = date1.format(outputFormatterFechaHasta);

		final int ROWS_PER_PAGE = 22; // Ajustado para Vertical

		// --- ENCABEZADO PROFESIONAL (Logo e Info alineados) ---
		PdfPTable headerTable = new PdfPTable(2);
		headerTable.setWidthPercentage(100);
		headerTable.setWidths(new float[] { 1, 3 });
		headerTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

		// LOGO
		Image img = Image.getInstance(getClass().getClassLoader().getResource("static/images/LogoSigat.png"));
		img.scaleAbsolute(55, 55);
		PdfPCell logoCell = new PdfPCell(img);
		logoCell.setBorder(Rectangle.NO_BORDER);
		logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		headerTable.addCell(logoCell);

		// INFO CORPORATIVA
		String fechaHoraFormateada = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
		Paragraph infoRight = new Paragraph();
		infoRight.add(new Chunk("SIGAT - Sistema General Administrativo de Turnos\n",
				new Font(Font.HELVETICA, 12, Font.BOLD, Color.DARK_GRAY)));
		infoRight.add(new Chunk("Informe Generado el: " + fechaHoraFormateada + "\n",
				new Font(Font.HELVETICA, 8, Font.NORMAL, Color.GRAY)));
		infoRight
				.add(new Chunk("Motivo: Cancelación del Médico", new Font(Font.HELVETICA, 8, Font.ITALIC, Color.GRAY)));
		infoRight.setAlignment(Element.ALIGN_RIGHT);

		PdfPCell infoCell = new PdfPCell(infoRight);
		infoCell.setBorder(Rectangle.NO_BORDER);
		infoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		headerTable.addCell(infoCell);

		// --- TÍTULO MODERNO ---
		Paragraph titulo = new Paragraph("LISTADO DE TURNOS OCUPADOS PARA REPROGRAMAR",
				new Font(Font.HELVETICA, 14, Font.BOLD, Color.BLACK));
		titulo.setSpacingBefore(15);

		// Línea divisoria
		LineSeparator line = new LineSeparator();
		line.setLineColor(new Color(200, 200, 200));
		line.setLineWidth(1f);

		// PERIODO
		Paragraph periodo = new Paragraph(
				"Datos obtenidos desde el " + outputDateStringFechaDesde + " hasta el " + outputDateStringFechaHasta,
				new Font(Font.HELVETICA, 10, Font.NORMAL, Color.GRAY));
		periodo.setSpacingAfter(15);

		// ENCABEZADOS DE TABLA (Adaptados a A4 Vertical)
		PdfPTable tablaTurnosOcupados = new PdfPTable(7);
		tablaTurnosOcupados.setWidthPercentage(100);
		// Ajuste de pesos para que no se amontonen en Vertical
		tablaTurnosOcupados.setWidths(new float[] { 200f, 390f, 310f, 390f, 190f, 230f, 290f });

		String[] headers = { "ALTA", "PROFESIONAL", "ESPECIALIDAD", "PACIENTE", "DNI", "TELEFONO", "FECHA TURNO" };
		for (String h : headers) {
			PdfPCell cell = new PdfPCell(new Phrase(h, new Font(Font.HELVETICA, 8, Font.BOLD, Color.WHITE)));
			cell.setBackgroundColor(new Color(80, 80, 80)); // Gris oscuro profesional
			cell.setPaddingBottom(6f);
			cell.setPaddingTop(4f);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setBorderColor(Color.WHITE);
			tablaTurnosOcupados.addCell(cell);
		}

		// BUCLE DE TURNOS
		for (int i = 0; i < turnosocupadosfechas.size(); i++) {
			Turno item = turnosocupadosfechas.get(i);
			String formattedDateCreation = item.getFechaCreacion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
			String formattedDateTurno = new SimpleDateFormat("dd/MM/yyyy HH:mm")
					.format(item.getPlanilladetalle().getRangoFechaHora());

			Font smallFontBlack = FontFactory.getFont(FontFactory.HELVETICA, 8f);
			Font smallFontRed = new Font(Font.HELVETICA, 8, Font.NORMAL, Color.RED);

			tablaTurnosOcupados.addCell(crearCelda(formattedDateCreation, smallFontBlack));
			tablaTurnosOcupados.addCell(crearCelda(item.getProfesional().getApellido().toUpperCase() + " "
					+ item.getProfesional().getNombre().toUpperCase(), smallFontBlack));
			tablaTurnosOcupados
					.addCell(crearCelda(item.getEspecialidad().getEspecialidadNombre().toUpperCase(), smallFontBlack));
			tablaTurnosOcupados.addCell(crearCelda(
					item.getPaciente().getApellido().toUpperCase() + " " + item.getPaciente().getNombre().toUpperCase(),
					smallFontBlack));
			tablaTurnosOcupados.addCell(crearCelda(item.getPaciente().getDni(), smallFontBlack));
			tablaTurnosOcupados.addCell(crearCelda(String.valueOf(item.getPaciente().getTelefono()), smallFontBlack));
			tablaTurnosOcupados.addCell(crearCelda(formattedDateTurno, smallFontRed));

			if ((i + 1) % ROWS_PER_PAGE == 0 || (i + 1) == turnosocupadosfechas.size()) {
				document.add(headerTable);
				document.add(titulo);
				document.add(new Chunk(line));
				document.add(periodo);
				document.add(tablaTurnosOcupados);

				// --- PIE DE PÁGINA CENTRADO ---
				PdfContentByte cb = writer.getDirectContent();
				cb.beginText();
				BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, false);
				cb.setFontAndSize(bf, 9);

				String textoPagina = "Página: " + writer.getPageNumber();

				// Cálculo del centro: (Ancho total / 2) - (Ancho del texto / 2)
				float xCentro = (document.getPageSize().getWidth() / 2) - (bf.getWidthPoint(textoPagina, 9) / 2);

				cb.setTextMatrix(xCentro, 30);
				cb.showText(textoPagina);
				cb.endText();

				tablaTurnosOcupados.flushContent();
				if ((i + 1) < turnosocupadosfechas.size()) {
					document.newPage();
				}
			}
		}
		document.close();
	}
	
	private void generarArchivoFisicoSoFechas(List<Sobreturno> sobreturnosocupadosfechas, String fIni, String fFin)
			throws Exception {
		// 1. RUTA Y CARPETA
		String fechaStr = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
		//String nombreArchivo = "BackupSobreTurnosOcupadosFechas_" + fechaStr + ".pdf";
		String profCodEsp = ""; // 1. La declaras fuera con un valor inicial
		for (int i = 0; i < sobreturnosocupadosfechas.size(); i++) {
			Sobreturno datos = sobreturnosocupadosfechas.get(i);
			profCodEsp = datos.getProfesional().getApellido().toUpperCase() + "_" + datos.getEspecialidad().getEspecialidadCodigo().toUpperCase() + "_";			
		}		
		String nombreArchivo = profCodEsp  + fechaStr + ".pdf";
		String rutaDirectorio = "C:\\Users\\Public\\Documents\\BackupsSobreturnosPorCancelacionFechas";

		File directorio = new File(rutaDirectorio);
		if (!directorio.exists()) {
			directorio.mkdirs();
		}

		// 2. DOCUMENTO (Configurado en A4 Vertical con márgenes profesionales)
		Document document = new Document(PageSize.A4, 30, 30, 40, 40);

		PdfWriter writer = PdfWriter.getInstance(document,
				new FileOutputStream(rutaDirectorio + File.separator + nombreArchivo));

		document.open();

		// --- LÓGICA DE FECHAS ---
		LocalDate date = LocalDate.parse(fIni);
		DateTimeFormatter outputFormatterFechaDesde = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String outputDateStringFechaDesde = date.format(outputFormatterFechaDesde);

		LocalDate date1 = LocalDate.parse(fFin);
		DateTimeFormatter outputFormatterFechaHasta = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String outputDateStringFechaHasta = date1.format(outputFormatterFechaHasta);

		final int ROWS_PER_PAGE = 22; // Ajustado para Vertical

		// --- ENCABEZADO PROFESIONAL (Logo e Info alineados) ---
		PdfPTable headerTable = new PdfPTable(2);
		headerTable.setWidthPercentage(100);
		headerTable.setWidths(new float[] { 1, 3 });
		headerTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

		// LOGO
		Image img = Image.getInstance(getClass().getClassLoader().getResource("static/images/LogoSigat.png"));
		img.scaleAbsolute(55, 55);
		PdfPCell logoCell = new PdfPCell(img);
		logoCell.setBorder(Rectangle.NO_BORDER);
		logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		headerTable.addCell(logoCell);

		// INFO CORPORATIVA
		String fechaHoraFormateada = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
		Paragraph infoRight = new Paragraph();
		infoRight.add(new Chunk("SIGAT - Sistema General Administrativo de Turnos\n",
				new Font(Font.HELVETICA, 12, Font.BOLD, Color.DARK_GRAY)));
		infoRight.add(new Chunk("Informe Generado el: " + fechaHoraFormateada + "\n",
				new Font(Font.HELVETICA, 8, Font.NORMAL, Color.GRAY)));
		infoRight
				.add(new Chunk("Motivo: Cancelación del Médico", new Font(Font.HELVETICA, 8, Font.ITALIC, Color.GRAY)));
		infoRight.setAlignment(Element.ALIGN_RIGHT);

		PdfPCell infoCell = new PdfPCell(infoRight);
		infoCell.setBorder(Rectangle.NO_BORDER);
		infoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		headerTable.addCell(infoCell);

		// --- TÍTULO MODERNO ---
		Paragraph titulo = new Paragraph("LISTADO DE SOBRETURNOS OCUPADOS PARA REPROGRAMAR",
				new Font(Font.HELVETICA, 14, Font.BOLD, Color.BLACK));
		titulo.setSpacingBefore(15);

		// Línea divisoria
		LineSeparator line = new LineSeparator();
		line.setLineColor(new Color(200, 200, 200));
		line.setLineWidth(1f);

		// PERIODO
		Paragraph periodo = new Paragraph(
				"Datos obtenidos desde el " + outputDateStringFechaDesde + " hasta el " + outputDateStringFechaHasta,
				new Font(Font.HELVETICA, 10, Font.NORMAL, Color.GRAY));
		periodo.setSpacingAfter(15);

		// ENCABEZADOS DE TABLA (Adaptados a A4 Vertical)
		PdfPTable tablaSobreTurnosOcupados = new PdfPTable(7);
		tablaSobreTurnosOcupados.setWidthPercentage(100);
		// Ajuste de pesos para que no se amontonen en Vertical
		tablaSobreTurnosOcupados.setWidths(new float[] { 200f, 390f, 310f, 390f, 190f, 230f, 290f });

		String[] headers = { "ALTA", "PROFESIONAL", "ESPECIALIDAD", "PACIENTE", "DNI", "TELEFONO", "FECHA SOBRETURNO" };
		for (String h : headers) {
			PdfPCell cell = new PdfPCell(new Phrase(h, new Font(Font.HELVETICA, 8, Font.BOLD, Color.WHITE)));
			cell.setBackgroundColor(new Color(80, 80, 80)); // Gris oscuro profesional
			cell.setPaddingBottom(6f);
			cell.setPaddingTop(4f);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setBorderColor(Color.WHITE);
			tablaSobreTurnosOcupados.addCell(cell);
		}

		// BUCLE DE TURNOS
		for (int i = 0; i < sobreturnosocupadosfechas.size(); i++) {
			Sobreturno item = sobreturnosocupadosfechas.get(i);
			String formattedDateCreation = item.getFechaCreacion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
			/*
			 * String formattedDateTurno = new SimpleDateFormat("dd/MM/yyyy HH:mm")
			 * .format(item.getRangoFechaHora());
			 */
			String formattedDateTurno = item.getRangoFechaHora()
				    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

			Font smallFontBlack = FontFactory.getFont(FontFactory.HELVETICA, 8f);
			Font smallFontRed = new Font(Font.HELVETICA, 8, Font.NORMAL, Color.RED);

			tablaSobreTurnosOcupados.addCell(crearCelda(formattedDateCreation, smallFontBlack));
			tablaSobreTurnosOcupados.addCell(crearCelda(item.getProfesional().getApellido().toUpperCase() + " "
					+ item.getProfesional().getNombre().toUpperCase(), smallFontBlack));
			tablaSobreTurnosOcupados
					.addCell(crearCelda(item.getEspecialidad().getEspecialidadNombre().toUpperCase(), smallFontBlack));
			tablaSobreTurnosOcupados.addCell(crearCelda(
					item.getPaciente().getApellido().toUpperCase() + " " + item.getPaciente().getNombre().toUpperCase(),
					smallFontBlack));
			tablaSobreTurnosOcupados.addCell(crearCelda(item.getPaciente().getDni(), smallFontBlack));
			tablaSobreTurnosOcupados.addCell(crearCelda(String.valueOf(item.getPaciente().getTelefono()), smallFontBlack));
			tablaSobreTurnosOcupados.addCell(crearCelda(formattedDateTurno, smallFontRed));

			if ((i + 1) % ROWS_PER_PAGE == 0 || (i + 1) == sobreturnosocupadosfechas.size()) {
				document.add(headerTable);
				document.add(titulo);
				document.add(new Chunk(line));
				document.add(periodo);
				document.add(tablaSobreTurnosOcupados);

				// --- PIE DE PÁGINA CENTRADO ---
				PdfContentByte cb = writer.getDirectContent();
				cb.beginText();
				BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, false);
				cb.setFontAndSize(bf, 9);

				String textoPagina = "Página: " + writer.getPageNumber();

				// Cálculo del centro: (Ancho total / 2) - (Ancho del texto / 2)
				float xCentro = (document.getPageSize().getWidth() / 2) - (bf.getWidthPoint(textoPagina, 9) / 2);

				cb.setTextMatrix(xCentro, 30);
				cb.showText(textoPagina);
				cb.endText();

				tablaSobreTurnosOcupados.flushContent();
				if ((i + 1) < sobreturnosocupadosfechas.size()) {
					document.newPage();
				}
			}
		}
		document.close();
	}

	private void generarArchivoFisicoToFechasHoras(List<Turno> turnosocupadosfechasHoras, String fechaHoraIni, String fechaHoraFin)
			throws Exception {
		// 1. RUTA Y CARPETA
		String fechaStr = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
		//String nombreArchivo = "BackupTurnosOcupadosFechasHoras_" + fechaStr + ".pdf";
		String profCodEsp = ""; // 1. La declaras fuera con un valor inicial
		for (int i = 0; i < turnosocupadosfechasHoras.size(); i++) {
			Turno datos = turnosocupadosfechasHoras.get(i);
			profCodEsp = datos.getProfesional().getApellido().toUpperCase() + "_" + datos.getEspecialidad().getEspecialidadCodigo().toUpperCase() + "_";			
		}		
		String nombreArchivo = profCodEsp  + fechaStr + ".pdf";
		String rutaDirectorio = "C:\\Users\\Public\\Documents\\BackupsTurnosPorCancelacionFechasHoras";

		File directorio = new File(rutaDirectorio);
		if (!directorio.exists()) {
			directorio.mkdirs();
		}

		// 2. DOCUMENTO (Configurado en A4 Vertical con márgenes profesionales)
		Document document = new Document(PageSize.A4, 30, 30, 40, 40);

		PdfWriter writer = PdfWriter.getInstance(document,
				new FileOutputStream(rutaDirectorio + File.separator + nombreArchivo));

		document.open();

		// --- LÓGICA DE FECHA HORA ---
		String outputDateStringFechaDesde = fechaHoraIni;
		String outputDateStringFechaHasta = fechaHoraFin;
		
		final int ROWS_PER_PAGE = 22; // Ajustado para Vertical

		// --- ENCABEZADO PROFESIONAL (Logo e Info alineados) ---
		PdfPTable headerTable = new PdfPTable(2);
		headerTable.setWidthPercentage(100);
		headerTable.setWidths(new float[] { 1, 3 });
		headerTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

		// LOGO
		Image img = Image.getInstance(getClass().getClassLoader().getResource("static/images/LogoSigat.png"));
		img.scaleAbsolute(55, 55);
		PdfPCell logoCell = new PdfPCell(img);
		logoCell.setBorder(Rectangle.NO_BORDER);
		logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		headerTable.addCell(logoCell);

		// INFO CORPORATIVA
		String fechaHoraFormateada = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
		Paragraph infoRight = new Paragraph();
		infoRight.add(new Chunk("SIGAT - Sistema General Administrativo de Turnos\n",
				new Font(Font.HELVETICA, 12, Font.BOLD, Color.DARK_GRAY)));
		infoRight.add(new Chunk("Informe Generado el: " + fechaHoraFormateada + "\n",
				new Font(Font.HELVETICA, 8, Font.NORMAL, Color.GRAY)));
		infoRight
				.add(new Chunk("Motivo: Cancelación del Médico", new Font(Font.HELVETICA, 8, Font.ITALIC, Color.GRAY)));
		infoRight.setAlignment(Element.ALIGN_RIGHT);

		PdfPCell infoCell = new PdfPCell(infoRight);
		infoCell.setBorder(Rectangle.NO_BORDER);
		infoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		headerTable.addCell(infoCell);

		// --- TÍTULO MODERNO ---
		Paragraph titulo = new Paragraph("LISTADO DE TURNOS OCUPADOS PARA REPROGRAMAR",
				new Font(Font.HELVETICA, 14, Font.BOLD, Color.BLACK));
		titulo.setSpacingBefore(15);

		// Línea divisoria
		LineSeparator line = new LineSeparator();
		line.setLineColor(new Color(200, 200, 200));
		line.setLineWidth(1f);

		// PERIODO formateado a dd/MM/yyyy HH:mm *******************************************
		// 1. Definir los formatos de entrada y salida
		DateTimeFormatter formatoEntrada = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		DateTimeFormatter formatoSalida = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
		// 2. Parsear el String original a un objeto de fecha
		LocalDateTime fechaDesde = LocalDateTime.parse(fechaHoraIni, formatoEntrada);
		LocalDateTime fechaHasta = LocalDateTime.parse(fechaHoraFin, formatoEntrada);
		// 3. Convertir el objeto de fecha al nuevo String formateado
		outputDateStringFechaDesde = fechaDesde.format(formatoSalida);
		outputDateStringFechaHasta = fechaHasta.format(formatoSalida);
		//**********************************************************************************
		
		Paragraph periodo = new Paragraph(
				"Datos obtenidos desde el " + outputDateStringFechaDesde + " hasta el " + outputDateStringFechaHasta,
				new Font(Font.HELVETICA, 10, Font.NORMAL, Color.GRAY));
		periodo.setSpacingAfter(15);

		// ENCABEZADOS DE TABLA (Adaptados a A4 Vertical)
		PdfPTable tablaTurnosOcupados = new PdfPTable(7);
		tablaTurnosOcupados.setWidthPercentage(100);
		// Ajuste de pesos para que no se amontonen en Vertical
		tablaTurnosOcupados.setWidths(new float[] { 200f, 390f, 310f, 390f, 190f, 230f, 290f });

		String[] headers = { "ALTA", "PROFESIONAL", "ESPECIALIDAD", "PACIENTE", "DNI", "TELEFONO", "FECHA TURNO" };
		for (String h : headers) {
			PdfPCell cell = new PdfPCell(new Phrase(h, new Font(Font.HELVETICA, 8, Font.BOLD, Color.WHITE)));
			cell.setBackgroundColor(new Color(80, 80, 80)); // Gris oscuro profesional
			cell.setPaddingBottom(6f);
			cell.setPaddingTop(4f);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setBorderColor(Color.WHITE);
			tablaTurnosOcupados.addCell(cell);
		}

		// BUCLE DE TURNOS
		for (int i = 0; i < turnosocupadosfechasHoras.size(); i++) {
			Turno item = turnosocupadosfechasHoras.get(i);
			String formattedDateCreation = item.getFechaCreacion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
			String formattedDateTurno = new SimpleDateFormat("dd/MM/yyyy HH:mm")
					.format(item.getPlanilladetalle().getRangoFechaHora());

			Font smallFontBlack = FontFactory.getFont(FontFactory.HELVETICA, 8f);
			Font smallFontRed = new Font(Font.HELVETICA, 8, Font.NORMAL, Color.RED);

			tablaTurnosOcupados.addCell(crearCelda(formattedDateCreation, smallFontBlack));
			tablaTurnosOcupados.addCell(crearCelda(item.getProfesional().getApellido().toUpperCase() + " "
					+ item.getProfesional().getNombre().toUpperCase(), smallFontBlack));
			tablaTurnosOcupados
					.addCell(crearCelda(item.getEspecialidad().getEspecialidadNombre().toUpperCase(), smallFontBlack));
			tablaTurnosOcupados.addCell(crearCelda(
					item.getPaciente().getApellido().toUpperCase() + " " + item.getPaciente().getNombre().toUpperCase(),
					smallFontBlack));
			tablaTurnosOcupados.addCell(crearCelda(item.getPaciente().getDni(), smallFontBlack));
			tablaTurnosOcupados.addCell(crearCelda(String.valueOf(item.getPaciente().getTelefono()), smallFontBlack));
			tablaTurnosOcupados.addCell(crearCelda(formattedDateTurno, smallFontRed));

			if ((i + 1) % ROWS_PER_PAGE == 0 || (i + 1) == turnosocupadosfechasHoras.size()) {
				document.add(headerTable);
				document.add(titulo);
				document.add(new Chunk(line));
				document.add(periodo);
				document.add(tablaTurnosOcupados);

				// --- PIE DE PÁGINA CENTRADO ---
				PdfContentByte cb = writer.getDirectContent();
				cb.beginText();
				BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, false);
				cb.setFontAndSize(bf, 9);

				String textoPagina = "Página: " + writer.getPageNumber();

				// Cálculo del centro: (Ancho total / 2) - (Ancho del texto / 2)
				float xCentro = (document.getPageSize().getWidth() / 2) - (bf.getWidthPoint(textoPagina, 9) / 2);

				cb.setTextMatrix(xCentro, 30);
				cb.showText(textoPagina);
				cb.endText();

				tablaTurnosOcupados.flushContent();
				if ((i + 1) < turnosocupadosfechasHoras.size()) {
					document.newPage();
				}
			}
		}
		document.close();
	}
	
	private void generarArchivoFisicoSoFechasHoras(List<Sobreturno> sobreturnosocupadosfechasHoras, String fechaHoraIni, String fechaHoraFin)
			throws Exception {
		// 1. RUTA Y CARPETA
		String fechaStr = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
		//String nombreArchivo = "BackupSobreTurnosOcupadosFechasHoras_" + fechaStr + ".pdf";
		String profCodEsp = ""; // 1. La declaras fuera con un valor inicial
		for (int i = 0; i < sobreturnosocupadosfechasHoras.size(); i++) {
			Sobreturno datos = sobreturnosocupadosfechasHoras.get(i);
			profCodEsp = datos.getProfesional().getApellido().toUpperCase() + "_" + datos.getEspecialidad().getEspecialidadCodigo().toUpperCase() + "_";			
		}		
		String nombreArchivo = profCodEsp  + fechaStr + ".pdf";
		String rutaDirectorio = "C:\\Users\\Public\\Documents\\BackupsSobreturnosPorCancelacionFechasHoras";

		File directorio = new File(rutaDirectorio);
		if (!directorio.exists()) {
			directorio.mkdirs();
		}

		// 2. DOCUMENTO (Configurado en A4 Vertical con márgenes profesionales)
		Document document = new Document(PageSize.A4, 30, 30, 40, 40);

		PdfWriter writer = PdfWriter.getInstance(document,
				new FileOutputStream(rutaDirectorio + File.separator + nombreArchivo));

		document.open();

		// --- LÓGICA DE FECHA HORA ---
		String outputDateStringFechaDesde = fechaHoraIni;
		String outputDateStringFechaHasta = fechaHoraFin;
		
		final int ROWS_PER_PAGE = 22; // Ajustado para Vertical

		// --- ENCABEZADO PROFESIONAL (Logo e Info alineados) ---
		PdfPTable headerTable = new PdfPTable(2);
		headerTable.setWidthPercentage(100);
		headerTable.setWidths(new float[] { 1, 3 });
		headerTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

		// LOGO
		Image img = Image.getInstance(getClass().getClassLoader().getResource("static/images/LogoSigat.png"));
		img.scaleAbsolute(55, 55);
		PdfPCell logoCell = new PdfPCell(img);
		logoCell.setBorder(Rectangle.NO_BORDER);
		logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		headerTable.addCell(logoCell);

		// INFO CORPORATIVA
		String fechaHoraFormateada = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
		Paragraph infoRight = new Paragraph();
		infoRight.add(new Chunk("SIGAT - Sistema General Administrativo de Turnos\n",
				new Font(Font.HELVETICA, 12, Font.BOLD, Color.DARK_GRAY)));
		infoRight.add(new Chunk("Informe Generado el: " + fechaHoraFormateada + "\n",
				new Font(Font.HELVETICA, 8, Font.NORMAL, Color.GRAY)));
		infoRight
				.add(new Chunk("Motivo: Cancelación del Médico", new Font(Font.HELVETICA, 8, Font.ITALIC, Color.GRAY)));
		infoRight.setAlignment(Element.ALIGN_RIGHT);

		PdfPCell infoCell = new PdfPCell(infoRight);
		infoCell.setBorder(Rectangle.NO_BORDER);
		infoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		headerTable.addCell(infoCell);

		// --- TÍTULO MODERNO ---
		Paragraph titulo = new Paragraph("LISTADO DE SOBRETURNOS OCUPADOS PARA REPROGRAMAR",
				new Font(Font.HELVETICA, 14, Font.BOLD, Color.BLACK));
		titulo.setSpacingBefore(15);

		// Línea divisoria
		LineSeparator line = new LineSeparator();
		line.setLineColor(new Color(200, 200, 200));
		line.setLineWidth(1f);

		// PERIODO formateado a dd/MM/yyyy HH:mm *******************************************
		// 1. Definir los formatos de entrada y salida
		DateTimeFormatter formatoEntrada = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		DateTimeFormatter formatoSalida = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
		// 2. Parsear el String original a un objeto de fecha
		LocalDateTime fechaDesde = LocalDateTime.parse(fechaHoraIni, formatoEntrada);
		LocalDateTime fechaHasta = LocalDateTime.parse(fechaHoraFin, formatoEntrada);
		// 3. Convertir el objeto de fecha al nuevo String formateado
		outputDateStringFechaDesde = fechaDesde.format(formatoSalida);
		outputDateStringFechaHasta = fechaHasta.format(formatoSalida);
		//**********************************************************************************
		
		Paragraph periodo = new Paragraph(
				"Datos obtenidos desde el " + outputDateStringFechaDesde + " hasta el " + outputDateStringFechaHasta,
				new Font(Font.HELVETICA, 10, Font.NORMAL, Color.GRAY));
		periodo.setSpacingAfter(15);

		// ENCABEZADOS DE TABLA (Adaptados a A4 Vertical)
		PdfPTable tablaTurnosOcupados = new PdfPTable(7);
		tablaTurnosOcupados.setWidthPercentage(100);
		// Ajuste de pesos para que no se amontonen en Vertical
		tablaTurnosOcupados.setWidths(new float[] { 200f, 390f, 310f, 390f, 190f, 230f, 290f });

		String[] headers = { "ALTA", "PROFESIONAL", "ESPECIALIDAD", "PACIENTE", "DNI", "TELEFONO", "FECHA SOBRETURNO" };
		for (String h : headers) {
			PdfPCell cell = new PdfPCell(new Phrase(h, new Font(Font.HELVETICA, 8, Font.BOLD, Color.WHITE)));
			cell.setBackgroundColor(new Color(80, 80, 80)); // Gris oscuro profesional
			cell.setPaddingBottom(6f);
			cell.setPaddingTop(4f);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setBorderColor(Color.WHITE);
			tablaTurnosOcupados.addCell(cell);
		}

		// BUCLE DE TURNOS
		for (int i = 0; i < sobreturnosocupadosfechasHoras.size(); i++) {
			Sobreturno item = sobreturnosocupadosfechasHoras.get(i);
			String formattedDateCreation = item.getFechaCreacion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
			String formattedDateTurno = item.getRangoFechaHora()
				    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

			Font smallFontBlack = FontFactory.getFont(FontFactory.HELVETICA, 8f);
			Font smallFontRed = new Font(Font.HELVETICA, 8, Font.NORMAL, Color.RED);

			tablaTurnosOcupados.addCell(crearCelda(formattedDateCreation, smallFontBlack));
			tablaTurnosOcupados.addCell(crearCelda(item.getProfesional().getApellido().toUpperCase() + " "
					+ item.getProfesional().getNombre().toUpperCase(), smallFontBlack));
			tablaTurnosOcupados
					.addCell(crearCelda(item.getEspecialidad().getEspecialidadNombre().toUpperCase(), smallFontBlack));
			tablaTurnosOcupados.addCell(crearCelda(
					item.getPaciente().getApellido().toUpperCase() + " " + item.getPaciente().getNombre().toUpperCase(),
					smallFontBlack));
			tablaTurnosOcupados.addCell(crearCelda(item.getPaciente().getDni(), smallFontBlack));
			tablaTurnosOcupados.addCell(crearCelda(String.valueOf(item.getPaciente().getTelefono()), smallFontBlack));
			tablaTurnosOcupados.addCell(crearCelda(formattedDateTurno, smallFontRed));

			if ((i + 1) % ROWS_PER_PAGE == 0 || (i + 1) == sobreturnosocupadosfechasHoras.size()) {
				document.add(headerTable);
				document.add(titulo);
				document.add(new Chunk(line));
				document.add(periodo);
				document.add(tablaTurnosOcupados);

				// --- PIE DE PÁGINA CENTRADO ---
				PdfContentByte cb = writer.getDirectContent();
				cb.beginText();
				BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, false);
				cb.setFontAndSize(bf, 9);

				String textoPagina = "Página: " + writer.getPageNumber();

				// Cálculo del centro: (Ancho total / 2) - (Ancho del texto / 2)
				float xCentro = (document.getPageSize().getWidth() / 2) - (bf.getWidthPoint(textoPagina, 9) / 2);

				cb.setTextMatrix(xCentro, 30);
				cb.showText(textoPagina);
				cb.endText();

				tablaTurnosOcupados.flushContent();
				if ((i + 1) < sobreturnosocupadosfechasHoras.size()) {
					document.newPage();
				}
			}
		}
		document.close();
	}

	private PdfPCell crearCelda(String texto, Font fuente) {
		PdfPCell cell = new PdfPCell(new Phrase(texto, fuente));
		cell.setMinimumHeight(25f);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setPaddingLeft(3f);
		return cell;
	}

}

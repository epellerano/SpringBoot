package com.sigat.springboot.app.view.pdf;

import java.awt.Color;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractPdfView;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import com.sigat.springboot.app.models.entity.Profesional;
import com.sigat.springboot.app.models.entity.Turno;
import com.sigat.springboot.app.models.entity.Vinculacion;
import com.sigat.springboot.app.models.service.IProfesionalService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component("turnosOcupadosFechasPdfView")
public class TurnosOcupadosFechasPdfView extends AbstractPdfView {

	@Override
	protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		// response.setHeader("Content-Disposition", "inline;
		// filename=\"listado-profesionales.pdf\"");

		// 1. Formatear la fecha actual
		String fecha = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
		String nombreArchivo = "turnosOcupados_" + fecha + ".pdf";

		// 2. Configurar el nombre del archivo en la cabecera
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + nombreArchivo + "\"");

		// Obtener los datos del modelo(ReporteController) para armart el pdf.
		@SuppressWarnings("unchecked")
		List<Turno> turnosocupadosfechas = (List<Turno>) model.get("turnosocupadosfechas");

		// Obtener fecha inicio del modelo obtenido de (ReporteController).
		String fechaInicioReporte = String.valueOf(model.get("fechaInicioReporte"));
		// Convertimos fechaDesde al formato dd/MM/yyyy
		LocalDate date = LocalDate.parse(fechaInicioReporte);
		DateTimeFormatter outputFormatterFechaDesde = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String outputDateStringFechaDesde = date.format(outputFormatterFechaDesde);

		// Obtener fecha Final del modelo obtenido de (ReporteController).
		String fechaFinReporte = String.valueOf(model.get("fechaFinReporte"));
		// Convertimos fechaDesde al formato dd/MM/yyyy
		LocalDate date1 = LocalDate.parse(fechaFinReporte);
		DateTimeFormatter outputFormatterFechaHasta = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String outputDateStringFechaHasta = date1.format(outputFormatterFechaHasta);

		// Definir la cantidad de filas por página
		final int ROWS_PER_PAGE = 20;

		// LOGO
		// -----------------------------------------------------------------------------------------------
		Image img = Image.getInstance(getClass().getClassLoader().getResource("static/images/LogoSigat.png"));
		// ajusta el tamaño y la posición de la imagen
		img.scaleAbsolute(52, 53); // Ancho y alto
		// img.setAbsolutePosition(60, 780); // Posición X, Y
		img.setAlignment(Element.ALIGN_LEFT);
		img.setIndentationLeft(20f);
		// ----------------------------------------------------------------------------------------------------

		// TITULO ----------------------------------------------------------------
		PdfPTable tablaTitulo = new PdfPTable(1);
		// margen superior
		tablaTitulo.setSpacingBefore(5f);
		// margen inferior
		tablaTitulo.setSpacingAfter(3f);
		// Alinear de la tabla completa
		tablaTitulo.setHorizontalAlignment(PdfPTable.ALIGN_CENTER);
		// Ancho de la primera columna
		tablaTitulo.setWidthPercentage(90);
		// Estilo y color subrayado.
		String titulo = "Listado de Turnos Ocupados";
		Font fuenteTitulo = new Font(Font.TIMES_ROMAN, 12, 0, Color.white);
		PdfPCell celdaTitulo = new PdfPCell(new Phrase(titulo, fuenteTitulo));
		// Configurar la celda para que no tenga borde
		celdaTitulo.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		// Alto del recuadro
		celdaTitulo.setFixedHeight(20);
		// esPacio entre el contenido y el borde del rectangulo.
		celdaTitulo.setPadding(2.0f);
		// Añade margen izquierda al texto
		// celdaTitulo.setPaddingLeft(10);
		celdaTitulo.setBackgroundColor(Color.darkGray); // color de fondo
		// celdaTitulo.setBorder(Rectangle.NO_BORDER);
		// Añadir el Titulo
		tablaTitulo.addCell(celdaTitulo);
		// --FIN TITULO -----------------------------------------------------------

		// FECHA Y HORA (con estilo de Fuente)
		// ----------------------------------------------------------------------
		LocalDateTime fechaHoraActual = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
		String fechaHoraFormateada = fechaHoraActual.format(formatter);
		Font fuenteFechaHoraFormateada = new Font(Font.TIMES_ROMAN, 11, 0, Color.gray);
		PdfPCell celdaFechaHoraFormateada = new PdfPCell(
				new Phrase("Informe Generado el " + fechaHoraFormateada, fuenteFechaHoraFormateada));
		celdaFechaHoraFormateada.setBorder(Rectangle.NO_BORDER);
		celdaFechaHoraFormateada.setHorizontalAlignment(Element.ALIGN_CENTER);
		// Crear tabla para mostrar la fecha y hora
		PdfPTable tableFechaHora = new PdfPTable(1); // 2 columnas
		// margen inferior
		tableFechaHora.setSpacingAfter(1);
		// Añadir contenido a la tabla sin borde
		tableFechaHora.getDefaultCell().setBorder(0);
		tableFechaHora.addCell(celdaFechaHoraFormateada);
		// -----------------------------------------------------------------------------------------------------------

		// Creamos la tabla Periodo (desde-Hasta).
		Font fuenteFechaHoraFormateadaHasta = new Font(Font.TIMES_ROMAN, 11, 0, Color.gray);
		String periodo = "Datos obtenidos desde el " + outputDateStringFechaDesde + " hasta el "
				+ outputDateStringFechaHasta;
		PdfPCell celdaPeriodo = new PdfPCell(new Phrase(periodo, fuenteFechaHoraFormateadaHasta));
		celdaPeriodo.setBorder(Rectangle.NO_BORDER);
		celdaPeriodo.setHorizontalAlignment(Element.ALIGN_CENTER);
		PdfPTable tablaPeriodo = new PdfPTable(1);
		tablaPeriodo.setSpacingAfter(20); // Añadir contenido a la tabla sin borde
		tablaPeriodo.getDefaultCell().setBorder(0);
		// agragamos la celdaPeriodo a la tabla.
		tablaPeriodo.addCell(celdaPeriodo);

		// TURNOS ------------------------------------------------------------
		Font whiteFont = new Font(Font.HELVETICA, 9, Font.NORMAL, Color.white);
		PdfPCell cellFechaAlta = new PdfPCell(new Phrase("ALTA", whiteFont));
		PdfPCell cellProfesional = new PdfPCell(new Phrase("PROFESIONAL", whiteFont));
		PdfPCell cellEspecialidad = new PdfPCell(new Phrase("ESPECIALIDAD", whiteFont));
		PdfPCell cellPaciente = new PdfPCell(new Phrase("PACIENTE", whiteFont));
		PdfPCell cellDni = new PdfPCell(new Phrase("DNI", whiteFont));
		PdfPCell cellTelefono = new PdfPCell(new Phrase("TELEFONO", whiteFont));
		PdfPCell cellFechaTurno = new PdfPCell(new Phrase("FECHA TURNO", whiteFont));
		// CELDA TITULO SIN BORDE Y CON MARGEN INFERIOR
		// cellFechaAlta.setBorder(Rectangle.NO_BORDER);
		cellFechaAlta.setPaddingBottom(5f);
		cellFechaAlta.setBackgroundColor(Color.gray);
		// cellProfesional.setBorder(Rectangle.NO_BORDER);
		cellProfesional.setPaddingBottom(5f);
		cellProfesional.setBackgroundColor(Color.gray);
		// cellEspecialidad.setBorder(Rectangle.NO_BORDER);
		cellEspecialidad.setPaddingBottom(5f);
		cellEspecialidad.setBackgroundColor(Color.gray);
		// cellPaciente.setBorder(Rectangle.NO_BORDER);
		cellPaciente.setPaddingBottom(5f);
		cellPaciente.setBackgroundColor(Color.gray);
		// cellDni.setBorder(Rectangle.NO_BORDER);
		cellDni.setPaddingBottom(5f);
		cellDni.setBackgroundColor(Color.gray);
		// cellTelefono.setBorder(Rectangle.NO_BORDER);
		cellTelefono.setPaddingBottom(5f);
		cellTelefono.setBackgroundColor(Color.gray);
		// cellFechaTurno.setBorder(Rectangle.NO_BORDER);
		cellFechaTurno.setPaddingBottom(5f);
		cellFechaTurno.setBackgroundColor(Color.gray);
		// creamos tabla
		PdfPTable tablaTurnosOcupados = new PdfPTable(7);
		// tabla1.setSpacingAfter(20);
		// Ancho de la primera columna
		tablaTurnosOcupados.setWidthPercentage(100);
		tablaTurnosOcupados.getDefaultCell().setBorder(0);
		// tablaTurnosOcupados.setSpacingBefore(220);
		tablaTurnosOcupados.setSpacingAfter(15);
		// Define los anchos absolutos de cada columna
		float[] columnWidths = { 200f, 390f, 310f, 390f, 190f, 230f, 290f };
		// Asigna los anchos a la tabla
		tablaTurnosOcupados.setWidths(columnWidths);

		// Agregamos a la tabla los encabezados y su formato.
		tablaTurnosOcupados.addCell(cellFechaAlta);
		tablaTurnosOcupados.addCell(cellProfesional);
		tablaTurnosOcupados.addCell(cellEspecialidad);
		tablaTurnosOcupados.addCell(cellPaciente);
		tablaTurnosOcupados.addCell(cellDni);
		tablaTurnosOcupados.addCell(cellTelefono);
		tablaTurnosOcupados.addCell(cellFechaTurno);

		// Recorrer el List<> de turnos ocupados.
		for (int i = 0; i < turnosocupadosfechas.size(); i++) {

			// Agregar una fila a la tabla
			Turno item = turnosocupadosfechas.get(i);

			// Formatear la fecha de creacion
			// ******************************************************
			LocalDateTime dateCreacion = item.getFechaCreacion();
			DateTimeFormatter formatterFechaCreacion = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String formattedDateCreation = dateCreacion.format(formatterFechaCreacion);
			// *************************************************************************************

			// Formatear la fecha del Turno
			// ********************************************************
			Date dateTurno = item.getPlanilladetalle().getRangoFechaHora();
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			String formattedDateTurno = df.format(dateTurno);
			// *************************************************************************************

			// Crea una fuente Helvetica tamaño 10
			Font smallFontBlack = FontFactory.getFont(FontFactory.HELVETICA, 8f);
			Font smallFontRed = new Font(Font.HELVETICA, 8, Font.NORMAL, Color.RED);

			// Crear celdas para cada dato del objeto
			PdfPCell celdaFechaAlta = new PdfPCell(new Phrase(String.valueOf(formattedDateCreation), smallFontBlack));
			PdfPCell celdaProfesional = new PdfPCell(new Phrase(item.getProfesional().getApellido().toUpperCase() + " "
					+ item.getProfesional().getNombre().toUpperCase(), smallFontBlack));
			PdfPCell celdaEspecialidad = new PdfPCell(
					new Phrase(item.getEspecialidad().getEspecialidadCodigo().toUpperCase() + " - "
							+ item.getEspecialidad().getEspecialidadNombre().toUpperCase(), smallFontBlack));
			PdfPCell celdaPaciente = new PdfPCell(new Phrase(
					item.getPaciente().getApellido().toUpperCase() + " " + item.getPaciente().getNombre().toUpperCase(),
					smallFontBlack));
			PdfPCell celdaDni = new PdfPCell(new Phrase(item.getPaciente().getDni(), smallFontBlack));
			PdfPCell celdaTelefono = new PdfPCell(
					new Phrase(String.valueOf(item.getPaciente().getTelefono()), smallFontBlack));
			PdfPCell celdaFechaTurno = new PdfPCell(new Phrase(String.valueOf(formattedDateTurno), smallFontRed));

			// Alto de las celdas
			celdaFechaAlta.setMinimumHeight(25f); // Establece una altura mínima
			celdaProfesional.setMinimumHeight(25f); // Establece una altura mínima
			celdaEspecialidad.setMinimumHeight(25f); // Establece una altura mínima
			celdaPaciente.setMinimumHeight(25f); // Establece una altura mínima
			celdaDni.setMinimumHeight(25f); // Establece una altura mínima
			celdaTelefono.setMinimumHeight(25f); // Establece una altura mínima
			celdaFechaTurno.setMinimumHeight(25f); // Establece una altura mínima

			// Añadir las celdas a la tabla sin formato.
			tablaTurnosOcupados.addCell(celdaFechaAlta);
			tablaTurnosOcupados.addCell(celdaProfesional);
			tablaTurnosOcupados.addCell(celdaEspecialidad);
			tablaTurnosOcupados.addCell(celdaPaciente);
			tablaTurnosOcupados.addCell(celdaDni);
			tablaTurnosOcupados.addCell(celdaTelefono);
			tablaTurnosOcupados.addCell(celdaFechaTurno);

			// Si se alcanza el límite de filas por página o es el último elemento, añadir
			// la tabla y crear una nueva página
			if ((i + 1) % ROWS_PER_PAGE == 0 || (i + 1) == turnosocupadosfechas.size()) {
				document.add(img);
				document.add(tablaTitulo);
				document.add(tableFechaHora);
				document.add(tablaPeriodo);
				document.add(tablaTurnosOcupados);

				// PIE DE PAGINA
				// -----------------------------------------------------------------------------
				PdfContentByte cb = writer.getDirectContent();
				BaseFont bf = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1252, BaseFont.EMBEDDED);
				cb.setFontAndSize(bf, 10);

				String textoPie = "SIGAT - Sistema General Administrativo de Turnos | Página: ";
				String numPagina = String.valueOf(writer.getPageNumber());

				// Posicionar el pie de página en la parte inferior
				cb.beginText();
				cb.setTextMatrix(160, 30); // Ajusta la posición X e Y según necesites
				cb.showText(textoPie + numPagina);
				cb.endText();
				// fin pie pagina---------------------------------------------------------------

				tablaTurnosOcupados.flushContent(); // Limpiar la tabla para la siguiente página

				if ((i + 1) < turnosocupadosfechas.size()) {
					document.newPage(); // Crear una nueva página si hay más datos

				}
			}
		}
	}

}

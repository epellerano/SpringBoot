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
import com.sigat.springboot.app.models.entity.PlanillaCabecera;
import com.sigat.springboot.app.models.entity.Profesional;
import com.sigat.springboot.app.models.entity.Turno;
import com.sigat.springboot.app.models.entity.Vinculacion;
import com.sigat.springboot.app.models.service.IProfesionalService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component("planillasActivasFechasPdfView")
public class PlanillasActivasFechasPdfView extends AbstractPdfView {

	@Override
	protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		// response.setHeader("Content-Disposition", "inline;
		// filename=\"listado-profesionales.pdf\"");

		// 1. Formatear la fecha actual
		String fecha = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
		String nombreArchivo = "planillasActivas_" + fecha + ".pdf";

		// 2. Configurar el nombre del archivo en la cabecera
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + nombreArchivo + "\"");

		// Obtener los datos del modelo(ReporteController) para armart el pdf.
		@SuppressWarnings("unchecked")
		List<PlanillaCabecera> planillasactivasfechas = (List<PlanillaCabecera>) model.get("planillasactivasfechas");

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
		String titulo = "Listado de Planillas Generadas";
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

		// Planillas --------------------------------------------------------
		Font whiteFont = new Font(Font.HELVETICA, 9, Font.NORMAL, Color.white);
		PdfPCell cellProfesional = new PdfPCell(new Phrase("PROFESIONAL", whiteFont));
		PdfPCell cellEspecialidad = new PdfPCell(new Phrase("ESPECIALIDAD", whiteFont));
		PdfPCell cellDia = new PdfPCell(new Phrase("DIA", whiteFont));
		PdfPCell cellFechaIni = new PdfPCell(new Phrase("FECHA INI", whiteFont));
		PdfPCell cellFechaFin = new PdfPCell(new Phrase("FECHA FIN", whiteFont));
		PdfPCell cellDesdeTm = new PdfPCell(new Phrase("DESDE", whiteFont));
		PdfPCell cellHastaTm = new PdfPCell(new Phrase("HASTA", whiteFont));
		PdfPCell cellMinTm = new PdfPCell(new Phrase("MIN", whiteFont));
		PdfPCell cellDesdeTt = new PdfPCell(new Phrase("DESDE", whiteFont));
		PdfPCell cellHastaTt = new PdfPCell(new Phrase("HASTA", whiteFont));
		PdfPCell cellMinTt = new PdfPCell(new Phrase("MIN", whiteFont));
		PdfPCell cellEstado = new PdfPCell(new Phrase("ESTADO", whiteFont));

		// CELDA TITULO SIN BORDE Y CON MARGEN INFERIOR
		// cellProfesional.setBorder(Rectangle.NO_BORDER);
		cellProfesional.setPaddingBottom(5f);
		cellProfesional.setBackgroundColor(Color.gray);
		// cellEspecialidad.setBorder(Rectangle.NO_BORDER);
		cellEspecialidad.setPaddingBottom(5f);
		cellEspecialidad.setBackgroundColor(Color.gray);
		// cellDia.setBorder(Rectangle.NO_BORDER);
		cellDia.setPaddingBottom(5f);
		cellDia.setBackgroundColor(Color.gray);
		// cellFechaIni.setBorder(Rectangle.NO_BORDER);
		cellFechaIni.setPaddingBottom(5f);
		cellFechaIni.setBackgroundColor(Color.gray);
		// cellFechaFin.setBorder(Rectangle.NO_BORDER);
		cellFechaFin.setPaddingBottom(5f);
		cellFechaFin.setBackgroundColor(Color.gray);
		// cellDesdeTm.setBorder(Rectangle.NO_BORDER);
		cellDesdeTm.setPaddingBottom(5f);
		cellDesdeTm.setBackgroundColor(Color.gray);
		// cellHastaTm.setBorder(Rectangle.NO_BORDER);
		cellHastaTm.setPaddingBottom(5f);
		cellHastaTm.setBackgroundColor(Color.gray);
		// cellMinTm.setBorder(Rectangle.NO_BORDER);
		cellMinTm.setPaddingBottom(5f);
		cellMinTm.setBackgroundColor(Color.gray);
		// cellDesdeTt.setBorder(Rectangle.NO_BORDER);
		cellDesdeTt.setPaddingBottom(5f);
		cellDesdeTt.setBackgroundColor(Color.gray);
		// cellHastaTt.setBorder(Rectangle.NO_BORDER);
		cellHastaTt.setPaddingBottom(5f);
		cellHastaTt.setBackgroundColor(Color.gray);
		// cellMinTt.setBorder(Rectangle.NO_BORDER);
		cellMinTt.setPaddingBottom(5f);
		cellMinTt.setBackgroundColor(Color.gray);
		// cellEstado.setBorder(Rectangle.NO_BORDER);
		cellEstado.setPaddingBottom(5f);
		cellEstado.setBackgroundColor(Color.gray);

		// creamos tabla
		PdfPTable tablaPlanillasActivas = new PdfPTable(12);
		// tablaPlanillasActivas.setSpacingAfter(20);
		// Ancho de la primera columna
		tablaPlanillasActivas.setWidthPercentage(100);
		tablaPlanillasActivas.getDefaultCell().setBorder(0);
		// tablaPlanillasActivas.setSpacingBefore(220);
		tablaPlanillasActivas.setSpacingAfter(15);
		// Define los anchos absolutos de cada columna
		float[] columnWidths = { 390f, 390f, 190f, 190f, 190f, 190f, 190f, 190f, 190f, 190f, 190f, 200f };
		// Asigna los anchos a la tabla
		tablaPlanillasActivas.setWidths(columnWidths);

		// Agregamos a la tabla los encabezados y su formato.
		tablaPlanillasActivas.addCell(cellProfesional);
		tablaPlanillasActivas.addCell(cellEspecialidad);
		tablaPlanillasActivas.addCell(cellDia);
		tablaPlanillasActivas.addCell(cellFechaIni);
		tablaPlanillasActivas.addCell(cellFechaFin);
		tablaPlanillasActivas.addCell(cellDesdeTm);
		tablaPlanillasActivas.addCell(cellHastaTm);
		tablaPlanillasActivas.addCell(cellMinTm);
		tablaPlanillasActivas.addCell(cellDesdeTt);
		tablaPlanillasActivas.addCell(cellHastaTt);
		tablaPlanillasActivas.addCell(cellMinTt);
		tablaPlanillasActivas.addCell(cellEstado);

		// Recorrer el List<> de turnos ocupados.
		for (int i = 0; i < planillasactivasfechas.size(); i++) {

			// Agregar una fila a la tabla
			PlanillaCabecera item = planillasactivasfechas.get(i);

			// Crea una fuente Helvetica tamaño 10
			Font smallFontBlack = FontFactory.getFont(FontFactory.HELVETICA, 8f);
			Font smallFontRed = new Font(Font.HELVETICA, 8, Font.NORMAL, Color.RED);

			// Crear celdas para cada dato del objeto
			PdfPCell celdaProfesional = new PdfPCell(new Phrase(item.getProfesional().getApellido().toUpperCase() + " "
					+ item.getProfesional().getNombre().toUpperCase(), smallFontBlack));
			PdfPCell celdaEspecialidad = new PdfPCell(
					new Phrase(item.getEspecialidad().getEspecialidadCodigo().toUpperCase() + " - "
							+ item.getEspecialidad().getEspecialidadNombre().toUpperCase(), smallFontBlack));
			PdfPCell celdaDia = new PdfPCell(new Phrase(item.getDia().getNombre(), smallFontBlack));
			PdfPCell celdaFechaIni = new PdfPCell(new Phrase(item.getFechaInicio().toString(), smallFontBlack));
			PdfPCell celdaFechaFin = new PdfPCell(new Phrase(item.getFechaFinal().toString(), smallFontBlack));
			PdfPCell celdaDesdeTm = new PdfPCell(new Phrase(item.getHoraInicialR1(), smallFontBlack));
			PdfPCell celdaHastaTm = new PdfPCell(new Phrase(item.getHoraFinalR1(), smallFontBlack));
			PdfPCell celdaMinTm = new PdfPCell(new Phrase(item.getIntervaloR1().toString(), smallFontBlack));
			PdfPCell celdaDesdeTt = new PdfPCell(new Phrase(item.getHoraInicialR2(), smallFontBlack));
			PdfPCell celdaHastaTt = new PdfPCell(new Phrase(item.getHoraFinalR2(), smallFontBlack));
			PdfPCell celdaMinTt = new PdfPCell(new Phrase(item.getIntervaloR2().toString(), smallFontBlack));
			PdfPCell celdaEstado = new PdfPCell(new Phrase(item.getEstado().getNombre(), smallFontRed));

			// Alto de las celdas
			celdaProfesional.setMinimumHeight(25f); // Establece una altura mínima
			celdaEspecialidad.setMinimumHeight(25f); // Establece una altura mínima
			celdaDia.setMinimumHeight(25f); // Establece una altura mínima
			celdaFechaIni.setMinimumHeight(25f); // Establece una altura mínima
			celdaFechaFin.setMinimumHeight(25f); // Establece una altura mínima
			celdaDesdeTm.setMinimumHeight(25f); // Establece una altura mínima
			celdaHastaTm.setMinimumHeight(25f); // Establece una altura mínima
			celdaMinTm.setMinimumHeight(25f); // Establece una altura mínima
			celdaDesdeTt.setMinimumHeight(25f); // Establece una altura mínima
			celdaHastaTt.setMinimumHeight(25f); // Establece una altura mínima
			celdaMinTt.setMinimumHeight(25f); // Establece una altura mínima
			celdaEstado.setMinimumHeight(25f); // Establece una altura mínima

			// Añadir las celdas a la tabla sin formato.
			tablaPlanillasActivas.addCell(celdaProfesional);
			tablaPlanillasActivas.addCell(celdaEspecialidad);
			tablaPlanillasActivas.addCell(celdaDia);
			tablaPlanillasActivas.addCell(celdaFechaIni);
			tablaPlanillasActivas.addCell(celdaFechaFin);
			tablaPlanillasActivas.addCell(celdaDesdeTm);
			tablaPlanillasActivas.addCell(celdaHastaTm);
			tablaPlanillasActivas.addCell(celdaMinTm);
			tablaPlanillasActivas.addCell(celdaDesdeTt);
			tablaPlanillasActivas.addCell(celdaHastaTt);
			tablaPlanillasActivas.addCell(celdaMinTt);
			tablaPlanillasActivas.addCell(celdaEstado);

			// Si se alcanza el límite de filas por página o es el último elemento, añadir
			// la tabla y crear una nueva página
			if ((i + 1) % ROWS_PER_PAGE == 0 || (i + 1) == planillasactivasfechas.size()) {
				document.add(img);
				document.add(tablaTitulo);
				document.add(tableFechaHora);
				document.add(tablaPeriodo);
				document.add(tablaPlanillasActivas);

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

				tablaPlanillasActivas.flushContent(); // Limpiar la tabla para la siguiente página

				if ((i + 1) < planillasactivasfechas.size()) {
					document.newPage(); // Crear una nueva página si hay más datos

				}
			}
		}

		/*
		 * //PIE DE PAGINA PdfContentByte cb = writer.getDirectContent(); BaseFont bf =
		 * BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1252,
		 * BaseFont.EMBEDDED); cb.setFontAndSize(bf, 10);
		 * 
		 * String textoPie =
		 * "SIGAT - Sistema General Administrativo de Turnos | Página: "; String
		 * numPagina = String.valueOf(writer.getPageNumber());
		 * 
		 * // Posicionar el pie de página en la parte inferior cb.beginText();
		 * cb.setTextMatrix(160, 30); // Ajusta la posición X e Y según necesites
		 * cb.showText(textoPie + numPagina); cb.endText();
		 */

	}

}

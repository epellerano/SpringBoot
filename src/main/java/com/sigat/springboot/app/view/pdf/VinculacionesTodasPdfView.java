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

@Component("vinculacionesTodasPdfView")
public class VinculacionesTodasPdfView extends AbstractPdfView {

	@Override
	protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		// response.setHeader("Content-Disposition", "inline;
		// filename=\"listado-profesionales.pdf\"");

		// 1. Formatear la fecha actual
		//String fecha = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
		//String nombreArchivo = "vinculacionesAll_" + fecha + ".pdf";

		// 2. Configurar el nombre del archivo en la cabecera
		//response.setContentType("application/pdf");
		//response.setHeader("Content-Disposition", "attachment; filename=\"" + nombreArchivo + "\"");

		// Obtener los datos del modelo(ReporteController) para armart el pdf.
		@SuppressWarnings("unchecked")
		List<Vinculacion> vinculacionestodas = (List<Vinculacion>) model.get("vinculacionestodas");

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
		String titulo = "Listado de Vinculaciones Generales";
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
		tableFechaHora.setSpacingAfter(10);
		// Añadir contenido a la tabla sin borde
		tableFechaHora.getDefaultCell().setBorder(0);
		tableFechaHora.addCell(celdaFechaHoraFormateada);
		// -----------------------------------------------------------------------------------------------------------
		

		// VINCULACIONES ------------------------------------------------------------
		Font whiteFont = new Font(Font.HELVETICA, 9, Font.NORMAL, Color.white);
		PdfPCell cellCodigoProf = new PdfPCell(new Phrase("COD", whiteFont));
		PdfPCell cellProfesional = new PdfPCell(new Phrase("PROFESIONAL", whiteFont));
		PdfPCell cellEspecialidadVinculada = new PdfPCell(new Phrase("ESPECIALIDAD VINCULADA", whiteFont));
		PdfPCell cellObservaciones = new PdfPCell(new Phrase("OBSERVACIONES PARA TURNOS", whiteFont));
		// CELDA TITULO SIN BORDE Y CON MARGEN INFERIOR
		// cellCodigoProf.setBorder(Rectangle.NO_BORDER);
		cellCodigoProf.setPaddingBottom(5f);
		cellCodigoProf.setBackgroundColor(Color.gray);
		// cellProfesional.setBorder(Rectangle.NO_BORDER);
		cellProfesional.setPaddingBottom(5f);
		cellProfesional.setBackgroundColor(Color.gray);
		// cellEspecialidadVinculada.setBorder(Rectangle.NO_BORDER);
		cellEspecialidadVinculada.setPaddingBottom(5f);
		cellEspecialidadVinculada.setBackgroundColor(Color.gray);
		// cellObservaciones.setBorder(Rectangle.NO_BORDER);
		cellObservaciones.setPaddingBottom(5f);
		cellObservaciones.setBackgroundColor(Color.gray);

		// creamos tabla
		PdfPTable tablaVinculacionesAll = new PdfPTable(4);
		// tablaVinculacionesAll.setSpacingAfter(20);
		// Ancho de la primera columna
		tablaVinculacionesAll.setWidthPercentage(100);
		tablaVinculacionesAll.getDefaultCell().setBorder(0);
		// tablaVinculacionesAll.setSpacingBefore(220);
		tablaVinculacionesAll.setSpacingAfter(15);
		// Define los anchos absolutos de cada columna
		float[] columnWidths = {65f ,240f, 240f, 650f };
		// Asigna los anchos a la tabla
		tablaVinculacionesAll.setWidths(columnWidths);

		// Agregamos a la tabla los encabezados y su formato.
		tablaVinculacionesAll.addCell(cellCodigoProf);
		tablaVinculacionesAll.addCell(cellProfesional);
		tablaVinculacionesAll.addCell(cellEspecialidadVinculada);
		tablaVinculacionesAll.addCell(cellObservaciones);

		// Recorrer el List<> de turnos ocupados.
		for (int i = 0; i < vinculacionestodas.size(); i++) {

			// Agregar una fila a la tabla
			Vinculacion item = vinculacionestodas.get(i);

			// Crea una fuente Helvetica tamaño 10
			Font smallFontBlack = FontFactory.getFont(FontFactory.HELVETICA, 8f);
			Font smallFontRed = new Font(Font.HELVETICA, 8, Font.NORMAL, Color.RED);

			// Crear celdas para cada dato del objeto	
			PdfPCell celdaCodigoProf = new PdfPCell(new Phrase(item.getProfesional().getCodigo().toUpperCase(),smallFontBlack));
			PdfPCell celdaProfesional = new PdfPCell(new Phrase(item.getProfesional().getApellido().toUpperCase() + " "
					+ item.getProfesional().getNombre().toUpperCase(), smallFontBlack));
			PdfPCell celdaEspecVinculada = new PdfPCell(
					new Phrase(item.getEspecialidad().getEspecialidadCodigo().toUpperCase() + " - "
							+ item.getEspecialidad().getEspecialidadNombre().toUpperCase(), smallFontBlack));
			PdfPCell celdaObserv = new PdfPCell(new Phrase(item.getObservacion().toUpperCase(), smallFontRed));

			// Alto de las celdas
			celdaCodigoProf.setMinimumHeight(25f); // Establece una altura mínima
			celdaProfesional.setMinimumHeight(25f); // Establece una altura mínima
			celdaEspecVinculada.setMinimumHeight(25f); // Establece una altura mínima
			celdaObserv.setMinimumHeight(25f); // Establece una altura mínima

			// Añadir las celdas a la tabla sin formato.
			tablaVinculacionesAll.addCell(celdaCodigoProf);
			tablaVinculacionesAll.addCell(celdaProfesional);
			tablaVinculacionesAll.addCell(celdaEspecVinculada);
			tablaVinculacionesAll.addCell(celdaObserv);
			
			// Si se alcanza el límite de filas por página o es el último elemento, añadir
			// la tabla y crear una nueva página
			if ((i + 1) % ROWS_PER_PAGE == 0 || (i + 1) == vinculacionestodas.size()) {
				document.add(img);
				document.add(tablaTitulo);
				document.add(tableFechaHora);
				document.add(tablaVinculacionesAll);

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

				tablaVinculacionesAll.flushContent(); // Limpiar la tabla para la siguiente página

				if ((i + 1) < vinculacionestodas.size()) {
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

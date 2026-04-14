package com.sigat.springboot.app.view.pdf;

import java.awt.Color;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractPdfView;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import com.sigat.springboot.app.models.entity.Especialidad;
import com.sigat.springboot.app.models.entity.Profesional;
import com.sigat.springboot.app.models.entity.Vinculacion;
import com.sigat.springboot.app.models.service.IProfesionalService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component("especialidadPdfView")
public class EspecialidadPdfView extends AbstractPdfView {

	@Override
	protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		// response.setHeader("Content-Disposition", "inline;
		// filename=\"listado-especialidades.pdf\"");

		// Obtener los datos del modelo(ReporteController)
		@SuppressWarnings("unchecked")
		List<Especialidad> especialidades = (List<Especialidad>) model.get("especialidades");

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
		String titulo = "Listado de Especialidades";
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
		PdfPTable tableFechaHora = new PdfPTable(1); // 1 columnas
		// margen inferior
		tableFechaHora.setSpacingAfter(20);
		// Añadir contenido a la tabla sin borde
		tableFechaHora.getDefaultCell().setBorder(0);
		tableFechaHora.addCell(celdaFechaHoraFormateada);
		// -----------------------------------------------------------------------------------------------------------

		// ESPECIALIDAD ------------------------------------------------------------
		Font whiteFont = new Font(Font.HELVETICA, 12, Font.NORMAL, Color.white);
		PdfPCell cellNombre = new PdfPCell(new Phrase("NOMBRE", whiteFont));
		PdfPCell cellCodigo = new PdfPCell(new Phrase("CODIGO", whiteFont));
		// CELDA TITULO SIN BORDE Y CON MARGEN INFERIOR
		// cellNombre.setBorder(Rectangle.NO_BORDER);
		cellNombre.setPaddingBottom(5f);
		cellNombre.setBackgroundColor(Color.gray);
		// cellCodigo.setBorder(Rectangle.NO_BORDER);
		cellCodigo.setPaddingBottom(5f);
		cellCodigo.setBackgroundColor(Color.gray);

		// Creamos la tabla
		PdfPTable tablaEspecialidad = new PdfPTable(2);
		// tabla1.setSpacingAfter(20);
		// Ancho de la primera columna
		tablaEspecialidad.setWidthPercentage(90);
		tablaEspecialidad.getDefaultCell().setBorder(0);
		// tablaProfesional.setSpacingBefore(220);
		tablaEspecialidad.setSpacingAfter(15);
		// Define los anchos absolutos de cada columna
		float[] columnWidths = { 200f, 130f};
		// Asigna los anchos a la tabla
		tablaEspecialidad.setWidths(columnWidths);
		// Agregamos las celdas de Titulos.
		tablaEspecialidad.addCell(cellNombre);
		tablaEspecialidad.addCell(cellCodigo);

		// Recorrer la lista de Spring
		for (int i = 0; i < especialidades.size(); i++) {
			// Agregar una fila a la tabla
			Especialidad item = especialidades.get(i);
			// Crear celdas para cada dato del objeto
			PdfPCell celdaNombre = new PdfPCell(new Phrase(item.getEspecialidadNombre()));
			PdfPCell celdaCodigo = new PdfPCell(new Phrase(String.valueOf(item.getEspecialidadCodigo())));
			// Añadir las celdas a la tabla
			tablaEspecialidad.addCell(celdaNombre);
			tablaEspecialidad.addCell(celdaCodigo);
			// Si se alcanza el límite de filas por página o es el último elemento, añadir
			// la tabla y crear una nueva página
			if ((i + 1) % ROWS_PER_PAGE == 0 || (i + 1) == especialidades.size()) {
				document.add(img);
				document.add(tablaTitulo);
				document.add(tableFechaHora);
				document.add(tablaEspecialidad);

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

				tablaEspecialidad.flushContent(); // Limpiar la tabla para la siguiente página

				if ((i + 1) < especialidades.size()) {
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

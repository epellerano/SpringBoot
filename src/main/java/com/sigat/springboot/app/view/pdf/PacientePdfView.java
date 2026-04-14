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
import com.sigat.springboot.app.models.entity.Paciente;
import com.sigat.springboot.app.models.entity.Profesional;
import com.sigat.springboot.app.models.entity.Vinculacion;
import com.sigat.springboot.app.models.service.IProfesionalService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component("pacientePdfView")
public class PacientePdfView extends AbstractPdfView {

	@Override
	protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		// response.setHeader("Content-Disposition", "inline;
		// filename=\"listado-profesionales.pdf\"");

		// Obtener los datos del modelo(ReporteController)
		@SuppressWarnings("unchecked")
		List<Paciente> pacientes = (List<Paciente>) model.get("pacientes");

		// Definir la cantidad de filas por página
		final int ROWS_PER_PAGE = 20;

		// LOGO
		// -----------------------------------------------------------------------------------------------
		Image img = Image.getInstance(getClass().getClassLoader().getResource("static/images/LogoSigat.png"));
		// ajusta el tamaño y la posición de la imagen
		img.scaleAbsolute(52, 53); // Ancho y alto
		// img.setAbsolutePosition(60, 780); // Posición X, Y
		img.setAlignment(Element.ALIGN_LEFT);
		img.setIndentationLeft(10f);
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
		tablaTitulo.setWidthPercentage(100f);
		// Estilo y color subrayado.
		String titulo = "Listado de Pacientes";
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
		tableFechaHora.setSpacingAfter(20);
		// Añadir contenido a la tabla sin borde
		tableFechaHora.getDefaultCell().setBorder(0);
		tableFechaHora.addCell(celdaFechaHoraFormateada);
		// -----------------------------------------------------------------------------------------------------------

		// PACIENTE ------------------------------------------------------------
		Font whiteFont = new Font(Font.HELVETICA, 12, Font.NORMAL, Color.white);
		PdfPCell cellNombre = new PdfPCell(new Phrase("NOMBRE", whiteFont));
		PdfPCell cellNSocio = new PdfPCell(new Phrase("NSOCIO", whiteFont));
		PdfPCell cellDni = new PdfPCell(new Phrase("DNI", whiteFont));
		PdfPCell cellTelefono = new PdfPCell(new Phrase("TELEFONO", whiteFont));
		PdfPCell cellDomicilio = new PdfPCell(new Phrase("DOMICILIO", whiteFont));
		// CELDA TITULO SIN BORDE Y CON MARGEN INFERIOR
		// cellNombre.setBorder(Rectangle.NO_BORDER);
		cellNombre.setPaddingBottom(5f);
		cellNombre.setBackgroundColor(Color.gray);
		// cellNombre.setBorder(Rectangle.NO_BORDER);
		cellNSocio.setPaddingBottom(5f);
		cellNSocio.setBackgroundColor(Color.gray);
		// cellNSocio.setBorder(Rectangle.NO_BORDER);
		cellDni.setPaddingBottom(5f);
		cellDni.setBackgroundColor(Color.gray);
		// cellDni.setBorder(Rectangle.NO_BORDER);
		cellTelefono.setPaddingBottom(5f);
		cellTelefono.setBackgroundColor(Color.gray);
		// cellTelefono.setBorder(Rectangle.NO_BORDER);
		cellDomicilio.setPaddingBottom(5f);
		cellDomicilio.setBackgroundColor(Color.gray);
		// cellDomicilio.setBorder(Rectangle.NO_BORDER);

		// Creamos la tabla
		PdfPTable tablaPaciente = new PdfPTable(5);
		// tabla1.setSpacingAfter(20);
		// Ancho de la primera columna
		tablaPaciente.setWidthPercentage(100);
		tablaPaciente.getDefaultCell().setBorder(0);
		// tablaProfesional.setSpacingBefore(220);
		tablaPaciente.setSpacingAfter(15);
		// Define los anchos absolutos de cada columna
		float[] columnWidths = { 300f, 100f, 120f, 150f, 280f };
		// Asigna los anchos a la tabla
		tablaPaciente.setWidths(columnWidths);
		// Agregamos las celdas de Titulos.
		tablaPaciente.addCell(cellNombre);
		tablaPaciente.addCell(cellNSocio);
		tablaPaciente.addCell(cellDni);
		tablaPaciente.addCell(cellTelefono);
		tablaPaciente.addCell(cellDomicilio);

		// Recorrer la lista de Spring
		for (int i = 0; i < pacientes.size(); i++) {
			// Agregar una fila a la tabla
			Paciente item = pacientes.get(i);
			// Crear celdas para cada dato del objeto
			PdfPCell celdaNombre = new PdfPCell(new Phrase(item.getApellido() + " " + item.getNombre()));
			PdfPCell celdaNSocio = new PdfPCell(new Phrase(String.valueOf(item.getNumeroSocio())));
			PdfPCell celdaDni = new PdfPCell(new Phrase(String.valueOf(item.getDni())));
			PdfPCell celdaTelefono = new PdfPCell(new Phrase(String.valueOf(item.getTelefono())));
			PdfPCell celdaDomicilio = new PdfPCell(new Phrase(String.valueOf(item.getDomicilio())));

			// Añadir las celdas a la tabla
			tablaPaciente.addCell(celdaNombre);
			tablaPaciente.addCell(celdaNSocio);
			tablaPaciente.addCell(celdaDni);
			tablaPaciente.addCell(celdaTelefono);
			tablaPaciente.addCell(celdaDomicilio);
			// Si se alcanza el límite de filas por página o es el último elemento, añadir
			// la tabla y crear una nueva página
			if ((i + 1) % ROWS_PER_PAGE == 0 || (i + 1) == pacientes.size()) {
				document.add(img);
				document.add(tablaTitulo);
				document.add(tableFechaHora);
				document.add(tablaPaciente);

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

				tablaPaciente.flushContent(); // Limpiar la tabla para la siguiente página

				if ((i + 1) < pacientes.size()) {
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

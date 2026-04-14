package com.sigat.springboot.app.view.pdf;

import java.awt.Color;

import java.io.InputStream;
import java.sql.Timestamp;
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
import com.sigat.springboot.app.models.entity.Movimiento;
import com.sigat.springboot.app.models.entity.PlanillaCabecera;
import com.sigat.springboot.app.models.entity.Profesional;
import com.sigat.springboot.app.models.entity.Turno;
import com.sigat.springboot.app.models.entity.Vinculacion;
import com.sigat.springboot.app.models.service.IProfesionalService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component("movimientosFechasPdfView")
public class MovimientosFechasPdfView extends AbstractPdfView {

	@Override
	protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		// response.setHeader("Content-Disposition", "inline;
		// filename=\"listado-profesionales.pdf\"");
		
		// 1. Formatear la fecha actual
        String fecha = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        String nombreArchivo = "movimientos_" + fecha + ".pdf";

        // 2. Configurar el nombre del archivo en la cabecera
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + nombreArchivo + "\"");

		// Obtener los datos del modelo(ReporteController) para armart el pdf.
		@SuppressWarnings("unchecked")
		List<Movimiento> movimientosfechas = (List<Movimiento>) model.get("movimientosfechas");
		
		//Obtener fecha inicio del modelo obtenido de (ReporteController).
		String fechaInicioReporte = String.valueOf(model.get("fechaInicioReporte"));		
		//Convertimos fechaDesde al formato dd/MM/yyyy
        LocalDate date = LocalDate.parse(fechaInicioReporte);
        DateTimeFormatter outputFormatterFechaDesde = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String outputDateStringFechaDesde = date.format(outputFormatterFechaDesde);
        
        //Obtener fecha Final del modelo obtenido de (ReporteController).
        String fechaFinReporte = String.valueOf(model.get("fechaFinReporte"));
        //Convertimos fechaDesde al formato dd/MM/yyyy
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
		String titulo = "Listado de Movimientos Generadas";
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

		
		//Creamos la tabla Periodo (desde-Hasta).
		Font fuenteFechaHoraFormateadaHasta = new Font(Font.TIMES_ROMAN, 11, 0, Color.gray);		
		String periodo ="Datos obtenidos desde el " + outputDateStringFechaDesde + " hasta el " + outputDateStringFechaHasta;
		PdfPCell celdaPeriodo = new PdfPCell(new Phrase(periodo, fuenteFechaHoraFormateadaHasta));
		celdaPeriodo.setBorder(Rectangle.NO_BORDER);
		celdaPeriodo.setHorizontalAlignment(Element.ALIGN_CENTER);
		PdfPTable tablaPeriodo =new PdfPTable(1);
		tablaPeriodo.setSpacingAfter(20); // Añadir contenido a la tabla sin borde
		tablaPeriodo.getDefaultCell().setBorder(0);
		//agragamos la celdaPeriodo a la tabla.
		tablaPeriodo.addCell(celdaPeriodo);
		
		// Movimientos --------------------------------------------------------
		Font whiteFont = new Font(Font.HELVETICA, 9, Font.NORMAL, Color.white);
		PdfPCell cellFechaCreacion = new PdfPCell(new Phrase("FECHA ALTA", whiteFont));
		PdfPCell cellProfesional = new PdfPCell(new Phrase("PROFESIONAL", whiteFont));
		PdfPCell cellEspecialidad = new PdfPCell(new Phrase("ESPECIALIDAD", whiteFont));
		PdfPCell cellHoraIniTm = new PdfPCell(new Phrase("HORA INICIO", whiteFont));
		PdfPCell cellHoraFinTm = new PdfPCell(new Phrase("HORA FINAL", whiteFont));
		PdfPCell cellTotalTm = new PdfPCell(new Phrase("TOTAL", whiteFont));
		PdfPCell cellHoraIniTt = new PdfPCell(new Phrase("HORA INICIO", whiteFont));
		PdfPCell cellHoraFinTt = new PdfPCell(new Phrase("HORA FINAL", whiteFont));
		PdfPCell cellTotalTt = new PdfPCell(new Phrase("TOTAL", whiteFont));
		PdfPCell cellTotalGeneral = new PdfPCell(new Phrase("TOTAL GRAL", whiteFont));
		
		// CELDA TITULO SIN BORDE Y CON MARGEN INFERIOR
		//cellFechaCreacion.setBorder(Rectangle.NO_BORDER);
		cellFechaCreacion.setPaddingBottom(5f);
		cellFechaCreacion.setBackgroundColor(Color.gray);
		// cellProfesional.setBorder(Rectangle.NO_BORDER);
		cellProfesional.setPaddingBottom(5f);
		cellProfesional.setBackgroundColor(Color.gray);
		// cellEspecialidad.setBorder(Rectangle.NO_BORDER);
		cellEspecialidad.setPaddingBottom(5f);
		cellEspecialidad.setBackgroundColor(Color.gray);
		// cellHoraIniTm.setBorder(Rectangle.NO_BORDER);
		cellHoraIniTm.setPaddingBottom(5f);
		cellHoraIniTm.setBackgroundColor(Color.gray);
		// cellHoraFinTm.setBorder(Rectangle.NO_BORDER);
		cellHoraFinTm.setPaddingBottom(5f);
		cellHoraFinTm.setBackgroundColor(Color.gray);
		// cellTotalTm.setBorder(Rectangle.NO_BORDER);
		cellTotalTm.setPaddingBottom(5f);
		cellTotalTm.setBackgroundColor(Color.gray);
		// cellHoraIniTt.setBorder(Rectangle.NO_BORDER);
		cellHoraIniTt.setPaddingBottom(5f);
		cellHoraIniTt.setBackgroundColor(Color.gray);
		//cellHoraFinTt.setBorder(Rectangle.NO_BORDER);
		cellHoraFinTt.setPaddingBottom(5f);
		cellHoraFinTt.setBackgroundColor(Color.gray);
		//cellTotalTt.setBorder(Rectangle.NO_BORDER);
		cellTotalTt.setPaddingBottom(5f);
		cellTotalTt.setBackgroundColor(Color.gray);
		//cellTotalGeneral.setBorder(Rectangle.NO_BORDER);
		cellTotalGeneral.setPaddingBottom(5f);
		cellTotalGeneral.setBackgroundColor(Color.gray);
		
		
		// creamos tabla
		PdfPTable tablaMovimientos = new PdfPTable(10);
		// tablaMovimientos.setSpacingAfter(20);
		// Ancho de la primera columna
		tablaMovimientos.setWidthPercentage(100);
		tablaMovimientos.getDefaultCell().setBorder(0);
		// tablaMovimientos.setSpacingBefore(220);
		tablaMovimientos.setSpacingAfter(15);
		// Define los anchos absolutos de cada columna
		float[] columnWidths = { 210f, 410f, 410f, 160f, 160f, 160f, 160f, 160f, 160f, 160f };
		// Asigna los anchos a la tabla
		tablaMovimientos.setWidths(columnWidths);

		// Agregamos a la tabla los encabezados y su formato.
		tablaMovimientos.addCell(cellFechaCreacion);
		tablaMovimientos.addCell(cellProfesional); 
		tablaMovimientos.addCell(cellEspecialidad);		
		tablaMovimientos.addCell(cellHoraIniTm);
		tablaMovimientos.addCell(cellHoraFinTm);
		tablaMovimientos.addCell(cellTotalTm);
		tablaMovimientos.addCell(cellHoraIniTt);
		tablaMovimientos.addCell(cellHoraFinTt);
		tablaMovimientos.addCell(cellTotalTt);
		tablaMovimientos.addCell(cellTotalGeneral);
		
		
		// Recorrer el List<> de turnos ocupados.
		for (int i = 0; i < movimientosfechas.size(); i++) {

			// Agregar una fila a la tabla
			Movimiento item = movimientosfechas.get(i);

			// Crea una fuente Helvetica tamaño 10
			Font smallFontBlack = FontFactory.getFont(FontFactory.HELVETICA, 8f);
			Font smallFontRed = new Font(Font.HELVETICA, 8, Font.NORMAL, Color.RED);
			//formateamos la fecha de creacion
			Date fechaCreacion = item.getCreateAt(); // O tu objeto fecha
			SimpleDateFormat formatterFechaCreacion = new SimpleDateFormat("dd/MM/yyyy");
			String fechaCreacionFormateada = formatterFechaCreacion.format(fechaCreacion);
			
			// Crear celdas para cada dato del objeto
			PdfPCell celdaFechaCreacion = new PdfPCell(new Phrase(fechaCreacionFormateada, smallFontBlack));
			PdfPCell celdaProfesional = new PdfPCell(new Phrase(
					item.getProfesional().getApellido().toUpperCase() + " " + item.getProfesional().getNombre().toUpperCase(), smallFontBlack));
			PdfPCell celdaEspecialidad = new PdfPCell(new Phrase(item.getEspecialidad().getEspecialidadCodigo().toUpperCase() + " - "
					+ item.getEspecialidad().getEspecialidadNombre().toUpperCase(), smallFontBlack));
			PdfPCell celdaHoraIniTm = new PdfPCell(new Phrase(item.getHoraIniManana(), smallFontBlack));
			PdfPCell celdaHoraFinTm = new PdfPCell(new Phrase(item.getHoraFinManana(), smallFontBlack));
			PdfPCell celdaTotalTm = new PdfPCell(new Phrase(item.getTotalManana(), smallFontBlack));
			PdfPCell celdaHoraIniTt = new PdfPCell(new Phrase(item.getHoraIniTarde(), smallFontBlack));
			PdfPCell celdaHoraFinTt = new PdfPCell(new Phrase(item.getHoraFinTarde(), smallFontBlack));
			PdfPCell celdaTotalTt = new PdfPCell(new Phrase(item.getTotalTarde(), smallFontBlack));
			PdfPCell celdaTotalGral = new PdfPCell(new Phrase(item.getTotalGeneral(), smallFontRed));
			
			//Alto de las celdas
			celdaFechaCreacion.setMinimumHeight(25f); // Establece una altura mínima
			celdaProfesional.setMinimumHeight(25f); // Establece una altura mínima
			celdaEspecialidad.setMinimumHeight(25f); // Establece una altura mínima			
			celdaHoraIniTm.setMinimumHeight(25f); // Establece una altura mínima
			celdaHoraFinTm.setMinimumHeight(25f); // Establece una altura mínima
			celdaTotalTm.setMinimumHeight(25f); // Establece una altura mínima
			celdaHoraIniTt.setMinimumHeight(25f); // Establece una altura mínima
			celdaHoraFinTt.setMinimumHeight(25f); // Establece una altura mínima
			celdaTotalTt.setMinimumHeight(25f); // Establece una altura mínima
			celdaTotalGral.setMinimumHeight(25f); // Establece una altura mínima
			
			// Añadir las celdas a la tabla sin formato.
			tablaMovimientos.addCell(celdaFechaCreacion);
			tablaMovimientos.addCell(celdaProfesional);
			tablaMovimientos.addCell(celdaEspecialidad);			
			tablaMovimientos.addCell(celdaHoraIniTm);
			tablaMovimientos.addCell(celdaHoraFinTm);
			tablaMovimientos.addCell(celdaTotalTm);
			tablaMovimientos.addCell(celdaHoraIniTt);
			tablaMovimientos.addCell(celdaHoraFinTt);
			tablaMovimientos.addCell(celdaTotalTt);
			tablaMovimientos.addCell(celdaTotalGral);

			// Si se alcanza el límite de filas por página o es el último elemento, añadir
			// la tabla y crear una nueva página
			if ((i + 1) % ROWS_PER_PAGE == 0 || (i + 1) == movimientosfechas.size()) {
				document.add(img);
				document.add(tablaTitulo);
				document.add(tableFechaHora);
				document.add(tablaPeriodo);
				document.add(tablaMovimientos);

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

				tablaMovimientos.flushContent(); // Limpiar la tabla para la siguiente página

				if ((i + 1) < movimientosfechas.size()) {
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

package com.sigat.springboot.app.view.pdf;

import java.awt.Color;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
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
import com.sigat.springboot.app.models.entity.Vinculacion;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component("vinculaciones/reporteVinculacion")
public class VinculacionPdfView extends AbstractPdfView{

	@Override
	protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//Obtenemos los datos para mostrar
		Vinculacion vinculacion = (Vinculacion) model.get("vinculacion");
		
		//LOGO -----------------------------------------------------------------------------------------------
		Image img = Image.getInstance(getClass().getClassLoader().getResource("static/images/LogoSigat.png"));
        // ajusta el tamaño y la posición de la imagen
        img.scaleAbsolute(75, 40); // Ancho y alto
        //img.setAbsolutePosition(60, 780); // Posición X, Y
        img.setAlignment(Element.ALIGN_LEFT);
        img.setIndentationLeft(20f);
        //----------------------------------------------------------------------------------------------------
		
        //TITULO ----------------------------------------------------------------
		PdfPTable tablaTitulo = new PdfPTable(1);
		//margen superior
		tablaTitulo.setSpacingBefore(10f);
		//margen inferior
		tablaTitulo.setSpacingAfter(3f);		
		// Alinear de la tabla completa
		tablaTitulo.setHorizontalAlignment(PdfPTable.ALIGN_CENTER);
		// Ancho de la primera columna
		tablaTitulo.setWidthPercentage(90); 
		// Estilo y color subrayado.
        String titulo = "Listado de Especialidad Vinculada del Profesional";
        Font fuenteTitulo = new Font(Font.TIMES_ROMAN, 12,0, Color.white);
        PdfPCell celdaTitulo = new PdfPCell(new Phrase(titulo, fuenteTitulo));
        // Configurar la celda para que no tenga borde
        celdaTitulo.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        //Alto del recuadro
        celdaTitulo.setFixedHeight(20);
        //esPacio entre el contenido y el borde del rectangulo.
        celdaTitulo.setPadding(2.0f);
        // Añade margen izquierda al texto
        //celdaTitulo.setPaddingLeft(10);         
        celdaTitulo.setBackgroundColor(Color.darkGray); //color de fondo
        //celdaTitulo.setBorder(Rectangle.NO_BORDER);
        // Añadir el Titulo
        tablaTitulo.addCell(celdaTitulo);
        //-------------------------------------------------------------------------
        
        
        // FECHA Y HORA (con estilo de Fuente) ----------------------------------------------------------------------
        LocalDateTime fechaHoraActual = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String fechaHoraFormateada = fechaHoraActual.format(formatter);
        Font fuenteFechaHoraFormateada = new Font(Font.TIMES_ROMAN, 12, 0, Color.gray);
        PdfPCell celdaFechaHoraFormateada = new PdfPCell(new Phrase("Informe generado el " + fechaHoraFormateada, fuenteFechaHoraFormateada));
        celdaFechaHoraFormateada.setBorder(Rectangle.NO_BORDER);
        celdaFechaHoraFormateada.setHorizontalAlignment(Element.ALIGN_CENTER);
        // Crear tabla para mostrar la fecha y hora 
        PdfPTable tableFechaHora = new PdfPTable(1); // 2 columnas
        //margen inferior
        tableFechaHora.setSpacingAfter(20);
        // Añadir contenido a la tabla sin borde
        tableFechaHora.getDefaultCell().setBorder(0);
        tableFechaHora.addCell(celdaFechaHoraFormateada);
        //-----------------------------------------------------------------------------------------------------------
        
		
				
		//PROFESIONAL
		PdfPTable tablaProfesional = new PdfPTable(1);
		//tabla1.setSpacingAfter(20);			
		
		Font blueFont = new Font(Font.HELVETICA, 12, Font.NORMAL, Color.ORANGE);
		PdfPCell celltablaProfesional = new PdfPCell(new Phrase("Datos del Profesional", blueFont));		
		celltablaProfesional.setFixedHeight(16); //Alto del recuadro
		celltablaProfesional.setPadding(1.0f); //centrado en el recuadro
		celltablaProfesional.setBorderColor(Color.darkGray);
		tablaProfesional.addCell(celltablaProfesional);
		tablaProfesional.getDefaultCell().setBorder(0); 
		tablaProfesional.setSpacingAfter(15);
		tablaProfesional.addCell("Nombre: " + vinculacion.getProfesional().getApellido() + " " + vinculacion.getProfesional().getNombre());
		tablaProfesional.addCell("Codigo:  " + vinculacion.getProfesional().getCodigo());
		tablaProfesional.addCell("Correo:   " + vinculacion.getProfesional().getEmail());
		
		//ESPECIALIDAD VINCULADA
		PdfPTable tablaEspecialidad = new PdfPTable(1);
		PdfPCell celltablaEspecialidad = new PdfPCell(new Phrase("Datos de la Especialidad", blueFont));		
		celltablaEspecialidad.setFixedHeight(16);//Alto del recuadro
		celltablaEspecialidad.setPadding(1.0f); //centrado en el recuadro
		celltablaEspecialidad.setBorderColor(Color.darkGray);
		tablaEspecialidad.addCell(celltablaEspecialidad);	
		tablaEspecialidad.getDefaultCell().setBorder(0);
		tablaEspecialidad.setSpacingAfter(10);
		tablaEspecialidad.addCell("Nombre: " + vinculacion.getEspecialidad().getEspecialidadNombre());
		tablaEspecialidad.addCell("Codigo:  " + vinculacion.getEspecialidad().getEspecialidadCodigo());
		
		
		//Agregamos las tablas al Documento en Orden
		document.add(img);
		document.add(tablaTitulo);
		document.add(tableFechaHora);
		document.add(tablaProfesional);
		document.add(tablaEspecialidad);
		
		/*
		 * PdfPTable tabla3 = new PdfPTable(4); tabla3.setWidths(new float [] {3.5f, 1,
		 * 1, 1}); tabla3.addCell(mensajes.getMessage("text.factura.form.item.nombre"));
		 * tabla3.addCell(mensajes.getMessage("text.factura.form.item.precio"));
		 * tabla3.addCell(mensajes.getMessage("text.factura.form.item.cantidad"));
		 * tabla3.addCell(mensajes.getMessage("text.factura.form.item.total"));
		 * 
		 * for(ItemFactura item: factura.getItems()) {
		 * tabla3.addCell(item.getProducto().getNombre());
		 * tabla3.addCell(item.getProducto().getPrecio().toString());
		 * 
		 * cell = new PdfPCell(new Phrase(item.getCantidad().toString()));
		 * cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); tabla3.addCell(cell);
		 * tabla3.addCell(item.calcularImporte().toString()); }
		 * 
		 * cell = new PdfPCell(new Phrase(mensajes.getMessage("text.factura.form.total")
		 * + ": ")); cell.setColspan(3);
		 * cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT); tabla3.addCell(cell);
		 * tabla3.addCell(factura.getTotal().toString());
		 * 
		 * document.add(tabla3);
		 */
		
		//PIE DE PAGINA
		// Configurar el pie de página
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
	}

		
}

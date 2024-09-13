package com.sigat.springboot.app.view.pdf;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.ElementListener;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.sigat.springboot.app.models.entity.Vinculacion;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component("vinculaciones/verVinculacion")
public class VinculacionPdfView extends AbstractPdfView{

	@Override
	protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Vinculacion vinculacion = (Vinculacion) model.get("vinculacion");
		
		//Creamos el titulo
		PdfPTable tabla = new PdfPTable(1);
		tabla.setSpacingAfter(20);
		tabla.addCell("LISTADO DE VINCULACION PROFESIONAL / ESPECIALIDAD");
		//Creamos una tabla
		PdfPTable tabla1 = new PdfPTable(1);
		tabla1.setSpacingAfter(20);			
		tabla1.addCell("Datos del Profesional");		
		tabla1.addCell(vinculacion.getProfesional().getApellido() + " " + vinculacion.getProfesional().getNombre());
		tabla1.addCell("Codigo: " + vinculacion.getProfesional().getCodigo());
		tabla1.addCell(vinculacion.getProfesional().getEmail());
		//Creamos otra tabla
		PdfPTable tabla2 = new PdfPTable(1);	
		tabla2.addCell("Datos de la Especialidad");		
		tabla2.addCell("Especialidad: " + vinculacion.getEspecialidad().getEspecialidadNombre());
		tabla2.addCell("Codigo: " + vinculacion.getEspecialidad().getEspecialidadCodigo());
		//Agregamos las tablas al Documento
		document.add(tabla);
		document.add(tabla1);
		document.add(tabla2);
		
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
	}

}

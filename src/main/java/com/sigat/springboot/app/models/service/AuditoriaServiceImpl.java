package com.sigat.springboot.app.models.service;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sigat.springboot.app.models.dao.IPlanillaDetalleDao;
import com.sigat.springboot.app.models.dao.ISobreTurnosDao;
import com.sigat.springboot.app.models.dao.ITurnosDao;
import com.sigat.springboot.app.models.entity.Especialidad;
import com.sigat.springboot.app.models.entity.Estado;
import com.sigat.springboot.app.models.entity.PlanillaDetalle;
import com.sigat.springboot.app.models.entity.Profesional;
import com.sigat.springboot.app.models.entity.Sobreturno;
import com.sigat.springboot.app.models.entity.Turno;
import com.sigat.springboot.app.models.entity.Vinculacion;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Service
public class AuditoriaServiceImpl implements IAuditoriaService {

	@Autowired
	private ITurnosDao turnoDao;
	@Autowired
	private ISobreTurnosDao sobreturnoDao;
	@Autowired
	private IPlanillaDetalleDao planilladetalleDao;

	// ESTE ES EL ÚNICO MÉTODO QUE DEBE QUEDAR PARA TURNOS INDIVIDUALES
    @Override
    @Transactional
    public String restaurarTurnoIndividual(Long turnoId) {
        Turno turno = turnoDao.findById(turnoId).orElse(null);
        if (turno == null) return "Error: Turno no encontrado.";

        try {
            PlanillaDetalle pd = turno.getPlanilladetalle();
            java.util.Date fechaTurnoDate = pd.getRangoFechaHora();
            
            // 1. REGLA DE TIEMPO
            if (fechaTurnoDate.before(new java.util.Date())) {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
                return "No se puede restaurar: El horario (" + sdf.format(fechaTurnoDate) + ") ya pasó.";
            }

            // 2. REGLA DE DISPONIBILIDAD (Acepta 3 y 6)
            Long estadoHuecoId = pd.getEstado().getId();
            if (estadoHuecoId != 3L && estadoHuecoId != 6L) {
                return "No se puede restaurar: El horario ya ha sido ocupado por otro paciente.";
            }

            // 3. RESTAURACIÓN
            Estado ocupado = new Estado();
            ocupado.setId(4L); 

            turno.setEstado(ocupado);
            pd.setEstado(ocupado);
            pd.setObservacion("PLANILLA GENERADA"); // Limpieza de "Congreso"

            // 4. PERSISTENCIA
            planilladetalleDao.save(pd);
            turnoDao.save(turno);
            
            return "OK";

        } catch (Exception e) {
            return "Error crítico: " + e.getMessage();
        }
    }
    
    
    @Override
    @Transactional(readOnly = true)
    public List<Turno> listarMonitorTurnos() {
        return turnoDao.listarMonitor();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sobreturno> listarMonitorSobreturnos() {
        return sobreturnoDao.listarMonitor();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanillaDetalle> listarMonitorPlanillas() {
        return planilladetalleDao.listarMonitor();
    }


}

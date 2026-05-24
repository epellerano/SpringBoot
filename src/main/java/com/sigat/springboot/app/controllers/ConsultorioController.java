package com.sigat.springboot.app.controllers;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;    
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sigat.springboot.app.models.entity.Estado;
import com.sigat.springboot.app.models.entity.HistoriaClinica;
import com.sigat.springboot.app.models.entity.Paciente;
import com.sigat.springboot.app.models.entity.Profesional;
import com.sigat.springboot.app.models.entity.Sobreturno;
import com.sigat.springboot.app.models.entity.Turno;
import com.sigat.springboot.app.models.service.IEstadoService;
import com.sigat.springboot.app.models.service.IHistoriaClinicaService;
import com.sigat.springboot.app.models.service.IPacienteService;
import com.sigat.springboot.app.models.service.IProfesionalService;
import com.sigat.springboot.app.models.service.ISobreTurnoService;
import com.sigat.springboot.app.models.service.ITurnoService;

@Controller
@RequestMapping("/consultorio")
@SessionAttributes("historiaClinica")
public class ConsultorioController {

    @Autowired
    private IHistoriaClinicaService historiaService;    
    @Autowired
    private IPacienteService pacienteService; // Para traer datos del paciente    
    @Autowired
    private IProfesionalService profesionalService;    
    @Autowired
    private ITurnoService turnoService;
    @Autowired
    private ISobreTurnoService sobreturnoService;
    @Autowired
    private IEstadoService estadoService;

    @GetMapping("/atender/{pacienteId}")
    public String abrirConsultorio(
            @PathVariable(value = "pacienteId") Long pacienteId, 
            @RequestParam(name="turno", required=false) Long turnoId, 
            @RequestParam(name="sobreturno", required=false) Long sobreturnoId, 
            Model model, RedirectAttributes flash) {
        
        // 1. Buscamos el paciente
        Paciente paciente = pacienteService.findOne(pacienteId); 
        
        if (paciente == null) {
            flash.addFlashAttribute("error", "El paciente no existe.");
            return "redirect:/pacientes/listarPaciente"; 
        }

        // 2. Preparamos la nueva historia clínica
        HistoriaClinica nueva = new HistoriaClinica();
        nueva.setPaciente(paciente);
        
        // 3. Identificamos el origen y el ID de la cita
        // Si viene sobreturnoId, ese manda.
        Long idAtencion = (sobreturnoId != null) ? sobreturnoId : turnoId;
        String origenAtencion = (sobreturnoId != null) ? "SOBRETURNO" : "TURNO";

        // 4. Cargamos el modelo para la vista
        model.addAttribute("paciente", paciente);
        model.addAttribute("historiaClinica", nueva);
        model.addAttribute("historial", historiaService.obtenerHistorialPorPaciente(pacienteId));
        model.addAttribute("titulo", "Atención Médica");
        
        // IMPORTANTE: Estos dos viajan al Formulario
        model.addAttribute("turnoId", idAtencion);         
        model.addAttribute("origen", origenAtencion);
        
        // Flag por si necesitas lógica visual extra
        model.addAttribute("esSobreturno", (sobreturnoId != null));
        
        return "consultorio/estacion-trabajo";
    }


    // Método para el Auto-save y Guardado Final
    //que recargue la página) y que redirija a la agenda.
    @PostMapping("/guardar")
    public String guardarConsulta(HistoriaClinica historiaClinica, 
                                   @RequestParam(name="turnoId", required=false) Long turnoId, 
                                   Authentication auth, RedirectAttributes flash) {
        try {
            // 1. Asignamos médico
            Profesional medico = profesionalService.findByUsername(auth.getName());
            if (medico != null) historiaClinica.setProfesional(medico);

            // 2. Guardamos (solo una vez al presionar el botón)
            historiaService.guardarHistoria(historiaClinica);

            // 3. Cambiamos estado del turno
            if (turnoId != null) {
                Turno turno = turnoService.findById(turnoId);
                if (turno != null) {
                    turno.setEstado(estadoService.findById(9L)); // 9 = ATENDIDO
                    turnoService.save(turno);
                }
            }
            
            flash.addFlashAttribute("success", "Atención médica guardada correctamente.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al guardar: " + e.getMessage());
        }
        
        // 4. Redirigimos a la agenda
        return "redirect:/turnos/listarTurno";
    }
    
    

    
    @PostMapping("/finalizar")
    public String finalizarAtencion(HistoriaClinica historiaClinica, 
                                    @RequestParam(name="turnoId", required=false) Long turnoId, 
                                    @RequestParam(name="origenRedireccion", required=false) String origen,
                                    Authentication auth, 
                                    RedirectAttributes flash) {
        try {
            // 1. Identificamos al médico
            Profesional medico = profesionalService.findByUsername(auth.getName());
            if (medico != null) historiaClinica.setProfesional(medico);

            // 2. Seteamos el origen en la historia para MySQL
            historiaClinica.setTipoAtencion(origen); // Guardará "SOBRETURNO" o "TURNO"
            historiaService.guardarHistoria(historiaClinica);

            // 3. CIERRE DE ESTADO (El cable que faltaba)
            if (turnoId != null) {
                Estado atendido = estadoService.findById(9L); // ID 9: ATENDIDO

                if ("SOBRETURNO".equals(origen)) {
                    // BUSQUEDA DIRECTA EN SOBRETURNOS
                    Sobreturno s = sobreturnoService.findById(turnoId);
                    if (s != null) {
                        s.setEstado(atendido);
                        sobreturnoService.save(s);
                    }
                } else {
                    // BUSQUEDA DIRECTA EN TURNOS
                    Turno t = turnoService.findById(turnoId);
                    if (t != null) {
                        t.setEstado(atendido);
                        turnoService.save(t);
                    }
                }
            }

            flash.addFlashAttribute("success", "Atención finalizada con éxito.");
            
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        // 4. REDIRECCIÓN
        return ("SOBRETURNO".equals(origen)) ? "redirect:/sobreturnos/listarSobreturno" : "redirect:/turnos/listarTurno";
    }



    
    
 // VER DETALLE DE LA HISTORIA CLINICA
    @GetMapping("/detalle-historia/{id}")
    @ResponseBody
    public Map<String, Object> obtenerDetalle(@PathVariable Long id) {
        HistoriaClinica h = historiaService.findById(id);
        Map<String, Object> respuesta = new HashMap<>();
        
        if (h != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            respuesta.put("fecha", sdf.format(h.getFecha()));
            respuesta.put("medico", h.getProfesional().getApellido());
            
            // --- LA LÍNEA QUE FALTA: ORIGEN DE LA ATENCIÓN ---
            respuesta.put("tipoAtencion", h.getTipoAtencion()); 
            
            // Datos del Paciente
            respuesta.put("pacienteNombre", h.getPaciente().getApellido() + ", " + h.getPaciente().getNombre());
            respuesta.put("pacienteDni", h.getPaciente().getDni());

            // Campos Clínicos
            respuesta.put("alergias", h.getAlergias());
            respuesta.put("antecedentes", h.getAntecedentes());
            respuesta.put("motivo", h.getMotivoConsulta());
            respuesta.put("examen", h.getExamenFisico());
            respuesta.put("evolucion", h.getEvolucionClinica());
            respuesta.put("diagnostico", h.getDiagnostico());
            respuesta.put("indicaciones", h.getIndicaciones());
        }
        return respuesta;
    }

    
    


}

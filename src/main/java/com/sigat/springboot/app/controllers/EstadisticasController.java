package com.sigat.springboot.app.controllers;

import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sigat.springboot.app.dto.RecepcionDTO;
import com.sigat.springboot.app.models.entity.Sobreturno;
import com.sigat.springboot.app.models.entity.Turno;
import com.sigat.springboot.app.models.service.IEspecialidadService;
import com.sigat.springboot.app.models.service.IProfesionalService;
import com.sigat.springboot.app.models.service.ISobreTurnoService;
import com.sigat.springboot.app.models.service.ITurnoService;

@Controller
@RequestMapping("/estadisticas")
@PreAuthorize("hasRole('ROLE_ADMIN')") // Exclusivo dueños / administración
public class EstadisticasController {

    @Autowired
    private ITurnoService turnoService;
    @Autowired
    private ISobreTurnoService sobreturnoService;
    @Autowired
    IProfesionalService profesionalService;
    @Autowired
    IEspecialidadService especialidadService;

    // --- MÉTODOS DE MAPEADO ADAPTADOS A TU DATE REAL ---
    private RecepcionDTO mapToDTO(Turno t) {
        RecepcionDTO dto = new RecepcionDTO();
        dto.setIdOriginal(t.getId());
        dto.setTipo("TURNO");
        dto.setPaciente(t.getPaciente() != null ? t.getPaciente().getApellido() + " " + t.getPaciente().getNombre() : "SIN PACIENTE");
        dto.setDni(t.getPaciente() != null ? t.getPaciente().getDni() : "");
        dto.setMedico(t.getProfesional() != null ? t.getProfesional().getApellido() + " " + t.getProfesional().getNombre() : "SIN MÉDICO");
        dto.setEspecialidad(t.getEspecialidad() != null ? t.getEspecialidad().getEspecialidadNombre() : "");
        dto.setEstadoId(t.getEstado() != null ? t.getEstado().getId() : 0L);
        dto.setEstadoNombre(t.getEstado() != null ? t.getEstado().getNombre() : "SIN ESTADO");
        dto.setPacienteId(t.getPaciente() != null ? t.getPaciente().getId() : null);
        dto.setProfesionalId(t.getProfesional() != null ? t.getProfesional().getId() : null);
        dto.setEspecialidadId(t.getEspecialidad() != null ? t.getEspecialidad().getId() : null);
        
        // Convertimos el LocalDateTime de la planilla a Date tradicional para tu DTO
        if (t.getFechaCreacion() != null) {
            dto.setFechaHora(java.sql.Timestamp.valueOf(t.getFechaCreacion()));
        }
        return dto;
    }

    private RecepcionDTO mapToDTO(Sobreturno s) {
        RecepcionDTO dto = new RecepcionDTO();
        dto.setIdOriginal(s.getId());
        dto.setTipo("SOBRETURNO");
        dto.setPaciente(s.getPaciente() != null ? s.getPaciente().getApellido() + " " + s.getPaciente().getNombre() : "SIN PACIENTE");
        dto.setDni(s.getPaciente() != null ? s.getPaciente().getDni() : "");
        dto.setMedico(s.getProfesional() != null ? s.getProfesional().getApellido() + " " + s.getProfesional().getNombre() : "SIN MÉDICO");
        dto.setEspecialidad(s.getEspecialidad() != null ? s.getEspecialidad().getEspecialidadNombre(): "");
        dto.setEstadoId(s.getEstado() != null ? s.getEstado().getId() : 0L);
        dto.setEstadoNombre(s.getEstado() != null ? s.getEstado().getNombre() : "SIN ESTADO");
        dto.setPacienteId(s.getPaciente() != null ? s.getPaciente().getId() : null);
        dto.setProfesionalId(s.getProfesional() != null ? s.getProfesional().getId() : null);
        dto.setEspecialidadId(s.getEspecialidad() != null ? s.getEspecialidad().getId() : null);
        
        // Convertimos el LocalDateTime de sobreturno a Date tradicional para tu DTO
        if (s.getRangoFechaHora() != null) {
            dto.setFechaHora(java.sql.Timestamp.valueOf(s.getRangoFechaHora()));
        }
        return dto;
    }

    @GetMapping("/dashboard")
    public String verDashboard(
            @RequestParam(name = "mes", required = false) Integer mesFiltro,
            @RequestParam(name = "anio", required = false) Integer anioFiltro,
            @RequestParam(name = "profesionalId", required = false) Long profesionalIdFiltro,
            @RequestParam(name = "especialidadId", required = false) Long especialidadIdFiltro,
            Model model) {
            
        List<RecepcionDTO> consolidado = new ArrayList<>();

        // 1. Carga de datos unificada
        List<Turno> turnosRaw = turnoService.findAll(); 
        List<Sobreturno> sobreturnosRaw = sobreturnoService.findAll();

        if (turnosRaw != null) turnosRaw.forEach(t -> consolidado.add(mapToDTO(t)));
        if (sobreturnosRaw != null) sobreturnosRaw.forEach(s -> consolidado.add(mapToDTO(s)));

        // 2. DETECCIÓN DE FILTROS PASADOS POR LA PANTALLA
        Calendar cal = Calendar.getInstance();
        int mesActual = cal.get(Calendar.MONTH); // Base 0 (0 = Enero, 4 = Mayo)
        int anioActual = cal.get(Calendar.YEAR);

        // Si viene filtro de mes restamos 1, porque en el combo Enero es 1 pero en Calendar es 0
        int mesAProcesar = (mesFiltro != null) ? (mesFiltro - 1) : mesActual;
        int anioAProcesar = (anioFiltro != null) ? anioFiltro : anioActual;

        // FILTRADO DINÁMICO EN MEMORIA EN CASCADA
        List<RecepcionDTO> citasDelMes = consolidado.stream()
            .filter(c -> c.getFechaHora() != null)
            .filter(c -> {
                Calendar fechaCita = Calendar.getInstance();
                fechaCita.setTime(c.getFechaHora());
                return fechaCita.get(Calendar.MONTH) == mesAProcesar && fechaCita.get(Calendar.YEAR) == anioAProcesar;
            })
            // Filtro por Profesional (Si no selecciona o es 0, no filtra y pasan todos)
            .filter(c -> profesionalIdFiltro == null || profesionalIdFiltro == 0L || 
                    (c.getProfesionalId() != null && c.getProfesionalId().equals(profesionalIdFiltro)))
            // Filtro por Especialidad (Si no selecciona o es 0, no filtra y pasan todas)
            .filter(c -> especialidadIdFiltro == null || especialidadIdFiltro == 0L || 
                    (c.getEspecialidadId() != null && c.getEspecialidadId().equals(especialidadIdFiltro)))
            .collect(Collectors.toList());

        // 3. MATEMÁTICA GERENCIAL (KPIs del Mes Filtrado)
        long atendidos = citasDelMes.stream().filter(c -> c.getEstadoId() != null && c.getEstadoId() == 9L).count(); 
        long enSala = citasDelMes.stream().filter(c -> c.getEstadoId() != null && c.getEstadoId() == 10L).count();   
        long anulados = citasDelMes.stream().filter(c -> c.getEstadoId() != null && (c.getEstadoId() == 5L || c.getEstadoId() == 6L)).count(); 
        long totalMes = citasDelMes.size();

        model.addAttribute("kpiAtendidos", atendidos);
        model.addAttribute("kpiEnSala", enSala);
        model.addAttribute("kpiAnulados", anulados);
        model.addAttribute("kpiTotalMes", totalMes);

        // 4. DATOS GRÁFICO 1: Distribución por Estados (Dona)
        Map<String, Long> estadosMap = citasDelMes.stream()
            .filter(c -> c.getEstadoNombre() != null)
            .collect(Collectors.groupingBy(RecepcionDTO::getEstadoNombre, Collectors.counting()));
        
        model.addAttribute("estadosLabels", estadosMap.keySet());
        model.addAttribute("estadosValores", estadosMap.values());

        // 5. DATOS GRÁFICO 2: Ranking de Médicos (Barras de Atendidos)
        Map<String, Long> medicosMap = citasDelMes.stream()
            .filter(c -> c.getEstadoId() != null && c.getEstadoId() == 9L)
            .filter(c -> c.getMedico() != null)
            .collect(Collectors.groupingBy(RecepcionDTO::getMedico, Collectors.counting()));

        model.addAttribute("medicosLabels", medicosMap.keySet());
        model.addAttribute("medicosValores", medicosMap.values());

        // --- ATRIBUTOS PARA LOS COMBOS DEL HTML ---
        model.addAttribute("mesSeleccionado", mesAProcesar + 1);
        model.addAttribute("anioSeleccionado", anioAProcesar);
        model.addAttribute("profesionalSeleccionado", profesionalIdFiltro);
        model.addAttribute("especialidadSeleccionada", especialidadIdFiltro);
        
        // Inyectamos las listas completas de la base de datos para poblar los selectores
        // (Ajustá los nombres de tus métodos si usás .listar() en lugar de .findAll())
        model.addAttribute("listaProfesionales", profesionalService.findAll());
        model.addAttribute("listaEspecialidades", especialidadIdFiltro != null ? especialidadService.findAll() : especialidadService.findAll());

        // Generamos la lista de años
        model.addAttribute("listaAnios", Arrays.asList(anioActual, anioActual - 1, anioActual - 2));

        model.addAttribute("titulo", "Dashboard de Gestión");
        
        return "estadisticas/dashboard"; 
    }


}

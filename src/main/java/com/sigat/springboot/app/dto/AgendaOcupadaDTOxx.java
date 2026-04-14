package com.sigat.springboot.app.dto;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

public class AgendaOcupadaDTOxx {
    private Long id;
    private Long pacienteId;
    private String pacienteNombre;
    
    // Esta anotación obliga a Spring a mandar la FECHA REAL como texto y no como la hora del sistema
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "America/Argentina/Buenos_Aires")
    private Date fechaHora; 
    
    private String box;
    private String observacion;
    private boolean esSobreturno;
    private Integer estadoId;

    public AgendaOcupadaDTOxx() {}

    // --- GETTERS Y SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPacienteId() { return pacienteId; }
    public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }

    public String getPacienteNombre() { return pacienteNombre; }
    public void setPacienteNombre(String pacienteNombre) { this.pacienteNombre = pacienteNombre; }

    public Date getFechaHora() { return fechaHora; }
    public void setFechaHora(Date fechaHora) { this.fechaHora = fechaHora; }

    public String getBox() { return box; }
    public void setBox(String box) { this.box = box; }

    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }

    public boolean isEsSobreturno() { return esSobreturno; }
    public void setEsSobreturno(boolean esSobreturno) { this.esSobreturno = esSobreturno; }

    public Integer getEstadoId() { return estadoId; }
    public void setEstadoId(Integer estadoId) { this.estadoId = estadoId; }
}
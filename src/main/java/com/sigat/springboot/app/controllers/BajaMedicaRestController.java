package com.sigat.springboot.app.controllers;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.sigat.springboot.app.models.service.IPlanillaDetalleService;

@RestController
@RequestMapping("/api/baja-medica")
public class BajaMedicaRestController {

    @Autowired
    private IPlanillaDetalleService planillaDetalleService;

    @GetMapping("/ejecutar-proceso-completo")
    public ResponseEntity<?> ejecutarBajaMedicaCompleta(
            @RequestParam Long profId, 
            @RequestParam Long especId,
            @RequestParam String inicio, 
            @RequestParam String fin,
            @RequestParam String motivo,
            @RequestParam(defaultValue = "false") boolean ignorarBackup) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Log de depuración (Opcional, para ver en consola de Eclipse)
            System.out.println("Iniciando Baja Médica Masiva para Prof: " + profId + " | IgnorarBackup: " + ignorarBackup);

            // Llamamos al Service. 
            // IMPORTANTE: Asegurate que dentro de este método del Service la llamada a 
            // generarPdfLlamadosElite termine con el String "Planillas".
            String resultadoMensaje = planillaDetalleService.ejecutarBajaMedicaMasivaCompleta(
                    profId, especId, inicio, fin, motivo, ignorarBackup);
            
            response.put("status", "success");
            response.put("message", resultadoMensaje);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            // Si hay un error (ej: fallo el backup o no hay turnos), se captura aquí
            response.put("status", "error");
            response.put("message", "Error en el proceso: " + e.getMessage());
            e.printStackTrace(); // Para que veas el error completo en la consola
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}

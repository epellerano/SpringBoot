package com.sigat.springboot.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sigat.springboot.app.models.service.BackupService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/db")
@CrossOrigin(origins = "*") // Permite que cualquier frontend se conecte (puedes restringirlo luego)
public class BackupController {

    @Autowired
    private BackupService backupService;

    @GetMapping("/backup")
    public ResponseEntity<Map<String, Object>> realizarBackup(@RequestParam(name = "tipo", defaultValue = "General") String tipo) {
        Map<String, Object> response = new HashMap<>();
        
        // Log para verificar qué tipo está llegando desde el frontend
        System.out.println("Solicitud de backup recibida para el tipo: " + tipo); 
        
        // CAMBIO CLAVE: Usamos la variable 'tipo' que viene por parámetro
        boolean exito = backupService.ejecutarBackup(tipo);

        if (exito) {
            response.put("status", "success");
            response.put("message", "Backup " + tipo + " realizado correctamente.");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            response.put("message", "El comando mysqldump falló para el tipo: " + tipo);
            return ResponseEntity.internalServerError().body(response);
        }
    }
}


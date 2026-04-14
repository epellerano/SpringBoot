package com.sigat.springboot.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public ResponseEntity<Map<String, Object>> realizarBackup() {
        Map<String, Object> response = new HashMap<>();
        
        System.out.println("Solicitud de backup recibida..."); // Log para ver en la consola de Java
        
        boolean exito = backupService.ejecutarBackup();

        if (exito) {
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            response.put("message", "El comando mysqldump falló en el servidor.");
            return ResponseEntity.internalServerError().body(response);
        }
    }
}


package com.sigat.springboot.app.models.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
public class BackupService {

    // REEMPLAZA ESTA RUTA con la que obtuviste en el paso anterior
    // Usa barras inclinadas (/) para evitar problemas de escape en Windows
    private static final String MYSQLDUMP_EXE = "C:/Program Files/MySQL/MySQL Server 8.0/bin/mysqldump.exe"; 
    private static final String BACKUP_DIRECTORY = "C:/Users/Public/Documents/BackupBDCancelacion";

    @Value("${spring.datasource.username}")
    private String user;

    @Value("${spring.datasource.password}")
    private String password;

    // Asegúrate de que en properties sea spring.database.name o database.name
    @Value("${spring.database.name}") 
    private String dbName;

 // Modificamos la firma para recibir el tipo (Sobreturnos, Turnos, Planillas)
    public boolean ejecutarBackup(String tipo) {
        try {
            // 1. Construimos la ruta dinámica: BackupBDCancelacion + Sobreturnos, etc.
            String rutaDinamica = "C:/Users/Public/Documents/BackupBDCancelacion" + tipo + "/";
            File folder = new File(rutaDinamica);
            
            // 2. Si la carpeta no existe (ej: BackupBDCancelacionSobreturnos), la crea
            if (!folder.exists()) {
                folder.mkdirs();
            }

            // 3. Limpiamos backups viejos solo en esa subcarpeta
            limpiarBackupsAntiguos(folder);

            // 4. Nombramos el archivo incluyendo el tipo para que sea fácil de identificar
            // Ejemplo: backup_Sobreturnos_1713745200000.bak
            String fileName = "backup_" + tipo + "_" + System.currentTimeMillis() + ".bak";
            File backupFile = new File(folder, fileName);

            List<String> commandArgs = new ArrayList<>();
            commandArgs.add(MYSQLDUMP_EXE); 
            commandArgs.add("-u" + user.trim());
            
            if (password != null && !password.trim().isEmpty()) {
                commandArgs.add("-p" + password.trim());
            }
            
            commandArgs.add("--databases");
            commandArgs.add(dbName.trim());
            commandArgs.add("-r");
            commandArgs.add(backupFile.getAbsolutePath());

            ProcessBuilder pb = new ProcessBuilder(commandArgs);
            pb.redirectErrorStream(true);
            Process process = pb.start();
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("LOG MYSQLDUMP (" + tipo + "): " + line);
                }
            }

            return process.waitFor() == 0;
        } catch (Exception e) {
            System.err.println("Error crítico en backup de " + tipo + " en " + MYSQLDUMP_EXE);
            e.printStackTrace();
            return false;
        }
    }


    private void limpiarBackupsAntiguos(File folder) {
        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".bak"));
        if (files != null && files.length > 0) {
            Arrays.sort(files, Comparator.comparingLong(File::lastModified));
            for (int i = 0; i < files.length - 1; i++) {
                files[i].delete();
            }
        }
    }
}

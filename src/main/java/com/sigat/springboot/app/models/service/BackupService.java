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

    public boolean ejecutarBackup() {
        try {
            File folder = new File(BACKUP_DIRECTORY);
            if (!folder.exists()) folder.mkdirs();

            limpiarBackupsAntiguos(folder);

            String fileName = "backup_" + System.currentTimeMillis() + ".bak";
            File backupFile = new File(folder, fileName);

            List<String> commandArgs = new ArrayList<>();
            commandArgs.add(MYSQLDUMP_EXE); // Uso de la constante que elimina el warning
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
                    System.out.println("LOG MYSQLDUMP: " + line);
                }
            }

            return process.waitFor() == 0;
        } catch (Exception e) {
            System.err.println("Error crítico: No se encuentra el archivo en " + MYSQLDUMP_EXE);
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

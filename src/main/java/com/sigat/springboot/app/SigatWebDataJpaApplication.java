package com.sigat.springboot.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.sigat.springboot.app.models.service.IUploadFileService;

@SpringBootApplication
public class SigatWebDataJpaApplication implements CommandLineRunner {

	@Autowired
	IUploadFileService uploadFileService;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	public static void main(String[] args) {
		SpringApplication.run(SigatWebDataJpaApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		
		// vaciamos y eliminamos el directorio de fotos uploads
		uploadFileService.deleteAll();		
		// creamos el directorio uploads en la raiz del proyecto cuando se inicia.
		uploadFileService.init();
		
		//Creamos 2 password encriptados al iniciar el sistema, usando Bcrypt.
		String password = "12345";		
		for(int i=0; i<2; i++) {
			String bcryptPassword = passwordEncoder.encode(password);
			System.out.println(bcryptPassword);
		}
	}

}

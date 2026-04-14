package com.sigat.springboot.app.models.service;

import java.io.IOException;
import java.net.MalformedURLException;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface IUploadFileService {

	public Resource load(String filename) throws MalformedURLException;
	
	public String copy(MultipartFile file) throws IOException;
	
	public boolean delete(String filename);
	
	//elimina todas las imagenes y carpeta incluida.
	public void deleteAll();
	//crea la carpeta uploads en el directorio raiz.
	public void init() throws IOException;
}

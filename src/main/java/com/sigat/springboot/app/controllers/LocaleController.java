package com.sigat.springboot.app.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class LocaleController {
	
	/*Se encarga de procesar la respuesta y de redirigir a la ultima pagina o url donde estabamos,
	  despues de que se haya cambiado el idioma.*/
	@GetMapping("/locale")
	public String Locale(HttpServletRequest request)
	{
		String ultimaUrl= request.getHeader("referer");
		return "redirect:".concat(ultimaUrl);
	}

}

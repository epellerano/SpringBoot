package com.sigat.springboot.app.controllers;

import java.security.Principal;
import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class loginController {

	    // ESTO ES CLAVE: Si alguien escribe solo localhost:8080/ lo mandamos al login
	    // O si ya está logueado, el successHandler se encargará de llevarlo a su lugar.
		@GetMapping("/")
		public String index(Principal principal) {
		    if (principal != null) {
		        // Si ya está logueado, lo mandamos a la página principal de gestión
		        return "redirect:/turnos/listarTurno"; 
		    }
		    // Si no está logueado, al login
		    return "redirect:/login";
		}

	    @GetMapping("/login")
	    public String login(@RequestParam(value = "error", required = false) String error,
	            @RequestParam(value = "logout", required = false) String logout, 
	            Model model, Principal principal,
	            RedirectAttributes flash, Locale locale) {
	        
	        if (principal != null) {
	            flash.addFlashAttribute("info", "Atención: Usted ya tiene una sesión activa."); 
	            return "redirect:/";
	        }

	        if (error != null) {
	            model.addAttribute("error",
	                    "Error en el login: Nombre de usuario o contraseña incorrecta.");
	        }

	        if (logout != null) {
	            model.addAttribute("success", "Has cerrado sesión con éxito. ¡Vuelve pronto!");
	        }
	        
	        return "login";
	    }
}

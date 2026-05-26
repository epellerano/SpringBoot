package com.sigat.springboot.app.auth.handler;

import java.io.IOException;
import java.util.Collection;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.SessionFlashMapManager;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private LocaleResolver localeResolver;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		// 1. Mantenemos intacto tu mensaje de éxito original de SIGAT
		SessionFlashMapManager flashMapManager = new SessionFlashMapManager();
		FlashMap flashMap = new FlashMap();
		flashMap.put("success", "Hola " + authentication.getName() + ", has iniciado sesión con éxito!");		
		flashMapManager.saveOutputFlashMap(flashMap, request, response);
		
		if(authentication != null) {
			logger.info("El usuario '" + authentication.getName() + "' ha iniciado sesión con éxito");
		}
		
		// 2. DETECTOR INTELIGENTE DE ROLES PARA ASIGNAR EL DESTINO AUTOMÁTICO
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		String targetUrl = "/"; // Destino base de respaldo

		for (GrantedAuthority authority : authorities) {
			String role = authority.getAuthority();
			
			if (role.equals("ROLE_PACIENTE")) {
				// El paciente viaja de cabeza y de forma automática a su portal web
				targetUrl = "/turnos/autoTurno";
				break;
			} else if (role.equals("ROLE_ADMIN") || role.equals("ROLE_USER")) {
				// Las secretarias y administradores van al panel general de la clínica
				targetUrl = "/turnos/listarTurno";
				break;
			} else if (role.equals("ROLE_MEDICO")) {
				// Los profesionales van a su consultorio de HCE
				targetUrl = "/consultorio/estacion-trabajo"; // Ajustá a tu ruta real de médicos
				break;
			}
		}
		
		// 3. Redirección física veloz en microsegundos
		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}
}
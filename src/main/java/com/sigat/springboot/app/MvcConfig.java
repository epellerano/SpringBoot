package com.sigat.springboot.app;
import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Configuration
public class MvcConfig implements WebMvcConfigurer{

	/*private final Logger log = LoggerFactory.getLogger(getClass());
	
	
	 * @Override public void addResourceHandlers(ResourceHandlerRegistry registry) {
	 * WebMvcConfigurer.super.addResourceHandlers(registry); String resourcePath =
	 * Paths.get("uploads").toAbsolutePath().toUri().toString();
	 * log.info(resourcePath); registry.addResourceHandler("/uploads/**")
	 * .addResourceLocations(resourcePath); //"file:/C:/Temp/uploads/" }
	 */
	
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/error_403").setViewName("error_403");
	}
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	
	//metodo que se encarga de guardar el Objeto Locale con nuestra internacionalizacion("es", "ES").
	@Bean	
	public LocaleResolver localeResolver() {
		SessionLocaleResolver localeResolver = new SessionLocaleResolver();
		localeResolver.setDefaultLocale(new Locale("es", "ES"));
		return localeResolver;
	}
	
	
	//Interceptor que se encarga de enviar el nuevo lenguaje elejido y cambiarlo en la pagina.
	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor localeInterceptor = new LocaleChangeInterceptor();
		localeInterceptor.setParamName("lang");
		return localeInterceptor;
	}

	//Registrar el interceptor en la aplicacion
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
	}
}

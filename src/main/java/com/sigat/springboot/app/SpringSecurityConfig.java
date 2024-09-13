package com.sigat.springboot.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import com.sigat.springboot.app.auth.handler.LoginSuccessHandler;
import com.sigat.springboot.app.models.service.JpaUserDetailsService;
 
@Configuration
//@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SpringSecurityConfig {
    
	//Inyectamos la clase login LoginSuccessHandler del package(com.sigat.springboot.app.auth.handler) anotada con @Component.
	@Autowired
	private LoginSuccessHandler successHandler;
	
	
	@Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private JpaUserDetailsService userDetailService;

    @Autowired
    public void userDetailsService(AuthenticationManagerBuilder build) throws Exception {
       build.userDetailsService(userDetailService) //obtenemos Usuarios y roles
       .passwordEncoder(passwordEncoder); //Obtenemos la contraseña
    }
    
    /*
    @Bean
    public UserDetailsService userDetailsService()throws Exception{
                
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User
                .withUsername("andres")
                .password(passwordEncoder().encode("12345"))
                .roles("USER")
                .build());
         manager.createUser(User
                    .withUsername("admin")
                    .password(passwordEncoder().encode("12345"))
                    .roles("ADMIN","USER")
                    .build());
        
        return manager;
    }*/
     
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
 
        http.authorizeHttpRequests(
            (authz) -> authz
            	//para profesionales a nivel controller
                .requestMatchers("/", "/css/**", "/js/**", "/images/**", "/listar", "/locale").permitAll()
                .requestMatchers("/ver/**").hasAnyRole("USER") // en controlador: @Secured("ROLE_USER")
                .requestMatchers("/uploads/**").hasAnyRole("USER")
                .requestMatchers("/form/**").hasAnyRole("ADMIN")
                .requestMatchers("/eliminar/**").hasAnyRole("ADMIN")
                // para clientes a nivel controller
                .requestMatchers("pacientes/verPaciente/**").hasAnyRole("USER")
                .requestMatchers("pacientes/formPaciente/**").hasAnyRole("ADMIN")
                .requestMatchers("pacientes/eliminarPaciente/**").hasAnyRole("ADMIN")
                // para especialidades a nivel controller
                .requestMatchers("especialidades/verEspecialidad/**").hasAnyRole("USER")
                .requestMatchers("especialidades/formEspecialidad/**").hasAnyRole("ADMIN")
                .requestMatchers("especialidades/eliminarEspecialidad/**").hasAnyRole("ADMIN")
             // para vinculaciones a nivel controller
                .requestMatchers("vinculaciones/verVinculacion/**").hasAnyRole("USER")
                .requestMatchers("vinculaciones/formVinculacion/**").hasAnyRole("ADMIN")
                .requestMatchers("vinculaciones/eliminarVinculacion/**").hasAnyRole("ADMIN")
             // para movimientos a nivel controller
                .requestMatchers("movimientos/verMovimiento/**").hasAnyRole("USER")
                .requestMatchers("movimientos/formMovimiento/**").hasAnyRole("ADMIN")
                .requestMatchers("movimientos/eliminarMovimiento/**").hasAnyRole("ADMIN")
                .requestMatchers("movimientos/editarMovimiento/**").hasAnyRole("ADMIN")
                .anyRequest().authenticated()
             )
             .formLogin(login -> login.permitAll()
            		 .successHandler(successHandler)
            		 .loginPage("/login"))
             .logout(logout -> logout.permitAll())
             .exceptionHandling((exception)-> exception.accessDeniedPage("/error_403"));
 
        return http.build();
    }
}
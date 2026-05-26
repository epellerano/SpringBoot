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
public class SpringSecurityConfig {
    
	@Autowired
	private LoginSuccessHandler successHandler;
	
	@Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private JpaUserDetailsService userDetailService;

    @Autowired
    public void userDetailsService(AuthenticationManagerBuilder build) throws Exception {
       build.userDetailsService(userDetailService) 
       .passwordEncoder(passwordEncoder); 
    }
     
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(
            (authz) -> authz
                // 1. BLINDAJE DE ACCESOS PÚBLICOS Y EXCLUSIÓN DE RECURSOS ESTÁTICOS DE SIGAT
                .requestMatchers("/login", "/registro/**", "/css/**", "/js/**", "/images/**", "/locale/**", "/favicon.ico", "/error/**").permitAll()
                .requestMatchers(org.springframework.boot.autoconfigure.security.servlet.PathRequest.toStaticResources().atCommonLocations()).permitAll()
                
                // Abrimos el paso absoluto de lectura para que el JavaScript del paciente dibuje las pastillas libres
                .requestMatchers("/turnos/cargar-especialidades/**").permitAll()
                .requestMatchers("/turnos/obtener-observaciones/**").permitAll()
                .requestMatchers("/turnos/listar-horarios/**").permitAll()
                
                // 2. CONSULTORIO (HCE) - SOLO MÉDICOS
                .requestMatchers("/consultorio/**").hasAuthority("ROLE_MEDICO")

                // 3. PROFESIONALES
                .requestMatchers("/ver/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_MEDICO") 
                .requestMatchers("/uploads/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_MEDICO")
                .requestMatchers("/form/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/eliminar/**").hasAuthority("ROLE_ADMIN")

                // 4. PACIENTES ADMINISTRATIVOS
                .requestMatchers("/pacientes/listarPaciente").hasAnyAuthority("ROLE_ADMIN", "ROLE_MEDICO")
                .requestMatchers("/pacientes/verPaciente/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_MEDICO")
                .requestMatchers("/pacientes/formPaciente/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/pacientes/eliminarPaciente/**").hasAuthority("ROLE_ADMIN")

                // 5. ESPECIALIDADES
                .requestMatchers("/especialidades/verEspecialidad/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_MEDICO")
                .requestMatchers("/especialidades/formEspecialidad/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/especialidades/eliminarEspecialidad/**").hasAuthority("ROLE_ADMIN")

                // 6. VINCULACIONES
                .requestMatchers("/vinculaciones/verVinculacion/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_MEDICO")
                .requestMatchers("/vinculaciones/formVinculacion/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/vinculaciones/eliminarVinculacion/**").hasAuthority("ROLE_ADMIN")

                // 7. MOVIMIENTOS
                .requestMatchers("/movimientos/verMovimiento/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_MEDICO")
                .requestMatchers("/movimientos/formMovimiento/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/movimientos/eliminarMovimiento/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/movimientos/editarMovimiento/**").hasAuthority("ROLE_ADMIN")

                // 8. PLANILLAS
                .requestMatchers("/planillacabecera/verPlanillaCabecera/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_MEDICO")
                .requestMatchers("/planillacabecera/formPlanillaCabecera/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/planillacabecera/eliminarPlanillaCabecera/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/planillacabecera/editarPlanillaCabecera/**").hasAuthority("ROLE_ADMIN")

                // =========================================================================
                // 9. TURNOS (Gestión de Agenda - Sincronizado con Autogestión de SIGAT)
                // =========================================================================
                .requestMatchers("/turnos/autoTurno").hasAnyAuthority("ROLE_ADMIN", "ROLE_PACIENTE")
                
                .requestMatchers("/turnos/listarTurno").hasAnyAuthority("ROLE_ADMIN", "ROLE_MEDICO")
                .requestMatchers("/turnos/verTurno/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_MEDICO")
                .requestMatchers("/turnos/formTurno/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/turnos/eliminarTurno/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/turnos/openModal/**").hasAuthority("ROLE_ADMIN")
                
                // 10. SOBRETURNOS
                .requestMatchers("/sobreturnos/listarSobreturno").hasAnyAuthority("ROLE_ADMIN", "ROLE_MEDICO")
                .requestMatchers("/sobreturnos/formSobreturno/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/sobreturnos/eliminar/**").hasAuthority("ROLE_ADMIN")

                // 11. APIS REST: Habilitadas para las consultas libres del calendario de pastillas
                .requestMatchers("/api/**").permitAll()
                .requestMatchers("/paciente-web/**").hasAuthority("ROLE_PACIENTE") 

                // Cualquier otra petición requiere autenticación estándar
                .anyRequest().authenticated()
        )
        .formLogin(login -> login
            .loginPage("/login") 
            .successHandler(successHandler) 
            .permitAll())
        .logout(logout -> logout.permitAll())
        .exceptionHandling((exception) -> exception.accessDeniedPage("/error_403"));

        return http.build();
    }



}
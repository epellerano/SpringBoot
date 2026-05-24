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
                // 1. ACCESOS PÚBLICOS (Estilos, imágenes y endpoints del registro AJAX locales)
                .requestMatchers("/login", "/registro/**", "/css/**", "/js/**", "/images/**", "/locale").permitAll()
                
                // 2. CONSULTORIO (HCE) - SOLO MÉDICOS
                .requestMatchers("/consultorio/**").hasRole("MEDICO")

                // 3. PROFESIONALES
                .requestMatchers("/ver/**").hasAnyRole("ADMIN", "MEDICO") 
                .requestMatchers("/uploads/**").hasAnyRole("ADMIN", "MEDICO")
                .requestMatchers("/form/**").hasRole("ADMIN")
                .requestMatchers("/eliminar/**").hasRole("ADMIN")

                // 4. PACIENTES
                .requestMatchers("/pacientes/listarPaciente").hasAnyRole("ADMIN", "MEDICO")
                .requestMatchers("/pacientes/verPaciente/**").hasAnyRole("ADMIN", "MEDICO")
                .requestMatchers("/pacientes/formPaciente/**").hasRole("ADMIN")
                .requestMatchers("/pacientes/eliminarPaciente/**").hasRole("ADMIN")

                // 5. ESPECIALIDADES
                .requestMatchers("/especialidades/verEspecialidad/**").hasAnyRole("ADMIN", "MEDICO")
                .requestMatchers("/especialidades/formEspecialidad/**").hasRole("ADMIN")
                .requestMatchers("/especialidades/eliminarEspecialidad/**").hasRole("ADMIN")

                // 6. VINCULACIONES
                .requestMatchers("/vinculaciones/verVinculacion/**").hasAnyRole("ADMIN", "MEDICO")
                .requestMatchers("/vinculaciones/formVinculacion/**").hasRole("ADMIN")
                .requestMatchers("/vinculaciones/eliminarVinculacion/**").hasRole("ADMIN")

                // 7. MOVIMIENTOS
                .requestMatchers("/movimientos/verMovimiento/**").hasAnyRole("ADMIN", "MEDICO")
                .requestMatchers("/movimientos/formMovimiento/**").hasRole("ADMIN")
                .requestMatchers("/movimientos/eliminarMovimiento/**").hasRole("ADMIN")
                .requestMatchers("/movimientos/editarMovimiento/**").hasRole("ADMIN")

                // 8. PLANILLAS
                .requestMatchers("/planillacabecera/verPlanillaCabecera/**").hasAnyRole("ADMIN", "MEDICO")
                .requestMatchers("/planillacabecera/formPlanillaCabecera/**").hasRole("ADMIN")
                .requestMatchers("/planillacabecera/eliminarPlanillaCabecera/**").hasRole("ADMIN")
                .requestMatchers("/planillacabecera/editarPlanillaCabecera/**").hasRole("ADMIN")

                // 9. TURNOS (TUS REGLAS ORIGINALES INTACTAS - Bloqueado para el paciente común)
                .requestMatchers("/turnos/listarTurno").hasAnyRole("ADMIN", "MEDICO")
                .requestMatchers("/turnos/verTurno/**").hasAnyRole("ADMIN", "MEDICO")
                .requestMatchers("/turnos/formTurno/**").hasRole("ADMIN")
                .requestMatchers("/turnos/eliminarTurno/**").hasRole("ADMIN")
                .requestMatchers("/turnos/openModal/**").hasRole("ADMIN")
                
                // 10. SOBRETURNOS (TUS REGLAS ORIGINALES INTACTAS - Bloqueado para el paciente común)
                .requestMatchers("/sobreturnos/listarSobreturno").hasAnyRole("ADMIN", "MEDICO")
                .requestMatchers("/sobreturnos/formSobreturno/**").hasRole("ADMIN")
                .requestMatchers("/sobreturnos/eliminar/**").hasRole("ADMIN")

                // =========================================================================
                // 11. AUTOGESTIÓN FUTURA (Habilitamos la API y el ROL para el mañana)
                // =========================================================================
                .requestMatchers("/api/**").hasAnyRole("ADMIN", "MEDICO", "PACIENTE", "USER")
                .requestMatchers("/paciente-web/**").hasRole("PACIENTE") // Reservado para tu vista autoTurno.html

                // Cualquier otra petición requiere estar autenticado (Aquí entra libre la secretaria con ROLE_USER)
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
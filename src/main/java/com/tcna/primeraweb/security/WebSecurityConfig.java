package com.tcna.primeraweb.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration //Registra beans
public class WebSecurityConfig {

    @Bean //Registra un bean en el contenedor de spring
    public InMemoryUserDetailsManager userDetailsManager() {
        //Usuarios en memoria, registrados por defecto
        UserDetails user1 = User.builder()
                .username("user1")
                .password("{bcrypt}$2a$10$CAx815ayRyYSGiN//1Qys.Hn0EFBjaUu83ewYmipeAFWyVmp1nZJK")
                .roles("USER")
                .build();
        UserDetails user2 = User.builder()
                .username("admin")
                .password("{bcrypt}$2a$10$CAx815ayRyYSGiN//1Qys.Hn0EFBjaUu83ewYmipeAFWyVmp1nZJK")
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user1, user2);
    }


    //Filtro!!!, encargado de la parte de autorización y autenticación
    @Bean
    //Clase que nos permite acceder a las RUTAS, etc
    //El metodo HttpSecurity tiene metodos para acceder a endpoints, para manejar las excepciones, etc; sobre tódo estableciendo la autorización como abajo
    protected SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        //Indicamos que endpoints vamos a autorizar
        httpSecurity.authorizeHttpRequests(
                auth -> auth
                        .requestMatchers("/personas").permitAll() //Este endpoint será disponible para todos los usuarios que ingresen a la página
                        .requestMatchers("/personas/nueva").hasAnyRole("ADMIN") //Solo a las personas que tengan el rol de ADMIN tienen acceso a este endpoint
                        .requestMatchers("/personas/editar/*","/personas/eliminar/*").hasRole("ADMIN") //El "*" es un parametro, donde el ADMIN tiene acceso
                        .anyRequest().authenticated()) //Cualquier otra petición que hagamos estara autenticada
                .httpBasic(Customizer.withDefaults())
                .exceptionHandling(e -> e.accessDeniedPage("/403")); //Excepcion donde nos envia al endopoint 403(archivo)
        return httpSecurity.build();
    }
}

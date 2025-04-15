package backend;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import com.fasterxml.jackson.databind.ObjectMapper;

import backend.entities.enums.RoleType;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Security configuration for the application.
 * <p>
 * Configures authentication, authorization, and exception handling for the application.
 * </p>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    /**
     * Configures exception handling for the application. 
     * Ignoring the access deny exception, so that the user will get it and can handle it's exception
     * 
     * @return a SimpleMappingExceptionResolver instance
     */
    @Bean
    @Qualifier("exceptionResolver")
    public SimpleMappingExceptionResolver exceptionResolver() {
        SimpleMappingExceptionResolver exceptionResolver = new SimpleMappingExceptionResolver();
        
        Properties exceptionMappings = new Properties();
        exceptionMappings.put("org.springframework.security.AccessDeniedException", "accessDenied");
        exceptionResolver.setExceptionMappings(exceptionMappings);

        //exceptionResolver.setExcludedExceptions(AuthorizationDeniedException.class, AccessDeniedException.class);
        
        
        // used idf we want to send the errors to Java Thread.setDefaultUncaughtExceptionHandler
        //exceptionResolver.setDefaultErrorView("uncaughtException");
        
        return exceptionResolver;
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		/*
		configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST"));
		configuration.addAllowedHeader("Content-Type");
		*/
		
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("*"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
    
    /**
     * Used for Swaager web gui
     * delete when using javafx
     */
    private static final String[] AUTH_WHITELIST = {

            // for Swagger UI v2
            "/v2/api-docs",
            "/swagger-ui.html",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/webjars/**",

            // for Swagger UI v3 (OpenAPI)
            "/v3/api-docs/**",
            "/swagger-ui/**"
    };
    
    /*
    @Bean
    public WebMvcConfigurer corsConfig() {
      return new WebMvcConfigurer() {
        public void addCorsMappings(@NotNull CorsRegistry registry) {
          registry.addMapping("/**")
            .allowedMethods("*")
            .allowedOrigins("*");
        }
      };
    }
    */

    /**
     * Configures the security filter chain for HTTP requests.
     * 
     * @param http the HttpSecurity instance
     * @return a SecurityFilterChain instance
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
        	.cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable()) // Disables CSRF protection, common in stateless REST APIs.
            /*.sessionManagement((session) ->
            	session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )*/
            .authorizeHttpRequests(requests -> requests
            		//This is for swagger, remove when using javafx
            		.requestMatchers("/images/**").permitAll()
            		.requestMatchers(AUTH_WHITELIST).permitAll()
            		.requestMatchers("/api/users/login", "/api/users/register", "/api/users/refresh").permitAll()
            		.requestMatchers(HttpMethod.GET, "/api/main/**").permitAll()
            		.requestMatchers("/api/users/carts/**").hasAnyRole(RoleType.ROLE_USER.getRoleName())
            		.requestMatchers("/error").permitAll()
            		.anyRequest().authenticated()
                /*
            	.requestMatchers("/auth").permitAll()
                .requestMatchers("/market").permitAll()
                .requestMatchers("/admin/**").hasRole(RoleType.ROLE_ADMIN.getRoleName())
                .requestMatchers("/user/**").hasAnyRole(RoleType.ROLE_ADMIN.getRoleName(), RoleType.ROLE_USER.getRoleName())
                .anyRequest().authenticated()
                */
            )
            .httpBasic(Customizer.withDefaults()) // Enables HTTP Basic authentication.
        	//.formLogin(form -> form.disable());
            .formLogin(Customizer.withDefaults());
        //add oauth2 protection for users.
        http.oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            );
        //used for moving the AccessDeny exception to the JavaFX
        http.exceptionHandling(cust -> cust
                .accessDeniedHandler((_, response, _) -> {
                	response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                	response.setStatus(HttpServletResponse.SC_FORBIDDEN);

                    final Map<String, Object> body = new HashMap<>();
                    body.put("code", HttpServletResponse.SC_FORBIDDEN);
                    body.put("payload", "Access Denied");

                    final ObjectMapper mapper = new ObjectMapper();
                    mapper.writeValue(response.getOutputStream(), body);
                })
                .authenticationEntryPoint((_, response, _) -> {
                	response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

                    final Map<String, Object> body = new HashMap<>();
                    body.put("code", HttpServletResponse.SC_UNAUTHORIZED);
                    body.put("payload", "You need to login first in order to perform this action.");

                    final ObjectMapper mapper = new ObjectMapper();
                    mapper.writeValue(response.getOutputStream(), body);
                })
                .defaultAccessDeniedHandlerFor((_, _, accessDeniedException) -> {
                	System.out.println("auth2");
                    throw new RuntimeException(accessDeniedException);
                }, null)  
                .defaultAuthenticationEntryPointFor((_, _, authException) -> {
                	System.out.println("auth3");
                    throw new RuntimeException(authException);
                }, null)
                
            );
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    
    @Autowired
    private JWTAuthenticationFilter jwtAuthenticationFilter;
    
    @Bean
    public FilterRegistrationBean<JWTAuthenticationFilter> registerJwtAuthenticationFilter(JWTAuthenticationFilter jwtAuthenticationFilter) {
		FilterRegistrationBean<JWTAuthenticationFilter> registrationBean = new FilterRegistrationBean<>(jwtAuthenticationFilter);
		registrationBean.setFilter(jwtAuthenticationFilter);
		registrationBean.setEnabled(false);
		return registrationBean;
	}

    /**
     * Provides the JwtAuthenticationConverter bean for converting JWTs to Authentication objects.
     * Used for checking users Roles.
     * 
     * @return a JwtAuthenticationConverter instance
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtConverter;
    }
}

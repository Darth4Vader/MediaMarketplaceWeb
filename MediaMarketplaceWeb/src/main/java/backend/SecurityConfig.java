package backend;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.proc.JWTProcessor;

import backend.auth.RSAKeysPair;
import backend.entities.enums.RoleType;
import backend.services.UserAuthenticateService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;

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

    private final RSAKeysPair rsaKeys;

    /**
     * Constructor for injecting the RSA key pair.
     * 
     * @param rsaKeys the RSA key pair used for JWT encoding and decoding
     */
    public SecurityConfig(RSAKeysPair rsaKeys) {
        this.rsaKeys = rsaKeys;
    }

    /**
     * Provides the AuthenticationManager bean.
     * 
     * @param detailsService the UserDetailsService for loading user-specific data
     * @return an AuthenticationManager instance
     */
    @Bean
    public AuthenticationManager authManager(UserDetailsService detailsService) {
        DaoAuthenticationProvider daoProvider = new DaoAuthenticationProvider();
        daoProvider.setUserDetailsService(detailsService);
        daoProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(daoProvider);
    }

    /**
     * Configures exception handling for the application. 
     * Ignoring the access deny exception, so that the user will get it and can handle it's exception
     * 
     * @return a SimpleMappingExceptionResolver instance
     */
    @Bean
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
        	//.cors(Customizer.withDefaults())
        	.cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable()) // Disables CSRF protection, common in stateless REST APIs.
            /*.sessionManagement((session) ->
            	session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )*/
            .authorizeHttpRequests(requests -> requests
            		//This is for swagger, remove when using javafx
            		.requestMatchers(AUTH_WHITELIST).permitAll()
            		.requestMatchers("/api/auth/login", "/api/auth/register").permitAll()
            		.requestMatchers(HttpMethod.GET, "/api/main/**").permitAll()
            		.requestMatchers("/api/users/carts/**").hasAnyRole(RoleType.ROLE_USER.getRoleName())
            		.requestMatchers("/error").permitAll()
        		//.requestMatchers("/**").permitAll()
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
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                	System.out.println("auth1");
                    throw new RuntimeException(accessDeniedException);
                })
                
                .authenticationEntryPoint((request, response, authException) -> {
                	
                    String jwtAuthToken = request.getHeader("Authorization");//here is your token value
                    System.out.println("SPOOOOO " + jwtAuthToken);
                    //loginUserFromJwt(jwtAuthToken);
                	
                	/*
                	System.out.println("SPOOOOO");
                	System.out.println(request.getContentType());
                	//System.out.println(response.);
                    throw new RuntimeException(authException);
                    */
                	
                	System.out.println(request.getRequestURI());
                	
                	response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

                    final Map<String, Object> body = new HashMap<>();
                    body.put("code", HttpServletResponse.SC_UNAUTHORIZED);
                    body.put("payload", "You need to login first in order to perform this action.");

                    final ObjectMapper mapper = new ObjectMapper();
                    mapper.writeValue(response.getOutputStream(), body);
                })
                
                .defaultAccessDeniedHandlerFor((request, response, accessDeniedException) -> {
                	System.out.println("auth2");
                    throw new RuntimeException(accessDeniedException);
                }, null)
                
                //.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                
                
                .defaultAuthenticationEntryPointFor((request, response, authException) -> {
                	System.out.println("auth3");
                    throw new RuntimeException(authException);
                }, null)
                
            );
        http.addFilterBefore(new JWTAuthenticationFilter(exceptionResolver()), UsernamePasswordAuthenticationFilter.class);
        
        //http.addFilterBefore(new UrlRewriteFilter(), MyWebFilter.class);
        //ttp.addFilterBefore(null, null)
        return http.build();
    }

    /**
     * Provides the PasswordEncoder bean.
     * 
     * @return a BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /*@Bean
    private JWTAuthenticationFilter jwtAuthenticationFilter;*/
    
    /*@Bean
    public JWTAuthenticationFilter jwtAuthenticationFilter() {
        return new JWTAuthenticationFilter();
    }*/

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
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtConverter;
    }

    /**
     * Provides the JwtDecoder bean for decoding JWTs.
     * 
     * @return a JwtDecoder instance
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(rsaKeys.getPublicKey()).build();
    }

    /**
     * Provides the JwtEncoder bean for encoding JWTs.
     * 
     * @return a JwtEncoder instance
     */
    @Bean
    public JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(rsaKeys.getPublicKey()).privateKey(rsaKeys.getPrivateKey()).build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }
}

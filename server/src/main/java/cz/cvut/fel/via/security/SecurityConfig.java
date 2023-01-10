package cz.cvut.fel.via.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AuthenticationProvider authenticationProvider;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationSuccess authenticationSuccess;
    private final LogoutSuccessHandler logoutSuccessHandler;

    @Autowired
    public SecurityConfig(AuthenticationProvider authenticationProvider, PasswordEncoder passwordEncoder, AuthenticationSuccess authenticationSuccess, LogoutSuccessHandler logoutSuccessHandler) {
        this.authenticationProvider = authenticationProvider;
        this.passwordEncoder = passwordEncoder;
        this.authenticationSuccess = authenticationSuccess;
        this.logoutSuccessHandler = logoutSuccessHandler;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        corsConfiguration.setAllowedOriginPatterns(List.of("*"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PUT","OPTIONS","PATCH", "DELETE"));
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setExposedHeaders(List.of("Authorization"));
        //super.configure(http);
        http.cors().configurationSource(request -> corsConfiguration).and().authorizeRequests()
                .antMatchers("/login", "/logout", "/user", "/openapi.yaml", "/openapi.json", "/movie", "/", "/swagger-ui/*").permitAll()
                .antMatchers("/list", "/list*").authenticated()
                //.anyRequest().authenticated()
                .and().exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                //.and().headers().frameOptions().sameOrigin()
                .and().authenticationProvider(authenticationProvider)
                .csrf().disable()
                .formLogin().successHandler(authenticationSuccess)
                .loginProcessingUrl("/login")
                .usernameParameter("username").passwordParameter("password")
                .and()
                .logout().invalidateHttpSession(true)//.deleteCookies(COOKIES_TO_DESTROY)
                .logoutUrl("/logout").logoutSuccessHandler(logoutSuccessHandler);//.logoutSuccessHandler(logoutSuccessHandler);

    }
}

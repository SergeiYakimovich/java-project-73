package hexlet.code.app.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import static hexlet.code.app.config.UrlConfig.BASE_URL;
import static hexlet.code.app.config.UrlConfig.LOGIN;
import static hexlet.code.app.config.UrlConfig.USER_CONTROLLER;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    //Note: Сейчас разрешены:
    // - GET('/api/users')
    // - POST('/api/users')
    // - POST('/api/login')
    // - все запросы НЕ начинающиеся на '/api'
    private final RequestMatcher publicUrls;
    private final RequestMatcher loginRequest;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JWTHelper jwtHelper;

    public SecurityConfig(@Value(BASE_URL) final String baseUrl,
                          final UserDetailsService userDetailsService,
                          final PasswordEncoder passwordEncoder, final JWTHelper jwtHelper) {
        this.loginRequest = new AntPathRequestMatcher(baseUrl + LOGIN, POST.toString());
        this.publicUrls = new OrRequestMatcher(
                loginRequest,
                new AntPathRequestMatcher(baseUrl + USER_CONTROLLER, POST.toString()),
                new AntPathRequestMatcher(baseUrl + USER_CONTROLLER, GET.toString()),
                new NegatedRequestMatcher(new AntPathRequestMatcher(baseUrl + "/**"))
        );
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtHelper = jwtHelper;
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }

    @Override
    public void configure(final HttpSecurity http) throws Exception {

        final var authenticationFilter = new JWTAuthenticationFilter(
                authenticationManagerBean(),
                loginRequest,
                jwtHelper
        );

        final var authorizationFilter = new JWTAuthorizationFilter(
                publicUrls,
                jwtHelper
        );

        http.csrf().disable()
                .authorizeRequests()
                .requestMatchers(publicUrls).permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilter(authenticationFilter)
                .addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .logout().disable();
    }

}

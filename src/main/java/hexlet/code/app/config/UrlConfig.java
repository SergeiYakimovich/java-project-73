package hexlet.code.app.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public class UrlConfig {
    public static final String BASE_URL = "/api";
    public static final String USER_CONTROLLER = "/users";
    public static final String LOGIN = "/login";
    public static final String ID = "/{id}";
    public static final List<GrantedAuthority> DEFAULT_AUTHORITIES = List.of(new SimpleGrantedAuthority("USER"));

}

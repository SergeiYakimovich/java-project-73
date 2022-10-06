package hexlet.code.app.utils;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;

@Configuration
@Profile(SpringConfigForTest.TEST_PROFILE)
@EnableAspectJAutoProxy
@ComponentScan(basePackages = "hexlet.code.app")
@PropertySource(value = "classpath:/config/application.yml")
public class SpringConfigForTest {

    public static final String TEST_PROFILE = "test";

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder().build();
    }
}

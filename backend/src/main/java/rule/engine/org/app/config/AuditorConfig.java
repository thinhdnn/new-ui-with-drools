package rule.engine.org.app.config;

import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

@Configuration
public class AuditorConfig {

    @Bean
    public AuditorAware<String> springSecurityAuditorAware() {
        return () -> Optional.of("system");
    }
}



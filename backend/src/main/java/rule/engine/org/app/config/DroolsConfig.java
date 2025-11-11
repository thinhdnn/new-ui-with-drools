package rule.engine.org.app.config;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DroolsConfig {

    @Bean
    public KieContainer kieContainer() {
        KieServices ks = KieServices.Factory.get();
        return ks.newKieClasspathContainer();
    }

    @Bean
    public KieSession kieSession(KieContainer kieContainer) {
        return kieContainer.newKieSession();
    }
}



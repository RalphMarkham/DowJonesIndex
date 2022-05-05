package ca.ralphsplace.djindex.config;

import ca.ralphsplace.djindex.mongo.ClientConnectionString;
import ca.ralphsplace.djindex.mongo.ClientFilter;
import ca.ralphsplace.djindex.mongo.ClientIdDatabaseFactory;
import com.mongodb.ConnectionString;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class AppConfigurration {

    @Bean
    public OpenAPI customOpenAPI(@Value("@springdoc.version@") String appVersion) {
        return new OpenAPI()
                .components(new Components())
                .info(new Info().title("Dow Jones Index Trade Data API").version(appVersion));
    }

    @Bean
    public FilterRegistrationBean<ClientFilter> tenantFilter() {
        FilterRegistrationBean<ClientFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ClientFilter());
        registrationBean.addUrlPatterns("/api/trade-data/*");
        return registrationBean;
    }

    @Bean
    @Lazy
    public MongoTemplate mongoTemplate() {
        ConnectionString connectionString = new ConnectionString(ClientConnectionString.getClientConnectionStr());
        ClientIdDatabaseFactory dbFactory = new ClientIdDatabaseFactory(connectionString);
        return new MongoTemplate(dbFactory);
    }
}

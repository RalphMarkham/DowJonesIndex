package ca.ralphsplace.djindex;

import com.mongodb.ConnectionString;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.MongoTemplate;

@SpringBootApplication(exclude = {
		MongoAutoConfiguration.class,
		MongoDataAutoConfiguration.class
})
public class DjindexApplication {

	public static void main(String[] args) {
		SpringApplication.run(DjindexApplication.class, args);
	}

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
		return new MongoTemplate(new ClientIdDatabaseFactory(connectionString));
	}
}

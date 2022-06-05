package br.dev.marcelo;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Contacts", version = "1.0", description = "Maintaining Contacts API"))
public class JavaSpringCrudMvcSampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(JavaSpringCrudMvcSampleApplication.class, args);
	}

}

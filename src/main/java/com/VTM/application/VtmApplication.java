package com.VTM.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(
		basePackages = "com.VTM.application.userAdministartion.repository",
		entityManagerFactoryRef = "entityManagerFactory",
		transactionManagerRef = "transactionManager"
)
@EntityScan(basePackages = "com.VTM.application.userAdministartion.entityOrDomain")
public class VtmApplication {
	public static void main(String[] args) {
		SpringApplication.run(VtmApplication.class, args);
	}
}

package io.github.amsatrio.spring_boot_container_demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SpringBootContainerDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootContainerDemoApplication.class, args);
	}

}

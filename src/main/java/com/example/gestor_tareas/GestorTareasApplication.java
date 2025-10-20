package com.example.gestor_tareas;

import com.example.gestor_tareas.model.Category;
import com.example.gestor_tareas.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GestorTareasApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestorTareasApplication.class, args);
	}

	@Bean
	CommandLineRunner initDatabase(CategoryRepository categoryRepository) {
		return args -> {
			// Solo crear categorías si no existen
			if (categoryRepository.count() == 0) {
				Category trabajo = new Category();
				trabajo.setName("Trabajo");
				trabajo.setIcon("💼");
				trabajo.setColor("#3b82f6");
				categoryRepository.save(trabajo);

				Category personal = new Category();
				personal.setName("Personal");
				personal.setIcon("🏠");
				personal.setColor("#10b981");
				categoryRepository.save(personal);

				Category estudio = new Category();
				estudio.setName("Estudio");
				estudio.setIcon("📚");
				estudio.setColor("#f59e0b");
				categoryRepository.save(estudio);

				Category salud = new Category();
				salud.setName("Salud");
				salud.setIcon("🏋️");
				salud.setColor("#ef4444");
				categoryRepository.save(salud);

				Category compras = new Category();
				compras.setName("Compras");
				compras.setIcon("🛒");
				compras.setColor("#8b5cf6");
				categoryRepository.save(compras);

			}
		};
	}
}

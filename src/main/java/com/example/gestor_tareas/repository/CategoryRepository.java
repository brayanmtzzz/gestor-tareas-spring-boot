package com.example.gestor_tareas.repository;

import com.example.gestor_tareas.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}

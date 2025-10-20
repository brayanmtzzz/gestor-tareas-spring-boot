package com.example.gestor_tareas.repository;

import com.example.gestor_tareas.model.Task;
import com.example.gestor_tareas.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByCompleted(boolean completed);
    List<Task> findByCategory(Category category);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.completed = true")
    long countCompleted();
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.completed = false")
    long countPending();
}

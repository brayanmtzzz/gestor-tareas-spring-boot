package com.example.gestor_tareas.controller;

import com.example.gestor_tareas.dto.ZenQuoteDTO;
import com.example.gestor_tareas.model.Category;
import com.example.gestor_tareas.model.Task;
import com.example.gestor_tareas.repository.CategoryRepository;
import com.example.gestor_tareas.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;

@Controller
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("/")
    public String dashboard(Model model) {
        long totalTasks = taskRepository.count();
        long completedTasks = taskRepository.countCompleted();
        long pendingTasks = taskRepository.countPending();
        
        model.addAttribute("totalTasks", totalTasks);
        model.addAttribute("completedTasks", completedTasks);
        model.addAttribute("pendingTasks", pendingTasks);
        model.addAttribute("recentTasks", taskRepository.findAll().stream()
            .sorted((t1, t2) -> t2.getCreatedAt().compareTo(t1.getCreatedAt()))
            .limit(5)
            .toList());
        model.addAttribute("categories", categoryRepository.findAll());
        
        // Obtener frase motivacional de ZenQuotes
        try {
            RestTemplate restTemplate = new RestTemplate();
            ZenQuoteDTO[] quotes = restTemplate.getForObject("https://zenquotes.io/api/today", ZenQuoteDTO[].class);
            if (quotes != null && quotes.length > 0) {
                model.addAttribute("quote", quotes[0]);
            }
        } catch (Exception e) {
            // Si falla, usar frase por defecto
            ZenQuoteDTO defaultQuote = new ZenQuoteDTO();
            defaultQuote.setQuote("El éxito es la suma de pequeños esfuerzos repetidos día tras día.");
            defaultQuote.setAuthor("Robert Collier");
            model.addAttribute("quote", defaultQuote);
        }
        
        return "dashboard";
    }

    @GetMapping("/tasks")
    public String getAllTasks(@RequestParam(required = false) String filter,
                              @RequestParam(required = false) Long categoryId,
                              Model model) {
        var tasks = taskRepository.findAll();
        
        if ("completed".equals(filter)) {
            tasks = taskRepository.findByCompleted(true);
        } else if ("pending".equals(filter)) {
            tasks = taskRepository.findByCompleted(false);
        } else if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId).orElse(null);
            if (category != null) {
                tasks = taskRepository.findByCategory(category);
            }
        }
        
        model.addAttribute("tasks", tasks);
        model.addAttribute("newTask", new Task());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("currentFilter", filter);
        model.addAttribute("currentCategoryId", categoryId);
        return "tasks";
    }

    @PostMapping("/tasks/save")
    public String saveTask(@ModelAttribute Task task, 
                          @RequestParam(required = false) Long categoryId) {
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId).orElse(null);
            task.setCategory(category);
        }
        
        if (task.getId() != null) {
            Task existingTask = taskRepository.findById(task.getId()).orElse(null);
            if (existingTask != null) {
                existingTask.setTitle(task.getTitle());
                existingTask.setDescription(task.getDescription());
                existingTask.setPriority(task.getPriority());
                existingTask.setCompleted(task.isCompleted());
                existingTask.setCategory(task.getCategory());
                if (task.isCompleted() && existingTask.getCompletedAt() == null) {
                    existingTask.setCompletedAt(LocalDateTime.now());
                } else if (!task.isCompleted()) {
                    existingTask.setCompletedAt(null);
                }
                taskRepository.save(existingTask);
                return "redirect:/tasks";
            }
        }
        taskRepository.save(task);
        return "redirect:/tasks";
    }
    
    @GetMapping("/tasks/toggle/{id}")
    public String toggleTaskComplete(@PathVariable Long id) {
        Task task = taskRepository.findById(id).orElse(null);
        if (task != null) {
            task.setCompleted(!task.isCompleted());
            if (task.isCompleted()) {
                task.setCompletedAt(LocalDateTime.now());
            } else {
                task.setCompletedAt(null);
            }
            taskRepository.save(task);
        }
        return "redirect:/tasks";
    }

    @PostMapping("/tasks/delete/{id}")
    public String deleteTask(@PathVariable Long id) {
        taskRepository.deleteById(id);
        return "redirect:/tasks";
    }
    
    @GetMapping("/tasks/edit/{id}")
    public String editTask(@PathVariable Long id, Model model) {
        Task task = taskRepository.findById(id).orElse(null);
        if (task == null) {
            return "redirect:/tasks";
        }
        
        model.addAttribute("task", task);
        model.addAttribute("categories", categoryRepository.findAll());
        return "edit-task";
    }
    
    @GetMapping("/categories")
    public String categories(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("newCategory", new Category());
        return "categories";
    }
    
    @PostMapping("/categories/save")
    public String saveCategory(@ModelAttribute Category category) {
        categoryRepository.save(category);
        return "redirect:/categories";
    }
    
    @PostMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryRepository.deleteById(id);
        return "redirect:/categories";
    }
    
    @GetMapping("/motivation")
    public String getMotivation(Model model) {
        RestTemplate restTemplate = new RestTemplate();
        
        try {
            // API de ZenQuotes - retorna un array
            ZenQuoteDTO[] quotes = restTemplate.getForObject("https://zenquotes.io/api/random", ZenQuoteDTO[].class);
            if (quotes != null && quotes.length > 0) {
                model.addAttribute("quote", quotes[0]);
            }
        } catch (Exception e) {
            model.addAttribute("error", "No se pudo obtener la frase motivacional. Intenta nuevamente.");
        }
        
        return "motivation";
    }
    
    @GetMapping("/stats")
    public String getStats(Model model) {
        long totalTasks = taskRepository.count();
        long completedTasks = taskRepository.countCompleted();
        long pendingTasks = taskRepository.countPending();
        
        var allTasks = taskRepository.findAll();
        long highPriority = allTasks.stream().filter(t -> "high".equals(t.getPriority())).count();
        long mediumPriority = allTasks.stream().filter(t -> "medium".equals(t.getPriority())).count();
        long lowPriority = allTasks.stream().filter(t -> "low".equals(t.getPriority())).count();
        
        model.addAttribute("totalTasks", totalTasks);
        model.addAttribute("completedTasks", completedTasks);
        model.addAttribute("pendingTasks", pendingTasks);
        model.addAttribute("highPriority", highPriority);
        model.addAttribute("mediumPriority", mediumPriority);
        model.addAttribute("lowPriority", lowPriority);
        model.addAttribute("categories", categoryRepository.findAll());
        
        // Calcular porcentaje de completado
        double completionRate = totalTasks > 0 ? (completedTasks * 100.0 / totalTasks) : 0;
        model.addAttribute("completionRate", Math.round(completionRate));
        
        return "stats";
    }
}

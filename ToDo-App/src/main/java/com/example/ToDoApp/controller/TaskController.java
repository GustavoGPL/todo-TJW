package com.example.ToDoApp.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
//import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.ToDoApp.model.Task;
import com.example.ToDoApp.repository.TaskRepository;

import jakarta.persistence.EntityNotFoundException;

@Controller
@RequestMapping("/tasks")
public class TaskController {
    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
    
    @GetMapping("/list")
    public String showTaskList(Model model) {
        List<Task> tasks = taskRepository.findAll();
        model.addAttribute("tasks", tasks);
        return "taskList";
    }
    
    @GetMapping("/edit")
    public String editTaskList() {
        return "editList";
    }
    
    @GetMapping("/edit/{id}")
    public String editTask(@PathVariable Long id, Model model) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada com ID: " + id));

        model.addAttribute("task", task);

        return "editList";
    }

    // Endpoint para listar todas as tarefas
    @GetMapping
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // Endpoint para obter uma tarefa por ID
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        Optional<Task> taskOptional = taskRepository.findById(id);
        return taskOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
//    
//    @PostMapping("/edit")
//    public String editTask(Task task) {
//    	taskRepository.save(task);
//        return "taskList";
//    }
    
    @PostMapping("/complete/{id}")
    public ResponseEntity<String> completeTask(@PathVariable Long id) {
        Optional<Task> taskOptional = taskRepository.findById(id);
        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            task.setCompleted(true);
            taskRepository.save(task);
            return ResponseEntity.ok("redirect:/tasks/list");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Endpoint para adicionar uma nova tarefa
    @PostMapping
    public ResponseEntity<Task> addTask(@RequestBody Task task) {
        Task newTask = taskRepository.save(task);
        return ResponseEntity.ok(newTask);
    }

    // Endpoint para atualizar uma tarefa existente
    @PutMapping("/edit/{id}")
    public ResponseEntity<String> updateTask(@PathVariable Long id, @RequestBody Task updatedTask) {
        Optional<Task> existingTaskOptional = taskRepository.findById(id);

        return existingTaskOptional.map(existingTask -> {
            existingTask.setDescription(updatedTask.getDescription());
            existingTask.setCompleted(updatedTask.isCompleted());
            taskRepository.save(existingTask);
            return ResponseEntity.ok("redirect:/tasks/list");
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Endpoint para excluir uma tarefa por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

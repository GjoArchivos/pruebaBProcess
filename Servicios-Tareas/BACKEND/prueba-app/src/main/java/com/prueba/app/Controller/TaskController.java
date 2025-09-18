package com.prueba.app.Controller;

import com.prueba.app.Model.TaskDTO;
import com.prueba.app.Service.ITaskService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.net.URI;
import java.util.List;


@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "http://localhost:4200")
public class TaskController {

    @Autowired
    private ITaskService service;

    @GetMapping("/Lista")
    public List<TaskDTO> list() {
        return service.findAll();
    }

    @PostMapping("/create")
    public ResponseEntity<TaskDTO> create(@RequestBody TaskDTO taskDTO) {
        TaskDTO created = service.create(taskDTO);
        return ResponseEntity
                .created(URI.create("/api/tasks/" + created.getId()))
                .body(created);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<TaskDTO> update(@PathVariable Long id, @RequestBody TaskDTO taskDTO) {
        try {
            // El id viene por la URL y se inyecta al DTO
            taskDTO.setId(id);
            TaskDTO updated = service.update(taskDTO);
            return ResponseEntity.ok(updated);         // 200 OK

        } catch (EntityNotFoundException ex) {
            return ResponseEntity.notFound().build();  // 404
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

package com.prueba.app.Service;

import com.prueba.app.Model.TaskDTO;
import com.prueba.app.Repository.ITaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class TaskServiceImpl implements ITaskService  {

    @Autowired
    private ITaskRepository repository;

    @Override
    public List<TaskDTO> findAll() {
        return repository.findAll();
    }

    @Override
    public TaskDTO create(TaskDTO taskDTO) {
        if (taskDTO.getCompleted() == null) {
            taskDTO.setCompleted(0); // 0 = no completada
        }
        if (taskDTO.getCreatedAt() == null) {
            taskDTO.setCreatedAt(OffsetDateTime.now());
        }
        if (taskDTO.getUpdated_at() == null) {
            taskDTO.setUpdated_at(OffsetDateTime.now());
        }
        return repository.save(taskDTO);
    }

    @Override
    public TaskDTO update(TaskDTO taskDTO) {
            return repository.update(taskDTO);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}

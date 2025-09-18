package com.prueba.app.Repository;

import com.prueba.app.Model.TaskDTO;
import org.springframework.scheduling.config.Task;

import java.nio.channels.FileChannel;
import java.util.List;

public interface ITaskRepository {

    List<TaskDTO> findAll();

    TaskDTO save(TaskDTO taskDTO);

    TaskDTO update(TaskDTO taskDTO);

    void deleteById(Long id);
}

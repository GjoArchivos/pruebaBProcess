package com.prueba.app.Service;


import com.prueba.app.Model.TaskDTO;

import java.util.List;

public interface ITaskService {


    List<TaskDTO> findAll();

    TaskDTO create(TaskDTO taskDTO);

    TaskDTO update(TaskDTO taskDTO);

    void delete(Long id);
}

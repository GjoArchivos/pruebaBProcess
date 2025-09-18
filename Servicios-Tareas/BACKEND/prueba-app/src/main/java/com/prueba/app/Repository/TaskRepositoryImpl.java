package com.prueba.app.Repository;

import com.prueba.app.Model.TaskDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
@Transactional
public class TaskRepositoryImpl implements ITaskRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional(readOnly = true)
    public List<TaskDTO> findAll() {
        // HQL/JPQL (no dependes de nombres de columnas, sino de propiedades del Entity)
        return em.createQuery("SELECT t FROM TaskDTO t ORDER BY t.id DESC", TaskDTO.class)
                .getResultList();
    }

    @Override
    public TaskDTO save(TaskDTO taskDTO) {
        // Asegura valores por defecto si llegan nulos (opcional: puedes dejarlo al Service)
        if (taskDTO.getCompleted() == null) taskDTO.setCompleted(0);
        if (taskDTO.getCreatedAt() == null) taskDTO.setCreatedAt(OffsetDateTime.now());
        if (taskDTO.getUpdated_at() == null) taskDTO.setUpdated_at(OffsetDateTime.now());

        em.persist(taskDTO);       // INSERT
        // tras flush/commit, taskDTO.id queda poblado por AUTO_INCREMENT
        return taskDTO;
    }

    @Override
    public TaskDTO update(TaskDTO taskDTO) {
        // Opción segura: buscar y mutar el managed entity (evita que merge inserte si no existe)
        TaskDTO managed = em.find(TaskDTO.class, taskDTO.getId());
        if (managed == null) {
            throw new EntityNotFoundException("Task id " + taskDTO.getId() + " no existe");
        }

        if (taskDTO.getTitle() != null) managed.setTitle(taskDTO.getTitle());
        if (taskDTO.getCompleted() != null) managed.setCompleted(taskDTO.getCompleted());
        managed.setUpdated_at(OffsetDateTime.now());

        // No hace falta em.merge(managed) porque 'managed' ya está en el contexto.
        return managed;
    }

    @Override
    public void deleteById(Long id) {
        // getReference evita SELECT; si no existe lanzará EntityNotFoundException al acceder
        try {
            TaskDTO ref = em.getReference(TaskDTO.class, id);
            em.remove(ref); // DELETE
        } catch (EntityNotFoundException ignore) {
            // Si no existe, elegimos hacer "nada" (idempotente).
            // Si prefieres, relanza una excepción custom aquí.
        }
    }

}

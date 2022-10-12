package hexlet.code.app.service;

import hexlet.code.app.dto.TaskDto;
import hexlet.code.app.model.Task;
import hexlet.code.app.repository.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    @Override
    public Task createNewTask(final TaskDto dto) {
        final Task task = new Task();
        task.setName(dto.getName());
        task.setDescription(dto.getDescription());
        task.setExecutor(dto.getExecutor());
        task.setTaskStatus(dto.getTaskStatus());
        return taskRepository.save(task);
    }

    @Override
    public Task updateTask(final long id, final TaskDto dto) {
        final Task taskToUpdate = taskRepository.findById(id).get();
        taskToUpdate.setName(dto.getName());
        taskToUpdate.setDescription(dto.getDescription());
        taskToUpdate.setExecutor(dto.getExecutor());
        taskToUpdate.setTaskStatus(dto.getTaskStatus());
        return taskRepository.save(taskToUpdate);
    }

}

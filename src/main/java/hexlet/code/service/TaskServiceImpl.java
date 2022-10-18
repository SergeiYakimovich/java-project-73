package hexlet.code.service;

import hexlet.code.dto.TaskDto;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.service.interfaces.TaskService;
import hexlet.code.service.interfaces.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;

    @Override
    public Task createNewTask(final TaskDto dto) {
        final Task newTask = makeTaskFromDto(new Task(), dto);
        return taskRepository.save(newTask);
    }

    @Override
    public Task updateTask(final long id, final TaskDto dto) {
        final Task taskToUpdate = makeTaskFromDto(taskRepository.findById(id).get(), dto);
        return taskRepository.save(taskToUpdate);
    }

    private Task makeTaskFromDto(final Task task, final TaskDto dto) {
        task.setName(dto.getName());
        task.setDescription(dto.getDescription());
        task.setTaskStatus(new TaskStatus(dto.getTaskStatusId()));
        task.setAuthor(userService.getCurrentUser());
        if (dto.getExecutorId() != null) {
            task.setExecutor(new User(dto.getExecutorId()));
        }
        Set<Long> labelsIds = dto.getLabelIds();
        Set<Label> labels = null;
        if (labelsIds != null && labelsIds.size() != 0) {
            labels = dto.getLabelIds().stream()
                    .map(Label::new)
                    .collect(Collectors.toSet());
        }
        task.setLabels(labels);
        return task;
    }

}

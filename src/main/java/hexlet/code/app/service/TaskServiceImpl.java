package hexlet.code.app.service;

import hexlet.code.app.dto.TaskDto;
import hexlet.code.app.model.Label;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.service.interfaces.TaskService;
import hexlet.code.app.service.interfaces.UserService;
import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
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
    private final TaskStatusRepository taskStatusRepository;
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

    private Task makeTaskFromDto(final Task task,final TaskDto dto) {
        task.setName(dto.getName());
        task.setDescription(dto.getDescription());
        task.setTaskStatus(new TaskStatus(dto.getTaskStatusId()));
        task.setAuthor(userService.getCurrentUser());
        if (dto.getExecutorId() != null) {
            task.setExecutor(new User(dto.getExecutorId()));
        }
        Set<Long> labelsIds = dto.getLabelIds();
        Set<Label> labels = null;
        if (!CollectionUtils.isEmpty(labelsIds)) {
            labels = dto.getLabelIds().stream()
                    .map(Label::new)
                    .collect(Collectors.toSet());
        }
        task.setLabels(labels);
        return task;
    }

}

package hexlet.code.app.service;

import hexlet.code.app.dto.TaskStatusDto;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.service.interfaces.TaskStatusService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class TaskStatusServiceImpl implements TaskStatusService {

    private final TaskStatusRepository taskStatusRepository;

    @Override
    public TaskStatus createNewStatus(final TaskStatusDto dto) {
        final TaskStatus taskStatus = new TaskStatus();
        taskStatus.setName(dto.getName());
        return taskStatusRepository.save(taskStatus);
    }

    @Override
    public TaskStatus updateStatus(final long id, final TaskStatusDto dto) {
        final TaskStatus taskStatusToUpdate = taskStatusRepository.findById(id).get();
        taskStatusToUpdate.setName(dto.getName());
        return taskStatusRepository.save(taskStatusToUpdate);
    }

}

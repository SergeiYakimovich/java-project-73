package hexlet.code.app.service.interfaces;

import hexlet.code.app.dto.TaskStatusDto;
import hexlet.code.app.model.TaskStatus;

public interface TaskStatusService {

    TaskStatus createNewStatus(TaskStatusDto dto);

    TaskStatus updateStatus(long id, TaskStatusDto dto);

}

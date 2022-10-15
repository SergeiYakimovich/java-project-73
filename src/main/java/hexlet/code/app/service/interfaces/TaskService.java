package hexlet.code.app.service.interfaces;


import hexlet.code.app.dto.TaskDto;
import hexlet.code.app.model.Task;

public interface TaskService {

    Task createNewTask(TaskDto dto);

    Task updateTask(long id, TaskDto dto);

}

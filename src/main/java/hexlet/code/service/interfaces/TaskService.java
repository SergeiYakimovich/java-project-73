package hexlet.code.service.interfaces;


import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;

public interface TaskService {

    Task createNewTask(TaskDto dto);

    Task updateTask(long id, TaskDto dto);

}

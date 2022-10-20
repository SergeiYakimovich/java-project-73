package hexlet.code.controller;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.service.interfaces.TaskStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import static hexlet.code.controller.TaskStatusController.STATUS_CONTROLLER_PATH;
import static org.springframework.http.HttpStatus.CREATED;

@AllArgsConstructor
@RestController
@RequestMapping("${base-url}" + STATUS_CONTROLLER_PATH)
public class TaskStatusController {

    public static final String STATUS_CONTROLLER_PATH = "/statuses";
    public static final String ID = "/{id}";

    private final TaskStatusService taskStatusService;
    private final TaskStatusRepository taskStatusRepository;

    @Operation(summary = "Create new Status")
    @ApiResponse(responseCode = "201", description = "Status created")
    @PostMapping
    @ResponseStatus(CREATED)
    public TaskStatus registerNewStatus(@RequestBody @Valid final TaskStatusDto dto) {
        return taskStatusService.createNewStatus(dto);
    }

    // Content используется для укзания содержимого ответа
    @ApiResponses(@ApiResponse(responseCode = "200", content =
            // Указываем тип содержимого ответа
    @Content(schema = @Schema(implementation = TaskStatus.class))
    ))
    @GetMapping
    @Operation(summary = "Get all statuses")
    public List<TaskStatus> getAllStatuses() {
        return taskStatusRepository.findAll()
                .stream()
                .toList();
    }

    @ApiResponses(@ApiResponse(responseCode = "200"))
    @GetMapping(ID)
    @Operation(summary = "Get status")
    public TaskStatus getStatusById(@PathVariable final Long id) {
        return taskStatusRepository.findById(id).get();
    }

    @PutMapping(ID)
    @Operation(summary = "Update status")
    public TaskStatus updateStatus(@PathVariable final long id, @RequestBody @Valid final TaskStatusDto dto) {
        return taskStatusService.updateStatus(id, dto);
    }

    @Operation(summary = "Delete status")
    @DeleteMapping(ID)
    public void deleteStatus(@PathVariable final long id) throws Exception {
        taskStatusRepository.deleteById(id);
    }

}

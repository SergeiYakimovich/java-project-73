package hexlet.code.app.controller;

import hexlet.code.app.dto.StatusDto;
import hexlet.code.app.model.Status;
import hexlet.code.app.repository.StatusRepository;
import hexlet.code.app.service.StatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import javax.validation.Valid;

import hexlet.code.app.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static hexlet.code.app.controller.StatusController.STATUS_CONTROLLER_PATH;
import static hexlet.code.app.controller.UserController.ID;
import static org.springframework.http.HttpStatus.CREATED;

@AllArgsConstructor
@RestController
@RequestMapping("${base-url}" + STATUS_CONTROLLER_PATH)
public class StatusController {

    public static final String STATUS_CONTROLLER_PATH = "/statuses";

    private static final String ONLY_OWNER_BY_ID = """
            @userRepository.findById(#id).get().getEmail() == authentication.getName()
        """;

    private final StatusService statusService;
    private final UserRepository userRepository;
    private final StatusRepository statusRepository;

    @Operation(summary = "Create new Status")
    @ApiResponse(responseCode = "201", description = "Status created")
    @PostMapping
    @ResponseStatus(CREATED)
    public Status registerNewStatus(@RequestBody @Valid final StatusDto dto) {
        return statusService.createNewStatus(dto);
    }

    // Content используется для укзания содержимого ответа
    @ApiResponses(@ApiResponse(responseCode = "200", content =
            // Указываем тип содержимого ответа
    @Content(schema = @Schema(implementation = Status.class))
    ))
    @GetMapping
    public List<Status> getAllStatuses() {
        return statusRepository.findAll()
                .stream()
                .toList();
    }

    @ApiResponses(@ApiResponse(responseCode = "200"))
    @GetMapping(ID)
    public Status getStatusById(@PathVariable final Long id) {
        return statusRepository.findById(id).get();
    }

    @PutMapping(ID)
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public Status updateStatus(@PathVariable final long id, @RequestBody @Valid final StatusDto dto) {
        return statusService.updateStatus(id, dto);
    }

    @DeleteMapping(ID)
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public void deleteStatus(@PathVariable final long id) {
        statusRepository.deleteById(id);
    }

}

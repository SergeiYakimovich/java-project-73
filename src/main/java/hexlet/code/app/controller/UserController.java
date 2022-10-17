package hexlet.code.app.controller;

import com.rollbar.notifier.Rollbar;
import hexlet.code.app.dto.UserDto;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import lombok.AllArgsConstructor;
import javax.validation.Valid;
import java.util.List;
import static hexlet.code.app.controller.UserController.USER_CONTROLLER_PATH;
import static org.springframework.http.HttpStatus.CREATED;

@AllArgsConstructor
@RestController
@RequestMapping("${base-url}" + USER_CONTROLLER_PATH)
public class UserController {
    public static final String USER_CONTROLLER_PATH = "/users";
    public static final String ID = "/{id}";

    private static final String ONLY_OWNER_BY_ID = """
            @userRepository.findById(#id).get().getEmail() == authentication.getName()
        """;

    private final UserService userService;
    private final UserRepository userRepository;
    private final Rollbar rollbar;

    @Operation(summary = "Create new user")
    @ApiResponse(responseCode = "201", description = "User created")
    @PostMapping
    @ResponseStatus(CREATED)
    public User registerNew(@RequestBody @Valid final UserDto dto) {
//        rollbar.log("Create new user " + dto.getFirstName() + " " + dto.getLastName());
        return userService.createNewUser(dto);
    }

    @ApiResponses(@ApiResponse(responseCode = "200", content =
    @Content(schema = @Schema(implementation = User.class))
    ))
    @GetMapping
    @Operation(summary = "Get all users")
    public List<User> getAll() throws Exception {
        return userRepository.findAll()
                .stream()
                .toList();
    }

    @ApiResponses(@ApiResponse(responseCode = "200"))
    @GetMapping(ID)
    @Operation(summary = "Get user")
    public User getUserById(@PathVariable final Long id) {
        return userRepository.findById(id).get();
    }

    @PutMapping(ID)
    @Operation(summary = "Update user")
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public User update(@PathVariable final long id, @RequestBody @Valid final UserDto dto) {
        return userService.updateUser(id, dto);
    }

    @DeleteMapping(ID)
    @Operation(summary = "Delete user")
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public void delete(@PathVariable final long id) throws Exception {
        final User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (user.getAuthorTasks().size() != 0 || user.getExecutorTasks().size() != 0) {
            throw new Exception("Нельзя удалить, т.к. используется в задачах");
        }
        userRepository.deleteById(id);
    }

}

package hexlet.code.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import javax.validation.Valid;

import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.service.UserService;
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

import hexlet.code.app.dto.UserDto;
import hexlet.code.app.model.User;

import static hexlet.code.app.config.UrlConfig.BASE_URL;
import static hexlet.code.app.config.UrlConfig.ID;
import static hexlet.code.app.config.UrlConfig.USER_CONTROLLER;
import static org.springframework.http.HttpStatus.CREATED;

@AllArgsConstructor
@RestController
@RequestMapping(BASE_URL + USER_CONTROLLER)
public class UserController {
    private static final String ONLY_OWNER_BY_ID = """
            @userRepository.findById(#id).get().getEmail() == authentication.getName()
        """;

    private final UserService userService;
    private final UserRepository userRepository;

    @Operation(summary = "Create new user")
    @ApiResponse(responseCode = "201", description = "User created")
    @PostMapping
    @ResponseStatus(CREATED)
    public User registerNewUser(@RequestBody @Valid final UserDto dto) {
        return userService.createNewUser(dto);
    }

    // Content используется для укзания содержимого ответа
    @ApiResponses(@ApiResponse(responseCode = "200", content =
            // Указываем тип содержимого ответа
    @Content(schema = @Schema(implementation = User.class))
    ))
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .toList();
    }

    @ApiResponses(@ApiResponse(responseCode = "200"))
    @GetMapping(ID)
    public User getUserById(@PathVariable final Long id) {
        return userRepository.findById(id).get();
    }

    @PutMapping(ID)
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public User updateUser(@PathVariable final long id, @RequestBody @Valid final UserDto dto) {
        return userService.updateUser(id, dto);
    }

    @DeleteMapping(ID)
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public void deleteUser(@PathVariable final long id) {
        userRepository.deleteById(id);
    }

}

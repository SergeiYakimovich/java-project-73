package hexlet.code.app.controller;

import hexlet.code.app.dto.LabelDto;
import hexlet.code.app.model.Label;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.service.LabelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@AllArgsConstructor
@RestController
@RequestMapping("${base-url}" + LabelController.LABEL_CONTROLLER_PATH)
public class LabelController {

    public static final String LABEL_CONTROLLER_PATH = "/labels";
    public static final String ID = "/{id}";
//    private static final String ONLY_OWNER_BY_ID = """
//            @userRepository.findById(#id).get().getEmail() == authentication.getName()
//        """;

    private final LabelService labelService;
    //    private final UserRepository userRepository;
    private final LabelRepository labelRepository;

    @Operation(summary = "Create new Label")
    @ApiResponse(responseCode = "201", description = "Label created")
    @PostMapping
    @ResponseStatus(CREATED)
    public Label registerNewTLabel(@RequestBody @Valid final LabelDto dto) {
        return labelService.createNewLabel(dto);
    }

    // Content используется для укзания содержимого ответа
    @ApiResponses(@ApiResponse(responseCode = "200", content =
            // Указываем тип содержимого ответа
    @Content(schema = @Schema(implementation = Label.class))
    ))
    @GetMapping
    public List<Label> getAllLabels() {
        return labelRepository.findAll()
                .stream()
                .toList();
    }

    @ApiResponses(@ApiResponse(responseCode = "200"))
    @GetMapping(ID)
    public Label getLabelById(@PathVariable final Long id) {
        return labelRepository.findById(id).get();
    }

    @PutMapping(ID)
//    @PreAuthorize(ONLY_OWNER_BY_ID)
    public Label updateLabel(@PathVariable final long id, @RequestBody @Valid final LabelDto dto) {
        return labelService.updateLabel(id, dto);
    }

    @DeleteMapping(ID)
//    @PreAuthorize(ONLY_OWNER_BY_ID)
    public void deleteLabel(@PathVariable final long id) {
        labelRepository.deleteById(id);
    }

}

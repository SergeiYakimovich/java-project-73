package hexlet.code.app.dto;

import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;

import hexlet.code.app.model.Status;
import hexlet.code.app.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {

    @NotBlank
    private String name;

    private String description;

    @ManyToOne
    private Status taskStatus;

    @ManyToOne
    private User executor;

}

package hexlet.code.app.model;

import java.util.Date;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.TemporalType.TIMESTAMP;

@Entity
@Getter
@Setter
@Table(name = "task_statuses")
@NoArgsConstructor
@AllArgsConstructor
public class TaskStatus {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @CreationTimestamp
    @Temporal(TIMESTAMP)
    private Date createdAt;

    @NotBlank
    @Column(unique = true)
    private String name;

    @OneToMany(mappedBy = "taskStatus")
    @JsonIgnore
    private Set<Task> tasks;

    public TaskStatus(Long id) {
        this.id = id;
    }

}

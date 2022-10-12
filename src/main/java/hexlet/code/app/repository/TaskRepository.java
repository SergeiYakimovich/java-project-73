package hexlet.code.app.repository;

import hexlet.code.app.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Optional<Task> findByName(String name);
    Optional<Task> findById(Long id);
    List<Task> findAll();

    @Query("select u.taskName from tasks u inner join u.authorId users where users.Id = :authorId")
    List<Task> findAllByParams(@Param("authorId") Long authorId);
}

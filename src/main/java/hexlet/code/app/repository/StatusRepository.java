package hexlet.code.app.repository;

import hexlet.code.app.model.Status;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StatusRepository extends CrudRepository<Status, Long> {

    Optional<Status> findByName(String name);
    Optional<Status> findById(Long id);
    List<Status> findAll();

}

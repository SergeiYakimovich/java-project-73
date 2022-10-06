package hexlet.code.app.repository;

import hexlet.code.app.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByFirstName(String firstName);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    List<User> findAll();

}

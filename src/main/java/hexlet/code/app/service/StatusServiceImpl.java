package hexlet.code.app.service;

import hexlet.code.app.dto.StatusDto;
import hexlet.code.app.model.Status;
import hexlet.code.app.repository.StatusRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@AllArgsConstructor
public class StatusServiceImpl implements StatusService {

    private final StatusRepository statusRepository;

    @Override
    public Status createNewStatus(final StatusDto dto) {
        final Status status = new Status();
        status.setName(dto.getName());
        return statusRepository.save(status);
    }

    @Override
    public Status updateStatus(final long id, final StatusDto dto) {
        final Status statusToUpdate = statusRepository.findById(id).get();
        statusToUpdate.setName(dto.getName());
        return statusRepository.save(statusToUpdate);
    }

}

package hexlet.code.app.service;

import hexlet.code.app.dto.StatusDto;
import hexlet.code.app.model.Status;

public interface StatusService {

    Status createNewStatus(StatusDto dto);

    Status updateStatus(long id, StatusDto dto);

}

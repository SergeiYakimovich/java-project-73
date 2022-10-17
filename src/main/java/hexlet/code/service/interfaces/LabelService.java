package hexlet.code.service.interfaces;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;

public interface LabelService {

    Label createNewLabel(LabelDto dto);

    Label updateLabel(long id, LabelDto dto);

}

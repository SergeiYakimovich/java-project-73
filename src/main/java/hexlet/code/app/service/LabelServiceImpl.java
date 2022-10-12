package hexlet.code.app.service;

import hexlet.code.app.dto.LabelDto;
import hexlet.code.app.model.Label;
import hexlet.code.app.repository.LabelRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@AllArgsConstructor
public class LabelServiceImpl implements LabelService {

    private final LabelRepository labelRepository;

    @Override
    public Label createNewLabel(final LabelDto dto) {
        final Label label = new Label();
        label.setName(dto.getName());
        return labelRepository.save(label);
    }

    @Override
    public Label updateLabel(final long id, final LabelDto dto) {
        final Label labelToUpdate = labelRepository.findById(id).get();
        labelToUpdate.setName(dto.getName());
        return labelRepository.save(labelToUpdate);
    }

}

package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.LabelRepository;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles(SpringConfigForIT.TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForIT.class)

public class LabelControllerTest {

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TestUtils utils;

    @BeforeEach
    public void clear() {
        utils.tearDown();
    }

    @Test
    public void registration() throws Exception {
        assertEquals(0, labelRepository.count());
        utils.regDefaultUser();
        utils.regDefaultLabel(TestUtils.TEST_USERNAME).andExpect(status().isCreated());
        assertEquals(1, labelRepository.count());
    }

    @Test
    public void getLabelById() throws Exception {
        utils.regDefaultUser();
        utils.regDefaultLabel(TestUtils.TEST_USERNAME);
        final Label expectedLabel = labelRepository.findAll().get(0);
        final var response = utils.perform(
                        get(TestUtils.BASE_URL + LabelController.LABEL_CONTROLLER_PATH + UserController.ID, expectedLabel.getId()), TestUtils.TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final TaskStatus taskStatus = TestUtils.fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertEquals(expectedLabel.getId(), taskStatus.getId());
        assertEquals(expectedLabel.getName(), taskStatus.getName());
    }

    @Test
    public void getLabelByIdFails() throws Exception {
        utils.regDefaultUser();
        utils.regDefaultLabel(TestUtils.TEST_USERNAME);
        final Label expectedLabel = labelRepository.findAll().get(0);
        Exception exception = assertThrows(
                Exception.class, () -> utils.perform(get(TestUtils.BASE_URL + LabelController.LABEL_CONTROLLER_PATH + UserController.ID,
                        expectedLabel.getId()))
        );
        String message = exception.getMessage();
        assertTrue(message.contains("No value present"));
    }

    @Test
    public void getAllLabels() throws Exception {
        utils.regDefaultUser();
        utils.regDefaultLabel(TestUtils.TEST_USERNAME);
        final var response = utils.perform(MockMvcRequestBuilders.get(TestUtils.BASE_URL + LabelController.LABEL_CONTROLLER_PATH), TestUtils.TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<Label> labels = TestUtils.fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        assertThat(labels).hasSize(1);
    }

    @Test
    public void twiceRegTheSameLabelFail() throws Exception {
        utils.regDefaultUser();
        utils.regDefaultLabel(TestUtils.TEST_USERNAME).andExpect(status().isCreated());
        utils.regDefaultLabel(TestUtils.TEST_USERNAME).andExpect(status().isUnprocessableEntity());

        assertEquals(1, labelRepository.count());
    }

    @Test
    public void updateLabel() throws Exception {
        utils.regDefaultUser();
        utils.regDefaultLabel(TestUtils.TEST_USERNAME);
        final Long labelId = labelRepository.findAll().get(0).getId();
        final var labelDto = new LabelDto(TestUtils.TEST_LABELNAME_2);

        final var updateRequest = MockMvcRequestBuilders.put(TestUtils.BASE_URL + LabelController.LABEL_CONTROLLER_PATH + UserController.ID,
                labelId)
                .content(TestUtils.asJson(labelDto))
                .contentType(APPLICATION_JSON);

        utils.perform(updateRequest, TestUtils.TEST_USERNAME).andExpect(status().isOk());
        assertTrue(labelRepository.existsById(labelId));
        assertNull(labelRepository.findByName(TestUtils.TEST_LABELNAME).orElse(null));
        assertNotNull(labelRepository.findByName(TestUtils.TEST_LABELNAME_2).orElse(null));
    }

    @Test
    public void deleteLabel() throws Exception {
        utils.regDefaultUser();
        utils.regDefaultLabel(TestUtils.TEST_USERNAME);
        final Long labelId = labelRepository.findAll().get(0).getId();

        utils.perform(MockMvcRequestBuilders.delete(TestUtils.BASE_URL + LabelController.LABEL_CONTROLLER_PATH + UserController.ID, labelId), TestUtils.TEST_USERNAME)
                .andExpect(status().isOk());
        assertEquals(0, labelRepository.count());
    }

    @Test
    public void deleteLabelFails() throws Exception {
        utils.regDefaultUser();
        utils.regDefaultLabel(TestUtils.TEST_USERNAME);
        final Long labelId = labelRepository.findAll().get(0).getId() + 1;
        utils.perform(MockMvcRequestBuilders.delete(TestUtils.BASE_URL + LabelController.LABEL_CONTROLLER_PATH + UserController.ID, labelId), TestUtils.TEST_USERNAME)
                .andExpect(status().isNotFound());
        assertEquals(1, labelRepository.count());
    }

}

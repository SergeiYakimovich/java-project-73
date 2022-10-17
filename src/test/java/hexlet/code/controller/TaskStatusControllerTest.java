package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import hexlet.code.utils.TestUtils;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static hexlet.code.utils.TestUtils.BASE_URL;
import static hexlet.code.utils.TestUtils.TEST_STATUSNAME;
import static hexlet.code.utils.TestUtils.TEST_STATUSNAME_2;
import static hexlet.code.utils.TestUtils.TEST_USERNAME;
import static hexlet.code.utils.TestUtils.asJson;
import static hexlet.code.utils.TestUtils.fromJson;
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

public class TaskStatusControllerTest {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TestUtils utils;

    @BeforeEach
    public void clear() {
        utils.tearDown();
    }

    @Test
    public void registration() throws Exception {
        assertEquals(0, taskStatusRepository.count());
        utils.regDefaultUser();
        utils.regDefaultStatus(TEST_USERNAME).andExpect(status().isCreated());
        assertEquals(1, taskStatusRepository.count());
    }

    @Test
    public void getStatusById() throws Exception {
        utils.regDefaultUser();
        utils.regDefaultStatus(TEST_USERNAME);
        final TaskStatus expectedTaskStatus = taskStatusRepository.findAll().get(0);
        final var response = utils.perform(
                        get(BASE_URL + TaskStatusController.STATUS_CONTROLLER_PATH + UserController.ID, expectedTaskStatus.getId()), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final TaskStatus taskStatus = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        assertEquals(expectedTaskStatus.getId(), taskStatus.getId());
        assertEquals(expectedTaskStatus.getName(), taskStatus.getName());
    }

    @Test
    public void getStatusByIdFails() throws Exception {
        utils.regDefaultUser();
        utils.regDefaultStatus(TEST_USERNAME);
        final TaskStatus expectedStatus = taskStatusRepository.findAll().get(0);

        Exception exception = assertThrows(
                Exception.class, () -> utils.perform(get(BASE_URL + TaskStatusController.STATUS_CONTROLLER_PATH + UserController.ID,
                        expectedStatus.getId()))
        );
        String message = exception.getMessage();
        assertTrue(message.contains("No value present"));
    }

    @Test
    public void getAllStatuses() throws Exception {
        utils.regDefaultUser();
        utils.regDefaultStatus(TEST_USERNAME);
        final var response = utils.perform(MockMvcRequestBuilders.get(BASE_URL + TaskStatusController.STATUS_CONTROLLER_PATH), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<TaskStatus> taskStatuses = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        assertThat(taskStatuses).hasSize(1);
    }

    @Test
    public void twiceRegTheSameStatusFail() throws Exception {
        utils.regDefaultUser();
        utils.regDefaultStatus(TEST_USERNAME).andExpect(status().isCreated());
        utils.regDefaultStatus(TEST_USERNAME).andExpect(status().isUnprocessableEntity());

        assertEquals(1, taskStatusRepository.count());
    }

    @Test
    public void updateStatus() throws Exception {
        utils.regDefaultUser();
        utils.regDefaultStatus(TEST_USERNAME);
        final Long statusId = taskStatusRepository.findAll().get(0).getId();
        final var statusDto = new TaskStatusDto(TEST_STATUSNAME_2);
        final var updateRequest = MockMvcRequestBuilders.put(BASE_URL + TaskStatusController.STATUS_CONTROLLER_PATH + UserController.ID, statusId)
                .content(asJson(statusDto))
                .contentType(APPLICATION_JSON);

        utils.perform(updateRequest, TEST_USERNAME).andExpect(status().isOk());
        assertTrue(taskStatusRepository.existsById(statusId));
        assertNull(taskStatusRepository.findByName(TEST_STATUSNAME).orElse(null));
        assertNotNull(taskStatusRepository.findByName(TEST_STATUSNAME_2).orElse(null));
    }

    @Test
    public void deleteStatus() throws Exception {
        utils.regDefaultUser();
        utils.regDefaultStatus(TEST_USERNAME);
        final Long statusId = taskStatusRepository.findAll().get(0).getId();

        utils.perform(MockMvcRequestBuilders.delete(BASE_URL + TaskStatusController.STATUS_CONTROLLER_PATH + UserController.ID, statusId), TEST_USERNAME)
                .andExpect(status().isOk());
        assertEquals(0, taskStatusRepository.count());
    }

    @Test
    public void deleteStatusFails() throws Exception {
        utils.regDefaultUser();
        utils.regDefaultStatus(TEST_USERNAME);
        final Long statusId = taskStatusRepository.findAll().get(0).getId() + 1;

        utils.perform(MockMvcRequestBuilders.delete(BASE_URL + TaskStatusController.STATUS_CONTROLLER_PATH + UserController.ID, statusId), TEST_USERNAME)
                .andExpect(status().isNotFound());
        assertEquals(1, taskStatusRepository.count());
    }

}

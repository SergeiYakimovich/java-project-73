package hexlet.code.app.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.app.config.SpringConfigForIT;
import hexlet.code.app.dto.TaskStatusDto;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import hexlet.code.app.utils.TestUtils;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static hexlet.code.app.config.SpringConfigForIT.TEST_PROFILE;
import static hexlet.code.app.controller.TaskStatusController.STATUS_CONTROLLER_PATH;
import static hexlet.code.app.controller.UserController.ID;
import static hexlet.code.app.utils.TestUtils.TEST_USERNAME;
import static hexlet.code.app.utils.TestUtils.asJson;
import static hexlet.code.app.utils.TestUtils.fromJson;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForIT.class)

public class TaskStatusControllerTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TestUtils utils;

    @BeforeEach
    public void clear() {
        utils.tearDown();
//        userRepository.deleteAll();
//        statusRepository.deleteAll();
    }

    public static final String TEST_STATUSNAME = "new";
    public static final String TEST_STATUSNAME_2 = "new2";

    private final TaskStatusDto testRegTaskStatusDto = new TaskStatusDto(
            TEST_STATUSNAME
    );

    public ResultActions regDefaultStatus(final String byUser) throws Exception {
        return regStatus(testRegTaskStatusDto, byUser);
    }

    public ResultActions regStatus(final TaskStatusDto dto, final String byUser) throws Exception {
        final var request = post(STATUS_CONTROLLER_PATH)
                .content(asJson(dto))
                .contentType(APPLICATION_JSON);

        return utils.perform(request, byUser);
    }

    @Test
    public void registration() throws Exception {
        assertEquals(0, taskStatusRepository.count());
        utils.regDefaultUser();
        regDefaultStatus(TEST_USERNAME).andExpect(status().isCreated());
        assertEquals(1, taskStatusRepository.count());
    }

    @Test
    public void getStatusById() throws Exception {
        utils.regDefaultUser();
        regDefaultStatus(TEST_USERNAME);
        final TaskStatus expectedTaskStatus = taskStatusRepository.findAll().get(0);
        final var response = utils.perform(
                        get(STATUS_CONTROLLER_PATH + ID, expectedTaskStatus.getId(), TEST_USERNAME)
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final TaskStatus taskStatus = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertEquals(expectedTaskStatus.getId(), taskStatus.getId());
        assertEquals(expectedTaskStatus.getName(), taskStatus.getName());
    }

//    @Disabled("For now active only positive tests")
//    @Test
//    public void getStatusByIdFails() throws Exception {
//        utils.regDefaultUser();
//        regDefaultStatus(TEST_USERNAME);
//        final Status expectedStatus = statusRepository.findAll().get(0);
//        utils.perform(get(STATUS_CONTROLLER_PATH + ID, expectedStatus.getId(), TEST_USERNAME_2))
//                .andExpect(status().isUnauthorized());
//
//    }

    @Test
    public void getAllStatuses() throws Exception {
        utils.regDefaultUser();
        regDefaultStatus(TEST_USERNAME);
        final var response = utils.perform(get(STATUS_CONTROLLER_PATH), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<TaskStatus> taskStatuses = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(taskStatuses).hasSize(1);
    }

//    @Disabled("For now active only positive tests")
//    @Test
//    public void twiceRegTheSameStatusFail() throws Exception {
//        utils.regDefaultUser();
//        regDefaultStatus(TEST_USERNAME).andExpect(status().isCreated());
//        regDefaultStatus(TEST_USERNAME).andExpect(status().isBadRequest());
//
//        assertEquals(1, statusRepository.count());
//    }

    @Test
    public void updateStatus() throws Exception {
        utils.regDefaultUser();
        regDefaultStatus(TEST_USERNAME);

        final Long statusId = taskStatusRepository.findAll().get(0).getId();

        final var statusDto = new TaskStatusDto(TEST_STATUSNAME_2);

        final var updateRequest = put(STATUS_CONTROLLER_PATH + ID, statusId)
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
        regDefaultStatus(TEST_USERNAME);

        final Long statusId = taskStatusRepository.findAll().get(0).getId();

        utils.perform(delete(STATUS_CONTROLLER_PATH + ID, statusId), TEST_USERNAME)
                .andExpect(status().isOk());

        assertEquals(0, taskStatusRepository.count());
    }

//    @Disabled("For now active only positive tests")
//    @Test
//    public void deleteStatusFails() throws Exception {
//        utils.regDefaultUser();
//        regDefaultStatus(TEST_USERNAME);
//        regStatus(new StatusDto(
//                TEST_STATUSNAME_2
//        ), TEST_USERNAME);
//
//        final Long statusId = statusRepository.findByName(TEST_STATUSNAME_2).get().getId();
//
//        utils.perform(delete(STATUS_CONTROLLER_PATH + ID, statusId), TEST_USERNAME_2)
//                .andExpect(status().isNotFound() /*isForbidden()*/);
//
//        assertEquals(2, statusRepository.count());
//    }

}

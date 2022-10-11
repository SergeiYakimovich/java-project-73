package hexlet.code.app.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.app.dto.StatusDto;
import hexlet.code.app.model.Status;
import hexlet.code.app.repository.StatusRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import hexlet.code.app.utils.TestUtils;

import java.util.List;

import static hexlet.code.app.config.UrlConfig.BASE_URL;
import static hexlet.code.app.config.UrlConfig.ID;

import static hexlet.code.app.config.UrlConfig.STATUS_CONTROLLER;
import static hexlet.code.app.utils.SpringConfigForTest.TEST_PROFILE;
import static hexlet.code.app.utils.TestUtils.TEST_STATUS;
import static hexlet.code.app.utils.TestUtils.TEST_STATUS_2;
import static hexlet.code.app.utils.TestUtils.TEST_USERNAME;
import static hexlet.code.app.utils.TestUtils.TEST_USERNAME_2;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForTest.class)

public class StatusControllerTest {

    @Autowired
//    private UserRepository userRepository;
    private StatusRepository statusRepository;

    @Autowired
    private TestUtils utils;

    @AfterEach
    public void clear() {
        utils.tearDown();
    }

    @Test
    public void registration() throws Exception {
        assertEquals(0, statusRepository.count());
        utils.regDefaultStatus().andExpect(status().isCreated());
        assertEquals(1, statusRepository.count());
    }

    @Test
    public void getStatusById() throws Exception {
        utils.regDefaultStatus();
        final Status expectedStatus = statusRepository.findAll().get(0);
        final var response = utils.perform(
                        get(BASE_URL + STATUS_CONTROLLER + ID, expectedStatus.getId())
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final Status status = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertEquals(expectedStatus.getId(), status.getId());
        assertEquals(expectedStatus.getName(), status.getName());
    }

    @Disabled("For now active only positive tests")
    @Test
    public void getStatusByIdFails() throws Exception {
        utils.regDefaultStatus();
        final Status expectedStatus = statusRepository.findAll().get(0);
        utils.perform(get(BASE_URL + STATUS_CONTROLLER + ID, expectedStatus.getId()))
                .andExpect(status().isUnauthorized());

    }

    @Test
    public void getAllStatuses() throws Exception {
        utils.regDefaultStatus();
        final var response = utils.perform(get(BASE_URL + STATUS_CONTROLLER))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<Status> statuses = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(statuses).hasSize(1);
    }

    @Disabled("For now active only positive tests")
    @Test
    public void twiceRegTheSameUserFail() throws Exception {
        utils.regDefaultStatus().andExpect(status().isCreated());
        utils.regDefaultStatus().andExpect(status().isBadRequest());

        assertEquals(1, statusRepository.count());
    }

    @Test
    public void updateStatus() throws Exception {
        utils.regDefaultStatus();

        final Long statusId = statusRepository.findAll().get(0).getId();

        final var statusDto = new StatusDto(TEST_STATUS_2);

        final var updateRequest = put(BASE_URL + STATUS_CONTROLLER + ID, statusId)
                .content(asJson(statusDto))
                .contentType(APPLICATION_JSON);

        utils.perform(updateRequest).andExpect(status().isOk());

        assertTrue(statusRepository.existsById(statusId));
        assertNull(statusRepository.findByName(TEST_STATUS).orElse(null));
        assertNotNull(statusRepository.findByName(TEST_STATUS_2).orElse(null));
    }

    @Test
    public void deleteStatus() throws Exception {
        utils.regDefaultStatus();

        final Long statusId = statusRepository.findAll().get(0).getId();

        utils.perform(delete(BASE_URL + STATUS_CONTROLLER + ID, statusId), TEST_USERNAME)
                .andExpect(status().isOk());

        assertEquals(0, statusRepository.count());
    }

    @Disabled("For now active only positive tests")
    @Test
    public void deleteUserFails() throws Exception {
        utils.regDefaultStatus();
        utils.regStatus(new StatusDto(
                TEST_STATUS_2
        ));

        final Long statusId = statusRepository.findByName(TEST_STATUS_2).get().getId();

        utils.perform(delete(BASE_URL + STATUS_CONTROLLER + ID, statusId), TEST_USERNAME_2)
                .andExpect(status().isForbidden());

        assertEquals(2, statusRepository.count());
    }


}

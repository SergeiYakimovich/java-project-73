package hexlet.code.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.component.JWTHelper;
import hexlet.code.dto.LabelDto;
import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.dto.UserDto;
import hexlet.code.model.Label;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.controller.LabelController;
import hexlet.code.controller.TaskController;
import hexlet.code.controller.TaskStatusController;
import hexlet.code.controller.UserController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Map;
import java.util.Set;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Component
public class TestUtils {
    public static final String BASE_URL = "/api";
    public static final String TEST_USERNAME = "email@email.com";
    public static final String TEST_USERNAME_2 = "email2@email.com";
    public static final String TEST_LABELNAME = "label";
    public static final String TEST_LABELNAME_2 = "label2";
    public static final String TEST_STATUSNAME = "status";
    public static final String TEST_STATUSNAME_2 = "status2";
    private final LabelDto testRegLabelDto = new LabelDto(
            TEST_LABELNAME
    );
    private final TaskStatusDto testRegTaskStatusDto = new TaskStatusDto(
            TEST_STATUSNAME
    );
    private final UserDto testRegistrationDto = new UserDto(
            TEST_USERNAME,
            "fname",
            "lname",
            "pwd"
    );

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private JWTHelper jwtHelper;

    public void tearDown() {
        taskRepository.deleteAll();
        taskStatusRepository.deleteAll();
        labelRepository.deleteAll();
        userRepository.deleteAll();
    }

    public UserDto getTestRegistrationDto() {
        return testRegistrationDto;
    }

    public User getUserByEmail(final String email) {
        return userRepository.findByEmail(email).get();
    }

    public ResultActions regDefaultUser() throws Exception {
        return regUser(testRegistrationDto);
    }

    public ResultActions regUser(final UserDto dto) throws Exception {
        final var request = MockMvcRequestBuilders.post(BASE_URL + UserController.USER_CONTROLLER_PATH)
                .content(asJson(dto))
                .contentType(APPLICATION_JSON);

        return perform(request);
    }

    public ResultActions perform(final MockHttpServletRequestBuilder request, final String byUser) throws Exception {
        final String token = jwtHelper.expiring(Map.of("username", byUser));
        request.header(AUTHORIZATION, token);

        return perform(request);
    }

    public ResultActions perform(final MockHttpServletRequestBuilder request) throws Exception {
        return mockMvc.perform(request);
    }

    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    public static String asJson(final Object object) throws JsonProcessingException {
        return MAPPER.writeValueAsString(object);
    }

    public static <T> T fromJson(final String json, final TypeReference<T> to) throws JsonProcessingException {
        return MAPPER.readValue(json, to);
    }

    public ResultActions regDefaultLabel(final String byUser) throws Exception {
        return regLabel(testRegLabelDto, byUser);
    }

    public ResultActions regLabel(final LabelDto dto, final String byUser) throws Exception {
        final var request = MockMvcRequestBuilders.post(BASE_URL + LabelController.LABEL_CONTROLLER_PATH)
                .content(asJson(dto))
                .contentType(APPLICATION_JSON);

        return perform(request, byUser);
    }

    public ResultActions regDefaultStatus(final String byUser) throws Exception {
        return regStatus(testRegTaskStatusDto, byUser);
    }

    public ResultActions regStatus(final TaskStatusDto dto, final String byUser) throws Exception {
        final var request = MockMvcRequestBuilders.post(BASE_URL + TaskStatusController.STATUS_CONTROLLER_PATH)
                .content(asJson(dto))
                .contentType(APPLICATION_JSON);

        return perform(request, byUser);
    }

    public ResultActions regDefaultTask(final String byUser) throws Exception {
        regDefaultUser();
        regDefaultLabel(TEST_USERNAME);
        regDefaultStatus(TEST_USERNAME);
        final User user = userRepository.findAll().get(0);
        final TaskStatus taskStatus = taskStatusRepository.findAll().get(0);
        final Label label = labelRepository.findAll().get(0);
        final TaskDto testRegTaskDto = new TaskDto(
                "Task",
                "Description",
                taskStatus.getId(),
                Set.of(label.getId()),
                user.getId()
        );
        return regTask(testRegTaskDto, byUser);
    }

    public ResultActions regTask(final TaskDto dto, final String byUser) throws Exception {
        final var request = MockMvcRequestBuilders.post(BASE_URL + TaskController.TASK_CONTROLLER_PATH)
                .content(asJson(dto))
                .contentType(APPLICATION_JSON);

        return perform(request, byUser);
    }

}

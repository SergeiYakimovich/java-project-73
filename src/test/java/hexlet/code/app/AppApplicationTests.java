package hexlet.code.app;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AppApplicationTests {
	@Autowired
	private MockMvc mockMvc;
	@Test
	void testWelcomePage() throws Exception {
		MockHttpServletResponse response = mockMvc
				.perform(get("/welcome"))
				.andReturn()
				.getResponse();

		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsString()).contains("Welcome to Spring");
	}

}

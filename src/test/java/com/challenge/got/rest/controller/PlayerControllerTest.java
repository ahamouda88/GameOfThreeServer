package com.challenge.got.rest.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.challenge.got.application.MainApplication;
import com.challenge.got.factory.PlayerFactory;
import com.challenge.got.persist.model.Player;
import com.challenge.got.rest.constants.PathConstants;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MainApplication.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
public class PlayerControllerTest {

	private final String END_POINT = PathConstants.PLAYERS_FULL_PATH;

	private MockMvc mockMvc;
	private ObjectMapper mapper;

	@Autowired
	private PlayerController playerController;

	@Before
	public void testCreate() throws Exception {
		MockitoAnnotations.initMocks(this);

		mapper = new ObjectMapper();
		mockMvc = MockMvcBuilders	.standaloneSetup(playerController)
									.build();

		testCreatePlayer("Ahmed");
		testCreatePlayer("Gerard");
		testCreatePlayer("Shakira");
	}

	@Test
	public void testRemoveAndGet() throws Exception {
		/** Test get method with valid Id **/
		mockMvc	.perform(get(END_POINT + "/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status.message", is("Success")))
				.andExpect(jsonPath("$.data.name", is("Ahmed")));

		/** Test get method with invalid Id **/
		mockMvc	.perform(get(END_POINT + "/100"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status.message", is("Failed")))
				.andExpect(jsonPath("$.status.errors[0]", is("Player with the following Id 100 doesn't exist")));

		/** Test delete method with valid Id **/
		mockMvc	.perform(delete(END_POINT + "/1"))
				.andExpect(status().isOk());

		/** Test delete method with invalid Id **/
		mockMvc	.perform(delete(END_POINT + "/10"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status.message", is("Failed")))
				.andExpect(jsonPath("$.status.errors[0]",
						is("Exception while deleting a player: Model with the following id 10 doesn't exist")));
	}

	@After
	public void testGetAll() throws Exception {
		mockMvc	.perform(get(END_POINT))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status.message", is("Success")))
				.andExpect(jsonPath("$.data", hasSize(2)));
	}

	private void testCreatePlayer(String name) throws Exception {
		Player player = PlayerFactory.createPlayer(name);
		mockMvc	.perform(post(END_POINT, player).contentType(MediaType.APPLICATION_JSON)
												.content(mapper.writeValueAsString(player)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.status.message", is("Success")))
				.andExpect(jsonPath("$.data.name", is(name)))
				.andExpect(jsonPath("$.data.id", notNullValue()));
	}
}

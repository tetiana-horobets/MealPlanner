package hello;


import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.springtestdbunit.assertion.DatabaseAssertionMode.NON_STRICT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class, TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class})
public class IngredientTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DatabaseSetup("ingredient-setup-findById.xml")
    public void findsIngredientById() throws Exception {
        mockMvc.perform(get("/ingredient/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\n" +
                        "  \"id\": 1,\n" +
                        "  \"name\": \"Onion\",\n" +
                        "  \"quantity\": \"1\",\n" +
                        "  \"unit\": \"piece\"\n" +
                        "}"));
    }

    @Test
    @DatabaseSetup("ingredient-setup-findAll.xml")
    public void findsAllIngredients() throws Exception {

        mockMvc.perform(get("/ingredient"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[\n" +
                        "  {\n" +
                        "    \"id\": 1,\n" +
                        "    \"name\": \"Onion\",\n" +
                        "    \"quantity\": \"1\",\n" +
                        "    \"unit\": \"piece\"\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"id\": 2,\n" +
                        "    \"name\": \"Milk\",\n" +
                        "    \"quantity\": \"300\",\n" +
                        "    \"unit\": \"ml\"\n" +
                        "  }\n" +
                        "]"));
    }

    @Test
    @DatabaseSetup("ingredient-setup-delete.xml")
    @ExpectedDatabase(assertionMode = NON_STRICT, value = "ingredient-expected-delete.xml")
    public void deletesIngredient() throws Exception {

        mockMvc.perform(delete("/ingredient/1"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DatabaseSetup("ingredient-setup-create.xml")
    @ExpectedDatabase(assertionMode = NON_STRICT, value = "ingredient-expected-create.xml")
    public void createsIngredient() throws Exception {
        mockMvc.perform(post("/ingredient")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"name\": \"Milk\",\n" +
                        "  \"quantity\": \"300\",\n" +
                        "  \"unit\": \"ml\"\n" +
                        "}"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DatabaseSetup("ingredient-setup-edit.xml")
    @ExpectedDatabase(assertionMode = NON_STRICT, value = "ingredient-expected-edit.xml")
    public void editsIngredients() throws Exception {

        mockMvc.perform(put("/ingredient/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"name\":\"Milk\",\n" +
                        "  \"quantity\": \"1\",\n" +
                        "  \"unit\": \"l\"\n" +
                        "}"))
                .andDo(print())
                .andExpect(status().isOk());
    }

}

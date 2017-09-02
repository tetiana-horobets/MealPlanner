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
public class RecipeTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DatabaseSetup("recipe-setup-findById.xml")
    public void findsRecipeById() throws Exception {
        mockMvc.perform(get("/recipe/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\n" +
                        "  \"id\": 1,\n" +
                        "  \"dayOfWeek\": \"Monday\",\n" +
                        "  \"content\": \"water + potato + beet\",\n" +
                        "  \"searchTag\": \"soup\"\n" +
                        "}"));
    }

    @Test
    @DatabaseSetup("recipe-setup-create.xml")
    @ExpectedDatabase(assertionMode = NON_STRICT, value = "recipe-expected-create.xml")
    public void createsRecipe() throws Exception {
        mockMvc.perform(post("/recipe")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"dayOfWeek\": \"Tuesday\",\n" +
                        "  \"content\": \"bread + butter\",\n" +
                        "  \"searchTag\": \"sandwich\"\n" +
                        "}"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DatabaseSetup("recipe-setup-edit.xml")
    @ExpectedDatabase(assertionMode = NON_STRICT, value = "recipe-expected-edit.xml")
    public void editsRecipe() throws Exception {

        mockMvc.perform(put("/recipe/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"dayOfWeek\":\"Tuesday\",\n" +
                        "  \"content\": \"bread\",\n" +
                        "  \"searchTag\": \"sandwich\"\n" +
                        "}"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DatabaseSetup("recipe-setup-delete.xml")
    @ExpectedDatabase(assertionMode = NON_STRICT, value = "recipe-expected-delete.xml")
    public void deletesRecipe() throws Exception {

        mockMvc.perform(delete("/recipe/1"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DatabaseSetup("recipe-setup-findAll.xml")
    public void findsAllRecipes() throws Exception {

        mockMvc.perform(get("/recipe"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[\n" +
                        "  {\n" +
                        "    \"id\": 9999,\n" +
                        "    \"dayOfWeek\": \"Wednesday\",\n" +
                        "    \"content\": \"bread 1\",\n" +
                        "    \"searchTag\": \"sandwich\"\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"id\": 10000,\n" +
                        "    \"dayOfWeek\": \"Wednesday\",\n" +
                        "    \"content\": \"bread 2\",\n" +
                        "    \"searchTag\": \"sandwich\"\n" +
                        "  }\n" +
                        "]"));
    }

}

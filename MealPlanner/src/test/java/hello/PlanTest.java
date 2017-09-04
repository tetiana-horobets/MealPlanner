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
public class PlanTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DatabaseSetup("plan-setup-findById.xml")
    public void findsPlanById() throws Exception {
        mockMvc.perform(get("/plan/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\n" +
                        "  \"id\": 1,\n" +
                        "  \"name\": \"Vegetarian\",\n" +
                        "  \"startDate\": \"2017-09-04\"\n" +
                        "}"));
    }

    @Test
    @DatabaseSetup("plan-setup-findAll.xml")
    public void findsAllPlans() throws Exception {
        mockMvc.perform(get("/plan"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[\n" +
                        "  {\n" +
                        "    \"id\": 1,\n" +
                        "    \"name\": \"Vegetarian\",\n" +
                        "    \"startDate\": \"2017-09-04\"\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"id\": 2,\n" +
                        "    \"name\": \"Light\",\n" +
                        "    \"startDate\": \"2017-09-15\"\n" +
                        "  }\n" +
                        "]"));
    }

    @Test
    @DatabaseSetup("plan-setup-delete.xml")
    @ExpectedDatabase(assertionMode = NON_STRICT, value = "plan-expected-delete.xml")
    public void deletesPlan() throws Exception {
        mockMvc.perform(delete("/plan/1"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DatabaseSetup("plan-setup-create.xml")
    @ExpectedDatabase(assertionMode = NON_STRICT, value = "plan-expected-create.xml")
    public void createsPlan() throws Exception {
        mockMvc.perform(post("/plan")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"name\": \"Light\",\n" +
                        "    \"startDate\": \"2017-09-15\"\n" +
                        "}"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DatabaseSetup("plan-setup-edit.xml")
    @ExpectedDatabase(assertionMode = NON_STRICT, value = "plane-expected-edit.xml")
    public void editsPlants() throws Exception {

        mockMvc.perform(put("/plan/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"name\": \"Light\",\n" +
                        "    \"startDate\": \"2018-01-10\"\n" +
                        "}"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}

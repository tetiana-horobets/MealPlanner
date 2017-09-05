package hello;

import com.google.common.base.Predicate;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class, PlanWebTest.MockTestConfiguration.class}, webEnvironment = DEFINED_PORT)
@AutoConfigureWebMvc
@ActiveProfiles("web-test")
public class PlanWebTest {
    private static WebDriver webDriver;
    private static WebDriverWait wait;
    private static PlanWebTest.PlanPage page;
    private static PlanWebTest.AddPlanForm addForm;

    @Autowired
    private PlanWebTest.MockPlanRepository mockRepository;

    @BeforeClass
    public static void setUp() {
        webDriver = new ChromeDriver();
        wait = new WebDriverWait(webDriver, 10);
        page = new PlanPage();
        addForm = new PlanWebTest.AddPlanForm();
    }

    @Before
    public void setUpRepository() {
        mockRepository.reset();
    }

    @Test
    public void showsAllPlans() throws Exception {
        mockRepository.add(new Plan(1, "Vegetarian", "2017-09-04"));
        mockRepository.add(new Plan(2, "Light", "2017-09-15"));

        page.open();

        assertThat(page.getPlanNames()).containsExactly("Vegetarian", "Light");
        assertThat(page.getPlanStartDate()).containsExactly("2017-09-04", "2017-09-15");
    }

    @Test
    public void addsPlans() throws Exception {
        mockRepository.add(new Plan(1, "Vegetarian", "2017-09-04"));

        page.open();

        addForm.writeName("Light");
        addForm.writeStartDate("2017-09-15");
        addForm.submit();

        assertThat(page.getPlanNames()).containsExactly("Vegetarian", "Light");
        assertThat(page.getPlanStartDate()).containsExactly("2017-09-04", "2017-09-15");

    }

    private static Predicate<WebDriver> requestIsComplete() {
        return (webDriver) -> webDriver != null && webDriver.findElement(By.id("async-load")).getAttribute("value").equals("false");
    }

    @AfterClass
    public static void tearDown() {
        webDriver.close();
    }

    @Configuration
    static class MockTestConfiguration {

        @Bean
        @Primary
        @Profile("web-test")
        public PlanWebTest.MockPlanRepository repository() {
            return new PlanWebTest.MockPlanRepository();
        }
    }

    static class PlanPage {

        void open() {
            webDriver.get("localhost:8080/plan.html");
            wait.until(requestIsComplete());
        }

        List<String> getPlanNames() {
            return getClassValues("plan-name");
        }

        List<String> getPlanStartDate() {
            return getClassValues("plan-startDate");
        }

        private List<String> getClassValues(String className) {
            List<WebElement> elements = webDriver.findElements(By.className(className));
            return elements.stream().map(WebElement::getText).collect(Collectors.toList());
        }
    }

    static class AddPlanForm {

        void writeName(String name) {
            writeToInput("add-input-plan-name", name);
        }

        void writeStartDate(String startDate) {
            writeToInput("add-input-plan-startDate", startDate);
        }

        void submit() {
            webDriver.findElement(By.id("add-button-plan")).click();
            wait.until(requestIsComplete());
        }

        private void writeToInput(String id, String value) {
            webDriver.findElement(By.id(id)).sendKeys(value);
        }
    }

    static class MockPlanRepository implements PlanRepository {

        private Map<Long, Plan> plans = new HashMap<>();
        private long lastId = 0;


        @Override
        public Plan findById(long id) {
            return null;
        }

        @Override
        public Collection<Plan> findAll() { return plans.values();
        }

        @Override
        public void delete(long id) {

        }

        @Override
        public void add(Plan plan) {
            lastId++;
            plans.put(lastId, plan);
        }

        @Override
        public void edit(long id, Plan plan) {

        }

        void reset() {
            plans.clear();
            lastId = 0;
        }
    }
}



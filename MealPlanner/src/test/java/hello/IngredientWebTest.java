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
@SpringBootTest(classes = {Application.class, IngredientWebTest.MockTestConfiguration.class}, webEnvironment = DEFINED_PORT)
@AutoConfigureWebMvc
@ActiveProfiles("web-test")
public class IngredientWebTest {

    private static WebDriver webDriver;
    private static WebDriverWait wait;
    private static IngredientPage page;
    private static AddIngredientForm addForm;
    private static EditIngredientForm editForm;

    @Autowired
    private MockIngredientRepository mockRepository;

    @BeforeClass
    public static void setUp() {
        webDriver = new ChromeDriver();
        wait = new WebDriverWait(webDriver, 10);

        page = new IngredientPage();
        addForm = new AddIngredientForm();
        editForm = new EditIngredientForm();
    }

    @Before
    public void setUpRepository() {
        mockRepository.reset();
    }

    @Test
    public void showsAllIngredients() throws Exception {
        mockRepository.add(new Ingredient(1, "Garlic", "1", "piece"));
        mockRepository.add(new Ingredient(2, "Milk", "300", "ml"));

        page.open();

        assertThat(page.getIngredientNames()).containsExactly("Garlic", "Milk");
        assertThat(page.getIngredientQuantities()).containsExactly("1", "300");
        assertThat(page.getIngredientUnits()).containsExactly("piece", "ml");
    }

    @Test
    public void addsIngredient() throws Exception {
        mockRepository.add(new Ingredient(1, "Garlic", "1", "piece"));

        page.open();

        addForm.writeName("Sausage");
        addForm.writeQuantity("1");
        addForm.writeUnit("kg");
        addForm.submit();

        assertThat(page.getIngredientNames()).containsExactly("Garlic", "Sausage");
        assertThat(page.getIngredientQuantities()).containsExactly("1", "1");
        assertThat(page.getIngredientUnits()).containsExactly("piece", "kg");
    }

    @Test
    public void deletesIngredient() throws Exception {
        mockRepository.add(new Ingredient(1, "Garlic", "1", "piece"));
        mockRepository.add(new Ingredient(2, "Milk", "300", "ml"));

        page.open();
        page.clickOnRemoveButton(2);

        assertThat(page.getIngredientNames()).containsExactly("Garlic");
        assertThat(page.getIngredientQuantities()).containsExactly("1");
        assertThat(page.getIngredientUnits()).containsExactly("piece");
    }

    @Test
    public void editIngredient() throws Exception {
        mockRepository.add(new Ingredient(1, "Garlic", "1", "piece"));
        mockRepository.add(new Ingredient(2, "Milk", "300", "ml"));

        page.open();
        page.clickOnEditButton(2);

        assertThat(editForm.getName()).isEqualTo("Milk");
        assertThat(editForm.getQuantity()).isEqualTo("300");
        assertThat(editForm.getUnit()).isEqualTo("ml");

        editForm.replaceName("Water");
        editForm.replaceQuantity("2");
        editForm.replaceUnit("l");
        editForm.submit();

        assertThat(page.getIngredientNames()).containsExactly("Garlic", "Water");
        assertThat(page.getIngredientQuantities()).containsExactly("1", "2");
        assertThat(page.getIngredientUnits()).containsExactly("piece", "l");
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
        public MockIngredientRepository repository() {
            return new MockIngredientRepository();
        }
    }

    static class IngredientPage {

        void open() {
            webDriver.get("localhost:8080/ingredient.html");
            wait.until(requestIsComplete());
        }

        List<String> getIngredientNames() {
            return getClassValues("ingredient-name");
        }

        List<String> getIngredientQuantities() {
            return getClassValues("ingredient-quantity");
        }

        List<String> getIngredientUnits() {
            return getClassValues("ingredient-unit");
        }

        void clickOnRemoveButton(int id) {
            webDriver.findElement(By.id("delete-button-ingredient-" + id)).click();
            wait.until(requestIsComplete());
        }

        void clickOnEditButton(int id) {
            webDriver.findElement(By.id("edit-button-ingredient-" + id)).click();
            wait.until(requestIsComplete());
        }

        private List<String> getClassValues(String className) {
            List<WebElement> elements = webDriver.findElements(By.className(className));
            return elements.stream().map(WebElement::getText).collect(Collectors.toList());
        }
    }

    static class AddIngredientForm {

        void writeName(String name) {
            writeToInput("add-input-ingredient-name", name);
        }

        void writeQuantity(String quantity) {
            writeToInput("add-input-ingredient-quantity", quantity);
        }

        void writeUnit(String unit) {
            writeToInput("add-input-ingredient-unit", unit);
        }

        void submit() {
            webDriver.findElement(By.id("add-button-ingredient")).click();
            wait.until(requestIsComplete());
        }

        private void writeToInput(String id, String value) {
            webDriver.findElement(By.id(id)).sendKeys(value);
        }
    }

    static class EditIngredientForm {

        String getName() {
            return getInputValue("edit-input-ingredient-name");
        }

        String getQuantity() {
            return getInputValue("edit-input-ingredient-quantity");
        }

        String getUnit() {
            return getInputValue("edit-input-ingredient-unit");
        }

        void replaceName(String name) {
            replaceInputText("edit-input-ingredient-name", name);
        }

        void replaceQuantity(String quantity) {
            replaceInputText("edit-input-ingredient-quantity", quantity);
        }

        void replaceUnit(String unit) {
            replaceInputText("edit-input-ingredient-unit", unit);
        }

        void submit() {
            webDriver.findElement(By.id("save-button-ingredient")).click();
            wait.until(requestIsComplete());
        }

        private String getInputValue(String id) {
            return webDriver.findElement(By.id(id)).getAttribute("value");
        }

        private void replaceInputText(String id, String value) {
            WebElement element = webDriver.findElement(By.id(id));
            element.clear();
            element.sendKeys(value);
        }
    }

    static class MockIngredientRepository implements IngredientRepository {

        private Map<Long, Ingredient> ingredients = new HashMap<>();
        private long lastId = 0;

        @Override
        public Ingredient findById(long id) {
            return ingredients.get(id);
        }

        @Override
        public Collection<Ingredient> findAll() {
            return ingredients.values();
        }

        @Override
        public void delete(long id) {
            ingredients.remove(id);
        }

        @Override
        public void add(Ingredient ingredient) {
            lastId++;
            ingredients.put(lastId, ingredient);
        }

        @Override
        public void edit(long id, Ingredient ingredient) {
            ingredients.put(id, ingredient);
        }

        void reset() {
            ingredients.clear();
            lastId = 0;
        }
    }
}

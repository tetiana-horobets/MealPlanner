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

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class, IngredientWebTest.MockTestConfiguration.class}, webEnvironment = DEFINED_PORT)
@AutoConfigureWebMvc
@ActiveProfiles("web-test")
public class IngredientWebTest {

    private static WebDriver webDriver;
    private static WebDriverWait wait;

    @Autowired
    private MockIngredientRepository mockRepository;

    @BeforeClass
    public static void setUp() {
        webDriver = new ChromeDriver();
        wait = new WebDriverWait(webDriver, 10);
    }

    @Before
    public void setUpRepository() {
        mockRepository.reset();
    }

    @Test
    public void showsAllIngredients() throws Exception {
        mockRepository.add(new Ingredient(1, "Garlic", "1", "piece"));
        mockRepository.add(new Ingredient(2, "Milk", "300", "ml"));

        webDriver.get("localhost:8080/ingredient.html");
        wait.until(requestIsComplete());

        List<WebElement> names = webDriver.findElements(By.className("ingredient-name"));
        List<WebElement> quantities = webDriver.findElements(By.className("ingredient-quantity"));
        List<WebElement> units = webDriver.findElements(By.className("ingredient-unit"));

        assertThat(names).extracting(WebElement::getText).containsExactly("Garlic", "Milk");
        assertThat(quantities).extracting(WebElement::getText).containsExactly("1", "300");
        assertThat(units).extracting(WebElement::getText).containsExactly("piece", "ml");
    }

    @Test
    public void addsIngredient() throws Exception {
        mockRepository.add(new Ingredient(1, "Garlic", "1", "piece"));

        webDriver.get("localhost:8080/ingredient.html");
        webDriver.findElement(By.id("add-input-ingredient-name")).sendKeys("Sausage");
        webDriver.findElement(By.id("add-input-ingredient-quantity")).sendKeys("1");
        webDriver.findElement(By.id("add-input-ingredient-unit")).sendKeys("kg");
        webDriver.findElement(By.id("add-button-ingredient")).click();
        wait.until(requestIsComplete());

        List<WebElement> names = webDriver.findElements(By.className("ingredient-name"));
        List<WebElement> quantities = webDriver.findElements(By.className("ingredient-quantity"));
        List<WebElement> units = webDriver.findElements(By.className("ingredient-unit"));

        assertThat(names).extracting(WebElement::getText).containsExactly("Garlic", "Sausage");
        assertThat(quantities).extracting(WebElement::getText).containsExactly("1", "1");
        assertThat(units).extracting(WebElement::getText).containsExactly("piece", "kg");
    }

    @Test
    public void deletesIngredient() throws Exception {
        mockRepository.add(new Ingredient(1, "Garlic", "1", "piece"));
        mockRepository.add(new Ingredient(2, "Milk", "300", "ml"));

        webDriver.get("localhost:8080/ingredient.html");
        wait.until(requestIsComplete());

        webDriver.findElement(By.id("delete-button-ingredient-2")).click();
        wait.until(requestIsComplete());

        List<WebElement> names = webDriver.findElements(By.className("ingredient-name"));
        List<WebElement> quantities = webDriver.findElements(By.className("ingredient-quantity"));
        List<WebElement> units = webDriver.findElements(By.className("ingredient-unit"));

        assertThat(names).extracting(WebElement::getText).containsExactly("Garlic");
        assertThat(quantities).extracting(WebElement::getText).containsExactly("1");
        assertThat(units).extracting(WebElement::getText).containsExactly("piece");
    }

    @Test
    public void editIngredient() throws Exception {
        mockRepository.add(new Ingredient(1, "Garlic", "1", "piece"));
        mockRepository.add(new Ingredient(2, "Milk", "300", "ml"));

        webDriver.get("localhost:8080/ingredient.html");
        wait.until(requestIsComplete());

        webDriver.findElement(By.id("edit-button-ingredient-2")).click();
        wait.until(requestIsComplete());

        WebElement inputName = webDriver.findElement(By.id("edit-input-ingredient-name"));
        WebElement inputQuantity = webDriver.findElement(By.id("edit-input-ingredient-quantity"));
        WebElement inputUnit = webDriver.findElement(By.id("edit-input-ingredient-unit"));

        assertThat(inputName.getAttribute("value")).isEqualTo("Milk");
        assertThat(inputQuantity.getAttribute("value")).isEqualTo("300");
        assertThat(inputUnit.getAttribute("value")).isEqualTo("ml");

        inputName.clear();
        inputQuantity.clear();
        inputUnit.clear();

        inputName.sendKeys("Water");
        inputQuantity.sendKeys("2");
        inputUnit.sendKeys("l");

        webDriver.findElement(By.id("save-button-ingredient")).click();
        wait.until(requestIsComplete());

        List<WebElement> names = webDriver.findElements(By.className("ingredient-name"));
        List<WebElement> quantities = webDriver.findElements(By.className("ingredient-quantity"));
        List<WebElement> units = webDriver.findElements(By.className("ingredient-unit"));

        assertThat(names).extracting(WebElement::getText).containsExactly("Garlic", "Water");
        assertThat(quantities).extracting(WebElement::getText).containsExactly("1", "2");
        assertThat(units).extracting(WebElement::getText).containsExactly("piece", "l");
    }

    private Predicate<WebDriver> requestIsComplete() {
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

package hello;


import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
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

    @Autowired
    private MockIngredientRepository mockRepository;

    @BeforeClass
    public static void setUp() {
        webDriver = new ChromeDriver();
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

        Thread.sleep(1000);

        List<WebElement> names = webDriver.findElements(By.className("ingredient-name"));
        assertThat(names).extracting(WebElement::getText).containsExactly("Garlic", "Milk");

        List<WebElement> quantities = webDriver.findElements(By.className("ingredient-quantity"));
        assertThat(quantities).extracting(WebElement::getText).containsExactly("1", "300");

        List<WebElement> units = webDriver.findElements(By.className("ingredient-unit"));
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

        Thread.sleep(1000);

        Ingredient savedIngredient = mockRepository.getLastSavedIngredient();

        assertThat(savedIngredient.getName()).isEqualTo("Sausage");
        assertThat(savedIngredient.getQuantity()).isEqualTo("1");
        assertThat(savedIngredient.getUnit()).isEqualTo("kg");

        List<WebElement> names = webDriver.findElements(By.className("ingredient-name"));
        assertThat(names).extracting(WebElement::getText).containsExactly("Garlic", "Sausage");

        List<WebElement> quantities = webDriver.findElements(By.className("ingredient-quantity"));
        assertThat(quantities).extracting(WebElement::getText).containsExactly("1", "1");

        List<WebElement> units = webDriver.findElements(By.className("ingredient-unit"));
        assertThat(units).extracting(WebElement::getText).containsExactly("piece", "kg");
    }

    @Test
    public void deletesIngredient() throws Exception {
        Ingredient garlic = new Ingredient(1, "Garlic", "1", "piece");
        Ingredient milk = new Ingredient(2, "Milk", "300", "ml");

        mockRepository.add(garlic);
        mockRepository.add(milk);

        webDriver.get("localhost:8080/ingredient.html");

        Thread.sleep(1000);

        webDriver.findElement(By.id("delete-button-ingredient-2")).click();

        Thread.sleep(1000);

        assertThat(mockRepository.findAll()).containsOnly(garlic);

        List<WebElement> names = webDriver.findElements(By.className("ingredient-name"));
        assertThat(names).extracting(WebElement::getText).containsExactly("Garlic");

        List<WebElement> quantities = webDriver.findElements(By.className("ingredient-quantity"));
        assertThat(quantities).extracting(WebElement::getText).containsExactly("1");

        List<WebElement> units = webDriver.findElements(By.className("ingredient-unit"));
        assertThat(units).extracting(WebElement::getText).containsExactly("piece");
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

        Ingredient getLastSavedIngredient() {
            return findById(lastId);
        }

        void reset() {
            ingredients.clear();
            lastId = 0;
        }
    }
}

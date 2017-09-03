package hello;


import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = DEFINED_PORT)
@AutoConfigureWebMvc
public class IngredientWebTest {

    @MockBean
    private IngredientRepository repository;

    private static WebDriver webDriver;

    @BeforeClass
    public static void setUp() {
        webDriver = new ChromeDriver();
    }

    @Test
    public void showsAllIngredients() throws Exception {
        Collection<Ingredient> allIngredients = asList(
                new Ingredient(1, "Garlic", "1", "piece"),
                new Ingredient(2, "Milk", "300", "ml")
        );

        doReturn(allIngredients).when(repository).findAll();

        webDriver.get("localhost:8080/ingredient.html");

        Thread.sleep(1000);

        String welcomeText = webDriver.findElement(By.tagName("h1")).getText();
        assertThat(welcomeText).isEqualTo("Ingredients");

        List<WebElement> names = webDriver.findElements(By.className("ingredient-name"));
        assertThat(names).extracting(WebElement::getText).containsExactly("Garlic", "Milk");

        List<WebElement> quantities = webDriver.findElements(By.className("ingredient-quantity"));
        assertThat(quantities).extracting(WebElement::getText).containsExactly("1", "300");

        List<WebElement> units = webDriver.findElements(By.className("ingredient-unit"));
        assertThat(units).extracting(WebElement::getText).containsExactly("piece", "ml");
    }

    @Test
    public void addsIngredient() throws Exception {
        Collection<Ingredient> allIngredients = new ArrayList<>();
        allIngredients.add(new Ingredient(1, "Garlic", "1", "piece"));

        doReturn(allIngredients).when(repository).findAll();
        doAnswer(invocation -> {
            Ingredient ingredient = (Ingredient) invocation.getArguments()[0];
            allIngredients.add(ingredient);
            return null;
        }).when(repository).add(any(Ingredient.class));

        webDriver.get("localhost:8080/ingredient.html");

        webDriver.findElement(By.id("add-input-ingredient-name")).sendKeys("Sausage");
        webDriver.findElement(By.id("add-input-ingredient-quantity")).sendKeys("1");
        webDriver.findElement(By.id("add-input-ingredient-unit")).sendKeys("kg");

        webDriver.findElement(By.id("add-button-ingredient")).click();

        Thread.sleep(1000);

        ArgumentCaptor<Ingredient> captor = ArgumentCaptor.forClass(Ingredient.class);
        verify(repository).add(captor.capture());
        Ingredient savedIngredient = captor.getValue();

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

    @AfterClass
    public static void tearDown() {
        webDriver.close();
    }
}

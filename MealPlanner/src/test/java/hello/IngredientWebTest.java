package hello;


import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = DEFINED_PORT)
@AutoConfigureWebMvc
public class IngredientWebTest {

    @MockBean
    private IngredientRepository repository;

    private WebDriver webDriver = new ChromeDriver();

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

    @After
    public void tearDown() {
        webDriver.close();
    }
}

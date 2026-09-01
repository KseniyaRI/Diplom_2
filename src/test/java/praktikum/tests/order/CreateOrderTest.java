package praktikum.tests.order;

import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import praktikum.BaseTest;
import praktikum.data.TestUserData;
import praktikum.models.Order;
import praktikum.models.User;
import praktikum.steps.OrderSteps;
import praktikum.steps.UserSteps;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

public class CreateOrderTest extends BaseTest {

    private final UserSteps userSteps = new UserSteps();
    private final OrderSteps orderSteps = new OrderSteps();
    private String accessToken;

    @BeforeEach
    public void prepareData() {
        accessToken = null;
        User user = TestUserData.randomUser();
        accessToken = userSteps.createUser(user).path("accessToken");
    }

    @AfterEach
    public void cleanUp() {
        if (accessToken != null) {
            userSteps.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Можно создать заказ с авторизацией и ингредиентами")
    public void shouldCreateOrderWithAuthAndIngredients() {
        List<String> ids = orderSteps.getIngredientIds();
        Order order = new Order(List.of(ids.get(0), ids.get(1)));

        Response response = orderSteps.createOrder(accessToken, order);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", greaterThan(0));
    }

    @Test
    @DisplayName("Создание заказа без авторизации, с ингредиентами")
    public void shouldCreateOrderWithoutAuthAndWithIngredients() {
        List<String> ids = orderSteps.getIngredientIds();
        Order order = new Order(List.of(ids.get(0), ids.get(1)));

        Response response = orderSteps.createOrder(null, order);

        // фактический код сверить со стендом; ниже — типичный 200
        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", greaterThan(0));
    }

    @Test
    @DisplayName("Нельзя создать заказ без ингредиентов")
    public void shouldReturnErrorWhenNoIngredients() {
        Order order = new Order(List.of());

        Response response = orderSteps.createOrder(accessToken, order);

        response.then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Неверный хеш ингредиента возвращает 500")
    public void shouldReturnErrorWhenIngredientHashIsInvalid() {
        Order order = new Order(List.of("invalid-hash"));

        Response response = orderSteps.createOrder(accessToken, order);

        // По PDF ожидаем 500. Стенд отвечает 400 — тест красный в Allure, см. README
        response.then().statusCode(500);
    }
}
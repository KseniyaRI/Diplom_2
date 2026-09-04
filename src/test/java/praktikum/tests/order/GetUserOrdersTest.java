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
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

public class GetUserOrdersTest extends BaseTest {

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
    @DisplayName("Авторизованный пользователь получает свои заказы")
    public void shouldGetOrdersWhenAuthorized() {
        List<String> ids = orderSteps.getIngredientIds();
        orderSteps.createOrder(accessToken, new Order(List.of(ids.get(0), ids.get(1))));

        Response response = orderSteps.getUserOrders(accessToken);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("orders.size()", greaterThanOrEqualTo(1));
    }

    @Test
    @DisplayName("Без авторизации нельзя получить заказы пользователя")
    public void shouldReturnErrorWhenUnauthorized() {
        Response response = orderSteps.getUserOrders(null);

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}
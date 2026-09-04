package praktikum.steps;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.util.List;
import io.restassured.specification.RequestSpecification;

import praktikum.config.BurgersConfig;
import praktikum.models.Order;

public class OrderSteps {

    @Step("Создать заказ")
    public Response createOrder(String accessToken, Order order) {
        RequestSpecification request = RestAssured.given()
                .header("Content-type", "application/json")
                .body(order);
        if (accessToken != null) {
            request.header("Authorization", accessToken);
        }
        return request.when().post(BurgersConfig.ORDERS);
    }    

    @Step("Получить заказы пользователя")
    public Response getUserOrders(String accessToken) {
        RequestSpecification request = RestAssured.given();
        if (accessToken != null) {
            request.header("Authorization", accessToken);
        }
        return request.when().get(BurgersConfig.ORDERS);
    }

    @Step("Получить id ингредиентов")
    public List<String> getIngredientIds() {
        return RestAssured.given()
                .when()
                .get(BurgersConfig.INGREDIENTS)
                .jsonPath()
                .getList("data._id");
    }
}

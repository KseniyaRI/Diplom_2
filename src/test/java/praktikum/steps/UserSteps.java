package praktikum.steps;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import praktikum.config.BurgersConfig;
import praktikum.models.User;

public class UserSteps {

    @Step("Создать пользователя")
    public Response createUser(User user) {
        return RestAssured.given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post(BurgersConfig.AUTH_REGISTER);
    }

    @Step("Залогинить пользователя")
    public Response login(User user) {
        return RestAssured.given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post(BurgersConfig.AUTH_LOGIN);
    }

    @Step("Изменить данные пользователя с авторизацией")
    public Response updateUser(String accessToken, User user) {
        return RestAssured.given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .body(user)
                .when()
                .patch(BurgersConfig.AUTH_USER);
    }

    @Step("Изменить данные пользователя без авторизации")
    public Response updateUserWithoutAuth(User user) {
        return RestAssured.given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .patch(BurgersConfig.AUTH_USER);
    }

    @Step("Удалить пользователя")
    public Response deleteUser(String accessToken) {
        return RestAssured.given()
                .header("Authorization", accessToken)
                .when()
                .delete(BurgersConfig.AUTH_USER);
    }
}

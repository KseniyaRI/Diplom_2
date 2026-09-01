package praktikum.tests.user;

import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import praktikum.BaseTest;
import praktikum.data.TestUserData;
import praktikum.models.User;
import praktikum.steps.UserSteps;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CreateUserTest extends BaseTest {

    private final UserSteps userSteps = new UserSteps();
    private String accessToken;
    private User user;

    static Stream<User> missingFields() {
        User full = TestUserData.randomUser();
        return Stream.of(
                // email = null: Jackson не пишет ключ в JSON — поле «не заполнили», не "email": null
                new User(null, full.getPassword(), full.getName()),
                new User(full.getEmail(), null, full.getName()),
                new User(full.getEmail(), full.getPassword(), null)
        );
    }

    @BeforeEach
    public void prepareData() {
        accessToken = null;
        user = TestUserData.randomUser();
    }

    @AfterEach
    public void cleanUp() {
        if (accessToken != null) {
            userSteps.deleteUser(accessToken);
            accessToken = null;
        }
    }

    @Test
    @DisplayName("Можно создать уникального пользователя")
    public void shouldCreateUniqueUser() {
        Response response = userSteps.createUser(user);
        accessToken = response.path("accessToken");

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail().toLowerCase()))
                .body("user.name", equalTo(user.getName()))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }

    @Test
    @DisplayName("Нельзя создать пользователя, который уже зарегистрирован")
    public void shouldNotCreateUserWhoAlreadyExists() {
        Response firstResponse = userSteps.createUser(user);
        accessToken = firstResponse.path("accessToken");

        Response secondResponse = userSteps.createUser(user);

        secondResponse.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @ParameterizedTest
    @MethodSource("missingFields")
    @DisplayName("Нельзя создать пользователя без обязательного поля")
    public void shouldNotCreateUserWhenRequiredFieldIsMissing(User invalidUser) {
        Response response = userSteps.createUser(invalidUser);

        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}

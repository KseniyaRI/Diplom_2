package praktikum.tests.user;

import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import praktikum.BaseTest;
import praktikum.data.TestUserData;
import praktikum.models.User;
import praktikum.steps.UserSteps;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class LoginUserTest extends BaseTest {

    private final UserSteps userSteps = new UserSteps();
    private String accessToken;
    private User user;

    @BeforeEach
    public void prepareData() {
        accessToken = null;
        user = TestUserData.randomUser();
        Response createResponse = userSteps.createUser(user);
        accessToken = createResponse.path("accessToken");
    }

    @AfterEach
    public void cleanUp() {
        if (accessToken != null) {
            userSteps.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Можно залогиниться под существующим пользователем")
    public void shouldLoginExistingUser() {
        Response response = userSteps.login(user);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue())
                .body("user.email", equalTo(user.getEmail().toLowerCase()))
                .body("user.name", equalTo(user.getName()));
    }

    @Test
    @DisplayName("Нельзя залогиниться с неверным паролем")
    public void shouldNotLoginWithWrongPassword() {
        User wrongPassword = new User(user.getEmail(), "wrong" + user.getPassword(), user.getName());
        Response response = userSteps.login(wrongPassword);

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Нельзя залогиниться с неверным логином")
    public void shouldNotLoginWithWrongLogin() {
        User wrongLogin = new User(TestUserData.randomUser().getEmail(), user.getPassword(), user.getName());
        Response response = userSteps.login(wrongLogin);

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}
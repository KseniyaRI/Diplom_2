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

public class UpdateUserTest extends BaseTest {

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
    @DisplayName("Авторизованный пользователь может изменить email")
    public void shouldUpdateEmailWhenAuthorized() {
        User patch = new User(TestUserData.randomUser().getEmail(), null, null);
        Response response = userSteps.updateUser(accessToken, patch);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(patch.getEmail().toLowerCase()));
    }

    @Test
    @DisplayName("Авторизованный пользователь может изменить пароль")
    public void shouldUpdatePasswordWhenAuthorized() {
        User patch = new User(null, TestUserData.randomUser().getPassword(), null);
        Response response = userSteps.updateUser(accessToken, patch);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true));

        user.setPassword(patch.getPassword());
        userSteps.login(user).then().statusCode(200);
    }

    @Test
    @DisplayName("Авторизованный пользователь может изменить имя")
    public void shouldUpdateNameWhenAuthorized() {
        User patch = new User(null, null, TestUserData.randomUser().getName());
        Response response = userSteps.updateUser(accessToken, patch);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.name", equalTo(patch.getName()));
    }

    @Test
    @DisplayName("Без авторизации нельзя изменить email")
    public void shouldReturnErrorWhenUpdateEmailWithoutAuthorization() {
        User patch = new User(TestUserData.randomUser().getEmail(), null, null);
        Response response = userSteps.updateUserWithoutAuth(patch);

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Без авторизации нельзя изменить пароль")
    public void shouldReturnErrorWhenUpdatePasswordWithoutAuthorization() {
        User patch = new User(null, TestUserData.randomUser().getPassword(), null);
        Response response = userSteps.updateUserWithoutAuth(patch);

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Без авторизации нельзя изменить имя")
    public void shouldReturnErrorWhenUpdateNameWithoutAuthorization() {
        User patch = new User(null, null, TestUserData.randomUser().getName());
        Response response = userSteps.updateUserWithoutAuth(patch);

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}

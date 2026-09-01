package praktikum;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import praktikum.config.BurgersConfig;

public class BaseTest {

    @BeforeEach
    public void setUp() {
        // reset сбрасывает старые фильтры, иначе AllureRestAssured накапливается и в отчёте появляется много одинаковых Request/Response
        RestAssured.reset();
        RestAssured.baseURI = BurgersConfig.BASE_URI;
        RestAssured.filters(new AllureRestAssured());
    }
}

package praktikum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertTrue;

public class UserLoginStellarBurgersTest {

    private UserStellarBurgers user;
    private UserClientStellarBurgers userClient;

    private String token;

    @Before
    public void setUp() {
        user = UserStellarBurgers.getRandomUser();
        userClient = new UserClientStellarBurgers();
        userClient.createUser(user);
    }

    @After
    public void setDown() {
        if (token != null) userClient.deleteUser(token);
    }

    @Test
    @DisplayName("Checking the successful authorization of the user")
    public void checkingUserLoginWithValidDataTest() {
        ValidatableResponse response = userClient.loginUser(UserCredentialsStellarBurgers.from(user));
        int statusCode = response.extract().statusCode();
        token = response.extract().path("accessToken");
        String refreshToken = response.extract().path("refreshToken");

        assertThat("Некорректный код статуса", statusCode, equalTo(200));
        assertThat("Отсутствует refreshToken в теле ответа", refreshToken, notNullValue());
        assertTrue("Некорректный accessToken в теле ответа", token.startsWith("Bearer"));
    }

    @Test
    @DisplayName("Checking the invalid authorization of the user")
    public void checkLoginWithInvalidCredentialsTest() {
        ValidatableResponse response = userClient.loginUser(UserCredentialsStellarBurgers.getUserPermissionWithNotValidCredentials());
        int statusCode = response.extract().statusCode();
        boolean isNotValidUserNotCreated = response.extract().path("message").equals("email or password are incorrect");
        token = response.extract().path("accessToken");
        assertThat("Некорректный код статуса", statusCode, equalTo(401));
        assertThat("Удалось авторизоваться пользователем с некорректными данными", isNotValidUserNotCreated);
    }

}

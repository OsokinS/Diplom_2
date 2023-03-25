package praktikum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class UserChangeCredentialsStellarBurgersTest {

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
    @DisplayName("Checking user can change credentials after authorization")
    public void checkingTheAbilityToChangeUserCredentialsAfterAuthorizationTest() {

        token = userClient.loginUser(UserCredentialsStellarBurgers.from(user)).extract().path("accessToken");
        token = StringUtils.remove(token, "Bearer ");
        ValidatableResponse response = userClient.changeUser(UserCredentialsChangeStellarBurgers.changeUserCredentials(), token);
        int statusCode = response.extract().statusCode();
        boolean isChangesSuccess = response.extract().path("success");

        assertThat("Некорректный код статуса", statusCode, equalTo(200));
        assertThat("Удалось авторизоваться пользователем с некорректными данными", isChangesSuccess);
    }

    @Test
    @DisplayName("Checking user can't change credentials without authorization")
    public void checkingThatTheUserCannotChangeCredentialsWithoutAuthorizationTest() {
        ValidatableResponse response = userClient.changeUser(UserCredentialsChangeStellarBurgers.changeUserCredentials(), "");
        token = response.extract().path("accessToken");
        int statusCode = response.extract().statusCode();
        boolean isNotSuccessChanges = response.extract().path("message").equals("You should be authorised");

        assertThat("Некорректный код статуса", statusCode, equalTo(401));
        assertThat("Успешный запрос на изменение данных без авторизации", isNotSuccessChanges);
    }

}

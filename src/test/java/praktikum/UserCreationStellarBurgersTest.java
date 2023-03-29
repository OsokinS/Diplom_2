package praktikum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertTrue;

public class UserCreationStellarBurgersTest extends RestAssuredClientStellarBurgers {

    private UserClientStellarBurgers userClient;
    private UserStellarBurgers user;
    private List<String> token = new ArrayList<>();

    @Before
    public void setUp() {
        user = UserStellarBurgers.getRandomUser();
        userClient = new UserClientStellarBurgers();
    }

    @After
    public void setDown() {
        if (token != null) {
            for (String s : token) {
                if(s != null) {
                    userClient.deleteUser(s);
                }
            }
        }
    }


    @Test
    @DisplayName("Check successful user creation")
    public void checkingVerificationSuccessfulUserCreationTest() {
        ValidatableResponse response = userClient.createUser(user);
        int statusCode = response.extract().statusCode();
        boolean isUserCreated = response.extract().path("success");
        token.add(response.extract().path("accessToken"));

        assertTrue("Пользователь не создан", isUserCreated);
        assertThat("Некорректный код статуса", statusCode, equalTo(200));
        assertTrue("Некорректный accessToken в теле ответа", token.get(0).startsWith("Bearer"));
    }

    @Test
    @DisplayName("Check it's impossible to create two identical users")
    public void checkingImpossilityOfCreatingIdenticalUsersTest() {
        token.add(userClient.createUser(user).extract().path("accessToken"));
        ValidatableResponse response = userClient.createUser(user);
        token.add(response.extract().path("accessToken"));
        int statusCode = response.extract().statusCode();
        boolean isIdenticalUserNotCreated = response.extract().path("message").equals("User already exists");

        assertThat("Некорректный код статуса", statusCode, equalTo(403));
        assertTrue("Создано два одинаковых пользователя", isIdenticalUserNotCreated);
    }

}
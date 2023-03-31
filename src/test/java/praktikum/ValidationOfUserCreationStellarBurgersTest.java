package praktikum;

import com.github.javafaker.Faker;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class ValidationOfUserCreationStellarBurgersTest {

    private UserClientStellarBurgers userClient;
    private UserCredentialsStellarBurgers userCredentialsStellarBurgers;
    private UserStellarBurgers user;
    private int expectedStatus;
    private String expectedErrorMessage;

    private String token;

    @Before
    public void setUp() {

        userClient = new UserClientStellarBurgers();
    }

    @After
    public void setDown() {
        userCredentialsStellarBurgers = new UserCredentialsStellarBurgers();
        userCredentialsStellarBurgers.setEmail(user.getEmail());
        userCredentialsStellarBurgers.setPassword(user.getPassword());
        token = userClient.loginUser(userCredentialsStellarBurgers).extract().path("accessToken");
        if (token != null) userClient.deleteUser(token);
    }

    public ValidationOfUserCreationStellarBurgersTest(UserStellarBurgers user, int expectedStatus, String expectedErrorMessage) {
        this.user = user;
        this.expectedStatus = expectedStatus;
        this.expectedErrorMessage = expectedErrorMessage;
    }

    @Parameterized.Parameters
    public static Object[][] getTestData() {
        return new Object[][]{
                {UserStellarBurgers.getUserWithoutEmail(), 403, "Email, password and name are required fields"},
                {UserStellarBurgers.getUserWithoutName(), 403, "Email, password and name are required fields"},
                {UserStellarBurgers.getUserWithoutPassword(), 403, "Email, password and name are required fields"},
        };
    }

    @Test
    @DisplayName("Checking field validation for user creation")
    public void checkingFieldsValidationUserCreationValidationTest() {
        ValidatableResponse response = userClient.createUser(user);
        int statusCode = response.extract().statusCode();
        String errorMessage = response.extract().path("message");
        assertEquals("Некорректный код статуса", expectedStatus, statusCode);
        assertEquals("Некорректное сообщение об ошибке", expectedErrorMessage, errorMessage);
    }

}

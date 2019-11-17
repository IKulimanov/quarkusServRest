package ru.rsatu.dologin;

/**
 * Класс для ответа в метода логина
 *
 * @author npetrov
 */
public class LoginResponse {

    private String token;
    private String userName;

    public LoginResponse() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}

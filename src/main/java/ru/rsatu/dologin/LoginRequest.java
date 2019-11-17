package ru.rsatu.dologin;

/**
 * Запрос для логина
 *
 * @author npetrov
 */
public class LoginRequest {

    private String login;
    private String password;

    public LoginRequest() {
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}

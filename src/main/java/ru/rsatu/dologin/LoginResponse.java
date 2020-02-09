package ru.rsatu.dologin;

import scala.Int;

/**
 * Класс для ответа в метода логина
 *
 * @author npetrov
 */
public class LoginResponse {

    private String login;
    private String role;

    public LoginResponse() {
    }

    public String getlogin() {
        return login;
    }

    public void setlogin(String login) {
        this.login = login;
    }

    public String getrole() {
        return role;
    }

    public void setrole(String role) {
        this.role = role;
    }

}

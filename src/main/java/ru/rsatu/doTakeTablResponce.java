package ru.rsatu;

public class doTakeTablResponce {
    private String login;
    private String nameuser;
    private String email;
    private Integer phone;
    private String typepet;
    private String namepet;
    private Integer agepet;
    private Boolean ischild;
    private Boolean ispet;
    private Boolean ishouse;

    public boolean isIschild() {
        return ischild;
    }

    public void setIschild(Boolean ischild) {
        this.ischild = ischild;
    }

    public Boolean isIspet() {
        return ispet;
    }

    public void setIspet(Boolean ispet) {
        this.ispet = ispet;
    }

    public Boolean isIshouse() {
        return ishouse;
    }

    public void setIshouse(Boolean ishouse) {
        this.ishouse = ishouse;
    }



    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getNameuser() {
        return nameuser;
    }

    public void setNameuser(String nameuser) {
        this.nameuser = nameuser;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getPhone() {
        return phone;
    }

    public void setPhone(Integer phone) {
        this.phone = phone;
    }

    public String getTypepet() {
        return typepet;
    }

    public void setTypepet(String typepet) {
        this.typepet = typepet;
    }

    public String getNamepet() {
        return namepet;
    }

    public void setNamepet(String namepet) {
        this.namepet = namepet;
    }

    public Integer getAgepet() {
        return agepet;
    }

    public void setAgepet(Integer agepet) {
        this.agepet = agepet;
    }

    public String getGenderpet() {
        return genderpet;
    }

    public void setGenderpet(String genderpet) {
        this.genderpet = genderpet;
    }

    private String genderpet;

}

package ru.rsatu.models;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.*;

@Entity
@Table(name = "users", schema = "public", catalog = "postgres")
@NamedQueries({
        @NamedQuery(name = "UsersEntity.findlogin",
                query = "SELECT f FROM UsersEntity f WHERE f.login = ?1"),
        @NamedQuery(name = "UsersEntity.findemail",
                query = "SELECT h FROM UsersEntity h WHERE h.email = :email"),
        @NamedQuery(name = "UsersEntity.deleteUser",
                query = "DELETE FROM UsersEntity g WHERE g.login = :login"),
        @NamedQuery(name = "UsersEntity.updateReqVol",
                query = "UPDATE UsersEntity s SET s.reqvol = :reqvol WHERE s.login = :login"),
        @NamedQuery(name = "UsersEntity.findHash",
                query = "SELECT r FROM UsersEntity r WHERE r.hashsum = :hashsum"),
        @NamedQuery(name = "UsersEntity.findloginStat",
                query = "SELECT f FROM UsersEntity f WHERE f.login = :login AND f.active = :active"),
        @NamedQuery(name = "UsersEntity.findUsersStat",
                query = "SELECT f FROM UsersEntity f WHERE f.active = :active")

})

public class UsersEntity extends PanacheEntity {

    private int userId;

    private Integer hashsum;
    private Integer telephone;
    private String email;
    private String role;
    private Integer age;
    private Boolean reqvol;
    private String firstname;
    private String lastname;
    private String login;
    private Boolean active;


    @Basic
    @Column(name = "active")
    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }




    public UsersEntity getUser(String login){
        return find("login", login).firstResult();
    }

    public UsersEntity getUserMail(String mail){
        return find("email", mail).firstResult();
    }


    @Id
    @Column(name = "user_id")
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "hashsum")
    public Integer getHashsum() {
        return hashsum;
    }

    public void setHashsum(Integer hashsum) {
        this.hashsum = hashsum;
    }

    @Basic
    @Column(name = "telephone")
    public Integer getTelephone() {
        return telephone;
    }

    public void setTelephone(Integer telephone) {
        this.telephone = telephone;
    }

    @Basic
    @Column(name = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Basic
    @Column(name = "role")
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Basic
    @Column(name = "age")
    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Basic
    @Column(name = "reqvol")
    public Boolean getReqvol() {
        return reqvol;
    }

    public void setReqvol(Boolean reqvol) {
        this.reqvol = reqvol;
    }

    @Basic
    @Column(name = "firstname")
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    @Basic
    @Column(name = "lastname")
    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @Basic
    @Column(name = "login")
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UsersEntity that = (UsersEntity) o;

        if (userId != that.userId) return false;
        if (hashsum != null ? !hashsum.equals(that.hashsum) : that.hashsum != null) return false;
        if (telephone != null ? !telephone.equals(that.telephone) : that.telephone != null) return false;
        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (role != null ? !role.equals(that.role) : that.role != null) return false;
        if (age != null ? !age.equals(that.age) : that.age != null) return false;
        if (reqvol != null ? !reqvol.equals(that.reqvol) : that.reqvol != null) return false;
        if (firstname != null ? !firstname.equals(that.firstname) : that.firstname != null) return false;
        if (lastname != null ? !lastname.equals(that.lastname) : that.lastname != null) return false;
        if (login != null ? !login.equals(that.login) : that.login != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = userId;
        result = 31 * result + (hashsum != null ? hashsum.hashCode() : 0);
        result = 31 * result + (telephone != null ? telephone.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (role != null ? role.hashCode() : 0);
        result = 31 * result + (age != null ? age.hashCode() : 0);
        result = 31 * result + (reqvol != null ? reqvol.hashCode() : 0);
        result = 31 * result + (firstname != null ? firstname.hashCode() : 0);
        result = 31 * result + (lastname != null ? lastname.hashCode() : 0);
        result = 31 * result + (login != null ? login.hashCode() : 0);
        return result;
    }
}

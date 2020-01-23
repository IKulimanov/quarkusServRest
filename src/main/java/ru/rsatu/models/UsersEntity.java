package ru.rsatu.models;

import javax.persistence.*;

@Entity
@Table(name = "users", schema = "public", catalog = "postgres")
public class UsersEntity {
    private int userId;
    private String hashsum;
    private String fio;
    private String telephone;
    private String email;
    private String role;
    private Integer age;
    private Boolean reqvol;

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
    public String getHashsum() {
        return hashsum;
    }

    public void setHashsum(String hashsum) {
        this.hashsum = hashsum;
    }

    @Basic
    @Column(name = "fio")
    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    @Basic
    @Column(name = "telephone")
    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UsersEntity that = (UsersEntity) o;

        if (userId != that.userId) return false;
        if (hashsum != null ? !hashsum.equals(that.hashsum) : that.hashsum != null) return false;
        if (fio != null ? !fio.equals(that.fio) : that.fio != null) return false;
        if (telephone != null ? !telephone.equals(that.telephone) : that.telephone != null) return false;
        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (role != null ? !role.equals(that.role) : that.role != null) return false;
        if (age != null ? !age.equals(that.age) : that.age != null) return false;
        if (reqvol != null ? !reqvol.equals(that.reqvol) : that.reqvol != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = userId;
        result = 31 * result + (hashsum != null ? hashsum.hashCode() : 0);
        result = 31 * result + (fio != null ? fio.hashCode() : 0);
        result = 31 * result + (telephone != null ? telephone.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (role != null ? role.hashCode() : 0);
        result = 31 * result + (age != null ? age.hashCode() : 0);
        result = 31 * result + (reqvol != null ? reqvol.hashCode() : 0);
        return result;
    }
}

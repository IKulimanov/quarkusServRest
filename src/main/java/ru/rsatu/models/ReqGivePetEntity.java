package ru.rsatu.models;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.*;

@Entity
@Table(name = "req_give_pet", schema = "public", catalog = "postgres")
@NamedQueries({
        @NamedQuery(name = "ReqGivePetEntity.getall",
                query = "SELECT x FROM ReqGivePetEntity x "),
        @NamedQuery(name = "ReqGivePetEntity.findreq",
                query = "SELECT z.namepet,z.typePet,z.status FROM ReqGivePetEntity z WHERE z.idUser = :id_user AND z.active =: active")
})

public class ReqGivePetEntity extends PanacheEntity {
    @GeneratedValue
    private int idReqGp;

    private Integer status;
    private String namepet;
    private Integer agepet;
    private String genderPet;
    private String typePet;
    private Integer id_user;
    private Boolean active;

    @Basic
    @Column(name = "active")
    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Basic
    @Column(name = "status")
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Basic
    @Column(name = "namepet")
    public String getNamepet() {
        return namepet;
    }

    public void setNamepet(String namepet) {
        this.namepet = namepet;
    }

    @Id
    @Column(name = "id_req_gp")
    public int getIdReqGp() {
        return idReqGp;
    }

    public void setIdReqGp(int idReqGp) {
        this.idReqGp = idReqGp;
    }

    @Basic
    @Column(name = "agepet")
    public Integer getAgepet() {
        return agepet;
    }

    public void setAgepet(Integer agepet) {
        this.agepet = agepet;
    }

    @Basic
    @Column(name = "gender_pet")
    public String getGenderPet() {
        return genderPet;
    }

    public void setGenderPet(String genderPet) {
        this.genderPet = genderPet;
    }

    @Basic
    @Column(name = "type_pet")
    public String getTypePet() {
        return typePet;
    }

    public void setTypePet(String typePet) {
        this.typePet = typePet;
    }

    @Basic
    @Column(name = "id_user")
    public Integer getIdUser() {
        return id_user;
    }

    public void setIdUser(Integer id_user) {
        this.id_user = id_user;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReqGivePetEntity that = (ReqGivePetEntity) o;

        if (idReqGp != that.idReqGp) return false;
        if (namepet != null ? !namepet.equals(that.namepet) : that.namepet != null) return false;
        if (agepet != null ? !agepet.equals(that.agepet) : that.agepet != null) return false;
        if (genderPet != null ? !genderPet.equals(that.genderPet) : that.genderPet != null) return false;
        if (typePet != null ? !typePet.equals(that.typePet) : that.typePet != null) return false;
        if (id_user != null ? !id_user.equals(that.id_user) : that.id_user != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = namepet != null ? namepet.hashCode() : 0;
        result = 31 * result + idReqGp;
        result = 31 * result + (agepet != null ? agepet.hashCode() : 0);
        result = 31 * result + (genderPet != null ? genderPet.hashCode() : 0);
        result = 31 * result + (typePet != null ? typePet.hashCode() : 0);
        result = 31 * result + (id_user != null ? id_user.hashCode() : 0);
        return result;
    }
}

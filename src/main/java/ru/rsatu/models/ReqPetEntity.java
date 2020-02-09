package ru.rsatu.models;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "req_pet", schema = "public", catalog = "postgres")
@NamedQueries({
        @NamedQuery(name = "ReqPetEntity.getall",
                query = "SELECT x FROM PetsEntity x "),
        @NamedQuery(name = "ReqPetEntity.findreq",
                query = "SELECT z.petId,z.status FROM ReqPetEntity z WHERE z.userId = :id_user AND z.active =: active")
})
public class ReqPetEntity extends PanacheEntity {

    @GeneratedValue
    private int reqpetId;

    private Date dateConc;
    private Boolean isChild;
    private Boolean isPets;
    private Boolean isHouse;
    private Boolean active;
    private int userId;
    private int petId;
    private Integer status;

    @Basic
    @Column(name = "user_id")
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "pet_id")
    public Integer getPetId() {
        return petId;
    }

    public void setPetId(Integer petId) {
        this.petId = petId;
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
    @Column(name = "active")
    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Id
    @Column(name = "reqpet_id")
    public int getReqpetId() {
        return reqpetId;
    }

    public void setReqpetId(int reqpetId) {
        this.reqpetId = reqpetId;
    }

    @Basic
    @Column(name = "date_conc")
    public Date getDateConc() {
        return dateConc;
    }

    public void setDateConc(Date dateConc) {
        this.dateConc = dateConc;
    }

    @Basic
    @Column(name = "is_child")
    public Boolean getChild() {
        return isChild;
    }

    public void setChild(Boolean child) {
        isChild = child;
    }

    @Basic
    @Column(name = "is_pets")
    public Boolean getPets() {
        return isPets;
    }

    public void setPets(Boolean pets) {
        isPets = pets;
    }

    @Basic
    @Column(name = "is_house")
    public Boolean getIsHouse() {
        return isHouse;
    }

    public void setIsHouse(Boolean isHouse) {
        this.isHouse = isHouse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReqPetEntity that = (ReqPetEntity) o;

        if (reqpetId != that.reqpetId) return false;
        if (dateConc != null ? !dateConc.equals(that.dateConc) : that.dateConc != null) return false;
        if (isChild != null ? !isChild.equals(that.isChild) : that.isChild != null) return false;
        if (isPets != null ? !isPets.equals(that.isPets) : that.isPets != null) return false;
        if (isHouse != null ? !isHouse.equals(that.isHouse) : that.isHouse != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = reqpetId;
        result = 31 * result + (dateConc != null ? dateConc.hashCode() : 0);
        result = 31 * result + (isChild != null ? isChild.hashCode() : 0);
        result = 31 * result + (isPets != null ? isPets.hashCode() : 0);
        result = 31 * result + (isHouse != null ? isHouse.hashCode() : 0);
        return result;
    }
}

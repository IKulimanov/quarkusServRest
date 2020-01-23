package ru.rsatu.models;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "pets", schema = "public", catalog = "postgres")
public class PetsEntity extends PanacheEntity{
    private int petId;
    private String name;
    private Integer agePet;
    private String type;
    private Date recDate;

    @Id
    @Column(name = "pet_id")
    public int getPetId() {
        return petId;
    }

    public void setPetId(int petId) {
        this.petId = petId;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "age_pet")
    public Integer getAgePet() {
        return agePet;
    }

    public void setAgePet(Integer agePet) {
        this.agePet = agePet;
    }

    @Basic
    @Column(name = "type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Basic
    @Column(name = "rec_date")
    public Date getRecDate() {
        return recDate;
    }

    public void setRecDate(Date recDate) {
        this.recDate = recDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PetsEntity that = (PetsEntity) o;

        if (petId != that.petId) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (agePet != null ? !agePet.equals(that.agePet) : that.agePet != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (recDate != null ? !recDate.equals(that.recDate) : that.recDate != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = petId;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (agePet != null ? agePet.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (recDate != null ? recDate.hashCode() : 0);
        return result;
    }
}

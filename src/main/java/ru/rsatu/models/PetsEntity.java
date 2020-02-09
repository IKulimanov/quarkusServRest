package ru.rsatu.models;

import com.arjuna.ats.internal.jdbc.drivers.modifiers.list;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.*;

@Entity
@Table(name = "pets", schema = "public", catalog = "postgres")
@NamedQueries(
        {
            @NamedQuery(name = "PetsEntity.getall",
                    query = "SELECT f FROM PetsEntity f "),
            @NamedQuery(name = "PetsEntity.getPet",
                    query = "SELECT f FROM PetsEntity f WHERE f.type = :type AND f.gender = :gender AND" +
                            " f.age >= :agel AND f.age <= :ageh AND f.active = :active"),
            @NamedQuery(name = "PetsEntity.deletePets",
                    query = "DELETE FROM PetsEntity g WHERE g.petId = :petId"),
                @NamedQuery(name = "PetsEntity.getPetfromID",
                        query = "SELECT a.name, a.type FROM PetsEntity a WHERE a.petId = :petId")
        }
)

public class PetsEntity extends PanacheEntity {
    @GeneratedValue
    private int petId;

    private String name;
    private Integer age;
    private String type;
    private String gender;
    private String photo;
    private Boolean active;


    @Basic
    @Column(name = "active")
    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }


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
    @Column(name = "age")
    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
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
    @Column(name = "gender")
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Basic
    @Column(name = "photo")
    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }




    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PetsEntity that = (PetsEntity) o;

        if (petId != that.petId) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (age != null ? !age.equals(that.age) : that.age != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (gender != null ? !gender.equals(that.gender) : that.gender != null) return false;
        if (photo != null ? !photo.equals(that.photo) : that.photo != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = petId;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (age != null ? age.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (gender != null ? gender.hashCode() : 0);
        result = 31 * result + (photo != null ? photo.hashCode() : 0);
        return result;
    }
}

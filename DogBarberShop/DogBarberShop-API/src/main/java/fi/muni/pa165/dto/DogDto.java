package fi.muni.pa165.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.joda.time.LocalDate;

/**
 *  @TODO doplnit vsade validaciu aby sme nemali v DTO null hodnoty
 * 
 * @author Oliver Pentek
 */

@XmlRootElement
public final class DogDto implements Serializable {
    private Long id;
    private String name;
    private String breed;
    
    private LocalDate birthDate;
    private CustomerDto owner;

    public DogDto() {
    }
    
    public DogDto(Long id){
        this(id, null, null, null, null);
    }
    
    public DogDto(String name, String breed, LocalDate birthDate, CustomerDto owner) {
        this(null, name, breed, birthDate, owner);
    }

    public DogDto(Long id, String name, String breed, LocalDate birthDate, CustomerDto owner) {
        this.id = id;
        this.name = name;
        this.breed = breed;
        this.birthDate = birthDate;
        this.owner = owner;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CustomerDto getOwner() {
        return owner;
    }

    public void setOwner(CustomerDto owner) {
        this.owner = owner;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DogDto other = (DogDto) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DogDto{" + "id=" + id + ", name=" + name + ", breed=" + breed + ", birthDate=" + birthDate + ", owner=" + owner + '}';
    }
       
    public String toStringData() {
        if (birthDate != null) {
            return id + " " + name + " " + breed + " " + birthDate.toString("dd.MM.yyyy") + " " + owner;
        }
        return id + " " + name + " " + breed + " " + "null" + " " + owner;
    }
}

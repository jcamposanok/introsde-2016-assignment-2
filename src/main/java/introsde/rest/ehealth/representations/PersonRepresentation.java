package introsde.rest.ehealth.representations;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import introsde.rest.ehealth.models.HealthProfileItem;
import introsde.rest.ehealth.models.Person;
import introsde.rest.ehealth.util.DateParser;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@XmlRootElement(name = "person")
@JsonRootName("person")
public class PersonRepresentation {

    private int personId;

    private String lastname;

    private String firstname;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateParser.DEFAULT_FORMAT)
    private Date birthdate;

    private List<HealthProfileItemRepresentation> healthProfile;


    public PersonRepresentation(Person p) {
        personId = p.getPersonId();
        lastname = p.getLastname();
        firstname = p.getFirstname();
        birthdate = p.getBirthdate();
        setHealthProfile(p.getHealthProfile());
    }

    public PersonRepresentation() {

    }


    // Getters and setters
    @XmlElement(name = "pid")
    @JsonProperty("pid")
    public int getPersonId() {
        return personId;
    }

    public String getLastname() {
        return lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    @XmlTransient
    @JsonIgnore
    public List<HealthProfileItemRepresentation> getHealthProfile() {
        return healthProfile;
    }

    @XmlElementWrapper(name = "healthProfile")
    @XmlElement(name = "measure")
    @JsonProperty("healthProfile")
    public List<HealthProfileItemRepresentation> getCurrentHealthProfile() {
        List<HealthProfileItemRepresentation> currentHealthProfile = new ArrayList<>();
        if (healthProfile != null && healthProfile.size() > 0) {
            for (HealthProfileItemRepresentation hp : healthProfile) {
                if (hp.isValid()) {
                    currentHealthProfile.add(hp);
                }
            }
        }
        return currentHealthProfile;
    }


    // Setters

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public void setHealthProfile(List<HealthProfileItem> healthProfileItemList) {
        healthProfile = new ArrayList<>();
        if (healthProfileItemList != null && healthProfileItemList.size() > 0) {
            for (HealthProfileItem i : healthProfileItemList) {
                healthProfile.add(new HealthProfileItemRepresentation(i));
            }
        }
    }
}

package introsde.rest.ehealth.representations;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import introsde.rest.ehealth.models.Measure;
import introsde.rest.ehealth.models.Person;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "people")
public class PersonListRepresentation {

    private List<PersonRepresentation> people;

    @XmlElement(name = "person")
    @JsonProperty("people")
    public List<PersonRepresentation> getPeople() {
        return people;
    }

    public void setPeople(List<PersonRepresentation> people) {
        this.people = people;
    }

    public PersonListRepresentation(List<PersonRepresentation> people) {
        setPeople(people);
    }

    public PersonListRepresentation() {
        this(new ArrayList<>());
    }

}

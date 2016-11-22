package introsde.rest.ehealth.representations;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import introsde.rest.ehealth.models.HealthProfileItem;
import introsde.rest.ehealth.models.Measure;
import introsde.rest.ehealth.models.Person;
import introsde.rest.ehealth.util.DateParser;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement(name = "measure")
@JsonRootName("measure")
public class HealthProfileItemRepresentation {

    private int healthProfileId;

    private Person person;

    private Measure measure;

    private Float value;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateParser.DEFAULT_FORMAT)
    private Date created;

    private boolean isValid;

    private String measureName;


    public HealthProfileItemRepresentation(HealthProfileItem i) {
        healthProfileId = i.getHealthProfileId();
        person = i.getPerson();
        measure = i.getMeasure();
        value = i.getValue();
        created = i.getCreated();
        isValid = i.isValid();
    }

    public HealthProfileItemRepresentation() {

    }


    // Getters and setters


    @XmlElement(name = "mid")
    @JsonProperty("mid")
    public int getHealthProfileId() {
        return healthProfileId;
    }

    @XmlElement(name = "name")
    @JsonProperty("name")
    public String getMeasureName() {
        if (measure != null) {
            return measure.getName();
        }
        return measureName;
    }

    public Float getValue() {
        return value;
    }

    public Date getCreated() {
        return created;
    }

    @JsonIgnore
    public boolean isValid() {
        return isValid;
    }


    // Setters

    public void setHealthProfileId(int healthProfileId) {
        this.healthProfileId = healthProfileId;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public void setMeasure(Measure measure) {
        this.measure = measure;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public void setMeasureName(String measureName) {
        this.measureName = measureName;
    }
}

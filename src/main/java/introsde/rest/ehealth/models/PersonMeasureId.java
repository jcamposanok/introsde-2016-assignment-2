package introsde.rest.ehealth.models;

import java.io.Serializable;


public class PersonMeasureId implements Serializable {
    private int personId;
    private int measureId;

    public int hashCode() {
        return (personId + measureId);
    }

    public boolean equals(Object object) {
        if (object instanceof PersonMeasureId) {
            PersonMeasureId otherId = (PersonMeasureId) object;
            return (otherId.personId == this.personId) && (otherId.measureId == this.measureId);
        }
        return false;
    }
}

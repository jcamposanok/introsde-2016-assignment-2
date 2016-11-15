package introsde.rest.ehealth.models;

import javax.xml.bind.annotation.*;
import java.util.*;


@XmlRootElement(name = "measureTypes")
public class MeasureTypes {

    private List<String> measureTypes;

    @XmlElement(name = "measureType")
    public List<String> getMeasureTypes() {
        return measureTypes;
    }

    public void setMeasureTypes(List<Measure> measureTypes) {
        this.measureTypes = new ArrayList<>();
        for (Measure m : measureTypes) {
            this.measureTypes.add(m.getName());
        }
    }
}

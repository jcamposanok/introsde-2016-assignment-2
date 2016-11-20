package introsde.rest.ehealth.models;

import com.fasterxml.jackson.annotation.JsonRootName;

import javax.xml.bind.annotation.*;
import java.util.*;


@XmlRootElement(name = "measureTypes")
@JsonRootName("measureTypes")
public class MeasureTypes {

    private List<Measure> measureTypes;

    @XmlElement(name = "measureType")
    public List<Measure> getMeasureTypes() {
        return measureTypes;
    }

    public void setMeasureTypes(List<Measure> measureTypes) {
        this.measureTypes = measureTypes;
    }
}

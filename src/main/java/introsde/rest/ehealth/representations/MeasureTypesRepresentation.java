package introsde.rest.ehealth.representations;

import com.fasterxml.jackson.annotation.JsonRootName;
import introsde.rest.ehealth.models.Measure;

import javax.xml.bind.annotation.*;
import java.util.*;


@XmlRootElement(name = "measureTypes")
@JsonRootName("measureTypes")
public class MeasureTypesRepresentation {

    private List<Measure> measureTypes;

    @XmlElement(name = "measureType")
    public List<Measure> getMeasureTypes() {
        return measureTypes;
    }

    public void setMeasureTypes(List<Measure> measureTypes) {
        this.measureTypes = measureTypes;
    }
}

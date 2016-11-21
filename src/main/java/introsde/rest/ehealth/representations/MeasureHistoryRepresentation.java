package introsde.rest.ehealth.representations;

import com.fasterxml.jackson.annotation.JsonRootName;
import introsde.rest.ehealth.models.HealthProfileItem;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;


@XmlRootElement(name = "measureHistory")
@JsonRootName("")
public class MeasureHistoryRepresentation {

    private List<HealthProfileItem> history;

    @XmlElement(name = "measure")
    public List<HealthProfileItem> getHistory() {
        return history;
    }

    public void setHistory(List<HealthProfileItem> history) {
        this.history = history;
    }

    public MeasureHistoryRepresentation(List<HealthProfileItem> history) {
        setHistory(history);
    }

    public MeasureHistoryRepresentation() {
        this(new ArrayList<>());
    }

}

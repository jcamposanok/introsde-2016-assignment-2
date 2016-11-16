package introsde.rest.ehealth.models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;


@XmlRootElement(name = "measureHistory")
public class MeasureHistoryWrapper {

    private List<MeasureHistory> history;

    @XmlElement(name = "measure")
    public List<MeasureHistory> getMeasureHistory() {
        return history;
    }

    public void setMeasureHistory(List<MeasureHistory> history) {
        this.history = new ArrayList<>();
        for (MeasureHistory historyItem : history) {
            this.history.add(historyItem);
        }
    }
}

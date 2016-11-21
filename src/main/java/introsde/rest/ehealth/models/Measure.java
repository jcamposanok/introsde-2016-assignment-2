package introsde.rest.ehealth.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import introsde.rest.ehealth.dao.HealthDao;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name="Measure")
@NamedQueries({
        @NamedQuery(name="Measure.findAll", query="SELECT m FROM Measure m"),
        @NamedQuery(name = "Measure.findByName", query = "SELECT m FROM Measure m WHERE m.name = :name")
})
@XmlRootElement
public class Measure implements Serializable {

    @Id
    @GeneratedValue(generator = "sqlite_measure")
    @TableGenerator(name = "sqlite_measure", table = "sqlite_sequence", pkColumnName = "name", valueColumnName = "seq", pkColumnValue = "Measure", allocationSize = 1)
    @Column(name = "measureId")
    private int measureId;

    @NotNull
    private String name;

    @OneToMany(mappedBy = "measure", fetch = FetchType.EAGER)
    private List<HealthProfileItem> measureHistory;

    @XmlTransient
    @JsonIgnore
    public int getMeasureId() {
        return measureId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public static List<Measure> getAll() {
        List<Measure> list = new ArrayList<>();
        EntityManager em = HealthDao.createEntityManager();
        if (em != null) {
            list = em.createNamedQuery("Measure.findAll").getResultList();
            em.close();
        }
        return list;
    }

    public static Measure getByName(String name) {
        Measure m = new Measure();
        EntityManager em = HealthDao.createEntityManager();
        if (em != null) {
            List<Measure> measureList = em.createNamedQuery("Measure.findByName")
                    .setParameter("name", name).getResultList();
            if (measureList != null && measureList.size() > 0) {
                m = measureList.get(0);
            }
            em.close();
        }
        return m;
    }
}

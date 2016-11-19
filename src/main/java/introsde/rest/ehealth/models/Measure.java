package introsde.rest.ehealth.models;

import introsde.rest.ehealth.dao.HealthDao;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
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
    @Column(name = "idMeasure")
    private int idMeasure;

    @Column(name="name")
    @NotNull
    private String name;

    @OneToMany(mappedBy = "measure", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<PersonMeasure> healthProfile;

    public int getIdMeasure() {
        return idMeasure;
    }

    @XmlElement(name = "measureType")
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
            if (measureList != null) {
                m = measureList.get(0);
            }
            em.close();
        }
        return m;
    }
}

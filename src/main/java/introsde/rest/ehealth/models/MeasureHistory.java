package introsde.rest.ehealth.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import introsde.rest.ehealth.dao.HealthDao;
import introsde.rest.ehealth.util.DateParser;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by JC on 15/11/2016.
 */
@Entity
@Table(name="MeasureHistory")
@NamedQueries({
        @NamedQuery(name = "MeasureHistory.findAll", query = "SELECT h FROM MeasureHistory h"),
        @NamedQuery(name = "MeasureHistory.findByPersonAndType",
                    query = "SELECT h FROM MeasureHistory h " +
                            "WHERE h.person = :person AND h.measure = :measure")
        ,
        @NamedQuery(name = "MeasureHistory.findByDate",
                query = "SELECT h FROM MeasureHistory h " +
                        "WHERE h.person = :person AND h.measure = :measure " +
                        "AND h.created BETWEEN :after AND :before")
})
@XmlRootElement(name = "measure")
public class MeasureHistory implements Serializable {

    @Id
    @GeneratedValue(generator = "sqlite_measurehistory")
    @TableGenerator(name = "sqlite_measurehistory", table = "sqlite_sequence", pkColumnName = "name", valueColumnName = "seq", pkColumnValue = "MeasureHistory")
    @Column(name = "idMeasureHistory")
    private int idMeasureHistory;

    @ManyToOne
    @JoinColumn(name = "idPerson", referencedColumnName = "idPerson")
    private Person person;

    @ManyToOne
    @JoinColumn(name = "idMeasure", referencedColumnName = "idMeasure")
    private Measure measure;

    @Temporal(TemporalType.DATE)
    @Column(name="created")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateParser.DEFAULT_FORMAT)
    private Date created;

    @Column(name = "value")
    private Float value;

    @XmlElement(name = "mid")
    @JsonProperty("mid")
    public int getIdMeasureHistory() {
        return idMeasureHistory;
    }

    @XmlElement(name = "name")
    @JsonProperty("name")
    public String getMeasureName() {
        return measure.getName();
    }
    public Date getCreated() {
        return created;
    }
    public Float getValue() {
        return value;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
    public void setMeasure(Measure measure) {
        this.measure = measure;
    }
    public void setCreated(Date created) {
        this.created = created;
    }
    public void setValue(Float value) {
        this.value = value;
    }

    public MeasureHistory() {
    }

    public static MeasureHistory getById(int measureHistoryId) {
        MeasureHistory h = new MeasureHistory();
        EntityManager em = HealthDao.createEntityManager();
        if (em != null) {
            h = em.find(MeasureHistory.class, measureHistoryId);
            em.close();
        }
        return h;
    }

    public static List<MeasureHistory> getAllByPersonAndType(int personId, String measureType) {
        List<MeasureHistory> resultList = new ArrayList<>();
        EntityManager em = HealthDao.createEntityManager();
        if (em != null) {
            Person person = Person.getById(personId);
            Measure measure = Measure.getByName(measureType);
            if (person != null && measure != null) {
                resultList = em.createNamedQuery("MeasureHistory.findByPersonAndType")
                        .setParameter("person", person)
                        .setParameter("measure", measure)
                        .getResultList();
            }
            em.close();
        }
        return resultList;
    }

    public static List<MeasureHistory> getAllByDate(int personId, String measureType, Date startDate, Date endDate) {
        List<MeasureHistory> resultList = new ArrayList<>();
        EntityManager em = HealthDao.createEntityManager();
        if (em != null) {
            Person person = Person.getById(personId);
            Measure measure = Measure.getByName(measureType);
            if (person != null && measure != null) {
                resultList = em.createNamedQuery("MeasureHistory.findByDate")
                        .setParameter("person", person)
                        .setParameter("measure", measure)
                        .setParameter("after", startDate)
                        .setParameter("before", endDate)
                        .getResultList();
            }
            em.close();
        }
        return resultList;
    }

    public static MeasureHistory saveMeasureHistory(MeasureHistory h) {
        EntityManager em = HealthDao.createEntityManager();
        if (em != null) {
            EntityTransaction tx = em.getTransaction();
            tx.begin();
            em.persist(h);
            tx.commit();
            em.close();
        }
        return h;
    }
}

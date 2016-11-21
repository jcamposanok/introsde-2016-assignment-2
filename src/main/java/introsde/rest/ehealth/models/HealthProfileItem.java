package introsde.rest.ehealth.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import introsde.rest.ehealth.dao.HealthDao;
import introsde.rest.ehealth.util.DateParser;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity
@Table(name = "HealthProfile")
@NamedQueries({
        @NamedQuery(name = "HealthProfile.findAll", query = "SELECT hp FROM HealthProfileItem hp"),
        @NamedQuery(name = "HealthProfile.findByType",
                query = "SELECT hp FROM HealthProfileItem hp " +
                        "WHERE hp.measure = :measure"),
        @NamedQuery(name = "HealthProfile.findByTypeMin",
                query = "SELECT hp FROM HealthProfileItem hp " +
                        "WHERE hp.measure = :measure AND hp.value >= :minValue"),
        @NamedQuery(name = "HealthProfile.findByTypeMax",
                query = "SELECT hp FROM HealthProfileItem hp " +
                        "WHERE hp.measure = :measure AND hp.value <= :maxValue"),
        @NamedQuery(name = "HealthProfile.findByTypeRange",
                query = "SELECT hp FROM HealthProfileItem hp " +
                        "WHERE hp.measure = :measure AND hp.value BETWEEN :minValue AND :maxValue"),
        @NamedQuery(name = "HealthProfile.findByPersonAndType",
                query = "SELECT hp FROM HealthProfileItem hp " +
                        "WHERE hp.person = :person AND hp.measure = :measure"),
        @NamedQuery(name = "HealthProfile.getCurrentByPersonAndType",
                query = "SELECT hp FROM HealthProfileItem hp " +
                        "WHERE hp.person = :person AND hp.measure = :measure AND hp.isValid = TRUE"),
        @NamedQuery(name = "HealthProfile.findByDate",
                query = "SELECT hp FROM HealthProfileItem hp " +
                        "WHERE hp.person = :person AND hp.measure = :measure " +
                        "AND hp.created BETWEEN :after AND :before")
})
@XmlRootElement
public class HealthProfileItem implements Serializable {

    @Id
    @GeneratedValue(generator = "sqlite_healthprofile")
    @TableGenerator(name = "sqlite_healthprofile", table = "sqlite_sequence", pkColumnName = "name", valueColumnName = "seq", pkColumnValue = "HealthProfile", allocationSize = 10)
    @Column(name = "healthProfileId")
    private int healthProfileId;

    @ManyToOne
    @JoinColumn(name = "personId", referencedColumnName = "personId")
    private Person person;

    @ManyToOne
    @JoinColumn(name = "measureId", referencedColumnName = "measureId")
    private Measure measure;

    @Column(name = "value")
    private Float value;

    @Temporal(TemporalType.DATE)
    @Column(name="created")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateParser.DEFAULT_FORMAT)
    private Date created;

    @Column(name = "isValid")
    private boolean isValid;


    public HealthProfileItem() {
    }



    @XmlElement(name = "mid")
    @JsonProperty("mid")
    public int getHealthProfileId() {
        return healthProfileId;
    }

    @XmlTransient // To prevent infinite loop
    @JsonIgnore
    public Person getPerson() {
        return person;
    }

    @XmlTransient // To prevent infinite loop
    @JsonIgnore
    public Measure getMeasure() {
        return measure;
    }

    public Float getValue() {
        return value;
    }

    public Date getCreated() {
        return created;
    }

    public boolean isValid() {
        return isValid;
    }

    /*
    @XmlElement(name = "name") // Fake getter to retrieve the measure name
    @JsonProperty("name")
    public String getMeasureName() {
        if (measure != null) {
            return measure.getName();
        }
        return measureName;
    }
    */


    public void setHealthProfileId(int healthProfileId) {
        this.healthProfileId = healthProfileId;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public void setMeasure(Measure measure) {
        if (measure != null) {
            this.measure = measure;
            // this.measureName = measure.getName();
        }
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public void setValid(boolean valid) {
        this.isValid = valid;
    }



    public static HealthProfileItem getById(int mid) {
        HealthProfileItem hp = new HealthProfileItem();
        EntityManager em = HealthDao.createEntityManager();
        if (em != null) {
            hp = em.find(HealthProfileItem.class, mid);
            em.close();
        }
        return hp;
    }

    public static List<HealthProfileItem> getAllByType(String measureType, float minValue, float maxValue) {
        List<HealthProfileItem> resultList = new ArrayList<>();
        EntityManager em = HealthDao.createEntityManager();
        if (em != null) {
            Measure measure = Measure.getByName(measureType);
            if (measure != null) {
                if (minValue >= 0 && maxValue >= 0) {
                    resultList = em.createNamedQuery("HealthProfile.findByTypeRange")
                            .setParameter("measure", measure)
                            .setParameter("minValue", minValue)
                            .setParameter("maxValue", maxValue)
                            .getResultList();
                }
                else if (minValue >= 0) {
                    resultList = em.createNamedQuery("HealthProfile.findByTypeMin")
                            .setParameter("measure", measure)
                            .setParameter("minValue", minValue)
                            .getResultList();
                }
                else if (maxValue >= 0) {
                    resultList = em.createNamedQuery("HealthProfile.findByTypeMax")
                            .setParameter("measure", measure)
                            .setParameter("maxValue", maxValue)
                            .getResultList();
                }
                else {
                    resultList = em.createNamedQuery("HealthProfile.findByType")
                            .setParameter("measure", measure)
                            .getResultList();
                }
            }
            em.close();
        }
        return resultList;
    }

    public static List<HealthProfileItem> getAllByPersonAndType(int personId, String measureType) {
        List<HealthProfileItem> resultList = new ArrayList<>();
        EntityManager em = HealthDao.createEntityManager();
        if (em != null) {
            Person person = Person.getById(personId);
            Measure measure = Measure.getByName(measureType);
            if (person != null && measure != null) {
                resultList = em.createNamedQuery("HealthProfile.findByPersonAndType")
                        .setParameter("person", person)
                        .setParameter("measure", measure)
                        .getResultList();
            }
            em.close();
        }
        return resultList;
    }

    public static HealthProfileItem getCurrentByPersonAndType(int personId, String measureType) {
        HealthProfileItem result = new HealthProfileItem();
        List<HealthProfileItem> resultList = new ArrayList<>();
        EntityManager em = HealthDao.createEntityManager();
        if (em != null) {
            Person person = Person.getById(personId);
            Measure measure = Measure.getByName(measureType);
            if (person != null && measure != null) {
                resultList = em.createNamedQuery("HealthProfile.getCurrentByPersonAndType")
                        .setParameter("person", person)
                        .setParameter("measure", measure)
                        .getResultList();
            }
            em.close();
        }
        if (resultList != null && resultList.size() > 0) {
            result = resultList.get(0);
        }
        return result;
    }

    public static List<HealthProfileItem> getAllByDate(int personId, String measureType, Date startDate, Date endDate) {
        List<HealthProfileItem> resultList = new ArrayList<>();
        EntityManager em = HealthDao.createEntityManager();
        if (em != null) {
            Person person = Person.getById(personId);
            Measure measure = Measure.getByName(measureType);
            if (person != null && measure != null) {
                resultList = em.createNamedQuery("HealthProfile.findByDate")
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

    public static HealthProfileItem saveHealthProfileItem(HealthProfileItem hp) {
        EntityManager em = HealthDao.createEntityManager();
        if (em != null && hp.getPerson() != null && hp.getMeasure() != null) {
            EntityTransaction tx = em.getTransaction();
            tx.begin();
            em.persist(hp);
            tx.commit();
            em.close();
        }
        return hp;
    }

    public static HealthProfileItem updateHealthProfileItem(HealthProfileItem hp) {
        EntityManager em = HealthDao.createEntityManager();
        if (em != null && hp.getPerson() != null && hp.getMeasure() != null) {
            EntityTransaction tx = em.getTransaction();
            tx.begin();
            hp = em.merge(hp);
            tx.commit();
            em.close();
        }
        return hp;
    }
}

package introsde.rest.ehealth.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import introsde.rest.ehealth.dao.HealthDao;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity
@Table(name = "PersonMeasure")
@NamedQueries({
        @NamedQuery(name = "PersonMeasure.findAll", query = "SELECT l FROM PersonMeasure l"),
        @NamedQuery(name = "PersonMeasure.findByType",
                query = "SELECT pm FROM PersonMeasure pm " +
                        "WHERE pm.measure = :measure"),
        @NamedQuery(name = "PersonMeasure.findByTypeMin",
                query = "SELECT pm FROM PersonMeasure pm " +
                        "WHERE pm.measure = :measure AND pm.value >= :minValue"),
        @NamedQuery(name = "PersonMeasure.findByTypeMax",
                query = "SELECT pm FROM PersonMeasure pm " +
                        "WHERE pm.measure = :measure AND pm.value <= :maxValue"),
        @NamedQuery(name = "PersonMeasure.findByTypeRange",
                query = "SELECT pm FROM PersonMeasure pm " +
                        "WHERE pm.measure = :measure AND pm.value BETWEEN :minValue AND :maxValue"),
        @NamedQuery(name = "PersonMeasure.findByPersonAndType",
                query = "SELECT pm FROM PersonMeasure pm " +
                        "WHERE pm.person = :person AND pm.measure = :measure")
})
@XmlRootElement(name = "measure")
public class PersonMeasure implements Serializable {

    @Id
    @GeneratedValue(generator = "sqlite_personmeasure")
    @TableGenerator(name = "sqlite_personmeasure", table = "sqlite_sequence", pkColumnName = "name", valueColumnName = "seq", pkColumnValue = "PersonMeasure")
    @Column(name = "idPersonMeasure")
    private int idPersonMeasure;

    @ManyToOne
    @JoinColumn(name = "idPerson", referencedColumnName = "idPerson", insertable = true, updatable = true)
    private Person person;

    @ManyToOne
    @JoinColumn(name = "idMeasure", referencedColumnName = "idMeasure", insertable = true, updatable = true)
    private Measure measure;

    @Column(name = "value")
    private Float value;

    @Temporal(TemporalType.DATE)
    @Column(name = "created")
    private Date created;

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

    @XmlTransient // To hide this
    @JsonIgnore
    public int getIdPersonMeasure() {
        return idPersonMeasure;
    }

    @XmlTransient // to hide this
    @JsonIgnore
    public Date getCreated() {
        return created;
    }

    @XmlElement(name = "name") // Fake getter, just to display the name
    @JsonProperty("name")
    public String getMeasureName() {
        return measure.getName();
    }

    public Float getValue() {
        return value;
    }


    public void setIdPersonMeasure(int idPersonMeasure) {
        this.idPersonMeasure = idPersonMeasure;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public void setMeasure(Measure measure) {
        this.measure = measure;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public PersonMeasure() {
    }

    public static PersonMeasure getById(int mid) {
        PersonMeasure personMeasure = new PersonMeasure();
        EntityManager em = HealthDao.createEntityManager();
        if (em != null) {
            personMeasure = em.find(PersonMeasure.class, mid);
            em.close();
        }
        return personMeasure;
    }

    public static List<PersonMeasure> getAllByType(String measureType, float minValue, float maxValue) {
        List<PersonMeasure> resultList = new ArrayList<>();
        EntityManager em = HealthDao.createEntityManager();
        if (em != null) {
            Measure measure = Measure.getByName(measureType);
            if (measure != null) {
                if (minValue >= 0 && maxValue >= 0) {
                    resultList = em.createNamedQuery("PersonMeasure.findByTypeRange")
                            .setParameter("measure", measure)
                            .setParameter("minValue", minValue)
                            .setParameter("maxValue", maxValue)
                            .getResultList();
                }
                else if (minValue >= 0) {
                    resultList = em.createNamedQuery("PersonMeasure.findByTypeMin")
                            .setParameter("measure", measure)
                            .setParameter("minValue", minValue)
                            .getResultList();
                }
                else if (maxValue >= 0) {
                    resultList = em.createNamedQuery("PersonMeasure.findByTypeMax")
                            .setParameter("measure", measure)
                            .setParameter("maxValue", maxValue)
                            .getResultList();
                }
                else {
                    resultList = em.createNamedQuery("PersonMeasure.findByType")
                            .setParameter("measure", measure)
                            .getResultList();
                }
            }
            em.close();
        }
        return resultList;
    }

    public static List<PersonMeasure> getAllByPersonAndType(int personId, String measureType) {
        List<PersonMeasure> resultList = new ArrayList<>();
        EntityManager em = HealthDao.createEntityManager();
        if (em != null) {
            Person person = Person.getById(personId);
            Measure measure = Measure.getByName(measureType);
            if (person != null && measure != null) {
                resultList = em.createNamedQuery("PersonMeasure.findByPersonAndType")
                        .setParameter("person", person)
                        .setParameter("measure", measure)
                        .getResultList();
            }
            em.close();
        }
        return resultList;
    }

    public static PersonMeasure savePersonMeasure(PersonMeasure pm) {
        EntityManager em = HealthDao.createEntityManager();
        if (em != null) {
            EntityTransaction tx = em.getTransaction();
            tx.begin();
            em.persist(pm);
            tx.commit();
            em.close();
        }
        return pm;
    }

    public static PersonMeasure updatePersonMeasure(PersonMeasure pm) {
        EntityManager em = HealthDao.createEntityManager();
        if (em != null) {
            EntityTransaction tx = em.getTransaction();
            tx.begin();
            pm = em.merge(pm);
            tx.commit();
            em.close();
        }
        return pm;
    }
}

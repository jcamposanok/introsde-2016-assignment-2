package introsde.rest.ehealth.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
        @NamedQuery(name = "PersonMeasure.findAll", query = "SELECT l FROM PersonMeasure l") ,
        @NamedQuery(name = "PersonMeasure.findByPersonAndType",
                query = "SELECT pm FROM PersonMeasure pm " +
                        "WHERE pm.person = :person AND pm.measure = :measure")
})
@XmlRootElement(name="measureType")
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
    private String value;

    @Temporal(TemporalType.DATE)
    @Column(name="created")
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

    @XmlElement(name = "measure") // Fake getter, just to display the name
    public String getMeasureName() { return measure.getName(); }

    public String getValue() {
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
    public void setValue(String value) {
        this.value = value;
    }
    public void setCreated(Date created) {
        this.created = created;
    }

    public static List<PersonMeasure> getAllByPersonAndType(int personId, String measureType) {
        List<PersonMeasure> resultList = new ArrayList<>();
        EntityManager em = HealthDao.instance.createEntityManager();
        if (em != null) {
            Person person = Person.getById(personId);
            Measure measure = Measure.getByName(measureType);
            if (person != null && measure != null) {
                resultList = em.createNamedQuery("PersonMeasure.findByPersonAndType")
                        .setParameter("person", person)
                        .setParameter("measure", measure)
                        .getResultList();
            }
            HealthDao.instance.closeConnections(em);
        }
        return resultList;
    }

    public static PersonMeasure savePersonMeasure(PersonMeasure pm) {
        EntityManager em = HealthDao.instance.createEntityManager();
        if (em != null) {
            EntityTransaction tx = em.getTransaction();
            tx.begin();
            em.persist(pm);
            tx.commit();
            HealthDao.instance.closeConnections(em);
        }
        return pm;
    }

    public static PersonMeasure updatePersonMeasure(PersonMeasure pm) {
        EntityManager em = HealthDao.instance.createEntityManager();
        if (em != null) {
            EntityTransaction tx = em.getTransaction();
            tx.begin();
            pm = em.merge(pm);
            tx.commit();
            HealthDao.instance.closeConnections(em);
        }
        return pm;
    }
}

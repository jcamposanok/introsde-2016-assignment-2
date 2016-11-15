package introsde.rest.ehealth.models;

import introsde.rest.ehealth.dao.HealthDao;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.*;


@Entity
@Table(name="\"Person\"")
@NamedQuery(name="Person.findAll", query="SELECT p FROM Person p")
@XmlRootElement
public class Person implements Serializable {

    @Id
    @GeneratedValue(generator = "sqlite_person")
    @TableGenerator(name = "sqlite_person", table = "sqlite_sequence", pkColumnName = "name", valueColumnName = "seq", pkColumnValue = "Person")
    @Column(name = "idPerson")
    private int idPerson;

    @Column(name = "lastname")
    private String lastname;

    @Column(name = "firstname")
    private String firstname;

    @Temporal(TemporalType.DATE)
    @Column(name = "birthdate")
    private Date birthdate;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<PersonMeasure> healthProfile;

    public Person() {
    }

    // Getters and setters
    /*
    // This is commented so it won't appear in the response
    public int getIdPerson() {
        return idPerson;
    }
    */
    public void setIdPerson(int idPerson) {
        this.idPerson = idPerson;
    }
    public String getLastname() {
        return lastname;
    }
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
    public String getFirstname() {
        return firstname;
    }
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
    public Date getBirthdate() {
        return birthdate;
    }
    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }
    @XmlElementWrapper(name = "healthProfile")
    @XmlElement(name = "measureType")
    public List<PersonMeasure> getHealthProfile() {
        return healthProfile;
    }
    public void setHealthProfile(List<PersonMeasure> healthProfile) {
        this.healthProfile = healthProfile;
    }

/*
    public static int getNextId() {
        int count = 0;
        EntityManager em = HealthDao.instance.createEntityManager();
        if (em != null) {
            count = em.createNativeQuery("Person.findAll").getResultList().size();
        }
        return (count + 1);
    }
*/

    public static List<Person> getAll() {
        List<Person> list = new ArrayList<>();
        EntityManager em = HealthDao.instance.createEntityManager();
        if (em != null) {
            list = em.createNamedQuery("Person.findAll").getResultList();
            HealthDao.instance.closeConnections(em);
        }
        return list;
    }

    public static Person getById(int personId) {
        Person p = new Person();
        EntityManager em = HealthDao.instance.createEntityManager();
        if (em != null) {
            p = em.find(Person.class, personId);
            HealthDao.instance.closeConnections(em);
        }
        return p;
    }

    public static Person savePerson(Person p) {
        EntityManager em = HealthDao.instance.createEntityManager();
        if (em != null) {
            EntityTransaction tx = em.getTransaction();
            tx.begin();
            em.persist(p);
            tx.commit();
            HealthDao.instance.closeConnections(em);
        }
        return p;
    }

    public static Person updatePerson(Person p) {
        EntityManager em = HealthDao.instance.createEntityManager();
        if (em != null) {
            EntityTransaction tx = em.getTransaction();
            tx.begin();
            p = em.merge(p);
            tx.commit();
            HealthDao.instance.closeConnections(em);
        }
        return p;
    }

    public static void removePerson(Person p) {
        EntityManager em = HealthDao.instance.createEntityManager();
        if (em != null) {
            EntityTransaction tx = em.getTransaction();
            tx.begin();
            p = em.merge(p);
            em.remove(p);
            tx.commit();
            HealthDao.instance.closeConnections(em);
        }
    }
}

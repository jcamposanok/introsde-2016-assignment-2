package introsde.rest.ehealth.resources;

import introsde.rest.ehealth.models.Person;
import introsde.rest.ehealth.models.PersonMeasure;
import introsde.rest.ehealth.util.DateParser;

import javax.persistence.EntityManager;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;


public class PersonResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    int id;

    public PersonResource(UriInfo uriInfo, Request request, int id) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.id = id;
    }

    // Request 2

    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Person getPerson() {
        Person person = Person.getById(this.id);
        if (person == null) {
            throw new NotFoundException("GET: Person with id " + this.id + " not found");
        }
        return person;
    }

    // Request 3

    @PUT
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response putPerson(Person person) {
        System.out.println("--> Updating person with id " + this.id);
        System.out.println("--> " + person.toString());

        Response res;

        person.setIdPerson(this.id);
        Person existing = Person.getById(this.id);

        if (existing == null) {
            res = Response.created(uriInfo.getAbsolutePath()).build();
            Person newPerson = Person.savePerson(person);
            List<PersonMeasure> healthProfile = newPerson.getHealthProfile();

            if (healthProfile != null && healthProfile.size() > 0) {
                System.out.println("Creating health profile for the new person...");
                for (int i = 0; i < healthProfile.size(); i++) {
                    PersonMeasure pm = healthProfile.get(i);
                    pm.setMeasureName(pm.getMeasureName());
                    pm.setPerson(newPerson);
                    if (pm.getCreated() == null) {
                        pm.setCreated(new DateParser.RequestParam().parseFromString());
                    }
                    pm = PersonMeasure.updatePersonMeasure(pm);
                    healthProfile.set(i, pm);
                }
                newPerson.setHealthProfile(healthProfile);
            }
            // throw new RuntimeException("PUT: Person with id " + this.id + " not found");
            // res = Response.notModified("PUT: Person with id " + this.id + " not found").build();
        } else {
            res = Response.ok().build();
            person.setHealthProfile(existing.getHealthProfile()); // Do not update
            Person.updatePerson(person);
        }

        return res;
    }

    // Request 5

    @DELETE
    public void deletePerson() {
        Person p = Person.getById(this.id);
        if (p == null) {
            throw new RuntimeException("DELETE: Person with " + this.id + " not found");
        }
        Person.removePerson(p);
    }
}

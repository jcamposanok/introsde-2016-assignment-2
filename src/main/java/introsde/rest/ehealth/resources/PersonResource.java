package introsde.rest.ehealth.resources;

import introsde.rest.ehealth.models.Person;

import javax.persistence.EntityManager;
import javax.ws.rs.*;
import javax.ws.rs.core.*;


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
            throw new RuntimeException("GET: Person with id " + this.id + " not found");
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

        Person existing = Person.getById(this.id);
        if (existing == null) {
            res = Response.noContent().build();
        } else {
            res = Response.created(uriInfo.getAbsolutePath()).build();
            person.setIdPerson(this.id);
            // Person.updatePerson(person);
        }
        Person.updatePerson(person);

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

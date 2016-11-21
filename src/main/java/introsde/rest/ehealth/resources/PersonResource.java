package introsde.rest.ehealth.resources;

import introsde.rest.ehealth.models.HealthProfileItem;
import introsde.rest.ehealth.models.Measure;
import introsde.rest.ehealth.models.Person;
import introsde.rest.ehealth.representations.PersonRepresentation;
import introsde.rest.ehealth.util.DateParser;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Collections;
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
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getPerson() {
        Person person = Person.getById(this.id);
        if (person == null) {
            return Response.status(Response.Status.NOT_FOUND.getStatusCode())
                    .entity("GET: Person with id " + this.id + " not found").build();
        }
        PersonRepresentation entity = new PersonRepresentation(person);
        return Response.ok().entity(entity).build();
    }

    // Request 3

    @PUT
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response putPerson(PersonRepresentation entity) {
        System.out.println("--> Updating person with id " + this.id);
        System.out.println("--> " + entity.toString());

        Response res;

        Person existingPerson = Person.getById(this.id);
        if (existingPerson == null) {
            Person newPerson = new Person();
            newPerson.setPersonId(this.id);
            newPerson.setLastname(entity.getLastname());
            newPerson.setFirstname(entity.getFirstname());
            newPerson.setBirthdate(entity.getBirthdate());
            newPerson = Person.savePerson(newPerson);

            PersonRepresentation newEntity = new PersonRepresentation(newPerson);
            res = Response.created(uriInfo.getAbsolutePath()).entity(newEntity).build();
        } else {
            existingPerson.setLastname(entity.getLastname());
            existingPerson.setFirstname(entity.getFirstname());
            existingPerson.setBirthdate(entity.getBirthdate());
            Person updatedPerson = Person.updatePerson(existingPerson);

            PersonRepresentation updatedEntity = new PersonRepresentation(updatedPerson);
            res = Response.ok().entity(updatedEntity).build();
        }

        return res;
    }

    // Request 5

    @DELETE
    public Response deletePerson() {
        Person p = Person.getById(this.id);
        if (p == null) {
            return Response
                    .status(Response.Status.NOT_FOUND.getStatusCode())
                    .entity("DELETE: Person with " + this.id + " not found")
                    .build();
        }
        Person.removePerson(p);
        PersonRepresentation entity = new PersonRepresentation(p);
        return Response.ok().entity(entity).build();
    }
}

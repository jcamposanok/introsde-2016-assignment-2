package introsde.rest.ehealth.resources;

import introsde.rest.ehealth.models.Person;
import introsde.rest.ehealth.util.DateParser;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Path("person")
public class PersonCollectionResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    // Request 1

    @GET
    @Produces({MediaType.TEXT_XML,  MediaType.APPLICATION_JSON ,  MediaType.APPLICATION_XML })
    public List<Person> getAllPeople() {
        System.out.println("Getting list of people...");
        List<Person> people = Person.getAll();
        return people;
    }

    // Requests 2, 3, 5

    @Path("{personId}")
    public PersonResource getPersonResource(@PathParam("personId") int id) {
        return new PersonResource(uriInfo, request, id);
    }

    // Requests 6, 8, 11

    @Path("{personId}/{measureType}")
    public MeasureHistoryResource getMeasureHistoryResource(@PathParam("personId") int personId, @PathParam("measureType") String measureType) {
        return new MeasureHistoryResource(uriInfo, request, personId, measureType);
    }

    // Requests 7, 10

    @Path("{personId}/{measureType}/{mid}")
    public PersonMeasureResource getPersonMeasureResource(@PathParam("personId") int personId, @PathParam("measureType") String measureType, @PathParam("mid") int mid) {
        return new PersonMeasureResource(uriInfo, request, personId, measureType, mid);
    }

    // Request 4

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void newPerson(@FormParam("lastname") String lastname,
                          @FormParam("firstname") String firstname,
                          @FormParam("birthdate") String birthdate,
                          @Context HttpServletResponse servletResponse) throws IOException {
        Person p = new Person();
        // p.setIdPerson(Person.getNextId());
        p.setLastname(lastname);
        p.setFirstname(firstname);
        p.setBirthdate(new DateParser.RequestParam(birthdate).parseFromString());

        Person newPerson = this.newPerson(p);
        servletResponse.sendRedirect("/"); // TODO: Check this redirection
    }

    @POST
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Person newPerson(Person person) throws IOException {
        System.out.println("Creating new person...");
        return Person.savePerson(person);
    }
}

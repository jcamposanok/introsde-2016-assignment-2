package introsde.rest.ehealth.resources;

import introsde.rest.ehealth.models.Measure;
import introsde.rest.ehealth.models.MeasureTypes;
import introsde.rest.ehealth.models.Person;
import introsde.rest.ehealth.models.PersonMeasure;
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
import java.util.*;


@Path("person")
public class PersonCollectionResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    // Requests 1, 12

    @GET
    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<Person> getAllPeople(@QueryParam("measureType") String measureType,
                                     @QueryParam("min") Float min,
                                     @QueryParam("max") Float max) {
        List<Person> people = new ArrayList<>();
        List<PersonMeasure> personMeasureList;

        // Request 12
        if (measureType != null) {
            float minValue = (min == null || min <= 0) ? -1 : min;
            float maxValue = (max == null || max <= 0) ? -1 : max;
            System.out.println("Getting list of people with measure '" + "'");
            personMeasureList = PersonMeasure.getAllByType(measureType, minValue, maxValue);
            for (PersonMeasure pm : personMeasureList) {
                people.add(pm.getPerson());
            }
        }
        // Request 1
        else {
            System.out.println("Getting list of all people...");
            people = Person.getAll();
        }

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
        List<PersonMeasure> healthProfile;

        System.out.println("Creating new person...");
        Person newPerson = Person.savePerson(person);
        healthProfile = newPerson.getHealthProfile();

        if (healthProfile != null && healthProfile.size() > 0) {
            System.out.println("Creating health profile for the new person...");
            for (int i = 0; i < healthProfile.size(); i++) {
                PersonMeasure pm = healthProfile.get(i);
                Measure m = Measure.getByName(pm.getMeasureName());
                if (m != null) {
                    pm.setMeasure(m);
                    // pm.setMeasureName(pm.getMeasureName());
                    pm.setPerson(newPerson);
                    if (pm.getCreated() == null) {
                        pm.setCreated(new DateParser.RequestParam().parseFromString());
                    }
                    pm = PersonMeasure.updatePersonMeasure(pm);
                    healthProfile.set(i, pm);
                }
            }
            newPerson.setHealthProfile(healthProfile);
        }

        return newPerson;
    }
}

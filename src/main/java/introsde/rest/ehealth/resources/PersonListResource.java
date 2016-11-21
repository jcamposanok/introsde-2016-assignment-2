package introsde.rest.ehealth.resources;

import introsde.rest.ehealth.models.*;
import introsde.rest.ehealth.representations.PersonListRepresentation;
import introsde.rest.ehealth.representations.PersonRepresentation;
import introsde.rest.ehealth.util.DateParser;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;

import java.net.URI;
import java.util.*;


@Path("person")
public class PersonListResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    // Requests 1, 12

    @GET
    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public PersonListRepresentation getAllPeople(@QueryParam("measureType") String measureType,
                                                   @QueryParam("min") Float min,
                                                   @QueryParam("max") Float max) {
        PersonListRepresentation result = new PersonListRepresentation();
        List<PersonRepresentation> people = new ArrayList<>();
        List<Person> peopleList = new ArrayList<>();
        Set<Person> uniquePeople = new HashSet<>();
        List<HealthProfileItem> healthProfileItems;

        // Request 12
        if (measureType != null) {
            float minValue = (min == null || min <= 0) ? -1 : min;
            float maxValue = (max == null || max <= 0) ? -1 : max;
            System.out.println("Getting list of people with measure '" + "'");
            healthProfileItems = HealthProfileItem.getAllByType(measureType, minValue, maxValue);
            for (HealthProfileItem item : healthProfileItems) {
                peopleList.add(item.getPerson());
            }
            uniquePeople.addAll(peopleList);
            peopleList.clear();
            peopleList.addAll(uniquePeople);
        }
        // Request 1
        else {
            System.out.println("Getting list of all people...");
            peopleList = Person.getAll();
        }

        for (Person p : peopleList) {
            PersonRepresentation pr = new PersonRepresentation(p);
            people.add(pr);
        }
        result.setPeople(people);
        return result;
    }

    // Requests 2, 3, 5

    @Path("{personId}")
    public PersonResource getPersonResource(@PathParam("personId") int id) {
        return new PersonResource(uriInfo, request, id);
    }

    // Requests 6, 8, 11

    @Path("{personId}/{measureType}")
    public HealthProfileResource getHealthProfileResource(@PathParam("personId") int personId, @PathParam("measureType") String measureType) {
        return new HealthProfileResource(uriInfo, request, personId, measureType);
    }

    // Requests 7, 10

    @Path("{personId}/{measureType}/{mid}")
    public HealthProfileItemResource getHealthProfileItemResource(@PathParam("personId") int personId, @PathParam("measureType") String measureType, @PathParam("mid") int mid) {
        return new HealthProfileItemResource(uriInfo, request, personId, measureType, mid);
    }

    // Request 4

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response newPerson(@FormParam("lastname") String lastname,
                          @FormParam("firstname") String firstname,
                          @FormParam("birthdate") String birthdate,
                          @Context HttpServletResponse servletResponse) throws IOException {
        Person p = new Person();
        p.setLastname(lastname);
        p.setFirstname(firstname);
        p.setBirthdate(new DateParser.RequestParam(birthdate).parseFromString());

        PersonRepresentation entity = this.newPerson(p);

        URI uri = UriBuilder.fromUri("/person/" + String.valueOf(entity.getPersonId())).build();
        return Response.seeOther(uri).build();
    }

    @POST
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public PersonRepresentation newPerson(Person person) throws IOException {
        // List<HealthProfileItem> healthProfile;

        System.out.println("Creating new person...");
        Person newPerson = Person.savePerson(person);

        /*
        healthProfile = newPerson.getHealthProfile();

        if (healthProfile != null && healthProfile.size() > 0) {
            System.out.println("Creating health profile for the new person...");
            for (int i = 0; i < healthProfile.size(); i++) {
                HealthProfileItem hp = healthProfile.get(i);
                Measure m = Measure.getByName(hp.getMeasureName());
                if (m != null) {
                    hp.setMeasure(m);
                    hp.setPerson(newPerson);
                    if (hp.getCreated() == null) {
                        hp.setCreated(new DateParser.RequestParam().parseFromString());
                    }
                    hp.setValid(true);
                    hp = HealthProfileItem.updateHealthProfileItem(hp);
                    healthProfile.set(i, hp);
                }
            }
            newPerson.setHealthProfile(healthProfile);
        }
        */

        PersonRepresentation entity = new PersonRepresentation(newPerson);
        return entity;
    }
}

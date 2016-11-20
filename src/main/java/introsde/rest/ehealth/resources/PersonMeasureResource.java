package introsde.rest.ehealth.resources;

import introsde.rest.ehealth.models.PersonMeasure;

import javax.ws.rs.*;
import javax.ws.rs.core.*;


public class PersonMeasureResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    int personId;
    String measureType;
    int mid;

    public PersonMeasureResource(UriInfo uriInfo, Request request, int personId, String measureType, int mid) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.personId = personId;
        this.measureType = measureType;
        this.mid = mid;
    }

    // Request 7
/*
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public MeasureHistory getMeasureHistoryItem() {
        MeasureHistory measureHistory = MeasureHistory.getById(this.mid);
        if (measureHistory == null) {
            throw new RuntimeException("GET: Measure with id" + this.mid + " for person with id " + this.personId + " not found in history");
        }
        return measureHistory;
    }
*/
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public PersonMeasure getPersonMeasure() {
        PersonMeasure personMeasure = PersonMeasure.getById(this.mid);
        if (personMeasure == null) {
            throw new RuntimeException("GET: Measure with id " + this.mid + " for person with id " + this.personId + " not found in current Health Profile");
        }
        return personMeasure;
    }

    // Request 10

    @PUT
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response putPersonMeasure(PersonMeasure personMeasure) {
        System.out.println("--> Updating measure '" + personMeasure.getMeasureName() + "' for person with id " + this.personId);

        Response res;

        PersonMeasure existing = PersonMeasure.getById(this.mid);
        if (existing == null) {
            throw new NotFoundException("PUT: Health profile entry with mid " + this.mid + " not found");
        } else {
            res = Response.created(uriInfo.getAbsolutePath()).build();
            PersonMeasure.updatePersonMeasure(personMeasure);
        }

        return res;
    }
}

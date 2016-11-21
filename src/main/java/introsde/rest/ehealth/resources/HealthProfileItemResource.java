package introsde.rest.ehealth.resources;

import introsde.rest.ehealth.models.HealthProfileItem;
import introsde.rest.ehealth.models.Measure;
import introsde.rest.ehealth.models.Person;
import introsde.rest.ehealth.representations.HealthProfileItemRepresentation;
import introsde.rest.ehealth.util.DateParser;

import javax.ws.rs.*;
import javax.ws.rs.core.*;


public class HealthProfileItemResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    int personId;
    String measureType;
    int mid;

    public HealthProfileItemResource(UriInfo uriInfo, Request request, int personId, String measureType, int mid) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.personId = personId;
        this.measureType = measureType;
        this.mid = mid;
    }

    // Request 7

    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response getHealthProfileItem() {
        HealthProfileItem hp = HealthProfileItem.getById(this.mid);
        if (hp == null) {
            return Response.status(Response.Status.NOT_FOUND.getStatusCode())
                    .entity("GET: Measure with id " + this.mid
                            + " for person with id " + this.personId
                            + " not found.")
                    .build();
        }
        HealthProfileItemRepresentation entity = new HealthProfileItemRepresentation(hp);
        return Response.ok().entity(entity).build();
    }

    // Request 10

    @PUT
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response putHealthProfileItem(HealthProfileItemRepresentation entity) {
        System.out.println("--> Updating measure '" + entity.getMeasureName() + "' for person with id " + this.personId);

        Response res;

        HealthProfileItem existingHealthProfileItem = HealthProfileItem.getById(this.mid);
        if (existingHealthProfileItem == null) {
            HealthProfileItem newHealthProfileItem = new HealthProfileItem();
            Person person = Person.getById(this.personId);
            Measure measure = Measure.getByName(entity.getMeasureName());
            newHealthProfileItem.setHealthProfileId(this.mid);
            newHealthProfileItem.setPerson(person);
            newHealthProfileItem.setMeasure(measure);
            newHealthProfileItem.setValue(entity.getValue());
            newHealthProfileItem.setCreated(entity.getCreated());
            newHealthProfileItem.setValid(true);
            newHealthProfileItem = HealthProfileItem.saveHealthProfileItem(newHealthProfileItem);

            HealthProfileItemRepresentation newEntity = new HealthProfileItemRepresentation(newHealthProfileItem);
            res = Response.created(uriInfo.getAbsolutePath()).entity(newEntity).build();
        } else {

            HealthProfileItem backupHealthProfileItem = new HealthProfileItem();
            backupHealthProfileItem.setPerson(existingHealthProfileItem.getPerson());
            backupHealthProfileItem.setMeasure(existingHealthProfileItem.getMeasure());
            backupHealthProfileItem.setValue(existingHealthProfileItem.getValue());
            backupHealthProfileItem.setCreated(existingHealthProfileItem. getCreated());
            backupHealthProfileItem.setValid(false);
            HealthProfileItem.saveHealthProfileItem(backupHealthProfileItem);

            existingHealthProfileItem.setValue(entity.getValue());
            existingHealthProfileItem.setValid(true);
            existingHealthProfileItem = HealthProfileItem.updateHealthProfileItem(existingHealthProfileItem);

            HealthProfileItemRepresentation updatedEntity = new HealthProfileItemRepresentation(existingHealthProfileItem);
            res = Response.ok().entity(updatedEntity).build();
        }

        return res;
    }
}

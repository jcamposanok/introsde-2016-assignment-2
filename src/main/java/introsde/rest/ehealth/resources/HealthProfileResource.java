package introsde.rest.ehealth.resources;

import introsde.rest.ehealth.models.*;
import introsde.rest.ehealth.representations.HealthProfileItemRepresentation;
import introsde.rest.ehealth.representations.MeasureHistoryRepresentation;
import introsde.rest.ehealth.util.DateParser;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class HealthProfileResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    int personId;
    String measureType;

    public HealthProfileResource(UriInfo uriInfo, Request request, int personId, String measureType) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.personId = personId;
        this.measureType = measureType;
    }

    protected List<HealthProfileItem> getMeasureHistoryByDate(String before, String after) {
        Date startDate;
        Date endDate;

        try {
            startDate = new DateParser.RequestParam(after).parseFromString();
        }
        catch (Exception e) {
            startDate = new DateParser.RequestParam(DateParser.DEFAULT_START_DATE).parseFromString();
        }
        try {
            endDate = new DateParser.RequestParam(before).parseFromString();
        }
        catch (Exception e) {
            endDate = new DateParser.RequestParam(DateParser.DEFAULT_END_DATE).parseFromString();
        }

        return HealthProfileItem.getAllByDate(this.personId, this.measureType, startDate, endDate);
    }

    // Requests 6, 11

    @GET
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public MeasureHistoryRepresentation getMeasureHistory(@QueryParam("before") String before,
                                                      @QueryParam("after") String after) {
        MeasureHistoryRepresentation response = new MeasureHistoryRepresentation();
        List<HealthProfileItem> measureHistory;

        if (before == null && after == null) {
            measureHistory = HealthProfileItem.getAllByPersonAndType(this.personId, this.measureType);
        }
        else {
            measureHistory = this.getMeasureHistoryByDate(before, after);
        }
        response.setHistory(measureHistory);
        return response;
    }

    // Request 8

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response newHealthProfileItem(@FormParam("value") Float value,
                          @FormParam("created") String created,
                          @Context HttpServletResponse servletResponse) throws IOException {
        HealthProfileItem hp = new HealthProfileItem();
        Person person = Person.getById(this.personId);
        Measure measure = Measure.getByName(this.measureType);
        if (person != null && measure != null && value != null && value >= 0) {
            hp.setPerson(person);
            hp.setMeasure(measure);
            hp.setValue(value);
            hp.setCreated(new DateParser.RequestParam(created).parseFromString());
            hp.setValid(true);
            HealthProfileItemRepresentation entity = new HealthProfileItemRepresentation(hp);
            entity = this.newHealthProfileItem(entity);

            URI uri = UriBuilder.fromUri("/person/" + String.valueOf(this.personId) + "/mid/" + entity.getHealthProfileId()).build();
            return Response.seeOther(uri).build();
        }
        return Response.notModified().build();
    }

    @POST
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public HealthProfileItemRepresentation newHealthProfileItem(HealthProfileItemRepresentation requestEntity) throws IOException {
        System.out.println("Checking if a value currently exists for measure '" + this.measureType + "' and person with id " + this.personId);

        HealthProfileItem outdatedHealthProfileItem = HealthProfileItem.getCurrentByPersonAndType(this.personId, this.measureType);
        if (outdatedHealthProfileItem != null) {
            System.out.println("Measure already exists. Sending to history...");

            HealthProfileItem newHealthProfileItem = new HealthProfileItem();
            newHealthProfileItem.setPerson(outdatedHealthProfileItem.getPerson());
            newHealthProfileItem.setMeasure(outdatedHealthProfileItem.getMeasure());
            newHealthProfileItem.setValue(requestEntity.getValue());
            newHealthProfileItem.setCreated(requestEntity.getCreated());
            newHealthProfileItem.setValid(true);
            newHealthProfileItem = HealthProfileItem.saveHealthProfileItem(newHealthProfileItem);

            outdatedHealthProfileItem.setValid(false);
            HealthProfileItem updatedHealthProfileItem = HealthProfileItem.updateHealthProfileItem(outdatedHealthProfileItem);

            HealthProfileItemRepresentation entity = new HealthProfileItemRepresentation(newHealthProfileItem);
            return entity;
        }

        System.out.println("Measure not found for this user. Saving as new measure in database..");
        HealthProfileItem newHealthProfileItem = new HealthProfileItem();
        Person person = Person.getById(this.personId);
        Measure measure = Measure.getByName(this.measureType);
        newHealthProfileItem.setPerson(person);
        newHealthProfileItem.setMeasure(measure);
        newHealthProfileItem.setValue(requestEntity.getValue());
        newHealthProfileItem.setCreated(requestEntity.getCreated());
        newHealthProfileItem.setValid(true);
        newHealthProfileItem = HealthProfileItem.saveHealthProfileItem(newHealthProfileItem);
        HealthProfileItemRepresentation entity = new HealthProfileItemRepresentation(newHealthProfileItem);

        return entity;
    }

}

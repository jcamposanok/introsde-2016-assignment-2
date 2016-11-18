package introsde.rest.ehealth.resources;

import introsde.rest.ehealth.models.*;
import introsde.rest.ehealth.util.DateParser;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MeasureHistoryResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    int personId;
    String measureType;

    public MeasureHistoryResource(UriInfo uriInfo, Request request, int personId, String measureType) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.personId = personId;
        this.measureType = measureType;
    }

    protected List<MeasureHistory> getMeasureHistoryByDate(String before, String after) {
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

        return MeasureHistory.getAllByDate(this.personId, this.measureType, startDate, endDate);
    }

    // Requests 6, 11

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<MeasureHistory> getMeasureHistoryJson(@QueryParam("before") String before,
                                                      @QueryParam("after") String after) {
        List<MeasureHistory> measureHistory;

        if (before == null && after == null) {
            measureHistory = MeasureHistory.getAllByPersonAndType(this.personId, this.measureType);
        }
        else {
            measureHistory = this.getMeasureHistoryByDate(before, after);
        }
        if (measureHistory == null || measureHistory.size() < 1) {
            throw new RuntimeException("GET: Measure '" + this.measureType + "' history for person with id " + this.personId + " not found");
        }

        return measureHistory;
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public MeasureHistoryWrapper getMeasureHistoryXml(@QueryParam("before") String before,
                                                      @QueryParam("after") String after) {
        MeasureHistoryWrapper response = new MeasureHistoryWrapper();
        List<MeasureHistory> measureHistory;

        if (before == null && after == null) {
            measureHistory = MeasureHistory.getAllByPersonAndType(this.personId, this.measureType);
        }
        else {
            measureHistory = this.getMeasureHistoryByDate(before, after);
        }
        if (measureHistory == null || measureHistory.size() < 1) {
            throw new RuntimeException("GET: Measure '" + this.measureType + "' history for person with id " + this.personId + " not found");
        }

        response.setMeasureHistory(measureHistory);
        return response;
    }

    // Request 8

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void newPersonMeasure(@FormParam("value") Float value,
                          @FormParam("created") String created,
                          @Context HttpServletResponse servletResponse) throws IOException {
        PersonMeasure pm = new PersonMeasure();
        Person person = Person.getById(this.personId);
        Measure measure = Measure.getByName(this.measureType);
        if (person != null && measure != null && value != null && value >= 0) {
            pm.setPerson(person);
            pm.setMeasure(measure);
            pm.setValue(value);
            pm.setCreated(new DateParser.RequestParam(created).parseFromString());
            this.newPersonMeasure(pm);
        }
        servletResponse.sendRedirect("/"); // TODO: Check this redirection
    }

    @POST
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public PersonMeasure newPersonMeasure(PersonMeasure personMeasure) throws IOException {
        System.out.println("Checking if a value currently exists for measure '" + this.measureType + "' and person with id " + this.personId);
        List<PersonMeasure> pmList = PersonMeasure.getAllByPersonAndType(this.personId, this.measureType);
        if (pmList != null && pmList.size() > 0) {
            PersonMeasure personMeasureOld = pmList.get(0);
            System.out.println("Measure already exists. Sending to history...");

            // Persist in history the old measurements
            MeasureHistory h = new MeasureHistory();
            h.setPerson(personMeasureOld.getPerson());
            h.setMeasure(personMeasureOld.getMeasure());
            h.setValue(personMeasureOld.getValue());
            h.setCreated(personMeasureOld.getCreated());
            MeasureHistory.saveMeasureHistory(h);

            // Update current measurements (PersonMeasure) table
            personMeasure.setIdPersonMeasure(personMeasureOld.getIdPersonMeasure());
            return PersonMeasure.updatePersonMeasure(personMeasure);
        }

        System.out.println("Measure not found for this user. Saving as new measure in database..");
        return PersonMeasure.savePersonMeasure(personMeasure);
    }

}

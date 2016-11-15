package introsde.rest.ehealth.resources;

import introsde.rest.ehealth.models.Measure;
import introsde.rest.ehealth.models.MeasureHistory;
import introsde.rest.ehealth.models.Person;
import introsde.rest.ehealth.models.PersonMeasure;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class MeasureHistoryCollectionResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    int personId;
    String measureType;

    public MeasureHistoryCollectionResource(UriInfo uriInfo, Request request, int personId, String measureType) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.personId = personId;
        this.measureType = measureType;
    }

    // Request 6

    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public List<MeasureHistory> getMeasureHistoryCollection() {
        List<MeasureHistory> measureHistory = MeasureHistory.getAllByPersonAndType(this.personId, this.measureType);
        if (measureHistory == null || measureHistory.size() < 1) {
            throw new RuntimeException("GET: Measure '" + this.measureType + "' history for person with id " + this.personId + " not found");
        }
        return measureHistory;
    }

    // Request 8

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void newPerson(@FormParam("value") String value,
                          @FormParam("created") String created,
                          @Context HttpServletResponse servletResponse) throws IOException {
        PersonMeasure pm = new PersonMeasure();
        Person person = Person.getById(this.personId);
        Measure measure = Measure.getByName(this.measureType);
        if (person != null && measure != null) {
            pm.setPerson(person);
            pm.setMeasure(measure);
            pm.setValue(value);
            Date parsed;
            try {
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                parsed = format.parse(created);
            }
            catch(ParseException pe) {
                // throw new IllegalArgumentException();
                parsed = new Date();
            }
            pm.setCreated(parsed);
        }
        PersonMeasure newPersonMeasure = this.newPersonMeasure(pm);
        servletResponse.sendRedirect("/");
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

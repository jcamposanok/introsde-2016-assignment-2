package introsde.rest.ehealth.resources;

import introsde.rest.ehealth.models.Measure;
import introsde.rest.ehealth.models.MeasureTypes;
import introsde.rest.ehealth.models.Person;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Path("measureTypes")
public class MeasureTypesResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    // Request 9

    @GET
    @Produces({MediaType.TEXT_XML,  MediaType.APPLICATION_JSON ,  MediaType.APPLICATION_XML })
    public MeasureTypes getAllMeasureTypes() {
        System.out.println("Getting list of measures...");
        MeasureTypes measureTypes = new MeasureTypes();
        measureTypes.setMeasureTypes(Measure.getAll());
        return measureTypes;
    }
}

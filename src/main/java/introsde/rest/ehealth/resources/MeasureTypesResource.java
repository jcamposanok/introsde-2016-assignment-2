package introsde.rest.ehealth.resources;

import introsde.rest.ehealth.models.Measure;
import introsde.rest.ehealth.representations.MeasureTypesRepresentation;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;


@Path("measureTypes")
public class MeasureTypesResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    // Request 9

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public MeasureTypesRepresentation getAllMeasureTypes() {
        System.out.println("Getting list of measures...");
        MeasureTypesRepresentation measureTypes = new MeasureTypesRepresentation();
        measureTypes.setMeasureTypes(Measure.getAll());
        return measureTypes;
    }

    /*
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Measure> getAllMeasureTypes() {
        System.out.println("Getting list of measures...");
        return Measure.getAll();
    }
    */
}

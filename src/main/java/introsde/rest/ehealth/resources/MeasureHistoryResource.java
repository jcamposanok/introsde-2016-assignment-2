package introsde.rest.ehealth.resources;

import introsde.rest.ehealth.models.MeasureHistory;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import java.util.List;


public class MeasureHistoryResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    int personId;
    String measureType;
    int mid;

    public MeasureHistoryResource(UriInfo uriInfo, Request request, int personId, String measureType, int mid) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.personId = personId;
        this.measureType = measureType;
        this.mid = mid;
    }

    // Request 7

    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public MeasureHistory getMeasureHistory() {
        MeasureHistory measureHistory = MeasureHistory.getById(this.mid);
        if (measureHistory == null) {
            throw new RuntimeException("GET: Measure with id" + this.mid + " for person with id " + this.personId + " not found");
        }
        return measureHistory;
    }
}

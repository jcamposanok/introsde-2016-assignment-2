package introsde.client;

import introsde.rest.ehealth.models.*;
import introsde.rest.ehealth.representations.*;
import org.glassfish.jersey.client.ClientConfig;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by JC on 19/11/2016.
 */
public class Test {

    static String baseUrl;
    static WebTarget service;

    private static void setBaseUrl(String env) {
        Properties props = new Properties();
        URL propFileUrl = Test.class.getClassLoader().getResource("project.properties");
        try {
            props.load(propFileUrl.openStream());
            baseUrl = props.getProperty("project.baseurl." + env);
        } catch (IOException e) {
            e.printStackTrace();
            baseUrl = "";
        }
        baseUrl = (baseUrl.charAt(baseUrl.length() - 1) == '/') ? baseUrl + "/" : baseUrl;
        System.out.println("REST API URL set to " + baseUrl);
    }

    public static String getBaseUrl() {
        return baseUrl;
    }


    public static void main(String[] args) {
        String env = (args == null || args.length == 0) ? "local" : args[0];
        setBaseUrl(env);

        ClientConfig clientConfig = new ClientConfig();
        Client client = ClientBuilder.newClient(clientConfig);
        service = client.target(getBaseUrl());

        executeTestPlan(MediaType.APPLICATION_XML);
        // executeTestPlan(MediaType.APPLICATION_JSON);
    }
    
    private static void executeTestPlan(String mediaType) {
        String requestUrl;
        PersonRepresentation firstPerson;
        PersonRepresentation lastPerson;
        MeasureTypesRepresentation measureTypes;
        Response res1, res2, res3;

        try {

            // Step 3.1

            requestUrl = "person";
            printRequest(1, requestUrl, HttpMethod.GET, mediaType);
            res1 = get(requestUrl, mediaType);
            PersonListRepresentation people = res1.readEntity(PersonListRepresentation.class);
            printResponse(res1, people.getPeople().size() >= 2);
            firstPerson = people.getPeople().get(0);
            lastPerson = people.getPeople().get(people.getPeople().size() - 1);
            res1.close();

            // Step 3.2

            requestUrl = "person/" + String.valueOf(firstPerson.getPersonId());
            printRequest(2, requestUrl, HttpMethod.GET, mediaType);
            res2 = get(requestUrl, mediaType);
            printResponse(res2, res2.getStatus() == 200 || res2.getStatus() == 202);
            res2.close();

            // Step 3.3

            requestUrl = "person/" + String.valueOf(firstPerson.getPersonId());
            printRequest(3, requestUrl, HttpMethod.PUT, mediaType);
            String oldName = firstPerson.getFirstname();
            String newNameSeq = String.valueOf(Math.random());
            String newName = oldName + newNameSeq.substring(newNameSeq.length() -1);
            firstPerson.setFirstname(newName);
            Entity firstPersonEntity = Entity.entity(firstPerson, mediaType);
            res3 = put(requestUrl, firstPersonEntity, mediaType);
            PersonRepresentation updatedFirstPerson = res3.readEntity(PersonRepresentation.class);
            printResponse(res3, !updatedFirstPerson.getFirstname().equals(oldName));
            res3.close();

            // Step 3.4

            requestUrl = "person";
            printRequest(4, requestUrl, HttpMethod.POST, mediaType);
            Person newPerson = new Person();
            newPerson.setFirstname("Chuck");
            newPerson.setLastname("Norris");
            newPerson.setBirthdate(new SimpleDateFormat("yyyy-MM-dd").parse("1940-03-10")); // Chuck Norris' real birthday!
            Entity newPersonEntity = Entity.entity(newPerson, mediaType);
            Response res4 = post(requestUrl, newPersonEntity, mediaType);
            Person updatedNewPerson = res4.readEntity(Person.class);
            printResponse(res4, (res4.getStatus() == 200 || res4.getStatus() == 201 || res4.getStatus() == 202) && updatedNewPerson.getPersonId() >= 0);
            res4.close();

            // Step 3.5

            requestUrl = "person/" + String.valueOf(updatedNewPerson.getPersonId());
            printRequest(5, requestUrl, HttpMethod.DELETE, mediaType);
            Response res51 = delete(requestUrl, mediaType);
            Person deletedPerson = res51.readEntity(Person.class);
            res51.close();

            requestUrl = "person/" + String.valueOf(deletedPerson.getPersonId());
            printRequest(2, requestUrl, HttpMethod.GET, mediaType);
            Response res52 = get(requestUrl, mediaType);
            printResponse(res52, res52.getStatus() == Response.Status.NOT_FOUND.getStatusCode());
            res52.close();

            // Step 3.6

            requestUrl = "measureTypes";
            printRequest(9, requestUrl, HttpMethod.GET, mediaType);
            Response res6 = get(requestUrl, mediaType);
            measureTypes = res6.readEntity(MeasureTypesRepresentation.class);
            printResponse(res6, measureTypes.getMeasureTypes().size() > 2);
            res6.close();

            // Step 3.7

            MeasureHistoryRepresentation measureHistory;
            List<HealthProfileItem> savedMeasureHistory = new ArrayList<>();
            Measure savedMeasure = new Measure();
            for (Measure m : measureTypes.getMeasureTypes()) {
                requestUrl = "person/" + String.valueOf(firstPerson.getPersonId()) + "/" + m.getName();
                printRequest(6, requestUrl, HttpMethod.GET, mediaType);
                Response res7 = get(requestUrl, mediaType);
                measureHistory = res7.readEntity(MeasureHistoryRepresentation.class);
                Boolean hasMeasureHistory = measureHistory.getHistory() != null && measureHistory.getHistory().size() > 0;
                if (hasMeasureHistory) {
                    printResponse(res7, hasMeasureHistory);
                    savedMeasureHistory = measureHistory.getHistory();
                    savedMeasure = m;
                    res7.close();
                    break;
                }
                res7.close();
            }
            for (Measure m : measureTypes.getMeasureTypes()) {
                requestUrl = "person/" + String.valueOf(lastPerson.getPersonId()) + "/" + m.getName();
                printRequest(6, requestUrl, HttpMethod.GET, mediaType);
                Response res7 = get(requestUrl, mediaType);
                measureHistory = res7.readEntity(MeasureHistoryRepresentation.class);
                Boolean hasMeasureHistory = measureHistory.getHistory() != null && measureHistory.getHistory().size() > 0;
                /*
                printResponse(res7, hasMeasureHistory);
                if (hasMeasureHistory) {
                    savedMeasureHistory = measureHistory.getHistory();
                    res7.close();
                    break;
                }
                */
                res7.close();
            }


            if (savedMeasureHistory != null && savedMeasureHistory.size() > 0) {

                // Step 3.8

                HealthProfileItem item = savedMeasureHistory.get(0);
                requestUrl = "person/" + String.valueOf(firstPerson.getPersonId())
                        + "/" + savedMeasure.getName()
                        + "/" + item.getHealthProfileId();
                printRequest(7, requestUrl, HttpMethod.GET, mediaType);
                Response res8 = get(requestUrl, mediaType);
                printResponse(res8, res8.getStatus() == Response.Status.OK.getStatusCode());
                res8.close();

                // Step 3.9

                requestUrl = "person/" + String.valueOf(firstPerson.getPersonId()) + "/" + savedMeasure.getName();
                printRequest(6, requestUrl, HttpMethod.GET, mediaType);
                Response res91 = get(requestUrl, mediaType);
                measureHistory = res91.readEntity(MeasureHistoryRepresentation.class);
                printResponse(res91, res91.getStatus() == Response.Status.OK.getStatusCode());
                res91.close();
                int oldHistoryLength = 0;
                if (measureHistory != null && measureHistory.getHistory() != null) {
                    oldHistoryLength = measureHistory.getHistory().size();
                }

                printRequest(8, requestUrl, HttpMethod.POST, mediaType);
                HealthProfileItemRepresentation newHealthProfileItemRepresentation = new HealthProfileItemRepresentation();
                newHealthProfileItemRepresentation.setValue((float) 72.0);
                newHealthProfileItemRepresentation.setCreated(new SimpleDateFormat("yyyy-MM-dd").parse("2011-12-09"));
                Entity newHealthProfileItemEntity = Entity.entity(newHealthProfileItemRepresentation, mediaType);
                Response res92 = post(requestUrl, newHealthProfileItemEntity, mediaType);
                printResponse(res92, res92.getStatus() == Response.Status.OK.getStatusCode());
                res92.close();

                printRequest(6, requestUrl, HttpMethod.GET, mediaType);
                Response res93 = get(requestUrl, mediaType);
                measureHistory = res93.readEntity(MeasureHistoryRepresentation.class);
                int newHistoryLength = 0;
                if (measureHistory != null && measureHistory.getHistory() != null) {
                    newHistoryLength = measureHistory.getHistory().size();
                }
                printResponse(res93, oldHistoryLength != newHistoryLength);
                res93.close();
            }



        }
        catch (Exception e) {
            e.printStackTrace();
        }        
    }

    private static void printRequest(int requestNum, String path, String httpMethod, String mediaType) {
        System.out.println("Request #" + requestNum + ": "
                + httpMethod + " " + path +
                " Accept: " + mediaType +
                " Content-type: " + mediaType
        );
    }

    private static void printResponse(Response res, boolean result) {
        System.out.println("=> Result: " + (result ? "OK" : "ERROR"));
        System.out.println("=> HTTP Status: " + String.valueOf(res.getStatus()));
        System.out.println(res.readEntity(String.class));
    }

    public static Response get(String path, String mediaType) {
        Response res = service.path(path).request(mediaType).accept(mediaType).get();
        res.bufferEntity(); // To use readEntity multiple times
        return res;
    }

    public static Response put(String path, Entity entity, String mediaType) {
        Response res = service.path(path).request(mediaType).accept(mediaType).put(entity);
        res.bufferEntity();
        return res;
    }

    public static Response post(String path, Entity entity, String mediaType) {
        Response res = service.path(path).request(mediaType).accept(mediaType).post(entity);
        res.bufferEntity();
        return res;
    }

    public static Response delete(String path, String mediaType) {
        Response res = service.path(path).request(mediaType).accept(mediaType).delete();
        res.bufferEntity();
        return res;
    }
}

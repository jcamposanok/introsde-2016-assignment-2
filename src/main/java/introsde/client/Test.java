package introsde.client;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import introsde.rest.ehealth.models.*;
import introsde.rest.ehealth.representations.*;
import introsde.rest.ehealth.util.DateParser;
import org.glassfish.jersey.client.ClientConfig;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.*;
import java.net.URL;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;


public class Test {

    static String environment;
    static String logXmlPath;
    static String logJsonPath;

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

    private static void setLogger(String filepath) {
        File f = new File(filepath);
        if (f.getParentFile() != null) {
            f.getParentFile().mkdirs();
        }
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void log(String message, String filepath) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(filepath, true), true);
            out.write(message);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        environment = (args == null || args.length < 1) ? "local" : args[0];
        logXmlPath = (args == null || args.length < 2) ? "client-server-xml.log" : args[1];
        logJsonPath = (args == null || args.length < 3) ? "client-server-json.log" : args[2];

        setBaseUrl(environment);
        setLogger(logXmlPath);
        setLogger(logJsonPath);

        ClientConfig clientConfig = new ClientConfig();
        Client client = ClientBuilder.newClient(clientConfig);
        service = client.target(getBaseUrl());

        executeTestPlan(MediaType.APPLICATION_XML, logXmlPath);
        executeTestPlan(MediaType.APPLICATION_JSON, logJsonPath);
    }
    
    private static void executeTestPlan(String mediaType, String logFile) {
        String requestUrl;
        PersonRepresentation firstPerson = new PersonRepresentation();
        PersonRepresentation lastPerson = new PersonRepresentation();
        MeasureTypesRepresentation measureTypes;
        Response res1, res2, res3, res4, res51, res52, res6, res7;

        try {

            // Step 3.1

            requestUrl = "person";
            printRequest(1, requestUrl, HttpMethod.GET, mediaType, logFile);
            res1 = get(requestUrl, mediaType);
            PersonListRepresentation people = res1.readEntity(PersonListRepresentation.class);
            printResponse(res1, people != null && people.getPeople().size() >= 2, logFile);
            if (people != null && people.getPeople().size() >= 2) {
                firstPerson = people.getPeople().get(0);
                lastPerson = people.getPeople().get(people.getPeople().size() - 1);
            }
            res1.close();

            if (firstPerson != null && firstPerson.getPersonId() > 0) {

                // Step 3.2

                requestUrl = "person/" + String.valueOf(firstPerson.getPersonId());
                printRequest(2, requestUrl, HttpMethod.GET, mediaType, logFile);
                res2 = get(requestUrl, mediaType);
                printResponse(res2, res2.getStatus() == 200 || res2.getStatus() == 202, logFile);
                res2.close();

                // Step 3.3

                requestUrl = "person/" + String.valueOf(firstPerson.getPersonId());
                printRequest(3, requestUrl, HttpMethod.PUT, mediaType, logFile);
                String oldName = firstPerson.getFirstname();
                String newNameSeq = String.valueOf(Math.random());
                String newName = oldName + newNameSeq.substring(newNameSeq.length() - 1);
                firstPerson.setFirstname(newName);
                Entity firstPersonEntity = Entity.entity(firstPerson, mediaType);
                res3 = put(requestUrl, firstPersonEntity, mediaType);
                PersonRepresentation updatedFirstPerson = res3.readEntity(PersonRepresentation.class);
                printResponse(res3, !updatedFirstPerson.getFirstname().equals(oldName), logFile);
                res3.close();
            }

            // Step 3.4

            requestUrl = "person";
            printRequest(4, requestUrl, HttpMethod.POST, mediaType, logFile);
            Person newPerson = new Person();
            newPerson.setFirstname("Chuck");
            newPerson.setLastname("Norris");
            newPerson.setBirthdate(new SimpleDateFormat("yyyy-MM-dd").parse("1940-03-10")); // Chuck Norris' real birthday!
            Entity newPersonEntity = Entity.entity(newPerson, mediaType);
            res4 = post(requestUrl, newPersonEntity, mediaType);
            Person updatedNewPerson = res4.readEntity(Person.class);
            printResponse(res4,
                    (res4.getStatus() == 200 || res4.getStatus() == 201 || res4.getStatus() == 202)
                            && updatedNewPerson.getPersonId() >= 0,
                    logFile);
            res4.close();

            // Step 3.5

            requestUrl = "person/" + String.valueOf(updatedNewPerson.getPersonId());
            printRequest(5, requestUrl, HttpMethod.DELETE, mediaType, logFile);
            res51 = delete(requestUrl, mediaType);
            Person deletedPerson = res51.readEntity(Person.class);
            res51.close();

            requestUrl = "person/" + String.valueOf(deletedPerson.getPersonId());
            printRequest(2, requestUrl, HttpMethod.GET, mediaType, logFile);
            res52 = get(requestUrl, mediaType);
            printResponse(res52, res52.getStatus() == Response.Status.NOT_FOUND.getStatusCode(), logFile);
            res52.close();

            // Step 3.6

            requestUrl = "measureTypes";
            printRequest(9, requestUrl, HttpMethod.GET, mediaType, logFile);
            res6 = get(requestUrl, mediaType);
            measureTypes = res6.readEntity(MeasureTypesRepresentation.class);
            printResponse(res6, measureTypes.getMeasureTypes().size() > 2, logFile);
            Measure savedMeasure = new Measure();
            if (measureTypes != null && measureTypes.getMeasureTypes().size() > 0) {
                savedMeasure = measureTypes.getMeasureTypes().get(0);
            }
            res6.close();

            // Step 3.7

            MeasureHistoryRepresentation measureHistory;
            List<HealthProfileItem> savedMeasureHistory = new ArrayList<>();
            for (Measure m : measureTypes.getMeasureTypes()) {
                requestUrl = "person/" + String.valueOf(firstPerson.getPersonId()) + "/" + m.getName();
                printRequest(6, requestUrl, HttpMethod.GET, mediaType, logFile);
                res7 = get(requestUrl, mediaType);
                measureHistory = res7.readEntity(MeasureHistoryRepresentation.class);
                Boolean hasMeasureHistory = measureHistory.getHistory() != null && measureHistory.getHistory().size() > 0;
                if (hasMeasureHistory) {
                    printResponse(res7, hasMeasureHistory, logFile);
                    savedMeasureHistory = measureHistory.getHistory();
                    res7.close();
                    break;
                }
                res7.close();
            }
            for (Measure m : measureTypes.getMeasureTypes()) {
                requestUrl = "person/" + String.valueOf(lastPerson.getPersonId()) + "/" + m.getName();
                printRequest(6, requestUrl, HttpMethod.GET, mediaType, logFile);
                res7 = get(requestUrl, mediaType);
                measureHistory = res7.readEntity(MeasureHistoryRepresentation.class);
                Boolean hasMeasureHistory = measureHistory.getHistory() != null && measureHistory.getHistory().size() > 0;
                res7.close();
            }


            if (savedMeasureHistory != null && savedMeasureHistory.size() > 0) {

                // Step 3.8

                HealthProfileItem item = savedMeasureHistory.get(0);
                requestUrl = "person/" + String.valueOf(firstPerson.getPersonId())
                        + "/" + savedMeasure.getName()
                        + "/" + item.getHealthProfileId();
                printRequest(7, requestUrl, HttpMethod.GET, mediaType, logFile);
                Response res8 = get(requestUrl, mediaType);
                printResponse(res8, res8.getStatus() == Response.Status.OK.getStatusCode(), logFile);
                res8.close();

                // Step 3.9

                requestUrl = "person/" + String.valueOf(firstPerson.getPersonId()) + "/" + savedMeasure.getName();
                printRequest(6, requestUrl, HttpMethod.GET, mediaType, logFile);
                Response res91 = get(requestUrl, mediaType);
                measureHistory = res91.readEntity(MeasureHistoryRepresentation.class);
                printResponse(res91, res91.getStatus() == Response.Status.OK.getStatusCode(), logFile);
                res91.close();
                int oldHistoryLength = 0;
                if (measureHistory != null && measureHistory.getHistory() != null) {
                    oldHistoryLength = measureHistory.getHistory().size();
                }

                printRequest(8, requestUrl, HttpMethod.POST, mediaType, logFile);
                HealthProfileItemRepresentation newHealthProfileItemRepresentation = new HealthProfileItemRepresentation();
                newHealthProfileItemRepresentation.setValue((float) 72.0);
                newHealthProfileItemRepresentation.setCreated(new SimpleDateFormat("yyyy-MM-dd").parse("2011-12-09"));
                Entity newHealthProfileItemEntity = Entity.entity(newHealthProfileItemRepresentation, mediaType);
                Response res92 = post(requestUrl, newHealthProfileItemEntity, mediaType);
                newHealthProfileItemRepresentation = res92.readEntity(HealthProfileItemRepresentation.class);
                printResponse(res92, res92.getStatus() == Response.Status.OK.getStatusCode(), logFile);
                res92.close();

                printRequest(6, requestUrl, HttpMethod.GET, mediaType, logFile);
                Response res93 = get(requestUrl, mediaType);
                measureHistory = res93.readEntity(MeasureHistoryRepresentation.class);
                int newHistoryLength = 0;
                if (measureHistory != null && measureHistory.getHistory() != null) {
                    newHistoryLength = measureHistory.getHistory().size();
                }
                printResponse(res93, oldHistoryLength != newHistoryLength, logFile);
                res93.close();

                // Step 3.10

                if (newHealthProfileItemRepresentation != null) {
                    requestUrl = "person/" + String.valueOf(firstPerson.getPersonId())
                            + "/" + savedMeasure.getName() + "/" + newHealthProfileItemRepresentation.getHealthProfileId();
                    printRequest(10, requestUrl, HttpMethod.PUT, mediaType, logFile);
                    float oldValue = newHealthProfileItemRepresentation.getValue();
                    newHealthProfileItemRepresentation.setValue((float) 90);
                    newHealthProfileItemEntity = Entity.entity(newHealthProfileItemRepresentation, mediaType);
                    Response res10 = put(requestUrl, newHealthProfileItemEntity, mediaType);
                    newHealthProfileItemRepresentation = res10.readEntity(HealthProfileItemRepresentation.class);
                    printResponse(res10,
                            newHealthProfileItemRepresentation != null && newHealthProfileItemRepresentation.getValue() != oldValue,
                            logFile);
                    res10.close();
                }

            }

            if (savedMeasure != null) {

                // Step 3.11

                Format formatter = new SimpleDateFormat(DateParser.DEFAULT_FORMAT);
                Date after = new SimpleDateFormat(DateParser.DEFAULT_FORMAT).parse("2016-09-01");
                Date before = new SimpleDateFormat(DateParser.DEFAULT_FORMAT).parse("2016-12-31");
                requestUrl = "person/" + String.valueOf(firstPerson.getPersonId()) + "/" + savedMeasure.getName();
                String paramStr = "?before=" + formatter.format(before) + "&after=" + formatter.format(after);
                HashMap<String, Object> params = new HashMap<>();
                params.put("before", formatter.format(before));
                params.put("after", formatter.format(after));
                printRequest(11, requestUrl + paramStr, HttpMethod.GET, mediaType, logFile);
                Response res11 = get(requestUrl, mediaType, params);
                measureHistory = res11.readEntity(MeasureHistoryRepresentation.class);
                printResponse(res11, measureHistory != null && measureHistory.getHistory().size() > 0, logFile);
                res11.close();

                // Step 3.12

                int max = 70;
                int min = 65;
                requestUrl = "person";
                paramStr = "?measureType=" + savedMeasure.getName() + "&max=" + String.valueOf(max) + "&min=" + String.valueOf(min);
                params.clear();
                params.put("measureType", savedMeasure.getName());
                params.put("max", String.valueOf(max));
                params.put("min", String.valueOf(min));
                printRequest(12, requestUrl + paramStr, HttpMethod.GET, mediaType, logFile);
                Response res12 = get(requestUrl, mediaType, params);
                people = res12.readEntity(PersonListRepresentation.class);
                printResponse(res12, res12.getStatus() == Response.Status.OK.getStatusCode() && people.getPeople().size() > 0, logFile);
                res12.close();

            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }        
    }

    private static void printRequest(int requestNum, String path, String httpMethod, String mediaType, String logFilePath) {
        String message = System.lineSeparator()
                + "Request #" + requestNum + ": "
                + httpMethod + " " + path +
                " Accept: " + mediaType +
                " Content-type: " + mediaType;
        System.out.println(message);
        if (logFilePath != null) {
            log(message, logFilePath);
        }
    }

    private static void printRequest(int requestNum, String path, String httpMethod, String mediaType) {
        printRequest(requestNum, path, httpMethod, mediaType, null);
    }

    private static void printResponse(Response res, boolean result, String logFilePath) {
        String message = System.lineSeparator()
                + "=> Result: " + (result ? "OK" : "ERROR") + System.lineSeparator()
                + "=> HTTP Status: " + String.valueOf(res.getStatus()) + System.lineSeparator() 
                + res.readEntity(String.class);
        System.out.println(message);
        if (logFilePath != null) {
            log(message, logFilePath);
        }
    }

    private static void printResponse(Response res, boolean result) {
        printResponse(res, result, null);
    }

    public static Response get(String path, String mediaType, HashMap<String, Object> params) {
        WebTarget target = service.path(path);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            target.queryParam(entry.getKey(), entry.getValue());
        }
        Response res = target.request(mediaType).accept(mediaType).get();
        res.bufferEntity(); // To use readEntity multiple times
        return res;
    }

    public static Response get(String path, String mediaType) {
        return get(path, mediaType, new HashMap<>());
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

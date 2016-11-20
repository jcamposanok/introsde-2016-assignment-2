package introsde.client;

import introsde.rest.ehealth.models.Measure;
import introsde.rest.ehealth.models.MeasureTypes;
import introsde.rest.ehealth.models.Person;
import org.glassfish.jersey.client.ClientConfig;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBElement;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Random;

/**
 * Created by JC on 19/11/2016.
 */
public class Test {

    static String baseUrl;
    static WebTarget service;

    static int requestNum;
    static Person firstPerson;
    static Person lastPerson;
    static MeasureTypes measureTypes;

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

        try {
            // Step 3.1
            printRequest(1, "person", HttpMethod.GET, MediaType.APPLICATION_XML);
            Response res1 = get("person", MediaType.APPLICATION_XML);
            List<Person> people = res1.readEntity(new GenericType<List<Person>>() {});
            printResponse(res1, people.size() >= 2);
            firstPerson = people.get(0);
            lastPerson = people.get(people.size() - 1);
            // System.out.println("First person ID: " + String.valueOf(firstPersonId));
            // System.out.println("Last person ID: " + String.valueOf(lastPersonId));
            res1.close();

            // Step 3.2
            printRequest(2, "person/" + String.valueOf(firstPerson.getIdPerson()), HttpMethod.GET, MediaType.APPLICATION_XML);
            Response res2 = get("person/" + String.valueOf(firstPerson.getIdPerson()), MediaType.APPLICATION_XML);
            printResponse(res2, res2.getStatus() == 200 || res2.getStatus() == 202);
            res2.close();

            // Step 3.3
            printRequest(2, "person/" + String.valueOf(firstPerson.getIdPerson()), HttpMethod.PUT, MediaType.APPLICATION_XML);
            String oldName = firstPerson.getFirstname();
            String newNameSeq = String.valueOf(Math.random());
            String newName = oldName + newNameSeq.substring(newNameSeq.length() -1);
            firstPerson.setFirstname(newName);
            Entity firstPersonEntity = Entity.entity(firstPerson, MediaType.APPLICATION_XML);
            Response res3 = put("person/" + String.valueOf(firstPerson.getIdPerson()), firstPersonEntity, MediaType.APPLICATION_XML);
            Person updatedFirstPerson = res3.readEntity(Person.class);
            printResponse(res3, updatedFirstPerson.getFirstname().equals(oldName));
            res3.close();

            // Step 3.4
            printRequest(4, "person", HttpMethod.POST, MediaType.APPLICATION_XML);
            Person newPerson = new Person();
            newPerson.setFirstname("Chuck");
            newPerson.setLastname("Norris");
            newPerson.setBirthdate(new SimpleDateFormat("yyyy-MM-dd").parse("1940-03-10")); // Chuck Norris' real birthday!
            Entity newPersonEntity = Entity.entity(newPerson, MediaType.APPLICATION_XML);
            Response res4 = post("person", newPersonEntity, MediaType.APPLICATION_XML);
            Person updatedNewPerson = res4.readEntity(Person.class);
            printResponse(res4, (res4.getStatus() == 200 || res4.getStatus() == 201 || res4.getStatus() == 202) && updatedNewPerson.getIdPerson() >= 0);
            res4.close();

            // Step 3.5
            printRequest(5, "person/" + String.valueOf(updatedNewPerson.getIdPerson()), HttpMethod.DELETE, MediaType.APPLICATION_XML);
            Response res51 = delete("person/" + String.valueOf(updatedNewPerson.getIdPerson()), MediaType.APPLICATION_XML);
            Person deletedPerson = res51.readEntity(Person.class);
            res51.close();

            printRequest(2, "person/" + String.valueOf(deletedPerson.getIdPerson()), HttpMethod.GET, MediaType.APPLICATION_XML);
            Response res52 = get("person/" + String.valueOf(deletedPerson.getIdPerson()), MediaType.APPLICATION_XML);
            printResponse(res52, res2.getStatus() == 404);
            res52.close();

            // Step 3.6
            printRequest(9, "measureTypes", HttpMethod.GET, MediaType.APPLICATION_XML);
            Response res6 = get("measureTypes", MediaType.APPLICATION_XML);
            measureTypes = res6.readEntity(MeasureTypes.class);
            printResponse(res6, measureTypes.getMeasureTypes().size() > 2);
            res6.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }





        // get("person", MediaType.APPLICATION_XML);

    }

    public static void printRequest(int requestNum, String path, String httpMethod, String mediaType) {
        System.out.println("Request #" + requestNum + ": "
                + httpMethod + " " + path +
                " Accept: " + mediaType +
                " Content-type: " + mediaType
        );
    }

    public static void printResponse(Response res, boolean result) {
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

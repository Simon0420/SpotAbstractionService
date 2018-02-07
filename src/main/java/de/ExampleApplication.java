package de;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import de.domains.domainAux.GpsPoint;
import de.domains.domainAux.Route;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.UUID;

public class ExampleApplication {

    String serverip = "134.155.49.88";

    public static void main(String[] args) {
        ExampleApplication ex = new ExampleApplication();

        //send random generated linear route json string
        System.out.println("Process random generated linear route json string.");
        ex.processRandomRoute();

        //send route object
        System.out.println("Process route object.");
        Route route = ex.createRandomRouteObject();
        ex.processRouteObject(route);

        //request spots stored at server
        System.out.println("Get Spots in db:");
        ex.requestSpotsAlternative();

        //request relations between spots
        System.out.println("Get relation between spots in db:");
        ex.requestSpotsRelationAlternative();

        //request spots stored at server - DEPRECATED
        //System.out.println("Get Spot with id=0 in db:");
        //ex.returnSpot(0);
    }

    public void processRouteObject(Route route){
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        try {
            String input = ow.writeValueAsString(route);
            //System.out.println(input);

            URL url = new URL("http://localhost:5435/processRoute");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();

            /*if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }*/

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String output;
            System.out.println("Output from Server: ");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }

            conn.disconnect();
            System.out.println("done");
        } catch (FileNotFoundException fe){
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Route createRandomRouteObject(){
        Route route = new Route();

        double latitude = 0;
        double longitude = 0;
        long date = 0;
        double heading = 0;
        double speed = 0;
        double acceleration = 0;

        Random random = new Random();
        UUID.randomUUID().toString();
        latitude = random.nextDouble();
        longitude = random.nextDouble();
        date = random.nextLong();
        heading = random.nextDouble();
        speed = random.nextDouble();
        acceleration = random.nextDouble();
        long id = 0;
        route.route = new GpsPoint[50];
        route.id = random.nextLong();

        for (int i = 0; i < 50; i++) {
            latitude += 0.00001;
            date += 1000;
            GpsPoint gpsPoint = new GpsPoint();
            gpsPoint.id = id;
            gpsPoint.latitude = latitude;
            gpsPoint.longitude = longitude;
            gpsPoint.date = date;
            gpsPoint.heading = heading;
            gpsPoint.speed = speed;
            gpsPoint.acceleration = acceleration;
            route.route[i] = gpsPoint;
            id++;
        }
        return route;
    }

    /**
     * Sends a random generated linear route to the server
     */
    public void processRandomRoute(){
        double latitude = 0;
        double longitude = 0;
        long date = 0;
        double heading = 0;
        double speed = 0;
        double acceleration = 0;

        Random random = new Random();
        UUID.randomUUID().toString();
        latitude = random.nextDouble();
        longitude = random.nextDouble();
        date = random.nextLong();
        heading = random.nextDouble();
        speed = random.nextDouble();
        acceleration = random.nextDouble();

        // build json string

        long route_id = Math.abs(random.nextInt());
        String input = "{ \n" +
                "\t\"id\":"+route_id+",\n"+
                "\t\"route\":[";
        long id = 0;

        for (int i = 0; i < 300; i++) {
            latitude += 0.00001;
            date += 1000;
            id++;

            if (i < 300 - 1) {
                input += "{\"id\":"+id+",\"latitude\":" + latitude + ",\"longitude\":" + longitude + ",\"date\":" + date + ",\"heading\":" + heading + ",\"speed\":" + speed + ",\"acceleration\":" + acceleration +" },";
            } else {
                input += "{\"id\":"+id+",\"latitude\":" + latitude + ",\"longitude\":" + longitude + ",\"date\":" + date + ",\"heading\":" + heading + ",\"speed\":" + speed + ",\"acceleration\":" + acceleration +"}]}";
            }
        }
        //System.out.println(input);

        // send json string

        try {
            URL url = new URL("http://localhost:5435/processRoute");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();

            /*if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }*/

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String output;
            System.out.println("Output from Server: ");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }

            conn.disconnect();
            System.out.println("done");
        } catch (FileNotFoundException fe){
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void requestSpotsAlternative(){
        try {
            URL url = new URL("http://localhost:5435/spotservice/getAllSpotsJsonAlternative");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("GET");

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output;
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }
            conn.disconnect();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }
    }

    public void requestSpotsRelationAlternative(){
        try {
            URL url = new URL("http://localhost:5435/spotservice/getAllSpotsRelationsJsonAlternative");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("GET");

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output;
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }
            conn.disconnect();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }
    }

    /**
     * Request spot with specific id with relations at server, returns a spot object.
     * Not working, failure in deserialization of spots at server.
     * @param id
     */
    @Deprecated
    public void returnSpot(int id){
        try {
            URL url = new URL("http://localhost:5435/spotservice/getSpotJson?id="+id);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("GET");

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String output;
            System.out.println("Output from Server: ");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }
            conn.disconnect();

        } catch (Exception e) {
            //e.printStackTrace();
            return;
        }
    }

    /**
     * Request all spots including relations at server, returns an array of spot objects.
     * Currently not working, failure in deserialization of spots at server.
     */
    @Deprecated
    public void returnSpots(){
        try {
            URL url = new URL("http://localhost:5435/spotservice/getAllSpotsJson");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("GET");

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String output;
            System.out.println("Output from Server: ");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }
            conn.disconnect();

        } catch (Exception e) {
            //e.printStackTrace();
            return;
        }
    }

}

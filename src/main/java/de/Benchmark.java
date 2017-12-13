package de;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.UUID;

public class Benchmark {
    public static void main(String[] args){
        Benchmark bench = new Benchmark();
        try {
            bench.createSpotStructure();
        }catch(Exception e){

        }
        System.out.println("job done");
    }

    public void createSpotStructure(){
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

        long route_id = Math.abs(random.nextInt());
        String input = "{ \n" +
                "\t\"id\":"+route_id+",\n"+
                "\t\"route\":[";
        long id = 0;

        for (int i = 0; i < 300; i++) {
            latitude += 0.0001;
            date += 2000;
            id++;

            if (i < 300 - 1) {
                input += "{\"id\":"+id+",\"latitude\":" + latitude + ",\"longitude\":" + longitude + ",\"date\":" + date + ",\"heading\":" + heading + ",\"speed\":" + speed + ",\"acceleration\":" + acceleration +" },";
            } else {
                input += "{\"id\":"+id+",\"latitude\":" + latitude + ",\"longitude\":" + longitude + ",\"date\":" + date + ",\"heading\":" + heading + ",\"speed\":" + speed + ",\"acceleration\":" + acceleration +"}]}";
            }
        }

        try {
            System.out.println(input);

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
            //System.out.println("Output from Server: ");
            while ((output = br.readLine()) != null) {
                //System.out.println(output);
            }

            conn.disconnect();

        } catch (Exception e) {

            //e.printStackTrace();
            return;

        }
    }
}

package de.domains.domain;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import de.domains.domainAux.GpsPoint;
import de.geodesy.GeoDesy;
import de.services.Line;
import de.services.GPSDataProcessor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Spot implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    public Long spotID;

    public static double headDiffThreshold = 30;
    public static double distThreshold = 8;
    public static int stdRadius = 25;

    public double longitude; //!
    public double latitude; //!

    @Column(columnDefinition="geometry(Point,4326)")
    public Point location;

    public double spotHeading; //!
    public boolean intersection; //!

    public int numberOfNeighbours;

    @ManyToMany
    public List<Spot> neighbors;

    public int numberCenterCalcPoints; //!
    public double latitudeSum; //!
    public double longitudeSum; //!

    @Column(nullable = true)
    public int headCalcPoints; //!
    @Column(nullable = true)
    public double headSum; //!

    @Column(nullable = true)
    public boolean nodeProcessed;
    @Column(nullable = true)
    public boolean edgeProcessed;
    @Column(nullable = true)
    public boolean weightProcessed;

    public Spot(){}

    /**
     * Constructs a new Spot
     *
     * @param center :GPS_plus center-point of the spot
     * @param head   :heading direction of the spot
     */
    public Spot(GpsPoint center, double head) {
        this.latitude = center.getLatitude();
        this.longitude = center.getLongitude();
        if (head >= 180.0) {
            head = head - 180.0;
        }
        this.spotHeading = head;
        this.neighbors = new ArrayList<Spot>();
        this.intersection = false;
        this.headSum = head;
        this.headCalcPoints = 1;
        this.latitudeSum = center.getLatitude();
        this.longitudeSum = center.getLongitude();
        GeometryFactory gf = new GeometryFactory();
        Coordinate coord = new Coordinate(longitude, latitude );
        Point point = gf.createPoint(coord);
        this.location = point;
        this.numberCenterCalcPoints = 1;
    }

    /**
     * Adds a main point, that is used to update the spot, and calculates
     * heading & center-point
     *
     * @param point :GPS_plus object
     */
    public void updateSpot(GpsPoint point) {
        double phead = point.getHeading();
        if (phead >= 180.0) {
            phead = phead - 180.0;
        }
        double absHeadDiff = Math.abs((phead - spotHeading));
        if (absHeadDiff < headDiffThreshold) {
            this.spotHeading = this.calcSpotHeading(point.getHeading());
        }
        double distance = GPSDataProcessor.calcDistance(point.getLatitude(), point.getLongitude(), latitude, longitude);
        if (distance < distThreshold) {
            Line fromSpotCenter = calcLine(latitude, longitude, spotHeading + 90.0);
            Line fromNewPoint = calcLine(point, spotHeading);
            if (fromNewPoint.isVertical() || fromSpotCenter.isVertical()) {
                GpsPoint calcPoint = calcIntersectionVertical(fromSpotCenter, fromNewPoint);
                GpsPoint centerupd = this.clacSpotCenter(calcPoint);
                this.latitude = centerupd.getLatitude();
                this.longitude = centerupd.getLongitude();
            } else {
                GpsPoint calcPoint = calcIntersection(fromSpotCenter, fromNewPoint);
                GpsPoint centerupd = this.clacSpotCenter(calcPoint);
                this.latitude = centerupd.getLatitude();
                this.longitude = centerupd.getLongitude();
            }
        }
    }

    /**
     * Calculates a new spot heading
     *
     * @return double :new spot heading
     */
    private double calcSpotHeading(double head) {
        this.headCalcPoints++;
        if (head >= 180.0) {
            head = head - 180.0;
        }
        this.headSum = this.headSum + head;
        double avghead = (headSum / headCalcPoints);
        return avghead;
    }

    /**
     * Calculates a new spot center
     *
     * @return GPS_plus :new spot center
     */
    private GpsPoint clacSpotCenter(GpsPoint point) {
        this.numberCenterCalcPoints++;
        this.latitudeSum = this.latitudeSum + point.getLatitude();
        this.longitudeSum = this.longitudeSum + point.getLongitude();
        double avgLat = latitudeSum / numberCenterCalcPoints;
        double avgLong = longitudeSum / numberCenterCalcPoints;

        GpsPoint newCenterPoint = new GpsPoint();
        newCenterPoint.setLatitude(avgLat);
        newCenterPoint.setLongitude(avgLong);
        newCenterPoint.setHeading(this.spotHeading);
        return newCenterPoint;
    }

    private GpsPoint clacSpotCenter(float latitude, float longitude) {
        this.numberCenterCalcPoints++;
        this.latitudeSum = this.latitudeSum + latitude;
        this.longitudeSum = this.longitudeSum + longitude;
        double avgLat = latitudeSum / numberCenterCalcPoints;
        double avgLong = longitudeSum / numberCenterCalcPoints;

        GpsPoint newCenterPoint = new GpsPoint();
        newCenterPoint.setLatitude(avgLat);
        newCenterPoint.setLongitude(avgLong);
        newCenterPoint.setHeading(this.spotHeading);
        return newCenterPoint;
    }


    /**
     * Adds a new neighbor Spot to the Spot
     *
     * @param spot :Spot to add as neighbor
     */
    public boolean addNeighborAlternative(Spot spot) {
        List<Spot> neighbors = this.getNeighbors();
        boolean contained = false;

        for (int i = 0; i < neighbors.size(); i++) {
            if (spot.getSpotID().equals(neighbors.get(i))) {
                contained = true;
            }
        }

        if (!contained) {
            neighbors.add(spot);
            numberOfNeighbours++;
            this.setNeighbors(neighbors);

            if (neighbors.size() >= 3) {
                this.setIntersection(true);
                //System.out.println("SpotID: "+this.getSpotID());
                //System.out.println("NBS:");
                for(int i = 0; i < neighbors.size(); i++){
                    //System.out.println(neighbors.get(i).getSpotID());
                }
            }
            if(this.numberOfNeighbours >= 3){
                //this.setIntersection(true);
            }
            return true;
        }else{
            return false;
        }
    }

    /**
     * Adds a new neighbor Spot to the Spot
     *
     * spot :Spot to add as neighbor
     */
    public void addNeighbor(Spot spot) {
        if (spot != null) {
            double distance = GPSDataProcessor.calcDistance(spot.latitude, spot.longitude, latitude, longitude);
            if (distance >= 30 && distance <= 150) {
                if (!spot.getSpotID().equals(this.spotID)) {
                    List<Spot> neighbors = this.getNeighbors();
                    boolean contained = false;

                    for (int i = 0; i < neighbors.size(); i++) {
                        if (spot.getSpotID().equals(neighbors.get(i))) {
                            contained = true;
                        }
                    }
                    if (!contained) {
                        neighbors.add(spot);
                        //if (neighbors.size() >= 3) {
                        //this.setIntersection(true);
                        //}
                        numberOfNeighbours++;
                        if(this.numberOfNeighbours >= 3){
                            this.setIntersection(true);
                        }
                        this.setNeighbors(neighbors);

                    }
                }
            }
        }
    }

    /**
     * Checks if a point is in the range of the spot
     *
     * @param point :GPS_plus object
     * @return true if its in range, else false
     */
    public boolean inRange(GpsPoint point) {
        double dist = GPSDataProcessor.calcDistance(latitude, longitude, point.getLatitude(), point.getLongitude());
        if (dist <= stdRadius) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Calculates a line out of a point and a heading
     *
     * @param point :GPS_plus object for calculation
     * @param head  :Heading in double for calculation
     * @return Line - represented as a linear function
     */
    private Line calcLine(GpsPoint point, double head) {
        GpsPoint temp = new GpsPoint();
        float[] result = new float[3];
        // creates a new point after a arbitrary distance in head direction to
        // calculate a line
        result = GeoDesy.destinationCalculationGEODESY(point.getLatitude(), point.getLongitude(), 50f, (float) head);
        temp.setLatitude(result[1]);
        temp.setLongitude(result[2]);

        Line l = new Line();
        double numerator = (point.getLongitude() - temp.getLongitude());
        double denominator = (point.getLatitude() - temp.getLatitude());
        // set slope m
        l.setM(numerator / denominator);
        // set y-intercept
        l.setB(point.getLongitude() - (l.getM() * point.getLatitude()));
        // check result for correctness and if its a vertical line
        double check = temp.getLongitude() - (l.getM() * temp.getLatitude());
        if (denominator == 0) {
            l.setVertical(true);
            l.setX(point.getLatitude());
        } else {
            double difference = Math.abs(l.getB() - check);
            if (difference >
                    0.00000001) {
                System.out.println("line calc went wrong!");
                System.out.println(l.getB());
                System.out.println(check);
                return null;
            }
        }
        return l;
    }

    private Line calcLine(double lati, double longi, double head) {
        GpsPoint temp = new GpsPoint();
        float[] result = new float[3];
        // creates a new point after a arbitrary distance in head direction to
        // calculate a line
        result = GeoDesy.destinationCalculationGEODESY(lati, longi, 50f, (float) head);
        temp.setLatitude(result[1]);
        temp.setLongitude(result[2]);

        Line l = new Line();
        double numerator = (longi - temp.getLongitude());
        double denominator = (lati - temp.getLatitude());
        // set slope m
        l.setM(numerator / denominator);
        // set y-intercept
        l.setB(longi - (l.getM() * lati));
        // check result for correctness and if its a vertical line
        double check = temp.getLongitude() - (l.getM() * temp.getLatitude());
        if (denominator == 0) {
            l.setVertical(true);
            l.setX(lati);
        } else {
            double difference = Math.abs(l.getB() - check);
            if (difference >
                    0.00000001) {
                System.out.println("line calc went wrong!");
                System.out.println(l.getB());
                System.out.println(check);
                return null;
            }
        }
        return l;
    }

    /**
     * Calculates the intersection of two Lines
     *
     * @param l1 :Line 1
     * @param l2 :Line 2
     * @return GPS_plus - intersection coordinates
     */
    private GpsPoint calcIntersection(Line l1, Line l2) {
        double latitude = (l1.getB() - l2.getB()) / (l2.getM() - l1.getM());
        double longitude = (l1.getM() * latitude) + l1.getB();
        double check = (l2.getM() * latitude) + l2.getB();
        double diff = Math.abs(longitude - check);
        if (diff > 0.0001) {
            System.out.println(longitude);
            System.out.println(check);
            System.out.println("intersection failed");
            return null;
        }
        GpsPoint temp = new GpsPoint();
        temp.setLatitude((float) latitude);
        temp.setLongitude((float) longitude);
        return temp;
    }

    /**
     * Calculates the intersection of two Lines if one of them is a vertical
     * line
     *
     * @param l1 :Line 1
     * @param l2 :Line 2
     * @return GPS_plus - intersection coordinates
     */
    private GpsPoint calcIntersectionVertical(Line l1, Line l2) {
        double latitude = 0.0;
        double longitude = 0.0;
        if (l1.isVertical() && l2.isVertical()) {
            return null;
        } else if (l1.isVertical()) {
            latitude = l1.getX();
            longitude = (l2.getM() * latitude) + l2.getB();
        } else if (l2.isVertical()) {
            latitude = l2.getX();
            longitude = (l1.getM() * latitude) + l1.getB();
        }
        GpsPoint temp = new GpsPoint();
        temp.setLatitude((float) latitude);
        temp.setLongitude((float) longitude);
        return temp;
    }

    public Long getSpotID() {
        return spotID;
    }

    public void setSpotID(Long spotID) {
        this.spotID = spotID;
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    public static double getHeadDiffThreshold() {
        return headDiffThreshold;
    }

    public static void setHeadDiffThreshold(double headDiffThreshold) {
        Spot.headDiffThreshold = headDiffThreshold;
    }

    public static double getDistThreshold() {
        return distThreshold;
    }

    public static void setDistThreshold(double distThreshold) {
        Spot.distThreshold = distThreshold;
    }

    public static int getStdRadius() {
        return stdRadius;
    }

    public static void setStdRadius(int stdRadius) {
        Spot.stdRadius = stdRadius;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getSpotHeading() {
        return spotHeading;
    }

    public void setSpotHeading(double spotHeading) {
        this.spotHeading = spotHeading;
    }

    public boolean isIntersection() {
        return intersection;
    }

    public void setIntersection(boolean intersection) {
        this.intersection = intersection;
    }

    public int getNumberOfNeighbours() {
        return numberOfNeighbours;
    }

    public void setNumberOfNeighbours(int numberOfNeighbours) {
        this.numberOfNeighbours = numberOfNeighbours;
    }

    public List<Spot> getNeighbors() {
        return neighbors;
    }

    public void setNeighbors(List<Spot> neighbors) {
        this.neighbors = neighbors;
    }

    public int getNumberCenterCalcPoints() {
        return numberCenterCalcPoints;
    }

    public void setNumberCenterCalcPoints(int numberCenterCalcPoints) {
        this.numberCenterCalcPoints = numberCenterCalcPoints;
    }

    public double getLatitudeSum() {
        return latitudeSum;
    }

    public void setLatitudeSum(double latitudeSum) {
        this.latitudeSum = latitudeSum;
    }

    public double getLongitudeSum() {
        return longitudeSum;
    }

    public void setLongitudeSum(double longitudeSum) {
        this.longitudeSum = longitudeSum;
    }

    public int getHeadCalcPoints() {
        return headCalcPoints;
    }

    public void setHeadCalcPoints(int headCalcPoints) {
        this.headCalcPoints = headCalcPoints;
    }

    public double getHeadSum() {
        return headSum;
    }

    public void setHeadSum(double headSum) {
        this.headSum = headSum;
    }

    public boolean isNodeProcessed() {
        return nodeProcessed;
    }

    public void setNodeProcessed(boolean nodeProcessed) {
        this.nodeProcessed = nodeProcessed;
    }

    public boolean isEdgeProcessed() {
        return edgeProcessed;
    }

    public void setEdgeProcessed(boolean edgeProcessed) {
        this.edgeProcessed = edgeProcessed;
    }

    public boolean isWeightProcessed() {
        return weightProcessed;
    }

    public void setWeightProcessed(boolean weightProcessed) {
        this.weightProcessed = weightProcessed;
    }
}

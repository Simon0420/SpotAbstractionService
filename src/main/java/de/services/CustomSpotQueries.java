package de.services;

import de.domain.Spot;
import org.hibernate.Query;
import org.hibernate.Session;

public class CustomSpotQueries {

    public void addSpot(Spot s, Session session){
        String querystring = "INSERT INTO spot ("+
                "location, " +
                "latitude, " +
                "longitude, " +
                "spotHeading, " +
                "intersection, " +
                "numberOfNeighbours, " +
                "neighbors, " +
                "latitudeSum, " +
                "longitudeSum, " +
                "numberCenterCalcPoints, " +
                "headSum, " +
                "headCalcPoints, " +
                "nodeProcessed, " +
                "edgeProcessed) " +
                "VALUES ("+
                "ST_SetSRID(ST_MakePoint("+s.latitude+", "+s.longitude+"), 4326),"+
                s.latitude+", "+
                s.longitude+", "+
                s.spotHeading+", "+
                s.intersection+", "+
                s.numberOfNeighbours+", "+
                s.neighbors+", "+
                s.latitudeSum+", "+
                s.longitudeSum+", "+
                s.numberCenterCalcPoints+", "+
                s.headSum+", "+
                s.headCalcPoints+", "+
                s.nodeProcessed+", "+
                s.edgeProcessed+", "+
                s.spotID+"" +
                ")";
        //perform db operations
        Query q = session.createSQLQuery(querystring);
        q.executeUpdate();
    }

    public void updateSpot(Spot s, Session session){
        String querystring = "UPDATE spot SET "+
                "location = " + "ST_SetSRID(ST_MakePoint("+s.latitude+", "+s.longitude+"), 4326),"+
                "latitude = " + s.latitude +
                ", longitude = " + s.longitude +
                ", spotHeading = " + s.spotHeading +
                ", intersection = " + s.intersection +
                ", numberOfNeighbours = " + s.numberOfNeighbours +
                ", neighbors = " + s.neighbors +
                ", latitudeSum = " + s.latitudeSum +
                ", longitudeSum = " + s.longitudeSum +
                ", numberCenterCalcPoints = " + s.numberCenterCalcPoints +
                ", headSum = " + s.headSum +
                ", headCalcPoints = " + s.headCalcPoints +
                ", nodeProcessed = " + s.nodeProcessed +
                ", edgeProcessed = " + s.edgeProcessed +
                "WHERE spotID = "+s.spotID;
        //perform db operations
        Query q = session.createSQLQuery(querystring);
        q.executeUpdate();
    }

    public void addNeighbour(Spot spot1, Spot spot2, Session session){
        String querystring = "UPDATE spot SET "+
                "intersection = " + spot1.intersection +
                ", numberOfNeighbours = " + spot1.numberOfNeighbours +
                ", neighbors = " + spot1.neighbors +
                "WHERE spotID = "+spot1.spotID;
        //perform db operations
        Query q = session.createSQLQuery(querystring);
        q.executeUpdate();

        querystring = "UPDATE spot SET "+
                "intersection = " + spot2.intersection +
                ", numberOfNeighbours = " + spot2.numberOfNeighbours +
                ", neighbors = " + spot2.neighbors +
                "WHERE spotID = "+spot2.spotID;
        //perform db operations
        q = session.createSQLQuery(querystring);
        q.executeUpdate();
    }
}

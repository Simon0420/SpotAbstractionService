package de.services;

import de.domains.domain.Spot;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.util.List;

public class CustomSpotQueries {

    public long addSpot(Spot s, Session session){
        String querystring = "INSERT INTO spot ("+
                "location, " +
                "latitude, " +
                "longitude, " +
                "spotHeading, " +
                "intersection, " +
                "numberOfNeighbours, " +
                "latitudeSum, " +
                "longitudeSum, " +
                "numberCenterCalcPoints, " +
                "headSum, " +
                "headCalcPoints, " +
                "nodeProcessed, " +
                "edgeProcessed" +
                ") " +
                "VALUES ("+
                "ST_SetSRID(ST_MakePoint("+s.latitude+", "+s.longitude+"), 4326), "+
                s.latitude+", "+
                s.longitude+", "+
                s.spotHeading+", "+
                s.intersection+", "+
                s.numberOfNeighbours+", "+
                s.latitudeSum+", "+
                s.longitudeSum+", "+
                s.numberCenterCalcPoints+", "+
                s.headSum+", "+
                s.headCalcPoints+", "+
                s.nodeProcessed+", "+
                s.edgeProcessed+");";
        //perform db operations
        Query q = session.createSQLQuery(querystring);
        q.executeUpdate();

        /**
        String query = "Select spotid FROM spot WHERE latitude = "+s.latitude+" AND longitude = "+s.longitude+" AND spotHeading = "+s.spotHeading+";";
        q = session.createSQLQuery(querystring);
        q.executeUpdate();
        List list = q.list();
        System.out.println(list.get(0));*/

        return 0;
    }

    public void updateSpot(Spot s, Session session){
        String querystring = "UPDATE spot SET "+
                "location = " + "ST_SetSRID(ST_MakePoint("+s.latitude+", "+s.longitude+"), 4326),"+
                "latitude = " + s.latitude +
                ", longitude = " + s.longitude +
                ", spotHeading = " + s.spotHeading +
                ", intersection = " + s.intersection +
                ", numberOfNeighbours = " + s.numberOfNeighbours +
                ", latitudeSum = " + s.latitudeSum +
                ", longitudeSum = " + s.longitudeSum +
                ", numberCenterCalcPoints = " + s.numberCenterCalcPoints +
                ", headSum = " + s.headSum +
                ", headCalcPoints = " + s.headCalcPoints +
                ", nodeProcessed = " + s.nodeProcessed +
                ", edgeProcessed = " + s.edgeProcessed +
                " WHERE spotID = "+s.spotID;
        //perform db operations
        Query q = session.createSQLQuery(querystring);
        q.executeUpdate();
    }

    public void addNeighbour(Spot spot1, Spot spot2, Session session){
        String querystring = "UPDATE spot SET "+
                "intersection = " + spot1.intersection +
                ", numberOfNeighbours = " + spot1.numberOfNeighbours +
                "WHERE spotID = "+spot1.spotID;
        //perform db operations
        Query q = session.createSQLQuery(querystring);
        q.executeUpdate();

        querystring = "UPDATE spot SET "+
                "intersection = " + spot2.intersection +
                ", numberOfNeighbours = " + spot2.numberOfNeighbours +
                "WHERE spotID = "+spot2.spotID;
        //perform db operations
        q = session.createSQLQuery(querystring);
        q.executeUpdate();

        addNeighbourRelation(spot1,spot2,session);
    }

    public void addNeighbourRelation(Spot spot1, Spot spot2, Session session){
        String querystring = "INSERT INTO spot_spot (spot_spotid, neighbours_spotid) VALUES ("+spot1.spotID+", "+spot2.spotID+"), ("+spot2.spotID+", "+spot1.spotID+")"+";";
        //perform db operations
        Query q = session.createSQLQuery(querystring);
        q.executeUpdate();
    }


}

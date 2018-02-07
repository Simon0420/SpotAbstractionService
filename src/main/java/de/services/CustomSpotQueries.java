package de.services;

import de.domains.domain.Spot;
import de.repositories.preDBRepositories.SpotRepository;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.type.ListType;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CustomSpotQueries {

    @Autowired
    SpotRepository sr;

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

        Query selectQuery;
        String queryString = "Select spotid FROM spot WHERE latitude = "+s.latitude+" AND longitude = "+s.longitude+" AND spotHeading = "+s.spotHeading+";";
        selectQuery = session.createSQLQuery(queryString);
        List list = selectQuery.list();
        BigInteger id = (BigInteger)list.get(0);
        return id.intValue();
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
        String querystring = "INSERT INTO spot_spot (spot_spotid, neighbors_spotid) VALUES ("+spot1.spotID+", "+spot2.spotID+"), ("+spot2.spotID+", "+spot1.spotID+")"+";";
        //perform db operations
        Query q = session.createSQLQuery(querystring);
        q.executeUpdate();
    }

    public List<Spot> getClosestSpot(double latitude, double longitude, Session session){
        Query selectQuery;
        String query = "SELECT" +
                " spotid " +
                "FROM spot " +
                "ORDER BY location <-> ST_SetSRID(ST_POINT("+latitude+", "+longitude+"), 4326) " +
                "LIMIT 1;";
        selectQuery = session.createSQLQuery(query);
        List list = selectQuery.list();
        if(list != null &&  list.size() > 0) {
            BigInteger id = (BigInteger) list.get(0);
            long idRequest = id.longValue();
            List<Spot> ss = new ArrayList<>();
            try {
                Spot s = sr.getSpot(idRequest);
                ss.add(s);
            }catch (NullPointerException e){
                return null;
            }
            return ss;
        }
        else{
            return null;
        }
    }

    public String getSpots(Session session){
        String query = "Select spotid from spot;";
        Query q = session.createSQLQuery(query);
        List list = q.list();
        ArrayList<String> spots = new ArrayList<String>();
        for(int i = 0; i < list.size(); i++){
            spots.add("{");
        }

        for(int i = 0; i < list.size(); i++){
            String s = spots.get(i) +" id:"+list.get(i)+",";
            spots.set(i,s);
        }

        query = "Select latitude from spot;";
        q = session.createSQLQuery(query);
        list = q.list();

        for(int i = 0; i < list.size(); i++){
            String s = spots.get(i) +" latitude:"+list.get(i)+",";
            spots.set(i,s);
        }

        query = "Select longitude from spot;";
        q = session.createSQLQuery(query);
        list = q.list();

        for(int i = 0; i < list.size(); i++){
            String s = spots.get(i) +" longitude:"+list.get(i)+",";
            spots.set(i,s);
        }

        query = "Select spotheading from spot;";
        q = session.createSQLQuery(query);
        list = q.list();

        for(int i = 0; i < list.size(); i++){
            String s = spots.get(i) +" spotheading:"+list.get(i)+",";
            spots.set(i,s);
        }

        query = "Select numberofneighbours from spot;";
        q = session.createSQLQuery(query);
        list = q.list();

        for(int i = 0; i < list.size(); i++){
            String s = spots.get(i) +" numberofneighbours:"+list.get(i)+",";
            spots.set(i,s);
        }

        query = "Select intersection from spot;";
        q = session.createSQLQuery(query);
        list = q.list();

        for(int i = 0; i < list.size(); i++){
            String s = spots.get(i) +" intersection:"+list.get(i);
            spots.set(i,s);
        }

        for(int i = 0; i < list.size(); i++){
            String s = spots.get(i) +" }";
            spots.set(i,s);
        }

        String json = "[";
        for(int i = 0; i < spots.size()-1; i++){
            json += spots.get(i)+", ";
        }
        json += "]";

        System.out.println(json);

        return json;
    }

    public String getSpotsRelations(Session session){
        String query = "Select spot_spotid from spot_spot;";
        Query q = session.createSQLQuery(query);
        List list = q.list();
        ArrayList<String> spots = new ArrayList<String>();
        for(int i = 0; i < list.size(); i++){
            spots.add("{");
        }

        for(int i = 0; i < list.size(); i++){
            String s = spots.get(i) +" id:"+list.get(i)+",";
            spots.set(i,s);
        }

        query = "Select neighbors_spotid from spot_spot;";
        q = session.createSQLQuery(query);
        list = q.list();

        for(int i = 0; i < list.size(); i++){
            String s = spots.get(i) +" id_neighbor:"+list.get(i);
            spots.set(i,s);
        }

        for(int i = 0; i < list.size(); i++){
            String s = spots.get(i) +" }";
            spots.set(i,s);
        }

        String json = "[";
        for(int i = 0; i < spots.size()-1; i++){
            json += spots.get(i)+", ";
        }
        json += "]";

        System.out.println(json);

        return json;
    }
}

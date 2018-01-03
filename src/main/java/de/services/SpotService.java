package de.services;

import de.domains.domainAux.GpsPoint;
import de.domains.domainAux.Route;
import de.domains.domain.Spot;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import de.repositories.preDBRepositories.SpotRepository;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * The SpotService is responsible for the main tasks of mapping routes into
 * spots
 *
 * @author simon_000
 */
@Service
public class SpotService {

	@Autowired
	SpotRepository spotRepository;

	CustomSpotQueries spotQuery = new CustomSpotQueries();

	/**
	 * The calculation level increases every time a trajectory is processed
	 */
	static private int calculationLevel = 1;
	/**
	 * Saves the last spot that the trajectory crossed to check for crossroads
	 */
	static private Spot lastSpot;

	public Configuration config;
	public SessionFactory sessionFactory;
	public Session session;

	/**
	 * Generates and extends the spot structure
	 *
	 * @param route
	 *            :Route that is going to be mapped into Spots
	 * @return Route with information about the assigned Spot at each trajectory
	 *         point
	 */
	public Route learningSpotStructure(Route route) {
		if(this.session == null || !this.session.isOpen()) {
			if(session == null){
				Configuration config = new Configuration();
				config.configure();
				this.sessionFactory = config.buildSessionFactory();
				this.session = sessionFactory.openSession();
			}
			if(!session.isOpen()){
				this.session = sessionFactory.openSession();
			}
		}
		if (route == null) {
		} else {
			if (calculationLevel == 0) {
				route = initialSpotMapping(route);
			} else {
				route = extendSpotStructureSpeedUp_v2(route);
			}
		}
		//close session
		session.close();
		System.out.println("Job done :)");
		return route;
	}

	/**
	 * Does the initial spot mapping
	 *
	 * @param route
	 *            :Route that is going to be mapped into Spots
	 * @return Route with information about the assigned Spot at each trajectory
	 *         point
	 */
	private Route initialSpotMapping(Route route) {
		Date start_time = new Date();

		// increment calculation-level & mark route as processed
		calculationLevel++;

		// initialize grid-structure
		System.out.println("Grid erstellt! -- Datenbankverbindung aufbauen!");
		// create first spot
		Spot spot = generateSpot(route, 0);
		route.route[0].setSpot(spot);
		route.route[0].setMappedToSpot(true);
		spot.spotID = spotQuery.addSpot(spot,session);

		System.out.println(spot + " zur Datenbank hinzufügen");
		// create further spots
		for (int j = 1; j < route.route.length; j++) {
			InfoBundle infobundle = searchClosestSpot(route.route[j]);
			route.route[j].setClosestSpotInfo(infobundle);
			if (infobundle == null || infobundle.distance >= Spot.stdRadius * 2) {
				spot = generateSpot(route, j);
				route.route[j].setSpot(spot);
				route.route[j].setMappedToSpot(true);
				spot.spotID = spotQuery.addSpot(spot,session);
				System.out.println(spot + " zur Datenbank hinzufügen");

			} else if (infobundle.inRange) {
				spot = spotRepository.getSpot(infobundle.minDistance_spotID);
				System.out.println("Einen Spot aus der Datenbank laden");
				route.route[j].setSpot(spot);
				route.route[j].setMappedToSpot(true);
			} else if (!infobundle.inRange && infobundle.distance < Spot.stdRadius * 2) {

			}
		}
		// complete spot mapping and set neighbors of the created spots
		lastSpot = null;
		for (int j = 0; j < route.route.length; j++) {
			// check for the points that wasn't able to build an own spot or
			// wasn't in the range of a spot
			if (!route.route[j].isMappedToSpot()) {
				// search for the closets spots
				InfoBundle infobundle = searchClosestSpot(route.route[j]);
				route.route[j].setClosestSpotInfo(infobundle);
				// check for the current point if its in range of a spot
				if (infobundle.inRange) {
					System.out.println("Einen Spot aus der Datenbank laden");
					spot = spotRepository.getSpot(infobundle.minDistance_spotID);
					route.route[j].setSpot(spot);
					route.route[j].setMappedToSpot(true);
				}
				// else add it to the "outside-area" of the nearest spot,
				// because the distance to the nearest spot is too high to be
				// in range and is to close to build an own spot
				else {
					System.out.println("Einen Spot aus der Datenbank laden");
					spot = spotRepository.getSpot(infobundle.minDistance_spotID);
					route.route[j].setSpot(spot);
					route.route[j].setMappedToSpot(true);
				}
			}
			// set neighbors of the created spots
			Spot sp = route.route[j].getSpot();
			if (sp != null && lastSpot != null) {
				if (!sp.getSpotID().equals(lastSpot.getSpotID())) {
					addNeighbor(lastSpot,spot);
					}
			}
			lastSpot = sp;
		}
		lastSpot = null;

		Date stop_time = new Date();
		double time = stop_time.getTime() - start_time.getTime();
		time = time / 1000;
		System.out.println("SPOT FINDING: " + time + " seconds");
		return route;
	}

	/**
	 * Extends the spot structure
	 *
	 * @param route
	 *            :Route that is going to be mapped into Spots
	 * @return Route with information about the assigned Spot at each trajectory
	 *         point
	 */
	private Route extendSpotStructureSpeedUp_v2(Route route) {
		Date start = new Date();
		ArrayList<Integer> notMapped = new ArrayList<>();
		ArrayList<Long> spotIDs = new ArrayList<>();
		calculationLevel++;
		// counts the points that are in the range of the same (already
		// existing) spot
		int inRangeCounter = 0;
		// indicates if the last point was in the range of an existing spot
		boolean lastPointInSpot = false;
		// ID of the last Spot a trajectory point was in range of
		// default = 0
		Long lastInRangeID = -1l;
		// indicates if trajectory was in the last run in the range of a spot
		// and now is immediately in the range of another spot
		boolean changedSpotInRange = false;

		//lastSpot
		Spot lastSpot = null;

		// iterate through the trajectory
		for (int j = 0; j < route.route.length; j++) {
			// search the closest spot
			InfoBundle infobundle;
			GpsPoint gpsPoint = route.route[j];
			double distanceToLastSpot = 1000000;
			if(lastSpot !=null){
				distanceToLastSpot = GPSDataProcessor.calcDistance(gpsPoint.getLatitude(),gpsPoint.getLongitude(),lastSpot.getLatitude(),lastSpot.getLongitude());
			}
			if(distanceToLastSpot < Spot.stdRadius){
				InfoBundle bundle = new InfoBundle(lastSpot.getSpotID(), lastSpot.getLatitude(), lastSpot.getLongitude(), true, distanceToLastSpot);
				bundle.setSpot(lastSpot);
				infobundle = bundle;
			}
			else {
				infobundle = searchClosestSpot(route.route[j]);
			}
			route.route[j].setClosestSpotInfo(infobundle);

			// variables
			int tempCounter = inRangeCounter;
			Spot spot = null;

			// check if the current point is... in range / outside / able to
			// create a new spot
			if (infobundle == null || infobundle.distance >= (Spot.stdRadius * 2)) {
				// update counter
				inRangeCounter = 0;
				lastPointInSpot = false;
				// generate spot
				spot = generateSpot(route, j);
				route.route[j].setSpot(spot);
				route.route[j].setMappedToSpot(true);
				spot.spotID = spotQuery.addSpot(spot,session);
			} else if (!infobundle.inRange && infobundle.distance < (Spot.stdRadius * 2)) {
				// update counter
				inRangeCounter = 0;
				lastPointInSpot = false;
				notMapped.add(j);

			} else if (infobundle.inRange) {
				// point in range
				spot = infobundle.getSpot();
				route.route[j].setSpot(spot);
				route.route[j].setMappedToSpot(true);
				// check if the last point was in the same spot
				if (lastPointInSpot) {
					if (!infobundle.minDistance_spotID.equals(lastInRangeID)) {
						inRangeCounter = 0;
						changedSpotInRange = true;
					} else {
						inRangeCounter++;
					}
				} else {
					inRangeCounter++;
				}
				lastInRangeID = infobundle.minDistance_spotID;
				lastPointInSpot = true;
			}

			// Get closest point in range if there was more points in the range
			// of one spot to update the spot
			if (tempCounter > inRangeCounter) {
				// default = 100 - no meaning
				double minDistance = 100;
				int minIndex = 0;
				for (int n = 1; n <= tempCounter; n++) {
					double dist = route.route[j-n].getClosestSpotInfo().distance;
					if (dist < minDistance) {
						minDistance = dist;
						minIndex = (j - n);
					}
				}
				InfoBundle nearestClusterInfo = route.route[minIndex].getClosestSpotInfo();
				Spot sp = nearestClusterInfo.getSpot();
				// this function will update the spot
				sp.updateSpot(route.route[minIndex]);
				spotQuery.updateSpot(sp, session);
			}

			// if the spot in range was changed related to spot of the point
			// before
			if (changedSpotInRange) {
				inRangeCounter = 1;
				changedSpotInRange = false;
			}

			if(j==0){
				if(spot != null) {
					spotIDs.add(spot.getSpotID());
				}
			}
			if (spot != null && lastSpot != null) {
				if (!spot.getSpotID().equals(lastSpot.getSpotID())) {
					addNeighbor(lastSpot,spot);
					spotIDs.add(spot.getSpotID());
				}
			}
			if(spot != null){
				lastSpot = spot;
			}
		}

		// complete spot mapping
		for (int k = 0; k < notMapped.size(); k++) {
			// check for the points that wasn't able to build an own spot or
			// wasn't in the range of a spot
			GpsPoint currentPoint = route.route[notMapped.get(k)];

			Spot closestSpot = null;
			Spot currentclosestSpot = route.route[notMapped.get(k)].getClosestSpotInfo().getSpot();
			for (int temp = notMapped.get(k) + 1; temp < route.route.length; temp++) {
				if (route.route[temp].isMappedToSpot()) {
					Spot nextSpot = route.route[temp].getSpot();
					double distance = GPSDataProcessor.calcDistance(nextSpot.getLatitude(), nextSpot.getLongitude(),

								route.route[temp].getLatitude(), route.route[temp].getLongitude());
					if (distance < route.route[notMapped.get(k)].getClosestSpotInfo().minDistance_spotCenterlat) {
						closestSpot = nextSpot;
					} else {
						closestSpot = currentclosestSpot;
					}
					break;
				} else {
					closestSpot = currentclosestSpot;
				}
				if(temp > notMapped.get(k) + 5){
					break;
				}
			}
			route.route[notMapped.get(k)].setSpot(closestSpot);
			route.route[notMapped.get(k)].setMappedToSpot(true);
		}

		Collections.sort(spotIDs);

		Long lastValue = null;
		for(Iterator<Long> i = spotIDs.iterator(); i.hasNext();) {
			Long currentValue = i.next();
			if(lastValue != null && currentValue.equals(lastValue)) {
				i.remove();
			}
			lastValue = currentValue;
		}

		Date stop = new Date();

		long overallTime = stop.getTime()-start.getTime();
		//System.out.println("OVERALL TIME: SINGLE ROUTE: "+ overallTime);

		return route;
	}

	/**
	 * Adds a new neighbor Spot to the Spot
	 *
	 * @param spot :Spot to add as neighbor
	 */
	public void addNeighbor(Spot spot, Spot spot2) {
		if (spot != null) {
			double distance = GPSDataProcessor.calcDistance(spot.getLatitude(), spot.getLongitude(), spot2.getLatitude(), spot2.getLongitude());
			if (distance >= 25 && distance <= 150) {
				if (!spot.getSpotID().equals(spot2.getSpotID())) {
					List<Spot> neighbors = spot.getNeighbors();
					boolean contained = false;
					for (int i = 0; i < neighbors.size(); i++) {
						if (spot2.getSpotID().equals(neighbors.get(i))) {
							contained = true;
						}
					}
					if (!contained) {
						neighbors.add(spot2);
						spot.numberOfNeighbours++;
						spot.setNeighbors(neighbors);

						spot2.numberOfNeighbours++;
						spot2.getNeighbors().add(spot);

						if (neighbors.size() >= 3) {
							spot.setIntersection(true);
						}
						if(spot2.getNeighbors().size() >= 3){
							spot2.setIntersection(true);
						}
						spotQuery.addNeighbour(spot,spot2,session);
						spotQuery.updateSpot(spot,session);
						spotQuery.updateSpot(spot2,session);
					}
				}
			}
		}
	}

	/**
	 * Generates a new spot out of a point(GPS_plus object) with given index in
	 * a specific route
	 *
	 * @param route
	 *            :Route that contains the GPS_plus object
	 * @param indexGPSpoint
	 *            :Index of the point, which generates the Spot
	 * @return Created Spot
	 */
	private static Spot generateSpot(Route route, int indexGPSpoint) {
		GpsPoint currentPoint = route.route[indexGPSpoint];
		double heading = currentPoint.getHeading();
		Spot spot = new Spot(currentPoint, heading);
		return spot;
	}

	/**
	 * Calculates the closest spot of a GPS point (GPS_plus object)
	 *
	 * @param point
	 *            :GPS_plus point
	 * @return InfoBundle, gives information about the closest Spot (see
	 *         documentation InfoBundle)
	 */
	private InfoBundle searchClosestSpot(GpsPoint point) {

		System.out.println("searchClosestSpot");
		List<Spot> spots = spotRepository.getClosestSpot(point.getLatitude(),point.getLongitude());

		double minDistance;
		Long minDistance_spotID;
		// Center GPS_plus object of the closest spot
		double minDistance_centerGPSdatalat;
		double minDistance_centerGPSdatalong;
		// indicates if the closest spot is in the range of point
		boolean inRange = false;

		if (spots != null && spots.size() != 0) {
			minDistance = GPSDataProcessor.calcDistance(spots.get(0).getLatitude(),spots.get(0).getLongitude(), point.getLatitude(), point.getLongitude());
			minDistance_centerGPSdatalat = spots.get(0).getLatitude();
			minDistance_centerGPSdatalong = spots.get(0).getLongitude();
			minDistance_spotID = spots.get(0).getSpotID();

			if (minDistance < Spot.stdRadius) {
				inRange = true;
			}
			InfoBundle bundle = new InfoBundle(minDistance_spotID, minDistance_centerGPSdatalat, minDistance_centerGPSdatalong, inRange, minDistance);
			bundle.setSpot(spots.get(0));
			return bundle;
		} else {
			// if there was no spot within the search
			return null;
		}
	}
}

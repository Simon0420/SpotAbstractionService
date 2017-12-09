package de.services;

import de.domain.GpsPoint;
import de.geodesy.GeoDesy;

/**
 * This class processes GPS-data
 * 
 * @author simon_000
 *
 */
public class GPSDataProcessor {

	/**
	 * Calculates the distance between two GPS-points
	 * 
	 * @param start
	 *            :GPS_plus point
	 * @param end
	 *            :GPS_plus point
	 * @return distance in double
	 */
	static public double calcDistance(GpsPoint start, GpsPoint end) {
		double distanceInM = GeoDesy.distanceCalculationGEODESY((float)start.getLatitude(), (float)start.getLongitude(),
				(float)end.getLatitude(), (float)end.getLongitude());
		return distanceInM;
	}

	static public double calcDistance(double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
		double distanceInM = GeoDesy.distanceCalculationGEODESY((float)startLatitude, (float)startLongitude,
				(float)endLatitude, (float)endLongitude);
		return distanceInM;
	}

	/**
	 * Calculates the heading direction of a GPS point
	 * 
	 * @param start
	 *            :GPS_plus object, which is going to be attached by the heading
	 * @param end
	 *            :Sequential GPS_plus object
	 * @return double: heading for the start-point
	 */
	static private double calcHeading(GpsPoint start, GpsPoint end) {
		double heading = GeoDesy.headCalculationGEODESY((float)start.getLatitude(), (float)start.getLongitude(), (float)end.getLatitude(),
				(float)end.getLongitude());
		return heading;
	}

	/**
	 * Calculates the time difference between two GPS-points
	 * 
	 * @param p2
	 *            :GPS_plus point
	 * @param p1
	 *            :GPS_plus point
	 * @return long: (p2 - p1) time difference
	 */
	static private long timeDiff(GpsPoint p2, GpsPoint p1) {
		return p2.getDate() - p1.getDate();
	}
}

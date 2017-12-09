package de.geodesy;

/**
 * This classes uses the GeoDesy API to do different calculations with GPS-data.
 * Java Geodesy Library for GPS � Vincenty�s Formulae by Mike Gavaghan.
 * (c.f. http://www.gavaghan.org/blog/free-source-code/geodesy-library-vincentys-formula-java/)
 * 
 * @author simon_000
 *
 */
public class GeoDesy {
	/**
	 * Calculate destination out of a start point, distance, travel degree
	 * 
	 * @param startlat
	 *            latitude of Start-GPS-Data
	 * @param startlong
	 *            longitude of Start-GPS-Data
	 * @param distance
	 *            in meters
	 * @param degree
	 *            direction
	 * @return float-array with 0: end bearing, 1: latitude destination, 2:
	 *         longitude destination
	 */
	public static float[] destinationCalculationGEODESY(double startlat, double startlong, double distance, double degree) {
		GeodeticCalculator calculator = new GeodeticCalculator();
		GlobalCoordinates startCoordinate;
		Ellipsoid ellipsoid = Ellipsoid.WGS84;
		// calculate destination
		startCoordinate = new GlobalCoordinates(startlat, startlong);
		double[] endCoordinates = new double[1];
		GlobalCoordinates dest = calculator.calculateEndingGlobalCoordinates(ellipsoid, startCoordinate, degree, distance,
				endCoordinates);
		// get results
		float result[] = new float[3];
		// destination bearing
		result[0] = (float) endCoordinates[0];
		// destination latitude
		result[1] = (float) dest.getLatitude();
		// destination longitude
		result[2] = (float) dest.getLongitude();
		
		return result;
	}

	/**
	 * Calculate distance between two GPS-points
	 * 
	 * @param lat1
	 *            latitude of Start-GPS-Data
	 * @param long1
	 *            longitude of Start-GPS-Data
	 * @param lat2
	 *            latitude of End-GPS-Data
	 * @param long2
	 *            longitude of End-GPS-Data
	 * @return distance in meters
	 */
	public static double distanceCalculationGEODESY(double lat1, double long1, double lat2, double long2) {
		GeodeticCalculator calculator = new GeodeticCalculator();
		GlobalCoordinates startCoordinates;
		GlobalCoordinates endCoordinates;
		Ellipsoid ellipsoid = Ellipsoid.WGS84;

		// set coordinates
		startCoordinates = new GlobalCoordinates(lat1, long1);
		endCoordinates = new GlobalCoordinates(lat2, long2);
		
		// calculate the curve
		GeodeticCurve geoCurve = calculator.calculateGeodeticCurve(ellipsoid, startCoordinates, endCoordinates);
		double metres = geoCurve.getEllipsoidalDistance();

		return metres;
	}

	/**
	 * Calculate heading degree between 2 points
	 * 
	 * @param lat1
	 *            latitude of Start-GPS-Data
	 * @param long1
	 *            longitude of Start-GPS-Data
	 * @param lat2
	 *            latitude of End-GPS-Data
	 * @param long2
	 *            longitude of End-GPS-Data
	 * @return heading degree
	 */
	public static double headCalculationGEODESY(float lat1, float long1, float lat2, float long2) {
		// instantiate the calculator
		GeodeticCalculator calculator = new GeodeticCalculator();
		GlobalCoordinates coordinate1;
		GlobalCoordinates coordinate2;
		Ellipsoid ellipsoid = Ellipsoid.WGS84;

		// set coordinates
		coordinate1 = new GlobalCoordinates(lat1, long1);
		coordinate2 = new GlobalCoordinates(lat2, long2);

		// calculate the curve
		GeodeticCurve geoCurve = calculator.calculateGeodeticCurve(ellipsoid, coordinate1, coordinate2);

		return geoCurve.getAzimuth();
	}

}
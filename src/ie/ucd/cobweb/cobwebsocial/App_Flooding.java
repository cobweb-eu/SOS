package ie.ucd.cobweb.cobwebsocial;

//import ie.ucd.sixth.core.cyber.utils.geocode.Geocoder;
import ie.ucd.sixth.core.cyber.adaptor.sensor.AbstractAdaptorStream;
import ie.ucd.sixth.core.cyber.utils.geocode.Geocoder;
import ie.ucd.sixth.core.sensor.data.ISensorData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
//import java.awt.Color;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
//import dashboard.SplitViewFrame;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.osgi.framework.BundleContext;

import buildingblocks.AbstractApplication;
import buildingblocks.Infrastructure;
import buildingblocks.Sensor;

public class App_Flooding extends AbstractApplication {

	private final String TSTR = "flood";
	private final String NA = "data.nearest_area.";
	private final String LT = "latitude";
	private final String LN = "longitude";
	private final String NALT = NA + LT;
	private final String NALN = NA + LN;
	private static final String UTF8 = "UTF-8";

	private final String TDOT = "twitter.";
	private final String UDOT = "user.";
	private final String TUID = "userID";
	private final String TID = TDOT + UDOT + TUID;
	private final String TLT = TDOT + LT;
	private final String TLN = TDOT + LN;

	private final String TLOC = "twitter.user.userLocation";
	private final String SLT = "searchresults.place.0.lat";
	private final String SLN = "searchresults.place.0.lon";

	private final String TEXT = "text";
	private final String TWEET = "tweet";
	private final String UID = "userID";
	private final String GEOM = "geom";
	private final String TTEXT = TDOT + TEXT;
	private final String TTABLE = "twitterflood";

	private final int SRID = 4326;

	private final String DB = "jdbc:postgresql://localhost:5432/jakarta";
	private final String DRIVER = "org.postgresql.Driver";
	private final String USER = "conor";
	private final String PASS = "c0bw3b";

	BundleContext c;
	private static Logger logger = Logger.getLogger(AbstractAdaptorStream.class
			.getName());

	static {
		logger.setLevel(Level.ALL);
	}

	public App_Flooding(BundleContext context) {
		super(context);
		c = context;
	}

	@Override
	protected void setup() {
		// launch the dashboard for viewing the sensor data
		// gui = new SplitViewFrame();
		// gui.setup();

		// set up the monitoring infrastructure
		Infrastructure twitterInfrastructure = createEntityFocusedInfrastructure(
				"twitter", TSTR);
		twitterInfrastructure.generateLocations("worldweather");
		twitterInfrastructure.generateUsers();
		twitterInfrastructure.start();

	}

	@Override
	public void respondToData(Sensor sensor, ISensorData data) {
		// do something with the data - in this case display it on the dashboard
		// map and list using a colour code

		Map<String, String> map = data.getValues();
		Set<String> ks = map.keySet();
		String lat = null, lon = null;
		if (ks.contains(NALT)) {
			lat = map.get(NALT);
			lon = map.get(NALN);
		} else if (ks.contains(TLT)) {
			lat = map.get(TLT);
			lon = map.get(TLN);
		} else {
			String locationNameValue = data.getValues().get(TLOC);
			if (locationNameValue != null) {
				Map<String, String> coords = Geocoder
						.geocode(locationNameValue);
				lat = coords.get(SLT);
				lon = coords.get(SLN);
			}

		}

		System.out.println("Have tweet data");
		if(lat==null)System.out.println("Not inserting\n");
		else{
			// System.out.println("Tweet text: "+map.get(TTEXT));
			// System.out.println("Location: "+lon+" "+lat);

			String tweet = map.get(TTEXT);
			// Update DB
			System.out.println("Inserting tweet\n");

			// java.sql.Connection conn;

			try {

				URL url = new URL(
						"https://api.idolondemand.com/1/api/sync/analyzesentiment/v1?text="
								+ URLEncoder.encode(tweet, UTF8)
								+ "&apikey=2ec20ae0-802b-49cc-920d-aa0b0ab0b608");
				BufferedReader br = new BufferedReader(new InputStreamReader(
						url.openStream()));
				String line = null;
				StringBuilder sb = new StringBuilder();
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				br.close();
				JSONObject jO = new JSONObject(sb.toString());
				JSONObject agg = jO.getJSONObject("aggregate");
				Double d = agg.getDouble("score");
				
				URL u = c.getBundle().getResource(Constant.FN);
				
				Utility.post(lon, lat,
						"Sentiment: " + d + " User ID: " + map.get(TID)
								+ " Tweet: " + tweet, u.openStream());
				/*
				 * Load the JDBC driver and establish a connection.
				 */

				/*
				 * Class.forName(DRIVER);
				 * 
				 * conn = DriverManager.getConnection(DB, USER, PASS); /* Add
				 * the geometry types to the connection. Note that you must cast
				 * the connection to the pgsql-specific connection
				 * implementation before calling the addDataType() method.
				 */
				/*
				 * ((org.postgresql.PGConnection) conn).addDataType("geometry",
				 * Class.forName("org.postgis.PGgeometry"));
				 * ((org.postgresql.PGConnection) conn).addDataType("box3d",
				 * Class.forName("org.postgis.PGbox3d"));
				 * 
				 * PreparedStatement ps = conn.prepareStatement("INSERT INTO "
				 * // + TTABLE + " (" + TWEET + ", " + GEOM + ", " + UID +
				 * ") VALUES (?, ?, ?)"); ps.setString(1, tweet);
				 * 
				 * Point point = new Point(Double.parseDouble(lon),
				 * Double.parseDouble(lat));
				 * 
				 * PGgeometry pgG = new PGgeometry(point); point.setSrid(SRID);
				 * 
				 * ps.setObject(2, pgG);
				 * 
				 * ps.setString(3, map.get(TID));
				 * 
				 * ps.executeUpdate(); ps.close();
				 * 
				 * conn.close();
				 */
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		//System.out.println();

	}
}

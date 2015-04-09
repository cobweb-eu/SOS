package ie.ucd.cobweb.cobwebsocial;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URL;
import java.util.Calendar;
import java.util.UUID;

public class Utility {

	private static final String FN = "iT.xml";
	private static final char DDLM = '-';
	private static final char TDLM = ':';
	private static final String ADDR = "smartcoasts.ucd.ie";
	private static final char PT = '.';
	final private static int PORT = 8080;
	final private static String PATH = "/52n-sos-webapp/service";

	// private static final String UTF8="UTF-8";

	public static void post(String lon, String lat, String tweet, InputStream is)
			throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = br.readLine()) != null) {
			sb.append(line);
			sb.append("\r\n");
		}
		br.close();

		String data = sb.toString();

		String[] dat = data.split("%s");
		Calendar c = Calendar.getInstance();
		StringBuilder pt = new StringBuilder();
		pt.append(dat[0]);
		pt.append(c.get(Calendar.YEAR));
		pt.append(DDLM);
		pt.append(c.get(Calendar.MONTH));
		pt.append(DDLM);
		pt.append(c.get(Calendar.DAY_OF_MONTH));
		pt.append(dat[1]);
		
		pt.append(c.get(Calendar.HOUR_OF_DAY));
		pt.append(TDLM);
		pt.append(c.get(Calendar.MINUTE));
		pt.append(TDLM);
		pt.append(c.get(Calendar.SECOND));
		pt.append(PT);
		pt.append(c.get(Calendar.MILLISECOND));

		pt.append(dat[2]);
		pt.append(UUID.randomUUID().toString());
		pt.append(dat[3]);
		pt.append(lat);
		pt.append(dat[4]);
		pt.append(lon);
		pt.append(dat[5]);
		// pt.append(URLEncoder.encode(tweet,UTF8));
		pt.append(tweet);
		pt.append(dat[6]);

		Socket socket = new Socket(ADDR, PORT);

		BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(
				socket.getOutputStream()));

		String ptS = pt.toString();
		
		//System.out.println(ptS);

		wr.write("POST " + PATH + " HTTP/1.0\r\n");
		wr.write("Content-Length: " + ptS.length() + "\r\n");
		wr.write("Content-Type: application/soap+xml\r\n");
		wr.write("\r\n");

		// System.out.println(pt.toString());

		wr.write(ptS);
		wr.flush();

		BufferedReader rd = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));

		while ((line = rd.readLine()) != null) {
			//System.out.println(line);
		}
		wr.close();
		rd.close();
		socket.close();

	}
}

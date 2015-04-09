package ie.ucd.cobweb.cobwebsocial;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class RequestObs {

	private final static String FN = "request.json";
	private static final String ADDR = "smartcoasts.ucd.ie";
	final private static int PORT = 8080;
	final private static String PATH = "/52n-sos-webapp/service";

	public static void main(String[] args) throws IOException {
		System.out.println(post());
	}

	public static String post() throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File(FN))));
		String line = null;
		StringBuilder sb = new StringBuilder();
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		br.close();

		Socket socket = new Socket(ADDR, PORT);

		BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(
				socket.getOutputStream()));

		String ptS = sb.toString();

		wr.write("POST " + PATH + " HTTP/1.0\r\n");
		wr.write("Content-Length: " + ptS.length() + "\r\n");
		wr.write("Content-Type: application/json\r\n");
		wr.write("\r\n");

		wr.write(ptS);
		wr.flush();

		BufferedReader rd = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));

		sb = new StringBuilder();

		//boolean b = false;
		while ((line = rd.readLine()) != null) {
			// if (b) {
			sb.append(line);
			sb.append("\r\n");
			/*
			 * } else if (line.trim().equals("")) b = true;
			 */
		}
		wr.close();
		rd.close();
		socket.close();

		return sb.toString();
	}
}

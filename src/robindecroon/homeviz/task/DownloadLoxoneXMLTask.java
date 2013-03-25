package robindecroon.homeviz.task;

import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.xmlpull.v1.XmlPullParserException;

import robindecroon.homeviz.Constants;
import robindecroon.homeviz.exceptions.NoSuchDevicesInRoom;
import robindecroon.homeviz.room.Consumer;
import robindecroon.homeviz.room.Room;
import robindecroon.homeviz.xml.Entry;
import robindecroon.homeviz.xml.IEntry;
import robindecroon.homeviz.xml.LoxoneXMLParser;
import robindecroon.homeviz.xml.XMLReturnObject;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

public class DownloadLoxoneXMLTask extends
		AsyncTask<String, Void, Map<String, List<IEntry>>> {
	
	private List<Room> rooms;
	
	public DownloadLoxoneXMLTask(List<Room> rooms) {
		this.rooms = rooms;
	}

	@Override
	protected Map<String, List<IEntry>> doInBackground(String... urls) {
		Log.i(getClass().getSimpleName(), "Synchronization with Loxone started");
		try {
			return loadXmlFromNetwork(urls[0]);
		} catch (IOException e) {
			Log.e(getClass().getSimpleName(), "IO error: " + e.getMessage());
			return null;
		} catch (XmlPullParserException e) {
			Log.e(getClass().getSimpleName(), "XML error: " + e.getMessage());
			return null;
		}
	}

	@Override
	protected void onPostExecute(Map<String, List<IEntry>> result) {
		if (result != null) {
			
			for (String name : result.keySet()) {
				for(Room room : rooms) {
					try {
						Consumer cons = room.getConsumerWithName(name);
						cons.putEntries(result.get(name));
					} catch (NoSuchDevicesInRoom e) {
						// Not in this Room
					}
				}
			}
		}
	}

	private Map<String, List<IEntry>> loadXmlFromNetwork(String urlString)
			throws IOException, XmlPullParserException {

		Map<String, List<IEntry>> list = new HashMap<String, List<IEntry>>();

		FTPClient client = new FTPClient();
		try {

			client.connect(urlString);
			client.enterLocalPassiveMode();
			client.login(Constants.LOXONE_USER, Constants.LOXONE_PASSWORD);
			client.changeWorkingDirectory(Constants.WORKING_DIRECTORY);

			FTPFile[] ftpFiles = client.listFiles();
			Set<String> fileNames = new HashSet<String>();
			for (FTPFile ftpFile : ftpFiles) {
				fileNames.add(ftpFile.getName());
			}
			// needed for authentication
			CookieManager cookieManager = new CookieManager();
			CookieHandler.setDefault(cookieManager);
			for (String fileName : fileNames) {
				URL url;
				try {
					url = new URL("http://" + Constants.LOXONE_IP + "/stats/"
							+ fileName + ".xml");
					URLConnection httpConn = url.openConnection();
					byte[] auth = (Constants.LOXONE_USER + ":" + Constants.LOXONE_PASSWORD)
							.getBytes();
					String basic = Base64.encodeToString(auth, Base64.NO_WRAP);
					httpConn.setRequestProperty("Authorization", "Basic "
							+ basic);

					InputStream in = httpConn.getInputStream();
					if (in != null) {
						LoxoneXMLParser loxoneXMLParser = new LoxoneXMLParser();
						Log.i(getClass().getSimpleName(), "Started parsing "
								+ fileName);
						XMLReturnObject XMLResult = loxoneXMLParser.parse(in);
						list.put(XMLResult.getName(), XMLResult.getEntries());
//						list.add(loxoneXMLParser.parse(in));
					} else {
						Log.e(getClass().getSimpleName(), "No inputstream for "
								+ fileName);
					}

				} catch (Exception e) {
					Log.e(getClass().getSimpleName(), e.getMessage());
				}
			}

		} catch (SocketException e) {
			Log.e(getClass().getSimpleName(),
					"Connection error: " + e.getMessage());
		} catch (IOException e) {
			Log.e(getClass().getSimpleName(), "IO error: " + e.getMessage());
		} finally {
			if (client != null) {
				client.logout();
				client.disconnect();
			}
		}
		return list;
	}

}

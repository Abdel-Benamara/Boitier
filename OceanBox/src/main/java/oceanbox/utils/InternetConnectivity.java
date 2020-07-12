package oceanbox.utils;

import java.net.URL;
import java.net.URLConnection;

/**
 * Cette classe permet de tester la connexion internet
 */
public class InternetConnectivity {

	public InternetConnectivity() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Cette méthode teste la connexion internet en ciblant l'URL passée en
	 * paramètre
	 * 
	 * @param urlToTest : l'URL à tester
	 * @return : true si l'application parvient à accéder à l'URL, false sinon
	 */
	public static boolean checkConnexion(String urlToTest) {
		try {
			URL url = new URL("http://" + urlToTest);
			URLConnection connection = url.openConnection();
			connection.connect();
			return true;
		} catch (Exception e) {
			System.out.println("Internet Not Connected");
			return false;
		}
	}
}

package oceanbox.utils.propreties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Cette classe gère les informations relatives à l'OceanBox écrites dans le
 * fichier de propriété du même nom que la classe
 */
public class SystemProperties {

	protected static Properties props;
	protected static Map<String, String> defaultProperties;
	// protected static String path = "/home/pi/OceanBox/properties/SystemProperties.properties";
	protected static String path = "/Users/abdelbenamara/eclipse-workspace-java/Boitier/OceanBox/properties/SystemProperties.properties";

	public SystemProperties() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Cette méthode initialise des propriétés par défaut pour créer les champs
	 * nécessaires au bon fonctionnement de l'application
	 */
	private static void initDefaultProperties() {

		defaultProperties = new HashMap<String, String>();

		// OceanBox settings
		defaultProperties.put("oceanBoxNumber", "0");
		defaultProperties.put("oceanBoxIP", "0.0.0.0");

		// FTP settings
		defaultProperties.put("ftpUser", "ocean_ftp");
		defaultProperties.put("ftpIP", "37.187.107.122");
		defaultProperties.put("ftpPassword", "Stream2020");
		defaultProperties.put("ftpPort", "21");
		defaultProperties.put("ftpVideoPath", "/default_video/");

		// Database settings
		defaultProperties.put("dbUser", "ocean_bdd");
		defaultProperties.put("dbIP", "37.187.107.122");
		defaultProperties.put("dbPassword", "OceanBox2020");
		defaultProperties.put("dbPort", "3306");

		// Video settings
		defaultProperties.put("videoPath", "/home/pi/OceanBox/video/");

		// Log settings
		defaultProperties.put("localLogPath", "/home/pi/OceanBox/logs/");
		defaultProperties.put("remoteLogPath", "/logs/");

		// CMDs
		defaultProperties.put("mediaInfoCMD", "/usr/bin/mediainfo");
		defaultProperties.put("vlcCMD", "/usr/bin/vlc");

	}

	/**
	 * Cette méthode initialise le fichier de propriétés
	 * 
	 * @throws FileNotFoundException : si le fichier n'a pas été trouvé
	 * @throws IOException           : si lire ou écrire dans le fichier a échoué
	 */
	public static void initProperties() throws FileNotFoundException, IOException {

		props = new Properties();

		if (!propertiesFileExist()) {
			createProperties();
			try (OutputStream writer = new FileOutputStream(path)) {
				props.store(writer, null);
			}

		} else {
			try (InputStream input = new FileInputStream(path)) {
				props.load(input);
			}
		}
	}

	/**
	 * Cette méthode permet de savoir si le fichier de propriétés de cette classe
	 * existe
	 * 
	 * @return : true s'il existe, false sinon
	 */
	private static boolean propertiesFileExist() {

		File f = new File(path);

		return f.exists();
	}

	/**
	 * Cette méthode supprime le fichier de propriété
	 */
	public static void deletePropertiesFile() {

		if (propertiesFileExist()) {
			File f = new File(path);
			f.delete();
		}
	}

	/**
	 * Cette méthode crée le fichier et y écrit les propriétés par défaut
	 */
	private static void createProperties() {

		initDefaultProperties();

		for (String p : defaultProperties.keySet()) {
			props.setProperty(p, defaultProperties.get(p));
		}
	}

	/**
	 * Cette méthode récupère la propriété passée en paramètre
	 * 
	 * @param key : le nom (sensible à la casse) de la propriété voulue
	 * @return : la valeur de la propriété
	 */
	public static String getPropertie(String key) {
		return (String) props.get(key);
	}

	/**
	 * Cette méthode modifie la valeur de la propriété ciblée
	 * 
	 * @param key   : le nom (sensible à la casse) de la propriété ciblée
	 * @param value : la nouvelle valeur de la propriété
	 * @throws FileNotFoundException : si le fichier n'a pas été trouvé
	 * @throws IOException           : si lire ou écrire dans le fichier a échoué
	 */
	public static void setPropertie(String key, String value) throws FileNotFoundException, IOException {

		props.setProperty(key, value);
		try (OutputStream writer = new FileOutputStream(path)) {
			props.store(writer, null);
		}
	}
}

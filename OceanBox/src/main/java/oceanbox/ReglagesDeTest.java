package oceanbox;

import java.io.FileNotFoundException;
import java.io.IOException;

import oceanbox.utils.propreties.ClientProperties;
import oceanbox.utils.propreties.SystemProperties;

/**
 * Cette classe permet de modifier des propriétés rapidement pour pouvoir tester
 * chez soi
 */
public class ReglagesDeTest {

	public ReglagesDeTest() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Cette méthode modifie les propriétés nécessaires aux tests sur ordinateur
	 * 
	 * @throws FileNotFoundException : si le fichier n'a pas été trouvé
	 * @throws IOException           : si lire ou écrire dans le fichier a échoué
	 */
	public static void initPersonalSettings() throws FileNotFoundException, IOException {
		// -----------------------------------------------------------------------------------

		// Modifiez la ligne ci-dessous pour aller chercher les videos au bon endroit
		SystemProperties.setPropertie("videoPath", "/Users/abdelbenamara/Movies/OceanBox/");
		// SystemPropreties.setPropretie("videoPath", "/Users/daekc/Desktop/video/");
		// SystemPropreties.setPropretie("videoPath",
		// "/home/mathieuridet/Videos/OceanBox/");
		// SystemPropreties.setPropretie("videoPath", "/home/pi/OceanBox/video/");

		// Les properties ci-dessous influent directement sur l'application
		ClientProperties.setPropertie("wakingHour", "08:30:00");
		ClientProperties.setPropertie("activateStandby", "true");
		ClientProperties.setPropertie("timeBeforeStandby", "00:00:20");

		SystemProperties.setPropertie("vlcCMD", "/Applications/VLC.app/Contents/MacOS/VLC");
		// SystemPropreties.setPropretie("vlcCMD", "/usr/bin/vlc");

		SystemProperties.setPropertie("mediaInfoCMD", "/usr/local/bin/mediainfo");
		// SystemPropreties.setPropretie("mediaInfoCMD", "/usr/bin/mediainfo");

		SystemProperties.setPropertie("localLogPath",
				"/Users/abdelbenamara/eclipse-workspace-java/Boitier/OceanBox/logs/");

		// -----------------------------------------------------------------------------------
	}

}

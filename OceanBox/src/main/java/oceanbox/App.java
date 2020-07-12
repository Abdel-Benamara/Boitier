package oceanbox;

import java.io.FileNotFoundException;
import java.io.IOException;

import oceanbox.system.Content;
import oceanbox.system.Download;
import oceanbox.utils.OceanLogger;
import oceanbox.utils.bdd.DatabaseLoader;
import oceanbox.utils.propreties.ClientProperties;
import oceanbox.utils.propreties.SystemProperties;

/**
 * Cette classe est le point d'entrée de l'application
 */
public class App {

	public static void main(String[] args) throws FileNotFoundException, IOException {

		// On initialise les fichiers de propriétés puis on les complète avec les
		// informations qui sont sur la base de données
		SystemProperties.initProperties();
		ClientProperties.initProperties();

		// ---------------------------------------------
		// Pour tester sur ordinateur uniquement
		try {
			ReglagesDeTest.initPersonalSettings();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// ---------------------------------------------

		OceanLogger.initLogger();
		DatabaseLoader.setPropretiesFromDatabase();

		// ---------------------------------------------
		// Pour tester sur ordinateur uniquement
		try {
			ReglagesDeTest.initPersonalSettings();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// ---------------------------------------------

		Content c = Content.getInstance();
		Download d = Download.getInstance();

		d.initDownload();
		c.initVideos();
	}
}

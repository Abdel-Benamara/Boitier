package oceanbox.videosettings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import oceanbox.utils.propreties.SystemProperties;

/**
 * Cette classe gère la récupération des durées des vidéos
 */
public class MediaInfo {

	private String mediaInfoCmd;
	private static MediaInfo MEDIAINFO_INSTANCE = new MediaInfo();

	private MediaInfo() {
		this.mediaInfoCmd = SystemProperties.getPropertie("mediaInfoCMD");
	}

	/**
	 * Cette méthode permet de récupérer l'instance unique de MediaInfo
	 * 
	 * @return : l'instance unique de MediaInfo
	 */
	public static MediaInfo getInstance() {
		if (MEDIAINFO_INSTANCE == null)
			MEDIAINFO_INSTANCE = new MediaInfo();

		return MEDIAINFO_INSTANCE;
	}

	/**
	 * Cette méthode permet de récupérer la durée de la vidéo ciblée
	 * 
	 * @param path : le chemin vers la vidéo ciblée
	 * @return : la durée en secondes de la vidéo
	 */
	public int getDuration(String path) {

		String cmd = mediaInfoCmd + " --Inform=" + '\'' + "Video" + ";" + "%Duration%" + '\'' + " " + path;

		ProcessBuilder processbuild = new ProcessBuilder("sh", "-c", cmd);

		long durationInMillisec = 0;

		try {
			Process p = processbuild.start();
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			durationInMillisec = Long.parseLong(reader.readLine());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

		int durationInSec = (int) (durationInMillisec / 1000);
		return durationInSec;
	}
}

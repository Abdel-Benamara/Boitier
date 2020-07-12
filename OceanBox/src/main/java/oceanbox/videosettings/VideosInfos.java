package oceanbox.videosettings;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import oceanbox.utils.propreties.SystemProperties;

/**
 * Cette classe gère les interactions avec les vidéos du répertoire local
 */
public class VideosInfos {

	private File[] videos;
	private Map<Integer, Video> videosInfos;
	private int totalDurationOfVideos;
	private static VideosInfos VIDEOSINFOS_INSTANCE_FOR_CONTENT = new VideosInfos();
	private static VideosInfos VIDEOSINFOS_INSTANCE_FOR_DOWNLOAD = new VideosInfos();

	private VideosInfos() {
	}

	/**
	 * Cette méthode permet de récupérer l'instance unique de VideosInfos
	 * 
	 * @param forContent : true si l'instance est celle destinée à la classe
	 *                   Content, false si elle est destinée à la classe Download
	 * @return : l'instance unique de VideosInfos
	 */
	public static VideosInfos getInstance(boolean forContent) {
		if (forContent) {
			if (VIDEOSINFOS_INSTANCE_FOR_CONTENT == null)
				VIDEOSINFOS_INSTANCE_FOR_CONTENT = new VideosInfos();

			return VIDEOSINFOS_INSTANCE_FOR_CONTENT;

		} else {
			if (VIDEOSINFOS_INSTANCE_FOR_DOWNLOAD == null)
				VIDEOSINFOS_INSTANCE_FOR_DOWNLOAD = new VideosInfos();

			return VIDEOSINFOS_INSTANCE_FOR_DOWNLOAD;
		}
	}

	/**
	 * Cette méthode initialise la Map ayant pour clés les numéros de vidéos et pour
	 * valeur les objets Video
	 */
	public void initVideosInfos() {
		videos = new File(SystemProperties.getPropertie("videoPath")).listFiles();

		videosInfos = new TreeMap<Integer, Video>();

		totalDurationOfVideos = 0;

		for (File videoFile : videos) {
			if (videoFile.getName().startsWith("."))
				continue;

			Video v = new Video(videoFile.getAbsolutePath());

			if (videosInfos.containsKey(v.getNumero())
					&& videosInfos.get(v.getNumero()).getDate().compareTo(v.getDate()) < 0)
				v = videosInfos.get(v.getNumero());

			videosInfos.put(v.getNumero(), v);

			totalDurationOfVideos += videosInfos.get(v.getNumero()).getDuration();
		}
	}

	/**
	 * Cette méthode permet d'avoir la durée totale cumulée de toutes les vidéos
	 * 
	 * @return : le temps cumulé en secondes
	 */
	public int getTotalDurationOfVideos() {
		return totalDurationOfVideos;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();

		for (Video v : videosInfos.values()) {
			str.append(v.toString() + "\n");
		}

		return str.toString();
	}

	/**
	 * Cette méthode permet d'avoir la Map de vidéos du cycle de diffusion en cours
	 * 
	 * @return : la Map de vidéos
	 */
	public Map<Integer, Video> getVideosInfos() {
		return videosInfos;
	}
}

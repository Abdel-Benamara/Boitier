package oceanbox.system;

import java.io.IOException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import oceanbox.ReglagesDeTest;
import oceanbox.utils.InternetConnectivity;
import oceanbox.utils.bdd.DatabaseLoader;
import oceanbox.utils.ftp.RecoverVideoFromServer;
import oceanbox.utils.propreties.ClientProperties;
import oceanbox.utils.propreties.SystemProperties;
import oceanbox.videosettings.Video;
import oceanbox.videosettings.VideosInfos;

/**
 * Cette classe gère le téléchargement quotidien des vidéos
 */
public class Download {

	private Timer timeToDownload;
	private VideosInfos objectVideosInfos;
	private Map<Integer, Video> videosInfos;
	private Content content;
	private static Download DOWNLOAD_INSTANCE = new Download();

	public Download() {
		content = Content.getInstance();
		objectVideosInfos = VideosInfos.getInstance(false);
	}

	public static Download getInstance() {
		if (DOWNLOAD_INSTANCE == null)
			DOWNLOAD_INSTANCE = new Download();

		return DOWNLOAD_INSTANCE;
	}

	/**
	 * Cette méthode initialise le Timer qui déclenchera le téléchargement
	 */
	public void initDownload() {
		timeToDownload = new Timer();
		timeToDownload.schedule(new DownloadTask(), initTimeBeforeDownload());
	}

	/**
	 * Cette méthode calcule la Date du prochain téléchargement
	 * 
	 * @return : la Date du prochain téléchargement
	 */
	private Date initTimeBeforeDownload() {

		objectVideosInfos.initVideosInfos();

		long total = objectVideosInfos.getTotalDurationOfVideos();

		LocalDateTime ldt = LocalDateTime.now();

		String[] times = ClientProperties.getPropertie("wakingHour").split(":");

		int hour = Integer.parseInt(times[0]);
		int minutes = Integer.parseInt(times[1]);
		int seconds = Integer.parseInt(times[2]);

		ldt = LocalDateTime.of(ldt.getYear(), ldt.getMonth(), ldt.getDayOfMonth() + 1, hour, minutes, seconds);

		ldt = ldt.minus((long) total, ChronoUnit.SECONDS);

		Date nextDownloadTime = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());

		try {
			ClientProperties.setPropertie("nextDownloadTime", nextDownloadTime.toString());
			DatabaseLoader.setNextDownloadTime();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(nextDownloadTime);
		return nextDownloadTime;
	}

	/**
	 * Cette classe lance le téléchargement dans un Thread à part
	 */
	private class DownloadTask extends TimerTask {

		@Override
		public void run() {

			objectVideosInfos.initVideosInfos();

			DatabaseLoader.setPropretiesFromDatabase();
			
			// ---------------------------------------------
			// Pour tester sur ordinateur uniquement
			try {
				ReglagesDeTest.initPersonalSettings();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			// ---------------------------------------------

			videosInfos = objectVideosInfos.getVideosInfos();

			RecoverVideoFromServer.setVideoRegex();

			if (videosInfos.isEmpty()) {

				if (!InternetConnectivity.checkConnexion(SystemProperties.getPropertie("ftpIP"))) {
					System.out.println("Connectez-vous à internet pour pouvoir télécharger votre première vidéo");
					System.exit(0);
				}

				System.out.println(">>>>>>> Début du 1er téléchargement de l'application");

				RecoverVideoFromServer.setPrefixeNomVideo("2-1-2000_");
				RecoverVideoFromServer.setVideosFiles();

				for (int i : RecoverVideoFromServer.getVideosFiles()) {
					RecoverVideoFromServer.ftpsDownloadFile(i);
				}

				content.initInfosOfVideos();

			} else {

				if (!InternetConnectivity.checkConnexion(SystemProperties.getPropertie("ftpIP"))) {
					initDownload();
					return;
				}

				RecoverVideoFromServer.setVideosFiles();

				if (RecoverVideoFromServer.getPrefixeNomVideo().equals(
						videosInfos.get(RecoverVideoFromServer.getVideosFiles().size()).getStringOfDate() + "_")) {
					System.out.println(">>>>>>> J+1 déjà en mémoire");
					initDownload();
					return;
				}

				System.out.println("- \n>>>>>>> Début du téléchargement J+1");

				int n = 0;
				int[] currentVideoInfos;

				for (int i : RecoverVideoFromServer.getVideosFiles()) {
					n = i;

					currentVideoInfos = content.getCurrentVideoInfos(videosInfos, content.repereForDiffusion(), false);

					if (currentVideoInfos[0] <= i)
						if (i <= videosInfos.size())
							try {
								Thread.sleep((videosInfos.get(i).getDuration() - currentVideoInfos[1]) * 1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}

					RecoverVideoFromServer.ftpsDownloadFile(i);
				}

				if (n > 0) {
					for (int j = n + 1; j <= videosInfos.size(); j++) {
						RecoverVideoFromServer.deleteLocalOldFile(j);
					}
				}
			}

			System.out.println("- \n>>>>>>> Fin du téléchargement");

			RecoverVideoFromServer.uploadFtpLogFile();

			initDownload();
		}
	}
}

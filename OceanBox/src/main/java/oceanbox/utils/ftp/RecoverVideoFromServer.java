package oceanbox.utils.ftp;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPSClient;

import oceanbox.utils.OceanLogger;
import oceanbox.utils.ftp.FtpsConnectionHandler;
import oceanbox.utils.propreties.SystemProperties;;

/**
 * Cette classe gère les téléchargements des vidéos depuis le serveur FTP
 */
public class RecoverVideoFromServer {

	private static Set<Integer> videosFiles;
	private static String remotePath;
	private static String videoPrefix;
	private static String videoSuffix;
	private static String localPath;
	private static FTPSClient ftpsClient;

	public RecoverVideoFromServer() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Cette méthode permet de lancer l'upload du fichier de log sur le serveur FTP
	 */
	public static void uploadFtpLogFile() {
		ftpsClient = FtpsConnectionHandler.ftpsConnection();
		OceanLogger.uploadLogFileOnServer(ftpsClient);
		OceanLogger.initLogger();
		FtpsConnectionHandler.ftpsDeconnection();
	}

	/**
	 * Cette méthode télécharge la video ayant le numéro souhaité
	 * 
	 * @param numVideo : le numéro de la vidéo à télécharger
	 */
	public static void ftpsDownloadFile(int numVideo) {
		ftpsClient = FtpsConnectionHandler.ftpsConnection();

		String nameOfTargetVideo = videoPrefix + numVideo + videoSuffix;

		try {
			File localFile = new File(localPath + nameOfTargetVideo);
			localFile.createNewFile();

			OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(localFile, false));

			System.out.println("- \n*** Début du téléchargement du fichier : " + nameOfTargetVideo + " ***");
			OceanLogger.log(Level.INFO, "*** Début du téléchargement du fichier : " + nameOfTargetVideo + " ***");
			ftpsClient.retrieveFile(remotePath + nameOfTargetVideo, outputStream);
			OceanLogger.log(Level.INFO, "*** Fin du téléchargement du fichier : " + nameOfTargetVideo + " ***");
			System.out.println("*** Fin du téléchargement du fichier : " + nameOfTargetVideo + " ***");

			// Téléchargement du paquet terminé, on ferme les flux
			outputStream.close();

			deleteLocalOldFile(numVideo);

			// On ferme la connexion FTP
			FtpsConnectionHandler.ftpsDeconnection();

		} catch (IOException e) {
			// LOGGER.error(e.getMessage());
			e.printStackTrace();
		} finally {
			if (ftpsClient.isConnected()) {
				// On ferme la connexion FTP
				FtpsConnectionHandler.ftpsDeconnection();
			}
		}
	}

	/**
	 * Cette méthode supprime la vidéo ayant le numéro souhaité
	 * 
	 * @param videoNum : le numéro de la vidéo à supprimer
	 */
	public static void deleteLocalOldFile(int videoNum) {
		// Suppression de l'ancien paquet en local
		for (String videoName : new File(localPath).list()) {
			if (!videoName.startsWith(videoPrefix) && videoName.endsWith(videoNum + videoSuffix)) {
				new File(localPath + videoName).delete();
				OceanLogger.log(Level.INFO, "*** Suppression du fichier local : " + videoName + " ***");
				System.out.println("*** Suppression du fichier local : " + videoName + " ***");
			}
		}
	}

	/**
	 * Cette méthode initialise le Set " videosFiles " avec les numéros des vidéos à
	 * télécharger depuis le serveur
	 */
	public static void setVideosFiles() {
		ftpsClient = FtpsConnectionHandler.ftpsConnection();
		videosFiles = new TreeSet<Integer>();

		try {
			for (FTPFile paquet : ftpsClient.listFiles(remotePath)) {
				if (paquet.getName().startsWith(videoPrefix)) {
					videosFiles.add(Integer
							.parseInt(paquet.getName().substring(videoPrefix.length(), videoPrefix.length() + 1)));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Cette méthode initialise les informations nécessaires au téléchargement des
	 * vidéos du jour suivant
	 */
	public static void setVideoRegex() {
		remotePath = SystemProperties.getPropertie("ftpVideoPath");
		localPath = SystemProperties.getPropertie("videoPath");
		videoPrefix = LocalDateTime.now().plusDays(1).getDayOfMonth() + "-" + LocalDateTime.now().getMonthValue() + "-"
				+ LocalDateTime.now().getYear() + "_";
		videoSuffix = ".mp4";
	}

	/**
	 * Cette méthode renvoie le Set qui contient les numéros des vidéos à
	 * télécharger
	 * 
	 * @return : le Set de numéros des vidéos
	 */
	public static Set<Integer> getVideosFiles() {
		return videosFiles;
	}

	/**
	 * Cette méthode renvoie le préfixe contenant la date des vidéos à télécharger
	 * 
	 * @return : le préfixe contenant la date du lendemain
	 */
	public static String getPrefixeNomVideo() {
		return videoPrefix;
	}

	/**
	 * Cette méthode met à jour le préfixe en cas de besoin
	 * 
	 * @param videoPrefix : la nouvelle valeur que doit prendre le préfixe
	 */
	public static void setPrefixeNomVideo(String videoPrefix) {
		RecoverVideoFromServer.videoPrefix = videoPrefix;
	}
}

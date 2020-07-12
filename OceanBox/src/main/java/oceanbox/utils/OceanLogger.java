package oceanbox.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.net.ftp.FTPSClient;

import oceanbox.utils.propreties.SystemProperties;

/**
 * Cette classe gère tout ce qui est relatif aux logs de l'application
 */
public class OceanLogger {

	private static final String LOGGER_NAME = "Ocean Logger";
	private static final String PREFIXE = "OceanBox";
	private static final String FILE_EXTENSION = ".log";
	private static FileHandler fh;
	private static String localFileName, remoteFileName;

	public static Logger logger;

	public OceanLogger() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Cette méthode initialise le fichier de log
	 */
	public static void initLogger() {
		String logFileName = initLogFileName();
		remoteFileName = SystemProperties.getPropertie("remoteLogPath") + logFileName;
		localFileName = SystemProperties.getPropertie("localLogPath") + logFileName;

		for (String fileName : new File(SystemProperties.getPropertie("localLogPath")).list()) {
			if (!fileName.equals(logFileName))
				new File(SystemProperties.getPropertie("localLogPath") + fileName).delete();
		}

		logger = Logger.getLogger(LOGGER_NAME);

		try {
			fh = new FileHandler(localFileName, true);

		} catch (IOException e) {
			e.printStackTrace();
		}

		logger.addHandler(fh);
		logger.setUseParentHandlers(false);
	}

	/**
	 * Cette méthode permet de générer le nom du fichier de log avec la date du jour
	 * 
	 * @return : le nom du fichier de log avec la bonne date du jour
	 */
	private static String initLogFileName() {
		String currentDate = LocalDateTime.now().getDayOfMonth() + "-" + LocalDateTime.now().getMonthValue() + "-"
				+ LocalDateTime.now().getYear();

		String fileName = PREFIXE + SystemProperties.getPropertie("oceanBoxNumber") + "_" + currentDate
				+ FILE_EXTENSION;

		return fileName;
	}

	/**
	 * Cette méthode permet d'ajouter un commentaire/log dans le fichier de log en
	 * précisant le niveau du message (Info, Severe, etc..).
	 * 
	 * @param msgLevel : Le niveau d'importance du message
	 * @param msg      : le message
	 */
	public static void log(Level msgLevel, String msg) {
		fh.setLevel(msgLevel);
		logger.log(msgLevel, msg);
	}

	/**
	 * Cette méthode permet d'uploader le fichier de log sur le serveur FTP
	 * 
	 * @param ftpsClient : le client FTP
	 */
	public static void uploadLogFileOnServer(FTPSClient ftpsClient) {
		File file = new File(localFileName);
		FileInputStream fis = null;

		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			logger.log(Level.WARNING, "Fichier à uploader introuvable : " + localFileName);
			e.printStackTrace();
		}

		if (fis != null) {
			try {
				if (ftpsClient.storeFile(remoteFileName, fis)) {
					logger.log(Level.INFO, "Stockage du fichier sur serveur OK");
					System.out.println("Stockage du fichier " + remoteFileName + " sur serveur OK");

				} else {
					logger.log(Level.INFO, "Stockage du fichier sur serveur NOT OK");
					System.out.println("Stockage du fichier sur serveur NOT OK");
				}

			} catch (IOException e) {
				logger.log(Level.WARNING, "Stockage du fichier sur serveur NOT OK");
				e.printStackTrace();

			} finally {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		} else {
			logger.log(Level.WARNING, "Stockage du fichier sur serveur impossible");
			System.out.println("Stockage du fichier sur serveur impossible : " + remoteFileName);
		}

		fh.close();

		file.delete();
	}
}

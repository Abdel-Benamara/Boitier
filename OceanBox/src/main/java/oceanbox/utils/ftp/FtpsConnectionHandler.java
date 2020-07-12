package oceanbox.utils.ftp;

import java.io.IOException;
import java.util.logging.Level;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPSClient;

import oceanbox.utils.OceanLogger;
import oceanbox.utils.propreties.SystemProperties;

/**
 * Cette classe gère les accès au serveur FTP de manière sécurisée grâce à FTPS
 *
 */
public class FtpsConnectionHandler {

	private static final String FTP_IP = SystemProperties.getPropertie("ftpIP");
	private static final String FTP_USER = SystemProperties.getPropertie("ftpUser");
	private static final String FTP_PWD = SystemProperties.getPropertie("ftpPassword");
	private static final int FTP_PORT = Integer.parseInt(SystemProperties.getPropertie("ftpPort"));
	private static final FTPSClient FTPS_CLIENT = new FTPSClient();

	public FtpsConnectionHandler() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Cette méthode permet de se connecter au serveur FTP
	 * 
	 * @return : le client FTP
	 */
	public static FTPSClient ftpsConnection() {
		if (!FTPS_CLIENT.isConnected()) {
			try {
				FTPS_CLIENT.connect(FTP_IP, FTP_PORT);
				FTPS_CLIENT.login(FTP_USER, FTP_PWD);
				FTPS_CLIENT.execPBSZ(0);
				FTPS_CLIENT.execPROT("P");
				FTPS_CLIENT.enterLocalPassiveMode();
				FTPS_CLIENT.setFileType(FTP.BINARY_FILE_TYPE);
				OceanLogger.log(Level.INFO, "FTPS Connection OK");
			} catch (IOException e) {
				OceanLogger.log(Level.SEVERE, "FTPS Connection NOT OK");
				e.printStackTrace();
			}
		}

		return FTPS_CLIENT;
	}

	/**
	 * Cette méthode permet de se déconnecter du serveur FTP
	 */
	public static void ftpsDeconnection() {
		try {
			FTPS_CLIENT.logout();
			FTPS_CLIENT.disconnect();
			OceanLogger.log(Level.INFO, "FTPS Déconnexion OK");
		} catch (IOException e) {
			OceanLogger.log(Level.SEVERE, "FTPS Déconnexion NOT OK");
			e.printStackTrace();
		}
	}

	/**
	 * Cette méthode permet de récupérer le client FTP
	 * 
	 * @return : une instance de client FTP
	 */
	public FTPSClient getFtpsClient() {
		return FTPS_CLIENT;
	}
}

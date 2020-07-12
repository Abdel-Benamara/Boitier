package oceanbox.utils.bdd;

import java.io.IOException;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.logging.Level;

import oceanbox.utils.InternetConnectivity;
import oceanbox.utils.OceanLogger;
import oceanbox.utils.propreties.ClientProperties;
import oceanbox.utils.propreties.SystemProperties;

/**
 * Cette classe récupère les informations dans la base de données et les écrit
 * dans les fichiers de propriétés
 */
public class DatabaseLoader {

	// JDBC driver's name
	static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

	// Database credentials
	private static final String USER = SystemProperties.getPropertie("dbUser");
	private static final String PASS = SystemProperties.getPropertie("dbPassword");
	private static final String HOST = SystemProperties.getPropertie("dbIP");
	private static final String PORT = SystemProperties.getPropertie("dbPort");
	private static Statement stmt = null;
	private static PreparedStatement preparedStatement = null;
	private static Connection conn = null;

	private DatabaseLoader() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Cette méthode permet de se connecter à la base de données
	 * 
	 * @throws SQLException : si une erreur SQL est survenue
	 */
	private static void dbConnection() throws SQLException {
		if (conn == null || conn.isClosed()) {
			try {
				try {
					Class.forName(JDBC_DRIVER);
					OceanLogger.log(Level.INFO, "Driver Loaded Successfully");
				} catch (ClassNotFoundException ex) {
					OceanLogger.log(Level.INFO, "Driver Failed To Load");
					OceanLogger.log(Level.INFO, ex.getMessage());
				}
				// Open a connection
				OceanLogger.log(Level.INFO, "Connecting to oceandatabase...");

				conn = DriverManager.getConnection("jdbc:mysql://" + HOST + ":" + PORT + "/oceandatabase", USER, PASS);

				OceanLogger.log(Level.INFO, "Connected to oceandatabase successfully !");

			} catch (SQLException se) {
				// Handle errors for JDBC
				se.printStackTrace();
			} catch (Exception e) {
				// Handle errors for Class.forName
				e.printStackTrace();
			}
		}
	}

	/**
	 * Cette méthode permet de se déconnecter de la base de données
	 * 
	 * @throws SQLException : si une erreur SQL est survenue
	 */
	private static void dbDeconnexion() throws SQLException {
		if (conn != null || !conn.isClosed()) {
			try {
				conn.close();
			} catch (SQLException ex) {
				OceanLogger.log(Level.SEVERE, ex.toString());
				System.out.println("coucou 1");
			}
		}
	}

	/**
	 * Cette méthode permet de mettre à jour les fichiers de propriétés à partir des
	 * informations stockées dans la base de données
	 */
	public static void setPropretiesFromDatabase() {

		if (!InternetConnectivity.checkConnexion(HOST))
			return;

		try {
			dbConnection();

			stmt = conn.createStatement();

			InetAddress IP = InetAddress.getLocalHost();
			SystemProperties.setPropertie("oceanBoxIP", IP.getHostAddress());

			preparedStatement = conn.prepareStatement("UPDATE config SET oceanBoxIP = ? WHERE oceanBoxNumber = ?");
			preparedStatement.setString(1, SystemProperties.getPropertie("oceanBoxIP"));
			preparedStatement.setString(2, SystemProperties.getPropertie("oceanBoxNumber"));
			preparedStatement.executeUpdate();

			ResultSet result = stmt.executeQuery("SELECT * FROM db, ftp");

			while (result.next()) {
				SystemProperties.setPropertie("dbUser", result.getString("dbUser"));
				SystemProperties.setPropertie("dbIP", result.getString("dbIP"));
				SystemProperties.setPropertie("dbPassword", result.getString("dbPassword"));
				SystemProperties.setPropertie("dbPort", result.getString("dbPort"));
				SystemProperties.setPropertie("ftpUser", result.getString("ftpUser"));
				SystemProperties.setPropertie("ftpIP", result.getString("ftpIP"));
				SystemProperties.setPropertie("ftpPassword", result.getString("ftpPassword"));
				SystemProperties.setPropertie("ftpPort", result.getString("ftpPort"));
				SystemProperties.setPropertie("ftpVideoPath", result.getString("ftpVideoPath"));
			}

			preparedStatement = conn
					.prepareStatement("SELECT * FROM config, client WHERE oceanBoxNumber = ? AND userId = idClient");
			preparedStatement.setString(1, SystemProperties.getPropertie("oceanBoxNumber"));

			result = preparedStatement.executeQuery();

			while (result.next()) {
				ClientProperties.setPropertie("userId", result.getString("idClient"));
				ClientProperties.setPropertie("userName", result.getString("userName"));
				ClientProperties.setPropertie("userType", result.getString("userType"));
				ClientProperties.setPropertie("videoStream", result.getString("videoStream"));
				ClientProperties.setPropertie("wakingHour", result.getString("wakingHour"));
				ClientProperties.setPropertie("nextDownloadTime", result.getString("nextDownloadTime"));
				ClientProperties.setPropertie("activateStandby", result.getString("activateStandby"));
				ClientProperties.setPropertie("timeBeforeStandby", result.getString("timeBeforeStandby"));

				SystemProperties.setPropertie("videoPath", result.getString("videoPath"));
				SystemProperties.setPropertie("mediaInfoCMD", result.getString("mediaInfoCMD"));
				SystemProperties.setPropertie("vlcCMD", result.getString("vlcCMD"));
				SystemProperties.setPropertie("remoteLogPath", result.getString("remoteLogPath"));
				SystemProperties.setPropertie("localLogPath", result.getString("localLogPath"));
			}

		} catch (SQLException | IOException ex) {
			OceanLogger.log(Level.SEVERE, ex.toString());
			System.out.println("coucou 2");
		} finally {
			try {
				dbDeconnexion();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Cette méthode permet de mettre à jour dans la base de données quand le
	 * téléchargement des vidéos aura lieu la prochaine fois
	 */
	public static void setNextDownloadTime() {

		try {
			dbConnection();

			stmt = conn.createStatement();

			preparedStatement = conn
					.prepareStatement("UPDATE config SET nextDownloadTime = ? WHERE oceanBoxNumber = ?");
			preparedStatement.setString(1, ClientProperties.getPropertie("nextDownloadTime"));
			preparedStatement.setString(2, SystemProperties.getPropertie("oceanBoxNumber"));
			preparedStatement.executeUpdate();

		} catch (SQLException ex) {
			OceanLogger.log(Level.INFO, ex.toString());
		} finally {
			try {
				dbDeconnexion();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}

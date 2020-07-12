package oceanbox.videosettings;

import java.io.IOException;

import oceanbox.utils.propreties.SystemProperties;

/**
 * Cette classe est une implémentation de VideoPlayer pour VLC
 */
public class JVlcPlayer implements VideoPlayer {

	private String cmd;
	private Process vlcPlayerProcess;
	private static JVlcPlayer VLC_INSTANCE = new JVlcPlayer();

	private JVlcPlayer() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				stopPlayerProcess();
			}
		});
	}
	
	public static JVlcPlayer getInstance() {
		if (VLC_INSTANCE == null)
			VLC_INSTANCE = new JVlcPlayer();
		
		return VLC_INSTANCE;
	}

	public Process play(String videoPath, int time) {
		// Ici il faut mettre le chemin absolu vers VLC
		cmd = SystemProperties.getPropertie("vlcCMD");	
		cmd += " --start-time=" + time;
		cmd += " " +videoPath + " -f";

		// Pour Windows il faut mettre "CMD", sinon il faut mettre "sh"
		// ProcessBuilder playerBuilder = new ProcessBuilder("CMD", "-c", cmd);
		ProcessBuilder playerBuilder = new ProcessBuilder("sh", "-c", cmd);

		try {
			vlcPlayerProcess = playerBuilder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return vlcPlayerProcess;
	}

	public void stopPlayerProcess() {
		if (vlcPlayerProcess != null) {
			vlcPlayerProcess.destroy();
			vlcPlayerProcess = null;
		}
	}
}

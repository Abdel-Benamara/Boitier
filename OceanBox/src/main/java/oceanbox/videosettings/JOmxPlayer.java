package oceanbox.videosettings;

import java.io.IOException;

/**
 * Cette classe est une implémentation de VideoPlayer pour OmxPlayer
 */
public class JOmxPlayer implements VideoPlayer {

	private String cmd;
	private Process omxPlayerProcess;
	private static JOmxPlayer OMX_INSTANCE = new JOmxPlayer();

	private JOmxPlayer() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				stopPlayerProcess();
			}
		});
	}
	
	public static JOmxPlayer getInstance() {
		if (OMX_INSTANCE == null)
			OMX_INSTANCE = new JOmxPlayer();
		
		return OMX_INSTANCE;
	}

	public Process play(String videoPath, int time) {
		cmd = "omxplayer";
		cmd += " --advanced";
		cmd += " --pos " + time;
		cmd += " " + videoPath;

		ProcessBuilder playerBuilder = new ProcessBuilder("sh", "-c", cmd);

		try {
			omxPlayerProcess = playerBuilder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return omxPlayerProcess;
	}

	public void stopPlayerProcess() {
		if (omxPlayerProcess != null) {
			try {
				omxPlayerProcess.getOutputStream().write('q');
				omxPlayerProcess.getOutputStream().flush();
				omxPlayerProcess = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

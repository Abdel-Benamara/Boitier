package oceanbox.veille;

import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import oceanbox.system.Content;
import oceanbox.utils.propreties.ClientProperties;

/**
 * Cette classe est une implémentation de Veille pour une entrée clavier
 */
public class VeilleScanner implements Veille {

	private Timer timeBeforeStandby;
	private Boolean sleepMode;
	private static VeilleScanner VEILLE_INSTANCE = new VeilleScanner();

	public VeilleScanner() {
		this.sleepMode = false;
		Thread VeilleScannerThread = new Thread(() -> {
			if (ClientProperties.getPropertie("activateStandby").equals("1"))
				initStandby();
			Scanner sc = new Scanner(System.in);
			String entry = "";
			while (!entry.equals("exit")) {
				entry = sc.next();
				if (entry.equals("sleep"))
					goInStandby();
				else {
					System.out.println(">>> Motion Detected!");
					update();
				}
			}
			sc.close();
			System.exit(0);
		});

		VeilleScannerThread.start();
	}

	public static VeilleScanner getInstance() {
		if (VEILLE_INSTANCE == null)
			VEILLE_INSTANCE = new VeilleScanner();

		return VEILLE_INSTANCE;
	}

	@Override
	public void goInStandby() {
		System.out.println(">>> Get in veille !");
		sleepMode = true;
		Content.getInstance().stopDiffusion();

	}

	@Override
	public void update() {
		if (!sleepMode && ClientProperties.getPropertie("activateStandby").equals("1")) {
			pushStandby();
		} else {
			goOutStandby();
		}
	}

	@Override
	public void goOutStandby() {
		System.out.println(">>> Get out veille");
		if (ClientProperties.getPropertie("activateStandby").equals("1"))
			initStandby();
		sleepMode = false;
	}

	@Override
	public void pushStandby() {
		timeBeforeStandby.cancel();
		initStandby();
	}

	@Override
	public void initStandby() {
		timeBeforeStandby = new Timer();
		timeBeforeStandby.schedule(new VeilleTask(), initMiliSecondsBeforeStandby());
	}

	@Override
	public long initMiliSecondsBeforeStandby() {
		String[] times = ClientProperties.getPropertie("timeBeforeStandby").split(":");
		return 1000 * ((Integer.parseInt(times[0]) * 3600) + (Integer.parseInt(times[1]) * 60)
				+ Integer.parseInt(times[2]));
	}

	/**
	 * Cette classe interne met l'application en veille dans un Thread à part
	 */
	private class VeilleTask extends TimerTask {
		@Override
		public void run() {
			goInStandby();
		}
	}

	@Override
	public boolean isSleepMode() {
		return sleepMode;
	}
}

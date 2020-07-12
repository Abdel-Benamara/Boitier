package oceanbox.veille;

import java.util.Timer;
import java.util.TimerTask;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import oceanbox.system.Content;
import oceanbox.utils.propreties.ClientProperties;

/**
 * Cette classe est une implémentation de Veille pour un détecteur de mouvement
 */
public class VeilleDMV implements Veille {

	private Timer timeBeforeStandby;
	private Boolean sleepMode;
	private static VeilleDMV VEILLE_INSTANCE = new VeilleDMV();

	public VeilleDMV() {
		this.sleepMode = false;
		if (ClientProperties.getPropertie("activateStandby").equals("1")) {
			initMotionSensorListner();
		}
	}
	
	public static VeilleDMV getInstance() {
		if (VEILLE_INSTANCE == null)
			VEILLE_INSTANCE = new VeilleDMV();

		return VEILLE_INSTANCE;
	}

	private void initMotionSensorListner() {
		initStandby();

		// create gpio controller
		final GpioController gpioSensor = GpioFactory.getInstance();
		final GpioPinDigitalInput sensor = gpioSensor.provisionDigitalInputPin(RaspiPin.GPIO_07,
				PinPullResistance.PULL_DOWN);

		// create and register gpio pin listener
		sensor.addListener(new GpioPinListenerDigital() {
			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				if (event.getState().isHigh()) {
					System.out.println(">>> Motion Detected!");
					update();
				}
			}
		});
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

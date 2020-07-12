package oceanbox.veille;

/**
 * Cette interface définit les méthodes de gestion de la veille de l'application
 */
public interface Veille {

	/**
	 * Cette méthode met l'application en veille
	 */
	public void goInStandby();

	/**
	 * Cette méthode met à jour les propriétés de veille de l'application
	 */
	public void update();

	/**
	 * Cette méthode sort l'application de l'état de veille
	 */
	public void goOutStandby();

	/**
	 * Cette méthode repousse la prochaine veille automatique
	 */
	public void pushStandby();

	/**
	 * Cette méthode initialise les propriétés de veille automatique
	 */
	public void initStandby();

	/**
	 * Cette méthode permet de calculer le temps restant en millisecondes avant la
	 * prochaine mise en veille
	 * 
	 * @return : la durée en millisecondes au format de nombre long de Java
	 */
	public long initMiliSecondsBeforeStandby();

	/**
	 * Cette méthode permet de savoir l'application est en veille
	 * 
	 * @return : true si l'application est en veille, false sinon
	 */
	public boolean isSleepMode();
}

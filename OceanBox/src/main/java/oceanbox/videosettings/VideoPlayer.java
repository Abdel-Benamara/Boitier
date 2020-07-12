package oceanbox.videosettings;

/**
 * Cette interface définit les méthodes de lecteurs de vidéos
 */
public interface VideoPlayer {

	/**
	 * Cette méthode lit la vidéo cible en commençant sa lecture à la durée voulue
	 * 
	 * @param videoPath : chemin absolu vers la vidéo à lire
	 * @param time      : durée de la vidéo à laquelle doit commencer la lecture
	 * @return : un processus qui va lire la vidéo hors de ce programme Java
	 */
	public Process play(String videoPath, int time);

	/**
	 * Cette méthode met fin à la lecture de la vidéo et au processus qui la lisait
	 */
	public void stopPlayerProcess();

}

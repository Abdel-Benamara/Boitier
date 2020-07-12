package oceanbox.videosettings;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Cette classe représente une vidéo en objet Java pour pouvoir appeler
 * certaines méthodes dessus
 */
public class Video {

	private String path;
	private int duration;
	private String name;
	private String extension;
	private int numero;
	private Date date;
	private String stringOfDate;

	public Video(String path) {
		this.path = path;
		initNameAttribute();
		this.duration = MediaInfo.getInstance().getDuration(this.path);
	}

	/**
	 * Cette méthode partitionne le nom de fichier de la vidéo pour initialiser les
	 * attributs de classe correspondant
	 */
	private void initNameAttribute() {
		String[] splitOnSlash = path.split("\\\\|/");
		this.name = splitOnSlash[splitOnSlash.length - 1];

		String[] nameSplit = this.name.split("\\.");
		this.extension = nameSplit[1];

		String[] beginSplit = nameSplit[0].split("_");
		this.stringOfDate = beginSplit[0];
		try {
			this.date = new SimpleDateFormat("dd-MM-yyyy").parse(beginSplit[0]);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		this.numero = Integer.parseInt(beginSplit[1]);
	}

	@Override
	public String toString() {
		return "Video [path=" + path + ", duration=" + duration + ", name=" + name + ", extension=" + extension
				+ ", numero=" + numero + ", date=" + date + ", stringOfDate=" + stringOfDate + "]";
	}

	public String getPath() {
		return path;
	}

	public int getDuration() {
		return duration;
	}

	public String getName() {
		return name;
	}

	public String getExtension() {
		return extension;
	}

	public int getNumero() {
		return numero;
	}

	public Date getDate() {
		return date;
	}

	public String getStringOfDate() {
		return stringOfDate;
	}
}

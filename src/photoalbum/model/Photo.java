package photoalbum.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import javafx.scene.image.Image;

/**
 * Represents a photo. A photo is not unique, and there can be multiple photo objects
 * pointing to the same file on the filesystem.
 * @author Paul Warner & Kenny Zhang
 *
 */
public class Photo implements Serializable {
	
	private static final long serialVersionUID = -8750705225265712462L;
	
	/**
	 * Path to this photo.
	 */
	private String photoPath;
	
	/**
	 * tags represented as (key, value) pairs.
	 */
	private HashMap<String, String> tags;
	
	/**
	 * Date this photo was taken.
	 */
	private Calendar date;
	
	/**
	 * This photo's caption
	 */
	private String caption;
	
	/**
	 * @return A set of all tag keys this photo has.
	 */
	public Set<String> getTags() {
		return tags.keySet();
	}
	
	/**
	 * Check if the photo has a given tag.
	 * @param tag The tag key to check.
	 * @return true if the photo a value for that tag.
	 */
	public boolean hasTag(String tag) {
		return tags.containsKey(tag);
	}
	
	/**
	 * Check if photo has a tag, and that tag has the given value
	 * @param key
	 * @param value 
	 * @return true if photo's tag key is value, false otherwise.
	 */
	public boolean isTaggedsAs(String key, String value) {
		return hasTag(key) && tags.get(key).equals(value);
	}
	
	/**
	 * Set the given tag to the given value.
	 * @param key
	 * @param val
	 */
	public void setTag(String key, String val) {
		tags.put(key, val);
	}
	
	/**
	 * remove the given tag from this photo.
	 * @param key
	 */
	public void removeTag(String key) {
		tags.remove(key);
	}
	
	/**
	 * Get the value of the tag represented by the given key.
	 * @param key
	 * @return The value of the given tag.
	 */
	public String getTagValue(String key) {
		return tags.get(key);
	}
	
	/**
	 * @return The caption of this image, or if this image has no caption, simply "no caption"
	 */
	public String getCaption() {
		if (caption == null || caption.equals("")) {
			return "no caption";
		} 
		return caption;
	}
	
	/**
	 * @param caption new caption for this photo.
	 */
	public void setCaption(String caption) {
		this.caption = caption.equals("") ? null : caption;
	}
	
	public Photo(String path) {
		File file = new File(path);
		if (file.exists()) {
			Date modDate = new Date(file.lastModified());
			date = Calendar.getInstance();
			date.setTime(modDate);
			photoalbum.util.Services.setExtrasToZero(date);
		} else {
			
		}
		this.photoPath = path;
		this.tags = new HashMap<String, String>();
	}
	
	/**
	 * @return A string in the format dd-mm-yyyy for the date this photo was taken.
	 */
	public String getDateString() {
		SimpleDateFormat d = new SimpleDateFormat("dd-MM-yyyy");
		d.setTimeZone(date.getTimeZone());
		d.setCalendar(date);
		return d.format(date.getTime());
	}
	
	/**
	 * @return The photo path this photo is represented by.
	 */
	public String getPhotoPath() {
		return photoPath;
	}
	
	/**
	 * Load the image stored at photoPath and return it.
	 * @return Image stored in the given file path.
	 */
	public Image getImage() {
		try {
			return new Image(new FileInputStream(photoPath));
		} catch (FileNotFoundException e) {
			return null;
		}
	}
	
	/**
	 * Write this object to the given file steam.
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(photoPath);
		out.writeObject(tags);
		out.writeObject(date);
		out.writeObject(caption);
	}
	
	/**
	 * Read an instance of this object in from the given file stream.
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		photoPath = (String)in.readObject();
		tags = (HashMap<String, String>)in.readObject();
		date = (Calendar)in.readObject();
		caption = (String)in.readObject();
	}
	
	/**
	 * @return The date this photo was taken.
	 */
	public Calendar getDate() {
		return date; 
	}
}

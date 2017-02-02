package photoalbum.model;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a group of photos stored under a common name. Albums are uniquely
 * identified by a user by each having a unique name. 
 * @author Paul Warner & Kenny Zhang
 *
 */
public class Album implements Serializable, Comparable<Album> {
	private static final long serialVersionUID = -3681392020379689028L;
	
	/**
	 * This album's name.
	 */
	String albumName;
	
	/**
	 * All photos stored in this album.
	 */
	ArrayList<Photo> photos;
	
	/**
	 * Date of the oldest photo in this album.
	 */
	Calendar oldestDate;
	
	/**
	 * Date of oldest photo in this album represented as a string.
	 */
	String oldestDateString = "";
	
	/**
	 * Date of newest photo in this album represented as a string.
	 */
	String newestDateString = "";
	
	/**
	 * Date of the newest photo in this album.
	 */
	Calendar newestDate; 
	
	/**
	 * String array that stores the newest and oldest date.
	 */
	String[] dateRange = new String[2];
	
	/*
	 * create a new album with the name albumName
	 */
	public Album(String albumName) {
		super();
		this.albumName = albumName;
		photos = new ArrayList<Photo>();
	}
	
	/**
	 * This method returns the number of photos currently in the album by accessing the photos arraylist
	 * @return the number of photos in the album
	 */
	
	public int getPhotoCount() {
		return photos.size();
	}
	
	/**
	 * This method searchs through the list of photos stored in this album and finds the oldest date present.
	 */
	// FIXED THIS METHOD SO THAT IT WILL CORRECTLY SET OLDEST DATE IF OLDEST IS FIRST	
	public void findOldestDate() {
		Calendar oldestTemp = Calendar.getInstance();
		photoalbum.util.Services.setExtrasToZero(oldestTemp);
		boolean first = true;
		for(Photo p:photos) {
			if(first) {
				oldestTemp = p.getDate();
				first = false;
				oldestDateString = p.getDateString();
				continue;
			}
			
			if(!oldestTemp.before(p.getDate())) {
				oldestTemp = p.getDate();
				oldestDateString = p.getDateString();
			} 	
		}
		oldestDate = oldestTemp; 
		dateRange[0] = oldestDateString; 
	}
	
	/**
	 * This method searches through the list of photos and finds the newest date present. 
	 */
	
	// FIXED THIS METHOD SO THAT IT WILL CORRECTLY SET NEWEST DATE IF NEWEST IS FIRST
	public void findNewestDate() {
		Calendar newestTemp = Calendar.getInstance();
		photoalbum.util.Services.setExtrasToZero(newestTemp);
		boolean first = true;
		for(Photo p: photos) {
			if(first) {
				newestTemp = p.getDate();
				first = false;
				newestDateString = p.getDateString();
				continue;
			}
			
			if(!newestTemp.after(p.getDate())) {
				newestTemp = p.getDate();
				newestDateString = p.getDateString();
			}
		}
		newestDate = newestTemp;
		dateRange[1] = newestDateString; 
	}
	
	/**
	 * This method returns the range of dates of photos in this album.
	 * @return an array of size two containing string representing the oldest and highest date.
	 */
	
	public String[] getDateRange() {
		return dateRange; 
	}
	
	/**
	 * Retrieves the oldest date present within the album 
	 * @return A string containing the oldest date. 
	 */
	
	public String getOldestDate() {
		return oldestDateString; 
	}
	
	/**
	 * Retrieves the newest date present within the album
	 * @return A string containing the newest date. 
	 */
	
	public String getNewestDate() {
		return newestDateString;
	}

	/**
	 * Write this object to the given object stream
	 * @param out Stream that objects will be written to.
	 * @throws IOException If error when writing object.
	 */
	// CHANGED SERIALIZATION OBJECTS TO WORK WITH NEW FIELDS
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(albumName);
		out.writeObject(photos);
		out.writeObject(oldestDate);
		out.writeObject(oldestDateString);
		out.writeObject(newestDateString);
		out.writeObject(newestDate);
		out.writeObject(dateRange);
	}
	
	/**
	 * Read in an instance of Album from the given output stream.
	 * @param in input stream to read from.
	 * @throws IOException When error on writing object.
	 * @throws ClassNotFoundException When type of read object is unknown.
	 */
	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		albumName = (String)in.readObject();
		photos = (ArrayList<Photo>)in.readObject();
		oldestDate = (Calendar)in.readObject();
		oldestDateString = (String)in.readObject();
		newestDateString = (String)in.readObject();
		newestDate = (Calendar)in.readObject();
		dateRange  = (String[])in.readObject();
	}

	/**
	 * Return this album's name.
	 * @return this album's name.
	 */
	public String getAlbumName() {
		return albumName;
	}

	/**
	 * Set this albums current name.
	 * @param albumName
	 */
	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}
	
	/**
	 * Return all this album's photos.
	 * @return all photos in this album
	 */
	public ArrayList<Photo> getPhotos() {
		return photos;
	}	
	
	/**
	 * Add the given photo object to this album. If the photo is already contained in this album,
	 * it is not added and false is returned. Otherwise return true.
	 * @param p Photo to be added.
	 * @return True of the photo was added successfully, false otherwise.
	 */
	public boolean addPhoto(Photo p) {
		if (!photos.contains(p)) {
			photos.add(p);
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return albumName;
	}

	@Override
	public int compareTo(Album arg0) {
		return albumName.compareTo(arg0.getAlbumName());
	}
	
	/**
	 * Get all tags stored in this particular album.
	 * @return All unique tags on photos in this album.
	 */
	public Set<String> getAllTags() {
		HashSet<String> s = new HashSet<String>();
		for (Photo p : photos) {
			s.addAll(p.getTags());
		}
		return s;
	}
	
	/**
	 * Remove the photo p from this album.
	 * @param p
	 */
	public void removePhoto(Photo p) {
		photos.remove(p);
	}
	
}

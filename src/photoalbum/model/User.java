package photoalbum.model;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a single user in this program. A user is uniquely identified by the program
 * by his username, case sensitive. Each user contains zero or more albums that hold photos.
 * @author Paul Warner & Kenny Zhang
 */
public class User implements Serializable {
	private static final long serialVersionUID = 582056250847964587L;
	
	/**
	 * This users unique username
	 */
	private String username;
	
	/**
	 * All albums this user has stored according to their albumName.
	 */
	private HashMap<String, Album> albums;

	@Override
	public String toString() {
		return username;
	}

	/**
	 * @param username The username of the given user.
	 */
	public User(String username) {
		super();
		this.username = username;
		albums = new HashMap<String, Album>();
	}

	/**
	 * @return This user's username.
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * Write this user out to teh given output stream.
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(username);
		out.writeObject(albums);
	}
	
	/**
	 * Read in an instance of this object from the given input stream.
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		username = (String)in.readObject();
		albums = (HashMap<String, Album>)in.readObject();
	}
	
	public Album getAlbum(String albumname) {
		return albums.get(albumname);
	}
	
	/**
	 * Create a new album and store it in this user.
	 * @param albumname The name of the new album.
	 * @return Whether or not the album was successfully inserted.
	 */
	public boolean addAlbum(String albumname) {
		if (getAlbum(albumname) != null || !albumname.matches(".*\\w.*")){
			return false;
		}
		albums.put(albumname, new Album(albumname));
		return true;
	}
	
	/**
	 * Add the new album object to this user.
	 * @param newAlbum the new album to be added
	 * @return false if the newAlbum is already in this user, and true otherwise.
	 */
	public boolean addAlbum(Album newAlbum) {
		if (albums.get(newAlbum.getAlbumName()) != null || newAlbum.getAlbumName().isEmpty())
			return false;
		albums.put(newAlbum.getAlbumName(), newAlbum);
		return true;
	}
	
	/**
	 * @return A set containing all albums in this user.
	 */
	public Set<Album> getAllAlbums() {
		return new HashSet<Album>(albums.values());
	}
	
	/**
	 * Rename an album stored in this user. If the album is not found, then return false.
	 * @param oldname The old name of the album.
	 * @param newname The new name of the album.
	 * @return Whether or not the album was renamed successfully.
	 */
	public boolean renameAlbum(String oldname, String newname) {
		if (albums.containsKey(newname) || !albums.containsKey(oldname))
			return false;
		Album a = albums.get(oldname);
		albums.remove(oldname);
		a.setAlbumName(newname);
		albums.put(newname, a);
		return true;
	}
	
	/**
	 * Remove an album.
	 * @param albumname name of album to be removed
	 */
	public void removeAlbum(String albumname) {
		albums.remove(albumname);
	}
	
	/**
	 * Find and return a all tags (as in keys) in all albums owned by this user.
	 * @return A set containing all unique tags.
	 */
	public Set<String> getAllTags() {
		HashSet<String> s = new HashSet<String>();
		for (String a : albums.keySet()) {
			s.addAll(albums.get(a).getAllTags());
		}
		return s;
	}
	
	/**
	 * Find and return all unique photos in albums owned by this user.
	 * @return A set of all photos.
	 */
	public Set<Photo> getAllPhotos() {
		Set<Photo> s = new HashSet<Photo>();
		for (String al : albums.keySet()) {
			s.addAll(albums.get(al).getPhotos());
		}
		return s;
	}
	
	/**
	 * This find all photos within a specified date range and returns these photos in a set object 
	 * @param date1 The lower date bound. (Older date) 
	 * @param date2 The upper date bound. (Upper date)
	 * @return A set of photos that fall within the specified date range defined by date1 and date2
	 */
	
	public Set<Photo> findWithinRange(Date date1, Date date2) {
		Set<Photo> photosInRange = new HashSet<Photo>();
		Set<Photo> allPhotos = getAllPhotos();
		Calendar calDate1 = Calendar.getInstance();
		Calendar calDate2 = Calendar.getInstance(); 
		
		calDate1.setTime(date1);
		photoalbum.util.Services.setExtrasToZero(calDate1);
		calDate2.setTime(date2);
		photoalbum.util.Services.setExtrasToZero(calDate2);
		
		for(Photo p:allPhotos) {
			if (calDate1.equals(calDate2)) {
				if (p.getDate().equals(calDate1))
					photosInRange.add(p);
			}  else {
				if(p.getDate().before(calDate1) || p.getDate().after(calDate2)) {
					continue;
				}
				photosInRange.add(p);
			}
		}
		
		return photosInRange; 
	}
}

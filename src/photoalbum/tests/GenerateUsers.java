package photoalbum.tests;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import photoalbum.model.Album;
import photoalbum.model.Photo;
import photoalbum.model.User;

/**
 * A quick and dirty utility class to generate a set of sample users 
 * to test the program with.
 * @author Paul Warner & Kenny Zhang
 *
 */

public class GenerateUsers {
	
	/**
	 * Usernames used in generation.
	 */
	static String[] usernames = new String[] {
		"Amy",
		"Billy",
		"Cindy",
		"Darell",
		"Frances",
		"Greg",
		"Hannah"
	};
	
	/**
	 * Album names used in generation.
	 */
	static String[] albumnames = new String[] {
		"food",
		"posters",
		"boats",
		"family",
		"naked pictures",
		"old google image search",
		"wallpapers"
	};
	
	/**
	 * Paths to test photos.
	 */
	// NOTE: ALL TESTPHOTOS OPERATE WITH RELATIVE PATHS!!!
	static String[] testphotos = new String[] {
		"data/test1.jpg",
		"data/test2.jpg",
		"data/test3.jpg",
		"data/test4.png",
		"data/test5.png",
		"data/test6.png",
		"data/test7.jpg",
		"data/test8.jpg",
		"data/test9.jpg"
	};
	
	/**
	 * Test photo objects. Created here without tags or captions, which are added later.
	 */
	static Photo[] photos = new Photo[] {
			new Photo(testphotos[0]),
			new Photo(testphotos[1]),
			new Photo(testphotos[2]),
			new Photo(testphotos[3]),
			new Photo(testphotos[4]),
			new Photo(testphotos[5]),
			new Photo(testphotos[6]),
			new Photo(testphotos[7]),
			new Photo(testphotos[8]),
	};
	
	/**
	 * Album objects to be split up among users.
	 */
	static Album[] albums = new Album[] {
			new Album("food"),
			new Album("boats"),
			new Album("photos"),
			new Album("naked pictures"),
			new Album("Gross pictures of dead fish")
	};
	
	/**
	 * All captions used in caption generation.
	 */
	static String[] captions = new String[] {
			"a fun day at the park",
			"Why is there a bird in there?",
			"are you a cop? You have to tell me if you're a cop! Please tell me you're not a cop! I really hope you're not a cop!",
			"b",
			"There are lots of people with nothing at all",
			"She falls and she faints, and she never does arise",
			"By the pricking of my thumbs, something wicked this way comes",
			"I'm really tripping right now",
			""
	};
	
	/**
	 * Statically add photos to albums.
	 */
	private static void addPhotosToAlbum() {
		
		// Amy has no albums
		
		// Bob
		// Album zero is empty
		albums[1].addPhoto(photos[0]);
		albums[1].addPhoto(photos[1]);
		
		// Albums 1 and 2 share photo 1
		albums[2].addPhoto(photos[1]);
		albums[2].addPhoto(photos[2]);
		albums[2].addPhoto(photos[3]);
		
		// Cindy's photos
		albums[3].addPhoto(photos[4]);
		albums[3].addPhoto(photos[5]);
		albums[3].addPhoto(photos[6]);
		
		// Albums 3 and 4 are completely different
		albums[4].addPhoto(photos[7]);
		albums[4].addPhoto(photos[8]);
	}
	
	/**
	 * Statically add captions and tags to photos.
	 */
	private static void addCaptionsToPhotos() {
		// Photo zero has no caption and no tags
		photos[1].setCaption(captions[8]); //empty caption
		
		photos[2].setTag("location", "Westeros");
		photos[2].setTag("person", "Jon Snow");
		
		photos[2].setCaption(captions[1]);

		photos[3].setCaption(captions[2]);		
		photos[3].setTag("location", "Camelot");
		photos[3].setTag("person", "King Pellinore");

		photos[4].setCaption(captions[3]);
		photos[4].setTag("location", "Middle Earth");
		photos[4].setTag("person", "Bilbo Baggins");
		photos[6].setTag("depressing?", "bittersweet");
		
		photos[5].setCaption(captions[4]);
		photos[5].setTag("location", "Mitakihara City");
		photos[5].setTag("person", "Madoka Kaname");
		photos[5].setTag("depressing?", "oh god yes");
		
		photos[6].setCaption(captions[5]);
		photos[6].setTag("location", "King Haggard's Castle");
		photos[6].setTag("person", "King Haggard");
		photos[6].setTag("depressing?", "bittersweet");
		
		photos[7].setCaption(captions[6]);
		photos[7].setTag("location", "Lower Tadfield");
		photos[7].setTag("person", "Aziphrale");
		photos[7].setTag("His gay friend", "Crowley");
		
		photos[8].setCaption(captions[7]);
		photos[8].setTag("location", "Coger and Dark's Carnival");
		photos[8].setTag("person", "Mr. Electrico");
	}

	/**
	 * Generate a series of test users, albums and objects for testing purposes.
	 * @param args ignored
	 */
	public static void main(String[] args) {
		addCaptionsToPhotos();
		addPhotosToAlbum();
		HashMap<String, User> users = new HashMap<String, User>();
		User u;
		users.put(usernames[0], new User(usernames[0]));
		u = new User(usernames[1]);
		u.addAlbum(albums[0]);
		u.addAlbum(albums[1]);
		users.put(usernames[1], u);
		u = new User(usernames[2]);
		u.addAlbum(albums[2]);
		u.addAlbum(albums[3]);
		users.put(usernames[2], u);
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("./users"));
			oos.writeObject(users);
			oos.close();
			System.out.println("Users generated!");
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
	}
}

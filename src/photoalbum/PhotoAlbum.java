package photoalbum;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Set;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import photoalbum.controller.MasterController;
import photoalbum.model.Album;
import photoalbum.model.Photo;
import photoalbum.model.User;

/**
 * A GUI Photo application written in java and javafx that allows users to store, tag, search
 * and organize photos in a multi-user environment.
 * @author Paul Warner & Kenny Zhang
 *
 */
public class PhotoAlbum extends Application {
	
	/**
	 * A HashMap instance that stores all user data
	 */
	private HashMap<String, User> users = new HashMap<String,User>();
	
	/**
	 * The currently logged in user. This is null if no one is logged on or admin is logged in.
	 */
	private User currentUser;
	
	/**
	 * The album currently being viewed.
	 */
	private Album currentAlbum;
	
	
	/**
	 * The photo currently being viewed
	 */
	private Photo currentPhoto;
	
	/**
	 * The path where we store our users
	 */
	String savePath = "./users";

	public static void main(String[] args)  {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) {
		loadState();
		primaryStage.setOnCloseRequest((event) -> saveState());
		switchScene(primaryStage, "login");
	}
	
	/**
	 * Load a new scene specified by fxmlFile on the stage primaryStage, as well as initalizing
	 * that new scene's initial state
	 * @param primaryStage the stage we are loading onto
	 * @param fxmlFile the scene we are loading
	 */
	
	public void switchScene(Stage primaryStage, String fxmlFile) {
		saveState();
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/view/"+fxmlFile+".fxml"));
		Parent root;
		try {
			root = (Parent)loader.load();	
			MasterController controller = loader.getController();
			controller.setApp(this);
			controller.init();
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Photo Album"); // could change title of window?
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	//added a set user method through which the admin is able to modify the hashmap. 
	
	
	/**
	 * Add a new user to the program u with the name s
	 * @param s the name of 
	 * @param u
	 */
	public void setUser(String s, User u) {
		users.put(s, u);
	}
	
	
	/**
	 * get the user with the given username
	 * @param username the name of the user
	 * @return a User object
	 */
	public User getUser(String username) {
		return users.get(username);
	}
	
	
	/**
	 * 
	 * @return A set of strings containing all users currently stored by the system.
	 */
	public Set<String> getAllUsers() {
		return users.keySet();
	}
	
	/**
	 * 
	 * @return The currently logged in user.
	 */
	public User getCurrentUser() {
		return currentUser;
	}
	
	/**
	 * 
	 * @return The album currently being viewed by the user.
	 */
	public Album getCurrentAlbum() {
		return currentAlbum;
	}
	
	
	/**
	 * Remove a user from the system
	 * @param userName the name of the user to be removed
	 */
	public void removeUser(String userName) {
		users.remove(userName);
	}
	
	/**
	 * Save the state of the program including all user data. This is performed whenever the window
	 * is closed.
	 */
	private void saveState() {
			try {
				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(savePath));
				oos.writeObject(users);
				oos.close();
			} catch (IOException e) {
				e.printStackTrace();
			} 
	}
	
	/**
	 * Load all saved user data into the program.
	 */
	@SuppressWarnings("unchecked")
	private void loadState() {
		try {
			ObjectInputStream iis = new ObjectInputStream(new FileInputStream(savePath));
			users = (HashMap<String, User>)iis.readObject();
			iis.close();
		} catch (FileNotFoundException e) {
			users = new HashMap<String, User>();
		} 
		catch (ClassNotFoundException e) {} 
		catch (IOException e) {}
	}
	
	/**
	 * Set currentUser to the user with the given username.
	 * @param username The name of the user we want to switch to.
	 */
	public void setCurrentUser(String username) {
		currentUser = users.get(username);
	}
	
	/**
	 * Set currentAlbum by searching currentUser for that album
	 * @param albumName The name of the album.
	 */
	public void setCurrentAlbum(String albumName) {
		if (currentUser != null) {
			currentAlbum = currentUser.getAlbum(albumName);
		}
	}
	
	/**
	 * Create and set a new current album with a given name.
	 * @param name the name of the new album
	 * @param photos All photos we want to add to this album.
	 */
	public void setCurrentAlbum(String name, Set<Photo> photos) {
		Album anon = new Album(name);
		for (Photo p : photos) {
			anon.addPhoto(p);
		}
		currentAlbum = anon;
	}
	
	
	/**
	 * set the current photo to the given photo
	 * @param p the photo we wish to set.
	 */
	public void setCurrentPhoto(Photo p) {
		this.currentPhoto = p;
	}
	
	/**
	 * get the current value of currentPhoto.
	 * @return the photo being viewed
	 */
	public Photo getCurrentPhoto() {
		return currentPhoto;
	}
}

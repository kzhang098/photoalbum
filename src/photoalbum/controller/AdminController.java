package photoalbum.controller;

import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import photoalbum.model.User;
import photoalbum.util.Services;

/**
 * The admin page allows a user to administrate users of the program. From here,
 * From this page the admin user can add and delete users from the program.
 * @author Paul Warner & Kenny Zhang
 *
 */
public class AdminController extends MasterController{
	
	/**
	 * The view of our users
	 */
	@FXML 
	ListView<String> listView;
	
	/**
	 * The text field where new usernames are entered
	 */
	@FXML 
	TextField userField;
	
	/**
	 * The button clicked when we add a new user
	 */
	@FXML 
	Button addUserButton;
	
	/**
	 * The button pressed when we delete a user
	 */
	@FXML
	Button deleteUserButton;
	
	
	/**
	 * The button pressed to log out
	 */
	@FXML
	Button logoutButton;
	
	/**
	 * The list containing all usernames in the system
	 */
	private ObservableList<String> users; 
	
	
	/**
	 * add a new user with the username specified in userField. If that user already exists, then
	 * an error dialog is created.
	 */
	@FXML 
	public void addUser(MouseEvent e) {
		if (!leftClick(e))
			return;
		
		String userName = userField.getText().trim();
		
		if(userName.isEmpty()) {
			Services.createAlert("Please input a valid userName.");
		} else if(users.contains(userName)){
			Services.createAlert("User currently exists. Please input another value." );
			userField.setText("");
		} else if(userName.equals("admin")) {
			Services.createAlert("Can't create a user with the name admin");
		} else {
		
			User u = new User(userName);
		
			//Adds information to to observable list 
		
			users.add(userName); 
			java.util.Collections.sort(users);
			listView.getSelectionModel().select(users.indexOf(userName));
		
			userField.setText(userName);
			//modifies the hashmap of users
		
			this.app.setUser(userName, u);
		}
	}
	
	/**
	 * Log out and return to the login screen
	 * @param event
	 */
	@FXML
	private void logout(MouseEvent event) {
		if(!leftClick(event))
			return;
		this.app.switchScene((Stage)logoutButton.getScene().getWindow(), "login");
	}
	
	/**
	 * delete the user that is currently selected in the listView. An error dialog is created
	 * if the list is empty or if no user is selected.
	 */
	@FXML
	public void deleteUser(MouseEvent e) {
		if(!leftClick(e))
			return;
		
		if(users.isEmpty()) {
			Services.createAlert("The list is empty. You cannot delete anything!");
		}
		
		int userIndex = listView.getSelectionModel().getSelectedIndex();
	
		if (userIndex == -1)
			Services.createAlert("No user is selected!");
		this.app.removeUser(users.get(userIndex)); //removes the user from the hashmap by using a userName gotten from arraylist
		users.remove(userIndex); //removes the user from the current view
		
	}
	
	@Override
	public void init() {
		users = FXCollections.observableArrayList();
		getUsers();
		java.util.Collections.sort(users);
		listView.setItems(users);
		listView.getSelectionModel().select(0);	
	}
	
	/**
	 * Get all users stored in the app and refresh the list of users in listView
	 */
	private void getUsers() {
		Set<String> usernames = this.app.getAllUsers();
		users.addAll(usernames);
	}
}

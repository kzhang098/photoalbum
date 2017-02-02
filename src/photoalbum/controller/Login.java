package photoalbum.controller;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import photoalbum.util.Services;

/**
 * A user logs in or logs out from this page. He can also close the application by pressing
 * the close button. Typing "admin" in all lowercase will allow the the administrator to log
 * in.
 * @author Paul Warner & Kenny Zhang
 *
 */
public class Login extends MasterController {
	
	/**
	 * Button pressed to log in.
	 */
	@FXML
	Button LoginButton;
	
	/**
	 * Button pressed to close the application
	 */
	@FXML 
	Button LoginCloseButton;
	
	/**
	 * Field where user types in username.
	 */
	@FXML 
	TextField LoginField;
	
	/**
	 * Method run when LoginClosedButton is pressed. Closes the application. 
	 */
	@FXML
	public void CloseClicked(MouseEvent e) {
		if (!leftClick(e))
			return;
		System.out.println("Closing...");
		Platform.exit();
	}
	
	/**
	 * Method run with login is clicked. Attempts to login as the user specified in the text field.
	 * If the given username is not in the system then it will create an error dialog.
	 * @param e
	 */
	@FXML
	public void LoginClicked(MouseEvent e) {
			if (!leftClick(e))
				return;
			String login = LoginField.getText().trim();
			System.out.println(app.getUser(login));
			if(login.equals("admin")) {
				this.app.switchScene ((Stage)LoginField.getScene().getWindow(),"admin");
			} else if(this.app.getUser(login) != null) {
				this.app.setCurrentUser(login);
				this.app.switchScene((Stage)LoginField.getScene().getWindow(), "albums");
			} else {
				Services.createAlert("The login id you have used is invalid. Please try again.");
			}
	}
	
	public void init() {} // Does nothing
}

package photoalbum.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;
import photoalbum.model.Album;

public class Services {
	/**
	 * Create an alert.
	 * @param message The message to be displayed.
	 */
	public static void createAlert(String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setContentText(message);
		alert.showAndWait();
	}
	
	/**
	 * Open a dialog to rename something.
	 * @param prompt Message to be displayed.
	 * @param oldtext The old name of the object to be changed.
	 * @param resp A function to be run in response to the user's answer.
	 */
	public static void openRenameDialog(String prompt, String oldtext, BiConsumer<String, String> resp) {
		TextInputDialog dialog = new TextInputDialog(oldtext);
		dialog.setTitle("Rename");
		dialog.setHeaderText(prompt);
		Optional<String> result = dialog.showAndWait();
		
		if (result.isPresent() && !result.get().equals(oldtext)){
			resp.accept(oldtext, result.get());			
		}
	}
	
	/**
	 * Open a dialog asking the user to confirm something.
	 * @param text The prompt to be displayed to the user
	 * @param func A function that accepts and processes the users response.
	 */
	public static void openConfirmationDialog( String text, Consumer<Boolean> func) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("cofirm");
		alert.setHeaderText(text);
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
		   func.accept(true);
		} else {
		   func.accept(false);
		}
	}
	
	/**
	 * Set extra fields in a calendar to zero so they can be compared more accurately.
	 * @param cal
	 */
	public static void setExtrasToZero(Calendar cal) {
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR, 0);
	}
	
	/**
	 * Open a dialog to ask a user to choose one of a group of albums.
	 * @param string The prompt to be displayed
	 * @param allAlbums All albums to be listed
	 * @param resp A function to respond to the user's choice.
	 */
	public static void openChooseDialog(String string, Set<Album> allAlbums, Consumer<Album> resp) {
		
		ArrayList<Album> albumList = new ArrayList<Album>();
		albumList.addAll(allAlbums);
		
		ChoiceDialog<Album> dialog = new ChoiceDialog<Album>(albumList.get(0),albumList); 
		
		dialog.setTitle("Move to Album");
		dialog.setHeaderText(string);
		Optional<Album> result = dialog.showAndWait();
				
		if(result.isPresent()) {
			resp.accept(result.get()); 
		}
		
	}
	
}

package photoalbum.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import photoalbum.model.Album;
import photoalbum.model.Photo;
import photoalbum.util.Services;

/**
 * A view that allows a user to view and manage a single photo. Actions include 
 * editing and deleting tags, re-captioning, deleting, and moving to a different album.
 * @author Paul Warner & Kenny Zhang
 *
 */
public class PhotoViewController extends MasterController {

	/**
	 * Photo currently being viewed
	 */
	Photo currentPhoto;
	
	/**
	 * Button to go back to the album this photo is stored in.
	 */
	@FXML
	Button backButton;
	
	/**
	 * image viewer the photo is displayed in.
	 */
	@FXML
	ImageView photoViewer;
	
	/**
	 * Displays all tags on the curret image.
	 */
	@FXML
	ListView<String> tagList;
	
	/**
	 * Button clicked when user wants to click the current object.
	 */
	@FXML
	Button saveTagButtton;
	
	/**
	 * Field where user enters a new value for a given tag.
	 */
	@FXML
	TextField tagValueField;
	
	/**
	 * Button to add a new tag to an image.
	 */
	@FXML
	Button addTagButton;
	
	/**
	 * Label that displays the date of image.
	 */
	@FXML
	Label dateLabel;
	
	/**
	 * Label that displays caption of image.
	 */
	@FXML
	Label captionLabel;
	
	/**
	 * Label that displays the currently logged in user.
	 */
	@FXML
	Label usernameLabel;
	
	/**
	 * Button pressed when the user wants to log out.
	 */
	@FXML
	Button logoutButton;
	
	/**
	 * Button presses when user wants to edit a caption.
	 */
	@FXML
	Button editCaptionButton;
	
	/**
	 * Button pressed when user wants to delete the current picture.
	 */
	@FXML
	Button deleteButton;
	
	/**
	 * Button pressed when user wants to delete the currently selected tag.
	 */
	@FXML
	Button deleteTagButton;
	
	/**
	 * Button pressed when user wants to move this photo to a different album.
	 */
	@FXML
	Button moveToButton;
	
	/**
	 * List containing all valid tags.
	 */
	ObservableList<String> tags;
	
	/**
	 * The last item selected on the list of tags.
	 */
	private String lastSelection;
	
	@Override
	public void init() {
		currentPhoto = this.app.getCurrentPhoto();
		photoViewer.setImage(currentPhoto.getImage());
		photoViewer.maxHeight(600);
		photoViewer.maxWidth(600);
        
        backButton.setText(app.getCurrentAlbum().getAlbumName());
        
        tagList.setFocusTraversable(true);
        
        usernameLabel.setText(this.app.getCurrentUser().toString());
        
        
        captionLabel.setText(currentPhoto.getCaption());
        
        dateLabel.setText("Taken on "+currentPhoto.getDateString());
        
        tags = FXCollections.observableArrayList();
        tags.setAll(currentPhoto.getTags());
        tagList.setItems(tags);
	}
	
	/**
	 * Move backward to view the containing album.
	 * @param e
	 */
	@FXML
	private void toPhotos(MouseEvent e) {
		if (leftClick(e)) {
			app.setCurrentPhoto(null);
			app.switchScene((Stage)dateLabel.getScene().getWindow(), "photos");
		}
	}
	
	/**
	 * Open a dialog to ask for a new tag/value pair
	 * @param e
	 */
	@FXML
	private void addTag(MouseEvent e) {
		if (!leftClick(e))
			return;
		photoalbum.util.Services.openRenameDialog("Enter a new tag name", "", (s1, s2) -> checkTagName(s2));
	}
	
	private String tag;
	
	/**
	 * Confirm the tag name given is a valid tag name. If not, create an error dialog.
	 * @param tagName
	 */
	private void checkTagName(String tagName) {
		String[] keySplit;
		keySplit = tagName.trim().split("( +)");
		if (currentPhoto.getTagValue(tagName) != null) { // tag already present
			photoalbum.util.Services.createAlert("There is already a tag with that name");
		} else if (!tagName.matches(".*\\w.*")){
			photoalbum.util.Services.createAlert("You can't have a tag name with just white space. Try again.");
		} else if(keySplit.length > 1 && keySplit[keySplit.length - 1].equals("KEY")) {
			photoalbum.util.Services.createAlert("Please make sure you pick a name without KEY at the end. Hey you found a secret!");
		} else {
			tag = tagName;
			photoalbum.util.Services.openRenameDialog("Enter a value for \""+tagName+"\"", "",
					(s1, s2)-> createNewTag(s1, s2));
		}
	}
	
	/**
	 * Opens a dialog to edit the caption on this picture
	 * @param e
	 */
	@FXML
	private void editCaption(MouseEvent e) {
		System.out.println("editing caption...");
		if (leftClick(e)) {
			photoalbum.util.Services.openRenameDialog("Select a new caption", 
					currentPhoto.getCaption(), 
					(s1, s2) -> {
						currentPhoto.setCaption(s2);
						captionLabel.setText(currentPhoto.getCaption());
					});
		}
	}
	
	/**
	 * Given a particular key and value, add a tag to this image.
	 * @param key The tag itself.
	 * @param tagValue The value of the tag.
	 */
	private void createNewTag(String key, String tagValue) {
		if (tagValue == null || !tagValue.matches(".*\\w.*")) {
			photoalbum.util.Services.createAlert("you must enter a value for this tag");
		} else {
			tagValue = tagValue.trim().toLowerCase();
			tag = tag.trim().toLowerCase();
			currentPhoto.setTag(tag, tagValue);
	        tags.setAll(currentPhoto.getTags());
		}
	}
	
	/**
	 * Logout of this particular view.
	 * @param event
	 */
	@FXML
	private void logout(MouseEvent event) {
		if (!leftClick(event))
			return;
		System.out.println("logging out...");
		app.setCurrentUser(null);
		app.setCurrentAlbum(null);
		app.setCurrentPhoto(null);
		app.switchScene((Stage)usernameLabel.getScene().getWindow(), "login");
	}
	
	/**
	 * Save the newly edited tag value to the photo. If the tag is invalid, create an error dialog.
	 * @param e
	 */
	@FXML
	private void saveTag(MouseEvent e) {
		if (!leftClick(e))
			return;
		String newValue = tagValueField.getText().trim();
		if (newValue.isEmpty()) {
			photoalbum.util.Services.createAlert("The new tag value you've entered is invalid");
			tagValueField.setText(currentPhoto.getTagValue(tagList.getSelectionModel().getSelectedItem()));
		}
		String key = tagList.getSelectionModel().getSelectedItem();
		if (key == null)
			return;
		currentPhoto.setTag(key, newValue);
	}
	
	/**
	 * Called whenever the selection of the tag is changed in order to reset the value currently stored
	 * in the value field.
	 * @param e
	 */
	@FXML
	private void tagListSelectionChanged(Event e) {
		if (tagList.getSelectionModel().getSelectedItem() == null) {
			return;
		}
		String key = tagList.getSelectionModel().getSelectedItem();
		if (key.equals(lastSelection)) {
			return;
		}
		lastSelection = key;
		String value = currentPhoto.getTagValue(key);
		tagValueField.setText(value);	
	}
	
	/**
	 * Confirm the user want to removethe current photo from the current album.
	 * @param e
	 */
	@FXML
	private void confirmRemovePhoto(MouseEvent e) {
		if (leftClick(e)) {
			photoalbum.util.Services.openConfirmationDialog("Are you sure you want to remove this photo?", 
					(b) -> removePhoto(b));
		}
	}
	
	/**
	 * remove the currently selected photo from its containing album. If the photo is removed
	 * successfully, then the user is sent back to view the containing album.
	 * @param b Whether or not to remove the current photo or not.
	 */
	private void removePhoto(boolean b) {
		if (b == true) {
			Album curr = this.app.getCurrentAlbum();
			curr.removePhoto(currentPhoto);
			app.setCurrentPhoto(null);
			app.switchScene((Stage)usernameLabel.getScene().getWindow(), "photos");
		}
	}
	
	/**
	 * Open a dialog to confirm the user wants to delete the currently selected tag.
	 * @param e
	 */
	@FXML
	private void deleteTag(MouseEvent e) {
		if (!leftClick(e))
			return;
		if(tagList.getSelectionModel().isEmpty()) {
			Services.createAlert("Please select a tag!");
		} else{
		
		photoalbum.util.Services.openConfirmationDialog("Delete "+
				tagList.getSelectionModel().getSelectedItem()+" ?",
				(Boolean b) -> checkDelete(b));
		}
	}
	
	/**
	 * Check the response given by deleteTag, and if true, delete the currently selected tag.
	 * @param b
	 */
	private void checkDelete(Boolean b) {
		if (b == true) {
			String tagName = tagList.getSelectionModel().getSelectedItem();
			currentPhoto.removeTag(tagName);
			tags.remove(tagName);
			tagList.setItems(tags);
		}
	}
	
	/**
	 * Open a dialog to get the album the user wants to move the
	 * current photo to.
	 */
	@FXML
	private void moveFunction(MouseEvent e) {
		if (!leftClick(e))
			return;
		photoalbum.util.Services.openChooseDialog("Please select the album you would like to move this photo to", 
		this.app.getCurrentUser().getAllAlbums(), (Album a) -> moveToAlbum(a)); 
	}
	
	/**
	 * Move the current photo.
	 * @param a The album the photo will be moved to.
	 */
	private void moveToAlbum(Album a) {
		this.app.getCurrentAlbum().getPhotos().remove(currentPhoto);
		a.addPhoto(currentPhoto);
		app.switchScene((Stage)moveToButton.getScene().getWindow(),"photos");
	}
}

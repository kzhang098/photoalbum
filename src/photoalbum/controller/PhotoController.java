package photoalbum.controller;

import java.io.File;
import java.util.ArrayList;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Callback;
import photoalbum.model.Photo;

/**
 * Here the user can view, manage and delete a set of photos.
 * @author Paul Warner & Kenny Zhang
 *
 */
public class PhotoController extends MasterController {
	
	/**
	 * Label showing username of current user
	 */
	@FXML
	private Label userLabel;
	
	/**
	 * Context menu shown when user right clicks on a particular photo.
	 */
	@FXML
	private ContextMenu photoContextMenu; 
	
	/**
	 * Menu item to edit a particular photo.
	 */
	@FXML
	private MenuItem editButton;
	
	/**
	 * Menu item to delete a particular photo.
	 */
	@FXML
	private MenuItem deleteButton;
	
	/**
	 * View uses to show an image when in slideshow mode.
	 */
	@FXML
	private ImageView slideshowView;
	
	/**
	 * A ListView used to show the photos when in thumbnail view.
	 */
	@FXML
	private ListView<Photo> thumbnailView;
	
	/**
	 * button used to advance the slideshow forward by one.
	 */
	@FXML
	Button forwardButton;
	
	/**
	 * Box that stores all slideshow widgets.
	 */
	@FXML
	HBox slideshowBox;
	
	/**
	 * Button to move backwards in the slideshow.
	 */
	@FXML
	Button backwardButton;
	
	/**
	 * Pane that the image view is wrapped in.
	 */
	@FXML
	StackPane slideshowPane;
	
	// ADDED TWO NEW PANES TO WRAP AROUND CONTROLS
	/**
	 * Panel that stores all album specific controls.
	 */
	@FXML
	Pane albumPane;
	
	/**
	 * Panel where controls specific to search results are stored.
	 */ 
	@FXML
	Pane searchPane;
	
	/**
	 * Choice box that allows the user to choose what type of search they would like to do.
	 */
	@FXML 
	ChoiceBox<String> chooseSearch;
	
	/**
	 * Choice box that lets the user choose how they would like to view their images.
	 */
	@FXML
	ChoiceBox<String> viewChooser;
	
	
	/**
	 * obslist storing all photos currently being viewed.
	 */
	private ObservableList<Photo> obslist;
	
	/**
	 * Store whether or not this controller is working within a storage box.
	 */
	private boolean isSearchResults; // ADDED VARIABLE TO DETERMINE IF THIS VIEW IS SHOWING SEARCH RESULTS

	/**
	 * Select a new file to add to the album.
	 * @param e
	 */
	@FXML
	public void addPhoto(MouseEvent e) {
		if (!leftClick(e))
			return;
		FileChooser filechooser = new FileChooser();
		filechooser.setTitle("add new photo");
		
		filechooser.getExtensionFilters().addAll(
				new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg","*.gif", "*jpe")
		);
		
		File file = filechooser.showOpenDialog(thumbnailView.getScene().getWindow());
		
			if(file != null) {
				Photo p = new Photo(file.toString());
				app.getCurrentAlbum().addPhoto(p);
			}
		
		obslist.setAll(app.getCurrentAlbum().getPhotos());
	}
	
	/**
	 * Even run when a user tries to delete photo. Calls confirmDelete to deal with result.
	 */
	public void deletePhoto(){
		photoalbum.util.Services.openConfirmationDialog("Are you sure you want to delete this photo?", 
				(b) -> confirmDelete(b));
	}
	
	/**
	 * Given a boolean variable, determine whether the user wishes to delete a particular file.
	 * @param b
	 */
	public void confirmDelete(boolean b) {
		if (b == true) {
			Photo p = thumbnailView.getSelectionModel().getSelectedItem();
			if (p != null) {
				app.getCurrentAlbum().removePhoto(p);
				obslist.setAll(app.getCurrentAlbum().getPhotos());
			}
		}
	}
	
	/**
	 * Open up the single photo view
	 */
	@FXML
	private void editPhoto() {
		switchToPhotoView();
	}
	
	/**
	 * move backward one window to the all albums view.
	 * @param e
	 */
	@FXML
	private void toAlbums(MouseEvent e) {
		if (leftClick(e)) {
			app.setCurrentAlbum(null);
			app.switchScene((Stage)thumbnailView.getScene().getWindow(), "albums");
		}
	}
	
	@Override
	public void init() {
		
		// ADDED INITALIZATION TO CHECK IF AN ALBUM IS SEARCH RESULTS (MEANING ITS NAME IS EMPTY)
		isSearchResults = app.getCurrentAlbum().getAlbumName().equals("") ? true : false;
		
		// DECIDE TO SHOW SEARCH RESULT CONTROLS OR ALBUM CONTROLS
		hideAlbumControls(isSearchResults);
		
		userLabel.setText(this.app.getCurrentUser().getUsername());
		
		obslist = FXCollections.observableArrayList();
		
		ArrayList<Photo> s = app.getCurrentAlbum().getPhotos();
		
		obslist.setAll(s);
		
		setupThumbnailView();
		setupSlideshow();
		thumbnailView.setContextMenu(null);
		
		viewChooser.getSelectionModel().selectedIndexProperty().addListener(
				new ChangeListener<Number> () {

					@Override
					public void changed(ObservableValue<? extends Number> observable, Number oldValue,
							Number newValue) {
						if (oldValue.equals(newValue))
							return;
						if (newValue.intValue() == 1) {
							showThumbs(false);
							selectSlideshowImage();
						} else if (newValue.intValue() == 0) {
							showThumbs(true);
						}
						
					}
					
				});
		showThumbs(true);
	}
	
	/**
	 * Exactly what it sounds like. Also shows the search controls. 
	 * @param hide Hide the album controls and show search controls if true, reverse if false.
	 */
	private void hideAlbumControls(boolean hide) {
		albumPane.setVisible(!hide);
		albumPane.setManaged(!hide);
		searchPane.setVisible(hide);
		searchPane.setManaged(hide);
	}
	
	/**
	 * Show the thumbnail view if its argument is true, and show the slideshow view if its argument is
	 * false.
	 * @param thumbs
	 */
	private void showThumbs(boolean thumbs) {
		slideshowBox.setManaged(!thumbs);
		slideshowBox.setVisible(!thumbs);
		thumbnailView.setManaged(thumbs);
		thumbnailView.setVisible(thumbs);
	}
	
	/**
	 * Perform initial setup for the thumbnail view.
	 */
	private void setupThumbnailView() {
		thumbnailView.setCellFactory(new Callback<ListView<Photo>, ListCell<Photo>> () {

		@Override
		public ListCell<Photo> call(ListView<Photo> arg0) {
			return new ThumbnailCell();
		}
		
		});
		
		thumbnailView.setItems(obslist);
		
		if (obslist.size() > 0) {
			thumbnailView.getSelectionModel().select(0);
		}
	}
	
	/**
	 * Get the currently selected item in the thumbnail view and select that image in the slideshow.
	 */
	private void selectSlideshowImage() {
		Photo p = thumbnailView.getSelectionModel().getSelectedItem();
		if (p == null) {
			thumbnailView.getSelectionModel().select(0);
			p = thumbnailView.getSelectionModel().getSelectedItem();
			if (p == null)
				return;
		}
		slideshowView.setImage(p.getImage());
		disableDirectionalButtons();
	}
	
	/**
	 * Perform initial setup for the slideshow view.
	 */
	private void setupSlideshow() {
		slideshowView.setPreserveRatio(true);
		slideshowView.fitWidthProperty().bind(slideshowPane.widthProperty());
		disableDirectionalButtons();
	}
	
	/**
	 * A class used to to display a Photo in a thumbnail cell.
	 * @author Paul Warner & Kenny Zhang
	 *
	 */
	private class ThumbnailCell extends ListCell<Photo> {
		@Override
		public void updateItem(Photo item, boolean empty) {
			super.updateItem(item, empty);
			if (empty == true) {
				setGraphic(null);
				setText(null);
			} else {
				setText(item.getCaption());	
				
	            ImageView v = new ImageView(item.getImage());
	            v.setPreserveRatio(true);
	            v.setFitWidth(100);
	            v.setFitHeight(100);
	            setGraphic(v);	
			}
		}
	}
	
	/**
	 * Handle a click on either the thumbnail view or the slideshow view to open the edit menu
	 * if they are double left clicked.
	 * @param e
	 */
	@FXML
	private void photoClicked(MouseEvent e) {
		if (isSearchResults)
			return;
		if (leftDoubleClick(e)) {
			switchToPhotoView();
		} else if (rightClick(e)) {
			showPhotoMenu(e);
		}
	}
	
	/**
	 * Open photo context method. Called in the slideshow and thumbnail views.
	 * @param e
	 */
	private void showPhotoMenu(MouseEvent e) {
		photoContextMenu.show(thumbnailView, e.getScreenX(), e.getScreenY());
	}
	
	/**
	 * Switch to the single photo view.
	 */
	private void switchToPhotoView() {
		Photo p = thumbnailView.getSelectionModel().getSelectedItem();
		if (p == null)
			return;
		this.app.setCurrentPhoto(p);
		this.app.switchScene((Stage)thumbnailView.getScene().getWindow(), "photo");
	}
	
	/**
	 * Given the current position of the selection of the thumbnail view, disable or
	 * enable the slideshow view buttons.
	 */
	@FXML
	void disableDirectionalButtons() {
		int i = thumbnailView.getSelectionModel().getSelectedIndex();
		if (i == 0) {
			backwardButton.setDisable(true);
		} else {
			backwardButton.setDisable(false);
		}
		if (i == obslist.size() -1) { // last item
			forwardButton.setDisable(true);
		} else {
			forwardButton.setDisable(false);
		}
	}
	
	/**
	 * Move the slideshow forward by one.
	 * @param e
	 */
	@FXML
	public void forwardClicked(MouseEvent e) {
		if (leftClick(e)) {
			int i = thumbnailView.getSelectionModel().getSelectedIndex();
			i = i == -1 ? 0 : ++i;
			thumbnailView.getSelectionModel().clearAndSelect(i);
			selectSlideshowImage();
		}
	}
	
	/**
	 * Move the slideshow backward by one.
	 * @param e
	 */
	@FXML
	public void backwardClicked(MouseEvent e) {
		if (leftClick(e)) {
			int i = thumbnailView.getSelectionModel().getSelectedIndex();
			i = i == -1 ? 0 : --i;
			thumbnailView.getSelectionModel().clearAndSelect(i);
			selectSlideshowImage();
		}
	}
	
	/**
	 * Logout the current user.
	 * @param e
	 */
	@FXML
	private void logout(MouseEvent e) {
		if (!leftClick(e))
			return;
		System.out.println("logging out...");
		app.setCurrentUser(null);
		app.setCurrentAlbum(null);
		app.switchScene((Stage)forwardButton.getScene().getWindow(), "login");
	}

}

package photoalbum.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import photoalbum.model.Album;
import photoalbum.util.Services;

/**
 * From this page, the user can view, manage and delete his albums. He can also search for
 * photos by tags or by date. 
 * @author Paul Warner & Kenny Zhang
 *
 */
public class Albums extends SearchableController {
	
	/**
	 * Button to add a new album.
	 */
	@FXML
	Button addButton;
	
	/**
	 * ListView containing all of a users albums.
	 */
	@FXML
	ListView<Album> albumList;
	
	/**
	 * The context menu brought up when a user right clicks on an album.
	 */
	@FXML
	ContextMenu albumMenu;
	
	/**
	 * The menu item to rename an album.
	 */
	@FXML
	MenuItem renameButton;
	
	/**
	 * The menu item to delete an album.
	 */
	@FXML
	MenuItem deleteButton;
	
	/**
	 * An observable list containing all albums.
	 */
	ObservableList<Album> albums;
	
	public void init() {
		super.init();
		
		setupAlbumList();
		for (Album a: app.getCurrentUser().getAllAlbums()) {
			a.findNewestDate();
			a.findOldestDate();
		}
	}
	
	/**
	 * Do initial setup for albumList 
	 */
	private void setupAlbumList() {
		albums = FXCollections.observableArrayList();
		refreshAlbums();
		
		albumList.setCellFactory(new Callback<ListView<Album>, ListCell<Album>> () {

			@Override
			public ListCell<Album> call(ListView<Album> arg0) {
				return new AlbumCell();
			}
			
			});
		
		albumList.setItems(albums);
		albumList.setContextMenu(null);
	}
	
	/**
	 * Refresh all albums.
	 */
	void refreshAlbums() {
		albums.setAll(app.getCurrentUser().getAllAlbums());
		albums.sort((s1, s2) -> s1.compareTo(s2));
	}

	/**
	 * Event handler for when albumList is clicked. This handles both left and right clicks
	 * @param event The click event.
	 */
	@FXML
	private void albumClicked(MouseEvent event) {
		if (albumList.getSelectionModel().getSelectedIndex() == -1)
			return;
		if (leftDoubleClick(event)) {
			switchToPhotoView();
		} else if (rightClick(event)) {
			showAlbumMenu(event);
		}
	}
	
	/**
	 * Called whenever the albumList is right clicked. Customizes and shows the ContextMenu.
	 * @param event
	 */
	private void showAlbumMenu(MouseEvent event) {
		String selectedAlbum = albumList.getSelectionModel().getSelectedItem().getAlbumName();
		renameButton.setText("Rename \""+selectedAlbum+"\"");
		deleteButton.setText("Delete \""+selectedAlbum+"\"");
		albumMenu.show(albumList, event.getScreenX(), event.getScreenY());
	}
	
	/**
	 * Switch to viewing a single album.
	 */
	private void switchToPhotoView() {
		app.setCurrentAlbum(albumList.getSelectionModel().getSelectedItem().getAlbumName());
		app.switchScene((Stage)addButton.getScene().getWindow(), "photos");
	}
	
	/**
	 * Open a dialog to rename an album.
	 * @param e
	 */
	@FXML 
	private void openRenameDialog(Event e) {
		photoalbum.util.Services.openRenameDialog("Enter a new album name",
				albumList.getSelectionModel().getSelectedItem().getAlbumName(),
				(s1, s2) -> checkedAlbumRename(s1, s2));
	}
	
	/**
	 * Check if the name receive in openNameDialog is a valid name. If not, create an error dialog.
	 * @param oldname the previous name of the album.
	 * @param newname The (proposed) new name of the album.
	 */
	private void checkedAlbumRename(String oldname, String newname) {
		if (!app.getCurrentUser().renameAlbum(oldname, newname)) {
			photoalbum.util.Services.createAlert("There is already an album with the name "+newname);
		}
		app.getCurrentUser().renameAlbum(oldname, newname);
		refreshAlbums();
	}
	
	/**
	 * Open confirm dialog for deleting an album.
	 * @param e
	 */
	@FXML
	private void openDeleteConfirmation(Event e) {
		photoalbum.util.Services.openConfirmationDialog("Delete "+
				albumList.getSelectionModel().getSelectedItem()+" ?",
				(Boolean b) -> checkDelete(b));
	}
	
	/**
	 * Respond to a users decision about whether or not to delete an album.
	 * @param b The users response, true if they wanted the album deleted, false otherwise.
	 */
	private void checkDelete(Boolean b) {
		if (b == true) {
			String albumName = albumList.getSelectionModel().getSelectedItem().getAlbumName();
			app.getCurrentUser().removeAlbum(albumName);
			refreshAlbums();
		}
	}
	
	/**
	 * Open a dialog to get a name for a new album.
	 * @param e
	 */
	@FXML
	private void addAlbum(MouseEvent e) {
		if (!leftClick(e))
			return;
		photoalbum.util.Services.openRenameDialog("Enter the name of the new album",
				"Album name", (s1, s2) -> createNewAlbum(s2));
	}
	
	/**
	 * Check if the new album name is a valid album name and if exists already.
	 * If it doesn't then create it and update list. Otherwise create an error dialog.
	 * @param s
	 */
	private void createNewAlbum(String s) {
		if (user.addAlbum(s) == false) {
			if(!s.matches(".*\\w.*")) {
				Services.createAlert("You cannot have an album with a name of spaces.");
			} else {
				photoalbum.util.Services.createAlert("An album already exists with the name "+s);
			}
		} else {
			refreshAlbums();
		}
	}
	
	/**
	 * A custom class for showing an album in a ListView. Albums are displayed with
	 * their name, and have tooltip giving information about that album.
	 * @author Paul Warner & Kenny Zhang
	 *
	 */
	private class AlbumCell extends ListCell<Album> {
		@Override
		public void updateItem(Album item, boolean empty) {
			super.updateItem(item, empty);
			if (empty) {
				this.setTooltip(null);
				this.setText(null);
			} else {
				String s = String.format("Number of Photos: %d\nDate Range: [%s, %s]\nOldest Photo: %s", 
						item.getPhotoCount(),
						item.getDateRange()[0],
						item.getDateRange()[1],
						item.getOldestDate());
				this.setText(item.getAlbumName());
				this.setTooltip(new Tooltip(s));
			}
		}
	}
	
	/**
	 * Logout the current user
	 * @param e
	 */
	@FXML
	private void logout(MouseEvent e) {
		if (!leftClick(e))
			return;
		app.setCurrentUser(null);
		app.switchScene((Stage)albumList.getScene().getWindow(), "login");
	}
	
}

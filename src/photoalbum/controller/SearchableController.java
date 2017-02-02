package photoalbum.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import photoalbum.model.Album;
import photoalbum.model.Photo;
import photoalbum.model.User;

/**
 * This class splits off all code dealing with searching sets of photos.
 * It works by loading a PhotoController into a pane, and hiding and showing
 * that pane as necessary. It can also use the results in that pane to
 * create a new album.
 * @author Paul Warner & Kenny Zhang
 */
public abstract class SearchableController extends MasterController {

	/**
	 * The current user.
	 */
	protected User user;
	
	/**
	 * label displaying logged in user's name
	 */
	@FXML
	Label userLabel;
	
	/**
	 * Button pressed when user wants to log out.
	 */
	@FXML
	Button logoutButton;
	
	/**
	 * Allows user to choose how they want to search for photos.
	 */
	@FXML
	ChoiceBox<String> chooseSearch;
	
	/**
	 * Allows user to choose particular key they wish to search for.
	 */
	@FXML
	ChoiceBox<String> keyChooser;
	
	/**
	 * Allows user to choose the value of the key chosen by keyChooser they wish to search for.
	 */
	@FXML
	ChoiceBox<String> valueChooser;
	
	/**
	 * Box used to wrap the date pickers.
	 */
	@FXML
	VBox dateChoosers;
	
	/**
	 * Used to select the start date of a date range
	 */
	@FXML
	DatePicker startDate;
	
	/**
	 * Used to select then end date of a date range.
	 */
	@FXML
	DatePicker endDate;
	
	/**
	 * List of all tags on all photos.
	 */
	ObservableList<String> allTags;
	
	/**
	 * List of all values of the currently selected tag.
	 */
	ObservableList<String> allValues;
	
	/**
	 * View used to display all present albums.
	 */
	@FXML
	ListView<String> albumList;
	
	/**
	 * Pane into which the results will be loaded.
	 */
	@FXML
	Pane resultsPane;

	@FXML
	VBox resultsBox;
	
	/**
	 * Button pressed to search for photos.
	 */
	@FXML
	Button searchButton;
	
	/**
	 * Controller for the photo results.
	 */
	PhotoController resultsController;
	
	@Override
	public void init() {
		// get user stuff
		user = this.app.getCurrentUser();
		userLabel.setText(user.getUsername());
		
		// hide results search pane
		resultsBox.setManaged(false);
		resultsBox.setVisible(false);
		
		
		// set up chooseSearch
		chooseSearchSetup();	
		// set up keySearch
		keyChooserSetup();
		// set up valueChooser
		valueChooserSetup();
		refreshTags();
		
		hideSearches();
	}
	
	/**
	 * Setup the ChoiceBox used to choose a search method.
	 */
	private void chooseSearchSetup() {
		chooseSearch.setItems(FXCollections.observableArrayList("search by...", "date", "tags"));
		chooseSearch.getSelectionModel().select("search by...");
		chooseSearch.getSelectionModel().selectedIndexProperty().addListener( new 
				ChangeListener<Number>() {	
			@SuppressWarnings("rawtypes")
			@Override
			public void changed(ObservableValue ov, Number value, Number newValue) {
				pickSearch(newValue);
			}
		});
	}
	
	/**
	 * Perform initial setup of the keyChooser, the choice box used to select a particular tag for searching.
	 */
	private void keyChooserSetup() {
		allTags = FXCollections.observableArrayList();
		keyChooser.setItems(allTags);
		keyChooser.setOnAction((event) -> {
			refreshValues();
		});
	}

	/**
	 * Perform initial setup of the valueChooser, the choice box used to select a the value
	 * of a particular tag for searching.
	 */
	private void valueChooserSetup() {
		allValues = FXCollections.observableArrayList();
		valueChooser.setOnAction((event) -> {
			if (valueChooser.getSelectionModel().getSelectedIndex() == 0)
				return;
			refreshTags();
		});
		allTags.add("VALUE");
		valueChooser.setItems(allValues);
	}
	
	/**
	 * Refresh the tags displayed by keyChooser.
	 */
	private void refreshTags() {
		String currentTag = keyChooser.getSelectionModel().getSelectedItem();
		allTags.clear();
		allTags.add("KEY");
		allTags.addAll(app.getCurrentUser().getAllTags());
		if (allTags.contains(currentTag) && currentTag != null)
			keyChooser.getSelectionModel().select(currentTag);
		else {
			keyChooser.getSelectionModel().select(0);
		}
	}
	
	/**
	 * Refresh the values displayed by valueChooser.
	 */
	private void refreshValues() {
		Set<Photo> photos = user.getAllPhotos();
		HashSet<String> set = new HashSet<String>();
		String key = keyChooser.getSelectionModel().getSelectedItem();
		String value = valueChooser.getSelectionModel().getSelectedItem();
		if (key == null)
			return;
		else if (key.equals("KEY")) {
			allValues.clear();
			allValues.add("VALUE");
			valueChooser.getSelectionModel().select(0);
		} else {
			for (Photo  p : photos) {
				if (p.hasTag(key)) {
					set.add(p.getTagValue(key));
				}
			}
			allValues.clear();
			allValues.add("ANY");
			allValues.addAll(set);
			if (allValues.contains(value)) 
				valueChooser.getSelectionModel().select(value);
			else
				valueChooser.getSelectionModel().select(0);
		}
	}
	
	/**
	 * Display a single method to search with.
	 * @param newValue
	 */
	private void pickSearch(Number newValue) {
		int v = (int)newValue;
		hideSearches();
		if (v == 1) { // search by date
			dateChoosers.setManaged(true);
			dateChoosers.setVisible(true);
			searchButton.setVisible(true);
			searchButton.setManaged(true);
		} else if (v == 2) { // search by tags
			keyChooser.setManaged(true);
			keyChooser.setVisible(true);
			valueChooser.setManaged(true);
			valueChooser.setVisible(true);
			searchButton.setVisible(true);
			searchButton.setManaged(true);
		}
	}
	
	/**
	 * Hide all widgets related to search functionality.
	 */
	private void hideSearches() {
		dateChoosers.setVisible(false);
		dateChoosers.setManaged(false);
		keyChooser.setVisible(false);
		keyChooser.setManaged(false);
		valueChooser.setManaged(false);
		valueChooser.setVisible(false);
		searchButton.setVisible(false);
		searchButton.setManaged(false);
	}
	
	/**
	 * Log as the current user.
	 * @param event
	 */
	@FXML
	private void logout(MouseEvent event) {
		app.setCurrentUser(null);
		app.setCurrentAlbum(null);
		app.switchScene((Stage)keyChooser.getScene().getWindow(), "login");
	}
	
	/**
	 * Dispaly a given set of search results to the user.
	 * @param results Search results to be displayed.
	 */
	private void showResults(Set<Photo> results) {
		app.setCurrentAlbum("", results);
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/view/photos.fxml"));
		try {
			Pane root = loader.load();
			resultsPane.getChildren().setAll(root);
			resultsController = loader.getController();
			resultsController.setApp(app);
			resultsController.init();
			showAlbumView(false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Show or hide the list of albums.
	 * @param shouldShow Whether or not albumList should be shown.
	 */
	private void showAlbumView(boolean shouldShow) {
		albumList.setManaged(shouldShow);
		albumList.setVisible(shouldShow);
		resultsBox.setManaged(!shouldShow);
		resultsBox.setVisible(!shouldShow);
	}
	
	/**
	 * Handler for when the search button is clicked. Checks to see which search method 
	 * is displayed, then does the search and displays the results.
	 * @param e
	 */
	@FXML
	private void searchButtonClicked(MouseEvent e) {
		if (!leftClick(e))
			return;
		Set<Photo> results;
		if (keyChooser.isManaged()) {
			results = getTagResults();
		} else if (dateChoosers.isManaged()) {
			results = getDateResults();
		} else 
			return;
		if (results != null) {
			if (results.size() == 0)
				photoalbum.util.Services.createAlert("No photos were found with the matching criteria");
			else
				showResults(results);	
		}
	}
	
	/**
	 * Get results of a search in accordance to the values stored keyChooser and valueChooser.
	 * @return
	 */
	private Set<Photo> getTagResults() {
		HashSet<Photo> results = new HashSet<Photo>();
		String key = keyChooser.getSelectionModel().getSelectedItem();
		String value = valueChooser.getSelectionModel().getSelectedItem();
		if (key == null || value == null)
			System.out.println("getTagResults Error: either keyChooser or valueChooser currently have no value");
		Set<Photo> allphotos = app.getCurrentUser().getAllPhotos();
		for (Photo p : allphotos) {
			if (key.equals("KEY")) {
				results.add(p);
			}
			if (value.equals("ANY") || value.equals("VALUE")) { // find all that have that tag
				if (p.hasTag(key)) {
					results.add(p);
				}
			}
			else {
				if (p.hasTag(key) && p.isTaggedsAs(key, value))
					results.add(p);
			}
		}
		
		return results;
	}
	
	/**
	 * Get the results of a search in accordance with what is stored in our startDate and endDate.
	 * @return
	 */
	private Set<Photo> getDateResults() {
		
		LocalDate sd = startDate.getValue();
		LocalDate ed = endDate.getValue();
		if (sd == null || ed == null) {
			photoalbum.util.Services.createAlert("Please enter range of dates");
			return null;
		}
		if (sd.isAfter(ed)) {
			photoalbum.util.Services.createAlert("Please enter a start date that is before the end date");
			return null;
		}
		Date start = Date.from(sd.atStartOfDay(ZoneId.systemDefault()).toInstant());
		Date end = Date.from(ed.atStartOfDay(ZoneId.systemDefault()).toInstant());
		System.out.println("ED: "+end+" SD: "+start);
		
		return app.getCurrentUser().findWithinRange(start, end);	
	}
	
	/**
	 * When creating a new album from search results, ask the user for a new name for the album.
	 * @param e
	 */
	@FXML
	private void getNewAlbumName(MouseEvent e) {
		if (!leftClick(e) || !app.getCurrentAlbum().getAlbumName().equals("")) // not sure how that could happen
			return;
		photoalbum.util.Services.openRenameDialog("Enter a name for this album", "",
				  (s1, s2) -> createNewAlbum(s2));
		
	}
	
	/**
	 * Create a new album and store it with the current user. If the given name is
	 * invalid, creates an error dialog.
	 * @param albumName The name of the new album.
	 */
	private void createNewAlbum(String albumName) {
		Album newAlbum = app.getCurrentAlbum();
		newAlbum.setAlbumName(albumName);
		if (app.getCurrentUser().addAlbum(newAlbum) == false) {
			photoalbum.util.Services.createAlert("Can't create an album with that name");
			newAlbum.setAlbumName("");
		}

		refreshAlbums();
	}
	
	/**
	 * Show the album view
	 * @param e
	 */
	@FXML
	private void showAlbums(MouseEvent e) {
		if (!leftClick(e))
			return;
		showAlbumView(true);
	}
	
	abstract void refreshAlbums();
}

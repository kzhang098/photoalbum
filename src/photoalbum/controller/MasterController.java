package photoalbum.controller;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import photoalbum.PhotoAlbum;

/**
 * The base class for all controllers. This class exists less to allow code reuse,
 * though that does happen a bit. It is more used as a way to switching between
 * different views by providing a standardized way for new views to be loaded.
 * @author Paul Warner & Kenny Zhang
 *
 */
public abstract class MasterController {

	/**
	 * A pointer to the app. This also acts as a global namespace shared by all controllers.
	 */
	protected PhotoAlbum app;
	
	/**
	 * Set the app to the given argument
	 * @param app
	 */
	public void setApp(PhotoAlbum app) {
		this.app = app;
	}
	
	/**
	 * Check that the given MouseEvent is a left click. Return true if it is, and false otherwise.
	 * @param e
	 * @return
	 */
	protected boolean leftClick(MouseEvent e) {
		return e.getButton() == MouseButton.PRIMARY;
	}
	
	/**
	 * Check that the given MouseEvent is a double left click. Return true if it is, and false otherwise.
	 * @param e
	 * @return
	 */
	protected boolean leftDoubleClick(MouseEvent e) {
		return leftClick(e) && e.getClickCount() == 2;
	}
	
	/**
	 * Check that the given Mouse event is a right click. Return true if it is, and false otherwise.
	 * @param e
	 * @return
	 */
	protected boolean rightClick(MouseEvent e) {
		return e.getButton() == MouseButton.SECONDARY;
	}
	
	/**
	 * Initializes the controller and UI state before it is shown. In effect, it acts like each 
	 * controller's own main method.
	 */
	public abstract void init();
}

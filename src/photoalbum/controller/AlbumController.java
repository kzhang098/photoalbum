package photoalbum.controller;

import photoalbum.PhotoAlbum;

public abstract class AlbumController {

	protected PhotoAlbum app;
	
	public void setApp(PhotoAlbum app) {
		this.app = app;
	}
	
	public abstract void init();

}

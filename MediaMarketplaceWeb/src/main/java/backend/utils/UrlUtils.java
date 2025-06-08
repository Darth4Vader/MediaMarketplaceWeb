package backend.utils;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import backend.ActivateSpringApplication;

public class UrlUtils {

	public static String getServerURL() {
		return ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
	}
	
	public static String getFullImageURL(String imageUri) {
		if(imageUri == null) return null;
		return getServerURL() + "/" + ActivateSpringApplication.IMAGES_FOLDER + "/" + imageUri;
	}

}

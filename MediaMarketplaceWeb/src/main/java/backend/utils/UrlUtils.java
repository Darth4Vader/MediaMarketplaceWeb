package backend.utils;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import backend.ActivateSpringApplication;
import backend.ConfigValues;

@Component
public class UrlUtils {
	
	@Autowired
	private ConfigValues configValues;
	
	public String getFullURL(String relativePath) {
	    if (relativePath == null || relativePath.isBlank()) return null;

	    URI baseUri = URI.create(configValues.getFrontendUrl());
	    URI fullUri = baseUri.resolve(relativePath);

	    return fullUri.toString();
	}

	public static String getServerURL() {
		return ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
	}
	
	public static String getFullImageURL(String imageUri) {
		if(imageUri == null) return null;
		return getServerURL() + "/" + ActivateSpringApplication.IMAGES_FOLDER + "/" + imageUri;
	}

}

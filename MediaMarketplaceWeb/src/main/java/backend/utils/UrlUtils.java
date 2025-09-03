package backend.utils;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import backend.ActivateSpringApplication;
import backend.ConfigValues;

@Component
public class UrlUtils {
	
	@Autowired
	private ConfigValues configValues;
	
	@Value("${PROFILE}")
	private String profile;
	
	public String getFullURL(String relativePath) {
	    if (relativePath == null || relativePath.isBlank()) return null;

	    URI baseUri = URI.create(configValues.getFrontendUrl());
	    URI fullUri = baseUri.resolve(relativePath);

	    return fullUri.toString();
	}

	public String getServerURL() {
		if(profile != null && profile.equals("dev"))
			return ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
		return ServletUriComponentsBuilder.fromCurrentContextPath().replacePath("").build().toUriString();
	}
	
	public String getFullImageURL(String imageUri) {
		if(imageUri == null) return null;
		return getServerURL() + "/" + ActivateSpringApplication.IMAGES_FOLDER + "/" + imageUri;
	}

}

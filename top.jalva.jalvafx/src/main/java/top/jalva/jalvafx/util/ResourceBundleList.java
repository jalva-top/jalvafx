package top.jalva.jalvafx.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceBundleList {
	
	private static final Logger log = LoggerFactory.getLogger(ResourceBundleList.class.getName());

	public static List<String> get(String resourceBundleListName) {
		List<String> list = null;

		String baseName = "Lists";
		try {
			list = Arrays.asList(ResourceBundle.getBundle(baseName).getString(resourceBundleListName).split(","));
		} catch (Exception e) {
			log.error("ResourceBundle [{}] has not contain valid list with a key [{}]", baseName, resourceBundleListName);
		}

		if (list == null)
			list = new ArrayList<>();

		return list;
	}
}

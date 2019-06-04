package top.jalva.jalvafx.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class ResourceBundleList {

	public static List<String> get(String resourceBundleListName) {
		List<String> list = null;

		try {
			list = Arrays.asList(ResourceBundle.getBundle("Lists").getString(resourceBundleListName).split(","));
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (list == null)
			list = new ArrayList<>();

		return list;
	}
}

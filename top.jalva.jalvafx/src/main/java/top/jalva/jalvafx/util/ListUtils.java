package top.jalva.jalvafx.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ListUtils {

	/**
	 * Returns new List where old value of newElementValue replaced by new. If
	 * !list.contains(newElementValue) - non-modifiable list will be returned
	 */
	public static <T extends Object> ObservableList<T> getNewListChangingItem(ObservableList<T> list,
			T newElementValue) {
		ObservableList<T> newList = FXCollections.observableArrayList();

		for (T item : list) {
			if (item.equals(newElementValue))
				newList.add(newElementValue);
			else
				newList.add(item);
		}
		return newList;
	}

	/**
	 * If list1==null then list2 will be returned. If list2==null then list1
	 * will be returned.
	 */
	public static <T extends Object> List<T> getListContainsMatchingItems(List<T> list1, List<T> list2) {

		if (list1 == null && list2 == null)
			return Collections.emptyList();
		if (list1 == null)
			return list2;
		else if (list2 == null)
			return list1;
		else {

			List<T> result = new ArrayList<T>();

			for (T item : list1) {
				if (list2.contains(item))
					result.add(item);
			}

			return result;
		}
	}

	public static <T extends Object> ObservableList<T> getNewListChangingItems(Collection<T> items,
			Collection<T> updatedItems) {
		List<T> newList = FXCollections.observableArrayList();

		for (T item : items) {
			if (updatedItems.contains(item)) {
				T updatedItem = updatedItems.stream().filter(o -> o.equals(item)).findAny().orElse(null);

				if (updatedItem != null)
					newList.add(updatedItem);
			} else
				newList.add(item);
		}
		return (ObservableList<T>) newList;
	}
}

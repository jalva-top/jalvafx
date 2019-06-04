package top.jalva.jalvafx.node;

import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import top.jalva.jalvafx.util.ListUtils;

public class ListViewUtils {

	public static <T extends Object> void refreshItem(ListView<T> tableView, T item) {
		ObservableList<T> list = tableView.getItems();
		ObservableList<T> items = ListUtils.getNewListChangingItem(list, item);
		tableView.setItems(items);
		tableView.refresh();
	}
}

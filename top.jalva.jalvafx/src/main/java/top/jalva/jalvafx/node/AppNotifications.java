package top.jalva.jalvafx.node;

import org.controlsfx.control.Notifications;

import javafx.scene.Node;
import javafx.stage.Screen;
import javafx.stage.Window;

public class AppNotifications {

	private static Object defaultOwner = null;

	public static void setDefaultOwner(Screen screen) {
		defaultOwner = screen;
	}

	public static void setDefaultOwner(Window window) {
		defaultOwner = window;
	}

	public static void setDefaultOwner(Node node) {
		defaultOwner = node;
	}

	public static Notifications create() {
		return create(defaultOwner);
	}

	/**
	 * @param owner
	 *            can be Screen, Window or Node. If owner==null then
	 *            defaultOwner will be set as owner
	 */
	public static Notifications create(Object owner) {
		if (owner == null)
			owner = defaultOwner;
		return Notifications.create().owner(owner);
	}
}

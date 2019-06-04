package top.jalva.jalvafx.controller.interfaces;

import java.util.List;

public interface FxmlParentController extends FxmlControllerWithShownInUiProperty {

	default boolean hasChild(FxmlControllerClosable child) {
		return child != null && getChildren().contains(child);
	}

	List<FxmlControllerClosable> getChildren();

	void addChildController(FxmlControllerClosable childController);

	void closeChildStagesIfOpened();

}
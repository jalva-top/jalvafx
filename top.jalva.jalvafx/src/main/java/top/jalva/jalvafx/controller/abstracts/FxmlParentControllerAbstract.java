package top.jalva.jalvafx.controller.abstracts;

import java.util.ArrayList;
import java.util.List;

import top.jalva.jalvafx.controller.interfaces.FxmlControllerClosable;
import top.jalva.jalvafx.controller.interfaces.FxmlParentController;

public abstract class FxmlParentControllerAbstract implements FxmlParentController {

	List<FxmlControllerClosable> children = new ArrayList<>();
	boolean shownInUi = true;

	@Override
	public List<FxmlControllerClosable> getChildren() {
		return children;
	}

	@Override
	public final void closeChildStagesIfOpened() {
		for (FxmlControllerClosable controller : children) {
			if (controller != null) {
				if (controller instanceof FxmlParentController)
					((FxmlParentController) controller).closeChildStagesIfOpened();
				controller.closeStage();
			}
		}
		children.clear();
	}

	@Override
	public final void addChildController(FxmlControllerClosable childController) {
		children.add(childController);

		childController.getStage().setOnCloseRequest(e -> {
			removeChildController(childController);
		});
	}

	public final void removeChildController(FxmlControllerClosable childController) {
		children.remove(childController);
	}

	@Override
	public boolean isShownInUi() {
		return shownInUi;
	}

	@Override
	public void setShownInUi(boolean shown) {
		shownInUi = shown;
	}
}

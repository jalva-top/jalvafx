package top.jalva.jalvafx.controller.abstracts;

import javafx.stage.Stage;
import top.jalva.jalvafx.controller.interfaces.FxmlControllerClosable;

public abstract class FxmlControllerClosableAbstract implements FxmlControllerClosable {

	protected Stage stage = null;

	// Set in FXMLLoaderProvider class
	@Override
	public final void setStage(Stage stage) {
		this.stage = stage;
	}

	@Override
	public final void closeStage() {
		if (stage != null)
			stage.close();
	}

	@Override
	public Stage getStage() {
		return stage;
	}
}
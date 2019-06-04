package top.jalva.jalvafx.controller.interfaces;

import javafx.stage.Stage;

public interface FxmlControllerClosable extends FxmlController {
	
	Stage getStage();
	void setStage(Stage stage);	
	void closeStage();
}

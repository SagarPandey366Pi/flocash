package wallettemplate;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

public class BaliNightsController{

	public ComboBox<String> fruitCombo;
	public Label selectedFruit;
	
	public void initialize() {
		assert selectedFruit!= null : "fx:id=\"selectedFruit\" was not injected: check your fxml File.";
		
		selectedFruit.textProperty().bind(fruitCombo.getSelectionModel().selectedItemProperty());
		
	}
}

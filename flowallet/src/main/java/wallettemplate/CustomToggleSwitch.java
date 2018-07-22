package wallettemplate;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;


public class CustomToggleSwitch extends HBox {
	
	private final Label label = new Label();
	private final Button button = new Button();
	
	private SimpleBooleanProperty switchedOn = new SimpleBooleanProperty(false);
	public SimpleBooleanProperty switchOnProperty() { return switchedOn; }
	CustomToggleSwitchListener customToggleSwitchListener;
	
	private void init() {
		
		label.setText("TestNet");
		
		getChildren().addAll(label, button);	
		button.setOnAction((e) -> {
			switchedOn.set(!switchedOn.get());
		});
		label.setOnMouseClicked((e) -> {
			switchedOn.set(!switchedOn.get());
		});
		setStyle();
		bindProperties();
	}
	
	private void setStyle() {
		//Default Width
		label.setAlignment(Pos.CENTER);
		setMaxWidth(120.0f);
		setMaxHeight(30.0f);
		setStyle("-fx-background-color: grey; -fx-text-fill:black");
		setAlignment(Pos.CENTER_LEFT);
	}
	
	private void bindProperties() {
		label.setPrefWidth(120.0f);
		label.setPrefHeight(30.0f);
		button.setPrefWidth(120.0f);
		button.setPrefHeight(30.0f);
	}
	
	public CustomToggleSwitch(CustomToggleSwitchListener listener) {
		init();
		switchedOn.addListener((a,b,c) -> {
			if (c) {
                		label.setText("MainNet");
                		setStyle("-fx-background-color: green;");
                		label.toFront();
                		listener.onToggleSwitchClick(true);
            		}
            		else {
            			label.setText("TestNet");
            			setStyle("-fx-background-color: grey;");
                		button.toFront();
                		listener.onToggleSwitchClick(false);
            		}
		});
	}
	
	public interface CustomToggleSwitchListener {
		void onToggleSwitchClick(boolean enabled);
	}
}


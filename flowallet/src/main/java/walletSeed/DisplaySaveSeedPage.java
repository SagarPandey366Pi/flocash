package walletSeed;

import org.floj.core.Address;
import org.floj.core.NetworkParameters;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

//Displays the save your seed page for security reason!!!
public class DisplaySaveSeedPage {

	public void displayPage(Stage mainWindow, NetworkParameters params, Address address, String seed) {
		
		BorderPane bp = new BorderPane();
        bp.setPadding(new Insets(10,50,50,50));

        //Adding HBox
        HBox hb = new HBox();
        hb.setPadding(new Insets(50,20,20,30));
        
        //Adding GridPane
		GridPane gridPane = new GridPane();
		gridPane.setPadding(new Insets(20, 20, 20, 20));
		gridPane.setHgap(5);
		gridPane.setVgap(5);
		
		Text text = new Text("NEW ACCOUNT");
		text.setFont(Font.font("Courier New", FontWeight.BOLD, 28));
		
		final TextArea seedText = new TextArea();
		Button btnLogin = new Button("I UNDERSTAND");
		btnLogin.setTranslateX(150);
		
		gridPane.add(seedText, 1, 0);
		gridPane.add(btnLogin, 1, 2);
		
		//Adding text to HBox
        hb.getChildren().add(text);
        
		btnLogin.setId("btnLogin");
		
		
		seedText.setWrapText(true);
        double height = 250; //making a variable called height with a value 400
        double width = 450;  //making a variable called height with a value 300
        seedText.setPrefHeight(height);
        seedText.setPrefWidth(width);
        seedText.setEditable(false);
        
        seedText.setText("Treat your SEED (backup phrase) with care!" + "\n" + "Only SEED can provide access to your wallet." + "\n" + "\n" + "1. Don't put your SEED anywhere except official FLO clients."
        		+ "\n" + "\n" + "If someone else accesses it you will lose your funds." 
        		+ "\n" + "\n" + "2. Store your SEED safely, it is the only way to restore your wallet.");
        
        SignUp login = new SignUp();
        btnLogin.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				try {
					login.loginPage(mainWindow, params, address, seed);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
        
      //Add HBox and GridPane layout to BorderPane Layout
        bp.setTop(hb);
        bp.setCenter(gridPane);
        
        //Adding BorderPane to the scene and loading CSS
        Scene scene = new Scene(bp);
        //scene.getStylesheets().add(getClass().getClassLoader().getResource("login.css").toExternalForm());
        mainWindow.setScene(scene);
        mainWindow.titleProperty().unbindBidirectional(
                scene.widthProperty().asString().
                        concat(" : ").
                        concat(scene.heightProperty().asString()));
        //primaryStage.setResizable(false);
        mainWindow.show();
	}
}

package walletSeed;

import org.floj.core.Address;
import org.floj.core.NetworkParameters;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import wallettemplate.Main;

public class RestorePageFromSeed {

	String checkPw;
	public void loginPage(Stage mainWindow, NetworkParameters params, Address addre, WalletSeed w) throws Exception
    {
    	mainWindow.setTitle("FLO");
    	
        BorderPane bp = new BorderPane();
        bp.setPadding(new Insets(10,50,50,50));

        //Adding HBox
        HBox hb = new HBox();
        hb.setPadding(new Insets(50,20,20,30));

        //Adding GridPane
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20,20,20,20));
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        //Implementing Nodes for GridPane
        Label lblPassword = new Label("Password");
        final PasswordField pf;
        pf = new PasswordField();
        Label lblseed = new Label("seed");
        final TextArea seed = new TextArea();
        Button btnLogin = new Button("Restore Wallet");
        final Label lblMessage = new Label();
        
        
        //Adding Nodes to GridPane layout
        gridPane.add(lblseed, 0, 0);//lblUserName ,txtUserName, seed, Seedng, lblPassword, pf
        //gridPane.add(Seedng, 1, 0);
        gridPane.add(seed, 1, 0);
        gridPane.add(lblPassword, 0, 2);
        gridPane.add(pf, 1, 2);
        gridPane.add(btnLogin, 1, 4);
        gridPane.add(lblMessage, 1, 5);

        
        seed.setWrapText(true);
        double height = 100; //making a variable called height with a value 400
        double width = 150;  //making a variable called height with a value 300
        seed.setPrefHeight(height);
        seed.setPrefWidth(width);
        seed.setEditable(true);
        
        //Adding text and DropShadow effect to it
        Text text = new Text("Enter the seed in the given order with the password");
        text.setFont(Font.font("Courier New", FontWeight.BOLD, 28));

        //Adding text to HBox
        hb.getChildren().add(text);

        //Add ID's to Nodes
        bp.setId("bp");
        gridPane.setId("root");
        btnLogin.setId("btnLogin");
        text.setId("text");

        System.out.println("Address and seed in RestorePage:: " + addre + "Seed " + w.getMnemonic());
        //String pw = "abc";
        /*String pw = System.console().readLine();
        String confirmPassword = "abc";*/
        //Changes for seed creation Sagar - start
        
        /*List<String> seeds = MnemonicCode.INSTANCE.getWordList();
        byte[] seedd = MnemonicCode.toSeed(seeds, "");
        MnemonicCode mc = new MnemonicCode();
        
        WalletTest wt = new WalletTest(mc, params, seedd, "");
        ssss.setText(wt.getMnemonic());
       	//Seedng.setText(wt.getMnemonic());
       	
       	//Changes by Sagar from seed to key - start
       	
       	DeterministicSeed seed1 = null;
       	long creationtime = 1409478661L;
       	seed1 = new DeterministicSeed(wt.getMnemonic(), null,"", creationtime);
       	
       	Wallet wallet = Wallet.fromSeed(params, seed1);
       	Address address = wallet.currentReceiveAddress();
       	System.out.println("Address defined in the SignUp Class is: " + address);*/
       
       	//Changes by Sagar from seed to key - end

        //Changes for seed creation Sagar- end
       	
        //Action for btnLogin
       	RestorePageFromSeed rc = new RestorePageFromSeed();
       	Main main = new Main();
       	btnLogin.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				
				checkPw = pf.getText().toString();
				try {
					PushtoLocal.PushtoFile(addre.toString(), checkPw);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try
				{
					RestoreFromSeed.restore(params, w, 1409478661L);
					main.loginPage();
				}
				catch(Exception e)
				{
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
       
    //Changes for implementing Login Page to get to the wallet end- Sagar
}

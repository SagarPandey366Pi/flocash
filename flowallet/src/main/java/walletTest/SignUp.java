package walletTest;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import javax.crypto.NoSuchPaddingException;

import org.floj.core.Address;
import org.floj.core.NetworkParameters;
import org.floj.crypto.MnemonicCode;
import org.floj.wallet.DeterministicSeed;
import org.floj.wallet.Wallet;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import wallettemplate.Main;
import wallettemplate.controls.ClickableFLOAddress;

public class SignUp {

	String checkUser, checkPw, checkconfirmPw;
    ClickableFLOAddress addressControl;
    DeterministicSeed seed1;
    
	//Changes for implementing Login Page to get to the wallet start- Sagar
    public void loginPage(Stage mainWindow, NetworkParameters params) throws Exception
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
        Label lblUserName = new Label("Address");
        final TextField txtUserName = new TextField();
        Label lblPassword = new Label("Password");
        final PasswordField pf;
        pf = new PasswordField();
        Label lblConfirmPassword = new Label("Confirm Password");
        final PasswordField confirmPass;
        confirmPass = new PasswordField();
        Label lblseed = new Label("seed");
        final TextArea ssss = new TextArea();
        //final TextField Seedng = new TextField();
        Button btnLogin = new Button("Create User");
        final Label lblMessage = new Label();
        
        
        //Adding Nodes to GridPane layout
        gridPane.add(lblseed, 0, 0);//lblUserName ,txtUserName, seed, Seedng, lblPassword, pf
        //gridPane.add(Seedng, 1, 0);
        gridPane.add(ssss, 1, 0);
        gridPane.add(lblUserName, 0, 1);
        gridPane.add(txtUserName, 1, 1);
        gridPane.add(lblPassword, 0, 2);
        gridPane.add(pf, 1, 2);
        gridPane.add(lblConfirmPassword, 0, 3);
        gridPane.add(confirmPass, 1, 3);
        gridPane.add(btnLogin, 1, 4);
        gridPane.add(lblMessage, 1, 5);

        ssss.setWrapText(true);
        double height = 100; //making a variable called height with a value 400
        double width = 150;  //making a variable called height with a value 300
        ssss.setPrefHeight(height);
        ssss.setPrefWidth(width);
        ssss.setEditable(false);
        
        //Adding text and DropShadow effect to it
        Text text = new Text("FLO Login");
        text.setFont(Font.font("Courier New", FontWeight.BOLD, 28));

        //Adding text to HBox
        hb.getChildren().add(text);

        //Add ID's to Nodes
        bp.setId("bp");
        gridPane.setId("root");
        btnLogin.setId("btnLogin");
        text.setId("text");

        //String pw = "abc";
        /*String pw = System.console().readLine();
        String confirmPassword = "abc";*/
        //Changes for seed creation Sagar - start
        
        List<String> seeds = MnemonicCode.INSTANCE.getWordList();
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
       	System.out.println("Address defined in the SignUp Class is: " + address);
       	
       	txtUserName.setText(address.toString());
       	txtUserName.setEditable(false);
       	//Changes by Sagar from seed to key - end

        //Changes for seed creation Sagar- end
       	
        //Action for btnLogin
        btnLogin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
            	checkUser = address.toString();
            	System.out.println("CheckUser:: " + checkUser);
                checkPw = pf.getText().toString();
                System.out.println("Password: " + checkPw);
                checkconfirmPw = confirmPass.getText().toString();
                try {
					PushtoLocal.PushtoFile(checkUser, checkPw);
				} catch (Exception e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
                
                try {
					EncryptPassword.encrypt(checkPw, address);
					//EncryptPassword.decrypt();
				} catch (NoSuchAlgorithmException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (NoSuchPaddingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                
                if(checkconfirmPw.equals(checkPw)){
                    try {
						//Main.realStart(mainWindow);
                    	Main.loginPage();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
                else{
                    lblMessage.setText("Incorrect user or pw.");
                    lblMessage.setTextFill(Color.RED);
                }
                pf.setText("");
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

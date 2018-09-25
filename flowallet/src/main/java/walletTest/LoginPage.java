package walletTest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.JOptionPane;

import org.floj.core.Address;
import org.floj.core.NetworkParameters;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import wallettemplate.Main;

public class LoginPage {

	String checkUser, checkPw;
	
	public void loginPage(Stage mainWindow, NetworkParameters params, Address address) throws Exception
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
        Button btnLogin = new Button("Login");
        final Label lblMessage = new Label();
        
        
        //Adding Nodes to GridPane layout
        //gridPane.add(lblUserName, 0, 0);
        gridPane.add(txtUserName, 1, 0);
        gridPane.add(lblPassword, 0, 2);
        gridPane.add(pf, 1, 2);
        gridPane.add(btnLogin, 1, 3);
        gridPane.add(lblMessage, 1, 3);
        
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
        
       	txtUserName.setText(address.toString());
       	txtUserName.setEditable(false);
       	txtUserName.setStyle("-fx-background-color: transparent;");
       	txtUserName.setMinWidth(400.0);
       	//Changes by Sagar from seed to key - end

        //Changes for seed creation Sagar- end
        
        //Action for btnLogin
        btnLogin.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				//checkUser = address.toString();
				checkPw = pf.getText().toString();
				try {
					EncryptPassword.encrypt(checkPw, address);
					checkUser = EncryptPassword.decrypt();
					System.out.println("CheckUser Decrypted Value: " +checkUser );
				} 
				catch (InvalidKeyException | InvalidAlgorithmParameterException | NoSuchAlgorithmException
						| NoSuchPaddingException | UnsupportedEncodingException | IllegalBlockSizeException
						| BadPaddingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	System.out.println("CheckUser:: " + checkUser);
                
                System.out.println("Password: " + checkPw);
				boolean validate = ValidatePassword.myHelper(checkUser, checkPw);
				
				Main main = new Main();
				
				if(validate)
				{
					try {
						main.realStart(mainWindow);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else
				{
					JOptionPane.showMessageDialog(null, "Incorrect Password!!!");
				}
                //PushtoLocal.PushtoFile(checkUser, checkPw);
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

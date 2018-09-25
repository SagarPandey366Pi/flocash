package wallettemplate;

import org.floj.core.Coin;
import org.floj.wallet.Wallet;
import org.spongycastle.crypto.params.KeyParameter;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import wallettemplate.utils.GuiUtils;

public class BaliNightsController{

    public Button sendBtn;
    public Button cancelBtn;
    public TextField address;
    public Label titleLabel;
    public Label btcLabel;
    public TextField amountEdit;
   // public TextField floData;

    public Main.OverlayUI overlayUI;

    private Wallet.SendResult sendResult;
    private KeyParameter aesKey;

    // Called by FXMLLoader
    public void initialize() {
    	System.out.println("In BaliNights" + Main.params + " Address::" +  address + " Send Butoons::" + sendBtn);
        Coin balance = Main.flo.wallet().getBalance();
    }

    public void cancel(ActionEvent event) {
        overlayUI.done();
    }
	
    public void search(ActionEvent event) {

    	Stage primaryStage = Main.mainWindow;
    	try
    	{
    	FXMLLoader loader = new FXMLLoader(GuiUtils.class.getResource("Payment.fxml"));
    	Parent root = (Parent) loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));  
        stage.show();
         //primaryStage.close();
} catch(Exception e) {
   e.printStackTrace();
  }
    }
}

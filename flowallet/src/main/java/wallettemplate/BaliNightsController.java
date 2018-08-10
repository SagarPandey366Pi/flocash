package wallettemplate;

import static wallettemplate.utils.GuiUtils.checkGuiThread;
import static wallettemplate.utils.GuiUtils.crashAlert;
import static wallettemplate.utils.GuiUtils.informationalAlert;

import javax.annotation.Nullable;

import org.floj.core.Address;
import org.floj.core.Coin;
import org.floj.core.ECKey;
import org.floj.core.InsufficientMoneyException;
import org.floj.core.Transaction;
import org.floj.core.TransactionConfidence;
import org.floj.wallet.SendRequest;
import org.floj.wallet.Wallet;
import org.spongycastle.crypto.params.KeyParameter;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import wallettemplate.controls.FLOAddressValidator;

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
        Coin balance = Main.flo.wallet().getBalance();
        //checkState(!balance.isZero());
        new FLOAddressValidator(Main.params, address, sendBtn);
        //floData.setPromptText("Enter text data to store on the FLO blockchain (optional, "+Transaction.MAX_FLO_DATA_SIZE+" characters max)");
        /*new TextFieldValidator(amountEdit, text ->
                !WTUtils.didThrow(() -> checkState(Coin.parseCoin(text).compareTo(balance) <= 0)));
        amountEdit.setText(balance.toPlainString());*/
    }

    public void cancel(ActionEvent event) {
        overlayUI.done();
    }
	
    public void search(ActionEvent event) {
        // Address exception cannot happen as we validated it beforehand.
        try {
            Coin amount = Coin.parseCoin(amountEdit.getText());
            Address destination = Address.fromBase58(Main.params, address.getText());
            SendRequest req;
            if (amount.equals(Main.flo.wallet().getBalance()))
                req = SendRequest.emptyWallet(destination);
            else
                req = SendRequest.to(destination, amount);
            
          
            
            req.aesKey = aesKey;
            sendResult = Main.flo.wallet().sendCoins(req);
            Futures.addCallback(sendResult.broadcastComplete, new FutureCallback<Transaction>() {
                @Override
                public void onSuccess(@Nullable Transaction result) {
                    checkGuiThread();
                    overlayUI.done();
                }

                @Override
                public void onFailure(Throwable t) {
                    // We died trying to empty the wallet.
                    crashAlert(t);
                }
            });
            sendResult.tx.getConfidence().addEventListener((tx, reason) -> {
                if (reason == TransactionConfidence.Listener.ChangeReason.SEEN_PEERS)
                    updateTitleForBroadcast();
            });
            sendBtn.setDisable(false);
            address.setDisable(true);
            ((HBox)btcLabel.getParent()).getChildren().remove(btcLabel);
            updateTitleForBroadcast();
        } catch (InsufficientMoneyException e) {
            informationalAlert("Could not empty the wallet",
                    "You may have too little money left in the wallet to make a transaction.");
            overlayUI.done();
        } catch (ECKey.KeyIsEncryptedException e) {
            askForPasswordAndRetry();
        }
    }
    
    private void updateTitleForBroadcast() {
        final int peers = sendResult.tx.getConfidence().numBroadcastPeers();
        titleLabel.setText(String.format("Broadcasting ... seen by %d peers", peers));
    }
    
    private void askForPasswordAndRetry() {
        Main.OverlayUI<WalletPasswordController> pwd = Main.instance.overlayUI("wallet_password.fxml");
        final String addressStr = address.getText();
        pwd.controller.aesKeyProperty().addListener((observable, old, cur) -> {
            // We only get here if the user found the right password. If they don't or they cancel, we end up back on
            // the main UI screen. By now the send money screen is history so we must recreate it.
            checkGuiThread();
            Main.OverlayUI<DonateFloController> screen = Main.instance.overlayUI("donate_flo.fxml");
            //screen.controller.aesKey = cur;
            screen.controller.address.setText(addressStr);
            screen.controller.send(null);
        });
    }
}

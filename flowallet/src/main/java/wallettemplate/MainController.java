/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package wallettemplate;

import org.floj.core.Address;
import org.floj.core.Coin;
import org.floj.core.Transaction;
import org.floj.core.listeners.DownloadProgressTracker;
import org.floj.utils.MonetaryFormat;

import com.subgraph.orchid.TorClient;
import com.subgraph.orchid.TorInitializationListener;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import javafx.util.StringConverter;

import org.fxmisc.easybind.EasyBind;
import wallettemplate.controls.ClickableFLOAddress;
import wallettemplate.controls.NotificationBarPane;
import wallettemplate.utils.FLOUIModel;
import wallettemplate.utils.easing.EasingMode;
import wallettemplate.utils.easing.ElasticInterpolator;

import static wallettemplate.Main.flo;

/**
 * Gets created auto-magically by FXMLLoader via reflection. The widget fields are set to the GUI controls they're named
 * after. This class handles all the updates and event handling for the main UI.
 */
public class MainController {
    public HBox controlsBox;
    public Label balance;
    public Button sendMoneyOutBtn;
    public ClickableFLOAddress addressControl;
    public ListView<Transaction> transactionsList;

    private FLOUIModel model = new FLOUIModel();
    private NotificationBarPane.Item syncItem;

    public MainController(String MainControl) {
		// TODO Auto-generated constructor stub
    	super();
	}

	// Called by FXMLLoader.
    public void initialize() {
        addressControl.setOpacity(0.0);
    }

    public void onFLOSetup() {
        model.setWallet(flo.wallet());
        addressControl.addressProperty().bind(model.addressProperty());
        balance.textProperty().bind(EasyBind.map(model.balanceProperty(), coin -> MonetaryFormat.FLO.noCode().format(coin).toString()));
        // Don't let the user click send money when the wallet is empty.
        sendMoneyOutBtn.disableProperty().bind(model.balanceProperty().isEqualTo(Coin.ZERO));

        TorClient torClient = Main.flo.peerGroup().getTorClient();
        if (torClient != null) {
            SimpleDoubleProperty torProgress = new SimpleDoubleProperty(-1);
            String torMsg = "Initialising Tor";
            syncItem = Main.instance.notificationBar.pushItem(torMsg, torProgress);
            torClient.addInitializationListener(new TorInitializationListener() {
                @Override
                public void initializationProgress(String message, int percent) {
                    Platform.runLater(() -> {
                        syncItem.label.set(torMsg + ": " + message);
                        torProgress.set(percent / 100.0);
                    });
                }

                @Override
                public void initializationCompleted() {
                    Platform.runLater(() -> {
                        syncItem.cancel();
                        showFLOSyncMessage();
                    });
                }
            });
        } else {
            showFLOSyncMessage();
        }
        model.syncProgressProperty().addListener(x -> {
            if (model.syncProgressProperty().get() >= 1.0) {
                readyToGoAnimation();
                if (syncItem != null) {
                    syncItem.cancel();
                    syncItem = null;
                }
            } else if (syncItem == null) {
                showFLOSyncMessage();
            }
        });
        Bindings.bindContent(transactionsList.getItems(), model.getTransactions());
        
        transactionsList.setCellFactory(param-> new TextFieldListCell<>(new StringConverter<Transaction>() {
			@Override
			public String toString(Transaction tx) {
				Coin value = tx.getValue(Main.flo.wallet());
				
				//show floData if any
				StringBuilder s = new StringBuilder();
		        if (tx.getFloData().length>0) {
		        	s.append('\n');
		            s.append("FLO Data: ");
		            s.append(new String(tx.getFloData())); //convert to string first to retain actual byte values in string
		            s.append('\n');
		        }
				if (value.isPositive()) {
					return "Incoming payment of " + MonetaryFormat.FLO.format(value) + s;
				} else if (value.isNegative()) {
					Address address = tx.getOutput(0).getAddressFromP2PKHScript(Main.params);
					return "Outbound payment to " + address + s;
				}
				return "Payment with id " + tx.getHash();
			}
			
			@Override
			public Transaction fromString(String string) {
				return null;
			}
        }));
    }

    private void showFLOSyncMessage() {
        syncItem = Main.instance.notificationBar.pushItem("Synchronising with the FLO network", model.syncProgressProperty());
    }

    public void sendMoneyOut(ActionEvent event) {
        // Hide this UI and show the send money UI. This UI won't be clickable until the user dismisses send_money.
        Main.instance.overlayUI("send_money.fxml");
    }

    public void settingsClicked(ActionEvent event) {
        Main.OverlayUI<WalletSettingsController> screen = Main.instance.overlayUI("wallet_settings.fxml");
        System.out.println(Main.instance.overlayUI("wallet_settings.fxml"));
        screen.controller.initialize(null);
    }
    
    public void primaryClicked(ActionEvent event) {
    	//test signing a message
    	String sig64=flo.wallet().signMessage("FNEGQ6whhEr2BA3aYCs3Xe14mV4RN64zjM", "test message");
    	System.out.println("sig64: "+sig64);
    	
    	//test verifying a message
    	if(flo.wallet().verifyMessage("FNEGQ6whhEr2BA3aYCs3Xe14mV4RN64zjM","test message",sig64)) {
    		System.out.println("Verified!");
    	}
    }

    public void restoreFromSeedAnimation() {
        // Buttons slide out ...
        TranslateTransition leave = new TranslateTransition(Duration.millis(1200), controlsBox);
        leave.setByY(80.0);
        leave.play();
    }

    public void readyToGoAnimation() {
        // Buttons slide in and clickable address appears simultaneously.
        TranslateTransition arrive = new TranslateTransition(Duration.millis(1200), controlsBox);
        arrive.setInterpolator(new ElasticInterpolator(EasingMode.EASE_OUT, 1, 2));
        arrive.setToY(0.0);
        FadeTransition reveal = new FadeTransition(Duration.millis(1200), addressControl);
        reveal.setToValue(1.0);
        ParallelTransition group = new ParallelTransition(arrive, reveal);
        group.setDelay(NotificationBarPane.ANIM_OUT_DURATION);
        group.setCycleCount(1);
        group.play();
    }

    public DownloadProgressTracker progressBarUpdater() {
        return model.getDownloadProgressTracker();
    }
}

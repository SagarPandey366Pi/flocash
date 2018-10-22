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

import static wallettemplate.utils.GuiUtils.blurIn;
import static wallettemplate.utils.GuiUtils.blurOut;
import static wallettemplate.utils.GuiUtils.checkGuiThread;
import static wallettemplate.utils.GuiUtils.explodeOut;
import static wallettemplate.utils.GuiUtils.fadeIn;
import static wallettemplate.utils.GuiUtils.fadeOutAndRemove;
import static wallettemplate.utils.GuiUtils.informationalAlert;
import static wallettemplate.utils.GuiUtils.zoomIn;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;
import org.floj.core.Address;
import org.floj.core.ECKey;
import org.floj.core.NetworkParameters;
import org.floj.crypto.HDKeyDerivation;
import org.floj.crypto.MnemonicCode;
import org.floj.kits.WalletAppKit;
import org.floj.params.MainNetParams;
import org.floj.params.RegTestParams;
import org.floj.params.TestNet3Params;
import org.floj.utils.BriefLogFormatter;
import org.floj.utils.Threading;
import org.floj.wallet.DeterministicSeed;
import org.floj.wallet.Wallet;

import com.google.common.util.concurrent.Service;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import walletSeed.DisplaySaveSeedPage;
import walletSeed.LoginPage;
import walletSeed.OneClickAppLaunch;
import walletSeed.RestorePageFromSeed;
import walletSeed.SignUp;
import walletSeed.WalletSeed;
import wallettemplate.controls.ClickableFLOAddress;
import wallettemplate.controls.NotificationBarPane;
import wallettemplate.model.ParameterModel;
import wallettemplate.utils.GuiUtils;
import wallettemplate.utils.TextFieldValidator;

public class Main extends Application /*implements CustomToggleSwitchListener*/{
    public static NetworkParameters params= TestNet3Params.get();//MainNetParams.get(); //TestNet3Params.get(); //UnitTestParams.get();
    public static final String APP_NAME = "FloWallet";
    private static String WALLET_FILE_NAME = "";
    private static ParameterModel parameterModel = ParameterModel.getInstance();

    public static WalletAppKit flo;
    public static Main instance;

    private static StackPane uiStack;
    private static Pane mainUI;
    public static MainController mainController;
    public static NotificationBarPane notificationBar;
    public static Stage mainWindow = new Stage();
    boolean isOn = false;
   String checkUser, checkPw;
    ClickableFLOAddress addressControl;
    DeterministicSeed seed1;
    static Address address;
    
    Random rand = new Random();
    static WalletSeed wt;

    @Override
    public void start(Stage mainWindow) throws Exception {
        try {
        	params = parameterModel.getParameters();
        	
        	List<String> seeds = MnemonicCode.INSTANCE.getWordList();
            String randomEle = seeds.get(rand.nextInt(seeds.size()));
            byte[] seedd = MnemonicCode.toSeed(seeds, randomEle);
            MnemonicCode mc = new MnemonicCode();
            
            wt = new WalletSeed(mc, params, seedd, "");
           	
           	//Changes by Sagar from seed to key - start
           	
           	DeterministicSeed seed1 = null;       	
           	byte[] passSeed = new byte[DeterministicSeed.DEFAULT_SEED_ENTROPY_BITS/8];
           	seed1 = new DeterministicSeed(passSeed, wt.getMnemonic(), MnemonicCode.BIP39_STANDARDISATION_TIME_SECS);
    		System.out.println("Deterministic seed in main: "+wt.getMnemonic());
           	
           	Wallet wallet = Wallet.fromSeed(params, seed1);
           	address = wallet.currentReceiveAddress();
           	System.out.println("Address defined in the Main is: " + address);
        	loginPage();
        } catch (Throwable e) {
            GuiUtils.crashAlert(e);
            throw e;
        }
    }
    
    //Changes for implementing Login Page to get to the wallet start- Sagar
    public void loginPage() throws Exception
    {
    	mainWindow.setTitle("FLO");

        BorderPane bp = new BorderPane();
        bp.setPadding(new Insets(100,80,80,80));

        //Image image = new Image("background_image.jpg", 400, 400,false,true,true);
/*        Image image = new Image("background_image.jpg", 0, 0, false, true, true);
        ImageView imageView = new ImageView(image);
        bp.getChildren().add(imageView);*/

        //Adding HBox
        HBox hb = new HBox();
        hb.setPadding(new Insets(50,20,20,30));

        //Adding GridPane
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(50,50,50,50));
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        //Implementing Nodes for GridPane
        Button btnSignIn = new Button("SIGN IN");
        final Label lblSignIn = new Label();
        Button btnLogin = new Button("NEW ACCOUNT");
        final Label lblMessage = new Label();
        Button btnsign = new Button("IMPORT ACCOUNT");
        final Label lblsign = new Label();

       	//Changes by Sagar from seed to key - end

        //Changes for seed creation Sagar- end
        
        //Adding Nodes to GridPane layout
        
        gridPane.add(btnSignIn, 0, 0);
        gridPane.add(lblSignIn, 1, 0);
        gridPane.add(btnLogin, 1, 0);
        gridPane.add(lblMessage, 2, 0);
        gridPane.add(btnsign, 2, 0);
        gridPane.add(lblsign, 3, 0);

        //Adding text and DropShadow effect to it        
        Text text = new Text("ACCOUNTS");
        text.setFont(Font.font("Courier New", FontWeight.BOLD, 28));

        //Adding text to HBox
        hb.getChildren().add(text);

        //Add ID's to Nodes
        bp.setId("bp");
        gridPane.setId("root");
        btnLogin.setId("btnLogin");
        text.setId("text");
        
        LoginPage loginPage = new LoginPage();
        btnSignIn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				try {
					loginPage.loginPage(mainWindow, params, address);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
        
        DisplaySaveSeedPage display = new DisplaySaveSeedPage();
        btnLogin.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				display.displayPage(mainWindow, params, address, wt.getMnemonic());
			}
		});
        
        RestorePageFromSeed restorepage = new RestorePageFromSeed();
        btnsign.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				try {
					restorepage.loginPage(mainWindow, params, address, wt);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
        	
		});
        //Add HBox and GridPane layout to BorderPane Layout
        bp.setTop(hb);
        bp.setCenter(gridPane);

        //Set the Background
        //bp.setId("borderPane");
        //Adding BorderPane to the scene and loading CSS
        Scene scene = new Scene(bp);
        //scene.getStylesheets().addAll(getClass().getResource("/css/style.css").toExternalForm());
        mainWindow.setScene(scene);
        mainWindow.titleProperty().unbindBidirectional(
                scene.widthProperty().asString().
                        concat(" : ").
                        concat(scene.heightProperty().asString()));
        mainWindow.show();
     }
       
    //Changes for implementing Login Page to get to the wallet end- Sagar

    public void realStart(Stage mainWindow) throws Exception {
/*    	if(getParameters().getRaw().get(0).equalsIgnoreCase("1")) {
    		Main.params = TestNet3Params.get();
    		isOn = true;
    	}
    	else
    	{
    		Main.params = TestNet3Params.get();
    		isOn = false;
    	}*/
    	
    	
        Main.mainWindow = mainWindow;
        WALLET_FILE_NAME = APP_NAME.replaceAll("[^a-zA-Z0-9.-]", "_") + "-"
                + params.getPaymentProtocolId();
        instance = this;
        // Show the crash dialog for any exceptions that we don't handle and that hit the main loop.
        GuiUtils.handleCrashesOnThisThread();

        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            // We could match the Mac Aqua style here, except that (a) Modena doesn't look that bad, and (b)
            // the date picker widget is kind of broken in AquaFx and I can't be bothered fixing it.
            // AquaFx.style();
        }

        // Load the GUI. The MainController class will be automatically created and wired up.
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("main.fxml"));
        loader.setController(new MainController("MainController"));
        mainUI = loader.load();
        mainController = loader.getController();
        // Configure the window with a StackPane so we can overlay things on top of the main UI, and a
        // NotificationBarPane so we can slide messages and progress bars in from the bottom. Note that
        // ordering of the construction and connection matters here, otherwise we get (harmless) CSS error
        // spew to the logs.
        notificationBar = new NotificationBarPane(mainUI);
        mainWindow.setTitle(APP_NAME);
        uiStack = new StackPane();
        Scene scene = new Scene(uiStack);
        TextFieldValidator.configureScene(scene);   // Add CSS that we need.
        scene.getStylesheets().add(Main.class.getResource("wallet.css").toString());
        uiStack.getChildren().add(notificationBar);
        mainWindow.setScene(scene);
        mainWindow.resizableProperty().setValue(Boolean.FALSE);
        //Changes by Sagar for the toggle button
        /*customToggleSwitch.setTranslateX(mainUI.getWidth() - 80.0f);
        customToggleSwitch.setTranslateY(mainUI.getHeight());
        uiStack.getChildren().addAll(customToggleSwitch);*/
        //Changes End
        
        // Make log output concise.
        BriefLogFormatter.init();
        // Tell bitcoin to run event handlers on the JavaFX UI thread. This keeps things simple and means
        // we cannot forget to switch threads when adding event handlers. Unfortunately, the DownloadListener
        // we give to the app kit is currently an exception and runs on a library thread. It'll get fixed in
        // a future version.
        Threading.USER_THREAD = Platform::runLater;
        // Create the app kit. It won't do any heavy weight initialization until after we start it.
        setupWalletKit(null);

        if (flo.isChainFileLocked()) {
            informationalAlert("Already running", "This application is already running and cannot be started twice.");
            Platform.exit();
            return;
        }

        mainWindow.show();

        WalletSetPasswordController.estimateKeyDerivationTimeMsec();

        flo.addListener(new Service.Listener() {
            @Override
            public void failed(Service.State from, Throwable failure) {
                GuiUtils.crashAlert(failure);
            }
        }, Platform::runLater);
        flo.startAsync();

        scene.getAccelerators().put(KeyCombination.valueOf("Shortcut+F"), () -> flo.peerGroup().getDownloadPeer().close());
    }
    
    public static void setupWalletKit(@Nullable DeterministicSeed seed) {
        // If seed is non-null it means we are restoring from backup.
        flo = new WalletAppKit(params, new File("."), WALLET_FILE_NAME) {
            @Override
            protected void onSetupCompleted() {
                // Don't make the user wait for confirmations for now, as the intention is they're sending it
                // their own money!
                flo.wallet().allowSpendingUnconfirmedTransactions();
                Platform.runLater(mainController::onFLOSetup);
            }
        };
        // Now configure and start the appkit. This will take a second or two - we could show a temporary splash screen
        // or progress widget to keep the user engaged whilst we initialise, but we don't.
        if (params == RegTestParams.get()) {
            flo.connectToLocalHost();   // You should run a regtest mode bitcoind locally.
        } else if (params == TestNet3Params.get()) {
            // As an example!
//            flo.useTor();
            // flo.setDiscovery(new HttpDiscovery(params, URI.create("http://localhost:8080/peers"), ECKey.fromPublicOnly(BaseEncoding.base16().decode("02cba68cfd0679d10b186288b75a59f9132b1b3e222f6332717cb8c4eb2040f940".toUpperCase()))));
        }
        flo.setDownloadListener(mainController.progressBarUpdater())
               .setBlockingStartup(false)
               .setUserAgent(APP_NAME, "1.0");
        if (seed != null)
            flo.restoreWalletFromSeed(seed);
        
        //flo.awaitRunning();
        System.out.println("Params::"+params);
    }

    private Node stopClickPane = new Pane();

    public class OverlayUI<T> {
        public Node ui;
        public T controller;

        public OverlayUI(Node ui, T controller) {
            this.ui = ui;
            this.controller = controller;
        }

        public void show() {
            checkGuiThread();
            if (currentOverlay == null) {
                uiStack.getChildren().add(stopClickPane);
                uiStack.getChildren().add(ui);
                blurOut(mainUI);
                //darken(mainUI);
                fadeIn(ui);
                zoomIn(ui);
            } else {
                // Do a quick transition between the current overlay and the next.
                explodeOut(currentOverlay.ui);
                fadeOutAndRemove(uiStack, currentOverlay.ui);
                uiStack.getChildren().add(ui);
                ui.setOpacity(0.0);
                fadeIn(ui, 100);
                zoomIn(ui, 100);
            }
            currentOverlay = this;
        }

        public void outsideClickDismisses() {
            stopClickPane.setOnMouseClicked((ev) -> done());
        }

        public void done() {
            checkGuiThread();
            if (ui == null) return;  // In the middle of being dismissed and got an extra click.
            explodeOut(ui);
            fadeOutAndRemove(uiStack, ui, stopClickPane);
            blurIn(mainUI);
            //undark(mainUI);
            this.ui = null;
            this.controller = null;
            currentOverlay = null;
        }
    }

    @Nullable
    private OverlayUI currentOverlay;

    public <T> OverlayUI<T> overlayUI(Node node, T controller) {
        checkGuiThread();
        OverlayUI<T> pair = new OverlayUI<T>(node, controller);
        // Auto-magically set the overlayUI member, if it's there.
        try {
            controller.getClass().getField("overlayUI").set(controller, pair);
        } catch (IllegalAccessException | NoSuchFieldException ignored) {
        }
        pair.show();
        return pair;
    }

    /** Loads the FXML file with the given name, blurs out the main UI and puts this one on top. */
    public <T> OverlayUI<T> overlayUI(String name) {
        try {
            checkGuiThread();
            // Load the UI from disk.
            FXMLLoader loader = new FXMLLoader(GuiUtils.class.getResource("wallet_settings.fxml"));
            Pane ui = loader.load();
            T controller = loader.getController();
            OverlayUI<T> pair = new OverlayUI<T>(ui, controller);
            // Auto-magically set the overlayUI member, if it's there.
            try {
                if (controller != null)
                    controller.getClass().getField("overlayUI").set(controller, pair);
            } catch (IllegalAccessException | NoSuchFieldException ignored) {
                ignored.printStackTrace();
            }
            pair.show();
            return pair;
        } catch (IOException e) {
            throw new RuntimeException(e);  // Can't happen.
        }
    }

    //Changes by Sagar 
    /** Loads the FXML file with the given name, blurs out the main UI and puts this one on top. */
    public <T> OverlayUI<T> overlayUIsend(String name) {
        try {
            checkGuiThread();
            // Load the UI from disk.
            FXMLLoader loader = new FXMLLoader(GuiUtils.class.getResource("send_money.fxml"));
            Pane ui = loader.load();
            T controller = loader.getController();
            OverlayUI<T> pair = new OverlayUI<T>(ui, controller);
            // Auto-magically set the overlayUI member, if it's there.
            try {
                if (controller != null)
                    controller.getClass().getField("overlayUI").set(controller, pair);
            } catch (IllegalAccessException | NoSuchFieldException ignored) {
                ignored.printStackTrace();
            }
            pair.show();
            return pair;
        } catch (IOException e) {
            throw new RuntimeException(e);  // Can't happen.
        }
    }
    
    ReceiveMoneyController rmc = new ReceiveMoneyController();
    public <T> OverlayUI<T> overlayUIreceive(String name) throws Exception {
        try {
            checkGuiThread();
            // Load the UI from disk.
            FXMLLoader loader = new FXMLLoader(GuiUtils.class.getResource("flo_address.fxml"));
            Pane ui = loader.load();
            T controller = loader.getController();
            ((ReceiveMoneyController)controller).addressProperty().bind(mainController.getModel().addressProperty());
            //((ReceiveMoneyController)controller).addressProperty().set(address);
           //rmc.uri();
            OverlayUI<T> pair = new OverlayUI<T>(ui, controller);
            // Auto-magically set the overlayUI member, if it's there.
            try {
                if (controller != null)
                    controller.getClass().getField("overlayUI").set(controller, pair);
            } catch (IllegalAccessException | NoSuchFieldException ignored) {
                ignored.printStackTrace();
            }
            pair.show();
            return pair;
        } catch (IOException e) {
            throw new RuntimeException(e);  // Can't happen.
        }
    }
    
    public <T> OverlayUI<T> overlayUIdonateflo(String name) {
    	try {
            checkGuiThread();
            // Load the UI from disk.
            FXMLLoader loader = new FXMLLoader(GuiUtils.class.getResource("donate_flo.fxml"));
            Pane ui = loader.load();
            T controller = loader.getController();
            OverlayUI<T> pair = new OverlayUI<T>(ui, controller);
            // Auto-magically set the overlayUI member, if it's there.
            try {
                if (controller != null)
                    controller.getClass().getField("overlayUI").set(controller, pair);
            } catch (IllegalAccessException | NoSuchFieldException ignored) {
                ignored.printStackTrace();
            }
            pair.show();
            return pair;
        } catch (IOException e) {
            throw new RuntimeException(e);  // Can't happen.
        }
    }
    
    public <T> OverlayUI<T> overlayUIbaliNights(String name) {
    	try {
            checkGuiThread();
            // Load the UI from disk.
            FXMLLoader loader = new FXMLLoader(GuiUtils.class.getResource("bali_flo.fxml"));
            Pane ui = loader.load();
            T controller = loader.getController();
            OverlayUI<T> pair = new OverlayUI<T>(ui, controller);
            // Auto-magically set the overlayUI member, if it's there.
            try {
                if (controller != null)
                    controller.getClass().getField("overlayUI").set(controller, pair);
            } catch (IllegalAccessException | NoSuchFieldException ignored) {
                ignored.printStackTrace();
            }
            pair.show();
            return pair;
        } catch (IOException e) {
            throw new RuntimeException(e);  // Can't happen.
        }
    }
    
    public <T> OverlayUI<T> overlayUIpayment(String name) {
    	try {
            checkGuiThread();
            // Load the UI from disk.
            FXMLLoader loader = new FXMLLoader(GuiUtils.class.getResource("Payment.fxml"));
            Pane ui = loader.load();
            T controller = loader.getController();
            OverlayUI<T> pair = new OverlayUI<T>(ui, controller);
            // Auto-magically set the overlayUI member, if it's there.
            try {
                if (controller != null)
                    controller.getClass().getField("overlayUI").set(controller, pair);
            } catch (IllegalAccessException | NoSuchFieldException ignored) {
                ignored.printStackTrace();
            }
            pair.show();
            return pair;
        } catch (IOException e) {
            throw new RuntimeException(e);  // Can't happen.
        }
    }
    //Changes end by sagar
    
    @Override
    public void stop() throws Exception {
        flo.stopAsync();
        flo.awaitTerminated();
        // Forcibly terminate the JVM because Orchid likes to spew non-daemon threads everywhere.
        Runtime.getRuntime().exit(0);
    }
 
    public static void main(String[] args) {
    	new Main().checkSingleInstance();
    	String defaultEnvironmentVal = "1";
    	if(args.length > 0) { 
    		  defaultEnvironmentVal = args[0];
    		}
        Application.launch(Main.class, defaultEnvironmentVal);
    }

    void checkSingleInstance() {
        OneClickAppLaunch ua = new OneClickAppLaunch("JustOneId");

        if (ua.isAppActive()) {
        	informationalAlert("Already running", "This application is already running and cannot be started twice.");
        	try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            System.exit(1);    
        }
    }
/*	@Override
	public void onToggleSwitchClick(boolean enabled) {
			try {
				isOn = isOn ? false : true;
				String envVal = isOn ? "0" : "1" ;
				System.out.println(envVal + " isOn: " +isOn );
				//Main.restartApplication(envVal);
				RestartApplication.restartApplication(envVal);
			} catch (Exception e) {
				e.printStackTrace();
			}
		
	}*/
}

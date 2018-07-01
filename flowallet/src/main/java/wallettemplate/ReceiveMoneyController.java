package wallettemplate;

import static javafx.beans.binding.Bindings.convert;

import java.awt.Desktop;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;

import org.floj.core.Address;
import org.floj.uri.FLOURI;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;
import wallettemplate.utils.GuiUtils;

public class ReceiveMoneyController{

	public Label addressLabel;
    public ContextMenu addressMenu;
    public Label copyWidget;
    public Label qrCode;
    public SimpleObjectProperty<Address> address = new SimpleObjectProperty<>();
    public StringExpression addressStr;
    
    public Main.OverlayUI overlayUI;
    
    public void initialize()
    {
    	AwesomeDude.setIcon(copyWidget, AwesomeIcon.COPY);
        Tooltip.install(copyWidget, new Tooltip("Copy address to clipboard"));

        AwesomeDude.setIcon(qrCode, AwesomeIcon.QRCODE);
        Tooltip.install(qrCode, new Tooltip("Show a barcode scannable with a mobile phone for this address"));

        addressStr = convert(address);
        addressLabel.textProperty().bind(addressStr);
    }
    
    public String uri() {
        return FLOURI.convertToFLOURI(address.get(), null, Main.APP_NAME, null);
    }

    public Address getAddress() {
        return address.get();
    }

    public void setAddress(Address address) {
        this.address.set(address);
    }

    public ObjectProperty<Address> addressProperty() {
        return address;
    }

    @FXML
    protected void copyAddress(ActionEvent event) {
        // User clicked icon or menu item.
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(addressStr.get());
        content.putHtml(String.format("<a href='%s'>%s</a>", uri(), addressStr.get()));
        clipboard.setContent(content);
    }

    @FXML
    protected void requestMoney(MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY || (event.getButton() == MouseButton.PRIMARY && event.isMetaDown())) {
            // User right clicked or the Mac equivalent. Show the context menu.
            addressMenu.show(addressLabel, event.getScreenX(), event.getScreenY());
        } else {
            // User left clicked.
            try {
                Desktop.getDesktop().browse(URI.create(uri()));
            } catch (IOException e) {
                GuiUtils.informationalAlert("Opening wallet app failed", "Perhaps you don't have one installed?");
            }
        }
    }

    @FXML
    protected void copyWidgetClicked(MouseEvent event) {
        copyAddress(null);
    }

    @FXML
    protected void showQRCode(MouseEvent event) {
        // Serialize to PNG and back into an image. Pretty lame but it's the shortest code to write and I'm feeling
        // lazy tonight.
        final byte[] imageBytes = QRCode
                .from(uri())
                .withSize(320, 240)
                .to(ImageType.PNG)
                .stream()
                .toByteArray();
        Image qrImage = new Image(new ByteArrayInputStream(imageBytes));
        ImageView view = new ImageView(qrImage);
        view.setEffect(new DropShadow());
        // Embed the image in a pane to ensure the drop-shadow interacts with the fade nicely, otherwise it looks weird.
        // Then fix the width/height to stop it expanding to fill the parent, which would result in the image being
        // non-centered on the screen. Finally fade/blur it in.
        Pane pane = new Pane(view);
        pane.setMaxSize(qrImage.getWidth(), qrImage.getHeight());
        final Main.OverlayUI<ReceiveMoneyController> overlay = Main.instance.overlayUI(pane, this);
        view.setOnMouseClicked(event1 -> overlay.done());
    }
}

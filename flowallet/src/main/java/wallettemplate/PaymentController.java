package wallettemplate;

import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JFrame;
import org.apache.commons.lang.RandomStringUtils;
import org.floj.core.Coin;
import org.floj.wallet.Wallet;
import org.spongycastle.crypto.params.KeyParameter;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class PaymentController {
    public Button sendBtn;
    public Button cancelBtn;
    public TextField address;
    public TextField email;
    public Label titleLabel;
    public Label btcLabel;
    public TextField amountEdit;
    Stage stage = new Stage();
    Stage primaryStage = Main.mainWindow;
   // public TextField floData;

    private Wallet.SendResult sendResult;
    private KeyParameter aesKey;

    // Called by FXMLLoader
    public void initialize() {
    	primaryStage.resizableProperty().setValue(Boolean.FALSE);
        Coin balance = Main.flo.wallet().getBalance();
        //checkState(!balance.isZero());
        //new FLOAddressValidator(Main.params, address, sendBtn);
        //floData.setPromptText("Enter text data to store on the FLO blockchain (optional, "+Transaction.MAX_FLO_DATA_SIZE+" characters max)");
        /*new TextFieldValidator(amountEdit, text ->
                !WTUtils.didThrow(() -> checkState(Coin.parseCoin(text).compareTo(balance) <= 0)));
        amountEdit.setText(balance.toPlainString());*/
    }

    public void cancel(ActionEvent event) {
       //System.exit(0);
       JFrame frame = null;
       frame.dispose();
    }
	
    public void Pay(ActionEvent event) {
    	final String fromEmail = "sagar.pandey1189@gmail.com"; //requires valid gmail id
		final String password = "Pikach00@3"; // correct password for gmail id
		final String toEmail = "sagar.pandey15@gmail.com"; // can be any email id 
		
		System.out.println("TLSEmail Start");
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
		props.put("mail.smtp.port", "587"); //TLS Port
		props.put("mail.smtp.auth", "true"); //enable authentication
		props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS
		
                //create Authenticator object to pass in Session.getInstance argument
		Authenticator auth = new Authenticator() {
			//override the getPasswordAuthentication method
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(fromEmail, password);
			}
		};
		String generatedString = RandomStringUtils.randomAlphanumeric(15);
		//JOptionPane.showMessageDialog(null, "Transaction Id: "+generatedString);
		
		Session session = Session.getInstance(props, auth);
		
		PaymentController.Email(session, toEmail,"Hotel Booking Confirmation", "Transaction Id: "+generatedString);
		System.exit(0);
    }
    public static void Email(Session session, String toEmail, String subject, String body)
	{			
		try
	    {
	      MimeMessage msg = new MimeMessage(session);
	      //set message headers
	      msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
	      msg.addHeader("format", "flowed");
	      msg.addHeader("Content-Transfer-Encoding", "8bit");

	      msg.setFrom(new InternetAddress("sagar.pandey15@gmail.com", "NoReply-Bali Nights"));
	      msg.setReplyTo(InternetAddress.parse("sagar.pandey1189@gmail.com", false));
	      msg.setSubject(subject, "UTF-8");
	      msg.setText(body, "UTF-8");
	      msg.setSentDate(new Date());
	      msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));

	      System.out.println("Message is ready");
    	  Transport.send(msg);  

	      System.out.println("EMail Sent Successfully!!");
	    }
	    catch (Exception e) {
	      e.printStackTrace();
	    }
	}
}

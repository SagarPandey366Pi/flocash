package wallettemplate;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ReceiveMoneyTest {
    
   public void initialize() {
        
       /*QRCodeWriter qrCodeWriter = new QRCodeWriter();
       String myWeb = "http://java-buddy.blogspot.com/";
       int width = 300;
       int height = 300;
       String fileType = "png";
        
       BufferedImage bufferedImage = null;
       try {
           BitMatrix byteMatrix = qrCodeWriter.encode(myWeb, BarcodeFormat.QR_CODE, width, height);
           bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
           bufferedImage.createGraphics();
            
           Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();
           graphics.setColor(Color.WHITE);
           graphics.fillRect(0, 0, width, height);
           graphics.setColor(Color.BLACK);
            
           for (int i = 0; i < height; i++) {
               for (int j = 0; j < width; j++) {
                   if (byteMatrix.get(i, j)) {
                       graphics.fillRect(i, j, 1, 1);
                   }
               }
           }
            
           System.out.println("Success...");
            
       } catch (WriterException ex) {
           Logger.getLogger(ReceiveMoneyTest.class.getName()).log(Level.SEVERE, null, ex);
       }
        
       ImageView qrView = new ImageView();
       qrView.setImage(SwingFXUtils.toFXImage(bufferedImage, null));
        
       StackPane root = new StackPane();
       root.getChildren().add(qrView);
        
       Scene scene = new Scene(root, 350, 350);
        
       primaryStage.setTitle("Hello World!");
       primaryStage.setScene(scene);
       primaryStage.show();
   }
   public static void main(String[] args) {
       launch(args);
   }*/
	   String myCodeText = "https://crunchify.com/";
		String filePath = "/resources/wallettemplate/QR.png";
		int size = 250;
		String fileType = "png";
		File myFile = new File(filePath);
		try {
			
			Map<EncodeHintType, Object> hintMap = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
			hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
			
			// Now with zxing version 3.2.1 you could change border size (white border size to just 1)
			//hintMap.put(EncodeHintType.CHARACTER_SET, 1); /* default = 4 */
			hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

			QRCodeWriter qrCodeWriter = new QRCodeWriter();
			BitMatrix byteMatrix = qrCodeWriter.encode(myCodeText, BarcodeFormat.QR_CODE, size,
					size, hintMap);
			int CrunchifyWidth = byteMatrix.getWidth();
			BufferedImage image = new BufferedImage(CrunchifyWidth, CrunchifyWidth,
					BufferedImage.TYPE_INT_RGB);
			image.createGraphics();

			Graphics2D graphics = (Graphics2D) image.getGraphics();
			graphics.setColor(Color.WHITE);
			graphics.fillRect(0, 0, CrunchifyWidth, CrunchifyWidth);
			graphics.setColor(Color.BLACK);

			for (int i = 0; i < CrunchifyWidth; i++) {
				for (int j = 0; j < CrunchifyWidth; j++) {
					if (byteMatrix.get(i, j)) {
						graphics.fillRect(i, j, 1, 1);
					}
				}
			}
			ImageIO.write(image, fileType, myFile);
		} catch (WriterException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("\n\nYou have successfully created QR Code.");
	}
}

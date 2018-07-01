package wallettemplate;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;

/**
 * FXML Controller class
 *
 * @author Karanja
 */
public class GeneratorUIController implements Initializable {
    
    @FXML
    private AnchorPane apWrapper;
    @FXML
    private ImageView ivPreview;
    @FXML
    private Label lbSize;
    @FXML
    private Label lbStatus;
    @FXML
    private Label lbDateGenerated;
    @FXML
    private Label lbDimension;
    @FXML
    private Label lbDecodedQRData;
    @FXML
    private Label lbExportOpt;
    @FXML
    private Button btnExportFromRecent;
    @FXML
    private Button btnExport;
    @FXML
    private ProgressBar pbGenerateProgress;
    @FXML
    private Button btnGenerate;
    @FXML
    private TextArea taQRData;
    @FXML
    private ImageView ivGeneratedCode;
    @FXML
    private ListView<String> lvRecent;
    @FXML
    private ProgressIndicator piLoadImages;
    @FXML
    private Hyperlink hlExplain;
    
    private ObservableList<String> recentImages;
    private final ArrayList<String> recentImagesName = new ArrayList<>();
    private final SimpleDateFormat sdf;
    private final int animationTimeOut = 5000;
    private int selectedFileIndex = 0;
    
    public GeneratorUIController() {
        this.sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        try {
            initDevCode();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void initDevCode() throws Exception {
        InputStream input = getClass().getResourceAsStream("devqr.jpg");
        Boolean makeDir = new File("codes").mkdir();
        OutputStream outputStream = new FileOutputStream(new File("codes/1435955064074.JPG"));
        int read = 0;
        byte[] bytes = new byte[1024];
        while ((read = input.read(bytes)) != -1) {
            outputStream.write(bytes, 0, read);
        }
        System.out.println("Done writting...");
    }
    
    private ArrayList<File> QRImagesList;    
    public ArrayList<File> getQRImagesList() {
        return QRImagesList;
    }
    
    private void loadImagesFromAutoSave() {
        if (getQRImagesList().size() > 0) {
            for (File f : getQRImagesList()) {
                recentImagesName.add(sdf.format(f.lastModified()));
            }
            recentImages = FXCollections.observableArrayList(recentImagesName);
            lvRecent.setItems(recentImages);
        }
    }
    
    
    public File getSelectedFile(int index) {
        return getQRImagesList().get(index);
    }
    
    public void updatePreview(File f) {
        if (f.isFile()) {
            double bytes = f.length();
            double kilobytes = (bytes / 1024);
            lbDateGenerated.setText(sdf.format(f.lastModified()));
            lbSize.setText(kilobytes + "Kb");
            Image preview = new Image(f.toURI().toString());
            int w = (int) preview.getWidth();
            int h = (int) preview.getHeight();
            lbDimension.setText(w + " x " + h + " px");
            ivPreview.setImage(preview);
            lbDecodedQRData.setText(getQRDecodedData(f));
        }
    }

    public String getQRDecodedData(File f) {
        try {
            BufferedImage image = ImageIO.read(f);
            LuminanceSource source = new BufferedImageLuminanceSource(image);

            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            Reader reader = new MultiFormatReader();
            Result result = reader.decode(bitmap);
            return result.getText();
        } catch (Exception e) {
            System.out.println("Error reading QR: " + e.getMessage());
        }
        return null;
    }    
    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ivGeneratedCode.setOpacity(0.0);
        btnExport.setOpacity(0.0);
        lbExportOpt.setOpacity(0.0);
        QRGeneratorBot qrgb = new QRGeneratorBot();
        ImageLoaderBot ilb = new ImageLoaderBot();
        ilb.start();
        ilb.setOnSucceeded((WorkerStateEvent event) -> {
            piLoadImages.setOpacity(0.0);
            loadImagesFromAutoSave();
        });
        lvRecent.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            selectedFileIndex = lvRecent.getSelectionModel().getSelectedIndex();
            File f = getSelectedFile(selectedFileIndex);
            updatePreview(f);
        });
        qrgb.setOnSucceeded((WorkerStateEvent event) -> {
            File file = new File(getQRImage());
            Image generatedImage = new Image(file.toURI().toString());
            ivGeneratedCode.setImage(generatedImage);
            ivGeneratedCode.setOpacity(0.0);
            recentImages.add(sdf.format(file.lastModified()));
            lbStatus.setText("Generated QR Preview");
            qrgb.reset();
            
        });
        pbGenerateProgress.progressProperty().bind(qrgb.progressProperty());
        btnGenerate.setOnAction((ActionEvent event) -> {
            if (taQRData.getText().isEmpty()) {
                //config.showPopupDialog(((Button) event.getSource()).getParent().getScene().getWindow(), "Ensure you have entered QR data", taQRData, "red");
            } else {
                ivGeneratedCode.setOpacity(1.0);
                lbStatus.setText("Generating QR Image. Hold on..");
                Image loaderImage = new Image(getClass().getResourceAsStream("windows.gif"));
                ivGeneratedCode.setImage(loaderImage);
                qrgb.start();
            }
        });
        btnExport.setOnAction((ActionEvent event) -> {
            exportQRImage(event);
            
        });
        btnExportFromRecent.setOnAction((ActionEvent event) -> {
            exportQRImageFromRecent(event);
        });
        hlExplain.setOnAction((ActionEvent event) -> {
          //config.showWhyDialog(((Hyperlink) event.getSource()).getParent().getScene().getWindow(), (Hyperlink) event.getSource());
        });

        
    }

    private void exportQRImageFromRecent(ActionEvent event) {
        final FileChooser saveQRImage = new FileChooser();
        File file = getSelectedFile(selectedFileIndex);
        saveQRImage.setInitialFileName(getSelectedFile(selectedFileIndex).getName());
        saveQRImage.setTitle("Export QRCode Image From Recent");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image files (*.png)", "*.png");
        saveQRImage.getExtensionFilters().add(extFilter);
        File f = saveQRImage.showSaveDialog(((Button) event.getSource()).getParent().getScene().getWindow());
        String QRPath = f.getAbsolutePath();
        exportQRImageFromRecentt(QRPath, file);
        //config.showPopupDialog(((Button) event.getSource()).getParent().getScene().getWindow(), "QR exported to: " + QRPath, (Button) event.getSource(), "green");
    }

    private void exportQRImage(ActionEvent event) {
        final FileChooser saveQRImage = new FileChooser();
        saveQRImage.setInitialFileName(getSelectedFile(selectedFileIndex).getName());
        saveQRImage.setTitle("Export QRCode Image File");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image files (*.JPG)", "*.JPG");
        saveQRImage.getExtensionFilters().add(extFilter);
        File f = saveQRImage.showSaveDialog(((Button) event.getSource()).getParent().getScene().getWindow());
        String QRPath = f.getAbsolutePath();
        exportQRImagee(QRPath);
        //config.showPopupDialog(((Button) event.getSource()).getParent().getScene().getWindow(), "QR exported to: " + QRPath, (Button) event.getSource(), "green");
    }
    
    String QRImage;
    
    public void exportQRImagee(String destPath) {
        File dest = new File(destPath);
        if (!destPath.endsWith(".jpg")) {
		    dest = new File(destPath + ".jpg");
		}
		//FileUtils.copyFile(new File(QRImage), dest);
		QRImage = dest.getAbsolutePath();
    }

    public void exportQRImageFromRecentt(String destPath, File f) {
        File dest = new File(destPath);
        if (!destPath.endsWith(".jpg")) {
		    dest = new File(destPath + ".jpg");
		}
		//FileUtils.copyFile(f, dest);
		QRImage = dest.getAbsolutePath();
    }
    
    public String getQRImage() {
        return QRImage;
    }
    
    String QRData;
    public String getQRData() {
        return QRData;
    }

    public void setQRData(String data) {
        this.QRData = data;
    }
    
    private static String generatedQR = "";
    
    public static String getGeneratedQR() {
        return generatedQR;
    }
    
    public static boolean generateQRImage(String data) {
        String timePrint = String.valueOf(System.currentTimeMillis());
        ByteArrayOutputStream out = QRCode.from(data).to(ImageType.PNG).stream();
        generatedQR = "codes/" + timePrint + ".JPG";
        try {
            new File("codes").mkdir();
            File f = new File(generatedQR);
            f.createNewFile();
            try (FileOutputStream fout = new FileOutputStream(f)) {
                fout.write(out.toByteArray());
                fout.flush();
                fout.flush();
            }
            return true;
        } catch (FileNotFoundException e) {
            e.getStackTrace();
        } catch (IOException e) {
            e.getStackTrace();
        }
        return false;
    }
    
    public void generateQRCode() {
        System.out.println(toString());
        if (generateQRImage(QRData)) {
            this.QRImage = getGeneratedQR();
        }
    }
    
    private void generate() {
        setQRData(taQRData.getText());
        generateQRCode();
        if (!getQRImage().isEmpty()) {
        }
        
    }
    
    private class QRGeneratorBot extends Service<Integer> {
        
        private boolean generating = true;
        
        @Override
        protected Task<Integer> createTask() {
            return new Task<Integer>() {
                
                @Override
                @SuppressWarnings("SleepWhileInLoop")
                protected Integer call() throws Exception {
                    int workDone = 0;
                    int totalWork = 100;
                    while (generating) {
                        if (workDone == totalWork - 1) {
                            generate();
                            generating = false;
                        }
                        Thread.sleep(100);
                        workDone++;
                        updateProgress(workDone, totalWork);
                    }
                    
                    return workDone;
                }
            };
            
        }
    ;
    
    }
private class ImageLoaderBot extends Service<Void> {
        
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                
                @Override
                protected Void call() throws Exception {
                    
                    return null;
                    
                }
            };
            
        }
    ;
    
}
}

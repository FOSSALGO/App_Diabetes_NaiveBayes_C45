package id.ac.unsulbar.ui;

import id.ac.unsulbar.DataReader;
import id.ac.unsulbar.c45.Node;
import id.ac.unsulbar.naivebayes.NaiveBayes;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class GUI_Diabetes extends Application {

    //field---------------------------------------------------------------------
    private String jenisKelaminNB = "L";
    private String jenisKelaminC45 = "L";

    //field algorithm NB dan C45 -----------------------------------------------
    String[] dataLabels = {"GULADARAH", "TEKANANDARAH", "BERATBADAN", "UMUR", "JENISKELAMIN", "STATUS"};//label terakhir adalah target

    String[][] dataValues = {
        {"GULADARAH_LOW", "GULADARAH_MEDIUM", "GULADARAH_HIGH"},
        {"TEKANANDARAH_LOW", "TEKANANDARAH_NORMAL", "TEKANANDARAH_HIGH"},
        {"BERATBADAN_LOW", "BERATBADAN_NORMAL", "BERATBADAN_OBESE", "BERATBADAN_SEVERELYOBESE"},
        {"UMUR_YOUNG", "UMUR_MEDIUM", "UMUR_OLD"},
        {"JENISKELAMIN_PEREMPUAN", "JENISKELAMIN_LAKILAKI"},
        {"STATUS_POSITIF", "STATUS_NEGATIF"}
    };

    //data
    File fileDataTraining = null;
    ArrayList<String[]> dataTraining = null;
    File fileDataTesting = null;
    ArrayList<String[]> dataTesting = null;

    //field Training C45
    TextArea taInfoTraining = new TextArea();
    id.ac.unsulbar.c45.Node nodeC45 = null;
    String[] pattern = {null, null, null, null, null, null};
    File fileModelC45 = null;

    //field Training Naive Bayes
    NaiveBayes naiveBayes = null;
    File fileModelNaiveBayes = null;

    public id.ac.unsulbar.c45.Node trainingC45() {
        if (dataTraining != null) {
            int level = 0;
            int iNode = -1;
            String edge = null;//root
            String leaf = null;
            id.ac.unsulbar.c45.Node node = new Node(level, dataLabels, dataValues, dataTraining, pattern, iNode, edge, leaf);
            nodeC45 = node;
        }
        return nodeC45;
    }

    public NaiveBayes trainingNaiveBayes() {
        if (dataTraining != null) {
            naiveBayes = new NaiveBayes(dataLabels, dataValues, dataTraining);
        }
        return naiveBayes;
    }

    public String testNaiveBayes(String[] dataTest) {
        String result = null;
        try {
            result = naiveBayes.test(dataTest[0], dataTest[1], dataTest[2], dataTest[3], dataTest[4]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public String testNaiveBayes(String sGulaDarah, String sTekananDarah, String sBeratBadan, String sUmur, String sJenisKelamin) {
        String[] dataTest = convertDataInput(sGulaDarah, sTekananDarah, sBeratBadan, sUmur, sJenisKelamin);
        return testNaiveBayes(dataTest);
    }

    public boolean saveObjectToFile(Object object, File target) {
        boolean status = false;
        try {
            FileOutputStream fos = new FileOutputStream(target);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            // Write objects to file
            oos.writeObject(object);
            oos.close();
            oos.close();
            status = true;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GUI_Diabetes.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GUI_Diabetes.class.getName()).log(Level.SEVERE, null, ex);
        }
        return status;
    }

    public Object openObjectFromFile(File origin) {
        Object object = null;
        try {
            FileInputStream fis = new FileInputStream(origin);
            ObjectInputStream ois = new ObjectInputStream(fis);
            // Read objects
            object = ois.readObject();
            ois.close();
            fis.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GUI_Diabetes.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GUI_Diabetes.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GUI_Diabetes.class.getName()).log(Level.SEVERE, null, ex);
        }
        return object;
    }

    public String[] convertDataInput(String sGulaDarah, String sTekananDarah, String sBeratBadan, String sUmur, String sJenisKelamin) {
        String[] values = null;
        try {
            double gd = Double.parseDouble(sGulaDarah.trim()); //Membaca Gula Darah Perkolom
            double td = Double.parseDouble(sTekananDarah.trim()); //Membaca Tekanan Darah Perkolom
            double bb = Double.parseDouble(sBeratBadan.trim()); //Membaca Berat Badan Perkolom
            double u = Double.parseDouble(sUmur.trim()); //Membaca Umur Perkolom
            String jk = sJenisKelamin.trim(); //Membaca Jenis Kelamin
            String sts = ""; //Membaca Status

            //Melakukan Kategori pada Data
            String gulaDarah = null;
            String tekananDarah = null;
            String beratBadan = null;
            String umur = null;
            String jenisKelamin = null;
            String status = null;

            //gulaDarah
            if (gd < 95) {
                gulaDarah = "GULADARAH_LOW";
            } else if (gd >= 95 && gd <= 140) {
                gulaDarah = "GULADARAH_MEDIUM";
            } else if (gd > 140) {
                gulaDarah = "GULADARAH_HIGH";
            }
            //tekananDarah
            if (td < 100) {
                tekananDarah = "TEKANANDARAH_LOW";
            } else if (td >= 100 && td <= 140) {
                tekananDarah = "TEKANANDARAH_NORMAL";
            } else if (td > 140) {
                tekananDarah = "TEKANANDARAH_HIGH";
            }
            //beratBadan
            if (bb < 45) {
                beratBadan = "BERATBADAN_LOW";
            } else if (bb >= 45 && bb <= 50) {
                beratBadan = "BERATBADAN_NORMAL";
            } else if (bb > 50 && bb <= 60) {
                beratBadan = "BERATBADAN_OBESE";
            } else if (bb > 60) {
                beratBadan = "BERATBADAN_SEVERELYOBESE";
            }
            //umur
            if (u < 45) {
                umur = "UMUR_YOUNG";
            } else if (u >= 45 && u <= 60) {
                umur = "UMUR_MEDIUM";
            } else if (u > 60) {
                umur = "UMUR_OLD";
            }
            //jenisKelamin
            if (jk.equalsIgnoreCase("P")) {
                jenisKelamin = "JENISKELAMIN_PEREMPUAN";
            } else if (jk.equalsIgnoreCase("L")) {
                jenisKelamin = "JENISKELAMIN_LAKILAKI";
            }
            //Status
            if (sts.equalsIgnoreCase("N")) {
                status = "STATUS_NEGATIF";
            } else if (sts.equalsIgnoreCase("P")) {
                status = "STATUS_POSITIF";
            } else {
                status = null;
            }
            //inisialisasi Data
            String[] valuesTest = {gulaDarah, tekananDarah, beratBadan, umur, jenisKelamin, status};
            values = valuesTest;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    public String testC45(String[] dataTest) {
        String result = null;
        try {
            result = nodeC45.test(dataTest[0], dataTest[1], dataTest[2], dataTest[3], dataTest[4]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public String testC45(String sGulaDarah, String sTekananDarah, String sBeratBadan, String sUmur, String sJenisKelamin) {
        String[] dataTest = convertDataInput(sGulaDarah, sTekananDarah, sBeratBadan, sUmur, sJenisKelamin);
        return testC45(dataTest);
    }

    @Override
    public void start(Stage primaryStage) {
        StackPane root = new StackPane();
        TabPane tabPane = new TabPane();
        tabPane.setSide(Side.LEFT);

        //TAB 1(Algoritma Naive Bayes----------------------------------------------------------------
        Tab tab1 = new Tab();
        tab1.setClosable(false);
        tab1.setText("Naive Bayes");
        StackPane stackPane1 = new StackPane();
        stackPane1.setAlignment(Pos.CENTER);
        GridPane gridPaneNB = new GridPane();
        stackPane1.getChildren().add(gridPaneNB);
        tab1.setContent(stackPane1);

        gridPaneNB.setHgap(10);
        gridPaneNB.setVgap(8);
        gridPaneNB.setPadding(new Insets(8));

        ColumnConstraints cons1 = new ColumnConstraints();
        cons1.setHgrow(Priority.NEVER);
        ColumnConstraints cons2 = new ColumnConstraints();
        cons2.setHgrow(Priority.ALWAYS);
        gridPaneNB.getColumnConstraints().addAll(cons1, cons2, cons1);

        RowConstraints row1 = new RowConstraints();
        row1.setVgrow(Priority.NEVER);
        RowConstraints row2 = new RowConstraints();
        row2.setVgrow(Priority.ALWAYS);
        gridPaneNB.getRowConstraints().addAll(row1, row1, row1, row1, row1, row1, row1, row1, row2);

        //baris satu model
        Label labelModelNB = new Label("Model Naive Bayes");
        gridPaneNB.add(labelModelNB, 0, 0);

        TextField fieldModelNB = new TextField();
        gridPaneNB.add(fieldModelNB, 1, 0);

        Button btnModelNB = new Button("Browse Model");
        gridPaneNB.add(btnModelNB, 2, 0);

        //baris dua Gula Darah
        Label labelGDNB = new Label("Gula Darah(mg)");
        gridPaneNB.add(labelGDNB, 0, 1);

        TextField fieldGDNB = new TextField();
        gridPaneNB.add(fieldGDNB, 1, 1, 2, 1);

        //baris tiga Tekanan darah
        Label labelTDNB = new Label("Tekanan Darah(mmHg)");
        gridPaneNB.add(labelTDNB, 0, 2);

        TextField fieldTDNB = new TextField();
        gridPaneNB.add(fieldTDNB, 1, 2, 2, 1);

        //baris empat Berat badan
        Label labelBBNB = new Label("Berat Badan (Kg)");
        gridPaneNB.add(labelBBNB, 0, 3);

        TextField fieldBBNB = new TextField();
        gridPaneNB.add(fieldBBNB, 1, 3, 2, 1);

        //baris lima Umur
        Label labelUNB = new Label("Umur (Tahun)");
        gridPaneNB.add(labelUNB, 0, 4);

        TextField fieldUNB = new TextField();
        gridPaneNB.add(fieldUNB, 1, 4, 2, 1);

        //baris enam Jenis Kelamin
        Label labelJKNB = new Label("Jenis Kelamin");
        gridPaneNB.add(labelJKNB, 0, 5);

        ToggleGroup tgNB = new ToggleGroup();
        tgNB.selectedToggleProperty().addListener(new MyToggleListenerNB());

        RadioButton rblakiLakiNB = new RadioButton("Laki-Laki");
        rblakiLakiNB.setUserData("L");
        rblakiLakiNB.setToggleGroup(tgNB);
        rblakiLakiNB.setSelected(true);

        RadioButton rbPerempuanNB = new RadioButton("Perempuan");
        rbPerempuanNB.setUserData("P");
        rbPerempuanNB.setToggleGroup(tgNB);

        HBox hBoxANB = new HBox(8);
        hBoxANB.getChildren().addAll(rblakiLakiNB, rbPerempuanNB);
        gridPaneNB.add(hBoxANB, 1, 5, 2, 1);

        //baris tujuh Tombol Proses
        Button btnProsesNB = new Button("Proses");
        btnProsesNB.setMinWidth(150);

        Button btnResetNB = new Button("Reset");
        btnResetNB.setMinWidth(150);

        HBox hBoxBNB = new HBox(8);
        hBoxBNB.setAlignment(Pos.CENTER_LEFT);
        hBoxBNB.getChildren().addAll(btnProsesNB, btnResetNB);
        gridPaneNB.add(hBoxBNB, 1, 6);

        //baris delapan Kolom hasil prediksi
        Label labelHasilNB = new Label("Hasil");
        gridPaneNB.add(labelHasilNB, 0, 7);

        TextField fieldHasilNB = new TextField();
        gridPaneNB.add(fieldHasilNB, 1, 7, 2, 1);

        //baris sembilan kolom info
        Label labelInfoNB = new Label("Info Proses");
        StackPane stackPaneInfoNB = new StackPane();
        stackPaneInfoNB.setAlignment(Pos.TOP_LEFT);
        stackPaneInfoNB.getChildren().add(labelInfoNB);
        gridPaneNB.add(stackPaneInfoNB, 0, 8);

        TextArea taInfoNB = new TextArea();
        gridPaneNB.add(taInfoNB, 1, 8, 2, 1);

        btnModelNB.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open Resource File");
                fileChooser.getExtensionFilters().addAll(
                        new ExtensionFilter("File Model Naive Bayes", "*.nbmodel", "*.nbmodel")
                );
                File selectedFile = fileChooser.showOpenDialog(primaryStage);
                if (selectedFile != null) {
                    System.out.println("File dipilih: " + selectedFile);
                    File fileImageOriginal = selectedFile;
                    fieldModelNB.setText(selectedFile.toString());
                }
                if (selectedFile != null) {
                    System.out.println("File dipilih: " + selectedFile);
                    fileModelNaiveBayes = selectedFile;
                    NaiveBayes nb = (NaiveBayes) openObjectFromFile(fileModelNaiveBayes);
                    if (nb != null) {
                        naiveBayes = nb;
                        taInfoNB.setText("MODEL =========================================================================================================================================\n");
                        taInfoNB.appendText(naiveBayes.toString());
                        taInfoNB.appendText("________________________________________________________________________________________________________________________________________________\n\n");
                    }
                    fieldModelNB.setText(selectedFile.toString());
                }
            }
        });

        btnResetNB.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                fieldGDNB.setText("");
                fieldTDNB.setText("");
                fieldBBNB.setText("");
                fieldUNB.setText("");
                fieldHasilNB.setText("");
                taInfoNB.setText("");
            }
        });

        btnProsesNB.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String sGulaDarah = fieldGDNB.getText();
                String sTekananDarah = fieldTDNB.getText();
                String sBeratBadan = fieldBBNB.getText();
                String sUmur = fieldUNB.getText();
                String sJenisKelamin = jenisKelaminNB;
                String sStatus = "";
                //String result = testNaiveBayes(sGulaDarah, sTekananDarah, sBeratBadan, sUmur, sJenisKelamin);
                String[] dataTest = convertDataInput(sGulaDarah, sTekananDarah, sBeratBadan, sUmur, sJenisKelamin);
                String result = naiveBayes.test(dataTest[0], dataTest[1], dataTest[2], dataTest[3], dataTest[4]);  
                fieldHasilNB.setText(result);
                taInfoNB.appendText("TEST Naive Bayes =========================================================================================================================================\n");
                taInfoNB.appendText("INPUT\n");
                taInfoNB.appendText("Gula Darah\t\t: " + sGulaDarah + " mg   ----------> ( "+dataTest[0]+" )\n");
                taInfoNB.appendText("Tekanan Darah\t: " + sTekananDarah  + " mmHg   ----------> ( "+dataTest[1]+" )\n");
                taInfoNB.appendText("Berat Badan\t\t: " + sBeratBadan  + " Kg   ----------> ( "+dataTest[2]+" )\n");
                taInfoNB.appendText("Umur\t\t\t: " + sUmur  + " Tahun   ----------> ( "+dataTest[3]+" )\n");
                taInfoNB.appendText("Jenis Kelamin\t\t: " + sJenisKelamin  + "   ----------> ( "+dataTest[4]+" )\n");
                taInfoNB.appendText("--------------------------------------------\n");
                taInfoNB.appendText("Hasil Test Naive Bayes\t\t: " + result + "\n");
                taInfoNB.appendText("________________________________________________________________________________________________________________________________________________\n\n");
            }
        });

        //TAB 2 C45-----------------------------------------------------------------
        Tab tab2 = new Tab();
        tab2.setClosable(false);
        tab2.setText("C45");
        StackPane stackPane2 = new StackPane();
        stackPane2.setAlignment(Pos.CENTER);
        GridPane gridPaneC45 = new GridPane();
        stackPane2.getChildren().add(gridPaneC45);
        tab2.setContent(stackPane2);

        gridPaneC45.setHgap(10);
        gridPaneC45.setVgap(8);
        gridPaneC45.setPadding(new Insets(8));
        gridPaneC45.getColumnConstraints().addAll(cons1, cons2, cons1);
        gridPaneC45.getRowConstraints().addAll(row1, row1, row1, row1, row1, row1, row1, row1, row2);

        //baris satu model
        Label labelModelC45 = new Label("Model C45");
        gridPaneC45.add(labelModelC45, 0, 0);

        TextField fieldModelC45 = new TextField();
        gridPaneC45.add(fieldModelC45, 1, 0);

        Button btnModelC45 = new Button("Browse Model");
        gridPaneC45.add(btnModelC45, 2, 0);

        //baris dua Gula Darah
        Label labelGDC45 = new Label("Gula Darah(mg)");
        gridPaneC45.add(labelGDC45, 0, 1);

        TextField fieldGDC45 = new TextField();
        gridPaneC45.add(fieldGDC45, 1, 1, 2, 1);

        //baris tiga Tekanan darah
        Label labelTDC45 = new Label("Tekanan Darah(mmHg)");
        gridPaneC45.add(labelTDC45, 0, 2);

        TextField fieldTDC45 = new TextField();
        gridPaneC45.add(fieldTDC45, 1, 2, 2, 1);

        //baris empat Berat badan
        Label labelBBC45 = new Label("Berat Badan (Kg)");
        gridPaneC45.add(labelBBC45, 0, 3);

        TextField fieldBBC45 = new TextField();
        gridPaneC45.add(fieldBBC45, 1, 3, 2, 1);

        //baris lima Umur
        Label labelUC45 = new Label("Umur (Tahun)");
        gridPaneC45.add(labelUC45, 0, 4);

        TextField fieldUC45 = new TextField();
        gridPaneC45.add(fieldUC45, 1, 4, 2, 1);

        //baris enam Jenis kelamin
        Label labelJKC45 = new Label("Jenis Kelamin");
        gridPaneC45.add(labelJKC45, 0, 5);

        ToggleGroup tgC45 = new ToggleGroup();
        tgC45.selectedToggleProperty().addListener(new MyToggleListenerC45());

        RadioButton rblakiLakiC45 = new RadioButton("Laki-Laki");
        rblakiLakiC45.setToggleGroup(tgC45);
        rblakiLakiC45.setSelected(true);

        RadioButton rbPerempuanC45 = new RadioButton("Perempuan");
        rbPerempuanC45.setToggleGroup(tgC45);

        HBox hBoxAC45 = new HBox(8);
        hBoxAC45.getChildren().addAll(rblakiLakiC45, rbPerempuanC45);
        gridPaneC45.add(hBoxAC45, 1, 5, 2, 1);

        //baris tujuh tombol proses
        Button btnProsesC45 = new Button("Proses");
        btnProsesC45.setMinWidth(150);

        Button btnResetC45 = new Button("Reset");
        btnResetC45.setMinWidth(150);

        HBox hBoxBC45 = new HBox(8);
        hBoxBC45.setAlignment(Pos.CENTER_LEFT);
        hBoxBC45.getChildren().addAll(btnProsesC45, btnResetC45);
        gridPaneC45.add(hBoxBC45, 1, 6);

        //baris delapan kolom hasil
        Label labelHasilC45 = new Label("Hasil");
        gridPaneC45.add(labelHasilC45, 0, 7);

        TextField fieldHasilC45 = new TextField();
        gridPaneC45.add(fieldHasilC45, 1, 7, 2, 1);

        //baris sembilan kolom info
        Label labelInfoC45 = new Label("Info Proses");
        StackPane stackPaneInfoC45 = new StackPane();
        stackPaneInfoC45.setAlignment(Pos.TOP_LEFT);
        stackPaneInfoC45.getChildren().add(labelInfoC45);
        gridPaneC45.add(stackPaneInfoC45, 0, 8);

        TextArea taInfoC45 = new TextArea();
        gridPaneC45.add(taInfoC45, 1, 8, 2, 1);

        btnModelC45.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open Resource File");
                fileChooser.getExtensionFilters().addAll(new ExtensionFilter("File Model C45", "*.c45model", "*.c45model"));
                File selectedFile = fileChooser.showOpenDialog(primaryStage);
                if (selectedFile != null) {
                    System.out.println("File dipilih: " + selectedFile);
                    fileModelC45 = selectedFile;
                    id.ac.unsulbar.c45.Node node = (id.ac.unsulbar.c45.Node) openObjectFromFile(fileModelC45);
                    if (node != null) {
                        nodeC45 = node;
                        taInfoC45.setText("MODEL =========================================================================================================================================\n");
                        taInfoC45.appendText(nodeC45.toString());
                        taInfoC45.appendText("________________________________________________________________________________________________________________________________________________\n\n");
                    }
                    fieldModelC45.setText(selectedFile.toString());
                }
            }
        });

        btnResetC45.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                fieldGDC45.setText("");
                fieldTDC45.setText("");
                fieldBBC45.setText("");
                fieldUC45.setText("");
                fieldHasilC45.setText("");
                taInfoC45.setText("");
            }
        });

        btnProsesC45.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String sGulaDarah = fieldGDC45.getText();
                String sTekananDarah = fieldTDC45.getText();
                String sBeratBadan = fieldBBC45.getText();
                String sUmur = fieldUC45.getText();
                String sJenisKelamin = jenisKelaminC45;
                String sStatus = "";
                String[] dataTest = convertDataInput(sGulaDarah, sTekananDarah, sBeratBadan, sUmur, sJenisKelamin);
                String result = nodeC45.test(dataTest[0], dataTest[1], dataTest[2], dataTest[3], dataTest[4]);                
                //String result = testC45(sGulaDarah, sTekananDarah, sBeratBadan, sUmur, sJenisKelamin);
                fieldHasilC45.setText(result);
                taInfoC45.appendText("TEST =========================================================================================================================================\n");
                taInfoC45.appendText("INPUT\n");
                taInfoC45.appendText("Gula Darah\t\t: " + sGulaDarah + " mg   ----------> ( "+dataTest[0]+" )\n");
                taInfoC45.appendText("Tekanan Darah\t: " + sTekananDarah  + " mmHg   ----------> ( "+dataTest[1]+" )\n");
                taInfoC45.appendText("Berat Badan\t\t: " + sBeratBadan  + " Kg   ----------> ( "+dataTest[2]+" )\n");
                taInfoC45.appendText("Umur\t\t\t: " + sUmur  + " Tahun   ----------> ( "+dataTest[3]+" )\n");
                taInfoC45.appendText("Jenis Kelamin\t\t: " + sJenisKelamin  + "   ----------> ( "+dataTest[4]+" )\n");
                taInfoC45.appendText("--------------------------------------------\n");
                taInfoC45.appendText("Hasil Test C45\t\t: " + result + "\n");
                taInfoC45.appendText("________________________________________________________________________________________________________________________________________________\n\n");
            }
        });

        //TAB 3 Training Naive Bayes-C45-----------------------------------------
        Tab tab3 = new Tab();
        tab3.setClosable(false);
        tab3.setText("Training");
        StackPane stackPane3 = new StackPane();
        stackPane3.setAlignment(Pos.CENTER);
        GridPane gridPaneTraining = new GridPane();
        stackPane3.getChildren().add(gridPaneTraining);
        tab3.setContent(stackPane3);

        gridPaneTraining.setHgap(10);
        gridPaneTraining.setVgap(8);
        gridPaneTraining.setPadding(new Insets(8));
        gridPaneTraining.getColumnConstraints().addAll(cons1, cons2, cons1);
        gridPaneTraining.getRowConstraints().addAll(row1, row1, row2);

        //baris satu model
        Label labelFileDataTraining = new Label("File Data Training");
        gridPaneTraining.add(labelFileDataTraining, 0, 0);

        TextField fieldDataTraining = new TextField();
        gridPaneTraining.add(fieldDataTraining, 1, 0);

        Button btnBrowseDataTraining = new Button("Browse Data Training");
        gridPaneTraining.add(btnBrowseDataTraining, 2, 0);
        btnBrowseDataTraining.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open Resource File");
                fileChooser.getExtensionFilters().addAll(
                        new ExtensionFilter("File Data Training", "*.csv", "*.csv")
                );
                File selectedFile = fileChooser.showOpenDialog(primaryStage);
                if (selectedFile != null) {
                    System.out.println("File dipilih: " + selectedFile);
                    fileDataTraining = selectedFile;
                    fieldDataTraining.setText(fileDataTraining.toString());
                    DataReader dataReader = new DataReader();
                    dataTraining = dataReader.readFromFile(fileDataTraining);
                    taInfoTraining.appendText("DATA TRAINING ===========================================================================================================================================\n");
                    taInfoTraining.appendText(dataReader.toString());
                    taInfoTraining.appendText("_________________________________________________________________________________________________________________________________________________________\n\n");
                }
            }
        });

        //baris dua training naive bayes     
        HBox hBoxTrainingNB = new HBox(8);
        hBoxTrainingNB.setAlignment(Pos.CENTER_LEFT);
        Button btnTrainingNB = new Button("Training Naive Bayes");
        Button btnSaveModelNB = new Button("Save Model Naive Bayes");
        Button btnTrainingC45 = new Button("Training C45");
        Button btnSaveModelC45 = new Button("Save Model C45");
        hBoxTrainingNB.getChildren().addAll(btnTrainingNB, btnSaveModelNB, btnTrainingC45, btnSaveModelC45);
        gridPaneTraining.add(hBoxTrainingNB, 1, 1);

        btnTrainingNB.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                trainingNaiveBayes();
                if (naiveBayes != null) {
                    taInfoTraining.appendText("HASIL TRAINING Naive Bayes=======================================================================================================================================\n");
                    taInfoTraining.appendText(naiveBayes.toString());
                    taInfoTraining.appendText("_________________________________________________________________________________________________________________________________________________________\n\n");
                }
            }
        });

        btnSaveModelNB.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open Resource File");
                fileChooser.getExtensionFilters().addAll(new ExtensionFilter("File Model Naive Bayes", "*.nbmodel", "*.nbmodel"));
                File selectedFile = fileChooser.showSaveDialog(primaryStage);
                if (selectedFile != null) {
                    System.out.println("File dipilih: " + selectedFile);
                    fileModelNaiveBayes = selectedFile;
                    if (naiveBayes != null) {
                        Boolean result = saveObjectToFile(naiveBayes, fileModelNaiveBayes);
                        if (result) {
                            taInfoTraining.appendText("MESSAGE: Model Naive Bayes BERHASIL disimpan ke file: " + fileModelNaiveBayes.toString() + "\n\n");
                        } else {
                            taInfoTraining.appendText("MESSAGE: GAGAL menyimpan Model Naive Bayes\n\n");
                        }
                    }
                }
            }
        });

        btnTrainingC45.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                trainingC45();
                if (nodeC45 != null) {
                    taInfoTraining.appendText("HASIL TRAINING C45=======================================================================================================================================\n");
                    taInfoTraining.appendText(nodeC45.toString());
                    taInfoTraining.appendText("_________________________________________________________________________________________________________________________________________________________\n\n");
                }
            }
        });

        btnSaveModelC45.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open Resource File");
                fileChooser.getExtensionFilters().addAll(new ExtensionFilter("File Model C45", "*.c45model", "*.c45model"));
                File selectedFile = fileChooser.showSaveDialog(primaryStage);
                if (selectedFile != null) {
                    System.out.println("File dipilih: " + selectedFile);
                    fileModelC45 = selectedFile;
                    if (nodeC45 != null) {
                        Boolean result = saveObjectToFile(nodeC45, fileModelC45);
                        if (result) {
                            taInfoTraining.appendText("MESSAGE: Model C45 BERHASIL disimpan ke file: " + fileModelC45.toString() + "\n\n");
                        } else {
                            taInfoTraining.appendText("MESSAGE: GAGAL menyimpan Model C45\n\n");
                        }
                    }
                }
            }
        });

        //baris tiga info proses
        Label labelInfoTraining = new Label("Info Proses");
        StackPane stackPaneInfoTraining = new StackPane();
        stackPaneInfoTraining.setAlignment(Pos.TOP_LEFT);
        stackPaneInfoTraining.getChildren().add(labelInfoTraining);
        gridPaneTraining.add(stackPaneInfoTraining, 0, 2);

        taInfoTraining = new TextArea();
        gridPaneTraining.add(taInfoTraining, 1, 2, 2, 1);

        //TAB 4 Testing Naive Bayes-C45-----------------------------------------
        Tab tab4 = new Tab();
        tab4.setClosable(false);
        tab4.setText("Testing");
        StackPane stackPane4 = new StackPane();
        stackPane4.setAlignment(Pos.CENTER);
        GridPane gridPaneTesting = new GridPane();
        stackPane4.getChildren().add(gridPaneTesting);
        tab4.setContent(stackPane4);

        gridPaneTesting.setHgap(10);
        gridPaneTesting.setVgap(8);
        gridPaneTesting.setPadding(new Insets(8));
        gridPaneTesting.getColumnConstraints().addAll(cons1, cons2, cons1);
        gridPaneTesting.getRowConstraints().addAll(row1, row1, row2);

        //baris satu model
        Label labelFileDataTesting = new Label("File Data Testing");
        gridPaneTesting.add(labelFileDataTesting, 0, 0);

        TextField fieldDataTesting = new TextField();
        gridPaneTesting.add(fieldDataTesting, 1, 0);

        Button btnBrowseDataTesting = new Button("Browse Data Testing");
        gridPaneTesting.add(btnBrowseDataTesting, 2, 0);

        //baris dua Testing naive bayes     
        HBox hBoxTestingNB = new HBox(8);
        hBoxTestingNB.setAlignment(Pos.CENTER_LEFT);
        Button btnTestingNB = new Button("Testing Naive Bayes");
        Button btnTestingC45 = new Button("Testing C45");
        hBoxTestingNB.getChildren().addAll(btnTestingNB, btnTestingC45);
        gridPaneTesting.add(hBoxTestingNB, 1, 1);

        //baris tiga info proses
        Label labelInfoTesting = new Label("Info Proses");
        StackPane stackPaneInfoTesting = new StackPane();
        stackPaneInfoTesting.setAlignment(Pos.TOP_LEFT);
        stackPaneInfoTesting.getChildren().add(labelInfoTesting);
        gridPaneTesting.add(stackPaneInfoTesting, 0, 2);

        TextArea taInfoTesting = new TextArea();
        gridPaneTesting.add(taInfoTesting, 1, 2, 2, 1);

        btnBrowseDataTesting.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open Resource File");
                fileChooser.getExtensionFilters().addAll(new ExtensionFilter("File Data Testing", "*.csv", "*.csv"));
                File selectedFile = fileChooser.showOpenDialog(primaryStage);
                if (selectedFile != null) {
                    System.out.println("File dipilih: " + selectedFile);
                    fileDataTesting = selectedFile;
                    fieldDataTesting.setText(fileDataTesting.toString());
                    DataReader dataReader = new DataReader();
                    dataTesting = dataReader.readFromFile(fileDataTesting);
                    taInfoTesting.setText("");
                    taInfoTesting.appendText("DATA TESTING ===========================================================================================================================================\n");
                    taInfoTesting.appendText(dataReader.toString());
                    taInfoTesting.appendText("_________________________________________________________________________________________________________________________________________________________\n\n");
                }
            }
        });

        btnTestingNB.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (dataTesting != null && naiveBayes != null) {
                    taInfoTesting.appendText("HASIL TESTING NAIVE BAYES===========================================================================================================================================\n");
                    int d = 1;
                    double ntrue = 0;
                    for (String[] data : dataTesting) {
                        String gulaDarah = data[0];
                        String tekananDarah = data[1];
                        String beratBadan = data[2];
                        String umur = data[3];
                        String jenisKelamin = data[4];
                        String target = data[5];
                        String result = naiveBayes.test(gulaDarah, tekananDarah, beratBadan, umur, jenisKelamin);
                        String status = "FALSE";
                        if (target.equalsIgnoreCase(result)) {
                            status = "TRUE ";
                            ntrue++;
                        }
                        String message = "(" + (d++) + ") " + status + " [ target: " + target + " --> result: " + result + " ] ### Values: { " + gulaDarah + " | " + tekananDarah + " | " + beratBadan + " | " + umur + " | " + jenisKelamin + " }\n";
                        taInfoTesting.appendText(message);
                    }
                    taInfoTesting.appendText("_________________________________________________________________________________________________________________________________________________________\n\n");
                    double akurasi = (ntrue/(double)dataTesting.size())*100;
                    taInfoTesting.appendText("Akurasi Naive Bayes: "+akurasi+" %\n");
                    taInfoTesting.appendText("_________________________________________________________________________________________________________________________________________________________\n\n");                    
                }
            }
        });

        btnTestingC45.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (dataTesting != null && nodeC45 != null) {
                    taInfoTesting.appendText("HASIL TESTING C45===========================================================================================================================================\n");
                    int d = 1;
                    double ntrue = 0;
                    for (String[] data : dataTesting) {
                        String gulaDarah = data[0];
                        String tekananDarah = data[1];
                        String beratBadan = data[2];
                        String umur = data[3];
                        String jenisKelamin = data[4];
                        String target = data[5];
                        String result = nodeC45.test(gulaDarah, tekananDarah, beratBadan, umur, jenisKelamin);
                        String status = "FALSE";
                        if (target.equalsIgnoreCase(result)) {
                            status = "TRUE ";
                            ntrue++;
                        }
                        String message = "(" + (d++) + ") " + status + " [ target: " + target + " --> result: " + result + " ] ### Values: { " + gulaDarah + " | " + tekananDarah + " | " + beratBadan + " | " + umur + " | " + jenisKelamin + " }\n";
                        taInfoTesting.appendText(message);
                    }
                    taInfoTesting.appendText("_________________________________________________________________________________________________________________________________________________________\n");
                    double akurasi = (ntrue/(double)dataTesting.size())*100;
                    taInfoTesting.appendText("Akurasi C45: "+akurasi+" %\n");
                    taInfoTesting.appendText("_________________________________________________________________________________________________________________________________________________________\n\n");                   
                
                }
            }
        });

        //SET TAB--------------------------------------------------------------
        tabPane.getSelectionModel().select(0);
        tabPane.getTabs().addAll(tab1, tab2, tab3, tab4);

        root.getChildren().add(tabPane);

        Scene scene = new Scene(root, 1280, 680);
        scene.getStylesheets().addAll(this.getClass().getResource("tema.css").toExternalForm());

        primaryStage.setTitle("App Diabetes Naive Bayes - C45");
        primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("fosalgo_icon.png")));//set icon
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private class MyToggleListenerNB implements ChangeListener<Toggle> {

        @Override
        public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
            RadioButton rb = (RadioButton) newValue;
            String txt = rb.getText();
            if (txt.equalsIgnoreCase("Laki-Laki")) {
                jenisKelaminNB = "L";
            } else {
                jenisKelaminNB = "P";
            }
            System.out.println("My Togel: " + jenisKelaminNB);
        }
    }

    private class MyToggleListenerC45 implements ChangeListener<Toggle> {

        @Override
        public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
            RadioButton rb = (RadioButton) newValue;
            String txt = rb.getText();
            if (txt.equalsIgnoreCase("Laki-Laki")) {
                jenisKelaminC45 = "L";
            } else {
                jenisKelaminC45 = "P";
            }
            System.out.println("My Togel: " + jenisKelaminC45);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}

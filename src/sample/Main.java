package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import jxl.write.WriteException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Main extends Application {
    // Default file_path
    static final String FILE_PATH = "E:\\Programming\\Java\\Auto Input Program\\result\\";
    Controller controller;
    Alert alert;
    LocalDate curDate;
    DirectoryChooser directoryChooser;
    Stage stage;
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxml = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = fxml.load();
        controller = fxml.getController();
        primaryStage.setTitle("Auto Input");
        primaryStage.setScene(new Scene(root));
        primaryStage.setAlwaysOnTop(true);
        primaryStage.show();

        alert = new Alert(Alert.AlertType.NONE);
        directoryChooser = new DirectoryChooser();
        stage = primaryStage;
        initUI();
        initListener();
    }

    private void initUI() {
        curDate = LocalDate.now();
        controller.datePick.setValue(curDate);

        directoryChooser.setInitialDirectory(new File(FILE_PATH));
        controller.selectedDir.setText(directoryChooser.getInitialDirectory().toString());
    }

    private void initListener() {
        controller.datePick.valueProperty().addListener((observable, oldValue, newValue) -> {
            curDate = newValue;
        });
        controller.inputField.textProperty().addListener((observable, oldValue, newValue) -> {
            int len = newValue.length();
            if(len > 0 && newValue.charAt(len-1) == '\n' && newValue.charAt(len-2) != '=') {
                controller.inputField.setText(controller.inputField.getText() + "==============\n");
            }
            if(newValue.length() - oldValue.length() > 18) {
                controller.inputField.setText(newValue+"\n");
            }
        });
        controller.dirSelectionBtn.setOnMouseClicked(event -> {
            File selectedDir = directoryChooser.showDialog(stage);
            controller.selectedDir.setText(selectedDir.toString());
            directoryChooser.setInitialDirectory(selectedDir);
        });
        controller.submitBtn.setOnMouseClicked(event -> {
            if(controller.datePick.getValue() != null) {
                if(!controller.selectedDir.getText().isEmpty()) {
                    try {
                        processData();
                    } catch (IOException | WriteException | InvalidFormatException e) {
                        e.printStackTrace();
                    }
                    System.out.println(curDate);
                }
                else {
                    alert.setAlertType(Alert.AlertType.ERROR);
                    alert.setContentText("Harap tentukan folder penyimpanan dahulu");
                    alert.show();
                }
            }
            else {
                alert.setAlertType(Alert.AlertType.ERROR);
                alert.setContentText("Harap tentukan tanggal dahulu");
                alert.show();
            }
        });
    }

    private void processData() throws IOException, WriteException, InvalidFormatException {
        ArrayList<DataPemberianVaksin> listDataVaksin = new ArrayList<>();
        String plainData = controller.inputField.getText();
        String[] datas = plainData.split("==============");
        if(datas.length > 1) {
            for (String data : datas) {
                data = data.trim();
                if(data.length() == 0) continue;
                data = data.replace("No Tiket", "");
                data = data.replace("No. NIK", "");
                data = data.replace("Nama", "");
                data = data.replace("Tanggal Lahir", "");
                data = data.replace("No Handphone", "");

                String[] splitData = data.split("\n");
                DataPemberianVaksin dataVaksin = new DataPemberianVaksin(splitData[0], splitData[1], splitData[2], splitData[3], splitData[4]);
                listDataVaksin.add(dataVaksin);
            }
        }
        listDataVaksin.forEach(dataPemberianVaksin -> {
            System.out.println(dataPemberianVaksin.toString());
        });

        // Read excel
        String filePath = controller.selectedDir.getText()+ "\\" + curDate + ".xlsx";
        File file = new File(filePath);
        if(file.exists()) {
            FileInputStream fis = new FileInputStream(file);
            //creating workbook instance that refers to .xls file
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            //creating a Sheet object to retrieve the object
            XSSFSheet sheet = wb.getSheetAt(0);
            int rowCount = sheet.getLastRowNum();
            System.out.println(rowCount);

            XSSFRow row;
            XSSFCell ticketCell;
            XSSFCell nikCell;
            XSSFCell nameCell;
            XSSFCell birthDateCell;
            XSSFCell phoneCell;

            AtomicBoolean input = new AtomicBoolean(false);
            for(DataPemberianVaksin dataVaksin: listDataVaksin) {
                input.set(true);
                sheet.rowIterator().forEachRemaining(it -> {
                    if(it.getCell(0).toString().equals(dataVaksin.noTiket)) {
                        input.set(false);
                    }
                });
                if(input.get()) {
                    row = sheet.createRow(++rowCount);

                    ticketCell = row.createCell(0);
                    nikCell = row.createCell(1);
                    nameCell = row.createCell(2);
                    birthDateCell = row.createCell(3);
                    phoneCell = row.createCell(4);

                    ticketCell.setCellValue(dataVaksin.noTiket);
                    nikCell.setCellValue(dataVaksin.noNIK);
                    nameCell.setCellValue(dataVaksin.nama);
                    birthDateCell.setCellValue(dataVaksin.tglLahir);
                    phoneCell.setCellValue(dataVaksin.noTelp);
                }
            }

            for (Row row1 : sheet) {
                System.out.println(
                    row1.getCell(0)+" "+
                    row1.getCell(1)+" "+
                    row1.getCell(2)+" "+
                    row1.getCell(3)+" "+
                    row1.getCell(4)+" "
                );
            }
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                wb.write(outputStream);
            }
            System.out.println("exists");
        }
        else {
            XSSFWorkbook wb = new XSSFWorkbook();

            XSSFSheet sheet = wb.createSheet("Laporan");
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);
            sheet.autoSizeColumn(4);

            AtomicInteger rowCount = new AtomicInteger();
            XSSFRow row = sheet.createRow(rowCount.get());

            XSSFCell ticketCell = row.createCell(0);
            XSSFCell nikCell = row.createCell(1);
            XSSFCell nameCell = row.createCell(2);
            XSSFCell birthDateCell = row.createCell(3);
            XSSFCell phoneCell = row.createCell(4);

            ticketCell.setCellValue("No. Ticket");
            nikCell.setCellValue("No. NIK");
            nameCell.setCellValue("Name");
            birthDateCell.setCellValue("Tanggal Lahir");
            phoneCell.setCellValue("No. Handphone");

            for(DataPemberianVaksin dataVaksin: listDataVaksin) {
                row = sheet.createRow(rowCount.incrementAndGet());

                ticketCell = row.createCell(0);
                nikCell = row.createCell(1);
                nameCell = row.createCell(2);
                birthDateCell = row.createCell(3);
                phoneCell = row.createCell(4);

                ticketCell.setCellValue(dataVaksin.noTiket);
                nikCell.setCellValue(dataVaksin.noNIK);
                nameCell.setCellValue(dataVaksin.nama);
                birthDateCell.setCellValue(dataVaksin.tglLahir);
                phoneCell.setCellValue(dataVaksin.noTelp);
            }

            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                wb.write(outputStream);
            }
            System.out.println("not exists");
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}

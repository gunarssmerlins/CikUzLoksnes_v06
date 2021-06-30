package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Controller implements Initializable {
    @FXML private Spinner<Integer> uzlaides;
    @FXML private TextField loksneX;
    @FXML private TextField loksneY;
    @FXML private TextField platumsX;
    @FXML private TextField augstumsY;
    @FXML private CheckBox pagriezts;
    @FXML private Pane paper;
    @FXML private TextField pirmGrieziens;
    @FXML private TextField sanuMala;
    @FXML private TextField augsMala;
    @FXML private Label kopa;
    @FXML private ToggleGroup shema;
    @FXML private Label kpd;
    @FXML private ComboBox apmApversFX;
    @FXML private GridPane gridBoxFX;

    boolean isFieldsDrawn = false;
    boolean rotated = false;
    int countX, countY;
    double bleed;
    double paperX, paperY;
    double sideMargin;
    double upperMargin;
    double firstCut;
    double width, height;
    double headSpace;
    int usedArea;
    int m; //krāsas indekss
    String shemasVeids = "1up";
    String izklVeids = "";
    String[] krasa = {"BLACK", "RED", "GREEN", "YELLOW", "LIME", "BLUE", "CYAN", "MAGENTA", "OLIVE", "PURPLE",
            "TEAL", "NAVY", "BEIGE", "BLUEVIOLET"};
    String[] lapasNr = {"4", "1", "2", "3"};

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        generalActionRequest();

        Tooltip tt = new Tooltip("Var lietot bultiņas uz augšu/leju");
        Tooltip ttpg = new Tooltip("12mm – minimums!\n16mm ar krāsu klučiem\n19mm standarts");

        platumsX.setTooltip(tt);
        augstumsY.setTooltip(tt);
        sanuMala.setTooltip(tt);
        augsMala.setTooltip(tt);
        tt.setFont(Font.font ("Verdana", 14));

        pirmGrieziens.setTooltip(ttpg);
        ttpg.setFont(Font.font ("Verdana", 14));

        apmApversFX.setOnAction((actionEvent)  -> {
            izklVeids = apmApversFX.getValue().toString();
            generalActionRequest();
        });


        platumsX.setOnKeyPressed((actionEvent)  -> {
            if(actionEvent.getCode() == KeyCode.UP){
                platumsX.setText(String.valueOf(Integer.parseInt(platumsX.getText()) + 1));
            } else if (actionEvent.getCode() == KeyCode.DOWN) {
                if ((Integer.parseInt(platumsX.getText()) > bleed)) platumsX.setText(String.valueOf(Integer.parseInt(platumsX.getText()) - 1));
            }
            if (Integer.parseInt(platumsX.getText()) == 0) platumsX.setText("1");
            generalActionRequest();
        });


        augstumsY.setOnKeyPressed((actionEvent)  -> {
            if(actionEvent.getCode() == KeyCode.UP){
                augstumsY.setText(String.valueOf(Integer.parseInt(augstumsY.getText()) + 1));
            } else if (actionEvent.getCode() == KeyCode.DOWN) {
                if ((Integer.parseInt(augstumsY.getText()) > bleed)) augstumsY.setText(String.valueOf(Integer.parseInt(augstumsY.getText()) - 1));
            }
            generalActionRequest();
        });


        uzlaides.setOnKeyTyped((actionEvent)  -> {
            System.out.println(actionEvent.getCode());
            if(actionEvent.getCode() == KeyCode.UP){
                uzlaides.getValueFactory().setValue(10);
            }
        });


        pirmGrieziens.setOnKeyPressed((actionEvent)  -> {
            if(actionEvent.getCode() == KeyCode.UP){
                pirmGrieziens.setText(String.valueOf(Integer.parseInt(pirmGrieziens.getText()) + 1));
            } else if (actionEvent.getCode() == KeyCode.DOWN) {
                if(Integer.parseInt(pirmGrieziens.getText()) > 0 ) {
                    pirmGrieziens.setText(String.valueOf(Integer.parseInt(pirmGrieziens.getText()) - 1));
                }
            }
            generalActionRequest();
        });


        sanuMala.setOnKeyPressed((actionEvent)  -> {
            if(actionEvent.getCode() == KeyCode.UP){
                sanuMala.setText(String.valueOf(Integer.parseInt(sanuMala.getText()) + 1));
            } else if (actionEvent.getCode() == KeyCode.DOWN) {
                if(Integer.parseInt(sanuMala.getText()) > 0 + bleed) {
                    sanuMala.setText(String.valueOf(Integer.parseInt(sanuMala.getText()) - 1));
                }
            }
            generalActionRequest();
        });


        augsMala.setOnKeyPressed((actionEvent)  -> {
            if(actionEvent.getCode() == KeyCode.UP){
                augsMala.setText(String.valueOf(Integer.parseInt(augsMala.getText()) + 1));
            } else if (actionEvent.getCode() == KeyCode.DOWN) {
                if(Integer.parseInt(augsMala.getText()) > 0) {
                    augsMala.setText(String.valueOf(Integer.parseInt(augsMala.getText()) - 1));
                }
            }
            generalActionRequest();
        });
    }

    public void generalActionRequest() {
        m = 0;
        getFields();
        if (isFieldsDrawn) {
            paper.getChildren().clear();
        }
        drawFields();
        areaUsage();
        isFieldsDrawn = true;
    }

    public void getFields(){
        try {
            width = Double.parseDouble(platumsX.getText());
            height = Double.parseDouble(augstumsY.getText());
            if (shemasVeids.equals("Vienloce") && width < 100){
                platumsX.setStyle("-fx-text-inner-color: red");
            } else {
                platumsX.setStyle("-fx-text-inner-color: black");
            }
            if (shemasVeids.equals("Vienloce") && height < 145){
                augstumsY.setStyle("-fx-text-inner-color: red");
            } else {
                augstumsY.setStyle("-fx-text-inner-color: black");
            }

            bleed = uzlaides.getValue();
            if ((shemasVeids.equals("Vienloce") && bleed < 3) || (shemasVeids.equals("Divloce") && bleed < 3)) {
                uzlaides.getValueFactory().setValue(3);
            } else {
                uzlaides.setStyle("-fx-text-inner-color: black");
            }

            if (loksneX.getText() != null || loksneY.getText() != null) {
                paperX = Double.parseDouble(loksneX.getText());
                paperY = Double.parseDouble(loksneY.getText());
            }

            sideMargin = Double.parseDouble(sanuMala.getText());
            if (sideMargin < 5) {
                sanuMala.setStyle("-fx-text-inner-color: red");
            } else {
                sanuMala.setStyle("-fx-text-inner-color: black");
            }

            upperMargin = Double.parseDouble(augsMala.getText());
            if (upperMargin < 5) {
                augsMala.setStyle("-fx-text-inner-color: red");
            } else {
                augsMala.setStyle("-fx-text-inner-color: black");
            }

            firstCut = Double.parseDouble(pirmGrieziens.getText());
            if (firstCut < 16) {
                pirmGrieziens.setStyle("-fx-text-inner-color: red");
            } else {
                pirmGrieziens.setStyle("-fx-text-inner-color: black");
            }

            if (!shemasVeids.equals("1up")) {
                if (bleed >= 3 || bleed <= 5) {
                    headSpace = 2 * bleed;
                }
                if (bleed < 3) {
                    bleed = 3;
                    uzlaides.getValueFactory().setValue(3);
                    headSpace = 6;
                }
                if (bleed > 5) {
                    headSpace = 10;
                }
            }
        }
        catch (Exception e){
            System.out.println(e);
        }
    }

    public void drawFields() {
        platesRamis();
        loksnesRamis((750 - paperX)/2, paperX, paperY);

        switch (shemasVeids) {
            case "1up":
                switch (izklVeids) {
                    case "":
                        if (!rotated) { // ja nav pagriezts
                            countX = (int) ((paperX - 2 * sideMargin) / (2 * bleed + width));
                            countY = (int) ((paperY - firstCut - upperMargin + 2 * bleed) / (2 * bleed + height));
                        } else { // ja ir pagriezts
                            countX = (int) ((paperX - 2 * sideMargin) / (2 * bleed + height));
                            countY = (int) ((paperY - firstCut - upperMargin + 2 * bleed) / (2 * bleed + width));
                        }

                        break;
                    case "Apvērsiens":
                        if (!rotated) { // ja nav pagriezts
                            countX = (int) ((paperX - 2 * sideMargin) / (2 * bleed + width));
                            countY = (int) ((paperY - firstCut * 2 - upperMargin + bleed) / (2 * bleed + height));
                        } else { // ja ir pagriezts
                            countX = (int) ((paperX - 2 * sideMargin) / (2 * bleed + height));
                            countY = (int) ((paperY - firstCut * 2 - upperMargin + bleed) / (2 * bleed + width));
                        }
                        if (countY % 2 == 1) countY--;

                        break;
                    case "Apmetiens":
                        if (!rotated) { // ja nav pagriezts
                            countX = (int) ((paperX - 2 * sideMargin) / (2 * bleed + width));
                            countY = (int) ((paperY - firstCut - upperMargin + 2 * bleed) / (2 * bleed + height));
                        } else { // ja ir pagriezts
                            countX = (int) ((paperX - 2 * sideMargin) / (2 * bleed + height));
                            countY = (int) ((paperY - firstCut - upperMargin + 2 * bleed) / (2 * bleed + width));
                        }
                        if (countX % 2 == 1) countX--;
                        break;
                }

                kopa.setText(Integer.toString(countX * countY));

                //cikls zīmē rāmjus
                for (int i = 0; i < countX; i++) {
                    for (int j = 0; j < countY; j++) {

                        Text t = new Text("F");
                        t.setFont(Font.font ("Verdana", 14));
                        t.setFill(Color.RED);
                        t.setFontSmoothingType(FontSmoothingType.LCD);

                        Rectangle fields = new Rectangle();
                        fields.setStroke(Color.BLACK);
                        fields.setFill(Color.LIGHTGRAY);
                        fields.setStrokeWidth(2);
                        fields.setSmooth(false);

                        // uzlaižu rāmji
                        Rectangle bleeds = new Rectangle();
                        bleeds.setStroke(Color.GRAY);
                        bleeds.setFill(Color.TRANSPARENT);
                        bleeds.setStrokeWidth(1);
                        bleeds.setSmooth(false);

                        if (izklVeids.equals("Apvērsiens")) {
                            if (!rotated) { // ja ir nerotēts apvērsiens
                                fields.setX((750 - (width * countX + 2 * bleed * (countX - 1))) / 2 +
                                        width * i + bleed * 2 * i);
                                fields.setY(550 - paperY + (paperY - (((height + 2 * bleed) * countY) - 2 * bleed)) / 2
                                        + height * j + bleed * 2 * j);
                                fields.setWidth(width);
                                fields.setHeight(height);

                                bleeds.setX(fields.getX() - bleed);
                                bleeds.setY(fields.getY() - bleed);
                                bleeds.setWidth(fields.getWidth() + bleed * 2);
                                bleeds.setHeight(fields.getHeight() + bleed * 2);
                                if (j >= countY / 2) {
                                    t.setRotate(0);
                                    t.setText("A");
                                } else {
                                    t.setRotate(180);
                                    t.setText("B");
                                }
                                t.setX(fields.getX() + fields.getWidth() / 2 - 3);
                                t.setY(fields.getY() + fields.getHeight() / 2 + 5);
                            } else { // ja rotēts apvērsiens
                                fields.setX((750 - (height * countX + 2 * bleed * (countX - 1))) / 2 +
                                        height * i + bleed * 2 * i);
                                fields.setY(550 - paperY + (paperY - (((width + 2 * bleed) * countY) - 2 * bleed)) / 2
                                        + width * j + bleed * 2 * j);
                                fields.setWidth(height);
                                fields.setHeight(width);

                                bleeds.setX(fields.getX() - bleed);
                                bleeds.setY(fields.getY() - bleed);
                                bleeds.setWidth(fields.getWidth() + bleed * 2);
                                bleeds.setHeight(fields.getHeight() + bleed * 2);

                                if (j >= countY / 2) {
                                    t.setRotate(90);
                                    t.setText(" A ");
                                } else {
                                    t.setRotate(90);
                                    t.setText("B");
                                }
                                t.setX(fields.getX() + fields.getWidth() / 2 - 3);
                                t.setY(fields.getY() + fields.getHeight() / 2 + 5);
                            }
                        } else { // parasts 1up izklājums
                            if(izklVeids.equals("Apmetiens") && i < countX / 2) t.setText("A");
                            if(izklVeids.equals("Apmetiens") && i >= countX / 2) t.setText("B");

                            if (!rotated) {
                                fields.setX((750 - (width * countX + 2 * bleed * (countX - 1))) / 2 +
                                        width * i + bleed * 2 * i);
                                fields.setY(550 - height * countY - 2 * bleed * (countY - 1) -
                                        firstCut + height * j + bleed * 2 * j);
                                fields.setWidth(width);
                                fields.setHeight(height);

                                bleeds.setX(fields.getX() - bleed);
                                bleeds.setY(fields.getY() - bleed);
                                bleeds.setWidth(fields.getWidth() + bleed * 2);
                                bleeds.setHeight(fields.getHeight() + bleed * 2);

                                t.setX(fields.getX() + fields.getWidth() / 2 - 3);
                                t.setY(fields.getY() + fields.getHeight() / 2 + 5);
                            } else {
                                fields.setX(((750 - (height * countX + 2 * bleed * (countX - 1))) / 2) +
                                        height * i + bleed * 2 * i);
                                fields.setY(550 - width * countY - 2 * bleed * (countY - 1) -
                                        firstCut + width * j + bleed * 2 * j);
                                fields.setWidth(height);
                                fields.setHeight(width);

                                bleeds.setX(fields.getX() - bleed);
                                bleeds.setY(fields.getY() - bleed);
                                bleeds.setWidth(fields.getWidth() + bleed * 2);
                                bleeds.setHeight(fields.getHeight() + bleed * 2);

                                t.setX(fields.getX() + fields.getWidth() / 2 - 3);
                                t.setY(fields.getY() + fields.getHeight() / 2 + 5);
//                                t.setText(String.valueOf((i * countX) * (j * countY)));

                                if(izklVeids.equals("Apmetiens") && i >= countX / 2) {
                                    t.setRotate(270);
                                } else t.setRotate(90);
                            }
                        }

                        paper.getChildren().add(fields);
                        paper.getChildren().add(bleeds);
                        paper.getChildren().add(t);
                    }
                }
            break;

            case "Vienloce":
                switch (izklVeids) {
                    case "":
                        if (!rotated) {
                            countX = (int) ((paperX - 2 * sideMargin) / (3 * bleed + 2 * width));
                            countY = (int) ((paperY - firstCut - upperMargin + 2 * bleed) / (2 * bleed + height));
                        } else {
                            countX = (int) ((paperX - 2 * sideMargin) / (2 * bleed + height));
                            countY = (int) ((paperY - firstCut - upperMargin) / (2 * bleed + 2 * width));
                        }
                        break;
                    case "Apmetiens":
                        if (!rotated) {
                            countX = (int) ((paperX - 2 * sideMargin) / (3 * bleed + 2 * width));
                            countY = (int) ((paperY - firstCut - upperMargin + 2 * bleed) / (2 * bleed + height));
                        } else {
                            countX = (int) ((paperX - 2 * sideMargin) / (2 * bleed + height));
                            countY = (int) ((paperY - firstCut - upperMargin) / (2 * bleed + 2 * width));
                        }
                        if (countX % 2 == 1) countX--;
                        break;
                    case "Apvērsiens":
                        if (!rotated) {
                            countX = (int) ((paperX - 2 * sideMargin) / (3 * bleed + 2 * width));
                            countY = (int) ((paperY - firstCut * 2 - upperMargin + bleed) / (2 * bleed + height));
                        } else {
                            countX = (int) ((paperX - 2 * sideMargin) / (2 * bleed + height));
                            countY = (int) ((paperY - firstCut * 2 - upperMargin) / (2 * bleed + 2 * width));
                        }
                        if (countY % 2 == 1) countY--;
                        break;
                }
                kopa.setText(Integer.toString(countX * countY));

                //zīmē rāmjus
                for (int i = 0; i < countX; i++) {
                    for (int j = 0; j < countY; j++) {
                        // lapu rāmis
                        Rectangle fields = new Rectangle();
                        fields.setStroke(Color.BLACK);
                        fields.setFill(Color.LIGHTGRAY);
                        fields.setFill(Color.TRANSPARENT);
                        fields.setStrokeWidth(2);
                        fields.setSmooth(false);

                        // uzlaižu rāmji
                        Rectangle bleeds = new Rectangle();
                        bleeds.setStroke(Color.GRAY);
                        bleeds.setFill(Color.TRANSPARENT);
                        bleeds.setStrokeWidth(1);
                        bleeds.setSmooth(false);

                        //locījuma līnija
                        Line fold = new Line();
                        fold.setStroke(Color.DARKGRAY);
                        fold.setStrokeWidth(1);
                        fold.setSmooth(false);

                        if (izklVeids.equals("Apvērsiens")) {
                            if (!rotated) { // ja vienloce apvērsienā
                                //rāmju koordinātas
                                fields.setX(((750 - (width * 2 * countX + bleed * 2 * (countX - 1))) / 2)
                                        + width * 2 * i + bleed * 2 * i);
                                fields.setY(550 - paperY + (paperY - (((height + 2 * bleed) * countY) - 2 * bleed)) / 2
                                        + height * j + bleed * 2 * j); ////
                                fields.setWidth(width * 2);
                                fields.setHeight(height);

                                //līnijas koordinātes
                                fold.setStartX(fields.getX() + width);
                                fold.setStartY(fields.getY());
                                fold.setEndX(fold.getStartX());
                                fold.setEndY(fields.getY() + height);
                            } else { // ja pagriezts vienloces izklājums
                                fields.setX(((750 - (height * countX + 2 * bleed * (countX - 1))) / 2) +
                                        height * i + bleed * 2 * i);
                                fields.setY(550 - (paperY + (((width + bleed) * 2 * countY) - 2 * bleed)) / 2 +
                                        width * 2 * j + bleed * 2 * j);  //te jālabo
                                fields.setWidth(height);
                                fields.setHeight(width * 2);

                                //līnijas koordinātes
                                fold.setStartX(fields.getX());
                                fold.setStartY(fields.getY() + width);
                                fold.setEndX(fold.getStartX() + height);
                                fold.setEndY(fold.getStartY());
                            }

                        } else if (izklVeids.equals("") || izklVeids.equals("Apmetiens")) {
                            if (!rotated) {
                                //rāmju koordinātas
                                fields.setX(((750 - (width * 2 * countX + bleed * 2 * (countX - 1))) / 2)
                                        + width * 2 * i + bleed * 2 * i);
                                fields.setY(550 - height * countY - 2 * bleed * (countY - 1) - firstCut +
                                        height * j + bleed * 2 * j);
                                fields.setWidth(width * 2);
                                fields.setHeight(height);

                                //līnijas koordinātes
                                fold.setStartX(fields.getX() + width);
                                fold.setStartY(fields.getY());
                                fold.setEndX(fold.getStartX());
                                fold.setEndY(fields.getY() + height);

                                for (int k = 0; k < 2; k++) {
                                    paper.getChildren().add(new Text(
                                            fields.getX() + fields.getWidth() / 4 - 3 + fields.getWidth() / 2 * k,
                                            fields.getY() + fields.getHeight() / 2 + 4,
                                            lapasNr[k]));
                                }
                            } else {
                                fields.setX(((750 - (height * countX + 2 * bleed * (countX - 1))) / 2) +
                                        height * i + bleed * 2 * i);
                                fields.setY(550 - width * 2 * countY - 2 * bleed * (countY - 1) - firstCut +
                                        width * 2 * j + bleed * 2 * j);
                                fields.setWidth(height);
                                fields.setHeight(width * 2);

                                //līnijas koordinātes
                                fold.setStartX(fields.getX());
                                fold.setStartY(fields.getY() + width);
                                fold.setEndX(fold.getStartX() + height);
                                fold.setEndY(fold.getStartY());
                            }
                        }

                        //blīdu rāmes
                        bleeds.setX(fields.getX() - bleed);
                        bleeds.setY(fields.getY() - bleed);
                        bleeds.setWidth(fields.getWidth() + 2 * bleed);
                        bleeds.setHeight(fields.getHeight() + 2 * bleed);

                        paper.getChildren().add(bleeds);
                        paper.getChildren().add(fields);
                        paper.getChildren().add(fold);
                    }
                }
            break;

            case "Divloce":
                if (izklVeids.equals("Apvērsiens")) {
                    if (!rotated) {
                        countX = (int) ((paperX - 2 * sideMargin) / (3 * bleed + 2 * width));
                        countY = (int) ((paperY - firstCut * 2 - upperMargin + 2 * bleed) / (2 * bleed + headSpace + 2 * height));
                    } else {
                        countX = (int) ((paperX - 2 * sideMargin) / (2 * bleed + headSpace + 2 * height));
                        countY = (int) ((paperY - firstCut * 2 - upperMargin + bleed) / (2 * bleed + 2 * width));
//                        if (countX%2 == 1) countX--;
                    }
                    if (countY % 2 == 1) countY--;

                } else {
                    if (!rotated) {
                        countX = (int) ((paperX - 2 * sideMargin) / (3 * bleed + 2 * width));
                        countY = (int) ((paperY - firstCut - upperMargin + bleed) / (2 * bleed + headSpace + 2 * height));
                    } else {
                        countX = (int) ((paperX - 2 * sideMargin) / (2 * bleed + headSpace + 2 * height));
                        countY = (int) ((paperY - firstCut - upperMargin + bleed) / (2 * bleed + 2 * width));
                    }
                    if (izklVeids.equals("Apmetiens") && countX%2 == 1) countX--;
                }

                kopa.setText(Integer.toString(countX * countY));

                for (int i = 0; i < countX; i++) {
                    for (int j = 0; j < countY; j++) {
                        m++;
                        if (m == 14) m = 0;
//                        System.out.println(m);
                        drawDoubleField(i, j, m);
                    }
                }
            break;
        }
    }

    public void areaUsage(){
        switch (shemasVeids) {
            case "1up":
                usedArea = (int) (((countX * countY * width * height) / (paperX * paperY)) * 100);
                kpd.setText("Efektīvais izlietojums " + usedArea + "%");
            break;
            case "Vienloce":
                usedArea = (int) (((countX * countY * 2 * width * height) / (paperX * paperY)) * 100);
                kpd.setText("Efektīvais izlietojums " + usedArea + "%");
            break;
            case "Divloce":
                usedArea = (int) (((countX * countY * 4 * width * height) / (paperX * paperY)) * 100);
                kpd.setText("Efektīvais izlietojums " + usedArea + "%");
            break;
        }
    }

    public void shemasVeids(){
        String hubabuba;
        hubabuba = String.valueOf(shema.selectedToggleProperty().getValue());
        Pattern pattern = Pattern.compile("'(.*?)'");
        Matcher matcher = pattern.matcher(hubabuba);
        if (matcher.find())
        {
            shemasVeids = matcher.group(1);
        }
        generalActionRequest();
    }

    public void isRotated() {
        rotated = pagriezts.isSelected();
        generalActionRequest();
    }

    void drawDoubleField(int i, int j, int m){

        Rectangle fields1 = new Rectangle();
        fields1.setStroke(Color.valueOf(krasa[m]));
        fields1.setFill(Color.LIGHTGRAY);
        fields1.setStrokeWidth(1);
        fields1.setSmooth(true);

        Rectangle fields2 = new Rectangle();
        fields2.setStroke(Color.valueOf(krasa[m]));
        fields2.setFill(Color.LIGHTGRAY);
        fields2.setStrokeWidth(1);
        fields2.setSmooth(true);

        Line fold1 = new Line();
        fold1.setStroke(Color.DARKGRAY);
        fold1.setStrokeWidth(1);
        fold1.setSmooth(true);

        Line fold2 = new Line();
        fold2.setStroke(Color.DARKGRAY);
        fold2.setStrokeWidth(1);
        fold2.setSmooth(true);

        // uzlaižu rāmji
        Rectangle bleeds = new Rectangle();
        bleeds.setStroke(Color.GRAY);
        bleeds.setFill(Color.TRANSPARENT);
        bleeds.setStrokeWidth(1);
        bleeds.setSmooth(false);

        if (izklVeids.equals("Apvērsiens")) {
            double subjectWidth;
            double subjectHeight;
            if (!rotated) {
                subjectWidth = width * 2 * countX + bleed * 2 * (countX - 1);
                subjectHeight = (height * 2 + headSpace) * countY + bleed * 2 * (countY - 1);
                // pirmā puse
                fields1.setX(((750 - subjectWidth) / 2) + width * 2 * i + bleed * 2 * i);
                fields1.setY((550 - paperY) + (paperY - subjectHeight) / 2 +
                        (height * j * 2 + bleed * 2 * j + headSpace * j));
                fields1.setWidth(width * 2);
                fields1.setHeight(height);
                paper.getChildren().add(fields1);

                // otrā puse
                fields2.setX(fields1.getX());
                fields2.setY(fields1.getY() + height + headSpace);
                fields2.setWidth(width * 2);
                fields2.setHeight(height);
                paper.getChildren().add(fields2);

                //locījums pirmai pusei
                fold1.setStartX(fields1.getX() + width);
                fold1.setStartY(fields1.getY());
                fold1.setEndX(fold1.getStartX());
                fold1.setEndY(fold1.getStartY() + height);
                paper.getChildren().add(fold1);

                //locījums otrai pusei
                fold2.setStartX(fold1.getStartX());
                fold2.setStartY(fields2.getY());
                fold2.setEndX(fold1.getStartX());
                fold2.setEndY(fold1.getEndY() + height + headSpace);
                paper.getChildren().add(fold2);

                //blīdu rāmes
                bleeds.setX(fields1.getX() - bleed);
                bleeds.setY(fields1.getY() - bleed);
                bleeds.setWidth(fields1.getWidth() + 2 * bleed);
                bleeds.setHeight(fields1.getHeight() * 2 + 2 * bleed + headSpace);

            } else { // divloce, pagriezta un apvērsienā
                subjectWidth = (height * 2 + headSpace + bleed * 2) * countX - bleed * 2;
                subjectHeight = width * 2 * countY + bleed * 2 * countY - bleed * 2;

                // rāmji pirmā puse
                fields1.setX(((750 - subjectWidth) / 2) + (height * 2 + headSpace) * i + bleed * 2 * i);
                fields1.setY((550 - paperY) + (paperY - subjectHeight) / 2 + // sākuma punkts ir ok
                        ((width + bleed) * j * 2));
                fields1.setWidth(height);
                fields1.setHeight(width * 2);
                paper.getChildren().add(fields1);

                // otrā puse
                fields2.setX(fields1.getX() + height + headSpace);
                fields2.setY(fields1.getY());
                fields2.setWidth(height);
                fields2.setHeight(width * 2);
                paper.getChildren().add(fields2);

                //locījums pirmai pusei
                fold1.setStartX(fields1.getX());
                fold1.setStartY(fields1.getY() + width);
                fold1.setEndX(fields1.getX() + height);
                fold1.setEndY(fields1.getY() + width);
                paper.getChildren().add(fold1);

                //locījums otrai pusei
                fold2.setStartX(fields2.getX());
                fold2.setStartY(fields2.getY() + width);
                fold2.setEndX(fields2.getX() + height);
                fold2.setEndY(fields2.getY() + width);
                paper.getChildren().add(fold2);

                //blīdu rāmes
                bleeds.setX(fields1.getX() - bleed);
                bleeds.setY(fields1.getY() - bleed);
                bleeds.setWidth(fields1.getWidth() * 2 + 2 * bleed + headSpace);
                bleeds.setHeight(fields1.getHeight() + 2 * bleed);
            }
            paper.getChildren().add(bleeds);

            // parasta divloce vai apmetienā
        } else {
            if (!rotated) {
                double subjectWidth = width * 2 * countX + bleed * 2 * (countX - 1);
                double subjectHeight = (height * 2 + headSpace) * countY + bleed * 2 * (countY - 1);
                // pirmā puse
                fields1.setX(((750 - subjectWidth) / 2) + width * 2 * i + bleed * 2 * i);
                fields1.setY((550 - subjectHeight - firstCut) +
                        (height * j * 2 + bleed * 2 * j + headSpace * j));
                fields1.setWidth(width * 2);
                fields1.setHeight(height);
                paper.getChildren().add(fields1);

                // otrā puse
                fields2.setX(fields1.getX());
                fields2.setY(fields1.getY() + height + headSpace);
                fields2.setWidth(width * 2);
                fields2.setHeight(height);
                paper.getChildren().add(fields2);

                //locījums pirmai pusei
                fold1.setStartX(fields1.getX() + width);
                fold1.setStartY(fields1.getY());
                fold1.setEndX(fold1.getStartX());
                fold1.setEndY(fold1.getStartY() + height);
                paper.getChildren().add(fold1);

                //locījums otrai pusei
                fold2.setStartX(fold1.getStartX());
                fold2.setStartY(fields2.getY());
                fold2.setEndX(fold1.getStartX());
                fold2.setEndY(fold1.getEndY() + height + headSpace);
                paper.getChildren().add(fold2);

                //blīdu rāmes
                bleeds.setX(fields1.getX() - bleed);
                bleeds.setY(fields1.getY() - bleed);
                bleeds.setWidth(fields1.getWidth() + 2 * bleed);
                bleeds.setHeight(fields1.getHeight() * 2 + 2 * bleed + headSpace);
                paper.getChildren().add(bleeds);
            } else {
                double subjectWidth = (height * 2 + headSpace + bleed * 2) * countX - bleed * 2;
                double subjectHeight = width * 2 * countY + bleed * 2 * countY- bleed * 2;

                // rāmji pirmā puse
                fields1.setX(((750 - subjectWidth) / 2) + (height * 2 + headSpace) * i + bleed * 2 * i);
                fields1.setY((550 - subjectHeight - firstCut) +
                        (width * j * 2 + bleed * 2 * j));
                fields1.setWidth(height);
                fields1.setHeight(width * 2);
                paper.getChildren().add(fields1);

                // otrā puse
                fields2.setX(fields1.getX() + height + headSpace);
                fields2.setY(fields1.getY());
                fields2.setWidth(height);
                fields2.setHeight(width * 2);
                paper.getChildren().add(fields2);

                //locījums pirmai pusei
                fold1.setStartX(fields1.getX());
                fold1.setStartY(fields1.getY() + width);
                fold1.setEndX(fields1.getX() + height);
                fold1.setEndY(fields1.getY() + width);
                paper.getChildren().add(fold1);

                //locījums otrai pusei
                fold2.setStartX(fields2.getX());
                fold2.setStartY(fields2.getY() + width);
                fold2.setEndX(fields2.getX() + height);
                fold2.setEndY(fields2.getY() + width);
                paper.getChildren().add(fold2);

                //blīdu rāmes
                bleeds.setX(fields1.getX() - bleed);
                bleeds.setY(fields1.getY() - bleed);
                bleeds.setWidth(fields1.getWidth() * 2 + 2 * bleed + headSpace);
                bleeds.setHeight(fields1.getHeight() + 2 * bleed);
                paper.getChildren().add(bleeds);
            }
        }

    }

    public void loksnesRamis(double x, double pl, double aug) {
        Rectangle paperFrame = new Rectangle();
        paperFrame.setX(x);
        paperFrame.setY(550 - aug);
        paperFrame.setWidth(pl);
        paperFrame.setHeight(aug);
        paperFrame.setStroke(Color.BLACK);
        paperFrame.setFill(Color.LIGHTCORAL);
        paperFrame.setStrokeWidth(2);
        paper.getChildren().add(paperFrame);

        Rectangle paperFrame2 = new Rectangle();
        paperFrame2.setX(paperFrame.getX() + sideMargin);
        paperFrame2.setY(paperFrame.getY() + upperMargin);
        paperFrame2.setWidth(paperFrame.getWidth() - 2 * sideMargin);
        paperFrame2.setHeight(paperFrame.getHeight() - firstCut - upperMargin);
        paperFrame2.setStroke(Color.BLACK);
        paperFrame2.setFill(Color.WHITE);
        paperFrame2.setStrokeWidth(0);
        paper.getChildren().add(paperFrame2);
    }

    public void platesRamis() {
        Rectangle plateFrame = new Rectangle();
        plateFrame.setX(0);
        plateFrame.setY(0);
        plateFrame.setWidth(750);
        plateFrame.setHeight(550);
        plateFrame.setStroke(Color.BLACK);
        plateFrame.setFill(Color.GRAY);
        plateFrame.setStrokeWidth(1);
        plateFrame.setArcWidth(0);
        plateFrame.setArcHeight(0);
        paper.getChildren().add(plateFrame);
    }

    public void set640x450(){
        loksneX.setText("640");
        loksneY.setText("450");
        generalActionRequest();
    }

    public void set640x460(){
        loksneX.setText("640");
        loksneY.setText("460");
        generalActionRequest();
    }

    public void set450x320(){
        loksneX.setText("450");
        loksneY.setText("320");
        generalActionRequest();
    }

    public void set460x320(){
        loksneX.setText("460");
        loksneY.setText("320");
        generalActionRequest();
    }

    public void set720x510(){
        loksneX.setText("720");
        loksneY.setText("510");
        generalActionRequest();
    }

    public void set720x520(){
        loksneX.setText("720");
        loksneY.setText("520");
        generalActionRequest();
    }

    public void set320x450(){
        loksneX.setText("320");
        loksneY.setText("450");
        generalActionRequest();
    }
}
package test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.ArrayList;

public class TestFX extends Application {
    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 720, 720);
        scene.getStylesheets().add("style.css");
        ArrayList<Integer> colorslist = genPalette(3,3,2);

        ScrollPane scroll = new ScrollPane();
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        GridPane palette = new GridPane();
        palette.setHgap(0);
        palette.setVgap(0);

        for(int i = 0; i < colorslist.size(); i++) {
            Button button = new Button();
            String color = String.format("%06x", colorslist.get(i));
            button.getStyleClass().add("buttonPalette");
            button.setStyle("-fx-background-color: #" + color);
            button.setOnAction(e -> root.setStyle("-fx-background-color: #" + color));
            palette.add(button, i%32,i/32);
        }

        scroll.setContent(palette);
        root.setCenter(scroll);

        stage.setTitle("PaintFX Test");
        stage.setScene(scene);
        stage.show();
    }

    private ArrayList<Integer> genPalette(int red, int green, int blue) {
        ArrayList<Integer> palette = new ArrayList<>();
        float rf = 1.0f/((1<<red));
        float rfp = 1.0f/((1<<red)-1);
        float gf = 1.0f/((1<<green));
        float gfp = 1.0f/((1<<green)-1);
        float bf = 1.0f/((1<<blue));
        float bfp = 1.0f/((1<<blue)-1);
        int noc = 1<<(red+green+blue);
        for(int i = 0; i < noc; i++) {
            palette.add(((int)(rfp*(i/(int)(noc*rf))*255)<<16) | ((int)(gfp*(int)((int)(i%(1/(bf*gf)))/(1/bf))*255)<<8) | (int)(bfp*(i%(1/bf))*255));
        }

        return palette;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
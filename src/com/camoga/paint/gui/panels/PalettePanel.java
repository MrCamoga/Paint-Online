package com.camoga.paint.gui.panels;

import com.camoga.paint.ServerManager;
import com.camoga.paint.net.packets.Packet04SelectColor;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

public class PalettePanel extends BorderPane {

    ScrollPane scrollPane = new ScrollPane();
    GridPane grid = new GridPane();

    public PalettePanel() {
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setMinWidth(200);
        grid.setHgap(0);
        grid.setVgap(0);

        int[] colors = genPalette(3,2,3);

        for(int i = 0; i < colors.length; i++) {
            Button button = new Button();
            String color = String.format("%06x", colors[i]);
            button.getStyleClass().add("buttonPalette");
            button.setStyle("-fx-background-color: #" + color);
            final int c = i;
            button.setOnAction(e -> {
                ServerManager.currentsc.getCurrentPP().changeSelectedColor(0xff000000|colors[c]);
                Packet04SelectColor colorPacket = new Packet04SelectColor(0xff000000|colors[c]);
                colorPacket.writeData(ServerManager.currentsc.socketClient);
//                root.setStyle("-fx-background-color: #" + color);
            });
            grid.add(button, i%16,i/16);
        }

        scrollPane.setContent(grid);
        setCenter(scrollPane);
    }

    private int[] genPalette(int red, int green, int blue) {
        int noc = 1<<(red+green+blue);
        int[] palette = new int[noc];
        float rf = 1.0f/((1<<red));
        float rfp = 1.0f/((1<<red)-1);
        float gf = 1.0f/((1<<green));
        float gfp = 1.0f/((1<<green)-1);
        float bf = 1.0f/((1<<blue));
        float bfp = 1.0f/((1<<blue)-1);
        for(int i = 0; i < noc; i++) {
            palette[i] = ((int)(rfp*(i/(int)(noc*rf))*255)<<16) | ((int)(gfp*(int)((int)(i%(1/(bf*gf)))/(1/bf))*255)<<8) | ((int)(bfp*(i%(1/bf))*255));
        }
        return palette;
    }
}
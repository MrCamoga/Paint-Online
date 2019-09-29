package com.camoga.paint;

import com.camoga.paint.events.MouseHandler;
import com.camoga.paint.gui.elements.Cursor;
import com.camoga.paint.gui.panels.ChatPanel;
import com.camoga.paint.gui.panels.PaintPanel;
import com.camoga.paint.gui.panels.PalettePanel;
import com.camoga.paint.net.client.ClientSocket;
import com.camoga.paint.net.packets.Packet01Paint;
import com.camoga.paint.net.packets.Packet03PixelArray;
import com.camoga.paint.net.packets.Packet04SelectColor;
import com.camoga.paint.net.packets.Packet07FillBucket;
import javafx.application.Platform;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

//FIXME if client connected to same server from two tabs, he'll draw from the two tabs simultaneously
public class ServerClient extends SplitPane {
    public enum Tool {
        PENCIL(0), BUCKET(1), RUBBER(2), PICKCOLOR(3), BRUSH(4), RECTSEL(5), ELIPSEL(6), COLORSEL(7);

        private int id;

        private Tool(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    public int color = 0xffffffff;
    public int brushSize = 2;
    public Tool tool = Tool.PENCIL;
    public ChatPanel chatPanel;
    public PalettePanel palettePanel;
    private TabPane tabImages = new TabPane();

    public ClientSocket socketClient;

    private ArrayList<PaintPanel> paintpanels = new ArrayList<PaintPanel>();
    private ArrayList<ClientMP> connectedClients = new ArrayList<ClientMP>();
    public HashMap<String, Cursor> cursors = new HashMap<String, Cursor>();

    public ServerClient(ClientSocket socket) {
        socketClient = socket;
        chatPanel = new ChatPanel();
        palettePanel = new PalettePanel();
        tabImages.selectionModelProperty().addListener(c -> {
            paintpanels.forEach(pp -> pp.timer.stop());
            paintpanels.get(tabImages.getSelectionModel().getSelectedIndex()).timer.start();
        });
        tabImages.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);



        getItems().add(palettePanel);
        getItems().add(tabImages);
        getItems().add(chatPanel);
    }

    public void init(int width, int height, int UUID) {
        PaintPanel pp = new PaintPanel(this, width, height, UUID);

        paintpanels.add(pp);

        for(int i = 0; i < pp.image.getPixels().length; i++) {
            pp.image.setPixel(i, 0xffffff);
        }
        Platform.runLater(() -> tabImages.getTabs().add(new Tab(UUID + "", pp)));
    }

    public void tick() {
        // TODO different right - left click actions
        if(MouseHandler.pressed) {
            Integer UUID = getCurrentImageUUID();
            if(UUID == null) return;
            Image image = getCurrentImage();
            int WIDTH = image.width;
            int HEIGHT = image.height;
            int ys = MouseHandler.y / getCurrentPP().scale;
            int xs = MouseHandler.x / getCurrentPP().scale;
            System.out.println(xs + "," + ys);
            if((xs < 0) || (ys < 0) || (xs >= WIDTH) || (ys >= HEIGHT))
                return;
            if((ys != MouseHandler.ly) || (xs != MouseHandler.lx)) {
                switch(tool) {
                    case PENCIL:
                        Packet01Paint paintPacket = new Packet01Paint(xs, ys, color, brushSize, UUID, tool.getId());
                        paintPacket.writeData(socketClient);
                        pencil(xs, ys, brushSize, color, UUID);
                        break;
                    case BUCKET:
                        int target = image.getPixel(xs, ys);
                        if(target == color)
                            return;
                        System.out.println("fillbucket");
                        Packet07FillBucket bucketPacket = new Packet07FillBucket(xs, ys, color, tool.getId(), UUID);
                        bucketPacket.writeData(socketClient);
                        floodFill(xs, ys, target, color, UUID);
                        break;
                    case RUBBER:
                        Packet01Paint rubberPacket = new Packet01Paint(xs, ys, 0x00, brushSize, UUID, tool.getId());
                        rubberPacket.writeData(socketClient);
                        pencil(xs, ys, brushSize, 0x00, UUID);
                        break;
                    case PICKCOLOR:
                        color = image.getPixel(xs, ys);
                        Packet04SelectColor packet = new Packet04SelectColor(color);
                        packet.writeData(socketClient);
                        break;
                    case BRUSH:
                        break;
                    case RECTSEL:
                        break;
                    case ELIPSEL:
                        break;
                    case COLORSEL:
                        break;
                }

            }
        }
    }

    public void pencil(int xp, int yp, int size, int color, int UUID) {
        Image image = getImage(UUID);
        if(image == null) return;
        int WIDTH = image.width;
        int HEIGHT = image.height;
        for(int y = 0; y < size; y++) {
            int ya = y + yp - size / 2;
            for(int x = 0; x < size; x++) {
                int xa = x + xp - size / 2;
                if(xa < 0 || ya < 0 || xa >= WIDTH || ya >= HEIGHT) continue;
                else if(Math.abs((xa - xp) * (xa - xp) + (ya - yp) * (ya - yp)) < size * size / 4 + 2) {
                    if(image.getPixel(xa, ya) != color) {
                        image.setPixel(xa, ya, color);
                    }
                }
            }
        }
    }

    // TODO change floodFill algorithm

    private ArrayList<Point> queue = new ArrayList<Point>();

    public void floodFill(int x, int y, int targetColor, int color, int UUID) {
        Image image = getImage(UUID);
        int WIDTH = image.width;
        int HEIGHT = image.height;
        if(queue.size() > 0) queue.remove(0);
        while(y > 0 && image.getPixel(x, y - 1) == targetColor) {
            y--;
        }
        boolean left = false, right = false;
        while(y < HEIGHT && image.getPixel(x, y) == targetColor) {
            image.setPixel(x, y, color);

            if(!left && x > 0 && image.getPixel(x - 1, y) == targetColor) {
                int ytemp = y;
                while(ytemp > 0 && image.getPixel(x - 1, ytemp - 1) == targetColor) {
                    ytemp--;
                }
                queue.add(new Point(x - 1, ytemp));
                left = true;
            } else if(left && x > 0 && image.getPixel(x - 1, y) != targetColor) left = false;
            if(!right && x < WIDTH - 1 && image.getPixel(x + 1, y) == targetColor) {
                int ytemp = y;
                while(ytemp > 0 && image.getPixel(x + 1, ytemp - 1) == targetColor) {
                    ytemp--;
                }
                queue.add(new Point(x + 1, ytemp));
                right = true;
            } else if(right && x < WIDTH - 1 && image.getPixel(x + 1, y) != targetColor) right = false;

            y++;
        }
        if(queue.size() > 0)
            floodFill(queue.get(0).x, queue.get(0).y, targetColor, color, UUID);
    }

    public void imagepacket(int[] pixels, int num, int UUID) {
        for(int i = 0; i < pixels.length; i++) {
            getImage(UUID).setPixel(num * Packet03PixelArray.packetsize + i, pixels[i]);
        }
    }

    public void removeImage(int UUID) {
        //DONE Paint freezes for 15s after deleting image
        PaintPanel pp = getPP(UUID);
        System.out.println("Remove PaintPanel " + pp.image.UUID);
        pp.timer.stop();

        int i = 0;
        for(PaintPanel p : paintpanels) {
            if(p.image.UUID == UUID) break;
            i++;
        }
        tabImages.getTabs().remove(tabImages.getSelectionModel().getSelectedIndex());
        paintpanels.remove(pp);

    }

    // TODO draw line
    public void drawLine(int x0, int y0, int xf, int yf, int size, int color, int imageid) {
        for(int x = x0; x < xf; x++) {
            int y = Math.round(y0 + (yf - y0) / (xf - x0) * x);
            pencil(x, y, size, color, imageid);
        }
    }

    public void addClient(ClientMP c) {
        connectedClients.add(c);

        chatPanel.modifyList(c.getUsername(), -1);
        chatPanel.addText(c.getUsername() + " has joined...");

        addCursor(c);
    }

    public void removeClient(String username) {
        chatPanel.modifyList(username, Utils.getClientMPIndex(username, connectedClients));
        connectedClients.remove(Utils.getClientMPIndex(username, connectedClients));

        chatPanel.addText(username + " has disconnected...");

        removeCursor(username);
    }

    private void addCursor(ClientMP c) {
        cursors.put(c.getUsername(), new Cursor(c));
        System.out.println(c.getUsername() + " cursor added");
    }

    public void updateCursor(String username, int x, int y, int tool, int UUID) {
        cursors.get(username).update(x, y, tool, UUID);
    }

    private void removeCursor(String username) {
        cursors.remove(username);
        System.out.println(username + " cursor removed");
    }

    public PaintPanel getCurrentPP() {
        return paintpanels.get(tabImages.getSelectionModel().getSelectedIndex());
    }

    private PaintPanel getPP(int uuid) {
        for(PaintPanel pp : paintpanels)
            if(pp.image.UUID == uuid) return pp;
        return null;
    }

    public Integer getCurrentImageUUID() {
        if(tabImages.getSelectionModel().getSelectedIndex() < 0) return null;
        return paintpanels.get(tabImages.getSelectionModel().getSelectedIndex()).image.UUID;
    }

    public Image getImage(Integer uuid) {
        if(uuid == null) return null;
        PaintPanel pp = getPP(uuid);
        return pp != null ? pp.image : null;
    }

    public Image getCurrentImage() {
        return getImage(getCurrentImageUUID());
    }

    public void disconnect() {
        paintpanels.clear();
    }
}
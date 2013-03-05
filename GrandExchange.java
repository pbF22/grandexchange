package grandexchange;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import org.powerbot.core.event.events.MessageEvent;
import org.powerbot.core.event.listeners.MessageListener;
import org.powerbot.core.event.listeners.PaintListener;
import org.powerbot.core.script.ActiveScript;
import org.powerbot.core.script.job.Task;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.input.Mouse;

@Manifest(version = 1.0, authors = {"F22"}, description = "Grand Exchange class.", name = "F22 Grand Exchange")
public class GrandExchange extends ActiveScript implements PaintListener, MessageListener {

    RenderingHints antialiasing = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    Stroke stroke1 = new BasicStroke(1);
    static int currentCash = 5000000;
    static int goldCollected;
    static int goldSpent;

    public void checkCash() {
        Widgets.get(548, 40).interact("Examine money pouch");
    }

    @Override
    public void onStart() {
    }

    @Override
    public int loop() {
        GEMain.randomEmptySlot().createSellOffer("Swordfish", 1, -1);
        return 1000;
    }

    @Override
    public void onRepaint(Graphics g1) {
        Graphics2D g = (Graphics2D) g1;
        g.setRenderingHints(antialiasing);

        g.setStroke(stroke1);
        g.setColor(Color.WHITE);
        g.drawLine(Mouse.getX(), Mouse.getY() - 5, Mouse.getX(), Mouse.getY() + 5);
        g.drawLine(Mouse.getX() - 5, Mouse.getY(), Mouse.getX() + 5, Mouse.getY());
    }

    @Override
    public void messageReceived(MessageEvent me) {

        if (me.getMessage().contains("Your money pouch")) {
            String temp = me.getMessage().substring(me.getMessage().indexOf("contains ") + 9);
            temp = temp.substring(0, temp.indexOf(" coins"));
            currentCash = Integer.parseInt(temp.replace(",", ""));
        }
    }
}
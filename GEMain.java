package grandexchange;

import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.util.Random;

public class GEMain {

    static GESlot SLOT_1 = new GESlot(0);
    static GESlot SLOT_2 = new GESlot(1);
    static GESlot SLOT_3 = new GESlot(2);
    static GESlot SLOT_4 = new GESlot(3);
    static GESlot SLOT_5 = new GESlot(4);
    static GESlot SLOT_6 = new GESlot(5);
    private static GESlot slots[] = {SLOT_1, SLOT_2, SLOT_3, SLOT_4, SLOT_5, SLOT_6};

    public static GESlot randomUsedSlot() {
        if (emptySlotAmount() < 6) {
            int x = Random.nextInt(0, 5);
            while (slots[x].isEmpty()) {
                x = Random.nextInt(0, 5);
            }
            return slots[x];
        }
        return null;
    }

    public static GESlot randomEmptySlot() {
        if (emptySlotAmount() > 0) {
            int x = Random.nextInt(0, 5);
            if (slots[x].isEmpty()) {
                return slots[x];
            } else {
                randomEmptySlot();
            }
        }
        return null;
    }

    public static GESlot randomSlot() {
        return slots[Random.nextInt(0, 5)];
    }

    public static int emptySlotAmount() {
        int x = 0;
        for (int i = 0; i < 6; i++) {
            if (slots[i].isEmpty()) {
                x++;
            }
        }
        return x;
    }

    public static boolean isMainOffersOpen() {
        return Widgets.get(105, 18).visible();
    }

    public static boolean isCollectScreenOpen() {
        return Widgets.get(105, 198).visible();
    }

    public static boolean isOfferSettingsOpen() {
        return Widgets.get(105, 134).visible() && Widgets.get(105, 187).visible();
    }
}
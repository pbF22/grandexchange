package grandexchange;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import org.powerbot.core.script.job.Task;
import org.powerbot.core.script.util.Random;
import org.powerbot.core.script.util.Timer;
import org.powerbot.game.api.methods.Tabs;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.input.Keyboard;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.wrappers.widget.WidgetChild;

public class GESettings {

    static WidgetChild ADD_ONE = Widgets.get(105, 160);
    static WidgetChild ADD_TEN = Widgets.get(105, 162);
    static WidgetChild ADD_HUNDRED = Widgets.get(105, 164);
    static WidgetChild ADD_THOUSAND = Widgets.get(105, 166);
    static WidgetChild DECREASE_QUANTITY = Widgets.get(105, 155);
    static WidgetChild INCREASE_QUANTITY = Widgets.get(105, 157);
    static WidgetChild GUIDE_PRICE = Widgets.get(105, 175);
    static WidgetChild DECREASE_PRICE = Widgets.get(105, 169);
    static WidgetChild INCREASE_PRICE = Widgets.get(105, 171);
    static WidgetChild DECREASE_5_PERCENT = Widgets.get(105, 179);
    static WidgetChild INCREASE_5_PERCENT = Widgets.get(105, 181);

    public static void searchItem(String item) {
        if (GEMain.isOfferSettingsOpen() && !Widgets.get(389, 9).getText().equals(item.toLowerCase() + "*")) {
            if (Widgets.get(389, 1).visible()) {
                while (!Widgets.get(389, 9).getText().equals("*") && Widgets.get(389, 9).visible()) {
                    Keyboard.sendKey((char) KeyEvent.VK_BACK_SPACE);
                    Task.sleep(60, 70);
                }
                Task.sleep(350, 750);
                Keyboard.sendText(item.toLowerCase(), false);
                Timer t = new Timer(3000);
                while (Widgets.get(389, 4).getChildren().length < 1 && t.isRunning()) {
                    Task.sleep(750, 1500);
                }
            } else {
                Widgets.get(105, 190).interact("Choose item");
                Task.sleep(250, 500);
                searchItem(item);
            }
        }
    }

    public static void selectSearchItem(String item) {
        WidgetChild searchBox = Widgets.get(389, 4);
        if (GEMain.isOfferSettingsOpen() && searchBox.getChildren().length > 0) {
            int itemIndex = -1;
            for (int i = 0; i < searchBox.getChildren().length; i++) {
                if (searchBox.getChild(i).getText().toLowerCase().equals(item.toLowerCase())) {
                    itemIndex = i;
                    break;
                }
            }
            scrollToIndex(itemIndex);
            if (Widgets.get(389, 4).getChild(itemIndex).getAbsoluteLocation().getY() <= 486) {
                clickInBounds(Widgets.get(389, 4).getChild(itemIndex).getBoundingRectangle(), true);
            }
            Timer t = new Timer(Random.nextInt(2800, 3600));
            while (!Widgets.get(105, 142).getText().equals(item) && t.isRunning()) {
                Task.sleep(50, 100);
            }
        }
    }

    public static void selectInventoryItem(int itemID) {
        if (itemID != -1 && Inventory.getCount(itemID) > 0) {
            if (!Tabs.INVENTORY.isOpen()) {
                Tabs.INVENTORY.open();
            }
            Inventory.getItem(itemID).getWidgetChild().click(true);
            Timer t = new Timer(Random.nextInt(2800, 3400));
            while (Widgets.get(105, 139).getChildId() != itemID && Widgets.get(105, 139).getChildId() != itemID - 1 && t.isRunning()) {
                Task.sleep(50, 100);
            }
            if (Widgets.get(105, 139).getChildId() != itemID && Widgets.get(105, 139).getChildId() != itemID - 1) {
                selectInventoryItem(itemID);
            }
        } else {
            goToMainOffers();
        }
    }

    public static void selectInventoryItem(String itemName) {
        selectInventoryItem(findID(itemName));
    }

    private static int findID(String itemName) {
        for (int i = 0; i < 28; i++) {
            if (Inventory.getItemAt(i) != null && Inventory.getItemAt(i).getName().equals(itemName)) {
                return Inventory.getItemAt(i).getId();
            }
        }
        return -1;
    }

    public static void setOfferSettings(String itemName, int amount, int price, boolean buy) {
        while (!readyForConfirm(itemName, amount, price) && GEMain.isOfferSettingsOpen()) {
            try {
                if (!getCurrentItem().equals(itemName)) {
                    if (buy) {
                        searchItem(itemName);
                        selectSearchItem(itemName);
                    } else {
                        selectInventoryItem(itemName);
                    }
                } else if (getCurrentAmount() != amount) {
                    setAmount(amount);
                } else if (getCurrentPrice() != price && price != -1) {
                    setPrice(price);
                }
            } catch (Exception e) {
            }
            Task.sleep(250);
        }
    }

    public static boolean addAmount(int a) {
        if (getCurrentAmount() == 0) {
            int times[] = {(a % 10000 - a % 1000) / 1000,
                (a % 1000 - a % 100) / 100,
                (a % 100 - a % 10) / 10,
                a % 10};
            if (times[0] + times[1] + times[2] + times[3] <= 8) {
                WidgetChild btns[] = {ADD_THOUSAND, ADD_HUNDRED, ADD_TEN, ADD_ONE};
                for (int i = 0; i < btns.length; i++) {
                    clickButton(btns[i], times[i]);
                }
                return true;
            }
        }
        return false;
    }

    public static void clickButton(WidgetChild button, int times) {
        for (int i = 0; i < times; i++) {
            clickInBounds(button.getBoundingRectangle(), true);
            Task.sleep(12, 20);
        }
    }

    public static void setPrice(int a) {
        if (getCurrentPrice() != a) {
            setNumber(177, "Edit Price", a);
            Timer t = new Timer(Random.nextInt(2800, 3400));
            while (getCurrentPrice() != a) {
                Task.sleep(50, 100);
            }
        }
    }

    public static void setAmount(int a) {
        if (getCurrentAmount() != a) {
            if (!addAmount(a)) {
                setNumber(168, "Edit Quantity", a);
            }
            Timer t = new Timer(Random.nextInt(2800, 3400));
            while (getCurrentAmount() != a) {
                Task.sleep(50, 100);
            }
        }
    }

    private static void setNumber(int widget, String text, int a) {
        Widgets.get(105, widget).interact(text);
        Timer t = new Timer(Random.nextInt(2800, 3600));
        while (!Widgets.get(752, 4).visible() && t.isRunning()) {
            Task.sleep(50, 100);
        }
        if (Widgets.get(752, 4).visible()) {
            Task.sleep(500, 750);
            Keyboard.sendText("" + a, true);
        } else {
            setNumber(widget, text, a);
        }
    }

    public static void confirmOffer() {
        String offerType = Widgets.get(105, 134).getText();
        if (GEMain.isOfferSettingsOpen() && offerType.equals("Buy Offer")) {
            if (!Widgets.get(449, 25).visible()) {
                Widgets.get(105, 190).interact("Choose item");
                confirmOffer();
                return;
            }
        }
        if (offerType.equals("Sell Offer") || Widgets.get(449, 25).getTextColor() != 16711680) {
            Widgets.get(105, 187).interact("Confirm Offer");
            Task.sleep(750, 1500);
            Timer t = new Timer(Random.nextInt(2800, 3600));
            while (!GEMain.isMainOffersOpen() && t.isRunning()) {
                Task.sleep(50, 100);
            }
            if (!GEMain.isMainOffersOpen()) {
                confirmOffer();
            }
        } else {
            Task.sleep(750, 1750);
            goToMainOffers();
        }
    }

    public static void goToMainOffers() {
        if (Widgets.get(105, 128).visible()) {
            Widgets.get(105, 128).interact("Back");
            Timer t = new Timer(Random.nextInt(2400, 3200));
            while (!GEMain.isMainOffersOpen() && t.isRunning()) {
                Task.sleep(100, 250);
            }
            if (!GEMain.isMainOffersOpen()) {
                goToMainOffers();
            }
        }
    }

    public static String getCurrentItem() {
        return Widgets.get(105, 142).getText();
    }

    public static int getCurrentPrice() {
        return Integer.parseInt(Widgets.get(105, 153).getText().replace("gp", "").replace(",", "").replace(" ", ""));
    }

    public static int getCurrentAmount() {
        return Integer.parseInt(Widgets.get(105, 148).getText().replace(",", ""));
    }

    public static boolean readyForConfirm(String name, int amount, int price) {
        return getCurrentAmount() == amount && (getCurrentPrice() == price || price == -1) && getCurrentItem().equals(name) && GEMain.isOfferSettingsOpen();
    }

    public static void scrollToIndex(int i) {
        if (i >= 8) {
            while (Widgets.get(389, 4).getChild(i).getAbsoluteLocation().getY() >= Random.nextInt(482, 486)) {
                if (!Widgets.get(389, 8).getChild(5).getBoundingRectangle().contains(Mouse.getLocation())) {
                    Mouse.move((int) Widgets.get(389, 8).getChild(5).getAbsoluteLocation().getX() + Random.nextInt(4, 12),
                            (int) Widgets.get(389, 8).getChild(5).getAbsoluteLocation().getY() + Random.nextInt(4, 12));
                }
                Mouse.hold(100, true);
            }
        }
    }

    private static void clickInBounds(Rectangle rect, boolean left) {
        int x = (int) rect.getLocation().getX() + Random.nextInt(3, rect.width - 3);
        int y = (int) rect.getLocation().getY() + Random.nextInt(3, rect.height - 3);
        if (rect.contains(Mouse.getLocation())) {
            Mouse.click(left);
        } else {
            Mouse.click(x, y, left);
        }
    }
}
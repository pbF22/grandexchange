package grandexchange;

import org.powerbot.core.script.job.Task;
import org.powerbot.core.script.util.Random;
import org.powerbot.core.script.util.Timer;
import org.powerbot.game.api.methods.Widgets;

class GESlot {

    private int slot;

    public GESlot(int slot) {
        this.slot = slot;
    }

    public void createBuyOffer(String itemName, int amount, int price) {
        if (isEmpty()) {
            openNewOffer(true);
            GESettings.setOfferSettings(itemName, amount, price, true);
            if (GESettings.readyForConfirm(itemName, amount, price)) {
                GESettings.confirmOffer();
            } else {
                createSellOffer(itemName, amount, price);
            }
        }
    }

    public void createSellOffer(String itemName, int amount, int price) {
        if (isEmpty()) {
            openNewOffer(false);
            GESettings.setOfferSettings(itemName, amount, price, false);
            if (GESettings.readyForConfirm(itemName, amount, price)) {
                GESettings.confirmOffer();
            }
        }
    }

    public void openNewOffer(boolean buy) {
        GESettings.goToMainOffers();
        if (isEmpty()) {
            Widgets.get(105, convertSlot(slot, buy ? 31 : 32)).interact("Make " + (buy ? "Buy" : "Sell") + " Offer");
            Timer t = new Timer(Random.nextInt(2800, 3600));
            while (!GEMain.isOfferSettingsOpen() && t.isRunning()) {
                Task.sleep(50, 100);
            }
            if (!GEMain.isOfferSettingsOpen()) {
                openNewOffer(buy);
            }
        }
    }

    public void collectOffer() {
        GESettings.goToMainOffers();
        if (Widgets.get(105, convertSlot(slot, 19)).getChildren().length > 11) {
            Widgets.get(105, convertSlot(slot, 19)).interact("View Offer");
            Timer t = new Timer(Random.nextInt(2200, 3600));
            while (!GEMain.isCollectScreenOpen() && t.isRunning()) {
                Task.sleep(50, 100);
            }
            if (GEMain.isCollectScreenOpen()) {
                for (int i = 206; i <= 208; i += 2) {
                    if (Widgets.get(105, i).getChildStackSize() > 0) {
                        Widgets.get(105, i).interact(Widgets.get(105, i).getActions()[0]);
                        Task.sleep(200, 600);
                    }
                }
                GESettings.goToMainOffers();
            } else {
                collectOffer();
            }
        }
    }

    public void abortOffer() {
        GESettings.goToMainOffers();
        if (Widgets.get(105, convertSlot(slot, 19)).getChildren().length > 11
                && getCompletionPercent() < 100) {
            Widgets.get(105, convertSlot(slot, 19)).interact("Abort Offer");
            Timer t = new Timer(Random.nextInt(2200, 3600));
            while (getCompletionPercent() != 100) {
                Task.sleep(100, 150);
            }
            if (getCompletionPercent() != 100) {
                abortOffer();
            }
        }
    }

    public boolean isCompleted() {
        return getCompletionPercent() == 100;
    }

    public boolean isEmpty() {
        return Widgets.get(105, convertSlot(slot, 19)).getChild(10).getText().equals("Empty");
    }

    public boolean isSellOffer() {
        return Widgets.get(105, convertSlot(slot, 19)).getChild(10).getText().equals("Sell");
    }

    public boolean isBuyOffer() {
        return Widgets.get(105, convertSlot(slot, 19)).getChild(10).getText().equals("Buy");
    }

    public int getSlot() {
        return slot;
    }

    public String getItemName() {
        try {
            return Widgets.get(105, convertSlot(slot, 19)).getChild(18).getText();
        } catch (Exception e) {
            return null;
        }
    }

    public int getItemStackSize() {
        try {
            return Widgets.get(105, convertSlot(slot, 17)).getChild(19).getChildStackSize();
        } catch (Exception e) {
            return -1;
        }
    }

    public int getItemPrice() {
        try {
            return Integer.parseInt(Widgets.get(105, convertSlot(slot, 19)).getChild(19).getText().replace("gp", "").replace(",", "").replace(" ", ""));
        } catch (Exception e) {
            return -1;
        }
    }

    public int getCompletionPercent() {
        try {
            return (Widgets.get(105, convertSlot(slot, 19)).getChild(13).getHorizontalScrollThumbSize() * 100) / 124;
        } catch (Exception e) {
            return -1;
        }
    }

    private int convertSlot(int slot, int margin) {
        return (slot * 16) + margin + (slot >= 3 ? 3 * (slot - 2) : 0);
    }
}
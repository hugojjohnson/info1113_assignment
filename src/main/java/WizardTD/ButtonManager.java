package WizardTD;

import processing.core.PApplet;
import java.util.ArrayList;

public class ButtonManager {
    private App app;
    public ArrayList<Button> buttons = new ArrayList<Button>();

    public ButtonManager(App app) {
        this.app = app;
    }

    public void tick() {
        // System.out.println(app.mouseX);
        Button highlightedButton = mouseToButton(app.mouseX, app.mouseY);

        for (Button button : buttons) {
            if (button == highlightedButton) {
                button.highlighted = true;
            } else {
                button.highlighted = false;
            }
        }

        // I apologise.
        if (Enemy.speedMultiplier != 1) { buttons.get(0).active = true; } else { buttons.get(0).active = false; }
        if (app.paused) { buttons.get(1).active = true; } else { buttons.get(1).active = false; }
        if (app.placingTowers) { buttons.get(2).active = true; } else { buttons.get(2).active = false; }
        if (app.upgrade_range) { buttons.get(3).active = true; } else { buttons.get(3).active = false; }
        if (app.upgrade_speed) { buttons.get(4).active = true; } else { buttons.get(4).active = false; }
        if (app.upgrade_damage) { buttons.get(5).active = true; } else { buttons.get(5).active = false; }
    }

    // Returns the button that the mouse is over. If none, it returns null.
    public Button mouseToButton (int mouseX, int mouseY) {
        for (Button button : buttons) {
            boolean xInRange = mouseX > button.x && mouseX < button.x + button.DIAMETER;
            boolean yInRange = mouseY > button.y && mouseY < button.y + button.DIAMETER;
            if (xInRange && yInRange) {
                return button;
            } 
        }
        return null;
    }

}

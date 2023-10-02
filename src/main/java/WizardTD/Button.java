
package WizardTD;

import processing.core.PApplet;

public class Button extends GameObject {
    public final static int DIAMETER = 40;
    public String label;
    private String text;
    public boolean highlighted = false;
    public boolean active = false;

    public Button (int x, int y, String label, String text) {
        super(x, y);
        this.label = label;
        this.text = text;

    }

    @Override
    public void draw(App app) {
        // Text
        app.textSize(11);
        app.text(text, x+45, y+10);

        if (this.label == "M") {
            app.text(text + app.mana_pool_spell_initial_cost, x+45, y+10);
        } else {
            app.text(text, x+45, y+10);
        }

        // Rectangle
        if (highlighted) {
            app.fill(238, 232, 170);
        } else if (active) {
            app.fill(195, 176, 145);
        } else {
            app.fill(0, 0, 0, 0);
        }
        app.stroke(0, 0, 0);
        app.strokeWeight(3);
        app.rect(x, y, DIAMETER, DIAMETER, 3);

        // Label
        app.textSize(25);
        app.fill(0, 0, 0);

        app.text(label, x+5, y+30);
    }

    public void tick() {
        

    }

}
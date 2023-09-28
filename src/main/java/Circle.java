package WizardTD;
import WizardTD.App;
import WizardTD.GameObject;
import processing.core.PImage;


public class Circle extends GameObject {

    // ========== Variables ==========
    private int x_direction = 0;
    private int y_direction = 0;

    public static final int SECONDS_BETWEEN_MOVES = 1;
    private int timer;

    public Circle(int x, int y) {
        super(x, y);
        this.timer = 0;
    }

    public void tick() {
        // Increments the timer
        this.timer++;

        // If more frames have passed than the number of seconds x the framerate
        // the circle jumps 30 pixels to the left
        if (this.timer > SECONDS_BETWEEN_MOVES * App.FPS) {
            this.x += x_direction;
            this.y += y_direction;
            // The timer is reset to 0
            this.timer = 0;
        }
    }
    public void keyPressed(int keyCode) {
                // Left: 37
        // Up: 38
        // Right: 39
        // Down: 40
        if (keyCode == 37) {
            this.x_direction = -30;
        } else if (keyCode == 38) {
            this.y_direction = -30;
        } else if (keyCode == 39) {
            this.x_direction = 30;
        } else if (keyCode == 40) {
            this.y_direction = 30;
        }
    }

    public void keyReleased(int keyCode) {
                // Left: 37
        // Up: 38
        // Right: 39
        // Down: 40
        if (keyCode == 37 || keyCode == 39) {
            this.x_direction = 0;
        } else if (keyCode == 38 || keyCode == 40) {
            this.y_direction = 0;
        }
    }

}

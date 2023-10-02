package WizardTD;

import processing.core.PImage;
import processing.core.PApplet;

/**
 * Represents a game object.
 */
public abstract class GameObject {

    protected int x;
    protected int y;

    private PImage sprite;

    public static App app;

    /**
     * I'm not sure what this does but will leave it here until further notice
     * 
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     */
    public GameObject(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * 
     * @param sprite The new sprite to use.
     */
    public void setSprite(PImage sprite) {
        this.sprite = sprite;
    }
    public PImage getSprite() {
        return this.sprite;
    }

    /**
     * Updates the shape every frame.
     */
    public abstract void tick();

    /**
     * Draws the shape to the screen.
     * 
     * @param app The window to draw onto.
     */
    public void draw(App app) {
        // The image() method is used to draw PImages onto the screen.
        // The first argument is the image, the second and third arguments are coordinates
        app.image(this.sprite, this.x, this.y);
    }

    /**
     * Gets the x-coordinate.
     * @return The x-coordinate.
     */
    public int getX() {
        return this.x;
    }

    /**
     * Returns the y-coordinate.
     * @return The y-coordinate.
     */
    public int getY() {
        return this.y;
    }

    public void set_position(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

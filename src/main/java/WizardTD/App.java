package WizardTD;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.MouseEvent;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import java.io.*;
import java.util.*;

public class App extends PApplet {

    public static final int CELLSIZE = 32;
    public static final int WIZARDTOWERSIZE = 48;
    public static final int SIDEBAR = 120;
    public static final int TOPBAR = 40;
    public static final int BOARD_WIDTH = 20;

    public static int WIDTH = CELLSIZE*BOARD_WIDTH+SIDEBAR;
    public static int HEIGHT = BOARD_WIDTH*CELLSIZE+TOPBAR;

    public static final int FPS = 60;
    public String configPath;
    public Random random = new Random();


    // ========== Variables ==========
    public static String[][] map = new String[BOARD_WIDTH][BOARD_WIDTH];
    public Board_Piece piece;
    public Enemy enemy;
    public PImage path0, path1, path2, path3, gremlin;
    public PImage grass, shrub, beetle, fireball, gremlin1, gremlin2, gremlin3, gremlin4, gremlin5;
    public PImage tower0, tower1, tower2, wizard_house, worm;

    public PImage boardImg;

    public Coordinates wizardCoordinates;


    // ========== Methods ==========
    public App() {
        this.configPath = "config.json";
    }

	@Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Load all resources such as images. Initialise the elements such as the player, enemies and map elements.
     */
	@Override
    public void setup() {
        frameRate(FPS);
        load_images();

        try {
            setupBoard();
        } catch (FileNotFoundException e) {
            System.out.println(e);
            return;
        }

        Enemy.generateAllPaths();
        enemy = new Enemy(0, 0);
        enemy.setSprite(gremlin);
        enemy.draw(this);
    }

    public void load_images() {
        path0 = this.loadImage("src/main/resources/WizardTD/path0.png");
        path1 = this.loadImage("src/main/resources/WizardTD/path1.png");
        path2 = this.loadImage("src/main/resources/WizardTD/path2.png");
        path3 = this.loadImage("src/main/resources/WizardTD/path3.png");
        gremlin = this.loadImage("src/main/resources/WizardTD/gremlin.png");


        grass = this.loadImage("src/main/resources/WizardTD/grass.png");
        shrub = this.loadImage("src/main/resources/WizardTD/shrub.png");
        beetle = this.loadImage("src/main/resources/WizardTD/beetle.png");
        fireball = this.loadImage("src/main/resources/WizardTD/fireball.png");
        gremlin1 = this.loadImage("src/main/resources/WizardTD/gremlin1.png");
        gremlin2 = this.loadImage("src/main/resources/WizardTD/gremlin2.png");
        gremlin3 = this.loadImage("src/main/resources/WizardTD/gremlin3.png");
        gremlin4 = this.loadImage("src/main/resources/WizardTD/gremlin4.png");
        gremlin5 = this.loadImage("src/main/resources/WizardTD/gremlin5.png");

        tower0 = this.loadImage("src/main/resources/WizardTD/towe0.png");
        tower1 = this.loadImage("src/main/resources/WizardTD/tower1.png");
        tower2 = this.loadImage("src/main/resources/WizardTD/tower2.png");
        wizard_house = this.loadImage("src/main/resources/WizardTD/wizard_house.png");
        worm = this.loadImage("src/main/resources/WizardTD/worm.png");
    }
    
    public void setupBoard() throws FileNotFoundException {
        /**  Drawing the board was split into two stages to improve performance.
        This first stage reads in the map, and constructs the board with all of the correct paths in the correct
        orientations. It stitches each piece together in the PImage called boardImg.
        That way, none of the calculations have to be done again and again in the draw function.
        **/
        Scanner input = new Scanner(new File("level1.txt"));
        for (int i = 0; i < BOARD_WIDTH; i++) {
            map[i] = input.nextLine().split("");
        }
        // https://processing.org/reference/createImage_.html
        boardImg = createImage(CELLSIZE * (BOARD_WIDTH+1), CELLSIZE * (BOARD_WIDTH+1) + 30, ARGB);
        
        piece = new Board_Piece(0, 0);

        for (int i = 0; i < BOARD_WIDTH; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                piece.set_position(j*CELLSIZE, i*CELLSIZE + TOPBAR);
                if (map[i][j].equals(" ")) {
                    piece.setSprite(grass);
                } else  if (map[i][j].equals("S")) {
                    piece.setSprite(shrub);
                } else  if (map[i][j].equals("X")) {
                    piece.setSprite(find_piece(map, i, j));
                } else  if (map[i][j].equals("W")) {
                    wizardCoordinates = new Coordinates(i, j);
                }
                boardImg.copy(piece.getSprite(), 0, 0, CELLSIZE, CELLSIZE, j*CELLSIZE, i*CELLSIZE + TOPBAR, CELLSIZE, CELLSIZE);
            }
        }
    }

    public void drawBoard() {
        piece.setSprite(boardImg);
        piece.set_position(0, 0);
        piece.draw(this);

        // The wizard house is drawn on separately because it has a transparent background, and 
        // image.copy does not work with backgrounds like those, so it was easier to just draw it.
        piece.setSprite(wizard_house);
        piece.set_position(wizardCoordinates.getX() * CELLSIZE, wizardCoordinates.getY() * CELLSIZE + TOPBAR);
        piece.draw(this);
    }



    PImage find_piece(String[][] map, int x, int y) {
        /**
        The variable 'count' is used to combine the  number and orientation of paths into a single number.
        Top is 8, right is 4, bottom is 2, left is 1.
        For example, if count is 13 (= 8 + 4 + 1) there is a top, right and left path leading off it.
        **/
        int count = 0;

        // Top
        if (y == 0) {
            count += 8;
        } else if (map[x][y-1].equals("X")){
            count += 8;
        }

        // Right
        if (x == BOARD_WIDTH-1) {
            count += 4;
        } else if (map[x+1][y].equals("X")){
            count += 4;
        }

        // Bottom
        if (y == BOARD_WIDTH-1) {
            count += 2;
        } else if (map[x][y+1].equals("X")){
            count += 2;
        }

        // Left
        if (x == 0) {
            count += 1;
        } else if (map[x-1][y].equals("X")){
            count += 1;
        }

        // Hard coding problems require hardcoding solutions.
        // - Me
        switch (count) {
            case 1:
                return rotateImageByDegrees(path0, 90);
            case 2:
                return path0;
            case 3:
                return rotateImageByDegrees(path1, 180);
            case 4:
                return rotateImageByDegrees(path0, 90);
            case 5:
                return rotateImageByDegrees(path0, 90);
            case 6:
                return rotateImageByDegrees(path1, 270);
            case 7:
                return rotateImageByDegrees(path2, 270);
            case 8:
                return path0;
            case 9:
                return rotateImageByDegrees(path1, 90);
            case 10:
                return path0;
            case 11:
                return rotateImageByDegrees(path2, 180);
            case 12:
                return path1;
            case 13:
                return rotateImageByDegrees(path2, 90);
            case 14:
                return path2;
            case 15:
                return path3;
            default:
                // This shouldn't happen.
                return gremlin;
        }
    }



    @Override
    public void draw() {
        drawBoard();

        enemy.tick();
        enemy.draw(this);

    }

    /**
     * Receive key pressed signal from the keyboard.
     */
	@Override
    public void keyPressed(){
        // this.circle.keyPressed(this.keyCode);
    }

    /**
     * Receive key released signal from the keyboard.
     */
	@Override
    public void keyReleased(){
        // this.circle.keyReleased(this.keyCode);

    }

    @Override
    public void mousePressed(MouseEvent e) {
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    /*@Override
    public void mouseDragged(MouseEvent e) {

    }*/


    // ========== System ==========
    public static void main(String[] args) {
        PApplet.main("WizardTD.App");
    }

    /**
     * Source: https://stackoverflow.com/questions/37758061/rotate-a-buffered-image-in-java
     * @param pimg The image to be rotated
     * @param angle between 0 and 360 degrees
     * @return the new rotated image
     */
    public PImage rotateImageByDegrees(PImage pimg, double angle) {
        BufferedImage img = (BufferedImage) pimg.getNative();
        double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int w = img.getWidth();
        int h = img.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        PImage result = this.createImage(newWidth, newHeight, ARGB);
        //BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        BufferedImage rotated = (BufferedImage) result.getNative();
        Graphics2D g2d = rotated.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate((newWidth - w) / 2, (newHeight - h) / 2);

        int x = w / 2;
        int y = h / 2;

        at.rotate(rads, x, y);
        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
        for (int i = 0; i < newWidth; i++) {
            for (int j = 0; j < newHeight; j++) {
                result.set(i, j, rotated.getRGB(i, j));
            }
        }
        return result;
    }
}

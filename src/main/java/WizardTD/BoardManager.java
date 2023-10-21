package WizardTD;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

// import jogamp.graph.geom.plane.AffineTransform;
import processing.core.PImage;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;


public class BoardManager {

    public App app;
    public static int CELLSIZE, BOARD_WIDTH, TOPBAR;
    public String levelFile;
    private BoardPiece piece;
    private Coordinate wizardCoordinate;
    public PImage boardImg;

    public BoardManager (App app, String levelFile) {
        System.out.printf("Hi!s%n.");
        this.app = app;
        CELLSIZE = App.CELLSIZE;
        BOARD_WIDTH = App.BOARD_WIDTH;
        TOPBAR = App.TOPBAR;
        this.levelFile = levelFile;
    }


    public void setupBoard() throws FileNotFoundException {
        /**  Drawing the board was split into two stages to improve performance.
        This first stage reads in the map, and constructs the board with all of the correct paths in the correct
        orientations. It stitches each piece together in the PImage called boardImg.
        That way, none of the calculations have to be done again and again in the draw function.
        **/
        Scanner input = new Scanner(new File(levelFile));
        for (int i = 0; i < BOARD_WIDTH; i++) {
            App.map[i] = input.nextLine().split("");
        }

        // https://processing.org/reference/createImage_.html
        boardImg = app.createImage(CELLSIZE * (BOARD_WIDTH+1), CELLSIZE * (BOARD_WIDTH+1) + 30, App.ARGB);
        
        piece = new BoardPiece();

        for (int i = 0; i < App.BOARD_WIDTH; i++) {
            for (int j = 0; j < App.BOARD_WIDTH; j++) {
                piece.set_position(j*App.CELLSIZE, i*App.CELLSIZE + App.TOPBAR);
                if (App.map[i][j].equals(" ")) {
                    piece.setSprite(app.grass);
                } else  if (App.map[i][j].equals("S")) {
                    piece.setSprite(app.shrub);
                } else  if (App.map[i][j].equals("X")) {
                    piece.setSprite(find_piece(App.map, i, j));
                } else  if (App.map[i][j].equals("W")) {
                    wizardCoordinate = new Coordinate(i, j);
                }
                boardImg.copy(piece.getSprite(), 0, 0, CELLSIZE, CELLSIZE, j*CELLSIZE, i*CELLSIZE + TOPBAR, CELLSIZE, CELLSIZE);
            }
        }
    }

    public void drawBoard() {
        piece.setSprite(boardImg);
        piece.set_position(0, 0);
        piece.draw(app);

        // The wizard house is drawn on separately because it has a transparent background, and 
        // image.copy does not work with backgrounds like those, so it was easier to just draw it.
        piece.setSprite(app.wizard_house);
        piece.set_position(wizardCoordinate.getX() * CELLSIZE, wizardCoordinate.getY() * CELLSIZE + TOPBAR);
        piece.draw(app);
    }


    PImage find_piece(String[][] map, int x, int y) {
        /**
         * The variable 'count' is used to combine the number and orientation of paths
         * into a single number.
         * Top is 8, right is 4, bottom is 2, left is 1.
         * For example, if count is 13 (= 8 + 4 + 1) there is a top, right and left path
         * leading off it.
         **/
        int count = 0;

        // Top
        if (y == 0) {
            count += 8;
        } else if (map[x][y - 1].equals("X")) {
            count += 8;
        }

        // Right
        if (x == BOARD_WIDTH - 1) {
            count += 4;
        } else if (map[x + 1][y].equals("X")) {
            count += 4;
        }

        // Bottom
        if (y == BOARD_WIDTH - 1) {
            count += 2;
        } else if (map[x][y + 1].equals("X")) {
            count += 2;
        }

        // Left
        if (x == 0) {
            count += 1;
        } else if (map[x - 1][y].equals("X")) {
            count += 1;
        }

        // Hard coding problems require hardcoding solutions.
        // - Me
        switch (count) {
            case 1:
                return rotateImageByDegrees(app.path0, 90);
            case 2:
                return app.path0;
            case 3:
                return rotateImageByDegrees(app.path1, 180);
            case 4:
                return rotateImageByDegrees(app.path0, 90);
            case 5:
                return rotateImageByDegrees(app.path0, 90);
            case 6:
                return rotateImageByDegrees(app.path1, 270);
            case 7:
                return rotateImageByDegrees(app.path2, 270);
            case 8:
                return app.path0;
            case 9:
                return rotateImageByDegrees(app.path1, 90);
            case 10:
                return app.path0;
            case 11:
                return rotateImageByDegrees(app.path2, 180);
            case 12:
                return app.path1;
            case 13:
                return rotateImageByDegrees(app.path2, 90);
            case 14:
                return app.path2;
            case 15:
                return app.path3;
            default:
                // This shouldn't happen.
                return app.gremlin;
        }
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

        PImage result = app.createImage(newWidth, newHeight, App.ARGB);
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

// really just a wrapper for a GameObject.
class BoardPiece extends GameObject {
    public BoardPiece() {
        super(0, 0);
    }

    public void tick() {

    }
}
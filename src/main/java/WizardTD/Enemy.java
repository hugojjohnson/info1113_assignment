package WizardTD;

import WizardTD.App;
import WizardTD.GameObject;
// import WizardTD.Coardinates;
// import WizardTD.*;
import processing.core.PImage;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;


public class Enemy extends GameObject {

    // ========== Variables ==========
    public static ArrayList<ArrayList<Coardinates>> paths = new ArrayList<ArrayList<Coardinates>>();
    public ArrayList<Coardinates> checkpoints;

    public String type;
    public int hp;
    public float speed;
    public float armour;
    public int mana_gained_on_kill;


    public Enemy() {
        super (0, 0);
        this.checkpoints = new ArrayList<Coardinates>(paths.get(0));
        this.set_position(this.checkpoints.get(0).getX() * App.CELLSIZE, this.checkpoints.get(0).getY() * App.CELLSIZE + App.TOPBAR);

        this.type = "gremlin";
        this.hp = 100;
        this.speed = 1;
        this.armour = 0.5f;
        this.mana_gained_on_kill = 10;
        this.setSprite(new App().gremlin);
    }

    public Enemy(String type, int hp, float speed, float armour, int mana_gained_on_kill) {
        super (0, 0);
        this.checkpoints = new ArrayList<Coardinates>(paths.get(0));
        this.set_position(this.checkpoints.get(0).getX() * App.CELLSIZE, this.checkpoints.get(0).getY() * App.CELLSIZE + App.TOPBAR);

        this.type = type;
        this.hp = hp;
        this.speed = speed;
        this.armour = armour;
        this.mana_gained_on_kill = mana_gained_on_kill;
    }



    public void tick() {
        int xPos = checkpoints.get(0).getX() * App.CELLSIZE;
        int yPos = checkpoints.get(0).getY() * App.CELLSIZE + App.TOPBAR;
        // Move towards next checkpoint
        // Gives an allowance of +- one speed cycle to allow for smooth movement and stop overcorrection.
        if (this.x < xPos - speed) {
            this.x += speed;
        } else if (this.x > xPos + speed) {
            this.x -= speed;
        } else if (this.y < yPos - speed) {
            this.y += speed;
        } else if (this.y > yPos + speed){
            this.y -= speed;
        } else {
            this.checkpoints.remove(0);
            return;
        }
    }


    public static void generateAllPaths() {
        for (int i = 0; i < App.BOARD_WIDTH; i++) {
            if (App.map[0][i].equals("X")) {
                paths.add(new ArrayList<Coardinates>(generatePath(App.map, new Coardinates(0, i))));
            }

            if (App.map[App.BOARD_WIDTH-1][i].equals("X")) {
                paths.add(new ArrayList<Coardinates>(generatePath(App.map, new Coardinates(App.BOARD_WIDTH-1, i))));
            }

            if (App.map[i][0].equals("X")) {
                paths.add(new ArrayList<Coardinates>(generatePath(App.map, new Coardinates(i, 0))));
                paths.get(paths.size()-1).add(0, new Coardinates(i, -1));
            }

            if (App.map[i][App.BOARD_WIDTH-1].equals("X")) {
                paths.add(new ArrayList<Coardinates>(generatePath(App.map, new Coardinates(i, App.BOARD_WIDTH-1))));
                paths.get(paths.size()-1).add(0, new Coardinates(i, App.BOARD_WIDTH + 1));
            }
        }
    }

    public static ArrayList<Coardinates> generatePath (String[][] map, Coardinates start_point) {
        int x = start_point.getX();
        int y = start_point.getY();

        if (x > 0 && map[y][x-1].equals("W")) {
            ArrayList<Coardinates> returnList = new ArrayList<Coardinates>();
            returnList.add(new Coardinates(y, x));
            // returnList.add(new Coardinates(y, x-1)); // Add wizard house after.
            return returnList;
        }
        if (x < App.BOARD_WIDTH && map[y][x+1].equals("W")) {
            ArrayList<Coardinates> returnList = new ArrayList<Coardinates>();
            returnList.add(new Coardinates(y, x));
            // returnList.add(new Coardinates(y, x+1)); // Add wizard house after.
            return returnList;
        }
        if (y > 0 && map[y-1][x].equals("W")) {
            ArrayList<Coardinates> returnList = new ArrayList<Coardinates>();
            returnList.add(new Coardinates(y, x));
            // returnList.add(new Coardinates(y-1, x)); // Add wizard house after.
            return returnList;
        }
        if (y < App.BOARD_WIDTH && map[y+1][x].equals("W")) {
            ArrayList<Coardinates> returnList = new ArrayList<Coardinates>();
            returnList.add(new Coardinates(y, x));
            // returnList.add(new Coardinates(y+1, x)); // Add wizard house after.
            return returnList;
        }


        // Checks.
        if (x > 0 && map[y][x-1].equals("X")) {
            String[][] returnMap = copyMap(map);
            returnMap[y][x-1] = "-";
            ArrayList<Coardinates> returnList = generatePath(returnMap, new Coardinates(y, x-1));
            if (returnList != null) {
                returnList.add(0, new Coardinates(y, x));
                return returnList;
            }
        }
        if (x < App.BOARD_WIDTH && map[y][x+1].equals("X")) {
            String[][] returnMap = copyMap(map);
            returnMap[y][x+1] = "-";
            ArrayList<Coardinates> returnList = generatePath(returnMap, new Coardinates(y, x+1));
            if (returnList != null) {
                returnList.add(0, new Coardinates(y, x));
                return returnList;
            }
        }
        if (y > 0 && map[y-1][x].equals("X")) {
            String[][] returnMap = copyMap(map);
            returnMap[y-1][x] = "-";
            ArrayList<Coardinates> returnList = generatePath(returnMap, new Coardinates(y-1, x));
            if (returnList != null) {
                returnList.add(0, new Coardinates(y, x));
                return returnList;
            }
        }
        if (y < App.BOARD_WIDTH && map[y+1][x].equals("X")) {
            String[][] returnMap = copyMap(map);
            returnMap[y+1][x] = "-";
            ArrayList<Coardinates> returnList = generatePath(returnMap, new Coardinates(y+1, x));
            if (returnList != null) {
                returnList.add(0, new Coardinates(y, x));
                return returnList;
            }
        }
        return null;
    }

    public static String[][] copyMap(String[][] map) {
        String[][] mapCopy = new String[App.BOARD_WIDTH][App.BOARD_WIDTH];
        for (int i = 0; i < App.BOARD_WIDTH; i++) {
            for (int j = 0; j < App.BOARD_WIDTH; j++) {
                mapCopy[i][j] = map[i][j];
            }
        }
        return mapCopy;
    }
}



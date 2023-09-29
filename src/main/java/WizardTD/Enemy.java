package WizardTD;
import WizardTD.App;
import WizardTD.GameObject;
// import WizardTD.Coordinates;
// import WizardTD.*;
import processing.core.PImage;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;


public class Enemy extends GameObject {

    // ========== Variables ==========
    public static ArrayList<ArrayList<Coordinates>> paths = new ArrayList<ArrayList<Coordinates>>();
    public ArrayList<Coordinates> checkpoints;

    public String type;
    public int hp;
    public float speed;
    public float armour;
    public int mana_gained_on_kill;


    public Enemy() {
        super (0, 0);
        this.checkpoints = new ArrayList<Coordinates>(paths.get(0));
        System.out.println(this.checkpoints.get(0).getY());
        this.set_position(this.checkpoints.get(0).getX() * App.CELLSIZE, this.checkpoints.get(0).getY() * App.CELLSIZE + App.TOPBAR);

        this.type = "gremlin";
        this.hp = 100;
        this.speed = 1;
        this.armour = 0.5f;
        this.mana_gained_on_kill = 10;
        // this.setSprite(new App().gremlin);
    }

    public Enemy(String type, int hp, float speed, float armour, int mana_gained_on_kill) {
        super (0, 0);
        this.checkpoints = new ArrayList<Coordinates>(paths.get(0));
        this.set_position(this.checkpoints.get(0).getX() * App.CELLSIZE, this.checkpoints.get(0).getY() * App.CELLSIZE + App.TOPBAR);

        this.type = type;
        this.hp = hp;
        this.speed = speed;
        this.armour = armour;
        this.mana_gained_on_kill = mana_gained_on_kill;
    }



    public void tick() {
        if (this.x == checkpoints.get(0).getX() * App.CELLSIZE && this.y == checkpoints.get(0).getY() * App.CELLSIZE + App.TOPBAR) {
            this.checkpoints.remove(0);
            return;
        }
        // Move towards next checkpoint
        if (this.x < checkpoints.get(0).getX() * App.CELLSIZE) {
            this.x++;
        } else {
            this.x--;
        }
        if (this.y < checkpoints.get(0).getY() * App.CELLSIZE + App.TOPBAR) {
            this.y++;
        } else {
            this.y--;
        }
        // this.x++;
    }


    public static void generateAllPaths() {
        for (int i = 0; i < App.BOARD_WIDTH; i++) {
            if (App.map[0][i].equals("X")) {
                paths.add(new ArrayList<Coordinates>(generatePath(App.map, new Coordinates(0, i))));
            }

            if (App.map[App.BOARD_WIDTH-1][i].equals("X")) {
                paths.add(new ArrayList<Coordinates>(generatePath(App.map, new Coordinates(App.BOARD_WIDTH-1, i))));
            }

            if (App.map[i][0].equals("X")) {
                paths.add(new ArrayList<Coordinates>(generatePath(App.map, new Coordinates(i, 0))));
                paths.get(paths.size()-1).add(0, new Coordinates(i, -1));
            }

            if (App.map[i][App.BOARD_WIDTH-1].equals("X")) {
                paths.add(new ArrayList<Coordinates>(generatePath(App.map, new Coordinates(i, App.BOARD_WIDTH-1))));
                paths.get(paths.size()-1).add(0, new Coordinates(i, App.BOARD_WIDTH + 1));
            }
        }
        // for (Coordinates coord : paths.get(0)) {
        //     System.out.printf("Path at %d, %d.%n", coord.getX(), coord.getY());
        // }
    }

    public static ArrayList<Coordinates> generatePath (String[][] map, Coordinates start_point) {
        int x = start_point.getX();
        int y = start_point.getY();

        if (x > 0 && map[y][x-1].equals("W")) {
            ArrayList<Coordinates> returnList = new ArrayList<Coordinates>();
            returnList.add(new Coordinates(y, x));
            // returnList.add(new Coordinates(y, x-1)); // Add wizard house after.
            return returnList;
        }
        if (x < App.BOARD_WIDTH && map[y][x+1].equals("W")) {
            ArrayList<Coordinates> returnList = new ArrayList<Coordinates>();
            returnList.add(new Coordinates(y, x));
            // returnList.add(new Coordinates(y, x+1)); // Add wizard house after.
            return returnList;
        }
        if (y > 0 && map[y-1][x].equals("W")) {
            ArrayList<Coordinates> returnList = new ArrayList<Coordinates>();
            returnList.add(new Coordinates(y, x));
            // returnList.add(new Coordinates(y-1, x)); // Add wizard house after.
            return returnList;
        }
        if (y < App.BOARD_WIDTH && map[y+1][x].equals("W")) {
            ArrayList<Coordinates> returnList = new ArrayList<Coordinates>();
            returnList.add(new Coordinates(y, x));
            // returnList.add(new Coordinates(y+1, x)); // Add wizard house after.
            return returnList;
        }


        // Checks.
        if (x > 0 && map[y][x-1].equals("X")) {
            String[][] returnMap = copyMap(map);
            returnMap[y][x-1] = "-";
            ArrayList<Coordinates> returnList = generatePath(returnMap, new Coordinates(y, x-1));
            if (returnList != null) {
                returnList.add(0, new Coordinates(y, x));
                return returnList;
            }
        }
        if (x < App.BOARD_WIDTH && map[y][x+1].equals("X")) {
            String[][] returnMap = copyMap(map);
            returnMap[y][x+1] = "-";
            ArrayList<Coordinates> returnList = generatePath(returnMap, new Coordinates(y, x+1));
            if (returnList != null) {
                returnList.add(0, new Coordinates(y, x));
                return returnList;
            }
        }
        if (y > 0 && map[y-1][x].equals("X")) {
            String[][] returnMap = copyMap(map);
            returnMap[y-1][x] = "-";
            ArrayList<Coordinates> returnList = generatePath(returnMap, new Coordinates(y-1, x));
            if (returnList != null) {
                returnList.add(0, new Coordinates(y, x));
                return returnList;
            }
        }
        if (y < App.BOARD_WIDTH && map[y+1][x].equals("X")) {
            String[][] returnMap = copyMap(map);
            returnMap[y+1][x] = "-";
            ArrayList<Coordinates> returnList = generatePath(returnMap, new Coordinates(y+1, x));
            if (returnList != null) {
                returnList.add(0, new Coordinates(y, x));
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



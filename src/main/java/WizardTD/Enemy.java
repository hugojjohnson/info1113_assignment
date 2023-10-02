package WizardTD;

import java.util.ArrayList;


public class Enemy extends GameObject {

    // ========== Variables ==========
    public static ArrayList<ArrayList<Coordinate>> paths = new ArrayList<ArrayList<Coordinate>>();
    public ArrayList<Coordinate> checkpoints;

    public String type;
    public int hp;
    public int initialHP;
    public float speed;
    public float armour;
    public int mana_gained_on_kill;
    public static int speedMultiplier = 1;


    public Enemy(String type, int hp, float speed, float armour, int mana_gained_on_kill) {
        super (0, 0);
        this.checkpoints = new ArrayList<Coordinate>(paths.get(0));
        this.set_position(this.checkpoints.get(0).getX() * App.CELLSIZE, this.checkpoints.get(0).getY() * App.CELLSIZE + App.TOPBAR);

        switch (type) {
            case "gremlin":
                this.setSprite(app.gremlin);
                break;
            case "beetle":
                this.setSprite(app.beetle);
                break;
            case "worm":
                this.setSprite(app.worm);
                break;
            default:
                this.setSprite(app.gremlin);
                break;
        }

        this.type = type;
        this.hp = hp;
        this.initialHP = hp;
        this.speed = speed;
        this.armour = armour;
        this.mana_gained_on_kill = mana_gained_on_kill;
    }



    public void tick() {
        int xPos = checkpoints.get(0).getX() * App.CELLSIZE;
        int yPos = checkpoints.get(0).getY() * App.CELLSIZE + App.TOPBAR;
        // Move towards next checkpoint
        // Gives an allowance of +- one speed cycle to allow for smooth movement and stop overcorrection.
        if (this.x < xPos - speed * speedMultiplier) {
            this.x += speed * speedMultiplier;
        } else if (this.x > xPos + speed * speedMultiplier) {
            this.x -= speed * speedMultiplier;
        } else if (this.y < yPos - speed * speedMultiplier) {
            this.y += speed * speedMultiplier;
        } else if (this.y > yPos + speed * speedMultiplier){
            this.y -= speed * speedMultiplier;
        } else {
            this.checkpoints.remove(0);
            return;
        }
    }

    public static void toggleSpeed() {
        if (speedMultiplier == 1) {
            speedMultiplier = 2;
        } else {
            speedMultiplier = 1;
        }
    }


    public static void generateAllPaths() {
        for (int i = 0; i < App.BOARD_WIDTH; i++) {
            if (App.map[0][i].equals("X")) {
                paths.add(new ArrayList<Coordinate>(generatePath(App.map, new Coordinate(0, i))));
            }

            if (App.map[App.BOARD_WIDTH-1][i].equals("X")) {
                paths.add(new ArrayList<Coordinate>(generatePath(App.map, new Coordinate(App.BOARD_WIDTH-1, i))));
            }

            if (App.map[i][0].equals("X")) {
                paths.add(new ArrayList<Coordinate>(generatePath(App.map, new Coordinate(i, 0))));
                paths.get(paths.size()-1).add(0, new Coordinate(i, -1));
            }

            if (App.map[i][App.BOARD_WIDTH-1].equals("X")) {
                paths.add(new ArrayList<Coordinate>(generatePath(App.map, new Coordinate(i, App.BOARD_WIDTH-1))));
                paths.get(paths.size()-1).add(0, new Coordinate(i, App.BOARD_WIDTH + 1));
            }
        }
    }

    public static ArrayList<Coordinate> generatePath (String[][] map, Coordinate start_point) {
        int x = start_point.getX();
        int y = start_point.getY();

        if (x > 0 && map[y][x-1].equals("W")) {
            ArrayList<Coordinate> returnList = new ArrayList<Coordinate>();
            returnList.add(new Coordinate(y, x));
            // returnList.add(new Coordinate(y, x-1)); // Add wizard house after.
            return returnList;
        }
        if (x < App.BOARD_WIDTH && map[y][x+1].equals("W")) {
            ArrayList<Coordinate> returnList = new ArrayList<Coordinate>();
            returnList.add(new Coordinate(y, x));
            // returnList.add(new Coordinate(y, x+1)); // Add wizard house after.
            return returnList;
        }
        if (y > 0 && map[y-1][x].equals("W")) {
            ArrayList<Coordinate> returnList = new ArrayList<Coordinate>();
            returnList.add(new Coordinate(y, x));
            // returnList.add(new Coordinate(y-1, x)); // Add wizard house after.
            return returnList;
        }
        if (y < App.BOARD_WIDTH && map[y+1][x].equals("W")) {
            ArrayList<Coordinate> returnList = new ArrayList<Coordinate>();
            returnList.add(new Coordinate(y, x));
            // returnList.add(new Coordinate(y+1, x)); // Add wizard house after.
            return returnList;
        }


        // Checks.
        if (x > 0 && map[y][x-1].equals("X")) {
            String[][] returnMap = copyMap(map);
            returnMap[y][x-1] = "-";
            ArrayList<Coordinate> returnList = generatePath(returnMap, new Coordinate(y, x-1));
            if (returnList != null) {
                returnList.add(0, new Coordinate(y, x));
                return returnList;
            }
        }
        if (x < App.BOARD_WIDTH && map[y][x+1].equals("X")) {
            String[][] returnMap = copyMap(map);
            returnMap[y][x+1] = "-";
            ArrayList<Coordinate> returnList = generatePath(returnMap, new Coordinate(y, x+1));
            if (returnList != null) {
                returnList.add(0, new Coordinate(y, x));
                return returnList;
            }
        }
        if (y > 0 && map[y-1][x].equals("X")) {
            String[][] returnMap = copyMap(map);
            returnMap[y-1][x] = "-";
            ArrayList<Coordinate> returnList = generatePath(returnMap, new Coordinate(y-1, x));
            if (returnList != null) {
                returnList.add(0, new Coordinate(y, x));
                return returnList;
            }
        }
        if (y < App.BOARD_WIDTH && map[y+1][x].equals("X")) {
            String[][] returnMap = copyMap(map);
            returnMap[y+1][x] = "-";
            ArrayList<Coordinate> returnList = generatePath(returnMap, new Coordinate(y+1, x));
            if (returnList != null) {
                returnList.add(0, new Coordinate(y, x));
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



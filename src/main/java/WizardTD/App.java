
package WizardTD;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.MouseEvent;


import java.io.*;
import java.util.*;

import org.checkerframework.checker.units.qual.A;

public class App extends PApplet {

    public static final int CELLSIZE = 32;
    public static final int WIZARDTOWERSIZE = 48;
    public static final int SIDEBAR = 120;
    public static final int TOPBAR = 40;
    public static final int BOARD_WIDTH = 20;
    public static int WIDTH = CELLSIZE*BOARD_WIDTH+SIDEBAR;
    public static int HEIGHT = BOARD_WIDTH*CELLSIZE+TOPBAR;
    public static final int FPS = 20;
    public String configPath;
    public Random random = new Random();

    // ========== Variables ==========
    public static String[][] map = new String[BOARD_WIDTH][BOARD_WIDTH];
    public ArrayList<Enemy> enemies = new ArrayList<Enemy>();
    public ArrayList<Fireball> fireballs = new ArrayList<Fireball>();

    // I will put these in a HashMap at some point. 
    public PImage path0, path1, path2, path3, gremlin;
    public PImage grass, shrub, beetle, fireball, gremlin1, gremlin2, gremlin3, gremlin4, gremlin5;
    public PImage tower0, tower1, tower2, wizard_house, worm;
    public int currentWaveIndex = 0;


    JSONArray wavesJSON;
    EnemyManager enemyManager;

    ButtonManager buttonManager;
    TowerManager towerManager;
    BoardManager boardManager;

    // Actions
    public boolean placingTowers = false;
    public boolean paused = false;
    public boolean upgrade_range, upgrade_speed, upgrade_damage;


    public int mana;
    public int mana_cap;
    public int mana_gained_per_second;
    public int mana_pool_spell_initial_cost, mana_pool_spell_cost_increase_per_use;
    public int mana_pool_spell_cap_multiplier, mana_pool_spell_mana_gained_multiplier;

    String layout;

    int mana_trickle_counter = 0;





    // ========== Methods ==========
    public App() {
        this.configPath = "config.json";
    }

	@Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /*
     * Load all resources such as images. Initialise the elements such as the player, enemies and map elements.
     */
	@Override
    public void setup() {
        towerManager = new TowerManager(this);
        frameRate(FPS);
        load_images();
        load_json();
        buttonManager = new ButtonManager(this);
        setupButtons();
        GameObject.app = this;

        System.out.printf("Creating a new board with layout %s%n.", layout);
        boardManager = new BoardManager(this, layout);
        try {
            boardManager.setupBoard();
        } catch (FileNotFoundException e) {
            System.out.println(e);
        }

        Enemy.generateAllPaths();
        enemyManager = new EnemyManager(wavesJSON, this); // Must be called after generateAllPaths.
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

        tower0 = this.loadImage("src/main/resources/WizardTD/tower0.png");
        tower1 = this.loadImage("src/main/resources/WizardTD/tower1.png");
        tower2 = this.loadImage("src/main/resources/WizardTD/tower2.png");
        wizard_house = this.loadImage("src/main/resources/WizardTD/wizard_house.png");
        worm = this.loadImage("src/main/resources/WizardTD/worm.png");
    }
    
    public void load_json() {
        JSONObject json = loadJSONObject(configPath);
        layout = json.getString("layout");
        wavesJSON = json.getJSONArray("waves");

        TowerManager.initial_tower_range = json.getInt("initial_tower_range");
        TowerManager.initial_tower_firing_speed = json.getFloat("initial_tower_firing_speed");
        TowerManager.initial_tower_damage = json.getInt("initial_tower_damage");
        TowerManager.initial_tower_cost = json.getInt("tower_cost");


        mana = json.getInt("initial_mana");
        mana_cap = json.getInt("initial_mana_cap");
        mana_gained_per_second = json.getInt("initial_mana_gained_per_second");
        mana_pool_spell_initial_cost = json.getInt("mana_pool_spell_initial_cost");
        mana_pool_spell_cost_increase_per_use = json.getInt("mana_pool_spell_cost_increase_per_use");
        mana_pool_spell_cap_multiplier = json.getInt("mana_pool_spell_cap_multiplier");
        mana_pool_spell_mana_gained_multiplier = json.getInt("mana_pool_spell_mana_gained_multiplier");
    }

    public void setupButtons() {
        buttonManager.buttons.add(new Button(645, 60, "FF", "2x speed"));
        buttonManager.buttons.add(new Button(645, 120, "P", "PAUSE"));
        buttonManager.buttons.add(new Button(645, 180, "T", "Build\ntower"));
        buttonManager.buttons.add(new Button(645, 240, "U1", "Upgrade\nrange"));
        buttonManager.buttons.add(new Button(645, 300, "U2", "Upgrade\nspeed"));
        buttonManager.buttons.add(new Button(645, 360, "U3", "Upgrade\ndamage"));
        buttonManager.buttons.add(new Button(645, 420, "M", "Mana pool\ncost: "));
    }

    public void drawUI() {
        stroke(0, 0, 0, 0);
        fill(255, 245, 230);
        rect(0, 0, WIDTH, TOPBAR);
        rect(CELLSIZE * BOARD_WIDTH, 0, SIDEBAR, HEIGHT);
        textSize(20);
        fill(0, 0, 0);
        text("Wave: " + currentWaveIndex, 10, 30);

        // Draw buttons
        for (Button button : buttonManager.buttons) {
            button.draw(this);
        }

        // Just putting the progress bar here...
        strokeWeight(2);
        fill(255, 255, 255);
        // mana / max mana * width
        rect(375, 10, 345, 20);
        fill(0, 255, 255);
        rect(375, 10, (int)(345 * mana/mana_cap), 20);

        textSize(14);
        fill(0);
        text(mana + " / " + mana_cap, 500, 25);
    }



    public void updateManaWinOrLose() {
        mana_trickle_counter++;
        if (mana_trickle_counter >= mana_gained_per_second * FPS) {
            mana_trickle_counter = 0;
            if (mana < mana_cap) {
                mana+= 4;
            }
        }

        if (mana <= 0) {
            // lose game
        }
        if (mana > mana_cap) {
            mana = mana_cap;
        }
    }


    @Override
    public void draw() {
        drawUI();
        boardManager.drawBoard();
        enemyManager.drawEnemies();
        towerManager.drawTowers();

        if (paused) { return; }

        enemyManager.tick();
        buttonManager.tick();
        towerManager.tick();
        updateManaWinOrLose();
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
        towerManager.handleClick(mouseX, mouseY);
        Button buttonPressed = buttonManager.mouseToButton(mouseX, mouseY);
        if (buttonPressed == null) {
            return;
        }

        switch (buttonPressed.label) {
            case "FF":
                Enemy.toggleSpeed();
                return;
            case "P":
                paused= !paused;
                return;
            case "T":
                placingTowers = !placingTowers;
                return;
            case "U1":
                upgrade_range = !upgrade_range;
                return;
            case "U2":
                upgrade_speed = !upgrade_speed;
                return;
            case "U3":
                upgrade_damage = !upgrade_damage;
                return;
            case "M":
                if (mana <= mana_pool_spell_initial_cost) {
                    return;
                }
                mana -= mana_pool_spell_initial_cost;
                mana_pool_spell_initial_cost += mana_pool_spell_cost_increase_per_use;
                mana_cap = mana_cap * mana_pool_spell_cap_multiplier;
                mana_gained_per_second = mana_gained_per_second * mana_pool_spell_mana_gained_multiplier;
                return;
            default:
                return;

        }
        
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
}

package WizardTD;

import java.util.ArrayList;


public class TowerManager {

    public App app;
    public ArrayList<Tower> towers = new ArrayList<Tower>();

    public static int initial_tower_range, initial_tower_damage;
    public static int initial_tower_cost;
    public static float initial_tower_firing_speed;

    // As per assignment outline
    public static int initial_upgrade_cost = 20, cost_increase_per_upgrade = 10;

    public TowerManager (App app) {
        this.app = app;
    }

    public void tick() {
        if (towers.size() == 0) {
            return;
        }
        for (Tower tower : towers) {
            tower.tick();
        }
    }

    public void drawTowers() {
      for (Tower tower : towers) {
            tower.draw(app);
            tower.updateSprite();
        }
        for (Fireball fireball : app.fireballs) {
            fireball.draw(app);
        }
        
        // UI associated with towers
        int cellX = Math.round(app.mouseX / App.CELLSIZE);
        int cellY = Math.round((app.mouseY-App.TOPBAR) / App.CELLSIZE);

        Tower hoverTower = getTowerFromMouse(cellX, cellY);
        if (hoverTower == null) {
            return;
        }
        app.fill(0, 0, 0, 0);
        app.strokeWeight(5);
        app.stroke(255, 255, 100);
        app.ellipse(hoverTower.x + App.CELLSIZE/2, hoverTower.y + App.CELLSIZE/2, hoverTower.damageRadius * 2, hoverTower.damageRadius * 2);
        
    }

    public void handleClick (int mouseX, int mouseY) {
        // These variables start from zero so you can easily plug them into an array.
        int cellX = Math.round(mouseX / App.CELLSIZE);
        int cellY = Math.round((mouseY-App.TOPBAR) / App.CELLSIZE);

        if (mouseY < App.TOPBAR || mouseX > App.CELLSIZE * App.BOARD_WIDTH || !App.map[cellY][cellX].equals(" ")) {
            return;
        }

        Tower clickedTower = getTowerFromMouse(cellX, cellY);

        if (clickedTower == null) {
            buildTower(cellX, cellY);
        } else {
            upgradeTower(cellX, cellY, clickedTower);
        }
    }

    public void buildTower(int cellX, int cellY) {
        if (app.placingTowers == false) { return; }

        if (app.mana > initial_tower_cost) {
            app.mana -= initial_tower_cost;
            Tower tower = new Tower(cellX * App.CELLSIZE, cellY * App.CELLSIZE + App.TOPBAR);
            towers.add(tower);
            upgradeTower(cellX, cellY, tower);
        }
    }

    public void upgradeTower(int cellX, int cellY, Tower tower) {
        int upgrade_cost;
        // range
        upgrade_cost = initial_upgrade_cost + tower.range_level * cost_increase_per_upgrade;
        if (app.upgrade_range && app.mana > upgrade_cost) {
            app.mana -= upgrade_cost;
            tower.range_level++;
        }

        // speed
        upgrade_cost = initial_upgrade_cost + tower.speed_level * cost_increase_per_upgrade;
        if (app.upgrade_speed && app.mana > upgrade_cost) {
            app.mana -= upgrade_cost;
            tower.speed_level++;
        }

        // damage
        upgrade_cost = initial_upgrade_cost + tower.damage_level * cost_increase_per_upgrade;
        if (app.upgrade_damage && app.mana > upgrade_cost) {
            app.mana -= upgrade_cost;
            tower.damage_level++;
        }
    }

    public Tower getTowerFromMouse (int cellX, int cellY) {
        if (towers.size() == 0) {
            return null;
        }
        for (Tower tower : towers) {
            if (Math.round(tower.getX() / App.CELLSIZE) == cellX && Math.round((tower.getY()-App.TOPBAR) / App.CELLSIZE) == cellY) {
                return tower;
            }
        }
        return null;
    }
}

class Tower extends GameObject {
    public int range_level = 0;
    public int speed_level = 0;
    public int damage_level = 0;

    public int fireCounter = 0;
    public int damageRadius;

    public Tower (int x, int y) {
        super(x, y);
        this.setSprite(app.tower0);
    }

    public void tick () {
        damageRadius = TowerManager.initial_tower_range + App.CELLSIZE * this.range_level;

        fireCounter++;
        // INITIAL TOWERING FIRE SPEED NEEDS TO BE A FLOAT.
        int delayFrames = (int)(App.FPS * (TowerManager.initial_tower_firing_speed - speed_level * 0.5f));
        // Here for if delayFrames is zero.
        if (delayFrames < 1) { 
            delayFrames = 5;
            System.out.println("Had to modify");
        }
        if (fireCounter % (delayFrames) == 0) {
            for (Enemy enemy : app.enemies) {
                if (withinRange(enemy)) {
                    int fireballDamage = (int)(TowerManager.initial_tower_damage * (1 + damage_level/2));
                    app.fireballs.add(new Fireball(this.x + App.CELLSIZE/2, this.y + App.CELLSIZE/2, enemy, fireballDamage));
                    return;
                }
            }
        }

        // Update fireballs
        for (Fireball fireball : app.fireballs) {
            fireball.tick();
        }

        despawnFireballs();
    }

    void despawnFireballs() {
        if (app.fireballs.size() == 0) {
            return;
        }
        ArrayList<Fireball> choppingBlock = new ArrayList<Fireball>();
        for (Fireball fireball : app.fireballs) {
            if (fireball.hit_enemy) {
                fireball.target.hp -= fireball.damage;
                choppingBlock.add(fireball);
            }
        }
        for (Fireball fireball : choppingBlock) {
            app.fireballs.remove(fireball);
        }
    }

    public boolean withinRange(Enemy enemy) {
        double a2b2 = Math.pow((enemy.x - this.x + App.CELLSIZE/2), 2) + Math.pow((enemy.y - this.y + App.CELLSIZE/2), 2);
        double distance = Math.pow(a2b2, 0.5);
        System.out.println(distance);
        return distance < this.damageRadius;
    }

    public void updateSprite() {
        int offset = Math.min(Math.min(range_level, damage_level), speed_level);

        if (offset == 0) {
            this.setSprite(app.tower0);
        } else if (offset == 1) {
            this.setSprite(app.tower1);
        } else {
            this.setSprite(app.tower2);
        }

        app.textSize(12);
        app.fill(255, 0,  255);
        for (int i = 0; i < range_level - offset; i++) {
            app.text("o", x-3 + i * 10, y+7); // offset of 10
        }

        for (int i = 0; i < damage_level - offset; i++) {
            app.text("x", x-3 + i * 10, y+33); // offset of 10
        }
        
        app.fill(0, 0, 0, 0);
        if (speed_level - offset != 0) {
            app.strokeWeight((speed_level-offset) * 2);
            app.stroke(0, 200, 255);
            app.ellipse(x+15, y+15, 22, 22);
        }
    }
}

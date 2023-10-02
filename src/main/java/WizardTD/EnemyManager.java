package WizardTD;

import processing.data.JSONArray;
import processing.data.JSONObject;
import java.util.ArrayList;


public class EnemyManager {
    // Really just a wrapper for an ArrayList, but we're using it to simplify what's going on in the App class.
    App app;
    ArrayList<Wave> waves;
    private int waveClock = 0;

    private Wave currentWave;

    public EnemyManager(JSONArray arr, App app) {
        this.app = app;
        waves = new ArrayList<Wave>();
        for (int i = 0; i < arr.size(); i++) {
            Wave wa = new Wave(arr.getJSONObject(i), app);
            this.waves.add(wa);
        }
        loadNewWave();
    }

    public void loadNewWave() {
        app.currentWaveIndex++;
        currentWave = waves.get(0);
    }

    public void tick () {
        if (currentWave.monsters.size() == 0) {
            waveClock = 0;
            waves.remove(0);
            loadNewWave();
            return;
        }

        waveClock++;
        if (waveClock >= currentWave.pre_wave_pause && (waveClock - currentWave.pre_wave_pause)%(App.FPS) == 0f) {
            Enemy spawnNow = currentWave.monsters.get(app.random.nextInt(currentWave.monsters.size()));
            app.enemies.add(spawnNow);
            currentWave.monsters.remove(spawnNow);
        }

        for (int i = 0; i < app.enemies.size(); i++) {
            app.enemies.get(i).tick();
        }

        despawnEnemies();
    }

    void despawnEnemies() {
        if (app.enemies.size() == 0) {
            return;
        }
        ArrayList<Enemy> choppingBlock = new ArrayList<Enemy>();
        for (Enemy enemy : app.enemies) {
            // Reached wizard tower
            if (enemy.checkpoints.size() == 0) {
                app.mana -= enemy.hp;
                choppingBlock.add(enemy);
            } else if (enemy.hp <= 0) {
                choppingBlock.add(enemy);
                // Play killing animation
                app.mana += enemy.mana_gained_on_kill;
            }
        }
        for (Enemy enemy : choppingBlock) {
            app.enemies.remove(enemy);
        }
    }

    public void drawEnemies() {
        for (int i = 0; i < app.enemies.size(); i++) {
            Enemy enemy = app.enemies.get(i);
            enemy.draw(app);
            // Enemy health bar
            app.strokeWeight(0);
            app.fill(255, 0, 0);
            app.rect(enemy.x-5, enemy.y-10, 30, 3);
            app.fill(0, 255, 0);
            if (enemy.initialHP != 0) {
                app.rect(enemy.x-5, enemy.y-10, (30 * enemy.hp / enemy.initialHP), 3);
            }
        }
    }
}

class Wave {
    public App app;
    public float pre_wave_pause;
    public float duration;
    public ArrayList<Enemy> monsters;
    
    public Wave (JSONObject json, App app) {
        this.app = app;
        this.pre_wave_pause = json.getFloat("pre_wave_pause") * App.FPS;
        this.duration = json.getInt("duration") * App.FPS;
        this.monsters = new ArrayList<Enemy>();

        JSONArray jsonMonsters = json.getJSONArray("monsters");

        for (int i = 0; i < jsonMonsters.size(); i++) {
            JSONObject this_monster = jsonMonsters.getJSONObject(i);
            String type = this_monster.getString("type");
            int hp = this_monster.getInt("hp");
            float speed = this_monster.getFloat("speed");
            float armour = this_monster.getFloat("armour");
            int mana_gained_on_kill = this_monster.getInt("mana_gained_on_kill");

            for (int j = 0; j < this_monster.getInt("quantity"); j++) {
                Enemy enemyToAdd = new Enemy(type, hp, speed, armour, mana_gained_on_kill);
                this.monsters.add(enemyToAdd);
            }
        }
    }
}
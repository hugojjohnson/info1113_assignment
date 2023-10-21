package WizardTD;

public class Fireball extends GameObject {
    public static int speed = 5;

    public Enemy target;
    public int damage;
    public boolean hit_enemy = false;

    public Fireball (float x, float y, Enemy target, int damage) {
        super(x, y);
        this.target = target;
        this.damage = damage;
        this.setSprite(app.fireball);
    }

    public void tick() {
        if (distanceToTarget() <= speed) {
            hit_enemy = true;
            return;
        }

        this.x += speed * (target.x - this.x) / distanceToTarget();
        this.y += speed * (target.y - this.y) / distanceToTarget();

    }

    public int distanceToTarget() {
        return (int)Math.pow(Math.pow(this.x - target.x, 2) + Math.pow(this.y - target.y, 2), 0.5);
    }
}

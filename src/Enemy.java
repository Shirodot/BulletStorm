import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Enemy {
    public double x, y;
    public int hp, maxHp;
    public boolean active = true;
    public int type; // 0=fairy, 1=youkai, 2=mini-boss
    public double vx, vy;
    public int age = 0;
    public int shootTimer = 0;
    public int shootInterval;
    public int scoreValue;
    public double radius;
    public int patternType; // 0=aimed, 1=circle, 2=spread, 3=wave
    private Color bodyColor;
    private int maxAge; // Auto-despawn time

    // Movement path
    private double entryX, entryY;
    private int movePhase = 0;

    public Enemy(double x, double y, int type) {
        this.x = x;
        this.y = y;
        this.entryX = x;
        this.entryY = y;
        this.type = type;
        switch (type) {
            case 0: hp = 30; maxHp = 30; radius = 10; scoreValue = 100; shootInterval = 100; bodyColor = new Color(100, 180, 255); patternType = 0; maxAge = 480; break;
            case 1: hp = 80; maxHp = 80; radius = 14; scoreValue = 300; shootInterval = 90; bodyColor = new Color(180, 100, 255); patternType = 1; maxAge = 600; break;
            case 2: hp = 200; maxHp = 200; radius = 18; scoreValue = 1000; shootInterval = 70; bodyColor = new Color(255, 140, 50); patternType = 2; maxAge = 720; break;
        }
        vx = 0; vy = 1.5;
    }

    public void update() {
        age++;

        // Not yet visible (staggered spawn)
        if (age <= 0) return;

        // Auto despawn after maxAge
        if (age >= maxAge) {
            active = false;
            return;
        }

        // Entry movement
        if (age < 60) {
            x += vx;
            y += vy;
        } else if (age < 120) {
            // Slow down
            vx *= 0.95;
            vy *= 0.95;
            x += vx;
            y += vy;
        } else {
            // Patrol side to side
            double t = (age - 120) * 0.02;
            x = entryX + Math.sin(t) * 40;
            y = entryY + 60 + Math.sin(t * 0.5) * 15;
        }
        if (shootTimer > 0) shootTimer--;
    }

    public List<Bullet> tryShoot(double px, double py, int difficulty) {
        List<Bullet> bullets = new ArrayList<>();
        if (shootTimer > 0 || hp <= 0) return bullets;

        double speedMult = (1.0 + difficulty * 0.2) * 0.65; // Reduced to 65%
        int countAdd = difficulty;
        shootTimer = Math.max(15, shootInterval - difficulty * 5);

        switch (patternType) {
            case 0:
                bullets.addAll(BulletPattern.aimedSpread(x, y, px, py, 2,
                        Math.toRadians(15), 2.0 * speedMult, 4, new Color(100, 200, 255)));
                break;
            case 1:
                bullets.addAll(BulletPattern.circularBurst(x, y, 6 + difficulty,
                        1.8 * speedMult, 4, new Color(200, 100, 255), age * 0.1));
                break;
            case 2:
                bullets.addAll(BulletPattern.starBurst(x, y, 8 + difficulty,
                        2.5 * speedMult, 1.3 * speedMult, 4, new Color(255, 150, 50), age * 0.15));
                break;
            case 3:
                bullets.addAll(BulletPattern.waveBullets(x, y, px, py, 2,
                        2.0 * speedMult, 7, 0.15, 4, new Color(150, 255, 150)));
                break;
        }
        return bullets;
    }

    public void hit(int dmg) {
        hp -= dmg;
        if (hp <= 0) { hp = 0; active = false; }
    }

    public boolean collidesWith(double bx, double by, double br) {
        double dx = x - bx, dy = y - by;
        return dx*dx + dy*dy < (radius + br)*(radius + br);
    }

    public void draw(Graphics2D g2, int fieldX) {
        if (!active) return;
        int ix = (int)(x + fieldX);
        int iy = (int)y;

        switch (type) {
            case 0: drawFairy(g2, ix, iy); break;
            case 1: drawYoukai(g2, ix, iy); break;
            case 2: drawMiniBoss(g2, ix, iy); break;
        }

        // HP bar
        if (hp < maxHp) {
            int bw = 40;
            g2.setColor(Color.DARK_GRAY);
            g2.fillRect(ix - bw/2, iy - (int)radius - 10, bw, 4);
            g2.setColor(new Color(50, 220, 50));
            g2.fillRect(ix - bw/2, iy - (int)radius - 10, (int)(bw * (double)hp / maxHp), 4);
        }
    }

    private void drawFairy(Graphics2D g2, int ix, int iy) {
        // Wings
        g2.setColor(new Color(200, 230, 255, 180));
        g2.fillOval(ix - 18, iy - 8, 14, 22);
        g2.fillOval(ix + 4, iy - 8, 14, 22);
        // Body
        g2.setColor(bodyColor);
        g2.fillOval(ix - 7, iy - 10, 14, 20);
        // Head
        g2.setColor(new Color(255, 220, 180));
        g2.fillOval(ix - 6, iy - 18, 12, 12);
        // Eyes
        g2.setColor(Color.BLACK);
        g2.fillOval(ix - 4, iy - 15, 3, 3);
        g2.fillOval(ix + 1, iy - 15, 3, 3);
    }

    private void drawYoukai(Graphics2D g2, int ix, int iy) {
        // Cape
        g2.setColor(new Color(100, 0, 150));
        int[] cx = { ix-14, ix+14, ix+10, ix-10 };
        int[] cy = { iy+14, iy+14, iy-8, iy-8 };
        g2.fillPolygon(cx, cy, 4);
        // Body
        g2.setColor(bodyColor);
        g2.fillOval(ix - 10, iy - 12, 20, 26);
        // Head
        g2.setColor(new Color(255, 200, 180));
        g2.fillOval(ix - 8, iy - 22, 16, 16);
        // Horns
        g2.setColor(new Color(255, 100, 100));
        g2.fillOval(ix - 10, iy - 26, 6, 8);
        g2.fillOval(ix + 4, iy - 26, 6, 8);
        // Eyes glow
        g2.setColor(new Color(255, 50, 50));
        g2.fillOval(ix - 5, iy - 19, 4, 4);
        g2.fillOval(ix + 1, iy - 19, 4, 4);
    }

    private void drawMiniBoss(Graphics2D g2, int ix, int iy) {
        // Aura
        g2.setColor(new Color(255, 140, 0, 60));
        g2.fillOval(ix - 24, iy - 24, 48, 48);
        // Body
        g2.setColor(bodyColor);
        g2.fillOval(ix - 16, iy - 16, 32, 32);
        // Pattern
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        for (int i = 0; i < 6; i++) {
            double a = age * 0.05 + i * Math.PI / 3;
            g2.drawLine(ix, iy, (int)(ix + Math.cos(a)*14), (int)(iy + Math.sin(a)*14));
        }
        g2.setStroke(new BasicStroke(1));
        // Face
        g2.setColor(new Color(255, 220, 180));
        g2.fillOval(ix - 10, iy - 12, 20, 20);
        g2.setColor(new Color(255, 80, 0));
        g2.fillOval(ix - 6, iy - 8, 4, 4);
        g2.fillOval(ix + 2, iy - 8, 4, 4);
    }
}

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Player {
    public double x, y;
    public double speed = 4.0;
    public double focusSpeed = 2.0;
    public double hitRadius = 3.0;
    public double drawRadius = 12.0;
    public int lives = 3;
    public int bombs = 3;
    public int power = 0;   // 0-400
    public long score = 0;
    public int graze = 0;
    public boolean focused = false;
    public boolean invincible = false;
    public int invincibleTimer = 0;
    public int characterIndex = 0; // 0=Reimu, 1=Marisa
    public boolean active = true;
    public ExperienceSystem exp = new ExperienceSystem();

    private int shootCooldown = 0;
    private int shootRate = 5;
    private int deathFlash = 0;

    // Shot color per character
    private Color[] shotColors = { new Color(255, 80, 80), new Color(255, 220, 0) };

    public Player(double x, double y, int characterIndex) {
        this.x = x;
        this.y = y;
        this.characterIndex = characterIndex;
        this.lives = 3 + exp.hpMaxLevel;
    }

    public void update(boolean up, boolean down, boolean left, boolean right,
                       boolean shoot, boolean focus, int fieldW, int fieldH) {
        this.focused = focus;
        double spd = focus ? focusSpeed : speed;

        if (left  && x - drawRadius > 0)       x -= spd;
        if (right && x + drawRadius < fieldW)   x += spd;
        if (up    && y - drawRadius > 0)        y -= spd;
        if (down  && y + drawRadius < fieldH)   y += spd;

        if (invincible) {
            invincibleTimer--;
            if (invincibleTimer <= 0) invincible = false;
        }
        if (deathFlash > 0) deathFlash--;
        if (shootCooldown > 0) shootCooldown--;
    }

    public List<Bullet> shoot() {
        List<Bullet> bullets = new ArrayList<>();
        if (shootCooldown > 0) return bullets;
        shootCooldown = Math.max(2, shootRate - exp.getShootRateBonus());

        int powerLevel = power / 100; // 0-4
        Color c = shotColors[characterIndex];

        double speedMult = exp.getShotSpeedMultiplier() * 0.70; // Reduced to 70%

        if (characterIndex == 0) {
            // Reimu: homing amulets + forward shots
            // Forward shots
            bullets.add(makeBullet(x, y - drawRadius, -Math.PI/2, 12 * speedMult, 4, c));
            if (powerLevel >= 1) {
                bullets.add(makeBullet(x - 8, y - drawRadius, -Math.PI/2 - 0.08, 12 * speedMult, 4, c));
                bullets.add(makeBullet(x + 8, y - drawRadius, -Math.PI/2 + 0.08, 12 * speedMult, 4, c));
            }
            if (powerLevel >= 2) {
                // Side shots
                bullets.add(makeBullet(x - 20, y, -Math.PI/2 - 0.2, 10 * speedMult, 4, c));
                bullets.add(makeBullet(x + 20, y, -Math.PI/2 + 0.2, 10 * speedMult, 4, c));
            }
            if (powerLevel >= 3) {
                // Homing amulets
                Bullet h1 = makeBullet(x - 5, y, -Math.PI/2, 6 * speedMult, 5, new Color(255, 150, 150));
                Bullet h2 = makeBullet(x + 5, y, -Math.PI/2, 6 * speedMult, 5, new Color(255, 150, 150));
                h1.type = 1; h2.type = 1; // homing
                bullets.add(h1); bullets.add(h2);
            }
            if (powerLevel >= 4) {
                bullets.add(makeBullet(x - 14, y - drawRadius, -Math.PI/2 - 0.15, 12 * speedMult, 4, c));
                bullets.add(makeBullet(x + 14, y - drawRadius, -Math.PI/2 + 0.15, 12 * speedMult, 4, c));
            }
        } else {
            // Marisa: wide magic beam shots
            bullets.add(makeBullet(x, y - drawRadius, -Math.PI/2, 14 * speedMult, 5, c));
            if (powerLevel >= 1) {
                bullets.add(makeBullet(x - 10, y - drawRadius + 5, -Math.PI/2, 14 * speedMult, 5, c));
                bullets.add(makeBullet(x + 10, y - drawRadius + 5, -Math.PI/2, 14 * speedMult, 5, c));
            }
            if (powerLevel >= 2) {
                bullets.add(makeBullet(x - 22, y, -Math.PI/2, 12 * speedMult, 4, c));
                bullets.add(makeBullet(x + 22, y, -Math.PI/2, 12 * speedMult, 4, c));
            }
            if (powerLevel >= 3) {
                bullets.add(makeBullet(x - 30, y + 5, -Math.PI/2 + 0.05, 11 * speedMult, 4, c));
                bullets.add(makeBullet(x + 30, y + 5, -Math.PI/2 - 0.05, 11 * speedMult, 4, c));
            }
            if (powerLevel >= 4) {
                bullets.add(makeBullet(x, y - drawRadius, -Math.PI/2, 16 * speedMult, 7, new Color(255, 255, 100)));
            }
        }
        return bullets;
    }

    private Bullet makeBullet(double bx, double by, double angle, double speed, double r, Color c) {
        Bullet b = new Bullet(bx, by, angle, speed, r, c, true);
        b.damage = 10 + power / 50;
        return b;
    }

    public void die() {
        if (invincible || !active) return;
        lives--;
        power = Math.max(0, power - 100);
        invincible = true;
        invincibleTimer = 180;
        deathFlash = 30;
        // Reduce experience on death
        exp.exp = Math.max(0, exp.exp - 200);
        if (lives < 0) { lives = 0; active = false; }
    }

    public void draw(Graphics2D g2, int fieldX) {
        double drawX = x + fieldX;
        if (deathFlash > 0 && deathFlash % 4 < 2) return;
        if (invincible && invincibleTimer % 4 < 2 && deathFlash == 0) return;

        if (characterIndex == 0) drawReimu(g2, drawX);
        else drawMarisa(g2, drawX);

        // Draw hitbox in focus mode
        if (focused) {
            g2.setColor(new Color(255, 255, 255, 180));
            g2.setStroke(new BasicStroke(1f));
            g2.drawOval((int)(drawX - hitRadius), (int)(y - hitRadius),
                        (int)(hitRadius*2), (int)(hitRadius*2));
            // Rotating diamond
            g2.setColor(new Color(255, 100, 100, 200));
            drawRotatingDiamond(g2, drawX, y);
        }
    }

    private void drawReimu(Graphics2D g2, double drawX) {
        // Body - shrine maiden dress
        g2.setColor(new Color(220, 30, 30));
        int[] bodyX = { (int)drawX, (int)(drawX-10), (int)(drawX-8), (int)(drawX+8), (int)(drawX+10) };
        int[] bodyY = { (int)(y+14), (int)(y+14), (int)y, (int)y, (int)(y+14) };
        g2.fillPolygon(bodyX, bodyY, 5);
        // White collar
        g2.setColor(Color.WHITE);
        g2.fillRect((int)(drawX-6), (int)(y-2), 12, 6);
        // Head
        g2.setColor(new Color(255, 220, 180));
        g2.fillOval((int)(drawX-7), (int)(y-18), 14, 14);
        // Hair
        g2.setColor(new Color(80, 40, 20));
        g2.fillOval((int)(drawX-8), (int)(y-20), 16, 10);
        // Detached sleeves
        g2.setColor(new Color(255, 50, 50));
        g2.fillRoundRect((int)(drawX-22), (int)(y-5), 10, 16, 4, 4);
        g2.fillRoundRect((int)(drawX+12), (int)(y-5), 10, 16, 4, 4);
        // Ribbon
        g2.setColor(new Color(255, 50, 200));
        g2.fillOval((int)(drawX-4), (int)(y-22), 8, 6);
    }

    private void drawMarisa(Graphics2D g2, double drawX) {
        // Skirt
        g2.setColor(new Color(30, 30, 30));
        int[] sx = { (int)(drawX-12), (int)(drawX+12), (int)(drawX+8), (int)(drawX-8) };
        int[] sy = { (int)(y+14), (int)(y+14), (int)y, (int)y };
        g2.fillPolygon(sx, sy, 4);
        // Apron
        g2.setColor(Color.WHITE);
        g2.fillRect((int)(drawX-5), (int)(y+1), 10, 13);
        // Body
        g2.setColor(new Color(30, 30, 30));
        g2.fillRect((int)(drawX-7), (int)(y-4), 14, 8);
        // Head
        g2.setColor(new Color(255, 220, 180));
        g2.fillOval((int)(drawX-7), (int)(y-18), 14, 14);
        // Hair (blonde)
        g2.setColor(new Color(255, 200, 50));
        g2.fillOval((int)(drawX-8), (int)(y-21), 16, 10);
        // Witch hat
        g2.setColor(new Color(20, 20, 20));
        int[] hx = { (int)(drawX-2), (int)(drawX+2), (int)(drawX+8), (int)(drawX-8) };
        int[] hy = { (int)(y-30), (int)(y-30), (int)(y-20), (int)(y-20) };
        g2.fillPolygon(hx, hy, 4);
        g2.fillRect((int)(drawX-9), (int)(y-22), 18, 4);
        // Star on hat
        g2.setColor(Color.YELLOW);
        g2.fillOval((int)(drawX-3), (int)(y-29), 6, 6);
        // Broom
        g2.setColor(new Color(139, 90, 43));
        g2.setStroke(new BasicStroke(2));
        g2.drawLine((int)(drawX+10), (int)(y-5), (int)(drawX+20), (int)(y+10));
        g2.setStroke(new BasicStroke(1));
    }

    private long rotTimer = 0;
    private void drawRotatingDiamond(Graphics2D g2, double drawX, double drawY) {
        rotTimer++;
        double angle = rotTimer * 0.1;
        int r = 10;
        int[] px = new int[4], py = new int[4];
        for (int i = 0; i < 4; i++) {
            double a = angle + i * Math.PI / 2;
            px[i] = (int)(drawX + Math.cos(a) * r);
            py[i] = (int)(drawY + Math.sin(a) * r);
        }
        g2.setColor(new Color(255, 100, 100, 150));
        g2.fillPolygon(px, py, 4);
    }
}

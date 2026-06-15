import java.awt.*;
import java.awt.geom.Ellipse2D;

public class Bullet {
    public double x, y;
    public double vx, vy;
    public double radius;
    public Color color;
    public Color coreColor;
    public boolean active;
    public boolean isPlayerBullet;
    public int damage;
    // For special movement patterns
    public int type; // 0=normal, 1=homing, 2=wave, 3=accelerating
    public double angle;
    public double speed;
    public double angularVel;
    public double waveAmp;
    public double waveFreq;
    public double wavePhase;
    public double accel;
    public int lifetime;
    public int age;
    private double baseX, baseY;
    private double baseDx, baseDy;

    public Bullet(double x, double y, double angle, double speed, double radius, Color color, boolean isPlayerBullet) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.speed = speed;
        this.vx = Math.cos(angle) * speed;
        this.vy = Math.sin(angle) * speed;
        this.radius = radius;
        this.color = color;
        this.coreColor = Color.WHITE;
        this.isPlayerBullet = isPlayerBullet;
        this.active = true;
        this.damage = isPlayerBullet ? 10 : 1;
        this.type = 0;
        this.lifetime = Integer.MAX_VALUE;
        this.age = 0;
        this.baseX = x;
        this.baseY = y;
        this.baseDx = vx;
        this.baseDy = vy;
    }

    public void update(double playerX, double playerY) {
        age++;
        if (age >= lifetime) { active = false; return; }

        switch (type) {
            case 1: // Homing
                double dx = playerX - x;
                double dy = playerY - y;
                double dist = Math.sqrt(dx*dx + dy*dy);
                if (dist > 1) {
                    double targetAngle = Math.atan2(dy, dx);
                    double currentAngle = Math.atan2(vy, vx);
                    double diff = normalizeAngle(targetAngle - currentAngle);
                    double turnSpeed = 0.05;
                    currentAngle += Math.max(-turnSpeed, Math.min(turnSpeed, diff));
                    vx = Math.cos(currentAngle) * speed;
                    vy = Math.sin(currentAngle) * speed;
                }
                break;
            case 2: // Wave
                wavePhase += waveFreq;
                double perpX = -Math.sin(angle);
                double perpY = Math.cos(angle);
                double wave = Math.sin(wavePhase) * waveAmp;
                x += vx + perpX * wave;
                y += vy + perpY * wave;
                return;
            case 3: // Accelerating
                speed += accel;
                vx = Math.cos(angle) * speed;
                vy = Math.sin(angle) * speed;
                break;
            default:
                if (angularVel != 0) {
                    angle += angularVel;
                    vx = Math.cos(angle) * speed;
                    vy = Math.sin(angle) * speed;
                }
                break;
        }
        x += vx;
        y += vy;
    }

    private double normalizeAngle(double a) {
        while (a > Math.PI) a -= 2*Math.PI;
        while (a < -Math.PI) a += 2*Math.PI;
        return a;
    }

    public boolean collidesWith(double ox, double oy, double or) {
        double dx = x - ox, dy = y - oy;
        return dx*dx + dy*dy < (radius + or)*(radius + or);
    }

    public void draw(Graphics2D g2) {
        if (!active) return;
        int ix = (int)(x - radius);
        int iy = (int)(y - radius);
        int d = (int)(radius * 2);
        // Glow outer
        g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 80));
        g2.fillOval(ix - 2, iy - 2, d + 4, d + 4);
        // Main color
        g2.setColor(color);
        g2.fillOval(ix, iy, d, d);
        // White core
        g2.setColor(coreColor);
        int cd = Math.max(2, d / 3);
        g2.fillOval((int)(x - cd/2), (int)(y - cd/2), cd, cd);
    }
}

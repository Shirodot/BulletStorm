import java.awt.*;

public class Particle {
    public double x, y, vx, vy;
    public int life, maxLife;
    public Color color;
    public double size;
    public boolean active = true;

    public Particle(double x, double y, double vx, double vy, int life, Color color, double size) {
        this.x = x; this.y = y; this.vx = vx; this.vy = vy;
        this.life = this.maxLife = life;
        this.color = color;
        this.size = size;
    }

    public void update() {
        x += vx; y += vy;
        vy += 0.05; // gravity
        vx *= 0.98;
        life--;
        if (life <= 0) active = false;
    }

    public void draw(Graphics2D g2) {
        float alpha = (float) life / maxLife;
        g2.setColor(new Color(color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f, alpha));
        int s = (int)(size * alpha);
        g2.fillOval((int)(x - s/2), (int)(y - s/2), s, s);
    }
}

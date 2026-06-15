import java.awt.*;

public class Item {
    public double x, y;
    private double vy = -1.5;
    public boolean active = true;
    public int type; // 0=power, 1=point, 2=life
    private int age = 0;
    private static final double COLLECT_RADIUS = 15;

    public Item(double x, double y, int type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.vy = -2 + Math.random();
    }

    public void update(double px, double py, boolean attract) {
        age++;
        if (age < 30) { vy += 0.1; y += vy; return; }
        vy = Math.min(vy + 0.05, 1.5);

        if (attract) {
            double dx = px - x, dy = py - y;
            double dist = Math.sqrt(dx*dx + dy*dy);
            if (dist < 200) {
                x += dx / dist * 6;
                y += dy / dist * 6;
                return;
            }
        }
        y += vy;
        if (y > 600) active = false;
    }

    public boolean collidesWith(double px, double py) {
        double dx = x - px, dy = y - py;
        return dx*dx + dy*dy < COLLECT_RADIUS * COLLECT_RADIUS;
    }

    public void collect(Player player) {
        switch (type) {
            case 0:
                player.power = Math.min(400, player.power + 10);
                player.exp.addExp(20);
                break;
            case 1:
                long points = (long)(500 * (1 + player.power / 100));
                player.score += points;
                player.exp.addExp(50);
                break;
            case 2:
                player.lives = Math.min(8, player.lives + 1 + player.exp.hpMaxLevel);
                player.exp.addExp(100);
                break;
        }
    }

    public void draw(Graphics2D g, int fieldX) {
        int ix = (int)(x + fieldX);
        int iy = (int)y;
        switch (type) {
            case 0: // Power (magenta diamond)
                g.setColor(new Color(255, 80, 200));
                g.fillPolygon(new int[]{ix, ix+5, ix, ix-5}, new int[]{iy-7, iy, iy+7, iy}, 4);
                g.setColor(new Color(255, 180, 240));
                g.fillPolygon(new int[]{ix, ix+2, ix, ix-2}, new int[]{iy-3, iy, iy+3, iy}, 4);
                // P label
                g.setFont(new Font("SansSerif", Font.BOLD, 7));
                g.setColor(Color.WHITE);
                g.drawString("P", ix-2, iy+2);
                break;
            case 1: // Point (blue circle)
                g.setColor(new Color(50, 100, 255));
                g.fillOval(ix-5, iy-5, 10, 10);
                g.setColor(new Color(150, 200, 255));
                g.fillOval(ix-3, iy-3, 6, 6);
                break;
            case 2: // Life (red heart)
                g.setColor(new Color(255, 50, 50));
                g.fillOval(ix-5, iy-5, 8, 8);
                g.fillOval(ix-1, iy-5, 8, 8);
                int[] hx = {ix-5, ix+5, ix};
                int[] hy = {iy-1, iy-1, iy+7};
                g.fillPolygon(hx, hy, 3);
                g.setColor(new Color(255, 150, 150));
                g.fillOval(ix-4, iy-4, 4, 4);
                break;
        }
    }
}

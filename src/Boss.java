import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Boss {
    public double x, y;
    public int phase = 0;
    public boolean active = true;
    public double radius = 24;

    private int[] phaseMaxHp;
    private int hp;
    private int maxHp;
    private int phaseTimer = 0;
    private int maxPhaseTime;
    private int shootTimer = 0;
    private int age = 0;
    private int difficulty;
    private String name;
    private Color[] phaseColors;
    private int totalPhases;
    private int patternStep = 0;
    private double spiralAngle = 0;
    private boolean showName = true;
    private int nameTimer = 120;

    // Spell card names
    private String[][] spellNames = {
        { "PHASE I: Wave Pattern", "PHASE II: Ring Formation", "PHASE III: Spiral Arms",
          "PHASE IV: Butterfly Spray", "PHASE V: Ultimate Chaos" },
        { "PHASE I: Circular Burst", "PHASE II: Double Ring", "PHASE III: Accelerating Stream",
          "PHASE IV: Convergence", "PHASE V: Final Onslaught" }
    };
    private int bossIndex = 0;
    private String currentSpellName = "";

    public Boss(double x, double y, int difficulty, int bossIndex) {
        this.x = x;
        this.y = y;
        this.difficulty = difficulty;
        this.bossIndex = bossIndex;
        totalPhases = 5;
        maxPhaseTime = 1800 - difficulty * 200;
        phaseMaxHp = new int[]{ 800, 1200, 1500, 2000, 2500 };
        phaseColors = new Color[]{
            new Color(255, 150, 200),
            new Color(150, 200, 255),
            new Color(255, 200, 100),
            new Color(200, 100, 255),
            new Color(255, 50, 50)
        };
        hp = maxHp = phaseMaxHp[0];
        name = (bossIndex == 0) ? "Commander Aurora" : "Engineer Chronos";
        currentSpellName = spellNames[bossIndex][0];
    }

    public void update() {
        age++;
        phaseTimer++;

        // Movement: hover at top, move side to side
        double targetX = 200 + Math.sin(age * 0.01) * 120;
        double targetY = 80 + Math.sin(age * 0.007) * 30;
        x += (targetX - x) * 0.02;
        y += (targetY - y) * 0.02;

        if (shootTimer > 0) shootTimer--;
        if (nameTimer > 0) nameTimer--;
        else showName = false;

        // Phase transition on timeout or hp=0
        if (phaseTimer >= maxPhaseTime || hp <= 0) {
            nextPhase();
        }
    }

    private void nextPhase() {
        phase++;
        patternStep = 0;
        if (phase >= totalPhases) { active = false; return; }
        hp = maxHp = phaseMaxHp[phase];
        phaseTimer = 0;
        spiralAngle = 0;
        currentSpellName = spellNames[bossIndex][phase];
        showName = true;
        nameTimer = 120;
    }

    public List<Bullet> tryShoot(double px, double py) {
        List<Bullet> bullets = new ArrayList<>();
        if (shootTimer > 0) return bullets;

        double speedMult = (1.0 + difficulty * 0.25) * 0.65; // Reduced to 65%
        int countAdd = difficulty * 2;

        switch (phase) {
            case 0: // Cherry blossom: gentle spiral + aimed
                bullets.addAll(spellPhase0(px, py, speedMult, countAdd));
                shootTimer = 8;
                break;
            case 1: // Dark star: circular bursts
                bullets.addAll(spellPhase1(px, py, speedMult, countAdd));
                shootTimer = 6;
                break;
            case 2: // Dense spiral + homing
                bullets.addAll(spellPhase2(px, py, speedMult, countAdd));
                shootTimer = 5;
                break;
            case 3: // Butterfly barrage
                bullets.addAll(spellPhase3(px, py, speedMult, countAdd));
                shootTimer = 4;
                break;
            case 4: // Final: everything
                bullets.addAll(spellFinal(px, py, speedMult, countAdd));
                shootTimer = 3;
                break;
        }
        return bullets;
    }

    // Phase 0: Gentle petal scatter
    private List<Bullet> spellPhase0(double px, double py, double sm, int ca) {
        List<Bullet> b = new ArrayList<>();
        patternStep++;
        spiralAngle += 0.15;
        // Slow expanding ring
        if (patternStep % 40 == 0)
            b.addAll(BulletPattern.circularBurst(x, y, 8, 1.3 * sm, 6,
                new Color(255, 150, 200), spiralAngle));
        // Aimed shots every now and then
        if (patternStep % 60 == 30)
            b.addAll(BulletPattern.aimedSpread(x, y, px, py, 2, 0.25, 2.0 * sm, 4,
                new Color(255, 200, 220)));
        return b;
    }

    // Phase 1: Double ring + spinning
    private List<Bullet> spellPhase1(double px, double py, double sm, int ca) {
        List<Bullet> b = new ArrayList<>();
        patternStep++;
        spiralAngle += 0.08;
        if (patternStep % 35 == 0)
            b.addAll(BulletPattern.doubleRing(x, y, 10, 1.8 * sm, -0.9 * sm, 4,
                new Color(150, 200, 255), new Color(200, 150, 255), spiralAngle));
        return b;
    }

    // Phase 2: Dense spiral arms
    private List<Bullet> spellPhase2(double px, double py, double sm, int ca) {
        List<Bullet> b = new ArrayList<>();
        patternStep++;
        spiralAngle += 0.12;
        // Spiral
        if (patternStep % 15 == 0)
            b.addAll(BulletPattern.spiralArm(x, y, 3, 2.2 * sm, 4,
                new Color(255, 220, 50), spiralAngle));
        // Occasional homing
        if (patternStep % 90 == 0)
            b.addAll(BulletPattern.homingBullets(x, y, 2, 1.8, 6,
                new Color(255, 100, 100), spiralAngle));
        return b;
    }

    // Phase 3: Butterfly + laser barrage
    private List<Bullet> spellPhase3(double px, double py, double sm, int ca) {
        List<Bullet> b = new ArrayList<>();
        patternStep++;
        spiralAngle += 0.1;
        if (patternStep % 30 == 0)
            b.addAll(BulletPattern.butterflySpray(x, y, px, py, 3, 2.0 * sm, 4,
                new Color(200, 100, 255), patternStep * 0.08));
        if (patternStep % 50 == 0)
            b.addAll(BulletPattern.starBurst(x, y, 12, 2.5 * sm, 0.9, 4,
                new Color(255, 150, 50), spiralAngle));
        return b;
    }

    // Phase 4: Final chaos
    private List<Bullet> spellFinal(double px, double py, double sm, int ca) {
        List<Bullet> b = new ArrayList<>();
        patternStep++;
        spiralAngle += 0.2;

        int sub = patternStep % 150;
        if (sub < 50) {
            // Moderate spiral
            if (patternStep % 20 == 0)
                b.addAll(BulletPattern.spiralArm(x, y, 4, 2.5 * sm, 4, new Color(255, 50, 50), spiralAngle));
        } else if (sub < 100) {
            // Wave aimed
            if (patternStep % 35 == 0)
                b.addAll(BulletPattern.waveBullets(x, y, px, py, 3, 2.5 * sm, 10, 0.12, 4,
                    new Color(255, 200, 50)));
        } else {
            // Rotating burst
            if (patternStep % 40 == 0)
                b.addAll(BulletPattern.aimedSpread(x, y, px, py, 3, 0.4, 2.2 * sm, 4,
                    new Color(200, 50, 255)));
        }
        return b;
    }

    public void hit(int dmg) {
        hp -= dmg;
        if (hp < 0) hp = 0;
    }

    public boolean collidesWith(double bx, double by, double br) {
        double dx = x - bx, dy = y - by;
        return dx*dx + dy*dy < (radius + br)*(radius + br);
    }

    public void draw(Graphics2D g2, int fieldX) {
        if (!active) return;
        int ix = (int)(x + fieldX);
        int iy = (int)y;

        // Aura glow
        Color aura = phaseColors[Math.min(phase, phaseColors.length-1)];
        for (int i = 3; i >= 0; i--) {
            g2.setColor(new Color(aura.getRed(), aura.getGreen(), aura.getBlue(), 40));
            int r = (int)(radius + i*8 + Math.sin(age*0.1)*4);
            g2.fillOval(ix - r, iy - r, r*2, r*2);
        }
        // Body
        g2.setColor(aura);
        g2.fillOval(ix - (int)radius, iy - (int)radius, (int)(radius*2), (int)(radius*2));
        // Inner glow
        g2.setColor(new Color(255,255,255,120));
        g2.fillOval(ix - (int)(radius*0.5), iy - (int)(radius*0.5), (int)(radius), (int)(radius));

        // Rotating crystal pattern
        g2.setColor(new Color(255,255,255,180));
        g2.setStroke(new BasicStroke(1.5f));
        for (int i = 0; i < 8; i++) {
            double a = age * 0.05 + i * Math.PI / 4;
            double r2 = radius + 10;
            g2.drawLine(ix, iy,
                (int)(ix + Math.cos(a)*r2), (int)(iy + Math.sin(a)*r2));
        }
        g2.setStroke(new BasicStroke(1));

        // Face
        g2.setColor(new Color(255,220,200));
        g2.fillOval(ix-12, iy-14, 24, 22);

        // HP bar (full width at top of field)
        drawHPBar(g2, fieldX, aura);

        // Spell name
        if (showName) {
            float alpha = Math.min(1f, nameTimer / 30f);
            g2.setColor(new Color(1f, 0.9f, 0.5f, alpha));
            g2.setFont(new Font("SansSerif", Font.BOLD, 11));
            FontMetrics fm = g2.getFontMetrics();
            int sw = fm.stringWidth(currentSpellName);
            g2.drawString(currentSpellName, fieldX + 240 - sw/2, iy - (int)radius - 20);
        }

        // Phase indicators
        for (int i = 0; i < totalPhases; i++) {
            int ox = fieldX + 10 + i * 14;
            if (i < phase) {
                g2.setColor(Color.DARK_GRAY);
            } else if (i == phase) {
                g2.setColor(aura);
            } else {
                g2.setColor(new Color(200, 200, 200));
            }
            g2.fillRect(ox, 8, 10, 6);
            g2.setColor(Color.BLACK);
            g2.drawRect(ox, 8, 10, 6);
        }
    }

    private void drawHPBar(Graphics2D g2, int fieldX, Color aura) {
        int bw = 480;
        int bx = fieldX;
        int by2 = 18;
        g2.setColor(new Color(40, 40, 40));
        g2.fillRect(bx, by2, bw, 8);
        double ratio = (double)hp / maxHp;
        // Time bar
        double timeRatio = 1.0 - (double)phaseTimer / maxPhaseTime;
        g2.setColor(new Color(180, 180, 50, 120));
        g2.fillRect(bx, by2, (int)(bw * timeRatio), 8);
        // HP
        GradientPaint gp = new GradientPaint(bx, by2, aura.brighter(), bx + (int)(bw*ratio), by2, aura);
        g2.setPaint(gp);
        g2.fillRect(bx, by2, (int)(bw * ratio), 8);
        g2.setPaint(null);
        g2.setColor(Color.WHITE);
        g2.drawRect(bx, by2, bw, 8);
    }

    public String getCurrentSpellName() { return currentSpellName; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getPhase() { return phase; }
    public int getTotalPhases() { return totalPhases; }
}

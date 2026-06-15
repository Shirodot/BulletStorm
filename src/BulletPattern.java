import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Bullet pattern algorithms for enemy/boss attacks.
 * Each method returns a list of bullets to add to the scene.
 */
public class BulletPattern {

    // --- Basic helpers ---

    /** Fire N bullets evenly spread in a full circle from (x,y). */
    public static List<Bullet> circularBurst(double x, double y, int count, double speed, double radius, Color color, double offsetAngle) {
        List<Bullet> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            double angle = offsetAngle + i * (2 * Math.PI / count);
            list.add(new Bullet(x, y, angle, speed, radius, color, false));
        }
        return list;
    }

    /** Fire N bullets aimed at player with spread. */
    public static List<Bullet> aimedSpread(double x, double y, double px, double py,
                                            int count, double spread, double speed, double radius, Color color) {
        List<Bullet> list = new ArrayList<>();
        double baseAngle = Math.atan2(py - y, px - x);
        for (int i = 0; i < count; i++) {
            double offset = (count == 1) ? 0 : -spread/2 + spread * i / (count - 1);
            list.add(new Bullet(x, y, baseAngle + offset, speed, radius, color, false));
        }
        return list;
    }

    /** Fire bullets in a spiral (called each frame, rotates offsetAngle). */
    public static List<Bullet> spiralArm(double x, double y, int arms, double speed,
                                          double radius, Color color, double offsetAngle) {
        List<Bullet> list = new ArrayList<>();
        for (int i = 0; i < arms; i++) {
            double angle = offsetAngle + i * (2 * Math.PI / arms);
            list.add(new Bullet(x, y, angle, speed, radius, color, false));
        }
        return list;
    }

    /** Ring of bullets expanding outward + inner ring contracting (danmaku classic). */
    public static List<Bullet> doubleRing(double x, double y, int count,
                                           double outerSpeed, double innerSpeed,
                                           double radius, Color outerColor, Color innerColor, double offset) {
        List<Bullet> list = new ArrayList<>();
        list.addAll(circularBurst(x, y, count, outerSpeed, radius, outerColor, offset));
        list.addAll(circularBurst(x, y, count, innerSpeed, radius*0.7, innerColor, offset + Math.PI/count));
        return list;
    }

    /** Starburst: alternating fast/slow rings. */
    public static List<Bullet> starBurst(double x, double y, int count, double fastSpeed,
                                          double slowSpeed, double radius, Color color, double offset) {
        List<Bullet> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            double angle = offset + i * (2 * Math.PI / count);
            double speed = (i % 2 == 0) ? fastSpeed : slowSpeed;
            list.add(new Bullet(x, y, angle, speed, radius, color, false));
        }
        return list;
    }

    /** Fan: N bullets spread across a cone aimed at player. */
    public static List<Bullet> aimFan(double x, double y, double px, double py,
                                       int count, double spreadAngle, double speed,
                                       double radius, Color color) {
        List<Bullet> list = new ArrayList<>();
        double baseAngle = Math.atan2(py - y, px - x);
        for (int i = 0; i < count; i++) {
            double t = count == 1 ? 0.5 : (double) i / (count - 1);
            double angle = baseAngle - spreadAngle/2 + spreadAngle * t;
            list.add(new Bullet(x, y, angle, speed, radius, color, false));
        }
        return list;
    }

    /** Homing bullet aimed at player that tracks. */
    public static List<Bullet> homingBullets(double x, double y, int count, double speed,
                                              double radius, Color color, double offset) {
        List<Bullet> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            double angle = offset + i * (2 * Math.PI / count);
            Bullet b = new Bullet(x, y, angle, speed * 0.5, radius, color, false);
            b.type = 1; // homing
            b.speed = speed;
            list.add(b);
        }
        return list;
    }

    /** Wave bullets that move sinusoidally. */
    public static List<Bullet> waveBullets(double x, double y, double px, double py,
                                            int count, double speed, double amplitude,
                                            double frequency, double radius, Color color) {
        List<Bullet> list = new ArrayList<>();
        double baseAngle = Math.atan2(py - y, px - x);
        for (int i = 0; i < count; i++) {
            double offset = (count == 1) ? 0 : (i - count/2.0) * 0.25;
            Bullet b = new Bullet(x, y, baseAngle + offset, speed, radius, color, false);
            b.type = 2;
            b.waveAmp = amplitude;
            b.waveFreq = frequency;
            b.wavePhase = i * Math.PI / count;
            list.add(b);
        }
        return list;
    }

    /** Rotating ring that spins as it expands. */
    public static List<Bullet> spinningRing(double x, double y, int count, double speed,
                                             double spinRate, double radius, Color color, double offset) {
        List<Bullet> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            double angle = offset + i * (2 * Math.PI / count);
            Bullet b = new Bullet(x, y, angle, speed, radius, color, false);
            b.angularVel = spinRate;
            list.add(b);
        }
        return list;
    }

    /** Accelerating bullets that start slow then speed up. */
    public static List<Bullet> accelBurst(double x, double y, int count, double initSpeed,
                                           double acceleration, double radius, Color color, double offset) {
        List<Bullet> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            double angle = offset + i * (2 * Math.PI / count);
            Bullet b = new Bullet(x, y, angle, initSpeed, radius, color, false);
            b.type = 3;
            b.accel = acceleration;
            list.add(b);
        }
        return list;
    }

    /** Butterfly/figure-8 pattern using parametric equations. */
    public static List<Bullet> butterflySpray(double x, double y, double px, double py,
                                               int count, double speed, double radius, Color color, double timeOffset) {
        List<Bullet> list = new ArrayList<>();
        double baseAngle = Math.atan2(py - y, px - x);
        for (int i = 0; i < count; i++) {
            double t = timeOffset + i * 0.3;
            double spreadAngle = Math.sin(t) * Math.PI * 0.4;
            double angle = baseAngle + spreadAngle;
            list.add(new Bullet(x, y, angle, speed, radius, color, false));
        }
        return list;
    }

    /** Laser-like barrage: dense column of bullets. */
    public static List<Bullet> laserBarrage(double x, double y, double targetAngle,
                                             int count, double speed, double spread,
                                             double radius, Color color) {
        List<Bullet> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            double angle = targetAngle + (Math.random() - 0.5) * spread;
            Bullet b = new Bullet(x, y, angle, speed * (0.8 + Math.random() * 0.4), radius, color, false);
            list.add(b);
        }
        return list;
    }

    /** Danmaku sakura: petals of bullets in a flower formation. */
    public static List<Bullet> sakuraPetal(double x, double y, int petals, int bulletsPerPetal,
                                            double speed, double radius, Color color, double offset) {
        List<Bullet> list = new ArrayList<>();
        for (int p = 0; p < petals; p++) {
            double petalAngle = offset + p * (2 * Math.PI / petals);
            for (int i = 0; i < bulletsPerPetal; i++) {
                double spread = (double) i / bulletsPerPetal * 0.5 - 0.25;
                double angle = petalAngle + spread;
                double s = speed * (0.5 + (double) i / bulletsPerPetal);
                list.add(new Bullet(x, y, angle, s, radius, color, false));
            }
        }
        return list;
    }
}

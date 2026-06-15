import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    // Layout
    public static final int TOTAL_W = 800;
    public static final int TOTAL_H = 600;
    public static final int FIELD_X = 32;
    public static final int FIELD_Y = 16;
    public static final int FIELD_W = 480;
    public static final int FIELD_H = 560;
    public static final int HUD_X = FIELD_X + FIELD_W + 16;

    private GameState state = GameState.START;
    private javax.swing.Timer timer;

    // Input
    private boolean[] keys = new boolean[256];

    // Game objects
    private Player player;
    private List<Bullet> playerBullets = new ArrayList<>();
    private List<Bullet> enemyBullets = new ArrayList<>();
    private List<Enemy> enemies = new ArrayList<>();
    private Boss boss = null;
    private List<Particle> particles = new ArrayList<>();
    private List<Item> items = new ArrayList<>();

    // Stage / spawn
    private int frameCount = 0;
    private int stage = 1;
    private int spawnTimer = 0;
    private boolean bossSpawned = false;
    private boolean stageClearing = false;
    private int stageClearTimer = 0;

    // Selection state
    private int selectedChar = 0;
    private int selectedDiff = 1; // 0=Easy, 1=Normal, 2=Hard, 3=Lunatic
    private int menuCursor = 0;

    // Screen shake
    private int shakeTimer = 0;

    // Background stars
    private double[][] stars;
    private Color[] starColors;

    // Fonts
    private Font jpFont, titleFont, uiFont, smallFont;

    // Off-screen buffer for smooth rendering
    private BufferedImage buffer;
    private Graphics2D bufG;

    private static final String[] DIFF_NAMES = {"Easy", "Normal", "Hard", "Lunatic"};
    private static final String[] CHAR_NAMES = {"Aurora Striker", "Chronos Engineer"};
    private static final String[] CHAR_SUBS = {"Swift and precise", "Explosive power"};
    private static final Color[] DIFF_COLORS = {
        new Color(100,200,100), new Color(100,150,255),
        new Color(255,150,50), new Color(255,50,50)
    };

    public GamePanel() {
        setPreferredSize(new Dimension(TOTAL_W, TOTAL_H));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        initFonts();
        initStars();
        buffer = new BufferedImage(TOTAL_W, TOTAL_H, BufferedImage.TYPE_INT_ARGB);
        bufG = buffer.createGraphics();
        bufG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        bufG.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        timer = new javax.swing.Timer(16, this); // ~60fps
        timer.start();
    }

    private void initFonts() {
        try {
            jpFont = new Font("MS Gothic", Font.PLAIN, 13);
            if (jpFont.getFamily().equals("Dialog")) jpFont = new Font("SansSerif", Font.PLAIN, 13);
        } catch (Exception e) { jpFont = new Font("SansSerif", Font.PLAIN, 13); }
        titleFont = new Font("Serif", Font.BOLD | Font.ITALIC, 36);
        uiFont = new Font("SansSerif", Font.BOLD, 13);
        smallFont = new Font("SansSerif", Font.PLAIN, 11);
    }

    private void initStars() {
        stars = new double[150][3];
        starColors = new Color[150];
        Random rng = new Random(42);
        for (int i = 0; i < 150; i++) {
            stars[i][0] = rng.nextDouble() * TOTAL_W;
            stars[i][1] = rng.nextDouble() * TOTAL_H;
            stars[i][2] = rng.nextDouble() * 2 + 0.5;
            int br = 150 + rng.nextInt(105);
            starColors[i] = new Color(br, br, Math.min(255, br + rng.nextInt(50)));
        }
    }

    private void startGame() {
        frameCount = 0; stage = 1; spawnTimer = 0; bossSpawned = false;
        stageClearing = false; stageClearTimer = 0;
        player = new Player(FIELD_W / 2.0, FIELD_H - 80, selectedChar);
        playerBullets.clear(); enemyBullets.clear();
        enemies.clear(); particles.clear(); items.clear();
        boss = null;
        state = GameState.PLAYING;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        update();
        repaint();
    }

    private void update() {
        updateStars();
        if (shakeTimer > 0) shakeTimer--;

        switch (state) {
            case PLAYING:   updatePlaying(); break;
            case STAGE_CLEAR: updateStageClear(); break;
            default: break;
        }
    }

    private void updateStars() {
        for (double[] s : stars) {
            s[1] += s[2] * 0.5;
            if (s[1] > TOTAL_H) { s[1] = 0; s[0] = Math.random() * TOTAL_W; }
        }
    }

    private void updatePlaying() {
        frameCount++;
        boolean up = keys[KeyEvent.VK_UP], down = keys[KeyEvent.VK_DOWN];
        boolean left = keys[KeyEvent.VK_LEFT], right = keys[KeyEvent.VK_RIGHT];
        boolean shoot = keys[KeyEvent.VK_Z], focus = keys[KeyEvent.VK_SHIFT];
        boolean bomb = keys[KeyEvent.VK_X];

        // Player update
        player.update(up, down, left, right, shoot, focus, FIELD_W, FIELD_H);

        if (shoot) {
            List<Bullet> newBullets = player.shoot();
            playerBullets.addAll(newBullets);
        }

        if (bomb && player.bombs > 0) useBomb();

        // Update player bullets
        for (Bullet b : playerBullets) {
            b.update(0, 0);
            if (b.y < -20 || b.x < -20 || b.x > FIELD_W + 20) b.active = false;
        }

        // Update enemy bullets
        for (Bullet b : enemyBullets) {
            b.update(player.x, player.y);
            if (isOutOfField(b.x, b.y)) b.active = false;
        }

        // Spawn enemies
        spawnEnemies();

        // Update enemies
        for (Enemy en : enemies) {
            en.update();
            if (isOutOfField(en.x, en.y) && en.age > 120) en.active = false;
            if (!en.active) continue;
            if (en.age <= 0) continue; // Not yet spawned
            enemyBullets.addAll(en.tryShoot(player.x, player.y, selectedDiff));
        }

        // Update boss
        if (boss != null) {
            boss.update();
            List<Bullet> bossBullets = boss.tryShoot(player.x, player.y);
            enemyBullets.addAll(bossBullets);
            if (!boss.active) { bossDefeated(); }
        }

        // Update items
        for (Item it : items) {
            it.update(player.x, player.y, player.focused || player.y < FIELD_H * 0.25);
        }

        // Update particles
        for (Particle p : particles) p.update();

        // --- Collisions ---
        // Player bullets vs enemies
        for (Bullet b : playerBullets) {
            if (!b.active) continue;
            for (Enemy en : enemies) {
                if (!en.active) continue;
                if (en.collidesWith(b.x, b.y, b.radius)) {
                    en.hit(b.damage);
                    b.active = false;
                    if (!en.active) {
                        spawnExplosion(en.x, en.y, Color.CYAN);
                        spawnItems(en.x, en.y, en.type + 1);
                        player.score += en.scoreValue * (1 + selectedDiff);
                        // Award experience
                        long expReward = (long)(50 * (en.type + 1) * (selectedDiff + 1));
                        player.exp.addExp(expReward);
                    }
                    break;
                }
            }
            if (!b.active) continue;
            if (boss != null && boss.active && boss.collidesWith(b.x, b.y, b.radius)) {
                boss.hit(b.damage);
                b.active = false;
                player.score += 10L * (selectedDiff + 1);
                player.exp.addExp(100L * (selectedDiff + 1));
            }
        }

        // Enemy bullets vs player
        if (!player.invincible) {
            for (Bullet b : enemyBullets) {
                if (!b.active) continue;
                // Graze detection (wider radius)
                double dx = b.x - player.x, dy = b.y - player.y;
                double dist2 = dx*dx + dy*dy;
                double grazeR = (player.hitRadius + b.radius + 8) * player.exp.getGrazeRadiusMultiplier();
                if (dist2 < grazeR * grazeR) {
                    player.graze++;
                    player.score += 10;
                    player.exp.addExp(5);
                }
                if (b.collidesWith(player.x, player.y, player.hitRadius)) {
                    player.die();
                    spawnExplosion(player.x + FIELD_X, player.y, Color.RED);
                    shakeTimer = 30;
                    if (!player.active) { state = GameState.GAME_OVER; return; }
                    // Clear bullets on death
                    for (Bullet eb : enemyBullets) eb.active = false;
                    break;
                }
            }
        }

        // Item collection
        for (Item it : items) {
            if (!it.active) continue;
            if (it.collidesWith(player.x, player.y)) {
                it.collect(player);
                it.active = false;
            }
        }

        // Cleanup
        playerBullets.removeIf(b -> !b.active);
        enemyBullets.removeIf(b -> !b.active);
        enemies.removeIf(en -> !en.active);
        particles.removeIf(p -> !p.active);
        items.removeIf(it -> !it.active);
    }

    private void spawnEnemies() {
        if (bossSpawned) return;
        spawnTimer++;
        Random rng = new Random();

        int waveInterval = Math.max(60, 120 - stage * 10);
        if (spawnTimer % waveInterval == 0) {
            int wave = spawnTimer / waveInterval;
            spawnWave(wave);
        }

        // Boss spawns after N waves
        int bossWave = 8 + stage * 2;
        if (spawnTimer / waveInterval >= bossWave && enemies.isEmpty()) {
            spawnBoss();
        }
    }

    private void spawnWave(int wave) {
        int type = Math.min(wave / 3, 2);
        int count = 3 + Math.min(wave, 5);
        // Formation patterns - spawn with interval
        switch (wave % 4) {
            case 0: // Left to right line
                for (int i = 0; i < count; i++) {
                    Enemy en = new Enemy(60 + i * (FIELD_W - 120) / (count-1), -20, type);
                    en.vx = 0; en.vy = 1.5;
                    // Stagger spawn: spawn with 15-frame interval
                    en.age = -(i * 15);
                    enemies.add(en);
                }
                break;
            case 1: // V formation
                for (int i = 0; i < count; i++) {
                    double t = (double) i / (count-1);
                    Enemy en = new Enemy(50 + t * (FIELD_W - 100), -20 - Math.abs(t-0.5)*60, type);
                    en.age = -(i * 15);
                    enemies.add(en);
                }
                break;
            case 2: // Sides
                for (int i = 0; i < count; i++) {
                    boolean leftSide = i < count/2;
                    Enemy en = new Enemy(leftSide ? 30 : FIELD_W - 30, 40 + (i % (count/2 + 1)) * 40, type);
                    en.vx = leftSide ? 0.5 : -0.5;
                    en.age = -(i * 15);
                    enemies.add(en);
                }
                break;
            case 3: // Diagonal sweep
                for (int i = 0; i < count; i++) {
                    Enemy en = new Enemy(10 + i * 8, -20 - i * 15, type);
                    en.vx = 1.0;
                    en.age = -(i * 15);
                    enemies.add(en);
                }
                break;
        }
    }

    private void spawnBoss() {
        bossSpawned = true;
        enemies.clear();
        enemyBullets.clear();
        boss = new Boss(FIELD_W / 2.0, -60, selectedDiff, stage % 2);
    }

    private void bossDefeated() {
        spawnExplosion(boss.x, boss.y, Color.YELLOW);
        spawnExplosion(boss.x + 20, boss.y - 10, Color.WHITE);
        spawnExplosion(boss.x - 20, boss.y + 10, Color.CYAN);
        player.score += 50000L * (selectedDiff + 1);
        player.exp.addExp(5000L * (selectedDiff + 1));
        boss = null;
        stageClearing = true;
        stageClearTimer = 180;
        state = GameState.STAGE_CLEAR;
    }

    private void updateStageClear() {
        stageClearTimer--;
        if (stageClearTimer <= 0) {
            stage++;
            if (stage > 3) { state = GameState.GAME_OVER; return; } // Win
            bossSpawned = false;
            spawnTimer = 0;
            enemies.clear();
            enemyBullets.clear();
            playerBullets.clear();
            state = GameState.PLAYING;
        }
    }

    private void useBomb() {
        if (player.bombs <= 0) return;
        player.bombs--;
        enemyBullets.clear();
        for (Enemy en : enemies) {
            en.hit(en.maxHp / 2);
            if (!en.active) { spawnExplosion(en.x, en.y, Color.CYAN); spawnItems(en.x, en.y, 1); }
        }
        if (boss != null) boss.hit(200);
        player.score += 1000;
        player.invincible = true;
        player.invincibleTimer = Math.max(player.invincibleTimer, 120);
        shakeTimer = 20;
        // Bomb particle burst
        for (int i = 0; i < 60; i++) {
            double angle = Math.random() * Math.PI * 2;
            double speed = Math.random() * 5 + 1;
            particles.add(new Particle(player.x + FIELD_X, player.y,
                Math.cos(angle)*speed, Math.sin(angle)*speed,
                60, new Color(200, 200, 255), 8));
        }
    }

    private void spawnExplosion(double ex, double ey, Color c) {
        for (int i = 0; i < 20; i++) {
            double angle = Math.random() * Math.PI * 2;
            double speed = Math.random() * 4 + 0.5;
            particles.add(new Particle(ex + FIELD_X, ey,
                Math.cos(angle)*speed, Math.sin(angle)*speed,
                30 + (int)(Math.random()*30), c, 4 + Math.random()*4));
        }
    }

    private void spawnItems(double ex, double ey, int count) {
        for (int i = 0; i < count; i++) {
            int type = (Math.random() < 0.6) ? 0 : (Math.random() < 0.5 ? 1 : 2);
            items.add(new Item(ex + (Math.random()-0.5)*20, ey + (Math.random()-0.5)*20, type));
        }
    }

    private boolean isOutOfField(double x, double y) {
        return x < -30 || x > FIELD_W + 30 || y < -30 || y > FIELD_H + 30;
    }

    // ---- DRAWING ----

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawFrame();
        int sx = 0, sy = 0;
        if (shakeTimer > 0) {
            sx = (int)((Math.random()-0.5) * shakeTimer * 0.8);
            sy = (int)((Math.random()-0.5) * shakeTimer * 0.4);
        }
        g.drawImage(buffer, sx, sy, null);
    }

    private void drawFrame() {
        Graphics2D g = bufG;
        // Background
        g.setColor(new Color(5, 5, 20));
        g.fillRect(0, 0, TOTAL_W, TOTAL_H);
        drawStars(g);

        switch (state) {
            case START:           drawStartScreen(g); break;
            case CHARACTER_SELECT:drawCharSelect(g); break;
            case DIFFICULTY_SELECT:drawDiffSelect(g); break;
            case PLAYING:         drawGame(g); break;
            case STAGE_CLEAR:     drawGame(g); drawStageClear(g); break;
            case GAME_OVER:       drawGameOver(g); break;
        }
    }

    private void drawStars(Graphics2D g) {
        for (int i = 0; i < stars.length; i++) {
            int br = (int)(100 + stars[i][2] * 60);
            g.setColor(starColors[i]);
            int s = (int)(stars[i][2]);
            g.fillOval((int)stars[i][0] - s/2, (int)stars[i][1] - s/2, s+1, s+1);
        }
    }

    // ---- START SCREEN ----
    private void drawStartScreen(Graphics2D g) {
        // Decorative border
        drawBorder(g);
        // Title
        g.setFont(titleFont);
        String title = "BULLET STORM";
        FontMetrics fm = g.getFontMetrics();
        int tw = fm.stringWidth(title);
        // Modern gradient shadow
        g.setColor(new Color(50, 100, 200, 100));
        g.drawString(title, TOTAL_W/2 - tw/2 + 2, 153);
        // Main title with glow effect
        for (int i = 3; i >= 0; i--) {
            float t = i / 3f;
            g.setColor(new Color(0.4f + t*0.2f, 0.6f + t*0.3f, 1f, 0.3f));
            g.drawString(title, TOTAL_W/2 - tw/2, 150 - i*2);
        }
        g.setColor(new Color(100, 200, 255));
        g.drawString(title, TOTAL_W/2 - tw/2, 150);

        // Subtitle
        g.setFont(new Font("Serif", Font.ITALIC, 14));
        String sub = "Dodge the chaos. Master the storm.";
        fm = g.getFontMetrics();
        g.setColor(new Color(150, 200, 255));
        g.drawString(sub, TOTAL_W/2 - fm.stringWidth(sub)/2, 185);

        // Draw decorative danmaku art
        drawStartArt(g);

        // Menu items
        String[] menuItems = { "Play Game", "How to Play", "Exit" };
        g.setFont(uiFont);
        for (int i = 0; i < menuItems.length; i++) {
            boolean sel = menuCursor == i;
            fm = g.getFontMetrics();
            int mx = TOTAL_W/2 - fm.stringWidth(menuItems[i])/2;
            int my = 390 + i * 45;
            if (sel) {
                g.setColor(new Color(255, 220, 50, 60));
                g.fillRoundRect(mx - 20, my - 20, fm.stringWidth(menuItems[i]) + 40, 30, 8, 8);
                g.setColor(new Color(255, 220, 50));
                // Cursor diamonds
                g.fillPolygon(new int[]{mx-15, mx-5, mx-15}, new int[]{my-5, my+5, my+15}, 3);
            } else {
                g.setColor(new Color(200, 200, 220));
            }
            g.setFont(new Font("Serif", Font.BOLD, 20));
            g.drawString(menuItems[i], mx, my + 10);
        }

        // Version/credit
        g.setFont(smallFont);
        g.setColor(new Color(120, 120, 140));
        g.drawString("Bullet Storm v1.0", 10, TOTAL_H - 10);
        g.drawString("Z: Select  Arrow: Move", TOTAL_W - 150, TOTAL_H - 10);

        // Controls hint
        g.setColor(new Color(150,150,170));
        g.setFont(smallFont);
        g.drawString("↑↓: Select  Z: Confirm", TOTAL_W/2 - 60, TOTAL_H - 30);
    }

    private void drawStartArt(Graphics2D g) {
        // Modern geometric animation
        int cx = TOTAL_W / 2, cy = 290;
        long t = frameCount;

        // Rotating hexagons + particles
        for (int ring = 0; ring < 3; ring++) {
            int cnt = 6 + ring * 3;
            double r = 40 + ring * 35;
            double rot = t * 0.02 * (ring % 2 == 0 ? 1 : -1);

            // Draw hexagon outline
            g.setColor(new Color(100 + ring*30, 150 + ring*20, 255 - ring*30, 100));
            g.setStroke(new BasicStroke(1.5f));
            int[] px = new int[cnt], py = new int[cnt];
            for (int i = 0; i < cnt; i++) {
                double a = rot + i * 2*Math.PI/cnt;
                px[i] = (int)(cx + Math.cos(a) * r);
                py[i] = (int)(cy + Math.sin(a) * r);
            }
            g.drawPolygon(px, py, cnt);

            // Ring particles
            for (int i = 0; i < cnt; i++) {
                double a = rot + i * 2*Math.PI/cnt;
                int bx = (int)(cx + Math.cos(a) * r);
                int by = (int)(cy + Math.sin(a) * r);
                g.setColor(new Color(150 + ring*30, 180 + ring*20, 255));
                g.fillOval(bx-4, by-4, 8, 8);
                g.setColor(new Color(255, 255, 255, 180));
                g.fillOval(bx-2, by-2, 4, 4);
            }
        }
        g.setStroke(new BasicStroke(1));

        // Center pulse
        int pulse = (int)(Math.sin(t*0.03)*8);
        g.setColor(new Color(100, 200, 255, 120 + pulse*5));
        g.fillOval(cx-25-pulse, cy-25-pulse, 50+pulse*2, 50+pulse*2);
        g.setColor(new Color(150, 220, 255));
        g.fillOval(cx-15, cy-15, 30, 30);
    }

    // ---- CHAR SELECT ----
    private void drawCharSelect(Graphics2D g) {
        drawBorder(g);
        g.setFont(new Font("Serif", Font.BOLD, 22));
        FontMetrics fm = g.getFontMetrics();
        String title = "CHARACTER SELECT";
        g.setColor(new Color(100, 200, 255));
        g.drawString(title, TOTAL_W/2 - fm.stringWidth(title)/2, 80);

        for (int i = 0; i < 2; i++) {
            int cx = 200 + i * 400;
            int cy = 300;
            boolean sel = selectedChar == i;

            // Selection box
            if (sel) {
                g.setColor(new Color(255, 220, 50, 40));
                g.fillRoundRect(cx - 110, cy - 160, 220, 280, 12, 12);
                g.setColor(new Color(255, 220, 50));
                g.setStroke(new BasicStroke(2));
                g.drawRoundRect(cx - 110, cy - 160, 220, 280, 12, 12);
                g.setStroke(new BasicStroke(1));
            } else {
                g.setColor(new Color(80, 80, 120, 60));
                g.fillRoundRect(cx - 110, cy - 160, 220, 280, 12, 12);
                g.setColor(new Color(150, 150, 180));
                g.drawRoundRect(cx - 110, cy - 160, 220, 280, 12, 12);
            }

            // Draw character
            drawCharPreview(g, i, cx, cy - 60);

            // Name
            g.setFont(new Font("Serif", Font.BOLD, 16));
            fm = g.getFontMetrics();
            g.setColor(sel ? new Color(255, 220, 50) : new Color(200, 200, 220));
            g.drawString(CHAR_NAMES[i], cx - fm.stringWidth(CHAR_NAMES[i])/2, cy + 30);

            g.setFont(smallFont);
            fm = g.getFontMetrics();
            g.setColor(new Color(160, 160, 200));
            g.drawString(CHAR_SUBS[i], cx - fm.stringWidth(CHAR_SUBS[i])/2, cy + 50);

            // Shot description
            String[] shots = { "Shot: Tracking Particles\nBomb: Energy Burst", "Shot: Beam Barrage\nBomb: Temporal Shatter" };
            g.setFont(smallFont);
            g.setColor(sel ? new Color(220,220,255) : new Color(140,140,160));
            String[] lines = shots[i].split("\n");
            for (int li = 0; li < lines.length; li++)
                g.drawString(lines[li], cx - 70, cy + 75 + li * 16);
        }

        g.setFont(smallFont);
        g.setColor(new Color(150,150,170));
        g.drawString("← →: Select Character    Z: Confirm    X: Back", TOTAL_W/2 - 140, TOTAL_H - 20);
    }

    private void drawCharPreview(Graphics2D g, int charIdx, int cx, int cy) {
        // Scale up player drawing for preview
        Graphics2D g2 = (Graphics2D) g.create();
        g2.translate(cx, cy);
        g2.scale(2.5, 2.5);
        // Use player draw logic
        Player preview = new Player(0, 0, charIdx);
        preview.draw(g2, 0);
        g2.dispose();
    }

    // ---- DIFFICULTY SELECT ----
    private void drawDiffSelect(Graphics2D g) {
        drawBorder(g);
        g.setFont(new Font("Serif", Font.BOLD, 22));
        FontMetrics fm = g.getFontMetrics();
        String title = "DIFFICULTY SELECTION";
        g.setColor(new Color(100, 200, 255));
        g.drawString(title, TOTAL_W/2 - fm.stringWidth(title)/2, 80);

        String[] descs = {
            "Training mode. Gentle patterns,\nfocused on learning.",
            "Standard challenge. Balanced\npattern complexity and pace.",
            "Expert mode. Faster bullets\nand intricate formations.",
            "INSANE difficulty. Only try if\nyou are a true master."
        };

        for (int i = 0; i < 4; i++) {
            int dy = 160 + i * 100;
            boolean sel = selectedDiff == i;

            if (sel) {
                g.setColor(new Color(DIFF_COLORS[i].getRed(), DIFF_COLORS[i].getGreen(), DIFF_COLORS[i].getBlue(), 40));
                g.fillRoundRect(180, dy - 30, 440, 80, 10, 10);
                g.setColor(DIFF_COLORS[i]);
                g.setStroke(new BasicStroke(2));
                g.drawRoundRect(180, dy - 30, 440, 80, 10, 10);
                g.setStroke(new BasicStroke(1));
            }

            // Difficulty name
            g.setFont(new Font("Serif", Font.BOLD, 20));
            g.setColor(sel ? DIFF_COLORS[i] : new Color(160,160,180));
            g.drawString(DIFF_NAMES[i], 220, dy + 5);

            // Stars
            for (int s = 0; s < 4; s++) {
                g.setColor(s <= i ? DIFF_COLORS[i] : new Color(60,60,80));
                g.fillPolygon(star5(350 + s*20, dy, 8));
            }

            // Description
            g.setFont(smallFont);
            g.setColor(sel ? new Color(220,220,240) : new Color(130,130,150));
            String[] dl = descs[i].split("\n");
            for (int li = 0; li < dl.length; li++)
                g.drawString(dl[li], 420, dy - 5 + li * 15);
        }

        g.setFont(smallFont);
        g.setColor(new Color(150,150,170));
        g.drawString("↑↓: Select Difficulty    Z: Start Game    X: Back", TOTAL_W/2 - 140, TOTAL_H - 20);
    }

    private Polygon star5(int cx, int cy, int r) {
        int[] px = new int[5], py = new int[5];
        for (int i = 0; i < 5; i++) {
            double a = -Math.PI/2 + i * 2*Math.PI/5;
            px[i] = (int)(cx + Math.cos(a)*r);
            py[i] = (int)(cy + Math.sin(a)*r);
        }
        return new Polygon(px, py, 5);
    }

    // ---- GAME ----
    private void drawGame(Graphics2D g) {
        // Field background
        g.setColor(new Color(8, 8, 30));
        g.fillRect(FIELD_X, FIELD_Y, FIELD_W, FIELD_H);

        // Clip to field
        Shape oldClip = g.getClip();
        g.setClip(FIELD_X, FIELD_Y, FIELD_W, FIELD_H);

        // Field decorations
        drawFieldDecorations(g);

        // Draw particles (behind bullets)
        for (Particle p : particles) p.draw(g);

        // Draw items
        for (Item it : items) it.draw(g, FIELD_X);

        // Draw enemies
        for (Enemy en : enemies) en.draw(g, FIELD_X);

        // Draw boss
        if (boss != null) boss.draw(g, FIELD_X);

        // Draw enemy bullets
        for (Bullet b : enemyBullets) {
            if (!b.active) continue;
            Bullet displayB = new Bullet(b.x + FIELD_X, b.y, 0, 0, b.radius, b.color, false);
            displayB.coreColor = b.coreColor;
            displayB.draw(g);
        }

        // Draw player bullets
        for (Bullet b : playerBullets) {
            if (!b.active) continue;
            Bullet displayB = new Bullet(b.x + FIELD_X, b.y, 0, 0, b.radius, b.color, true);
            displayB.draw(g);
        }

        // Draw player
        player.draw(g, FIELD_X);

        g.setClip(oldClip);

        // Field border
        g.setColor(new Color(100, 100, 200));
        g.setStroke(new BasicStroke(2));
        g.drawRect(FIELD_X, FIELD_Y, FIELD_W, FIELD_H);
        g.setStroke(new BasicStroke(1));

        // HUD
        drawHUD(g);
    }

    private void drawFieldDecorations(Graphics2D g) {
        // Subtle grid
        g.setColor(new Color(20, 20, 50));
        for (int x = FIELD_X; x < FIELD_X + FIELD_W; x += 40)
            g.drawLine(x, FIELD_Y, x, FIELD_Y + FIELD_H);
        for (int y = FIELD_Y; y < FIELD_Y + FIELD_H; y += 40)
            g.drawLine(FIELD_X, y, FIELD_X + FIELD_W, y);
    }

    private void drawHUD(Graphics2D g) {
        int hx = HUD_X;

        // HUD background with gradient
        g.setColor(new Color(10, 10, 35));
        g.fillRect(hx - 8, FIELD_Y, TOTAL_W - hx + 8, FIELD_H);
        g.setColor(new Color(100, 150, 220));
        g.setStroke(new BasicStroke(2f));
        g.drawRect(hx - 8, FIELD_Y, TOTAL_W - hx + 8, FIELD_H);
        g.setStroke(new BasicStroke(1));

        // Title
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.setColor(new Color(100, 200, 255));
        g.drawString("HIGH SCORE", hx + 2, FIELD_Y + 18);
        g.setFont(new Font("Monospaced", Font.BOLD, 13));
        g.setColor(new Color(200, 220, 255));
        g.drawString("9999999", hx + 50, FIELD_Y + 18);

        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.setColor(new Color(100, 200, 255));
        g.drawString("SCORE", hx + 2, FIELD_Y + 40);
        g.setFont(new Font("Monospaced", Font.BOLD, 14));
        g.setColor(new Color(150, 220, 255));
        g.drawString(String.format("%08d", player.score), hx + 50, FIELD_Y + 40);

        // Separator
        g.setColor(new Color(100, 120, 180));
        g.setStroke(new BasicStroke(1.5f));
        g.drawLine(hx, FIELD_Y + 50, hx + 170, FIELD_Y + 50);
        g.setStroke(new BasicStroke(1));

        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        g.setColor(new Color(150, 180, 255));
        g.drawString("HEALTH", hx + 2, FIELD_Y + 70);
        drawLives(g, hx, FIELD_Y + 78);

        g.setColor(new Color(150, 180, 255));
        g.drawString("BOMBS", hx + 2, FIELD_Y + 115);
        drawBombs(g, hx, FIELD_Y + 123);

        g.setColor(new Color(100, 120, 180));
        g.setStroke(new BasicStroke(1.5f));
        g.drawLine(hx, FIELD_Y + 155, hx + 170, FIELD_Y + 155);
        g.setStroke(new BasicStroke(1));

        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        g.setColor(new Color(150, 180, 255));
        g.drawString("POWER", hx + 2, FIELD_Y + 175);
        drawPowerBar(g, hx, FIELD_Y + 183);

        g.setColor(new Color(150, 180, 255));
        g.drawString("GRAZE", hx + 2, FIELD_Y + 210);
        g.setColor(new Color(200, 220, 255));
        g.setFont(new Font("Monospaced", Font.BOLD, 12));
        g.drawString(String.format("%5d", player.graze), hx + 70, FIELD_Y + 210);

        g.setColor(new Color(100, 120, 180));
        g.setStroke(new BasicStroke(1.5f));
        g.drawLine(hx, FIELD_Y + 230, hx + 170, FIELD_Y + 230);
        g.setStroke(new BasicStroke(1));

        // Stage info
        g.setFont(new Font("SansSerif", Font.BOLD, 11));
        g.setColor(new Color(150, 180, 255));
        g.drawString("STAGE", hx + 2, FIELD_Y + 250);
        g.setColor(new Color(200, 220, 255));
        g.drawString(stage + " / 3", hx + 80, FIELD_Y + 250);

        // Difficulty
        g.setColor(new Color(150, 180, 255));
        g.drawString("DIFFICULTY", hx + 2, FIELD_Y + 267);
        g.setColor(DIFF_COLORS[selectedDiff]);
        g.setFont(new Font("SansSerif", Font.BOLD, 10));
        g.drawString(DIFF_NAMES[selectedDiff], hx + 80, FIELD_Y + 267);

        // Character
        g.setColor(new Color(150, 180, 255));
        g.setFont(new Font("SansSerif", Font.BOLD, 11));
        g.drawString("PLAYER", hx + 2, FIELD_Y + 285);
        g.setColor(new Color(200, 220, 255));
        g.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g.drawString(selectedChar == 0 ? "Aurora" : "Chronos", hx + 80, FIELD_Y + 285);

        // Boss info
        if (boss != null && boss.active) {
            g.setColor(new Color(100, 120, 180));
            g.setStroke(new BasicStroke(1.5f));
            g.drawLine(hx, FIELD_Y + 305, hx + 170, FIELD_Y + 305);
            g.setStroke(new BasicStroke(1));
            g.setFont(new Font("SansSerif", Font.BOLD, 11));
            g.setColor(new Color(255, 150, 200));
            g.drawString("BOSS", hx + 2, FIELD_Y + 323);
            g.setColor(new Color(220, 180, 255));
            g.setFont(new Font("SansSerif", Font.PLAIN, 9));
            String spellName = boss.getCurrentSpellName();
            drawWrapped(g, spellName, hx + 2, FIELD_Y + 335, 165, 11);
            // Phase dots
            for (int p = 0; p < boss.getTotalPhases(); p++) {
                int px2 = hx + 2 + p * 16;
                if (p < boss.getPhase()) g.setColor(new Color(60, 60, 80));
                else if (p == boss.getPhase()) g.setColor(new Color(255, 150, 200));
                else g.setColor(new Color(100, 100, 150));
                g.fillOval(px2, FIELD_Y + 352, 10, 10);
            }
        }

        // Experience section
        g.setColor(new Color(100, 120, 180));
        g.setStroke(new BasicStroke(1.5f));
        g.drawLine(hx, FIELD_Y + FIELD_H - 130, hx + 170, FIELD_Y + FIELD_H - 130);
        g.setStroke(new BasicStroke(1));

        g.setFont(new Font("SansSerif", Font.BOLD, 11));
        g.setColor(new Color(150, 180, 255));
        g.drawString("LEVEL " + player.exp.level, hx + 2, FIELD_Y + FIELD_H - 115);
        // EXP bar
        g.setColor(new Color(30, 30, 50));
        g.fillRect(hx + 2, FIELD_Y + FIELD_H - 108, 155, 8);
        float expRatio = (float)player.exp.getExpPercent() / 100f;
        g.setColor(new Color(100 + (int)(expRatio*155), 150, 255 - (int)(expRatio*100)));
        g.fillRect(hx + 2, FIELD_Y + FIELD_H - 108, (int)(155 * expRatio), 8);
        g.setColor(new Color(120, 150, 200));
        g.drawRect(hx + 2, FIELD_Y + FIELD_H - 108, 155, 8);

        // Skills display
        g.setFont(new Font("SansSerif", Font.BOLD, 8));
        int skillY = FIELD_Y + FIELD_H - 95;
        for (int sk = 0; sk < 5; sk++) {
            int level = player.exp.getSkillLevel(sk);
            String name = player.exp.getSkillName(sk);
            g.setColor(level > 0 ? new Color(150, 200, 255) : new Color(80, 80, 110));
            g.drawString(name.substring(0, Math.min(6, name.length())), hx + 2, skillY - sk*11);
            // Star rating
            for (int st = 0; st < 5; st++) {
                if (st < level) g.setColor(new Color(255, 200, 100));
                else g.setColor(new Color(60, 60, 80));
                g.fillPolygon(new int[]{hx+90+st*9, hx+93+st*9, hx+96+st*9},
                              new int[]{skillY-3-sk*11, skillY-8-sk*11, skillY-3-sk*11}, 3);
            }
        }

        // Controls reminder at bottom
        g.setFont(smallFont);
        g.setColor(new Color(100, 100, 130));
        g.drawString("Z: Shoot   X: Bomb", hx, FIELD_Y + FIELD_H - 15);
        g.drawString("Shift: Focus", hx, FIELD_Y + FIELD_H - 5);
    }

    private void drawLives(Graphics2D g, int x, int y) {
        for (int i = 0; i < 8; i++) {
            if (i < player.lives) {
                g.setColor(new Color(255, 80, 80));
                // Draw small character icon
                g.fillOval(x + i*18, y, 14, 14);
                g.setColor(new Color(255, 200, 180));
                g.fillOval(x + i*18 + 3, y+1, 8, 8);
            } else {
                g.setColor(new Color(50, 50, 70));
                g.drawOval(x + i*18, y, 14, 14);
            }
        }
    }

    private void drawBombs(Graphics2D g, int x, int y) {
        for (int i = 0; i < 8; i++) {
            if (i < player.bombs) {
                g.setColor(new Color(80, 150, 255));
                g.fillPolygon(star5(x + i*18 + 7, y + 7, 7));
            } else {
                g.setColor(new Color(50, 50, 70));
                g.drawPolygon(star5(x + i*18 + 7, y + 7, 7));
            }
        }
    }

    private void drawPowerBar(Graphics2D g, int x, int y) {
        int pw = 155;
        g.setColor(new Color(30, 30, 50));
        g.fillRect(x + 2, y, pw, 9);
        float ratio = player.power / 400f;
        Color pc = new Color(0.3f + ratio * 0.4f, 0.4f + ratio * 0.5f, 1f - ratio * 0.3f);
        g.setColor(pc);
        g.fillRect(x + 2, y, (int)(pw * ratio), 9);
        g.setColor(new Color(120, 150, 200));
        g.setStroke(new BasicStroke(1.2f));
        g.drawRect(x + 2, y, pw, 9);
        g.setStroke(new BasicStroke(1));
        g.setFont(new Font("Monospaced", Font.PLAIN, 9));
        g.setColor(new Color(180, 200, 220));
        g.drawString(String.format("%3d", player.power), x + pw + 10, y + 8);
    }

    private void drawWrapped(Graphics2D g, String text, int x, int y, int maxW, int lineH) {
        FontMetrics fm = g.getFontMetrics();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int cy = y;
        for (String w : words) {
            String test = line + (line.length() > 0 ? " " : "") + w;
            if (fm.stringWidth(test) > maxW && line.length() > 0) {
                g.drawString(line.toString(), x, cy);
                cy += lineH;
                line = new StringBuilder(w);
            } else { line = new StringBuilder(test); }
        }
        if (line.length() > 0) g.drawString(line.toString(), x, cy);
    }

    private void drawBorder(Graphics2D g) {
        // Outer frame
        g.setColor(new Color(80, 60, 20));
        g.fillRect(0, 0, TOTAL_W, 8);
        g.fillRect(0, TOTAL_H-8, TOTAL_W, 8);
        g.fillRect(0, 0, 8, TOTAL_H);
        g.fillRect(TOTAL_W-8, 0, 8, TOTAL_H);
        // Inner border line
        g.setColor(new Color(180, 140, 50));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRect(8, 8, TOTAL_W-17, TOTAL_H-17);
        // Corner ornaments
        for (int[] c : new int[][]{{10,10},{TOTAL_W-30,10},{10,TOTAL_H-30},{TOTAL_W-30,TOTAL_H-30}}) {
            g.fillOval(c[0], c[1], 20, 20);
            g.setColor(new Color(220, 180, 80));
            g.drawOval(c[0], c[1], 20, 20);
            g.setColor(new Color(180, 140, 50));
        }
        g.setStroke(new BasicStroke(1));
    }

    private void drawStageClear(Graphics2D g) {
        float alpha = Math.min(1f, (180f - stageClearTimer) / 30f);
        g.setColor(new Color(0f, 0f, 0f, alpha * 0.5f));
        g.fillRect(0, 0, TOTAL_W, TOTAL_H);
        g.setFont(new Font("Serif", Font.BOLD, 32));
        FontMetrics fm = g.getFontMetrics();
        String msg = "Stage " + stage + " Clear!";
        g.setColor(new Color(1f, 0.9f, 0.3f, alpha));
        g.drawString(msg, TOTAL_W/2 - fm.stringWidth(msg)/2, TOTAL_H/2);
        g.setFont(new Font("Serif", Font.PLAIN, 16));
        fm = g.getFontMetrics();
        String bonus = "Spell Bonus: " + (boss != null ? 0 : 10000 * (selectedDiff+1));
        g.setColor(new Color(1f, 0.8f, 0.5f, alpha));
        g.drawString(bonus, TOTAL_W/2 - fm.stringWidth(bonus)/2, TOTAL_H/2 + 35);
    }

    private void drawGameOver(Graphics2D g) {
        drawBorder(g);
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, TOTAL_W, TOTAL_H);

        boolean win = stage > 3;
        g.setFont(new Font("Serif", Font.BOLD, 40));
        FontMetrics fm = g.getFontMetrics();
        String title = win ? "VICTORY!" : "MISSION FAILED";
        Color tc = win ? new Color(100, 220, 255) : new Color(255, 100, 100);
        // Shadow
        g.setColor(new Color(tc.getRed()/4, tc.getGreen()/4, tc.getBlue()/4));
        g.drawString(title, TOTAL_W/2 - fm.stringWidth(title)/2 + 3, 223);
        g.setColor(tc);
        g.drawString(title, TOTAL_W/2 - fm.stringWidth(title)/2, 220);

        // Score summary
        g.setFont(new Font("Serif", Font.PLAIN, 18));
        fm = g.getFontMetrics();
        String scoreStr = "Final Score: " + String.format("%,d", player != null ? player.score : 0);
        g.setColor(new Color(220, 220, 255));
        g.drawString(scoreStr, TOTAL_W/2 - fm.stringWidth(scoreStr)/2, 280);

        if (player != null) {
            String[] stats = {
                "Character: " + CHAR_NAMES[selectedChar],
                "Difficulty: " + DIFF_NAMES[selectedDiff],
                "Graze: " + player.graze,
                "Stage Reached: " + Math.min(stage, 3) + " / 3"
            };
            g.setFont(new Font("SansSerif", Font.PLAIN, 14));
            fm = g.getFontMetrics();
            for (int i = 0; i < stats.length; i++) {
                g.setColor(new Color(180, 180, 220));
                g.drawString(stats[i], TOTAL_W/2 - fm.stringWidth(stats[i])/2, 320 + i * 25);
            }
        }

        // Menu options
        String[] opts = { "RETRY", "MAIN MENU" };
        for (int i = 0; i < opts.length; i++) {
            boolean sel = menuCursor == i;
            g.setFont(new Font("Serif", Font.BOLD, 18));
            fm = g.getFontMetrics();
            int ox = TOTAL_W/2 - fm.stringWidth(opts[i])/2;
            int oy = 440 + i * 40;
            if (sel) {
                g.setColor(new Color(100, 200, 255, 50));
                g.fillRoundRect(ox - 15, oy - 18, fm.stringWidth(opts[i]) + 30, 26, 6, 6);
                g.setColor(new Color(100, 200, 255));
            } else {
                g.setColor(new Color(180, 180, 220));
            }
            g.drawString(opts[i], ox, oy);
        }

        g.setFont(smallFont);
        g.setColor(new Color(120, 120, 140));
        g.drawString("↑↓: Select    Z: Confirm", TOTAL_W/2 - 70, TOTAL_H - 20);
    }

    // ---- KEY EVENTS ----
    @Override
    public void keyPressed(KeyEvent e) {
        int k = e.getKeyCode();
        if (k < keys.length) keys[k] = true;
        handleMenuKeys(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int k = e.getKeyCode();
        if (k < keys.length) keys[k] = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    private void handleMenuKeys(KeyEvent e) {
        int k = e.getKeyCode();
        switch (state) {
            case START:
                if (k == KeyEvent.VK_UP)   menuCursor = (menuCursor - 1 + 3) % 3;
                if (k == KeyEvent.VK_DOWN) menuCursor = (menuCursor + 1) % 3;
                if (k == KeyEvent.VK_Z) {
                    if (menuCursor == 0) { menuCursor = 0; state = GameState.CHARACTER_SELECT; }
                    else if (menuCursor == 2) System.exit(0);
                }
                break;
            case CHARACTER_SELECT:
                if (k == KeyEvent.VK_LEFT)  selectedChar = 0;
                if (k == KeyEvent.VK_RIGHT) selectedChar = 1;
                if (k == KeyEvent.VK_Z) state = GameState.DIFFICULTY_SELECT;
                if (k == KeyEvent.VK_X) state = GameState.START;
                break;
            case DIFFICULTY_SELECT:
                if (k == KeyEvent.VK_UP)   selectedDiff = Math.max(0, selectedDiff - 1);
                if (k == KeyEvent.VK_DOWN) selectedDiff = Math.min(3, selectedDiff + 1);
                if (k == KeyEvent.VK_Z) startGame();
                if (k == KeyEvent.VK_X) state = GameState.CHARACTER_SELECT;
                break;
            case PLAYING:
                if (k == KeyEvent.VK_ESCAPE) state = GameState.GAME_OVER; // Quick exit for now
                break;
            case GAME_OVER:
                if (k == KeyEvent.VK_UP)   menuCursor = (menuCursor - 1 + 2) % 2;
                if (k == KeyEvent.VK_DOWN) menuCursor = (menuCursor + 1) % 2;
                if (k == KeyEvent.VK_Z) {
                    if (menuCursor == 0) startGame();
                    else { state = GameState.START; menuCursor = 0; }
                }
                break;
        }
    }
}

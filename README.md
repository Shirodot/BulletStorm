# Bullet Storm - Danmaku Game

A high-performance bullet hell game in pure Java with RPG progression, leveling, and skill upgrades.

## Features

### Game Progression
- **Experience System**: Earn EXP from enemies, graze, items, and bosses
- **Level-Up System**: Unlock 5 skill upgrades automatically as you level
- **Skill Upgrades** (5 total):
  - Shot Speed (↑ 15% per level)
  - Fire Rate (↑ 2 frames faster per level)
  - Max HP (↑ 1 life per level)
  - Bomb Power (↑ 20% damage per level)
  - Graze Range (↑ 10% radius per level)

### Game Modes
- **3 Stages** with progressive difficulty
- **Boss Fights** with 5 attack phases each
- **Character Selection**: Aurora (homing shots) vs Chronos (beam barrages)
- **4 Difficulty Levels**: Training → Expert → Insane

### UI/UX Optimization
- **Modern Clean Interface**: Blue/cyan color scheme, geometric design
- **Right-side HUD**: Score, health, bombs, power, stage info
- **Real-time Statistics**: Graze counter, current spell card display
- **Minimalist Aesthetics**: No unnecessary clutter or dated styling

### Bullet Patterns (12 algorithms)
1. Circular burst rings
2. Aimed spreads
3. Spiral formations
4. Double rings
5. Alternating fast/slow
6. Homing bullets
7. Wave patterns
8. Spinning rings
9. Accelerating bullets
10. Butterfly sprays
11. Laser barrages
12. Petal formations

### Enemy System
- **Auto-Despawn**: Enemies automatically vanish after 8-12 seconds
- **Staggered Spawning**: Enemies spawn with 15-frame intervals (no crowding)
- **Wave Patterns**: 4 different formation types (line, V, sides, diagonal)
- **Progressive AI**: Enemies patrol and shoot patterns based on type

### Bullet Balance
- **Reduced bullet count** per attack (6-10 bullets per spawn)
- **Increased spawn intervals** (70-100 frames between attacks)
- **Lower baseline speeds** (1.8-2.5 units/frame)
- **Difficulty scaling**: Easy/Normal/Hard/Lunatic all balanced

## Controls

| Input | Action |
|-------|--------|
| **Arrow Keys** | Move character |
| **Z** | Shoot / Confirm menu |
| **X** | Deploy bomb / Back |
| **Shift** | Focus mode (slow + visible hitbox) |
| **Esc** | Quit to game over |

## Game Mechanics

### Power System
- Collect power items (magenta diamonds) dropped by enemies
- Max power: 400 (unlocks weapon upgrades)
- Power bar visible in right HUD panel

### Graze Mechanic
- Near-miss with bullets (within 8 pixels)
- Awards 10 points per graze
- Counter shown in real-time

### Lives & Bombs
- Start with 3 lives, gain extra from white items
- Start with 3 bombs to clear screen
- Bomb clears all enemy bullets + grants invulnerability

### Scoring & Experience
**Score**:
- Enemy defeat: 100-1000 points × (difficulty + 1)
- Point items: 500 × (power level)
- Boss defeat: 50,000 × (difficulty + 1)
- Graze: 10 points each

**Experience**:
- Enemy defeat: 50-150 EXP × (difficulty + 1)
- Boss hit: 100 EXP × (difficulty + 1)
- Boss defeat: 5,000 EXP × (difficulty + 1)
- Graze: 5 EXP each
- Item collection: 20-100 EXP

**Leveling**:
- Base: 1,000 EXP to reach level 2
- Growth: +500 EXP per level (2,000 for level 3, 2,500 for level 4, etc.)
- Auto-upgrade: Random skill selected when leveling up (or manually upgradeable)

## Running the Game

**Compile:**
```bash
javac -cp src -d out src/*.java
```

**Run:**
```bash
java -cp out Main
```

Or simply execute `build.bat` on Windows.

## Customization

### Adjust Bullet Difficulty
In `Enemy.java` and `Boss.java`:
- Modify `shootInterval` for spawn rate
- Change bullet count in pattern calls
- Adjust speeds via `speedMult`

### Change Colors
Edit `Color` instances in:
- `Enemy.java`: Enemy colors
- `Boss.java`: Boss phase colors
- `GamePanel.java`: UI theme

### Modify Patterns
Edit methods in `BulletPattern.java` to create new attack formations.

## Architecture

- **Player.java** - Character with dual shot patterns, Experience integration
- **Enemy.java** - 3 enemy types with staggered spawn and auto-despawn
- **Boss.java** - 5-phase boss with spell card system
- **Bullet.java** - Physics and movement types
- **BulletPattern.java** - 12 procedural pattern algorithms
- **Item.java** - Collectible items with Experience rewards
- **Particle.java** - Explosion and visual effects
- **ExperienceSystem.java** - Level-up, skill upgrades, progression tracking
- **GamePanel.java** - Main game loop, rendering, input, UI

## System Requirements

- Java 8+
- ~50 MB RAM
- Keyboard input

---

**Version**: 1.0  
**Built in**: Pure Java (no external libraries)  
**Frame Rate**: ~60 FPS

# Bullet Storm - Feature Summary

## ✨ New Features in Latest Update

### 1. Enemy Management
- ✅ **Auto-Despawn**: Enemies disappear after 8-12 seconds (Fairy/Youkai/Mini-boss)
- ✅ **Staggered Spawning**: Enemies appear with 15-frame intervals, no pileups
- ✅ **Smart Culling**: Out-of-field enemies automatically removed

### 2. Experience System (NEW)
```
Level 1 → Level 2: 1,000 EXP
Level 2 → Level 3: 1,500 EXP
Level 3 → Level 4: 2,000 EXP
... (+500 per level)
```

**EXP Rewards**:
| Source | EXP Gained |
|--------|-----------|
| Fairy defeated | 50 (Easy) - 200 (Hard) |
| Youkai defeated | 100 - 400 |
| Mini-boss defeated | 150 - 600 |
| Graze (near-miss) | 5 |
| Power item collected | 20 |
| Point item collected | 50 |
| Life item collected | 100 |
| Enemy bullet hit | 100× difficulty |
| Boss defeated | 5,000× difficulty |

### 3. Skill Upgrade System
Automatically unlock skills as you level up. Each skill has 5 levels.

**Skill Effects**:

| Skill | Effect per Level | Max Benefit |
|-------|------------------|-------------|
| **Shot Speed** | +15% bullet velocity | +75% faster shots |
| **Fire Rate** | -2 frames cooldown | 10 frames faster |
| **Max HP** | +1 extra life | +5 lives (8 total) |
| **Bomb Power** | +20% damage | +100% bomb effectiveness |
| **Graze Range** | +10% detection radius | +50% wider graze zone |

### 4. UI Enhancements
Right-side HUD now displays:
- Level and EXP progress bar
- 5 skill upgrade indicators with star ratings
- Real-time progression feedback

### 5. Game Balance Improvements

**Enemy Spawn Changes**:
- Fairies: 480 frame lifespan
- Youkai: 600 frame lifespan
- Mini-bosses: 720 frame lifespan
- Spawn interval: 15 frames between each enemy

**Attack Pattern Changes**:
- Reduced base bullet count by 30-40%
- Increased attack cooldowns by 50-70%
- Lower initial bullet speeds
- Scales with difficulty modifier

## 💡 Strategy Tips

### Progression
1. Focus on **Fire Rate** early for weapon efficiency
2. Upgrade **Shot Speed** for better damage output
3. Get **Max HP** when facing dangerous sections
4. **Graze Range** helps at higher difficulties

### Resource Management
- Collect power items → Auto-level → Better shots
- Graze often → Free EXP → Level faster
- Dodge patterns → Survive longer → Kill more enemies

### Difficulty Scaling
- **Easy**: 1.0× EXP, balanced bullet patterns
- **Normal**: 2.0× EXP, faster attacks
- **Hard**: 3.0× EXP, dense patterns
- **Lunatic**: 4.0× EXP, extreme difficulty

## 🎮 Controls Reference
| Key | Action |
|-----|--------|
| ↑↓←→ | Move |
| Z | Shoot / Confirm |
| X | Bomb / Back |
| Shift | Focus mode |
| Esc | Quit |

## 📊 Game Statistics
- **Total Enemy Types**: 3
- **Bullet Patterns**: 12 unique algorithms
- **Boss Phases**: 5 per boss × 2 bosses = 10 unique battles
- **Skill Upgrades**: 5 skills × 5 levels = 25 upgrade levels
- **Max Level**: 99 (theoretical cap)
- **Maximum Lives**: 8 (with level 5 HP upgrade)

## 🚀 Performance
- ~60 FPS gameplay
- 200+ bullets managed simultaneously
- Smooth anti-aliased graphics
- Efficient collision detection

---

**Version**: 1.0 with RPG Progression
**Build**: Java Swing
**Release**: 2024

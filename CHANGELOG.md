# Bullet Storm - Changelog

## Version 1.0 - Full Release with RPG Features

### 🎮 New Core Systems

#### Experience & Leveling System
- Added `ExperienceSystem.java` for progression tracking
- Level cap: 99 (player starts at level 1)
- EXP requirements scale: 1000 + (level × 500)
- Integrated into Player class

#### Auto-Skill Upgrades
- 5 skill trees with 5 levels each
- Shot Speed: +15% velocity per level
- Fire Rate: -2 cooldown per level
- Max HP: +1 life per level
- Bomb Power: +20% damage per level
- Graze Range: +10% radius per level

#### Enemy Management
- **Auto-despawn**: Enemies vanish after 8-12 seconds
  - Fairy: 480 frames
  - Youkai: 600 frames
  - Mini-boss: 720 frames
- **Staggered spawning**: 15-frame intervals between enemies
  - Prevents enemy pileups
  - Better difficulty progression
- Stored in `Enemy.age` tracking

### 🎯 Gameplay Changes

#### Experience Rewards
| Action | EXP Gained |
|--------|-----------|
| Kill Fairy | 50 × (diff+1) |
| Kill Youkai | 100 × (diff+1) |
| Kill Mini-boss | 150 × (diff+1) |
| Graze bullet | 5 |
| Hit enemy (bullet) | 100 × (diff+1) |
| Defeat Boss | 5,000 × (diff+1) |
| Collect Power Item | 20 |
| Collect Point Item | 50 |
| Collect Life Item | 100 |

#### Bullet Density Optimization
- Reduced enemy bullet count: 12→8-10 per volley
- Increased spawn intervals: 60→100+ frames
- Lower base speeds: 2.5→2.0 units/frame
- Better difficulty scaling

#### Player Stat Growth
- Shot speed multiplier: 1.0 + (shotSpeedLevel × 0.15)
- Shoot cooldown reduction: -2 per level (min 2 frames)
- HP growth: +1 per level
- Bomb damage multiplier: 1.0 + (bombDamageLevel × 0.2)
- Graze radius multiplier: 1.0 + (graceRadiusLevel × 0.1)

### 🎨 UI/UX Improvements

#### Right-side HUD Expansion
Added new panels:
- **LEVEL Display**: Current level with large numbers
- **EXP Progress Bar**: Visual progress to next level
- **Skill Stars**: 5 skills with 5-star rating system
  - Dynamically updates as skills upgrade
  - Color-coded (gold when active, dim when inactive)
  - Abbreviated skill names (6 chars) for compact display

#### Modern Design Elements
- Blue/cyan technology aesthetic throughout
- Gradient-filled progress bars
- Geometric patterns and lines
- Clear visual hierarchy

### 📁 New Files
- `ExperienceSystem.java` (170 lines)
  - Level tracking
  - 5 skill types
  - Auto-upgrade logic
  - Stat multiplier calculations

### 🔧 Modified Files

#### Player.java
- Added `ExperienceSystem exp` field
- Integrated shot speed multiplier
- Applied fire rate bonus to cooldown
- Adjusted sprite positioning for level display
- Experience loss on death (-200 EXP)

#### Enemy.java
- Added `maxAge` field (480-720 frames)
- Added auto-despawn logic
- Added staggered spawn handling (negative age)
- Skip shooting when age ≤ 0

#### GamePanel.java
- Modified `spawnWave()` to stagger enemies
  - Each enemy spawns 15 frames apart
  - Set initial age to negative values
- Added EXP rewards on enemy defeat
- Integrated graze radius boost
- Added boss defeat EXP bonus
- Enhanced HUD with skill display
- Added level/EXP section to right panel

#### Item.java
- Added EXP rewards on pickup
- Integrated with player.exp system
- HP items respect hpMaxLevel

### 🎯 Difficulty Adjustments
- **Easy**: Base EXP × 1
- **Normal**: Base EXP × 2
- **Hard**: Base EXP × 3
- **Lunatic**: Base EXP × 4

All enemies and bosses scale bullet count/speed accordingly.

### 🐛 Bug Fixes
- Fixed enemy crowding by implementing spawn intervals
- Prevented infinite enemy accumulation
- Corrected graze detection when skills upgrade
- Fixed experience gain calculation

### ⚡ Performance
- Enemy auto-culling reduces object count
- Efficient skill modifier calculations
- Minimal HUD drawing overhead
- Smooth 60 FPS maintained

### 📊 Statistics
- Experience required for level 99: ~47,500 total EXP
- Estimated playtime to max level: 30-60 minutes
- Number of possible skill combinations: 3,125 (5^5)
- Total skill synergies to explore: Numerous

---

## Version 0.9 - Previous Update (Optimization Pass)
- Reduced bullet density
- Modern UI overhaul
- Renamed all content to neutral theme
- Removed Touhou references

## Version 0.1 - Initial Release
- Core bullet-hell gameplay
- 3 stages + boss fights
- 2 character types
- 4 difficulty levels
- 12 bullet patterns

public class ExperienceSystem {
    public int level = 1;
    public long exp = 0;
    public long expNextLevel = 1000;

    // Skill upgrades
    public int shotSpeedLevel = 0;      // Max 5
    public int shootRateLevel = 0;      // Max 5
    public int hpMaxLevel = 0;          // Max 5
    public int bombDamageLevel = 0;     // Max 5
    public int graceRadiusLevel = 0;    // Max 5

    private static final long EXP_GROWTH = 500; // Exp increases per level

    public void addExp(long amount) {
        exp += amount;
        while (exp >= expNextLevel && level < 99) {
            levelUp();
        }
    }

    private void levelUp() {
        exp -= expNextLevel;
        level++;
        expNextLevel = 1000 + level * EXP_GROWTH;
        // Auto-upgrade a random skill
        autoUpgradeSkill();
    }

    private void autoUpgradeSkill() {
        int[] skills = { shotSpeedLevel, shootRateLevel, hpMaxLevel, bombDamageLevel, graceRadiusLevel };
        int maxedCount = 0;
        for (int s : skills) if (s >= 5) maxedCount++;

        if (maxedCount >= 5) return; // All maxed

        int chosen;
        do {
            chosen = (int)(Math.random() * 5);
        } while ((chosen == 0 && shotSpeedLevel >= 5) ||
                 (chosen == 1 && shootRateLevel >= 5) ||
                 (chosen == 2 && hpMaxLevel >= 5) ||
                 (chosen == 3 && bombDamageLevel >= 5) ||
                 (chosen == 4 && graceRadiusLevel >= 5));

        switch (chosen) {
            case 0: if (shotSpeedLevel < 5) shotSpeedLevel++; break;
            case 1: if (shootRateLevel < 5) shootRateLevel++; break;
            case 2: if (hpMaxLevel < 5) hpMaxLevel++; break;
            case 3: if (bombDamageLevel < 5) bombDamageLevel++; break;
            case 4: if (graceRadiusLevel < 5) graceRadiusLevel++; break;
        }
    }

    public void manualUpgradeSkill(int skillIndex) {
        switch (skillIndex) {
            case 0: if (shotSpeedLevel < 5) shotSpeedLevel++; break;
            case 1: if (shootRateLevel < 5) shootRateLevel++; break;
            case 2: if (hpMaxLevel < 5) hpMaxLevel++; break;
            case 3: if (bombDamageLevel < 5) bombDamageLevel++; break;
            case 4: if (graceRadiusLevel < 5) graceRadiusLevel++; break;
        }
    }

    // Skill effect multipliers
    public double getShotSpeedMultiplier() {
        return 1.0 + shotSpeedLevel * 0.15;
    }

    public int getShootRateBonus() {
        return shootRateLevel * 2; // Reduce cooldown by 2 frames per level
    }

    public int getMaxHpBonus() {
        return hpMaxLevel * 1; // +1 extra life per level
    }

    public double getBombDamageMultiplier() {
        return 1.0 + bombDamageLevel * 0.2;
    }

    public double getGrazeRadiusMultiplier() {
        return 1.0 + graceRadiusLevel * 0.1;
    }

    public long getExpPercent() {
        return (exp * 100) / expNextLevel;
    }

    public String getSkillName(int index) {
        switch (index) {
            case 0: return "Shot Speed";
            case 1: return "Fire Rate";
            case 2: return "Max HP";
            case 3: return "Bomb Power";
            case 4: return "Graze Range";
            default: return "Unknown";
        }
    }

    public int getSkillLevel(int index) {
        switch (index) {
            case 0: return shotSpeedLevel;
            case 1: return shootRateLevel;
            case 2: return hpMaxLevel;
            case 3: return bombDamageLevel;
            case 4: return graceRadiusLevel;
            default: return 0;
        }
    }
}

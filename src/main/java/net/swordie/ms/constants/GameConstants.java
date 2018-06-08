package net.swordie.ms.constants;

import net.swordie.ms.enums.BaseStat;
import net.swordie.ms.enums.EnchantStat;
import net.swordie.ms.enums.ItemJob;
import net.swordie.ms.util.container.Triple;
import net.swordie.ms.util.container.Tuple;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 1/23/2018.
 */
public class GameConstants {
    public static final int EXP_RATE = 10;
    public static final long MAX_MONEY = 10_000_000_000L;
    public static final int DROP_HEIGHT = 100; // was 20
    public static final int DROP_DIFF = 25;
    public static final int DROP_REMAIN_ON_GROUND_TIME = 120; // 2 minutes
    public static final int DROP_REMOVE_OWNERSHIP_TIME = 30; // 30 sec
    public static final short DAMAGE_SKIN_MAX_SIZE = 100;
    public static final int MAX_PET_AMOUNT = 3;
    public static final int BASE_MOB_RESPAWN_RATE = 5000; // In milliseconds
    public static final int MIN_MONEY_MULT = 6;
    public static final int MAX_MONEY_MULT = 9;

    //Combo Kill
    public static final int COMBO_KILL_RESET_TIMER = 5; // 5 sec
    public static final int COMBO_KILL_REWARD_BLUE = 50; // Combo kills
    public static final int COMBO_KILL_REWARD_PURPLE = 350; // Combo kills
    public static final int COMBO_KILL_REWARD_RED = 750; // Combo kills

    //Inner Ability
    public static final int CHAR_POT_BASE_ID = 70000000;
    public static final int CHAR_POT_END_ID = 70000062;
    public static final int BASE_CHAR_POT_UP_RATE = 10; // 10%
    public static final int BASE_CHAR_POT_DOWN_RATE = 10; // 10%
    public static final int CHAR_POT_RESET_COST = 100;
    public static final int CHAR_POT_GRADE_LOCK_COST = 10000;
    public static final int CHAR_POT_LOCK_1_COST = 3000;
    public static final int CHAR_POT_LOCK_2_COST = 5000;

    //Potential Chance on Drop Equips
    public static final int RANDOM_EQUIP_UNIQUE_CHANCE = 1; // out of a 100
    public static final int RANDOM_EQUIP_EPIC_CHANCE = 3; // out of a 100
    public static final int RANDOM_EQUIP_RARE_CHANCE = 8; // out of a 100

    //Rune
    public static final int RUNE_RESPAWN_TIME = 5; // minutes
    public static final int RUNE_COOLDOWN_TIME = 4; // minutes
    public static final int THUNDER_RUNE_ATTACK_DELAY = 4; // seconds
    public static final int DARKNESS_RUNE_NUMBER_OF_ELITE_MOBS_SPAWNED = 3; // number of elites spawned when activating Rune of Darkness

    //BurningField
    public static final int BURNING_FIELD_MAX_LEVEL = 10; //Maximum Burning Field Level
    public static final int BURNING_FIELD_LEVEL_ON_START = BURNING_FIELD_MAX_LEVEL; //Starts Burning Maps at BurningLevel 10
    public static final int BURNING_FIELD_TIMER = 10; // minutes
    public static final int BURNING_FIELD_MIN_MOB_LEVEL = 0; //Minimum Mob Level for the Field to become a Burning Field
    public static final int BURNING_FIELD_BONUS_EXP_MULTIPLIER_PER_LEVEL = 10; // multiplied by the BurningField Level  =  Bonus Exp% given

    //Exp Orb
    public static final int BLUE_EXP_ORB_ID = 2023484;
    public static final double BLUE_EXP_ORB_MULT = 2;
    public static final int PURPLE_EXP_ORB_ID = 2023494;
    public static final double PURPLE_EXP_ORB_MULT = 3.5;
    public static final int RED_EXP_ORB_ID = 2023495;
    public static final double RED_EXP_ORB_MULT = 5;

    // Elite mob
    public static final int ELITE_MOB_SKILL_COUNT = 2;
    public static final int ELITE_MOB_RESPAWN_TIME = 3; // seconds
    public static final int ELITE_MOB_SPAWN_CHANCE = 500; // out of a 1000
    public static final int ELITE_MOB_DARK_NOTIFICATION = 2;
    public static final int ELITE_BOSS_REQUIRED_KILLS = 5;
    public static final Integer[] ELITE_BOSS_TEMPLATES = new Integer[]{9303130, 9303131, 9303132, 9303133, 9303134, // 2 types, easy/hard I think
            9303135, 9303136, 9303137, 9303138, 9303139};
    public static final String ELITE_BOSS_BGM = "Bgm45/Anthem For Heroes";
    public static final long ELITE_BOSS_HP_RATE = 500; // multiplier for boss' hp compared to the mobs on the map


    public static long[] charExp = new long[251];
    private static int[][] enchantSuccessRates = new int[25][2];

    static {
        initCharExp();
        initEnchantRates();
    }

    private static void initCharExp() {
        // NEXTLEVEL::NEXTLEVEL
        charExp[1] = 15;
        charExp[2] = 32;
        charExp[3] = 57;
        charExp[4] = 92;
        charExp[5] = 135;
        charExp[6] = 372;
        charExp[7] = 560;
        charExp[8] = 840;
        charExp[9] = 1242;
        for (int i = 10; i <= 14; i++) {
            charExp[i] = charExp[i-1];
        }
        for(int i = 15; i <= 29; i++) {
            charExp[i] = (long) (charExp[i-1] * 1.2);
        }
        for(int i = 30; i <= 34; i++) {
            charExp[i] = charExp[i-1];
        }
        for(int i = 35; i <= 39; i++) {
            charExp[i] = (long) (charExp[i-1] * 1.2);
        }
        for(int i = 40; i <= 59; i++) {
            charExp[i] = (long) (charExp[i-1] * 1.08);
        }
        for(int i = 60; i <= 64; i++) {
            charExp[i] = charExp[i-1];
        }
        for(int i = 65; i <= 74; i++) {
            charExp[i] = (long) (charExp[i-1] * 1.08);
        }
        for(int i = 75; i <= 99; i++) {
            charExp[i] = (long) (charExp[i-1] * 1.07);
        }
        for(int i = 100; i <= 104; i++) {
            charExp[i] = charExp[i-1];
        }
        for(int i = 105; i <= 159; i++) {
            charExp[i] = (long) (charExp[i-1] * 1.07);
        }
        for(int i = 160; i <= 199; i++) {
            charExp[i] = (long) (charExp[i-1] * 1.06);
        }
        charExp[200] = charExp[199] * 2;
        for(int i = 201; i <= 209; i++) {
            charExp[i] = (long) (charExp[i-1] * 1.2);
        }
        charExp[210] = (long) (charExp[209] * 1.06 * 2);
        for(int i = 211; i <= 219; i++) {
            charExp[i] = (long) (charExp[i-1] * 1.06);
        }
        charExp[220] = (long) (charExp[219] * 1.04 * 2);
        for(int i = 221; i <= 229; i++) {
            charExp[i] = (long) (charExp[i-1] * 1.04);
        }
        charExp[230] = (long) (charExp[229] * 1.02 * 2);
        for(int i = 231; i <= 239; i++) {
            charExp[i] = (long) (charExp[i-1] * 1.02);
        }
        charExp[240] = (long) (charExp[239] * 1.01 * 2);
        for(int i = 241; i <= 249; i++) {
            charExp[i] = (long) (charExp[i-1] * 1.01);
        }
    }

    private static void initEnchantRates() {
        // kms rates: success / destroy
        // out of 1000
        enchantSuccessRates = new int[][]{
                {950, 0},
                {900, 0},
                {850, 0},
                {850, 0},
                {800, 0},

                {750, 0},
                {700, 0},
                {650, 0},
                {600, 0},
                {550, 0},

                {450, 0},
                {350, 0},
                {300, 7},
                {300, 14},
                {300, 14},

                {300, 21},
                {300, 21},
                {300, 21},
                {300, 28},
                {300, 28},

                {300, 70},
                {300, 70},
                {30, 194},
                {20, 294},
                {10, 396},
        };
    }

    public static long getEnchantmentMesoCost(int reqLevel, int chuc) {
        if (chuc < 10) {
            return (long) (1000 + Math.pow(reqLevel, 3) * (chuc + 1) / 25);
        } else if (chuc < 15) {
            return (long) (1000 + Math.pow(reqLevel, 3) * Math.pow(chuc + 1, 2.7) / 400);
        } else {
            return (long) (1000 + Math.pow(reqLevel, 3) * Math.pow(chuc + 1, 2.7) / 200);
        }
    }

    public static int getEnchantmentSuccessRate(short chuc) {
        if(chuc < 0 || chuc > 24) {
            return 0;
        }
        return enchantSuccessRates[chuc][0];
    }

    public static int getEnchantmentDestroyRate(short chuc) {
        if(chuc < 0 || chuc > 24) {
            return 0;
        }
        return enchantSuccessRates[chuc][1];
    }

    public static int getEnchantmentValByChuc(EnchantStat es, short chuc, int curAmount) {
        // TODO implement formula for this, depending on stat + weapon type
        return chuc + 1 + (curAmount / 50);
    }

    public static BaseStat getMainStatForJob(short job) {
        if (JobConstants.isBeginnerJob(job) || JobConstants.isBuccaneer(job) || JobConstants.isAdventurerPirate(job)
                || JobConstants.isPinkBean(job) || JobConstants.isDawnWarrior(job) || JobConstants.isKaiser(job)
                || JobConstants.isZero(job) || JobConstants.isDemon(job)
                || JobConstants.isDemonSlayer(job) || JobConstants.isAran(job) || JobConstants.isCannonShooter(job)
            || JobConstants.isDarkKnight(job) || JobConstants.isHero(job) || JobConstants.isPage(job)
            || JobConstants.isBlaster(job) || JobConstants.isHayato(job) || JobConstants.isMihile(job)
            || JobConstants.isShade(job) || JobConstants.isThunderBreaker(job) || JobConstants.isAdventurerWarrior(job)) {
            return BaseStat.str;
        } else if (JobConstants.isJett(job) || JobConstants.isCorsair(job) || JobConstants.isWildHunter(job)
                || JobConstants.isMercedes(job) || JobConstants.isAngelicBuster(job) || JobConstants.isWindArcher(job)
                || JobConstants.isAdventurerArcher(job)) {
            return BaseStat.dex;
        } else if (JobConstants.isBeastTamer(job) || JobConstants.isBlazeWizard(job) || JobConstants.isCleric(job)
                || JobConstants.isEvan(job) || JobConstants.isIceLightning(job) || JobConstants.isFirePoison(job)
                || JobConstants.isAdventurerMage(job) || JobConstants.isKanna(job) || JobConstants.isBlazeWizard(job)
                || JobConstants.isKenesis(job) || JobConstants.isLuminous(job)) {
            return BaseStat.inte;
        } else if (JobConstants.isAdventurerThief(job) || JobConstants.isNightLord(job) || JobConstants.isShadower(job)
                || JobConstants.isPhantom(job) || JobConstants.isNightWalker(job) || JobConstants.isDualBlade(job)) {
            return BaseStat.luk;
        } else if (JobConstants.isDemonAvenger(job)) {
            return BaseStat.mhp;
        }
        return null;
    }

    public static ItemJob getItemJobByJob(int jobArg) {
        short job = (short) jobArg;
        if (JobConstants.isPinkBean(job) || JobConstants.isDawnWarrior(job) || JobConstants.isKaiser(job)
                || JobConstants.isZero(job) || JobConstants.isDemon(job) || JobConstants.isDemonSlayer(job)
                || JobConstants.isAran(job) || JobConstants.isDarkKnight(job) || JobConstants.isHero(job)
                || JobConstants.isPage(job) || JobConstants.isBlaster(job) || JobConstants.isHayato(job)
                || JobConstants.isMihile(job) || JobConstants.isAdventurerWarrior(job)) {
            return ItemJob.WARRIOR;
        }
        if (JobConstants.isWildHunter(job) || JobConstants.isMercedes(job) || JobConstants.isWindArcher(job) ||
                JobConstants.isAdventurerArcher(job)) {
            return ItemJob.BOWMAN;
        }
        if (JobConstants.isBeastTamer(job) || JobConstants.isBlazeWizard(job) || JobConstants.isCleric(job)
                || JobConstants.isEvan(job) || JobConstants.isIceLightning(job) || JobConstants.isFirePoison(job)
                || JobConstants.isAdventurerMage(job) || JobConstants.isKanna(job) || JobConstants.isBlazeWizard(job)
                || JobConstants.isKenesis(job) || JobConstants.isLuminous(job)) {
            return ItemJob.MAGICIAN;
        }
        if (JobConstants.isAdventurerThief(job) || JobConstants.isNightLord(job) || JobConstants.isShadower(job)
                || JobConstants.isPhantom(job) || JobConstants.isNightWalker(job) || JobConstants.isDualBlade(job)) {
            return ItemJob.THIEF;
        }
        if (JobConstants.isBuccaneer(job) || JobConstants.isAdventurerPirate(job) || JobConstants.isCannonShooter(job)
                || JobConstants.isShade(job) || JobConstants.isThunderBreaker(job) || JobConstants.isCorsair(job)
                || JobConstants.isAngelicBuster(job) || JobConstants.isJett(job)) {
            return ItemJob.PIRATE;
        } else {
            return ItemJob.BEGINNER;
        }
    }

    public static BaseStat getSecStatByMainStat(BaseStat mainStat) {
        if(mainStat == null) {
            return null;
        }
        switch(mainStat) {
            case str:
                return BaseStat.dex;
            case dex:
                return BaseStat.str;
            case inte:
                return BaseStat.luk;
            case luk:
                return BaseStat.dex;
        }
        return null;
    }

    public static double getExpOrbExpModifierById(int itemID) {
        switch (itemID) {
            case BLUE_EXP_ORB_ID:
                return BLUE_EXP_ORB_MULT;
            case PURPLE_EXP_ORB_ID:
                return PURPLE_EXP_ORB_MULT;
            case RED_EXP_ORB_ID:
                return RED_EXP_ORB_MULT;
        }
        return 0;
    }

    /**
     * Gets a list of possible elite stats by mob level.
     * @param level the level of the mob
     * @return list of Triples, each triple indicating the level (left), extra hp rate (mid) and the extra exp/meso drop rate (right).
     */
    public static List<Triple<Integer, Double, Double>> getEliteInfoByMobLevel(int level) {
        List<Triple<Integer, Double, Double>> list = new ArrayList<>();
        if (level < 100) {
            list.add(new Triple<>(0, 21D, 10.5));
            list.add(new Triple<>(1, 29D, 14.5));
            list.add(new Triple<>(2, 38D, 19D));
        } else if (level < 200) {
            list.add(new Triple<>(0, 30D, 15D));
            list.add(new Triple<>(1, 45D, 22.5));
            list.add(new Triple<>(2, 60D, 30D));
        } else {
            // level >= 200
            list.add(new Triple<>(0, 35D, 17.5));
            list.add(new Triple<>(1, 52.5, 26.25));
            list.add(new Triple<>(2, 70D, 35D));
        }
        return list;
    }
}

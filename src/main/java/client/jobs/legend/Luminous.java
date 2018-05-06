package client.jobs.legend;

import client.Client;
import client.character.Char;
import client.character.HitInfo;
import client.character.skills.*;
import client.field.Field;
import client.jobs.Job;
import client.life.Mob;
import client.life.MobTemporaryStat;
import client.life.Summon;
import connection.InPacket;
import constants.JobConstants;
import constants.SkillConstants;
import enums.ChatMsgColour;
import enums.MobStat;
import enums.Stat;
import loaders.SkillData;
import packet.WvsContext;
import server.EventManager;
import util.Util;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static client.character.skills.CharacterTemporaryStat.*;
import static client.character.skills.SkillStat.*;

/**
 * Created on 12/14/2017.
 */
public class Luminous extends Job {
    public static final int SUNFIRE = 20040216;
    public static final int ECLIPSE = 20040217;
    public static final int EQUILIBRIUM = 20040219;
    public static final int EQUILIBRIUM2 = 20040220; //test
    public static final int INNER_LIGHT = 20040221;
    public static final int FLASH_BLINK = 20041222;
    public static final int CHANGE_LIGHT_DARK = 20041239;

    public static final int MAGIC_BOOSTER = 27101004; //Buff
    public static final int BLACK_BLESSING = 27100003;

    public static final int SHADOW_SHELL = 27111004; //Buff
    public static final int DUSK_GUARD = 27111005; //Buff
    public static final int PHOTIC_MEDITATION = 27111006; //Buff
    public static final int LUNAR_TIDE = 27110007;

    public static final int DARK_CRESCENDO = 27121005; //Buff
    public static final int ARCANE_PITCH = 27121006; //Buff
    public static final int MAPLE_WARRIOR_LUMI = 27121009; //Buff
    public static final int ENDER = 27121303;
    public static final int DARKNESS_MASTERY = 27120008;
    public static final int HEROS_WILL_LUMI = 27121010;

    public static final int EQUALIZE = 27121054;
    public static final int HEROIC_MEMORIES_LUMI = 27121053;
    public static final int ARMAGEDDON = 27121052; //Stun debuff

    private int[] addedSkills = new int[] {
            SUNFIRE,
            ECLIPSE,
            EQUILIBRIUM,
            //EQUILIBRIUM2,
            INNER_LIGHT,
            FLASH_BLINK,
            CHANGE_LIGHT_DARK
    };

    private final int[] buffs = new int[]{
            MAGIC_BOOSTER,
            SHADOW_SHELL,
            DUSK_GUARD,
            PHOTIC_MEDITATION,
            DARK_CRESCENDO,
            ARCANE_PITCH,
            MAPLE_WARRIOR_LUMI,
            HEROIC_MEMORIES_LUMI,
            EQUALIZE,
    };

    public Luminous(Char chr) {
        super(chr);
        if(isHandlerOfJob(chr.getJob())) {
            for (int id : addedSkills) {
                if (!chr.hasSkill(id)) {
                    Skill skill = SkillData.getSkillDeepCopyById(id);
                    skill.setCurrentLevel(skill.getMasterLevel());
                    chr.addSkill(skill);
                }
            }

            if (chr.getTemporaryStatManager().getLarknessManager() == null) {
                chr.getTemporaryStatManager().setLarknessManager(new LarknessManager(chr));
            }
//        changeMode();
        }
    }

    public void handleBuff(Client c, InPacket inPacket, int skillID, byte slv) {
        SkillInfo si = SkillData.getSkillInfoById(skillID);
        TemporaryStatManager tsm = c.getChr().getTemporaryStatManager();
        Summon summon;
        Field field;
        Option o1 = new Option();
        Option o2 = new Option();
        Option o3 = new Option();
        switch (skillID) {
            case MAGIC_BOOSTER:
                o1.nOption = si.getValue(x, slv);
                o1.rOption = skillID;
                o1.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(Booster, o1);
                break;
            case SHADOW_SHELL:
                //TODO Handler once Mob Skills are implemented
                break;
            case DUSK_GUARD:
                o1.nValue = si.getValue(indieMdd, slv);
                o1.nReason = skillID;
                o1.tStart = (int) System.currentTimeMillis();
                o1.tTerm = si.getValue(time, slv);
                tsm.putCharacterStatValue(IndieMDD, o1);
                o2.nValue = si.getValue(indiePdd, slv);
                o2.nReason = skillID;
                o2.tStart = (int) System.currentTimeMillis();
                o2.tTerm = si.getValue(time, slv);
                tsm.putCharacterStatValue(IndiePDD, o2);
                break;
            case PHOTIC_MEDITATION:
                o1.nOption = si.getValue(emad, slv);
                o1.rOption = skillID;
                o1.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(EMAD, o1);
                break;
            case DARK_CRESCENDO:
                o1.nOption = 1;
                o1.rOption = skillID;
                o1.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(StackBuff, o1);
                break;
            case ARCANE_PITCH:
                o1.nOption = si.getValue(y, slv);
                o1.rOption = skillID;
                o1.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(ElementalReset, o1);
                break;
            case MAPLE_WARRIOR_LUMI:
                o1.nValue = si.getValue(x, slv);
                o1.nReason = skillID;
                o1.tStart = (int) System.currentTimeMillis();
                o1.tTerm = si.getValue(time, slv);
                tsm.putCharacterStatValue(IndieStatR, o1);
                break;
            case EQUALIZE:
                LarknessManager lm = tsm.getLarknessManager();
                lm.changeMode();
                o1.nOption = 1;
                o1.rOption = EQUILIBRIUM;
                tsm.putCharacterStatValue(Larkness, o1);
                EventManager.addEvent(this::changeMode, getMoreEquilibriumTime(), TimeUnit.SECONDS);
                break;

            case HEROIC_MEMORIES_LUMI:
                o1.nReason = skillID;
                o1.nValue = si.getValue(indieDamR, slv);
                o1.tStart = (int) System.currentTimeMillis();
                o1.tTerm = si.getValue(time, slv);
                tsm.putCharacterStatValue(IndieDamR, o1);
                o2.nReason = skillID;
                o2.nValue = si.getValue(indieMaxDamageOverR, slv);
                o2.tStart = (int) System.currentTimeMillis();
                o2.tTerm = si.getValue(time, slv);
                tsm.putCharacterStatValue(IndieMaxDamageOverR, o2);
                break;
        }
        c.write(WvsContext.temporaryStatSet(tsm));
    }

    private void handleLarkness(int skillId) {
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        LarknessManager lm = tsm.getLarknessManager();
        if(SkillConstants.isLarknessLightSkill(skillId)) {
            lm.addGauge(2500, false);
        } else if(SkillConstants.isLarknessDarkSkill(skillId)) {
            lm.addGauge(2500, true);
        }
    }



    private void handleLunarTide() {
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        if(chr.hasSkill(LUNAR_TIDE)) {
            Option o1 = new Option();
            Option o2 = new Option();
            SkillInfo lti = SkillData.getSkillInfoById(LUNAR_TIDE);
            Skill skill = chr.getSkill(LUNAR_TIDE);
            byte slv = (byte) skill.getCurrentLevel();
            int maxMP = c.getChr().getStat(Stat.mmp);
            int curMP = c.getChr().getStat(Stat.mp);
            int maxHP = c.getChr().getStat(Stat.mhp);
            int curHP = c.getChr().getStat(Stat.hp);
            double ratioHP = ((double)curHP/maxHP);
            double ratioMP = ((double)curMP)/maxMP;

            //if (ratioHP > ratioMP) {
            if(ratioHP > ratioMP) {
                //Crit Rate      HP is Greater than MP
                o1.nOption = 2;
                o1.rOption = LUNAR_TIDE;
                o1.tOption = 0;
                tsm.putCharacterStatValue(LifeTidal, o1);
                o2.nOption = lti.getValue(prop, slv);     //only gives 10% for w/e reason but the SkillStat is correct
                o2.rOption = LUNAR_TIDE;
                o2.tOption = 0;
                tsm.putCharacterStatValue(CriticalBuff, o2);
            } else {
                //Damage         MP is Greater than HP
                o1.nOption = 1;
                o1.rOption = LUNAR_TIDE;
                o1.tOption = 0;
                tsm.putCharacterStatValue(LifeTidal, o1);
                o2.nOption = lti.getValue(x, slv);
                o2.rOption = LUNAR_TIDE;
                o2.tOption = 0;
                tsm.putCharacterStatValue(DamR, o2);
            }
            c.write(WvsContext.temporaryStatSet(tsm));
        }
    }

    private void handleDarkCrescendo(int skillId, TemporaryStatManager tsm, Client c) {
        Option o = new Option();
        Option o1 = new Option();
        SkillInfo crescendoInfo = SkillData.getSkillInfoById(DARK_CRESCENDO);
        Skill skill = chr.getSkill(DARK_CRESCENDO);
        byte slv = (byte) skill.getCurrentLevel();
        int MaxStack = getMaxStack(chr);
        int amount = 1;
        if(tsm.hasStat(StackBuff)) {
            amount = tsm.getOption(StackBuff).mOption;
            if(amount < getMaxStack(chr)) {
                amount++;
            }
        }
        o.nOption = 1;
        o.rOption = DARK_CRESCENDO;
        o.tOption = crescendoInfo.getValue(time, slv);
        o.mOption = amount;
        tsm.putCharacterStatValue(StackBuff, o);
        o1.nOption = (amount * crescendoInfo.getValue(damR, slv));
        o1.rOption = DARK_CRESCENDO;
        o1.tOption = crescendoInfo.getValue(time, slv);
        tsm.putCharacterStatValue(DamR, o1);
        c.write(WvsContext.temporaryStatSet(tsm));
    }

    private int getCrescendoProp(Char chr) {
        Skill skill = null;
        if (chr.hasSkill(DARK_CRESCENDO)) {
            skill = chr.getSkill(DARK_CRESCENDO);
        }
        return SkillData.getSkillInfoById(DARK_CRESCENDO).getValue(prop, chr.getSkill(27121005).getCurrentLevel());
    }

    private int getMaxStack(Char chr) {
        Skill skill = null;
        if (chr.hasSkill(DARK_CRESCENDO)) {
            skill = chr.getSkill(DARK_CRESCENDO);
        }
        return SkillData.getSkillInfoById(skill.getSkillId()).getValue(x, skill.getCurrentLevel());
    }

    public static void handleBlackBlessingIncrease(Client c) {
        Char chr = c.getChr();
        Option o = new Option();
        Option o1 = new Option();
        Option o2 = new Option();
        TemporaryStatManager tsm = chr.getTemporaryStatManager();

        int orbmad = 0;
        if(tsm.getOption(BlessOfDarkness).nOption == 1) {
            orbmad = 15;
        } else if (tsm.getOption(BlessOfDarkness).nOption == 2) {
            orbmad = 24;
        } else if(tsm.getOption(BlessOfDarkness).nOption == 3) {
            orbmad = 30;
        }

        int amount = 1;
        if(tsm.hasStat(BlessOfDarkness)) {
            amount = tsm.getOption(BlessOfDarkness).nOption;
            if(amount < 3) {
                amount++;
            }
        }
        o.nOption = amount;
        o.rOption = BLACK_BLESSING;
        o.tOption = 0;
        tsm.putCharacterStatValue(BlessOfDarkness, o);
        if(tsm.getOption(BlessOfDarkness).nOption > 0) {
            o1.nOption = 1; //absorbs ~70% of dmg
            o1.rOption = BLACK_BLESSING;
            o1.tOption = 0;
            tsm.putCharacterStatValue(IgnoreMobDamR, o1);
        }
        o2.nOption = orbmad;
        o2.rOption = BLACK_BLESSING;
        o2.tOption = 0;
        tsm.putCharacterStatValue(MAD, o2);
        c.write(WvsContext.temporaryStatSet(tsm));
    }

    public void resetBlackBlessing() {
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        tsm.removeStat(MAD, false);
        tsm.removeStat(BlessOfDarkness, false);
        tsm.removeStat(IgnoreMobDamR, false);
        c.write(WvsContext.temporaryStatReset(tsm, false));
    }

    public boolean isBuff(int skillID) {
        return Arrays.stream(buffs).anyMatch(b -> b == skillID);
    }

    @Override
    public void handleAttack(Client c, AttackInfo attackInfo) {
        Char chr = c.getChr();
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        Skill skill = chr.getSkill(attackInfo.skillId);
        int skillID = 0;
        SkillInfo si = null;
        boolean hasHitMobs = attackInfo.mobAttackInfo.size() > 0;
        byte slv = 0;
        if (skill != null) {
            si = SkillData.getSkillInfoById(skill.getSkillId());
            slv = (byte) skill.getCurrentLevel();
            skillID = skill.getSkillId();
        }
        handleLarkness(skillID);
        int crescendoProp = getCrescendoProp(chr);
        if (hasHitMobs) {
            if(!tsm.hasStat(Larkness)) {
                LarknessManager lm = tsm.getLarknessManager();
                Option o = new Option();
                o.nOption = 1;
                o.rOption = lm.isDark() ? ECLIPSE : SUNFIRE;
                tsm.putCharacterStatValue(Larkness, o);
                tsm.sendSetStatPacket();
            }
            //Dark Crescendo
            if (tsm.hasStat(StackBuff)) {
                if (Util.succeedProp(crescendoProp)) {
                    handleDarkCrescendo(skill.getSkillId(), tsm, c);
                }
            }
        }
        handleLunarTide();
        Option o1 = new Option();
        Option o2 = new Option();
        Option o3 = new Option();
        switch (attackInfo.skillId) {
            case ARMAGEDDON:
                for (MobAttackInfo mai : attackInfo.mobAttackInfo) {
                    Mob mob = (Mob) chr.getField().getLifeByObjectID(mai.mobId);
                    MobTemporaryStat mts = mob.getTemporaryStat();
                    o1.nOption = 1;
                    o1.rOption = skill.getSkillId();
                    o1.tOption = si.getValue(time, slv);
                    mts.addStatOptionsAndBroadcast(MobStat.Stun, o1);
                }
                break;
        }
    }

    @Override
    public void handleSkill(Client c, int skillID, byte slv, InPacket inPacket) {
        Char chr = c.getChr();
        Skill skill = chr.getSkill(skillID);
        SkillInfo si = null;
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        LarknessManager lm = tsm.getLarknessManager();
        if(skill != null) {
            si = SkillData.getSkillInfoById(skillID);
        }
        chr.chatMessage(ChatMsgColour.YELLOW, "SkillID: " + skillID);
        if (isBuff(skillID)) {
            handleBuff(c, inPacket, skillID, slv);
        } else {
            Option o1 = new Option();
            Option o2 = new Option();
            Option o3 = new Option();
            switch(skillID) {
                case ECLIPSE:
                case SUNFIRE:
                case CHANGE_LIGHT_DARK:

                    lm.changeMode();
                    o1.nOption = 1;
                    o1.rOption = EQUILIBRIUM;
//                    o1.tOption = SkillData.getSkillInfoById(EQUILIBRIUM).getValue(time, 1);
                    tsm.putCharacterStatValue(Larkness, o1);
                    EventManager.addEvent(this::changeMode, getMoreEquilibriumTime(), TimeUnit.SECONDS);
                    break;
                case HEROS_WILL_LUMI:
                    tsm.removeAllDebuffs();
                    break;
            }
            tsm.sendSetStatPacket();
        }
    }

    public void changeMode() {
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        LarknessManager lm = tsm.getLarknessManager();
        Option o = new Option();
        o.nOption = 1;
        o.rOption = lm.isDark() ? ECLIPSE : SUNFIRE;
        tsm.putCharacterStatValue(Larkness, o);
        tsm.sendSetStatPacket();
    }

    public int getMoreEquilibriumTime() {
        int eqTime = 20;
        SkillInfo eqi = SkillData.getSkillInfoById(DARKNESS_MASTERY);
        if(chr.hasSkill(DARKNESS_MASTERY)) {
            eqTime += eqi.getValue(time, eqi.getCurrentLevel());
            eqTime += 5;
        }
        return eqTime;
    }

    @Override
    public void handleHit(Client c, InPacket inPacket, HitInfo hitInfo) {
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        Option o = new Option();
        if(tsm.getOption(Larkness).rOption == EQUILIBRIUM) {
            return;
        } else {
            if (tsm.hasStat(BlessOfDarkness)) {
                if (tsm.getOption(BlessOfDarkness).nOption == 3) {
                    o.nOption = 2;
                    tsm.putCharacterStatValue(BlessOfDarkness, o);
                    tsm.sendSetStatPacket();
                } else if (tsm.getOption(BlessOfDarkness).nOption == 2) {
                    o.nOption = 1;
                    tsm.putCharacterStatValue(BlessOfDarkness, o);
                    tsm.sendSetStatPacket();
                } else if (tsm.getOption(BlessOfDarkness).nOption == 1) {
                    resetBlackBlessing();
                }
            }
        }
        super.handleHit(c, inPacket, hitInfo);
    }

    @Override
    public boolean isHandlerOfJob(short id) {
        JobConstants.JobEnum job = JobConstants.JobEnum.getJobById(id);
        switch (job) {
            case LUMINOUS:
            case LUMINOUS1:
            case LUMINOUS2:
            case LUMINOUS3:
            case LUMINOUS4:
                return true;
            default:
                return false;
        }
    }

    @Override
    public int getFinalAttackSkill() {
        return 0;
    }
}
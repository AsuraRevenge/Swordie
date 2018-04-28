package client.jobs.sengoku;

import client.Client;
import client.character.Char;
import client.character.HitInfo;
import client.character.skills.*;
import client.jobs.Job;
import client.life.Mob;
import client.life.MobTemporaryStat;
import connection.InPacket;
import constants.JobConstants;
import enums.ChatMsgColour;
import enums.MobStat;
import loaders.SkillData;
import packet.UserLocal;
import packet.WvsContext;
import util.Util;

import java.util.Arrays;

import static client.character.skills.CharacterTemporaryStat.*;
import static client.character.skills.SkillStat.*;

/**
 * Created on 12/14/2017.
 */
public class Hayato extends Job {

    //Blade Energy
    public static final int QUICK_DRAW = 40011288;
    public static final int NORMAL_STANCE_BONUS = 40011291;
    public static final int QUICK_DRAW_STANCE_BONUS = 40011292;
    public static final int SUMMER_RAIN = 40011289;
    public static final int HITOKIRI_HUNDRED_STRIKE = 40011290;
    public static final int MASTER_OF_BLADES = 40010000;
    public static final int SHIMADA_HEART = 40010067;

    public static final int BATTOUJUTSU_ADVANCE = 41001010; //not sure what this skill does

    public static final int KATANA_BOOSTER = 41101005; //Buff
    public static final int MILITARY_MIGHT = 41101003; //Buff

    public static final int WILLOW_DODGE = 41110006;
    public static final int MERCILESS_BLADE = 41110007;
    public static final int WARRIOR_HEART = 41110009;

    public static final int IRON_SKIN = 41121003; //Buff
    public static final int AKATSUKI_HERO_HAYATO = 41121005; //Buff
    public static final int TORNADO_BLADE = 41121017; //Attack (Stun Debuff)
    public static final int HITOKIRI_STRIKE = 41121002; //Crit% buff
    public static final int EYE_FOR_AN_EYE = 41121015; //  ON/OFF
    public static final int JINSOKU = 41120006;
    public static final int BLOODLETTER = 41120007;
    public static final int SUDDEN_STRIKE = 41121018;
    public static final int AKATSUKI_BLOSSOMS = 41121004;


    public static final int GOD_OF_BLADES = 41121054;
    public static final int PRINCESS_VOW_HAYATO = 41121053;

    //BattouJutsu Linked Skills
    public static final int SURGING_BLADE_BATTOUJUTSU = 41001014;
    public static final int SHOURYUUSEN_BATTOUJUTSU = 41001015;
    public static final int RISING_SLASH_BATTOUJUTSU = 41101014;
    public static final int FALCON_DIVE_BATTOUJUTSU = 41101015;
    public static final int DANKUUSEN_BATTOUJUTSU = 41111018;
    public static final int SWEEPING_SWORD_BATTOUJUTSU = 41111017;
    public static final int TORNADO_BLADE_BATTOUJUTSU = 41121020;
    public static final int SUDDEN_STRIKE_BATTOUJUTSU = 41121021;

    private int[] addedSkills = new int[] {
            QUICK_DRAW,
            SUMMER_RAIN,
            MASTER_OF_BLADES,
            SHIMADA_HEART,
    };

    private int[] buffs = new int[] {
            QUICK_DRAW,
            BATTOUJUTSU_ADVANCE,
            KATANA_BOOSTER,
            MILITARY_MIGHT,
            IRON_SKIN,
            AKATSUKI_HERO_HAYATO,
            EYE_FOR_AN_EYE,
            GOD_OF_BLADES,
            PRINCESS_VOW_HAYATO,
    };

    private int swordEnergy = 0;

    public Hayato(Char chr) {
        super(chr);
        if(isHandlerOfJob(chr.getJob())) {
            for (int id : addedSkills) {
                if (!chr.hasSkill(id)) {
                    Skill skill = SkillData.getSkillDeepCopyById(id);
                    skill.setCurrentLevel(skill.getMasterLevel());
                    chr.addSkill(skill);
                }
            }
        }
    }

    public void handleBuff(Client c, InPacket inPacket, int skillID, byte slv) {
        Char chr = c.getChr();
        SkillInfo si = SkillData.getSkillInfoById(skillID);
        TemporaryStatManager tsm = c.getChr().getTemporaryStatManager();
        Option o1 = new Option();
        Option o2 = new Option();
        Option o3 = new Option();
        Option o4 = new Option();
        Option o5 = new Option();
        switch (skillID) {
            case QUICK_DRAW:
                if(tsm.getOption(HayatoStance).nOption == 0) {
                    if(swordEnergy < 150) {
                        chr.chatMessage(ChatMsgColour.GAME_MESSAGE, "You need 150 sword energy to switch into quick draw stance.");
                        return;
                    } else {
                        swordEnergy -= 150;
                        c.write(UserLocal.modHayatoCombo(swordEnergy));
                    }
                }
                if(tsm.getOption(HayatoStance).nOption == 0) {
                    resetNormalStanceBonus();
                } else
                if(tsm.getOption(HayatoStance).nOption == 1) {
                    resetQuickDrawStanceBonus();
                }
                o1.nOption = 1;
                o1.rOption = skillID;
                tsm.putCharacterStatValue(HayatoStance, o1);
                handleSwordEnergyLevels();
                break;
            case BATTOUJUTSU_ADVANCE:
                o1.nOption = 1;
                o1.rOption = skillID;
                o1.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(BattoujutsuAdvance, o1);
                o2.nOption = 8;
                o2.rOption = skillID;
                o2.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(DamR, o2);
                break;
            case KATANA_BOOSTER:
                o1.nOption = si.getValue(x, slv);
                o1.rOption = skillID;
                o1.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(Booster, o1);
                break;
            case MILITARY_MIGHT:
                o1.nReason = skillID;
                o1.nValue = si.getValue(x, slv);
                o1.tStart = (int) System.currentTimeMillis();
                o1.tTerm = si.getValue(time, slv);
                tsm.putCharacterStatValue(IndieMHPR, o1); //Indie
                o2.nReason = skillID;
                o2.nValue = si.getValue(y, slv);
                o2.tStart = (int) System.currentTimeMillis();
                o2.tTerm = si.getValue(time, slv);
                tsm.putCharacterStatValue(IndieMMPR, o1); //Indie
                o3.nOption = si.getValue(speed, slv);
                o3.rOption = skillID;
                o3.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(Speed, o3);
                o4.nOption = si.getValue(jump, slv);
                o4.rOption = skillID;
                o4.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(Jump, o4);
                o5.nOption = si.getValue(padX, slv);
                o5.rOption = skillID;
                o5.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(PAD, o5);
                break;
            case IRON_SKIN:
                o1.nOption = si.getValue(x, slv);
                o1.rOption = skillID;
                o1.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(AsrR, o1);
                o1.nOption = si.getValue(y, slv);
                o1.rOption = skillID;
                o1.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(TerR, o1);
                break;
            case AKATSUKI_HERO_HAYATO:
                o1.nReason = skillID;
                o1.nValue = si.getValue(x, slv);
                o1.tStart = (int) System.currentTimeMillis();
                o1.tTerm = si.getValue(time, slv);
                tsm.putCharacterStatValue(IndieStatR, o1); //Indie
                break;
            case EYE_FOR_AN_EYE:
                o1.nOption = 1;
                o1.rOption = skillID;
                o1.tOption = 0;
                tsm.putCharacterStatValue(EyeForEye, o1);
                break;
            case PRINCESS_VOW_HAYATO:
                o1.nReason = skillID;
                o1.nValue = si.getValue(indieDamR, slv);
                o1.tStart = (int) System.currentTimeMillis();
                o1.tTerm = si.getValue(time, slv);
                tsm.putCharacterStatValue(IndieDamR, o1);
                o2.nReason = skillID;
                o2.nValue = si.getValue(indieMaxDamageOver, slv);
                o2.tStart = (int) System.currentTimeMillis();
                o2.tTerm = si.getValue(time, slv);
                tsm.putCharacterStatValue(IndieMaxDamageOver, o2);
                break;
            case GOD_OF_BLADES:
                o1.nReason = skillID;
                o1.nValue = si.getValue(indiePad, slv);
                o1.tStart = (int) System.currentTimeMillis();
                o1.tTerm = si.getValue(time, slv);
                tsm.putCharacterStatValue(IndiePAD, o1); //Indie
                o2.nOption = si.getValue(x, slv);
                o2.rOption = skillID;
                o2.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(AsrR, o2);
                tsm.putCharacterStatValue(TerR, o2);
                break;
        }
        c.write(WvsContext.temporaryStatSet(tsm));
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
        int slv = 0;
        if (skill != null) {
            si = SkillData.getSkillInfoById(skill.getSkillId());
            slv = skill.getCurrentLevel();
            skillID = skill.getSkillId();
        }
        handleSwordEnergy(attackInfo);
        handleSwordEnergyLevels();

        if(tsm.getOption(HayatoStance).nOption == 1) {
            resetNormalStanceBonus();
            handleQuickDrawStanceBonus();
            handleQuickDrawStunBonus(attackInfo);
        } else
        if(tsm.getOption(HayatoStance).nOption == 0) {
            resetQuickDrawStanceBonus();
            handleNormalStanceBonus();
        }

        if(hasHitMobs) {
            handleDoT(attackInfo);
            handleWarriorHeart(attackInfo);
        }
        Option o1 = new Option();
        Option o2 = new Option();
        Option o3 = new Option();
        switch (attackInfo.skillId) {
            case TORNADO_BLADE:
            //case TORNADO_BLADE_BATTOUJUTSU:
                for (MobAttackInfo mai : attackInfo.mobAttackInfo) {
                    if (Util.succeedProp(si.getValue(subProp, slv))) {
                        Mob mob = (Mob) chr.getField().getLifeByObjectID(mai.mobId);
                        MobTemporaryStat mts = mob.getTemporaryStat();
                        o1.nOption = 1;
                        o1.rOption = skill.getSkillId();
                        o1.tOption = si.getValue(time, slv);
                        mts.addStatOptionsAndBroadcast(MobStat.Stun, o1);
                    }
                }
                break;
            case SUDDEN_STRIKE:
            //case SUDDEN_STRIKE_BATTOUJUTSU:
                for (MobAttackInfo mai : attackInfo.mobAttackInfo) {
                    Mob mob = (Mob) chr.getField().getLifeByObjectID(mai.mobId);
                    MobTemporaryStat mts = mob.getTemporaryStat();
                    o1.nOption = si.getValue(y, slv);
                    o1.rOption = skill.getSkillId();
                    o1.tOption = si.getValue(time, slv);
                    mts.addStatOptionsAndBroadcast(MobStat.AddDamParty, o1);
                }
                break;
            case SUMMER_RAIN:
            case HITOKIRI_HUNDRED_STRIKE:
                if((tsm.getOptByCTSAndSkill(IndieDamR, SUMMER_RAIN) == null) || (tsm.getOptByCTSAndSkill(IndieDamR, HITOKIRI_HUNDRED_STRIKE) == null)) {
                    o1.nReason = skillID;
                    o1.nValue = 15;
                    o1.tStart = (int) System.currentTimeMillis();
                    o1.tTerm = 120;
                    tsm.putCharacterStatValue(IndieDamR, o1); //Indie
                    c.write(WvsContext.temporaryStatSet(tsm));
                    swordEnergy = 0;
                    c.write(UserLocal.modHayatoCombo(swordEnergy));
                }
                break;
            case HITOKIRI_STRIKE:
                if(tsm.getOptByCTSAndSkill(IndieCr, HITOKIRI_STRIKE) == null) {
                    o1.nReason = skillID;
                    o1.nValue = si.getValue(prop, slv);
                    o1.tStart = (int) System.currentTimeMillis();
                    o1.tTerm = si.getValue(time, slv);
                    tsm.putCharacterStatValue(IndieCr, o1);
                    c.write(WvsContext.temporaryStatSet(tsm));
                }
                break;
        }
    }

    @Override
    public void handleSkill(Client c, int skillID, byte slv, InPacket inPacket) {
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        Char chr = c.getChr();
        Skill skill = chr.getSkill(skillID);
        SkillInfo si = null;
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
                case AKATSUKI_BLOSSOMS:
                    tsm.removeAllDebuffs();
                    break;
            }
        }
    }

    @Override
    public void handleHit(Client c, InPacket inPacket, HitInfo hitInfo) {

        //Dodge
        if(hitInfo.HPDamage == 0 && hitInfo.MPDamage == 0) {
            handleJinsoku();
            handleWillowDodge();
        }
        super.handleHit(c, inPacket, hitInfo);
    }

    @Override
    public boolean isHandlerOfJob(short id) {
        return JobConstants.isHayato(id);
    }

    @Override
    public int getFinalAttackSkill() {
        return 0;
    }

    public void handleSwordEnergy(AttackInfo attackInfo) {
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        for (MobAttackInfo mai : attackInfo.mobAttackInfo) {
            Mob mob = (Mob) chr.getField().getLifeByObjectID(mai.mobId);
            int totaldmg = Arrays.stream(mai.damages).sum();

            if(totaldmg >= mob.getHp()) {

                //reward BladeEnergy
                if(tsm.getOption(HayatoStance).nOption == 0) {

                    //Reward 5 Blade Energy
                    if(swordEnergy + 5 > 1000) {
                        swordEnergy = 1000;
                    } else {
                        swordEnergy += 5;
                    }
                } else if (tsm.getOption(HayatoStance).nOption == 1) {

                    //Reward 2 Blade Energy
                    if(swordEnergy + 2 > 1000) {
                        swordEnergy = 1000;
                    } else {
                        swordEnergy += 2;
                    }
                }
                c.write(UserLocal.modHayatoCombo(swordEnergy));
            }
        }
    }

    public void handleSwordEnergyLevels() {
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        Option o = new Option();
        o.nOption = 1;
        o.rOption = tsm.getOption(HayatoStance).nOption == 0 ? NORMAL_STANCE_BONUS : QUICK_DRAW_STANCE_BONUS;
        tsm.putCharacterStatValue(HayatoStanceBonus, o);
        c.write(WvsContext.temporaryStatSet(tsm));
    }

    private void handleQuickDrawStanceBonus() {
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        Option o1 = new Option();
        Option o2 = new Option();
        Option o3 = new Option();
        Option o4 = new Option();
        int hayatoBD = 6;
        int hayatoBooster = -1;
        int hayatoCrit = 30;

        if(swordEnergy >= 200) {
            hayatoBD += 0;
            hayatoCrit += 5;
        }
        if(swordEnergy >= 400) {
            hayatoBD += 2;
            hayatoCrit += 5;
        }
        if(swordEnergy >= 700) {
            hayatoBD += 0;
            hayatoCrit += 5;
        }
        if(swordEnergy == 1000) {
            hayatoBD += 2;
            hayatoCrit += 5;
        }

        //BossDmg
        o1.nOption = hayatoBD;
        o1.rOption = QUICK_DRAW_STANCE_BONUS;
        tsm.putCharacterStatValue(HayatoBoss, o1);

        //Crit Rate
        o2.nOption = hayatoCrit;
        o2.rOption = QUICK_DRAW_STANCE_BONUS;
        tsm.putCharacterStatValue(HayatoCr, o2);

        //Booster
        o3.nOption = hayatoBooster;
        o3.rOption = QUICK_DRAW_STANCE_BONUS;
        tsm.putCharacterStatValue(HayatoBooster, o3);
    }

    public void handleQuickDrawStunBonus(AttackInfo attackInfo) {
        Option o = new Option();
        int stunProc = 30;
        if(swordEnergy >= 200) {
            stunProc += 5;
        }
        if(swordEnergy >= 400) {
            stunProc += 5;
        }
        if(swordEnergy >= 700) {
            stunProc += 5;
        }
        if(swordEnergy == 1000) {
            stunProc += 5;
        }

        for (MobAttackInfo mai : attackInfo.mobAttackInfo) {
            if(Util.succeedProp(stunProc)) {
                Mob mob = (Mob) chr.getField().getLifeByObjectID(mai.mobId);
                MobTemporaryStat mts = mob.getTemporaryStat();
                o.nOption = 1;
                o.rOption = QUICK_DRAW;
                o.tOption = 3;
                mts.addStatOptionsAndBroadcast(MobStat.Stun, o);
            }
        }
    }

    private void handleNormalStanceBonus() {
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        Option o1 = new Option();
        Option o2 = new Option();
        Option o3 = new Option();
        Option o4 = new Option();
        int hayatoPAD = 1;
        int hayatoMHPR = 20;
        int hayatoIED = 9;
        int hayatoStance = 80;

        if(swordEnergy >= 200) {
            hayatoPAD += 1;
            hayatoIED += 4;
        }
        if(swordEnergy >= 400) {
            hayatoPAD += 2;
            hayatoIED += 4;
        }
        if(swordEnergy >= 700) {
            hayatoPAD += 2;
            hayatoIED += 4;
        }
        if(swordEnergy == 1000) {
            hayatoPAD += 2;
            hayatoIED += 4;
        }

        //PAD
        o1.nOption = hayatoPAD;
        o1.rOption = NORMAL_STANCE_BONUS;
        tsm.putCharacterStatValue(HayatoPAD, o1);

        //MHP
        //MMP
        o2.nOption = hayatoMHPR;
        o2.rOption = NORMAL_STANCE_BONUS;
        tsm.putCharacterStatValue(HayatoHPR, o2);
        tsm.putCharacterStatValue(HayatoMPR, o2);

        //IED
        o3.nOption = hayatoIED;
        o3.rOption = NORMAL_STANCE_BONUS;
        tsm.putCharacterStatValue(IgnoreTargetDEF, o3);

        //Stance
        o4.nOption = hayatoStance;
        o4.rOption = NORMAL_STANCE_BONUS;
        tsm.putCharacterStatValue(Stance, o4);
    }

    public void resetNormalStanceBonus() {
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        tsm.removeStatsBySkill(NORMAL_STANCE_BONUS);
        c.write(WvsContext.temporaryStatReset(tsm, false));
    }

    public void resetQuickDrawStanceBonus() {
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        tsm.removeStatsBySkill(QUICK_DRAW_STANCE_BONUS);
        c.write(WvsContext.temporaryStatReset(tsm, false));
    }


    public void handleWillowDodge() {   //TODO
        Skill skill = chr.getSkill(WILLOW_DODGE);
        if (skill == null) {
            return;
        }
        byte slv = (byte) skill.getCurrentLevel();
        SkillInfo si = SkillData.getSkillInfoById(WILLOW_DODGE);
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        Option o = new Option();
        Option o1 = new Option();
        int amount = 3;
        if (tsm.hasStat(WillowDodge)) {
            amount = tsm.getOption(WillowDodge).nOption;
            if (amount < 5) {
                amount++;
            }
        }
        o.nOption = 2;
        o.rOption = WILLOW_DODGE;
        o.tOption = 20;
        tsm.putCharacterStatValue(WillowDodge, o);

        o1.nOption = si.getValue(damR, slv);
        o1.rOption = WILLOW_DODGE;
        o1.tOption = 20;
        tsm.putCharacterStatValue(DamR, o1);



        c.write(WvsContext.temporaryStatSet(tsm));
    }

    public void handleJinsoku() {
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        Option o = new Option();
        if(chr.hasSkill(JINSOKU)) {
            Skill skill = chr.getSkill(JINSOKU);
            byte slv = (byte) skill.getCurrentLevel();
            SkillInfo si = SkillData.getSkillInfoById(skill.getSkillId());
            int proc = si.getValue(t, slv);
            if(Util.succeedProp(proc)) {
                o.nOption = si.getValue(y, slv);
                o.rOption = skill.getSkillId();
                o.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(DamageReduce, o);
                c.write(WvsContext.temporaryStatSet(tsm));
            }
        }
    }

    public void handleDoT(AttackInfo attackInfo) {
        if(chr.hasSkill(BLOODLETTER)) {
            Skill skill = chr.getSkill(BLOODLETTER);
            byte slv = (byte) skill.getCurrentLevel();
            SkillInfo si = SkillData.getSkillInfoById(skill.getSkillId());
            int proc = si.getValue(prop, slv);
            for (MobAttackInfo mai : attackInfo.mobAttackInfo) {
                if(Util.succeedProp(proc)) {
                    Mob mob = (Mob) chr.getField().getLifeByObjectID(mai.mobId);
                    MobTemporaryStat mts = mob.getTemporaryStat();
                    mts.createAndAddBurnedInfo(chr.getId(), skill, 1);
                }
            }
        } else
        if(chr.hasSkill(MERCILESS_BLADE)) {
            Skill skill = chr.getSkill(MERCILESS_BLADE);
            byte slv = (byte) skill.getCurrentLevel();
            SkillInfo si = SkillData.getSkillInfoById(skill.getSkillId());
            int proc = si.getValue(prop, slv);
            for (MobAttackInfo mai : attackInfo.mobAttackInfo) {
                if (Util.succeedProp(proc)) {
                    Mob mob = (Mob) chr.getField().getLifeByObjectID(mai.mobId);
                    MobTemporaryStat mts = mob.getTemporaryStat();
                    mts.createAndAddBurnedInfo(chr.getId(), skill, 1);
                }
            }
        }
    }

    public void handleWarriorHeart(AttackInfo attackInfo) { //TODO on Crit hit,  proc% to gainHP%
        if(chr.hasSkill(WARRIOR_HEART)) {
            Skill skill = chr.getSkill(WARRIOR_HEART);
            byte slv = (byte) skill.getCurrentLevel();
            SkillInfo si = SkillData.getSkillInfoById(skill.getSkillId());
            int proc = si.getValue(prop, slv);
            int healrate = si.getValue(x, slv);
            int healhp = (int) ((chr.getMaxHP() / ((double) 100 / healrate)));

            //Get chance to heal on  #Crit hits
            if(Util.succeedProp(proc)) {
                chr.heal(healhp);
            }
        }
    }
}

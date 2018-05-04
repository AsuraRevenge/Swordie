package client.jobs.resistance;

import client.Client;
import client.character.Char;
import client.character.HitInfo;
import client.character.skills.*;
import client.field.Field;
import client.jobs.Job;
import client.life.Life;
import client.life.Mob;
import client.life.MobTemporaryStat;
import connection.InPacket;
import constants.JobConstants;
import constants.SkillConstants;
import enums.ChatMsgColour;
import enums.ForceAtomEnum;
import enums.MobStat;
import loaders.SkillData;
import packet.CField;
import packet.WvsContext;
import server.EventManager;
import util.Position;
import util.Rect;
import util.Util;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static client.character.skills.CharacterTemporaryStat.*;
import static client.character.skills.SkillStat.*;

/**
 * Created on 12/14/2017.
 */
public class Demon extends Job {

    //Demon Skills
    public static final int DARK_WINDS = 30010110;
    public static final int DEMONIC_BLOOD = 30010185;
    public static final int SECRET_ASSEMBLY = 30001281;


    //Demon Slayer
    public static final int CURSE_OF_FURY = 30010111;

    public static final int GRIM_SCYTHE = 31001000; //Special Attack            //TODO (Demon Force)
    public static final int BATTLE_PACT_DS = 31001001; //Buff

    public static final int SOUL_EATER = 31101000; //Special Attack             //TODO (Demon Force)
    public static final int DARK_THRUST = 31101001; //Special Attack            //TODO (Demon Force)
    public static final int CHAOS_LOCK = 31101002; //Special Attack  -Stun-     //TODO (Demon Force)
    public static final int VENGEANCE = 31101003; //Buff (Stun Debuff)

    public static final int JUDGEMENT = 31111000; //Special Attack              //TODO (Demon Force)
    public static final int VORTEX_OF_DOOM = 31111001; //Special Attack  -Stun- //TODO (Demon Force)
    public static final int RAVEN_STORM = 31111003; //Special Attack -GainHP-   //TODO (Demon Force)
    public static final int CARRION_BREATH = 31111005; //Special Attack  -DoT-  //TODO (Demon Force)
    public static final int POSSESSED_AEGIS = 31110008;
    public static final int MAX_FURY = 31110009;

    public static final int INFERNAL_CONCUSSION = 31121000; //Special Attack    //TODO (Demon Force)
    public static final int DEMON_IMPACT = 31121001; //Special Attack  -Slow-   //TODO (Demon Force)
    public static final int DEMON_CRY = 31121003; //Special Attack -DemonCry-   //TODO (Demon Force)
    public static final int BINDING_DARKNESS = 31121006; //Special Attack -Bind-//TODO (Demon Force)
    public static final int DARK_METAMORPHOSIS = 31121005; //Buff               //TODO (Demon Force)
    public static final int BOUNDLESS_RAGE = 31121007; //Buff                   //TODO (Demon Force)
    public static final int LEECH_AURA = 31121002; //Buff                       //TODO (Demon Force)
    public static final int MAPLE_WARRIOR_DS = 31121004; //Buff

    public static final int BLUE_BLOOD = 31121054;
    public static final int DEMONIC_FORTITUDE_DS = 31121053;
    public static final int CERBERUS_CHOMP = 31121052;

    public static final int DEMON_LASH = 31000004;
    public static final int DEMON_LASH_2 = 31001006;
    public static final int DEMON_LASH_3 = 31001007;
    public static final int DEMON_LASH_4 = 31001008;


    //Demon Avenger
    public static final int BLOOD_PACT = 30010242;
    public static final int EXCEED = 30010230;
    public static final int HYPER_POTION_MASTERY = 30010231;
    public static final int STAR_FORCE_CONVERSION = 30010232;

    public static final int EXCEED_DOUBLE_SLASH_1 = 31011000; //Special Attack  //TODO (EXCEED System)
    public static final int EXCEED_DOUBLE_SLASH_2 = 31011004; //Special Attack  //TODO (EXCEED System)
    public static final int EXCEED_DOUBLE_SLASH_3 = 31011005; //Special Attack  //TODO (EXCEED System)
    public static final int EXCEED_DOUBLE_SLASH_4 = 31011006; //Special Attack  //TODO (EXCEED System)
    public static final int EXCEED_DOUBLE_SLASH_PURPLE = 31011007; //Special Attack //TODO (EXCEED System)
    public static final int OVERLOAD_RELEASE = 31011001; // Special Buff        //TODO TempStat: ExceedOverload
    public static final int LIFE_SAP = 31010002; //Passive Life Drain

    public static final int EXCEED_DEMON_STRIKE_1 = 31201000; //Special Attack  //TODO (EXCEED System)
    public static final int EXCEED_DEMON_STRIKE_2 = 31201007; //Special Attack  //TODO (EXCEED System)
    public static final int EXCEED_DEMON_STRIKE_3 = 31201008; //Special Attack  //TODO (EXCEED System)
    public static final int EXCEED_DEMON_STRIKE_4 = 31201009; //Special Attack  //TODO (EXCEED System)
    public static final int EXCEED_DEMON_STRIKE_PURPLE = 31201010; //Special Attack //TODO (EXCEED System)
    public static final int BATTLE_PACT_DA = 31201002; //Buff
    public static final int BAT_SWARM = 31201001;

    public static final int EXCEED_LUNAR_SLASH_1 = 31211000; //Special Attack   //TODO (EXCEED System)
    public static final int EXCEED_LUNAR_SLASH_2 = 31211007; //Special Attack   //TODO (EXCEED System)
    public static final int EXCEED_LUNAR_SLASH_3 = 31211008; //Special Attack   //TODO (EXCEED System)
    public static final int EXCEED_LUNAR_SLASH_4 = 31211009; //Special Attack   //TODO (EXCEED System)
    public static final int EXCEED_LUNAR_SLASH_PURPLE = 31211010; //Special Attack //TODO (EXCEED System)
    public static final int VITALITY_VEIL = 31211001;
    public static final int SHIELD_CHARGE_RUSH = 31211002;
    public static final int SHIELD_CHARGE = 31211011; //Special Attack (Stun Debuff)
    public static final int DIABOLIC_RECOVERY = 31211004; //Buff
    public static final int WARD_EVIL = 31211003; //Buff
    public static final int ADVANCED_LIFE_SAP = 31210006; //Passive Life Drain
    public static final int PAIN_DAMPENER = 31210005;

    public static final int EXCEED_EXECUTION_1 = 31221000; //Special Attack     //TODO (EXCEED System)
    public static final int EXCEED_EXECUTION_2 = 31221009; //Special Attack     //TODO (EXCEED System)
    public static final int EXCEED_EXECUTION_3 = 31221010; //Special Attack     //TODO (EXCEED System)
    public static final int EXCEED_EXECUTION_4 = 31221011; //Special Attack     //TODO (EXCEED System)
    public static final int EXCEED_EXECUTION_PURPLE = 31221012; //Special Attack//TODO (EXCEED System)
    public static final int NETHER_SHIELD = 31221001; //Special Attack          //TODO
    public static final int NETHER_SHIELD_ATOM = 31221014; //atom
    public static final int NETHER_SLICE = 31221002; // Special Attack (DefDown Debuff)
    public static final int BLOOD_PRISON = 31221003; // Special Attack (Stun Debuff)
    public static final int MAPLE_WARRIOR_DA = 31221008; //Buff
    public static final int INFERNAL_EXCEED = 31220007;

    public static final int DEMONIC_FORTITUDE_DA = 31221053;
    public static final int FORBIDDEN_CONTRACT = 31221054;
    public static final int THOUSAND_SWORDS = 31221052;


    private int[] addedSkillsDS = new int[] {
            SECRET_ASSEMBLY,
            DARK_WINDS,
            DEMONIC_BLOOD,
            CURSE_OF_FURY,
    };

    private int[] addedSkillsDA = new int[] {
            SECRET_ASSEMBLY,
            DARK_WINDS,
            DEMONIC_BLOOD,
            EXCEED,
            BLOOD_PACT,
            HYPER_POTION_MASTERY,
            STAR_FORCE_CONVERSION,
    };

    private int[] buffs = new int[] {
            BATTLE_PACT_DS,
            BATTLE_PACT_DA,
            VENGEANCE,
            DARK_METAMORPHOSIS,
            BOUNDLESS_RAGE,
            LEECH_AURA,
            MAPLE_WARRIOR_DS,
            OVERLOAD_RELEASE,
            DIABOLIC_RECOVERY,
            MAPLE_WARRIOR_DA,
            BLUE_BLOOD,
            DEMONIC_FORTITUDE_DS,
            DEMONIC_FORTITUDE_DA,
            FORBIDDEN_CONTRACT,
            WARD_EVIL,
    };

    private long leechAuraCD = Long.MIN_VALUE;

    public Demon(Char chr) {
        super(chr);
        if(isHandlerOfJob(chr.getJob())) {
            if (JobConstants.isDemonSlayer(chr.getJob())) {
                for (int id : addedSkillsDS) {
                    if (!chr.hasSkill(id)) {
                        Skill skill = SkillData.getSkillDeepCopyById(id);
                        skill.setCurrentLevel(skill.getMasterLevel());
                        chr.addSkill(skill);
                    }
                }
                if (chr.hasSkill(MAX_FURY)) {
                    //regenDFInterval(); //TODO  WVsCrash
                }
            } else if (JobConstants.isDemonAvenger(chr.getJob())) {
                for (int id : addedSkillsDA) {
                    if (!chr.hasSkill(id)) {
                        Skill skill = SkillData.getSkillDeepCopyById(id);
                        skill.setCurrentLevel(skill.getMasterLevel());
                        chr.addSkill(skill);
                    }
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
        switch (skillID) {
            case OVERLOAD_RELEASE:
                int overloadcount = tsm.getOption(OverloadCount).nOption;
                if(overloadcount >= getMaxExceed(chr)) { //20 (or 18 w/Hyper)  overload count  for the buff
                    o2.nOption = si.getValue(indiePMdR, slv);
                    o2.rOption = skillID;
                    o2.tOption = si.getValue(time, slv);
                    //tsm.putCharacterStatValue(IndiePMdR, o2);
                    o3.nOption = 1;
                    o3.rOption = skillID;
                    o3.tOption = si.getValue(time, slv);
                    tsm.putCharacterStatValue(ExceedOverload, o3);
                    resetExceed(c, tsm);
                    chr.heal(chr.getMaxHP());
                }
                break;

            case BATTLE_PACT_DA:
            case BATTLE_PACT_DS:
                o1.nOption = si.getValue(x, slv);
                o1.rOption = skillID;
                o1.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(Booster, o1);
                break;
            case VENGEANCE: //stun chance = prop | stun dur. = subTime
                o1.nOption = si.getValue(y, slv);
                o1.rOption = skillID;
                o1.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(PowerGuard, o1);
                break;
            case DARK_METAMORPHOSIS:
                o1.nOption = si.getValue(damR, slv);
                o1.rOption = skillID;
                o1.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(DamR, o1);
                o2.nReason = skillID;
                o2.nValue = si.getValue(indieMhpR, slv);
                o2.tStart = (int) System.currentTimeMillis();
                o2.tTerm = si.getValue(time, slv);
                tsm.putCharacterStatValue(IndieMHPR, o2);
                o3.nOption = si.getValue(damage, slv); //?
                o3.rOption = skillID;
                o3.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(PowerGuard, o3);
                o4.nOption = 1;
                o4.rOption = skillID;
                o4.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(DevilishPower, o4);
                break;
            case BOUNDLESS_RAGE:
                o1.nOption = 1;
                o1.rOption = skillID;
                o1.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(InfinityForce, o1);
                break;
            case LEECH_AURA:
                o1.nOption = si.getValue(x, slv);
                o1.rOption = skillID;
                o1.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(Regen, o1);
                break;
            case WARD_EVIL:
                o1.nOption = si.getValue(x, slv);
                o1.rOption = skillID;
                o1.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(DamageReduce, o1);
                o2.nOption = si.getValue(z, slv);
                o2.rOption = skillID;
                o2.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(AsrR, o2);
                tsm.putCharacterStatValue(TerR, o2);
                break;
            case DIABOLIC_RECOVERY: // x = HP restored at interval
                o1.nReason = skillID;
                o1.nValue = si.getValue(indieMhpR, slv);
                o1.tStart = (int) System.currentTimeMillis();
                o1.tTerm = si.getValue(time, slv);
                tsm.putCharacterStatValue(IndieMHPR, o1);
                o2.nOption = 1;
                o2.rOption = skillID;
                o2.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(DiabolikRecovery, o2);
                handleDiabolicRecovery();
                break;
            case MAPLE_WARRIOR_DA:
            case MAPLE_WARRIOR_DS:
                o1.nReason = skillID;
                o1.nValue = si.getValue(x, slv);
                o1.tStart = (int) System.currentTimeMillis();
                o1.tTerm = si.getValue(time, slv);
                tsm.putCharacterStatValue(IndieStatR, o1);
                break;

            case DEMONIC_FORTITUDE_DS:
            case DEMONIC_FORTITUDE_DA:
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
            case FORBIDDEN_CONTRACT:
                o1.nReason = skillID;
                o1.nValue = si.getValue(indieDamR, slv);
                o1.tStart = (int) System.currentTimeMillis();
                o1.tTerm = si.getValue(time, slv);
                tsm.putCharacterStatValue(IndieDamR, o1);
                //HP consumption from Skills = 0;
                break;
            case BLUE_BLOOD:
                o1.nOption = si.getValue(x, slv);
                o1.rOption = skillID;
                o1.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(ShadowPartner, o1);
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
        if(JobConstants.isDemonSlayer(chr.getJob())) {
            if(hasHitMobs) {
                //Demon Slayer Fury Atoms
                handleFuryForceAtom(attackInfo);

                //Max Fury
                if(chr.hasSkill(MAX_FURY)) {
                    if(attackInfo.skillId == DEMON_LASH || attackInfo.skillId == DEMON_LASH_2 || attackInfo.skillId == DEMON_LASH_3 || attackInfo.skillId == DEMON_LASH_4) {
                        Skill maxfuryskill = chr.getSkill(MAX_FURY);
                        SkillInfo mfsi = SkillData.getSkillInfoById(MAX_FURY);
                        byte skillLevel = (byte) maxfuryskill.getCurrentLevel();
                        int propz = mfsi.getValue(prop, skillLevel);
                        if (Util.succeedProp(propz)) {
                            handleFuryForceAtom(attackInfo);
                        }
                    }
                }

                //Leech Aura
                handleLeechAura(attackInfo);
            }
        }

        if(JobConstants.isDemonAvenger(chr.getJob())) {

            //DA HP Cost System
            hpRCostDASkills(SkillConstants.getActualSkillIDfromSkillID(attackInfo.skillId));

            if(hasHitMobs) {
                //Nether Shield Recreation
                if (attackInfo.skillId == NETHER_SHIELD_ATOM) {
                    handleNetherShieldReCreation(attackInfo);
                }

                //Life Sap & Advanced Life Sap
                handleLifeSap();
            }
        }
        Option o1 = new Option();
        Option o2 = new Option();
        Option o3 = new Option();
        switch (attackInfo.skillId) {
            case CHAOS_LOCK: //prop Stun/Bind
            case VORTEX_OF_DOOM: //prop
                for (MobAttackInfo mai : attackInfo.mobAttackInfo) {
                    if (Util.succeedProp(si.getValue(prop, slv))) {
                        Mob mob = (Mob) chr.getField().getLifeByObjectID(mai.mobId);
                        MobTemporaryStat mts = mob.getTemporaryStat();
                        o1.nOption = 1;
                        o1.rOption = skill.getSkillId();
                        o1.tOption = si.getValue(time, slv);
                        mts.addStatOptionsAndBroadcast(MobStat.Stun, o1);
                    }
                }
                break;
            case BLOOD_PRISON:
                for (MobAttackInfo mai : attackInfo.mobAttackInfo) {
                            Mob mob = (Mob) chr.getField().getLifeByObjectID(mai.mobId);
                            MobTemporaryStat mts = mob.getTemporaryStat();
                            o1.nOption = 1;
                            o1.rOption = skill.getSkillId();
                            o1.tOption = si.getValue(time, slv);
                            mts.addStatOptionsAndBroadcast(MobStat.Stun, o1);
                }
                break;
            case SHIELD_CHARGE:
                for (MobAttackInfo mai : attackInfo.mobAttackInfo) {
                        Mob mob = (Mob) chr.getField().getLifeByObjectID(mai.mobId);
                        MobTemporaryStat mts = mob.getTemporaryStat();
                        o1.nOption = 1;
                        o1.rOption = SkillConstants.getActualSkillIDfromSkillID(skillID);
                        o1.tOption = 5;
                        mts.addStatOptionsAndBroadcast(MobStat.Stun, o1);
                }
                break;
            case CARRION_BREATH: //DoT
                for(MobAttackInfo mai : attackInfo.mobAttackInfo) {
                        Mob mob = (Mob) chr.getField().getLifeByObjectID(mai.mobId);
                        MobTemporaryStat mts = mob.getTemporaryStat();
                        mts.createAndAddBurnedInfo(chr.getId(), skill, 1);
                }
                break;
            case BINDING_DARKNESS: //stun + DoT
                for (MobAttackInfo mai : attackInfo.mobAttackInfo) {
                    Mob mob = (Mob) chr.getField().getLifeByObjectID(mai.mobId);
                    MobTemporaryStat mts = mob.getTemporaryStat();
                    o1.nOption = 1;
                    o1.rOption = skillID;
                    o1.tOption = si.getValue(time, slv);
                    mts.addStatOptions(MobStat.Stun, o1);
                    if(Util.succeedProp(si.getValue(prop, slv))) {
                        mts.createAndAddBurnedInfo(chr.getId(), skill, 1);
                    }
                }
                break;
            case DEMON_CRY:
                for (MobAttackInfo mai : attackInfo.mobAttackInfo) {
                    Mob mob = (Mob) chr.getField().getLifeByObjectID(mai.mobId);
                    MobTemporaryStat mts = mob.getTemporaryStat();
                    o1.nOption = -si.getValue(y, slv);
                    o1.rOption = skill.getSkillId();
                    o1.tOption = si.getValue(time, slv);
                    mts.addStatOptions(MobStat.PAD, o1);
                    mts.addStatOptions(MobStat.PDR, o1);
                    mts.addStatOptions(MobStat.MAD, o1);
                    mts.addStatOptions(MobStat.MDR, o1);
                    o2.nOption = -si.getValue(z, slv);
                    o2.rOption = skill.getSkillId();
                    o2.tOption = si.getValue(time, slv);
                    mts.addStatOptionsAndBroadcast(MobStat.ACC, o2);
                }
                break;
            case DEMON_IMPACT:
                for (MobAttackInfo mai : attackInfo.mobAttackInfo) {
                        Mob mob = (Mob) chr.getField().getLifeByObjectID(mai.mobId);
                        MobTemporaryStat mts = mob.getTemporaryStat();
                        o1.nOption = -20;
                        o1.rOption = skill.getSkillId();
                        o1.tOption = si.getValue(time, slv);
                        mts.addStatOptionsAndBroadcast(MobStat.Speed, o1);
                }
                break;
            case NETHER_SLICE:
                for (MobAttackInfo mai : attackInfo.mobAttackInfo) {
                    Mob mob = (Mob) chr.getField().getLifeByObjectID(mai.mobId);
                    MobTemporaryStat mts = mob.getTemporaryStat();
                    o1.nOption = si.getValue(x, slv);
                    o1.rOption = skill.getSkillId();
                    o1.tOption = 30;
                    mts.addStatOptions(MobStat.PDR, o1);
                    mts.addStatOptionsAndBroadcast(MobStat.MDR, o1);
                }
                break;
            case THOUSAND_SWORDS:
                for(int i = 0; i<5; i++) {
                    handleOverloadCount(attackInfo, skillID, tsm, c);
                }
                break;
            case CERBERUS_CHOMP:
                int furyabsorbed = si.getValue(x, slv);
                chr.healMP(furyabsorbed);
                break;
            case RAVEN_STORM:
                int hpheal = (int) (chr.getMaxHP() / ((double) 100 / si.getValue(x, slv)));
                chr.heal(hpheal);
                break;
            case VITALITY_VEIL:
                int amounthealed = si.getValue(y, slv);
                int healamount = (int) ((chr.getMaxHP()) / ((double) 100 / amounthealed));
                chr.heal(healamount);
                break;

            case EXCEED_DOUBLE_SLASH_1:
            case EXCEED_DOUBLE_SLASH_2:
            case EXCEED_DOUBLE_SLASH_3:
            case EXCEED_DOUBLE_SLASH_4:
            case EXCEED_DOUBLE_SLASH_PURPLE:

            case EXCEED_DEMON_STRIKE_1:
            case EXCEED_DEMON_STRIKE_2:
            case EXCEED_DEMON_STRIKE_3:
            case EXCEED_DEMON_STRIKE_4:
            case EXCEED_DEMON_STRIKE_PURPLE:

            case EXCEED_LUNAR_SLASH_1:
            case EXCEED_LUNAR_SLASH_2:
            case EXCEED_LUNAR_SLASH_3:
            case EXCEED_LUNAR_SLASH_4:
            case EXCEED_LUNAR_SLASH_PURPLE:

            case EXCEED_EXECUTION_1:
            case EXCEED_EXECUTION_2:
            case EXCEED_EXECUTION_3:
            case EXCEED_EXECUTION_4:
            case EXCEED_EXECUTION_PURPLE:
                handleOverloadCount(attackInfo, skillID, tsm, c);
                break;
        }
    }

    private void handleNetherShield() {
        Field field = chr.getField();
        SkillInfo si = SkillData.getSkillInfoById(NETHER_SHIELD);
        Rect rect = chr.getPosition().getRectAround(si.getRects().get(0));
        if(!chr.isLeft()) {
            rect = rect.moveRight();
        }
        List<Life> lifes = field.getLifesInRect(rect);
        Life life = lifes.get(0);
        if(life instanceof Mob) {
            int mobID = (life).getObjectId(); //
            int inc = ForceAtomEnum.NETHER_SHIELD.getInc();
            int type = ForceAtomEnum.NETHER_SHIELD.getForceAtomType();
                ForceAtomInfo forceAtomInfo = new ForceAtomInfo(1, inc, 20, 40,
                        0, 500, (int) System.currentTimeMillis(), 1, 0,
                        new Position(0, -100));
                chr.getField().broadcastPacket(CField.createForceAtom(false, 0, chr.getId(), type,
                        true, mobID, NETHER_SHIELD_ATOM, forceAtomInfo, new Rect(), 0, 300,
                        life.getPosition(), NETHER_SHIELD_ATOM, life.getPosition()));
        }
    }

    private void handleNetherShieldReCreation(AttackInfo attackInfo) {
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        SkillInfo si = SkillData.getSkillInfoById(NETHER_SHIELD);
        int anglenum = new Random().nextInt(360);
        for (MobAttackInfo mai : attackInfo.mobAttackInfo) {
            Mob mob = (Mob) chr.getField().getLifeByObjectID(mai.mobId);
            int TW1prop = 100;// TODO
            if (Util.succeedProp(TW1prop)) {
                int mobID = mai.mobId;

                int inc = ForceAtomEnum.NETHER_SHIELD_RECREATION.getInc();
                int type = ForceAtomEnum.NETHER_SHIELD_RECREATION.getForceAtomType();
                ForceAtomInfo forceAtomInfo = new ForceAtomInfo(1, inc, 35, 4,
                        anglenum, 0, (int) System.currentTimeMillis(), 1, 0,
                        new Position());
                chr.getField().broadcastPacket(CField.createForceAtom(true, chr.getId(), mobID, type,
                        true, mobID, NETHER_SHIELD_ATOM, forceAtomInfo, new Rect(), 0, 300,
                        mob.getPosition(), NETHER_SHIELD_ATOM, mob.getPosition()));
            }
        }
    }

    public void handleOverloadCount(AttackInfo attackInfo, int skillid, TemporaryStatManager tsm, Client c) {
        Option o = new Option();
        int amount = 1;
        if (tsm.hasStat(OverloadCount)) {
            amount = tsm.getOption(OverloadCount).nOption;
            if (amount < getMaxExceed(chr)) {
                amount++;
            }
        }
        o.nOption = amount;
        o.rOption = 30010230;
        o.tOption = 0;
        tsm.putCharacterStatValue(OverloadCount, o);
        c.write(WvsContext.temporaryStatSet(tsm));
    }

    private void resetExceed(Client c, TemporaryStatManager tsm) {
        tsm.getOption(OverloadCount).nOption = 1;
        tsm.removeStat(OverloadCount, false);
        //tsm.removeStat(IndiePMdR, false);
        c.write(WvsContext.temporaryStatReset(tsm, false));
    }

    private int getMaxExceed(Char chr) {
        int num = 20;
        if(chr.hasSkill(31220044)) { //Hyper Skill Boost [ Reduce Overload ]
            num = 18;
        }
        return num;
    }

    @Override
    public void handleSkill(Client c, int skillID, byte slv, InPacket inPacket) {
        Char chr = c.getChr();
        Skill skill = chr.getSkill(skillID);
        SkillInfo si = null;
        if (skill != null) {
            si = SkillData.getSkillInfoById(skillID);
        }
        chr.chatMessage(ChatMsgColour.YELLOW, "SkillID: " + skillID);
        if (isBuff(skillID)) {
            handleBuff(c, inPacket, skillID, slv);
        } else {
            Option o1 = new Option();
            Option o2 = new Option();
            Option o3 = new Option();
            switch (skillID) {
                case NETHER_SHIELD:
                    handleNetherShield();
                    handleNetherShield();
                    break;
            }
        }
    }

    @Override
    public void handleHit(Client c, InPacket inPacket, HitInfo hitInfo) {
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        Option o1 = new Option();

        //Vengeance
        if(tsm.getOptByCTSAndSkill(PowerGuard, VENGEANCE) != null) {
            if(hitInfo.HPDamage != 0) {
                Skill skill = chr.getSkill(VENGEANCE);
                byte slv = (byte) skill.getCurrentLevel();
                SkillInfo si = SkillData.getSkillInfoById(skill.getSkillId());
                int mobID = hitInfo.mobID;
                Mob mob = (Mob) chr.getField().getLifeByObjectID(mobID);
                MobTemporaryStat mts = mob.getTemporaryStat();
                if (Util.succeedProp(si.getValue(prop, slv))) {
                    o1.nOption = 1;
                    o1.rOption = skill.getSkillId();
                    o1.tOption = si.getValue(subTime, slv);
                    o1.bOption = 1;
                    mts.addStatOptionsAndBroadcast(MobStat.Freeze, o1);
                }
            }
        }

        //Possessed Aegis
        if(hitInfo.HPDamage == 0 && hitInfo.MPDamage == 0) {
            // Guarded
            if(chr.hasSkill(POSSESSED_AEGIS)) {
                Skill skill = chr.getSkill(POSSESSED_AEGIS);
                byte slv = (byte) skill.getCurrentLevel();
                SkillInfo si = SkillData.getSkillInfoById(skill.getSkillId());
                int propz = si.getValue(x, slv);
                if(Util.succeedProp(propz)) {
                    int mobID = hitInfo.mobID;
                    handlePossessedAegisFury(mobID);
                    chr.heal((int) (chr.getMaxHP() / ((double) 100 / si.getValue(y, slv))));
                }
            }
        }
        super.handleHit(c, inPacket, hitInfo);
    }

    @Override
    public boolean isHandlerOfJob(short id) {
        return id >= JobConstants.JobEnum.DEMON_SLAYER.getJobId() && id <= JobConstants.JobEnum.DEMON_AVENGER4.getJobId();
    }

    @Override
    public int getFinalAttackSkill() {
        if(chr.hasSkill(INFERNAL_EXCEED)) {
            Skill skill = chr.getSkill(INFERNAL_EXCEED);
            byte slv = (byte) skill.getCurrentLevel();
            SkillInfo si = SkillData.getSkillInfoById(skill.getSkillId());
            int proc = si.getValue(prop, slv);
            if(Util.succeedProp(proc)) {
                return INFERNAL_EXCEED;
            }
        }
        return 0;
    }

    private void handleFuryForceAtom(AttackInfo attackInfo) {
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        for (MobAttackInfo mai : attackInfo.mobAttackInfo) {
            Mob mob = (Mob) chr.getField().getLifeByObjectID(mai.mobId);
            int mobID = mai.mobId;
            int angle = new Random().nextInt(40)+30;
            int speed = new Random().nextInt(31)+29;

            //Attacking with Demon Lash
            if(attackInfo.skillId == DEMON_LASH || attackInfo.skillId == DEMON_LASH_2 || attackInfo.skillId == DEMON_LASH_3 || attackInfo.skillId == DEMON_LASH_4) {
                int inc = ForceAtomEnum.DEMON_SLAYER_FURY_1.getInc();
                int type = ForceAtomEnum.DEMON_SLAYER_FURY_1.getForceAtomType();
                if(mob.isBoss()) {
                    inc = ForceAtomEnum.DEMON_SLAYER_FURY_1_BOSS.getInc();
                    type = ForceAtomEnum.DEMON_SLAYER_FURY_1_BOSS.getForceAtomType();
                }
                if (chr.getJob() == JobConstants.JobEnum.DEMON_SLAYER4.getJobId()) {
                    inc = ForceAtomEnum.DEMON_SLAYER_FURY_2.getInc();
                    type = ForceAtomEnum.DEMON_SLAYER_FURY_2.getForceAtomType();
                    if(mob.isBoss()) {
                        inc = ForceAtomEnum.DEMON_SLAYER_FURY_2_BOSS.getInc();
                        type = ForceAtomEnum.DEMON_SLAYER_FURY_2_BOSS.getForceAtomType();
                    }
                }
                ForceAtomInfo forceAtomInfo = new ForceAtomInfo(1, inc, speed, 5,
                        angle, 50, (int) System.currentTimeMillis(), 1, 0,
                        new Position(0, 0));
                chr.getField().broadcastPacket(CField.createForceAtom(true, chr.getId(), mobID, type,
                        true, mobID, 0, forceAtomInfo, new Rect(), 0, 300,
                        mob.getPosition(), 0, mob.getPosition()));
            } else {

            //Attacking with another skill
                int totaldmg = Arrays.stream(mai.damages).sum();
                if (totaldmg > mob.getHp()) {
                    int inc = ForceAtomEnum.DEMON_SLAYER_FURY_1.getInc();
                    int type = ForceAtomEnum.DEMON_SLAYER_FURY_1.getForceAtomType();
                    if(mob.isBoss()) {
                        inc = ForceAtomEnum.DEMON_SLAYER_FURY_1_BOSS.getInc();
                        type = ForceAtomEnum.DEMON_SLAYER_FURY_1_BOSS.getForceAtomType();
                    }
                    if (chr.getJob() == JobConstants.JobEnum.DEMON_SLAYER4.getJobId()) {
                        inc = ForceAtomEnum.DEMON_SLAYER_FURY_2.getInc();
                        type = ForceAtomEnum.DEMON_SLAYER_FURY_2.getForceAtomType();
                        if(mob.isBoss()) {
                            inc = ForceAtomEnum.DEMON_SLAYER_FURY_2_BOSS.getInc();
                            type = ForceAtomEnum.DEMON_SLAYER_FURY_2_BOSS.getForceAtomType();
                        }
                    }
                    ForceAtomInfo forceAtomInfo = new ForceAtomInfo(1, inc, speed, 5,
                            angle, 50, (int) System.currentTimeMillis(), 1, 0,
                            new Position(0, 0));
                    chr.getField().broadcastPacket(CField.createForceAtom(true, chr.getId(), mobID, type,
                            true, mobID, 0, forceAtomInfo, new Rect(), 0, 300,
                            mob.getPosition(), 0, mob.getPosition()));
                }
            }
        }
    }

    private void handlePossessedAegisFury(int mobID) {
        Field field = chr.getField();
        Life life = field.getLifeByObjectID(mobID);
        if (life instanceof Mob) {
            int angle = new Random().nextInt(40)+30;
            int speed = new Random().nextInt(31)+29;
            int inc = ForceAtomEnum.DEMON_SLAYER_FURY_1.getInc();
            int type = ForceAtomEnum.DEMON_SLAYER_FURY_1.getForceAtomType();
            if (chr.getJob() == JobConstants.JobEnum.DEMON_SLAYER4.getJobId()) {
                inc = ForceAtomEnum.DEMON_SLAYER_FURY_2.getInc();
                type = ForceAtomEnum.DEMON_SLAYER_FURY_2.getForceAtomType();
            }
            ForceAtomInfo forceAtomInfo = new ForceAtomInfo(1, inc, speed, 4,
                    angle, 50, (int) System.currentTimeMillis(), 1, 0,
                    new Position(0, 0));
            chr.getField().broadcastPacket(CField.createForceAtom(true, chr.getId(), mobID, type,
                    true, mobID, 0, forceAtomInfo, new Rect(), 0, 300,
                    life.getPosition(), 0, life.getPosition()));
        }
    }

    public void regenDFInterval() {
        chr.healMP(10);
        EventManager.addEvent(() -> regenDFInterval(), 4, TimeUnit.SECONDS);
    }


    public void handleLeechAura(AttackInfo attackInfo) {
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        if(chr.hasSkill(LEECH_AURA)) {
            if(tsm.getOptByCTSAndSkill(Regen, LEECH_AURA) != null) {
                Skill skill = chr.getSkill(LEECH_AURA);
                byte slv = (byte) skill.getCurrentLevel();
                SkillInfo si = SkillData.getSkillInfoById(skill.getSkillId());
                int cd = si.getValue(y, slv) * 1000;
                if(cd + leechAuraCD < System.currentTimeMillis()) {
                    for (MobAttackInfo mai : attackInfo.mobAttackInfo) {
                        Mob mob = (Mob) chr.getField().getLifeByObjectID(mai.mobId);
                        int totaldmg = Arrays.stream(mai.damages).sum();
                        int hpheal = (int) (totaldmg * ((double) 100 / si.getValue(x, slv)));
                        if (hpheal >= (chr.getMaxHP() / 4)) {
                            hpheal = (chr.getMaxHP() / 4);
                        }
                        leechAuraCD = System.currentTimeMillis();
                        chr.heal(hpheal);
                    }
                }
            }
        }
    }

    public void handleLifeSap() {
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        if(chr.hasSkill(LIFE_SAP)) {
            Skill skill = chr.getSkill(LIFE_SAP);
            byte slv = (byte) skill.getCurrentLevel();
            SkillInfo si = SkillData.getSkillInfoById(skill.getSkillId());
            int proc = si.getValue(prop, slv);
            int amounthealed = si.getValue(x, slv);
            if(chr.hasSkill(ADVANCED_LIFE_SAP)) {
                amounthealed = SkillData.getSkillInfoById(ADVANCED_LIFE_SAP).getValue(x, chr.getSkill(ADVANCED_LIFE_SAP).getCurrentLevel());
            }
            if(chr.hasSkill(PAIN_DAMPENER)) {
                amounthealed -= SkillData.getSkillInfoById(PAIN_DAMPENER).getValue(x, chr.getSkill(PAIN_DAMPENER).getCurrentLevel());
            }
            int exceedamount = tsm.getOption(OverloadCount).nOption;
            int exceedpenalty = (int) Math.floor(exceedamount / 5);
            amounthealed -= exceedpenalty;
            if(Util.succeedProp(proc)) {
                int healamount = (int) ((chr.getMaxHP()) / ((double)100 / amounthealed));
                chr.heal(healamount);
            }
        }
    }


    public void handleDiabolicRecovery() {
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        if(tsm.hasStat(DiabolikRecovery)) {
            Skill skill = chr.getSkill(DIABOLIC_RECOVERY);
            byte slv = (byte) skill.getCurrentLevel();
            SkillInfo si = SkillData.getSkillInfoById(skill.getSkillId());
            int recovery = si.getValue(x, slv);
            int duration = si.getValue(w, slv);
            chr.heal((int) (chr.getMaxHP() / ((double) 100 / recovery)));
            EventManager.addEvent(() -> handleDiabolicRecovery(), duration, TimeUnit.SECONDS);
        }
    }

    private void hpRCostDASkills(int skillID) {
        if(skillID == NETHER_SHIELD_ATOM || skillID == 0) {
            return;
        }
        Skill skill = chr.getSkill(SkillConstants.getActualSkillIDfromSkillID(skillID));
        byte slv = (byte) skill.getCurrentLevel();
        SkillInfo si = SkillData.getSkillInfoById(skill.getSkillId());
        int hpRCost = si.getValue(hpRCon, slv);
        if(hpRCost > 0) {
            int skillcost = (int) (chr.getMaxHP() / ((double) 100 / hpRCost));
            if(chr.getHP() < skillcost) {
                return;
            }
            chr.heal(-skillcost);
        }
    }
}

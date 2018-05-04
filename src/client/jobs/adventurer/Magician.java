package client.jobs.adventurer;

import client.Client;
import client.character.Char;
import client.character.HitInfo;
import client.character.skills.*;
import client.field.Field;
import client.jobs.Job;
import client.life.*;
import connection.InPacket;
import constants.JobConstants;
import enums.*;
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

//TODO Bishop Skills
//TODO FP - Ignite doens't create AoE
//TODO FP - Viral Slime shouldnt follow like a Summon
//TODO IL&FP - Elemental Adaptation - Buff

/**
 * Created on 12/14/2017.
 */
public class Magician extends Job {
    //Common
    public static final int MAPLE_RETURN = 1281;
    public static final int TELEPORT = 2001009;
    public static final int MAGIC_GUARD = 2001002;


    //Mage FP
    public static final int MP_EATER_FP = 2100000;
    public static final int POISON_BREATH = 2101005;
    public static final int MAGIC_BOOSTER_FP = 2101008;
    public static final int MEDITATION_FP = 2101001;
    public static final int IGNITE = 2101010;
    public static final int IGNITE_AA = 2100010;
    public static final int BURNING_MAGIC = 2110000;
    public static final int POISON_MIST = 2111003;
    public static final int TELEPORT_MASTERY_FP = 2111007;
    public static final int ELEMENTAL_DECREASE_FP = 2111008;
    public static final int ELEMENTAL_ADAPTATION_FP = 2111011;
    public static final int VIRAL_SLIME = 2111010;
    public static final int PARALYZE = 2121006;
    public static final int MIST_ERUPTION = 2121003;
    public static final int FLAME_HAZE = 2121011;
    public static final int INFINITY_FP = 2121004;
    public static final int IFRIT = 2121005;
    public static final int MAPLE_WARRIOR_FP = 2121000;
    public static final int ELEMENTAL_DRAIN = 2100009;
    public static final int FERVENT_DRAIN = 2120014;
    public static final int ARCANE_AIM_FP = 2120010;
    public static final int HEROS_WILL_FP = 2121008;


    //Mage IL
    public static final int CHILLING_STEP = 2201009;
    public static final int COLD_BEAM = 2201008;
    public static final int FREEZING_CRUSH = 2200011;   //TODO Set stacks on mobs, gain buff from those stacks
    public static final int FROST_CLUTCH = 2220015;

    public static final int MAGIC_BOOSTER_IL = 2201010;
    public static final int MEDITATION_IL = 2201001;
    public static final int ICE_STRIKE = 2211002;
    public static final int GLACIER_CHAIN = 2211010;
    public static final int THUNDER_STORM = 2211011;
    public static final int TELEPORT_MASTERY_IL = 2211007;
    public static final int ELEMENTAL_DECREASE_IL = 2211008;
    public static final int ELEMENTAL_ADAPTATION_IL = 2211012;
    public static final int CHAIN_LIGHTNING = 2221006;
    public static final int FREEZING_BREATH = 2221011;
    public static final int INFINITY_IL = 2221004;
    public static final int ELQUINES = 2221005;
    public static final int ARCANE_AIM_IL = 2220010;
    public static final int MAPLE_WARRIOR_IL = 2221000;
    public static final int HEROS_WILL_IL = 2221008;

    //Bishop
    public static final int HEAL = 2301002;
    public static final int MAGIC_BOOSTER_BISH = 2301008;
    public static final int BLESS = 2301004;
    public static final int DISPEL = 2311001;
    public static final int SHINING_RAY = 2311004;
    public static final int HOLY_MAGIC_SHELL = 2311009;
    public static final int TELEPORT_MASTERY_BISH = 2311007;
    public static final int HOLY_FOUNTAIN = 2311011;
    public static final int DIVINE_PROTECTION = 2311012;
    public static final int MYSTIC_DOOR = 2311002;
    public static final int HOLY_SYMBOL = 2311003;
    public static final int ADV_BLESSING = 2321005;
    public static final int BAHAMUT = 2321003;
    public static final int INFINITY_BISH = 2321004;
    public static final int MAPLE_WARRIOR_BISH = 2321000;
    public static final int ARCANE_AIM_BISH = 2320011;
    public static final int ANGEL_RAY = 2321007;
    public static final int HEROS_WILL_BISH = 2321009;

    //Hypers
    public static final int EPIC_ADVENTURE_FP = 2121053;
    public static final int EPIC_ADVENTURE_IL = 2221053;
    public static final int EPIC_ADVENTURE_BISH = 2321053;
    public static final int ABSOLUTE_ZERO_AURA = 2121054;
    public static final int INFERNO_AURA = 2221054;
    public static final int RIGHTEOUSLY_INDIGNANT = 2321054;
    public static final int HEAVENS_DOOR = 2321052;
    public static final int MEGIDDO_FLAME = 2121052;
    public static final int MEGIDDO_FLAME_ATOM = 2121055;


    private int[] addedSkills = new int[]{
            MAPLE_RETURN,
    };

    private final int[] buffs = new int[]{
            MAGIC_GUARD,
            IGNITE,
            MAGIC_BOOSTER_FP,
            MEDITATION_FP,
            TELEPORT_MASTERY_FP,
            ELEMENTAL_DECREASE_FP,
            ELEMENTAL_ADAPTATION_FP,
            INFINITY_FP,
            IFRIT,
            MAPLE_WARRIOR_FP,
            MEDITATION_FP,
            CHILLING_STEP,
            MAGIC_BOOSTER_IL,
            MEDITATION_IL,
            THUNDER_STORM,
            TELEPORT_MASTERY_IL,
            ELEMENTAL_DECREASE_IL,
            ELEMENTAL_ADAPTATION_IL,
            INFINITY_IL,
            ELQUINES,
            MAPLE_WARRIOR_IL,
            VIRAL_SLIME,
            MAGIC_BOOSTER_BISH,
            BLESS,
            HOLY_MAGIC_SHELL,
            TELEPORT_MASTERY_BISH,
            DIVINE_PROTECTION,
            MYSTIC_DOOR,
            HOLY_SYMBOL,
            ADV_BLESSING,
            MAPLE_WARRIOR_BISH,
            INFINITY_BISH,
            BAHAMUT,

            EPIC_ADVENTURE_FP,
            EPIC_ADVENTURE_IL,
            EPIC_ADVENTURE_BISH,
            ABSOLUTE_ZERO_AURA,
            INFERNO_AURA,
            RIGHTEOUSLY_INDIGNANT,
    };

    public static int hmshits = 0;
    private int ferventDrainStack = 0;
    private int elementalAdaptationFP = 5;
    private int infinityStack = 0;

    public Magician(Char chr) {
        super(chr);
        if (isHandlerOfJob(chr.getJob())) {
            for (int id : addedSkills) {
                if (!chr.hasSkill(id)) {
                    Skill skill = SkillData.getSkillDeepCopyById(id);
                    skill.setCurrentLevel(skill.getMasterLevel());
                    chr.addSkill(skill);
                }
            }
        }
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

        if (hasHitMobs) {
            //handleArcaneAim();
        }

        if (JobConstants.isFirePoison(chr.getJob())) {
            if(hasHitMobs) {
                //Ignite
                handleIgnite(attackInfo, chr, tsm);

                //Megiddo Flame Recreation
                if(attackInfo.skillId == MEGIDDO_FLAME_ATOM) {
                    handleMegiddoFlameReCreation(skillID, slv, attackInfo);
                }
            }
            chr.chatMessage(ChatMsgColour.WHISPER_GREEN, "Elemental Drain Stack: " + getFerventDrainStack());
        }

        if (JobConstants.isIceLightning(chr.getJob())) {
            if(hasHitMobs) {
                //Freezing Crush / Frozen Clutch
                handleFreezingCrush(attackInfo, slv);

            }
        }

        if (JobConstants.isCleric(chr.getJob())) {
            if(hasHitMobs) {

            }
        }

        Option o1 = new Option();
        Option o2 = new Option();
        Option o3 = new Option();
        switch (skillID) {
            case POISON_BREATH:
                for (MobAttackInfo mai : attackInfo.mobAttackInfo) {
                    if (Util.succeedProp(si.getValue(prop, slv))) {
                        Mob mob = (Mob) chr.getField().getLifeByObjectID(mai.mobId);
                        MobTemporaryStat mts = mob.getTemporaryStat();
                        fpBurnedInfo(mob, skill, slv);
                    }
                }
                break;
            case POISON_MIST:
                AffectedArea aa = AffectedArea.getAffectedArea(chr, attackInfo);
                aa.setMobOrigin((byte) 0);
                int x = attackInfo.forcedX;
                int y = attackInfo.forcedY;
                aa.setPosition(new Position(x, y));
                aa.setRect(aa.getPosition().getRectAround(si.getRects().get(0)));
                aa.setDelay((short) 9);
                chr.getField().spawnAffectedArea(aa);
                break;
            case TELEPORT_MASTERY_FP:
                for (MobAttackInfo mai : attackInfo.mobAttackInfo) {
                    if (Util.succeedProp(si.getValue(prop, slv))) {
                        Mob mob = (Mob) chr.getField().getLifeByObjectID(mai.mobId);
                        MobTemporaryStat mts = mob.getTemporaryStat();
                        o1.nOption = 1;
                        o1.rOption = skill.getSkillId();
                        o1.tOption = si.getValue(time, slv);
                        mts.addStatOptions(MobStat.Stun, o1);
                        fpBurnedInfo(mob, skill, slv);
                    }
                }
                break;
            case FLAME_HAZE:
                for (MobAttackInfo mai : attackInfo.mobAttackInfo) {
                    Mob mob = (Mob) chr.getField().getLifeByObjectID(mai.mobId);
                    if (Util.succeedProp(si.getValue(prop, slv))) {
                        MobTemporaryStat mts = mob.getTemporaryStat();
                        o1.nOption = 1;
                        o1.rOption = skill.getSkillId();
                        o1.tOption = si.getValue(time, slv);
                        mts.addStatOptions(MobStat.Invincible, o1);
                        o1.nOption = 1;
                        o1.rOption = skill.getSkillId();
                        o1.tOption = si.getValue(time, slv);
                        mts.addStatOptions(MobStat.Speed, o1);
                        fpBurnedInfo(mob, skill, slv); //Global Burned Info Handler to regulate Fervent Drain/Element Drain
                    }
                    AffectedArea aa2 = AffectedArea.getAffectedArea(chr, attackInfo);
                    aa2.setMobOrigin((byte) 0);
                    int x2 = mob.getX();
                    int y2 = mob.getY();
                    aa2.setPosition(new Position(x2, y2));
                    aa2.setRect(aa2.getPosition().getRectAround(si.getRects().get(0)));
                    chr.getField().spawnAffectedArea(aa2);
                }
                break;
            case IFRIT:
                for (MobAttackInfo mai : attackInfo.mobAttackInfo) {
                    Mob mob = (Mob) chr.getField().getLifeByObjectID(mai.mobId);
                    fpBurnedInfo(mob, skill, slv);
                }
                break;
            case PARALYZE:
                for (MobAttackInfo mai : attackInfo.mobAttackInfo) {
                    Mob mob = (Mob) chr.getField().getLifeByObjectID(mai.mobId);
                    MobTemporaryStat mts = mob.getTemporaryStat();
                    o1.nOption = 1;
                    o1.rOption = skillID;
                    o1.tOption = si.getValue(time, slv);
                    mts.addStatOptionsAndBroadcast(MobStat.Stun, o1);
                    fpBurnedInfo(mob, skill, slv);
                }
                break;
            case COLD_BEAM:
            case ICE_STRIKE:
            case GLACIER_CHAIN:
                for (MobAttackInfo mai : attackInfo.mobAttackInfo) {
                    Mob mob = (Mob) chr.getField().getLifeByObjectID(mai.mobId);
                    MobTemporaryStat mts = mob.getTemporaryStat();
                    o1.nOption = 5;
                    o1.rOption = skillID;
                    o1.tOption = si.getValue(time, slv);
                    mts.addStatOptionsAndBroadcast(MobStat.Freeze, o1);
                }
                break;
            case TELEPORT_MASTERY_IL:
            case CHAIN_LIGHTNING:
            case SHINING_RAY:
                for (MobAttackInfo mai : attackInfo.mobAttackInfo) {
                    if (Util.succeedProp(si.getValue(prop, slv))) {
                        Mob mob = (Mob) chr.getField().getLifeByObjectID(mai.mobId);
                        MobTemporaryStat mts = mob.getTemporaryStat();
                        o1.nOption = 1;
                        o1.rOption = skillID;
                        o1.tOption = si.getValue(time, slv);
                        mts.addStatOptionsAndBroadcast(MobStat.Stun, o1);
                    }
                }
                break;
            case MIST_ERUPTION:
                for (int id : attackInfo.mists) {
                    Field field = chr.getField();
                    field.removeLife(id);
                }
                break;
            case BAHAMUT:
                for (MobAttackInfo mai : attackInfo.mobAttackInfo) {
                    Mob mob = (Mob) chr.getField().getLifeByObjectID(mai.mobId);
                    MobTemporaryStat mts = mob.getTemporaryStat();
                    o1.nOption = 25;
                    o1.rOption = skillID;
                    o1.tOption = si.getValue(subTime, slv);
                    mts.addStatOptionsAndBroadcast(MobStat.AddDamParty, o1);
                }
                break;
            case HEAVENS_DOOR:
                o1.nOption = 1;
                o1.rOption = HEAVENS_DOOR;
                o1.tOption = 0;
                tsm.putCharacterStatValue(ReviveOnce, o1);
                c.write(WvsContext.temporaryStatSet(tsm));
                break;
            case ANGEL_RAY:
                chr.heal(handleBishopHealingSkills(ANGEL_RAY));
                break;
            case MEGIDDO_FLAME_ATOM:    //TODO - Megiddo DoT
            case MEGIDDO_FLAME:
                Skill megiddoSkill = chr.getSkill(MEGIDDO_FLAME);
                for (MobAttackInfo mai : attackInfo.mobAttackInfo) {
                    Mob mob = (Mob) chr.getField().getLifeByObjectID(mai.mobId);
                    MobTemporaryStat mts = mob.getTemporaryStat();
                    mts.createAndAddBurnedInfo(chr, megiddoSkill, 1); //TODO change to the FP global Burned Info Handler
                }
                break;
        }

    }

    private void handleMegiddoFlame() {
        Field field = chr.getField();
        SkillInfo si = SkillData.getSkillInfoById(MEGIDDO_FLAME);
        Rect rect = chr.getPosition().getRectAround(si.getRects().get(0));
        if (!chr.isLeft()) {
            rect = rect.moveRight();
        }
        List<Mob> lifes = field.getMobsInRect(rect);
        Mob life = Util.getRandomFromList(lifes);
        int mobID2 = (life).getObjectId();
        int inc = ForceAtomEnum.DA_ORB.getInc();
        int type = ForceAtomEnum.DA_ORB.getForceAtomType();
        ForceAtomInfo forceAtomInfo = new ForceAtomInfo(1, inc, 20, 40,
                0, 500, (int) System.currentTimeMillis(), 1, 0,
                new Position(0, -100));
        chr.getField().broadcastPacket(CField.createForceAtom(false, 0, chr.getId(), type,
                true, mobID2, MEGIDDO_FLAME_ATOM, forceAtomInfo, new Rect(), 0, 300,
                life.getPosition(), MEGIDDO_FLAME_ATOM, life.getPosition()));

    }

    private void handleMegiddoFlameReCreation(int skillID, byte slv, AttackInfo attackInfo) {
        SkillInfo si = SkillData.getSkillInfoById(MEGIDDO_FLAME);
        int anglenum = new Random().nextInt(360);
        for (MobAttackInfo mai : attackInfo.mobAttackInfo) {
            Mob mob = (Mob) chr.getField().getLifeByObjectID(mai.mobId);
            int TW1prop = 85;//
            if (Util.succeedProp(TW1prop)) {
                int mobID = mai.mobId;

                int inc = ForceAtomEnum.DA_ORB_RECREATION.getInc();
                int type = ForceAtomEnum.DA_ORB_RECREATION.getForceAtomType();
                ForceAtomInfo forceAtomInfo = new ForceAtomInfo(1, inc, 30, 5,
                        anglenum, 0, (int) System.currentTimeMillis(), 1, 0,
                        new Position(0, 0));
                chr.getField().broadcastPacket(CField.createForceAtom(true, chr.getId(), mobID, type,
                        true, mobID, MEGIDDO_FLAME_ATOM, forceAtomInfo, new Rect(), 0, 300,
                        mob.getPosition(), MEGIDDO_FLAME_ATOM, mob.getPosition()));
            }
        }
    }

    private void handleArcaneAim() {
        Skill skill = chr.getSkill(getArcaneAimSkill());
        if (skill == null) {
            return;
        }
        SkillInfo arcaneAimInfo = SkillData.getSkillInfoById(skill.getSkillId());
        byte slv = (byte) skill.getCurrentLevel();
        int arcaneAimProp = arcaneAimInfo.getValue(prop, slv);
        if (!Util.succeedProp(arcaneAimProp)) {
            return;
        }
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        Option o = new Option();
        Option o1 = new Option();
        Option o2 = new Option();
        int amount = 1;
        if (tsm.hasStat(ArcaneAim)) {
            amount = tsm.getOption(ArcaneAim).nOption;
            if (amount < arcaneAimInfo.getValue(y, slv)) {
                amount++;
            }
        }
        o.nOption = amount;
        o.rOption = 2320011;
        o.tOption = 5; // No Time Variable
        tsm.putCharacterStatValue(ArcaneAim, o);
        o1.nOption = arcaneAimInfo.getValue(ignoreMobpdpR, slv);
        o1.rOption = 2320011;
        o1.tOption = 5; // No Time Variable
        tsm.putCharacterStatValue(IgnoreMobpdpR, o1);
        o2.nOption = (amount * arcaneAimInfo.getValue(x, slv));
        o2.rOption = 2320011;
        o2.tOption = 5; // No Time Variable
        tsm.putCharacterStatValue(DamR, o2);
        c.write(WvsContext.temporaryStatSet(tsm));
    }

    private void handleIgnite(AttackInfo attackInfo, Char chr, TemporaryStatManager tsm) {  //TODO  Fire Attribute Spells only
        if (tsm.hasStat(WizardIgnite)) {
            SkillInfo igniteInfo = SkillData.getSkillInfoById(IGNITE);
            Skill skill = chr.getSkill(IGNITE);
            byte slv = (byte) skill.getCurrentLevel();
            for (MobAttackInfo mai : attackInfo.mobAttackInfo) {
                Mob mob = (Mob) chr.getField().getLifeByObjectID(mai.mobId);
                if (Util.succeedProp(igniteInfo.getValue(prop, slv))) {
                    AffectedArea aa = AffectedArea.getPassiveAA(chr, IGNITE, (byte) 10);
                    aa.setMobOrigin((byte) 1);
                    aa.setPosition(mob.getPosition());
                    aa.setRect(aa.getPosition().getRectAround(igniteInfo.getRects().get(0)));
                    aa.setDelay((short) 2);
                    aa.setSkillID(IGNITE_AA);
                    chr.getField().spawnAffectedArea(aa);
                }
            }
        }
    }

    private void handleChillingStep(Char chr) {
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        if (tsm.hasStat(ChillingStep)) {
            for (int i = 0; i < 168; i += 56) {
                SkillInfo chillingStepInfo = SkillData.getSkillInfoById(CHILLING_STEP);
                Skill skill = chr.getSkill(CHILLING_STEP);
                int slv = skill.getCurrentLevel();
                AffectedArea aa = AffectedArea.getPassiveAA(chr, CHILLING_STEP, (byte) slv);
                aa.setMobOrigin((byte) 0);
                int x = chr.isLeft() ? chr.getPosition().getX() - i : chr.getPosition().getX() + i;
                int y = chr.getPosition().getY();
                aa.setPosition(new Position(x, y));
                aa.setRect(aa.getPosition().getRectAround(chillingStepInfo.getRects().get(0)));
                aa.setCurFoothold();
                aa.setDelay((short) 4);
                chr.getField().spawnAffectedArea(aa);
            }
        }
    }

    @Override
    public void handleSkill(Client c, int skillID, byte slv, InPacket inPacket) {
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
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
                case MAPLE_RETURN:
                    o1.nValue = si.getValue(x, slv);
                    Field toField = c.getChannelInstance().getField(o1.nValue);
                    chr.warp(toField);
                    break;
                case FREEZING_BREATH:
                    Rect rect = chr.getPosition().getRectAround(si.getRects().get(0));
                    if (!chr.isLeft()) {
                        rect = rect.moveRight();
                    }
                    for (Life life : chr.getField().getLifesInRect(rect)) {
                        if (life instanceof Mob && ((Mob) life).getHp() > 0) {
                            Mob mob = (Mob) life;
                            MobTemporaryStat mts = mob.getTemporaryStat();
                            o1.nOption = 1;
                            o1.rOption = skillID;
                            o1.tOption = 25;
                            mts.addStatOptionsAndBroadcast(MobStat.Freeze, o1);
                            o2.nOption = si.getValue(x, slv);
                            o2.rOption = skillID;
                            o2.tOption = 5;
                            mts.addStatOptionsAndBroadcast(MobStat.PDR, o2);
                            o3.nOption = si.getValue(y, slv);
                            o3.rOption = skillID;
                            o3.tOption = 5;
                            mts.addStatOptionsAndBroadcast(MobStat.MDR, o3);
                        }
                    }
                    o1.nOption = 1;
                    o1.rOption = skillID;
                    o1.tOption = 1;
                    tsm.putCharacterStatValue(NotDamaged, o1);
                    c.write(WvsContext.temporaryStatSet(tsm));
                    break;
                case MEGIDDO_FLAME:
                    handleMegiddoFlame();
                    break;
                case HOLY_FOUNTAIN:
                    AffectedArea aa = AffectedArea.getPassiveAA(chr, skillID, slv);
                    aa.setMobOrigin((byte) 0);
                    aa.setPosition(chr.getPosition());
                    aa.setRect(aa.getPosition().getRectAround(si.getRects().get(0)));
                    aa.setDelay((short) 4);
                    chr.getField().spawnAffectedArea(aa);
                    break;
                case TELEPORT:
                    handleChillingStep(chr);
                    break;
                case HEAL:
                    chr.heal(handleBishopHealingSkills(HEAL));
                    Rect rect3 = new Rect(inPacket.decodeShort(), inPacket.decodeShort()
                            , inPacket.decodeShort(), inPacket.decodeShort());
                    for (Life life : chr.getField().getLifesInRect(rect3)) {
                        if (life instanceof Mob && ((Mob) life).getHp() > 0) {
                            Mob mob = (Mob) life;
                            MobTemporaryStat mts = mob.getTemporaryStat();
                            o1.nOption = si.getValue(x, slv);
                            o1.rOption = skillID;
                            o1.tOption = si.getValue(time, slv);
                            mts.addStatOptionsAndBroadcast(MobStat.AddDamParty, o1);
                        }
                    }
                    break;
                case DISPEL:
                    tsm.removeAllDebuffs();
                    break;
                case HEROS_WILL_FP:
                case HEROS_WILL_IL:
                case HEROS_WILL_BISH:
                    tsm.removeAllDebuffs();
                    break;
            }
        }
    }

    @Override
    public void handleHit(Client c, InPacket inPacket, HitInfo hitInfo) {
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        if(tsm.hasStat(MagicGuard)) {
            Skill skill = chr.getSkill(MAGIC_GUARD);
            SkillInfo si = SkillData.getSkillInfoById(MAGIC_GUARD);
            int dmgPerc = si.getValue(x, skill.getCurrentLevel());
            int dmg = hitInfo.HPDamage;
            int mpDmg = (int) (dmg * (dmgPerc / 100D));
            mpDmg = chr.getStat(Stat.mp) - mpDmg < 0 ? chr.getStat(Stat.mp) : mpDmg;
            hitInfo.HPDamage = dmg - mpDmg;
            hitInfo.MPDamage = mpDmg;
        }

        if(tsm.getOptByCTSAndSkill(AntiMagicShell, ELEMENTAL_ADAPTATION_FP) != null) {
            eleAdaptationFP();
        }

        super.handleHit(c, inPacket, hitInfo);
    }


    public boolean isBuff(int skillID) {
        return Arrays.stream(buffs).anyMatch(b -> b == skillID);
    }

    private void handleBuff(Client c, InPacket inPacket, int skillID, byte slv) {
        Char chr = c.getChr();
        SkillInfo si = SkillData.getSkillInfoById(skillID);
        TemporaryStatManager tsm = c.getChr().getTemporaryStatManager();
        Option o1 = new Option();
        Option o2 = new Option();
        Option o3 = new Option();
        Option o4 = new Option();
        Option o5 = new Option();
        Option o6 = new Option();
        Option o7 = new Option();
        Summon summon;
        Field field;
        switch (skillID) {
            case MAGIC_GUARD:
                o1.nOption = si.getValue(x, slv);
                o1.rOption = skillID;
                o1.tOption = 0;
                tsm.putCharacterStatValue(MagicGuard, o1);
                break;
            case MAGIC_BOOSTER_FP:
            case MAGIC_BOOSTER_IL:
            case MAGIC_BOOSTER_BISH:
                o1.nOption = si.getValue(x, slv);
                o1.rOption = skillID;
                o1.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(Booster, o1);
                break;
            case MEDITATION_FP:
            case MEDITATION_IL:
                o1.nValue = si.getValue(indieMad, slv);
                o1.nReason = skillID;
                o1.tStart = (int) System.currentTimeMillis();
                o1.tTerm = si.getValue(time, slv);
                tsm.putCharacterStatValue(IndieMAD, o1);
                break;
            case IGNITE:
                o1.nOption = 1;
                o1.rOption = skillID;
                o1.tOption = 0;
                tsm.putCharacterStatValue(WizardIgnite, o1);
                break;
            case ELEMENTAL_DECREASE_FP:
            case ELEMENTAL_DECREASE_IL:
                o1.nOption = si.getValue(x, slv);
                o1.rOption = skillID;
                o1.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(ElementalReset, o1);
                break;
            case ELEMENTAL_ADAPTATION_FP:
                o1.nOption = 6;
                o1.rOption = skillID;
                tsm.putCharacterStatValue(AntiMagicShell, o1);
                break;
            case DIVINE_PROTECTION:
            case ELEMENTAL_ADAPTATION_IL:
                o1.nOption = 1;
                o1.rOption = skillID;
                o1.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(AntiMagicShell, o1);
                o2.nOption = 1;
                o2.rOption = skillID;
                o2.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(DamAbsorbShield, o2);
                break;
            case TELEPORT_MASTERY_FP:
            case TELEPORT_MASTERY_IL:
            case TELEPORT_MASTERY_BISH:
                o1.nOption = 1;
                o1.rOption = skillID;
                o1.tOption = 0;
                tsm.putCharacterStatValue(TeleportMasteryOn, o1);
                break;
            case INFINITY_FP:
            case INFINITY_IL:
            case INFINITY_BISH:
                o1.nOption = 1;
                o1.rOption = skillID;
                o1.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(Infinity, o1);
                o2.nOption = si.getValue(prop, slv);
                o2.rOption = skillID;
                o2.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(Stance, o2);
                infinityStack = 0;
                infinity();
                break;
            case VIRAL_SLIME:
                summon = Summon.getSummonBy(c.getChr(), skillID, slv);
                field = c.getChr().getField();
                summon.setFlyMob(true);
                summon.setMoveAbility(MoveAbility.SLOW_FORWARD.getVal());
                field.spawnSummon(summon);
                break;
            case BLESS:
                o1.nOption = si.getValue(u, slv);
                o1.rOption = skillID;
                o1.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(PDD, o1);
                tsm.putCharacterStatValue(MDD, o1);
                o2.nOption = si.getValue(v, slv);
                o2.rOption = skillID;
                o2.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(EVA, o2);
                tsm.putCharacterStatValue(ACC, o2);
                o3.nOption = si.getValue(x, slv);
                o3.rOption = skillID;
                o3.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(PAD, o3);
                tsm.putCharacterStatValue(MAD, o3);
                break;
            case ADV_BLESSING:
                o1.nOption = si.getValue(u, slv);
                o1.rOption = skillID;
                o1.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(PDD, o1);
                tsm.putCharacterStatValue(MDD, o1);
                o2.nOption = si.getValue(v, slv);
                o2.rOption = skillID;
                o2.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(EVA, o2);
                tsm.putCharacterStatValue(ACC, o2);
                o3.nOption = si.getValue(x, slv);
                o3.rOption = skillID;
                o3.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(PAD, o3);
                tsm.putCharacterStatValue(MAD, o3);
                o4.nValue = si.getValue(indieMhp, slv);
                o4.nReason = skillID;
                o4.tStart = (int) System.currentTimeMillis();
                o4.tTerm = si.getValue(time, slv);
                tsm.putCharacterStatValue(IndieMHP, o4);
                tsm.putCharacterStatValue(IndieMMP, o4);
                break;
            case HOLY_SYMBOL:
                o1.nOption = si.getValue(x, slv);
                o1.rOption = skillID;
                o1.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(HolySymbol, o1);
                break;
            case HOLY_MAGIC_SHELL:
                o1.nOption = 1;
                o1.rOption = skillID;
                o1.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(HolyMagicShell, o1);
                hmshits = 0;
                chr.heal(handleBishopHealingSkills(HOLY_MAGIC_SHELL));
                break;
            case IFRIT:
            case ELQUINES:
            case BAHAMUT:
                summon = Summon.getSummonBy(c.getChr(), skillID, slv);
                field = c.getChr().getField();
                summon.setFlyMob(true);
                summon.setMoveAbility(MoveAbility.FOLLOW.getVal());
                field.spawnSummon(summon);
                break;
            case MAPLE_WARRIOR_FP:
            case MAPLE_WARRIOR_IL:
            case MAPLE_WARRIOR_BISH:
                o1.nValue = si.getValue(x, slv);
                o1.nReason = skillID;
                o1.tStart = (int) System.currentTimeMillis();
                o1.tTerm = si.getValue(time, slv);
                tsm.putCharacterStatValue(IndieStatR, o1);
                break;
            case THUNDER_STORM:
                summon = Summon.getSummonBy(c.getChr(), skillID, slv);
                field = c.getChr().getField();
                summon.setFlyMob(true);
                field.spawnSummon(summon);
                break;
            case CHILLING_STEP:
                o1.nOption = 1;
                o1.rOption = skillID;
                tsm.putCharacterStatValue(ChillingStep, o1);
                break;

            case EPIC_ADVENTURE_FP:
            case EPIC_ADVENTURE_IL:
            case EPIC_ADVENTURE_BISH:
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
            case ABSOLUTE_ZERO_AURA:
                o1.nOption = 1;
                o1.rOption = skillID;
                o1.tOption = 0;
                tsm.putCharacterStatValue(FireAura, o1);
                o2.nOption = si.getValue(x, slv);
                o2.rOption = skillID;
                o2.tOption = 0;
                tsm.putCharacterStatValue(Stance, o2);
                o3.nOption = si.getValue(y, slv);
                o3.rOption = skillID;
                o3.tOption = 0;
                tsm.putCharacterStatValue(DamAbsorbShield, o3);
                o4.nOption = si.getValue(v, slv);
                o4.rOption = skillID;
                o4.tOption = 0;
                tsm.putCharacterStatValue(AsrR, o4);
                tsm.putCharacterStatValue(TerR, o4);
                break;
            case INFERNO_AURA:
                o1.nOption = 1;
                o1.rOption = skillID;
                o1.tOption = 0;
                tsm.putCharacterStatValue(IceAura, o1);
                break;
            case RIGHTEOUSLY_INDIGNANT:
                o1.nOption = 1;
                o1.rOption = skillID;
                o1.tOption = 0;
                tsm.putCharacterStatValue(VengeanceOfAngel, o1);
                o2.nReason = skillID;
                o2.nValue = si.getValue(indieMad, slv);
                o2.tStart = (int) System.currentTimeMillis();
                o2.tTerm = 0;
                tsm.putCharacterStatValue(IndieMAD, o2);
                o3.nReason = skillID;
                o3.nValue = si.getValue(indiePMdR, slv);
                o3.tStart = (int) System.currentTimeMillis();
                o3.tTerm = 0;
                tsm.putCharacterStatValue(IndiePMdR, o3);
                o4.nReason = skillID;
                o4.nValue = si.getValue(indieMaxDamageOver, slv);
                o4.tStart = (int) System.currentTimeMillis();
                o4.tTerm = 0;
                tsm.putCharacterStatValue(IndieMaxDamageOver, o4);
                o5.nReason = skillID;
                o5.nValue = si.getValue(indieBooster, slv);
                o5.tStart = (int) System.currentTimeMillis();
                o5.tTerm = 0;
                tsm.putCharacterStatValue(IndieBooster, o5);
                o6.nOption = si.getValue(ignoreMobpdpR, slv);
                o6.rOption = skillID;
                o6.tOption = 0;
                tsm.putCharacterStatValue(IgnoreMobpdpR, o6);
                o7.nOption = si.getValue(w, slv);
                o7.rOption = skillID;
                o7.tOption = 0;
                tsm.putCharacterStatValue(ElementalReset, o7);
                break;

        }
        c.write(WvsContext.temporaryStatSet(tsm));
    }

    @Override
    public boolean isHandlerOfJob(short id) {
        JobConstants.JobEnum jobEnum = JobConstants.JobEnum.getJobById(id);
        switch (jobEnum) {
            case MAGICIAN:
            case FP_WIZARD:
            case FP_MAGE:
            case FP_ARCHMAGE:
            case IL_WIZARD:
            case IL_MAGE:
            case IL_ARCHMAGE:
            case CLERIC:
            case PRIEST:
            case BISHOP:
                return true;
            default:
                return false;
        }
    }

    @Override
    public int getFinalAttackSkill() {
        return 0;
    }

    private int getArcaneAimSkill() {
        int res = 0;
        if (chr.hasSkill(ARCANE_AIM_FP)) {
            res = ARCANE_AIM_FP;
        } else if (chr.hasSkill(ARCANE_AIM_IL)) {
            res = ARCANE_AIM_IL;
        } else if (chr.hasSkill(ARCANE_AIM_BISH)) {
            res = ARCANE_AIM_BISH;
        }
        return res;
    }

    private void handleFreezingCrush(AttackInfo attackInfo, byte slv) {
        Option o1 = new Option();
        Option o2 = new Option();
        SkillInfo si = SkillData.getSkillInfoById(FROST_CLUTCH);
        for (MobAttackInfo mai : attackInfo.mobAttackInfo) {
            //if (Util.succeedProp(si.getValue(prop, slv))) {
            Mob mob = (Mob) chr.getField().getLifeByObjectID(mai.mobId);
            MobTemporaryStat mts = mob.getTemporaryStat();
            o1.nOption = -10;
            o1.rOption = FREEZING_CRUSH;
            o1.tOption = 5; //si.getValue(subTime, slv);
            o1.mOption = 2; //Should be Amount
            mts.addStatOptionsAndBroadcast(MobStat.Speed, o1); // IDA says it's Speed, but it doesn't broadcast the Debuff over the mob

            o2.nOption = 21;
            o2.rOption = 100001;
            o2.tOption = 5;
            o2.cOption = 2;
            mts.addStatOptionsAndBroadcast(MobStat.Freeze, o2);
            //}
        }
    }

    private int handleBishopHealingSkills(int skillID) {
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        Skill skill = chr.getSkill(skillID);
        byte slv = (byte) skill.getCurrentLevel();
        SkillInfo si = SkillData.getSkillInfoById(skillID);
        int rate = 0;
        int maxHP = chr.getMaxHP();
        int healrate = 0;
        switch (skillID) {
            case HEAL:
                rate = si.getValue(hp, slv);
                healrate = maxHP;// (int) (maxHP / ((double)100 / rate));
                break;
            case HOLY_MAGIC_SHELL:
                rate = si.getValue(z, slv);
                healrate = (int) (maxHP / ((double)100 / rate));
                break;
            case ANGEL_RAY:
                rate = si.getValue(hp, slv);
                healrate = (int) (maxHP / ((double)100 / rate));
                break;
            case INFINITY_BISH:

                break;
        }
        if(tsm.hasStat(VengeanceOfAngel)) {
            SkillInfo hsi = SkillData.getSkillInfoById(RIGHTEOUSLY_INDIGNANT);
            healrate = (int) (healrate / ((double) 100 / (hsi.getValue(hp, 1)))); //TODO
        }
        return healrate;
    }

    public static int getHolyMagicShellMaxGuards(Char chr) {
        int num = 9;
        if(chr.hasSkill(2320043)) { //Extra 2 Guards  Hyper Skill
            num = 11;
        }
        return num;
    }

    //Elemental/Fervent Drain - FP
    public int getFerventDrainStack() {
        return ferventDrainStack;
    }

    public void setFerventDrainStack(int ferventDrainStack) {
        this.ferventDrainStack = ferventDrainStack;
    }

    private int getElementalDrainSkill() {
        int res = 0;
        if (chr.hasSkill(FERVENT_DRAIN)) {
            res = FERVENT_DRAIN;
        } else if (chr.hasSkill(ELEMENTAL_DRAIN)) {
            res = ELEMENTAL_DRAIN;
        }
        return res;
    }

    private void fpBurnedInfo(Mob mob, Skill attackSkill, byte attackSlv) {
        Skill skill = chr.getSkill(getElementalDrainSkill());
        SkillInfo edi = SkillData.getSkillInfoById(skill.getSkillId());
        byte edLv = (byte) skill.getCurrentLevel();
        int biDuration = SkillData.getSkillInfoById(attackSkill.getSkillId()).getValue(dotTime, attackSlv);
        MobTemporaryStat mts = mob.getTemporaryStat();
        mts.createAndAddBurnedInfo(chr, attackSkill, attackSlv);
        setFerventDrainStack(getFerventDrainStack() + 1);
        updateElementDrain();
        EventManager.addEvent(this::eventSetFerventDrain, biDuration, TimeUnit.SECONDS);
    }

    private void eventSetFerventDrain() {
        setFerventDrainStack(getFerventDrainStack() - 1);
        updateElementDrain();
    }

    private void updateElementDrain() {
        if(!chr.hasSkill(ELEMENTAL_DRAIN)) {
            return;
        }
        Skill skill = chr.getSkill(getElementalDrainSkill());
        SkillInfo edi = SkillData.getSkillInfoById(skill.getSkillId());
        byte slv = (byte) skill.getCurrentLevel();
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        Option o = new Option();
        Option o1 = new Option();

        o.nOption = (getFerventDrainStack() > 5 ? 5 : getFerventDrainStack());
        o.rOption = ELEMENTAL_DRAIN;
        tsm.putCharacterStatValue(DotBasedBuff, o);

        o1.nOption = ( (getFerventDrainStack() > 5 ? 5 : getFerventDrainStack()) * edi.getValue(x, slv) );
        o1.rOption = ELEMENTAL_DRAIN;
        tsm.putCharacterStatValue(DamR, o1);
        if(getFerventDrainStack() <= 0) {
            tsm.removeStatsBySkill(ELEMENTAL_DRAIN);
            tsm.sendResetStatPacket();
        } else {
            tsm.sendSetStatPacket();
        }
        chr.chatMessage(ChatMsgColour.WHISPER_GREEN, "Elemental Drain Stack: " + getFerventDrainStack());
    }

    //Elemental Adaptation - FP
    private void eleAdaptationFP() {
        if(!chr.hasSkill(ELEMENTAL_ADAPTATION_FP)) {
            return;
        }
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        Option o = new Option();
        Skill skill = chr.getSkill(ELEMENTAL_ADAPTATION_FP);
        byte slv = (byte) skill.getCurrentLevel();
        SkillInfo si = SkillData.getSkillInfoById(skill.getSkillId());
        int proc = si.getValue(prop, slv);

        int stack = tsm.getOption(AntiMagicShell).nOption;
        if(stack > 0) {
            if(Util.succeedProp(proc)) {
                stack--;

                o.nOption = stack;
                o.rOption = ELEMENTAL_ADAPTATION_FP;
                tsm.putCharacterStatValue(AntiMagicShell, o);
                tsm.sendSetStatPacket();
            } else {
                tsm.removeStatsBySkill(ELEMENTAL_ADAPTATION_FP);
                tsm.sendResetStatPacket();
            }
        } else {
            tsm.removeStatsBySkill(ELEMENTAL_ADAPTATION_FP);
            tsm.sendResetStatPacket();
        }
    }

    private void infinity() {
        if(!chr.hasSkill(getInfinitySkill())) {
            return;
        }
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        Option o1 = new Option();
        Skill skill = chr.getSkill(getInfinitySkill());
        byte slv = (byte) skill.getCurrentLevel();
        SkillInfo si = SkillData.getSkillInfoById(skill.getSkillId());
        infinityStack++;
        if(tsm.hasStat(Infinity)) {
            o1.nValue = infinityStack * si.getValue(damage, slv);
            o1.nReason = getInfinitySkill()+100; //To make the buff icon hidden
            o1.tStart = (int) System.currentTimeMillis();
            tsm.putCharacterStatValue(IndieMADR, o1);
            tsm.sendSetStatPacket();

            chr.heal((int) (chr.getMaxHP() / ((double) 100 / si.getValue(y, slv))));

            EventManager.addEvent(this::infinity, 4, TimeUnit.SECONDS);
        } else {
            tsm.removeStatsBySkill(getInfinitySkill()+100);
            tsm.sendResetStatPacket();
            infinityStack = 0;
        }
    }

    private int getInfinitySkill() {
        int skill = 0;
        if(chr.hasSkill(INFINITY_FP)) {
            skill = INFINITY_FP;
        }
        if(chr.hasSkill(INFINITY_IL)) {
            skill = INFINITY_IL;
        }
        if(chr.hasSkill(INFINITY_BISH)) {
            skill = INFINITY_BISH;
        }
        return skill;
    }
}


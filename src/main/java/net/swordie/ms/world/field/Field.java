package net.swordie.ms.world.field;

import net.swordie.ms.client.Client;
import net.swordie.ms.client.character.Char;
import net.swordie.ms.client.character.items.Item;
import net.swordie.ms.client.character.runestones.RuneStone;
import net.swordie.ms.client.character.skills.info.SkillInfo;
import net.swordie.ms.client.character.skills.temp.TemporaryStatManager;
import net.swordie.ms.connection.OutPacket;
import net.swordie.ms.connection.packet.*;
import net.swordie.ms.constants.GameConstants;
import net.swordie.ms.constants.ItemConstants;
import net.swordie.ms.enums.DropLeaveType;
import net.swordie.ms.enums.EliteState;
import net.swordie.ms.enums.LeaveType;
import net.swordie.ms.handlers.EventManager;
import net.swordie.ms.life.AffectedArea;
import net.swordie.ms.life.Life;
import net.swordie.ms.life.Reactor;
import net.swordie.ms.life.Summon;
import net.swordie.ms.life.drop.Drop;
import net.swordie.ms.life.drop.DropInfo;
import net.swordie.ms.life.mob.Mob;
import net.swordie.ms.loaders.ItemData;
import net.swordie.ms.loaders.MobData;
import net.swordie.ms.loaders.SkillData;
import net.swordie.ms.scripts.ScriptManagerImpl;
import net.swordie.ms.scripts.ScriptType;
import net.swordie.ms.util.Position;
import net.swordie.ms.util.Rect;
import org.apache.log4j.Logger;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static net.swordie.ms.client.character.skills.SkillStat.time;

/**
 * Created on 12/14/2017.
 */
public class Field {
    private static final Logger log = Logger.getLogger(Field.class);
    private Rectangle rect;
    private double mobRate;
    private int id;
    private int returnMap, forcedReturn, createMobInterval, timeOut, timeLimit, lvLimit, lvForceMove;
    private int consumeItemCoolTime, link;
    private long uniqueId;
    private boolean town, swim, fly, reactorShuffle, expeditionOnly, partyOnly, needSkillForFly;
    private Set<Portal> portals;
    private Set<Foothold> footholds;
    private List<Life> lifes;
    private List<Char> chars;
    private Map<Life, Char> lifeToControllers;
    private Map<Life, ScheduledFuture> lifeSchedules;
    private String onFirstUserEnter = "", onUserEnter = "";
    private int fixedMobCapacity;
    private int objectIDCounter = 1000000;
    private boolean userFirstEnter = false;
    private Set<Reactor> reactors;
    private String fieldScript = "";
    private ScriptManagerImpl scriptManagerImpl;
    private RuneStone runeStone;
    private ScheduledFuture runeStoneHordesTimer;
    private int burningFieldLevel;
    private ScheduledFuture burningFieldCheckTimer;
    private long nextEliteSpawnTime = System.currentTimeMillis();
    private int killedElites;
    private EliteState eliteState;

    public Field(int fieldID, long uniqueId) {
        this.id = fieldID;
        this.uniqueId = uniqueId;
        this.rect = new Rectangle(800, 600);
        this.portals = new HashSet<>();
        this.footholds = new HashSet<>();
        this.lifes = Collections.synchronizedList(new ArrayList<>());
        this.chars = Collections.synchronizedList(new ArrayList<>());
        this.lifeToControllers = new HashMap<>();
        this.lifeSchedules = new HashMap<>();
        this.reactors = new HashSet<>();
        startFieldScript();
    }

    private void startFieldScript() {
        String script = getFieldScript();
        if(!"".equalsIgnoreCase(script)) {
            scriptManagerImpl = new ScriptManagerImpl(this);
            log.debug(String.format("Starting field script %s.", script));
            scriptManagerImpl.startScript(getId(), script, ScriptType.FIELD);
        }
    }

    public Rectangle getRect() {
        return rect;
    }

    public void setRect(Rectangle rect) {
        this.rect = rect;
    }

    public int getWidth() {
        return (int) getRect().getWidth();
    }

    public int getHeight() {
        return (int) getRect().getHeight();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(long uniqueId) {
        this.uniqueId = uniqueId;
    }

    public Set<Portal> getPortals() {
        return portals;
    }

    public void setPortals(Set<Portal> portals) {
        this.portals = portals;
    }

    public void addPortal(Portal portal) {
        getPortals().add(portal);
    }

    public int getReturnMap() {
        return returnMap;
    }

    public void setReturnMap(int returnMap) {
        this.returnMap = returnMap;
    }

    public int getForcedReturn() {
        return forcedReturn;
    }

    public void setForcedReturn(int forcedReturn) {
        this.forcedReturn = forcedReturn;
    }

    public double getMobRate() {
        return mobRate;
    }

    public void setMobRate(double mobRate) {
        this.mobRate = mobRate;
    }

    public int getCreateMobInterval() {
        return createMobInterval;
    }

    public void setCreateMobInterval(int createMobInterval) {
        this.createMobInterval = createMobInterval;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public int getLvLimit() {
        return lvLimit;
    }

    public void setLvLimit(int lvLimit) {
        this.lvLimit = lvLimit;
    }

    public int getLvForceMove() {
        return lvForceMove;
    }

    public void setLvForceMove(int lvForceMove) {
        this.lvForceMove = lvForceMove;
    }

    public int getConsumeItemCoolTime() {
        return consumeItemCoolTime;
    }

    public void setConsumeItemCoolTime(int consumeItemCoolTime) {
        this.consumeItemCoolTime = consumeItemCoolTime;
    }

    public int getLink() {
        return link;
    }

    public void setLink(int link) {
        this.link = link;
    }

    public boolean isTown() {
        return town;
    }

    public void setTown(boolean town) {
        this.town = town;
    }

    public boolean isSwim() {
        return swim;
    }

    public void setSwim(boolean swim) {
        this.swim = swim;
    }

    public boolean isFly() {
        return fly;
    }

    public void setFly(boolean fly) {
        this.fly = fly;
    }

    public boolean isReactorShuffle() {
        return reactorShuffle;
    }

    public void setReactorShuffle(boolean reactorShuffle) {
        this.reactorShuffle = reactorShuffle;
    }

    public boolean isExpeditionOnly() {
        return expeditionOnly;
    }

    public void setExpeditionOnly(boolean expeditionONly) {
        this.expeditionOnly = expeditionONly;
    }

    public boolean isPartyOnly() {
        return partyOnly;
    }

    public void setPartyOnly(boolean partyOnly) {
        this.partyOnly = partyOnly;
    }

    public boolean isNeedSkillForFly() {
        return needSkillForFly;
    }

    public void setNeedSkillForFly(boolean needSkillForFly) {
        this.needSkillForFly = needSkillForFly;
    }

    public String getOnFirstUserEnter() {
        return onFirstUserEnter;
    }

    public void setOnFirstUserEnter(String onFirstUserEnter) {
        this.onFirstUserEnter = onFirstUserEnter;
    }

    public String getOnUserEnter() {
        return onUserEnter;
    }

    public void setOnUserEnter(String onUserEnter) {
        this.onUserEnter = onUserEnter;
    }

    public Portal getPortalByName(String name) {
        return getPortals().stream().filter(portal -> portal.getName().equals(name)).findAny().orElse(null);
    }

    public Portal getPortalByID(int id) {
        return getPortals().stream().filter(portal -> portal.getId() == id).findAny().orElse(null);
    }

    public RuneStone getRuneStone() {
        return runeStone;
    }

    public void setRuneStone(RuneStone runeStone) {
        this.runeStone = runeStone;
    }

    public int getBurningFieldLevel() {
        return burningFieldLevel;
    }

    public void setBurningFieldLevel(int burningFieldLevel) {
        this.burningFieldLevel = burningFieldLevel;
    }

    public Foothold findFootHoldBelow(Position pos) {
        Set<Foothold> footholds = getFootholds().stream().filter(fh -> fh.getX1() <= pos.getX() && fh.getX2() >= pos.getX()).collect(Collectors.toSet());
        Foothold res = null;
        int lastY = Integer.MAX_VALUE;
        for (Foothold fh : footholds) {
            int y = fh.getYFromX(pos.getX());
            if (res == null && y >= pos.getY()) {
                res = fh;
                lastY = y;
            } else {
                if (y < lastY && y >= pos.getY()) {
                    res = fh;
                    lastY = y;
                }
            }
        }
        return res;
    }

    public Set<Foothold> getFootholds() {
        return footholds;
    }

    public void setFootholds(Set<Foothold> footholds) {
        this.footholds = footholds;
    }

    public void addFoothold(Foothold foothold) {
        getFootholds().add(foothold);
    }

    public void setFixedMobCapacity(int fixedMobCapacity) {
        this.fixedMobCapacity = fixedMobCapacity;
    }

    public int getFixedMobCapacity() {
        return fixedMobCapacity;
    }

    public List<Life> getLifes() {
        return lifes;
    }

    public void addLife(Life life) {
        if (life.getObjectId() < 0) {
            life.setObjectId(getNewObjectID());
        }
        if (!getLifes().contains(life)) {
            getLifes().add(life);
            life.setField(this);
        }
    }

    public void removeLife(int id) {
        Life life = getLifeByObjectID(id);
        if (life == null) {
            return;
        }
        getLifes().remove(life);
    }

    public void spawnSummon(Summon summon) {
        Summon oldSummon = (Summon) getLifes().stream()
                .filter(s -> s instanceof Summon &&
                        ((Summon) s).getCharID() == summon.getCharID() &&
                        ((Summon) s).getSkillID() == summon.getSkillID())
                .findFirst().orElse(null);
        if (oldSummon != null) {
            removeLife(oldSummon.getObjectId(), false);
        }
        spawnLife(summon, null);
    }

    public void spawnAddSummon(Summon summon) { //Test
        spawnLife(summon, null);
    }

    public void spawnLife(Life life, Char onlyChar) {
        addLife(life);
        if (getChars().size() > 0) {
            Char controller = null;
            if (getLifeToControllers().containsKey(life)) {
                controller = getLifeToControllers().get(life);
            }
            if (controller == null) {
                controller = getChars().get(0);
                putLifeController(life, controller);
            }
            life.broadcastSpawnPacket(onlyChar);
        }
    }

    private void removeLife(Life life) {
        getLifes().remove(life);
    }

    public Foothold getFootholdById(int fh) {
        return getFootholds().stream().filter(f -> f.getId() == fh).findFirst().orElse(null);
    }

    public List<Char> getChars() {
        return chars;
    }

    public Char getCharByID(int id) {
        return getChars().stream().filter(c -> c.getId() == id).findFirst().orElse(null);
    }

    public void addChar(Char chr) {
        if (!getChars().contains(chr)) {
            getChars().add(chr);
            if(!isUserFirstEnter() && hasUserFirstEnterScript()) {
                chr.chatMessage("First enter script!");
                chr.getScriptManager().startScript(getId(), getOnFirstUserEnter(), ScriptType.FIELD);
                setUserFirstEnter(true);
            }
        }
        broadcastPacket(UserPool.userEnterField(chr), chr);
    }

    private boolean hasUserFirstEnterScript() {
        return getOnFirstUserEnter() != null && !getOnFirstUserEnter().equalsIgnoreCase("");
    }

    public void broadcastPacket(OutPacket outPacket, Char exceptChr) {
        getChars().stream().filter(chr -> !chr.equals(exceptChr)).forEach(
                chr -> chr.write(outPacket)
        );
    }

    public void removeChar(Char chr) {
        getChars().remove(chr);
        broadcastPacket(UserPool.userLeaveField(chr), chr);
        // set controllers to null
        for (Map.Entry<Life, Char> entry : getLifeToControllers().entrySet()) {
            if (entry.getValue() != null && entry.getValue().equals(chr)) {
                putLifeController(entry.getKey(), null);
            }
        }
        // remove summons of that char
        List<Integer> removedList = new ArrayList<>();
        for (Life life : getLifes()) {
            if (life instanceof Summon && ((Summon) life).getCharID() == chr.getId()) {
                removedList.add(life.getObjectId());
            }
        }
        for (int id : removedList) {
            removeLife(id, false);
        }
    }

    public Map<Life, Char> getLifeToControllers() {
        return lifeToControllers;
    }

    public void setLifeToControllers(Map<Life, Char> lifeToControllers) {
        this.lifeToControllers = lifeToControllers;
    }

    public void putLifeController(Life life, Char chr) {
        getLifeToControllers().put(life, chr);
    }

    public void changeLifeController(Life life) {

    }

    public Life getLifeByObjectID(int mobId) {
        return getLifes().stream().filter(mob -> mob.getObjectId() == mobId).findFirst().orElse(null);
    }

    public void spawnLifesForChar(Char chr) {
        for (Life life : getLifes()) {
            spawnLife(life, chr);
        }
        for (Reactor reactor : getReactors()) {
            spawnLife(reactor, chr);
        }
        if (getRuneStone() != null && getMobs().size() > 0) {
            broadcastPacket(CField.runeStoneAppear(runeStone));
        }
        //if (getMobs().size() > 0 && getBurningFieldLevel() > 0) { //Burning Level shown per map entry is commented out.
        //    showBurningLevel();
        //}
    }

    @Override
    public String toString() {
        return "" + getId();
    }

    public void respawn(Mob mob) {
        mob.setHp(mob.getMaxHp());
        mob.setMp(mob.getMaxMp());
        mob.setPosition(mob.getHomePosition().deepCopy());
        spawnLife(mob, null);
    }

    public void broadcastPacket(OutPacket outPacket) {
        for (Char c : getChars()) {
            c.getClient().write(outPacket);
        }
    }

    public void spawnAffectedArea(AffectedArea aa) {
        addLife(aa);
        SkillInfo si = SkillData.getSkillInfoById(aa.getSkillID());
        if (si != null) {
            int duration = si.getValue(time, aa.getSlv()) * 1000;
            ScheduledFuture sf = EventManager.addEvent(() -> removeLife(aa.getObjectId(), true), duration);
            addLifeSchedule(aa, sf);
        }
        broadcastPacket(CField.affectedAreaCreated(aa));
        getChars().forEach(chr -> aa.getField().checkCharInAffectedAreas(chr));
        getMobs().forEach(mob -> aa.getField().checkMobInAffectedAreas(mob));
    }

    public List<Mob> getMobs() {
        return getLifes().stream().filter(life -> life instanceof Mob).map(l -> (Mob) l).collect(Collectors.toList());
    }

    public void setObjectIDCounter(int idCounter) {
        objectIDCounter = idCounter;
    }

    public int getNewObjectID() {
        return objectIDCounter++;
    }

    public List<Life> getLifesInRect(Rect rect) {
        List<Life> lifes = new ArrayList<>();
        for (Life life : getLifes()) {
            Position position = life.getPosition();
            int x = position.getX();
            int y = position.getY();
            if (x >= rect.getLeft() && y >= rect.getTop()
                    && x <= rect.getRight() && y <= rect.getBottom()) {
                lifes.add(life);
            }
        }
        return lifes;
    }

    public List<Char> getCharsInRect(Rect rect) {
        List<Char> chars = new ArrayList<>();
        for (Char chr : getChars()) {
            Position position = chr.getPosition();
            int x = position.getX();
            int y = position.getY();
            if (x >= rect.getLeft() && y >= rect.getTop()
                    && x <= rect.getRight() && y <= rect.getBottom()) {
                chars.add(chr);
            }
        }
        return chars;
    }

    public List<Mob> getMobsInRect(Rect rect) {
        List<Mob> mobs = new ArrayList<>();
        for (Mob mob : getMobs()) {
            Position position = mob.getPosition();
            int x = position.getX();
            int y = position.getY();
            if (x >= rect.getLeft() && y >= rect.getTop()
                    && x <= rect.getRight() && y <= rect.getBottom()) {
                mobs.add(mob);
            }
        }
        return mobs;
    }

    public List<Mob> getBossMobsInRect(Rect rect) {
        List<Mob> mobs = new ArrayList<>();
        for (Mob mob : getMobs()) {
            if(mob.isBoss()) {
                Position position = mob.getPosition();
                int x = position.getX();
                int y = position.getY();
                if (x >= rect.getLeft() && y >= rect.getTop()
                        && x <= rect.getRight() && y <= rect.getBottom()) {
                    mobs.add(mob);
                }
            }
        }
        return mobs;
    }

    public synchronized void removeLife(Integer id, Boolean fromSchedule) {
        Life life = getLifeByObjectID(id);
        if (life == null) {
            return;
        }
        removeLife(id);
        removeSchedule(life, fromSchedule);
        if (life instanceof Summon) {
            Summon summon = (Summon) life;
            broadcastPacket(Summoned.summonedRemoved(summon, LeaveType.ANIMATION));
        } else if (life instanceof AffectedArea) {
            AffectedArea aa = (AffectedArea) life;
            broadcastPacket(CField.affectedAreaRemoved(aa, false));
            for (Char chr : getChars()) {
                TemporaryStatManager tsm = chr.getTemporaryStatManager();
                if (tsm.hasAffectedArea(aa)) {
                    tsm.removeStatsBySkill(aa.getSkillID());
                }
            }
        }
    }

    public synchronized void removeDrop(Integer dropID, Integer pickupUserID, Boolean fromSchedule) {
        Life life = getLifeByObjectID(dropID);
        if (life instanceof Drop) {
            if(pickupUserID != 0) {
                broadcastPacket(DropPool.dropLeaveField(dropID, pickupUserID));
            } else {
                broadcastPacket(DropPool.dropLeaveField(DropLeaveType.FADE, 0, life.getObjectId(),
                        (short) 0, 0, 0));
            }
            removeLife(dropID, fromSchedule);
        }
    }

    public Map<Life, ScheduledFuture> getLifeSchedules() {
        return lifeSchedules;
    }

    public void addLifeSchedule(Life life, ScheduledFuture scheduledFuture) {
        getLifeSchedules().put(life, scheduledFuture);
    }

    public void removeSchedule(Life life, boolean fromSchedule) {
        if (!getLifeSchedules().containsKey(life)) {
            return;
        }
        if (!fromSchedule) {
            getLifeSchedules().get(life).cancel(false);
        }
        getLifeSchedules().remove(life);
    }

    public List<AffectedArea> getAffectedAreas() {
        return getLifes().stream().filter(life -> life instanceof AffectedArea).map(l -> (AffectedArea) l).collect(Collectors.toList());
    }

    public void checkMobInAffectedAreas(Mob mob) {
        for (AffectedArea aa : getAffectedAreas()) {
            if (aa.getRect().hasPositionInside(mob.getPosition())) {
                aa.handleMobInside(mob);
            }
        }
    }

    public void checkCharInAffectedAreas(Char chr) {
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        for (AffectedArea aa : getAffectedAreas()) {
            boolean isInsideAA = aa.getRect().hasPositionInside(chr.getPosition());
            if (isInsideAA) {
                aa.handleCharInside(chr);
            } else if (tsm.hasAffectedArea(aa) && !isInsideAA) {
                tsm.removeAffectedArea(aa);
            }
        }
    }

    private void broadcastWithPredicate(OutPacket outPacket, Predicate<? super Char> predicate) {
        getChars().stream().filter(predicate).forEach(chr -> chr.write(outPacket));
    }

    /**
     * Drops an item to this map, given a {@link Drop}, a starting Position and an ending Position.
     * Immediately broadcasts the drop packet.
     *
     * @param drop    The Drop to drop.
     * @param posFrom The Position that the drop starts off from.
     * @param posTo   The Position where the drop lands.
     */
    public void drop(Drop drop, Position posFrom, Position posTo) {
        addLife(drop);
        getLifeSchedules().put(drop,
                EventManager.addEvent(() -> removeDrop(drop.getObjectId(), 0, true),
                        GameConstants.DROP_REMAIN_ON_GROUND_TIME, TimeUnit.SECONDS));

        if(ItemConstants.isCollisionLootItem(drop.getItem().getItemId())) { // Check for Collision Items such as Exp Orbs from Combo Kills
            broadcastPacket(DropPool.dropEnterFieldCollisionPickUp(drop, posFrom, 0));
        } else {
            broadcastPacket(DropPool.dropEnterField(drop, posFrom, posTo, 0));
        }

    }

    /**
     * Drops a {@link Drop} according to a given {@link DropInfo DropInfo}'s specification.
     *
     * @param dropInfo The
     * @param posFrom  The Position that hte drop starts off from.
     * @param posTo    The Position where the drop lands.
     * @param ownerID  The owner's character ID. Will not be able to be picked up by Chars that are not the owner.
     */
    public void drop(DropInfo dropInfo, Position posFrom, Position posTo, int ownerID) {
        int itemID = dropInfo.getItemID();
        Item item;
        Drop drop = new Drop(-1);
        drop.setOwnerID(ownerID);
        if (itemID != 0) {
            item = ItemData.getItemDeepCopy(itemID, true);
            if (item != null) {
                item.setQuantity(dropInfo.getQuantity());
                drop.setItem(item);
            } else {
                log.error("Was not able to find the item to drop! id = " + itemID);
                return;
            }
        } else {
            drop.setMoney(dropInfo.getMoney());
        }
        addLife(drop);
        getLifeSchedules().put(drop,
                EventManager.addEvent(() -> removeDrop(drop.getObjectId(), 0, true),
                        GameConstants.DROP_REMAIN_ON_GROUND_TIME, TimeUnit.SECONDS));
        broadcastWithPredicate(DropPool.dropEnterField(drop, posFrom, posTo, ownerID),
                (Char chr) -> dropInfo.getQuestReq() == 0 || chr.hasQuestInProgress(dropInfo.getQuestReq()));
    }

    /**
     * Drops a Set of {@link DropInfo}s from a base Position.
     *
     * @param dropInfos The Set of DropInfos.
     * @param position  The Position the initial Drop comes from.
     * @param ownerID   The owner's character ID.
     */
    public void drop(Set<DropInfo> dropInfos, Position position, int ownerID) {
        drop(dropInfos, findFootHoldBelow(position), position, ownerID);
    }

    /**
     * Drops a {@link Drop} at a given Position. Calculates the Position that the Drop should land at.
     *
     * @param drop     The Drop that should be dropped.
     * @param position The Position it is dropped from.
     */
    public void drop(Drop drop, Position position) {
        int x = position.getX();
        Position posTo = new Position(x, findFootHoldBelow(position).getYFromX(x));
        drop(drop, position, posTo);
    }

    /**
     * Drops a Set of {@link DropInfo}s, locked to a specific {@link Foothold}.
     * Not all drops are guaranteed to be dropped, as this method calculates whether or not a Drop should drop, according
     * to the DropInfo's prop chance.
     *
     * @param dropInfos The Set of DropInfos that should be dropped.
     * @param fh        The Foothold this Set of DropInfos is bound to.
     * @param position  The Position the Drops originate from.
     * @param ownerID   The ID of the owner of all drops.
     */
    public void drop(Set<DropInfo> dropInfos, Foothold fh, Position position, int ownerID) {
        int x = position.getX();
        int minX = fh.getX1();
        int maxX = fh.getX2();
        int diff = 0;
        for (DropInfo dropInfo : dropInfos) {
            if (dropInfo.willDrop()) {
                x = (x + diff) > maxX ? maxX - 10 : (x + diff) < minX ? minX + 10 : x + diff;
                Position posTo = new Position(x, fh.getYFromX(x));
                drop(dropInfo, position, posTo, ownerID);
                diff = diff < 0 ? Math.abs(diff - GameConstants.DROP_DIFF) : -(diff + GameConstants.DROP_DIFF);
                dropInfo.generateNextDrop();
            }
        }
    }

    public List<Portal> getclosestPortal(Rect rect) {
        List<Portal> portals = new ArrayList<>();
        for (Portal portals2 : getPortals()) {
            int x = portals2.getX();
            int y = portals2.getY();
            if (x >= rect.getLeft() && y >= rect.getTop()
                    && x <= rect.getRight() && y <= rect.getBottom()) {
                portals.add(portals2);
            }
        }
        return portals;
    }

    public Char getCharByName(String name) {
        return getChars().stream().filter(chr -> chr.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public void execUserEnterScript(Char chr) {
        if(getOnUserEnter() == null || getOnUserEnter().equalsIgnoreCase("")) {
            return;
        }
        String script = getOnUserEnter();
        chr.getScriptManager().startScript(getId(), script, ScriptType.FIELD);
    }

    public boolean isUserFirstEnter() {
        return userFirstEnter;
    }

    public void setUserFirstEnter(boolean userFirstEnter) {
        this.userFirstEnter = userFirstEnter;
    }

    public int getAliveMobCount() {
        return getLifes().stream()
                .filter(life -> life instanceof Mob && ((Mob) life).isAlive())
                .collect(Collectors.toList())
                .size();
    }

    public int getAliveMobCount(int mobID) {
        return getLifes().stream()
                .filter(life -> life instanceof Mob && life.getTemplateId() == mobID && ((Mob) life).isAlive())
                .collect(Collectors.toList())
                .size();
    }

    public Set<Reactor> getReactors() {
        return reactors;
    }

    public void addReactor(Reactor reactor) {
        if (reactor.getObjectId() < 0) {
            reactor.setObjectId(getNewObjectID());
        }
        getReactors().add(reactor);
        reactor.setField(this);
    }

    public void removeReactor(Reactor reactor) {
        if(reactor != null) {
            getReactors().remove(reactor);
        }
    }

    public void removeReactorByObjID(int reactorObjID) {
        removeReactor(getReactorByObjID(reactorObjID));
    }

    public Reactor getReactorByObjID(int reactorObjID) {
        return getReactors().stream().filter(r -> r.getObjectId() == reactorObjID).findAny().orElse(null);
    }

    public String getFieldScript() {
        return fieldScript;
    }

    public void setFieldScript(String fieldScript) {
        this.fieldScript = fieldScript;
    }

    public Mob spawnMob(int id, int x, int y, boolean respawnable) {
        Mob mob = MobData.getMobDeepCopyById(id);
        Position pos = new Position(x, y);
        mob.setPosition(pos.deepCopy());
        mob.setPrevPos(pos.deepCopy());
        mob.setPosition(pos.deepCopy());
        mob.setNotRespawnable(!respawnable);
        if (mob.getField() == null) {
            mob.setField(this);
        }
        spawnLife(mob, null);
        return mob;
    }

    public void spawnRuneStone() {
        if(getMobs().size() <= 0) {
            return;
        }
        if(getRuneStone() == null) {
            RuneStone runeStone = new RuneStone().getRandomRuneStone(this);
            setRuneStone(runeStone);
            broadcastPacket(CField.runeStoneAppear(runeStone));
        }
    }

    public void useRuneStone(Client c, RuneStone runeStone) {
        broadcastPacket(CField.runeStoneSkillAck(runeStone.getRuneType()));

        broadcastPacket(CField.completeRune(c.getChr()));
        c.write(CField.runeStoneDisappear());

        setRuneStone(null);

        EventManager.addEvent(() -> spawnRuneStone(), GameConstants.RUNE_RESPAWN_TIME, TimeUnit.MINUTES);
    }

    public void runeStoneHordeEffect(int mobRateMultiplier, int duration) {
        double prevMobRate = getMobRate();
        setMobRate(getMobRate() * mobRateMultiplier); //Temporary increase in mob Spawn
        if(runeStoneHordesTimer != null && !runeStoneHordesTimer.isDone()) {
            runeStoneHordesTimer.cancel(true);
        }
        runeStoneHordesTimer = EventManager.addEvent(() -> setMobRate(prevMobRate), duration, TimeUnit.SECONDS);
    }

    public int getBonusExpByBurningFieldLevel() {
        return burningFieldLevel * 10; //Level 1 BurningField = 10% EXP
    }

    public void showBurningLevel() {
        String string = "Burning Field has been destroyed.";
        if(getBurningFieldLevel() > 0) {
            string = "Burning Stage " + getBurningFieldLevel() + ": " + getBonusExpByBurningFieldLevel() + "% Bonus EXP";
        }
        Effect effect = Effect.createBurningFieldTextEffect(string);
        broadcastPacket(CField.onEffect(effect));
    }

    public void increaseBurningLevel() {
        setBurningFieldLevel(getBurningFieldLevel() + 1);
    }

    public void decreaseBurningLevel() {
        setBurningFieldLevel(getBurningFieldLevel() - 1);
    }

    public void startBurningFieldTimer() {
        if(getMobs().size() > 0 &&
                getMobs().stream().mapToInt(m -> m.getForcedMobStat().getLevel()).min().orElse(0) >= GameConstants.BURNING_FIELD_MIN_MOB_LEVEL) {
            setBurningFieldLevel(GameConstants.BURNING_FIELD_LEVEL_ON_START);
            burningFieldCheckTimer = EventManager.addFixedRateEvent(() -> changeBurningLevel(), 0, GameConstants.BURNING_FIELD_TIMER, TimeUnit.MINUTES); //Every X minutes runs 'changeBurningLevel()'
        }
    }

    public void changeBurningLevel() {
        boolean showMessage = true;

        if(getBurningFieldLevel() <= 0) {
            showMessage = false;
        }

        //If there are players on the map,  decrease the level  else  increase the level
        if(getChars().size() > 0 && getBurningFieldLevel() > 0) {
            decreaseBurningLevel();

        } else if(getChars().size() <= 0 && getBurningFieldLevel() < 10){
            increaseBurningLevel();
            showMessage = true;
        }

        if(showMessage) {
            showBurningLevel();
        }
    }

    public void setNextEliteSpawnTime(long nextEliteSpawnTime) {
        this.nextEliteSpawnTime = nextEliteSpawnTime;
    }

    public long getNextEliteSpawnTime() {
        return nextEliteSpawnTime;
    }

    public boolean canSpawnElite() {
        return getEliteState() == EliteState.NORMAL && nextEliteSpawnTime < System.currentTimeMillis();
    }

    public int getKilledElites() {
        return killedElites;
    }

    public void setKilledElites(int killedElites) {
        this.killedElites = killedElites;
    }

    public void incrementEliteKillCount() {
        setKilledElites(getKilledElites() + 1);
    }

    public void setEliteState(EliteState eliteState) {
        this.eliteState = eliteState;
    }

    public EliteState getEliteState() {
        return eliteState;
    }
}

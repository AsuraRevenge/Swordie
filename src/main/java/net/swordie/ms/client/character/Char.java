package net.swordie.ms.client.character;

import net.swordie.ms.client.Account;
import net.swordie.ms.client.Client;
import net.swordie.ms.client.LinkSkill;
import net.swordie.ms.client.character.avatar.AvatarData;
import net.swordie.ms.client.character.avatar.AvatarLook;
import net.swordie.ms.client.character.cards.MonsterBookInfo;
import net.swordie.ms.client.character.damage.DamageCalc;
import net.swordie.ms.client.character.damage.DamageSkinSaveData;
import net.swordie.ms.client.character.info.ExpIncreaseInfo;
import net.swordie.ms.client.character.info.FarmUserInfo;
import net.swordie.ms.client.character.info.FreezeHotEventInfo;
import net.swordie.ms.client.character.info.ZeroInfo;
import net.swordie.ms.client.character.items.BodyPart;
import net.swordie.ms.client.character.items.*;
import net.swordie.ms.client.character.keys.FuncKeyMap;
import net.swordie.ms.client.character.monsterbattle.MonsterBattleLadder;
import net.swordie.ms.client.character.monsterbattle.MonsterBattleMobInfo;
import net.swordie.ms.client.character.monsterbattle.MonsterBattleRankInfo;
import net.swordie.ms.client.character.potential.CharacterPotential;
import net.swordie.ms.client.character.potential.CharacterPotentialMan;
import net.swordie.ms.client.character.quest.Quest;
import net.swordie.ms.client.character.quest.QuestManager;
import net.swordie.ms.client.character.skills.*;
import net.swordie.ms.client.character.skills.info.SkillInfo;
import net.swordie.ms.client.character.skills.temp.TemporaryStatManager;
import net.swordie.ms.client.friend.Friend;
import net.swordie.ms.client.friend.FriendFlag;
import net.swordie.ms.client.friend.FriendRecord;
import net.swordie.ms.client.friend.FriendshipRingRecord;
import net.swordie.ms.client.guild.Guild;
import net.swordie.ms.client.guild.GuildMember;
import net.swordie.ms.client.guild.updates.GuildUpdate;
import net.swordie.ms.client.guild.updates.GuildUpdateMemberLogin;
import net.swordie.ms.client.jobs.Job;
import net.swordie.ms.client.jobs.JobManager;
import net.swordie.ms.client.jobs.resistance.WildHunterInfo;
import net.swordie.ms.client.party.Party;
import net.swordie.ms.connection.OutPacket;
import net.swordie.ms.connection.db.DatabaseManager;
import net.swordie.ms.connection.packet.*;
import net.swordie.ms.constants.GameConstants;
import net.swordie.ms.constants.ItemConstants;
import net.swordie.ms.constants.JobConstants;
import net.swordie.ms.constants.SkillConstants;
import net.swordie.ms.enums.*;
import net.swordie.ms.handlers.ChatHandler;
import net.swordie.ms.handlers.EventManager;
import net.swordie.ms.life.AffectedArea;
import net.swordie.ms.life.Familiar;
import net.swordie.ms.life.drop.Drop;
import net.swordie.ms.life.pet.Pet;
import net.swordie.ms.loaders.*;
import net.swordie.ms.scripts.ScriptManagerImpl;
import net.swordie.ms.util.FileTime;
import net.swordie.ms.util.Position;
import net.swordie.ms.util.Rect;
import net.swordie.ms.world.field.Field;
import net.swordie.ms.world.field.FieldInstanceType;
import net.swordie.ms.world.field.Portal;
import net.swordie.ms.world.shop.NpcShopDlg;
import org.apache.log4j.Logger;

import javax.persistence.*;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static net.swordie.ms.client.character.items.BodyPart.*;
import static net.swordie.ms.client.character.skills.temp.CharacterTemporaryStat.FullSoulMP;
import static net.swordie.ms.client.character.skills.temp.CharacterTemporaryStat.SoulMP;
import static net.swordie.ms.enums.ChatMsgColour.GAME_MESSAGE;
import static net.swordie.ms.enums.InvType.EQUIP;
import static net.swordie.ms.enums.InvType.EQUIPPED;
import static net.swordie.ms.enums.InventoryOperation.*;
import static net.swordie.ms.world.field.FieldInstanceType.CHANNEL;

/**
 * Created on 11/17/2017.
 */
@Entity
@Table(name = "characters")
public class Char {

	@Transient
	private static final Logger log = Logger.getLogger(Char.class);

	@Transient
	private Client client;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "accId")
	private int accId;

	@JoinColumn(name = "questManager")
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	private QuestManager questManager;

	@JoinColumn(name = "equippedInventory")
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	private Inventory equippedInventory = new Inventory(InvType.EQUIPPED, 52);

	@JoinColumn(name = "equipInventory")
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	private Inventory equipInventory = new Inventory(InvType.EQUIP, 52);

	@JoinColumn(name = "consumeInventory")
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	private Inventory consumeInventory = new Inventory(InvType.CONSUME, 52);

	@JoinColumn(name = "etcInventory")
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	private Inventory etcInventory = new Inventory(InvType.ETC, 52);

	@JoinColumn(name = "installInventory")
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	private Inventory installInventory = new Inventory(InvType.INSTALL, 52);

	@JoinColumn(name = "cashInventory")
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	private Inventory cashInventory = new Inventory(InvType.CASH, 52);

	@JoinColumn(name = "avatarData")
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	private AvatarData avatarData;
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	private FuncKeyMap funcKeyMap;

	@JoinColumn(name = "charId")
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Skill> skills;

	@JoinColumn(name = "ownerID")
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private Set<Friend> friends;

	@JoinColumn(name = "charID")
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private Set<CharacterPotential> potentials;

	@JoinColumn(name = "charID")
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private Set<Familiar> familiars;

	@JoinColumn(name = "charID")
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Macro> macros = new ArrayList<>();

	@JoinColumn(name = "guild")
	@OneToOne(cascade = CascadeType.ALL)
	private Guild guild;

	@JoinColumn(name = "monsterBook")
	@OneToOne(cascade = CascadeType.ALL)
	private MonsterBookInfo monsterBookInfo;

	@JoinColumn(name = "charID")
	@OneToMany(cascade = CascadeType.ALL)
	private Set<StolenSkill> stolenSkills;

	@JoinColumn(name = "charID")
	@OneToMany(cascade = CascadeType.ALL)
	private Set<ChosenSkill> chosenSkills;

	@Transient
	private CharacterPotentialMan potentialMan;

	@Transient
	private Ranking ranking;
	@Transient
	private int combatOrders;
	@Transient
	private List<ItemPot> itemPots;
	@Transient
	private List<Pet> pets;
	@Transient
	private List<FriendRecord> friendRecords;
	@Transient
	private List<ExpConsumeItem> expConsumeItems;
	@Transient
	private List<MonsterBattleMobInfo> monsterBattleMobInfos;
	@Transient
	private MonsterBattleLadder monsterBattleLadder;
	@Transient
	private MonsterBattleRankInfo monsterBattleRankInfo;
	@Transient
	private Position position;
	@Transient
	private Position oldPosition;
	@Transient
	private Field field;
	@Transient
	private byte moveAction;
	@Transient
	private TemporaryStatManager temporaryStatManager;
	@Transient
	private Job jobHandler;
	@Transient
	private boolean left;
	@Transient
	private MarriageRecord marriageRecord;
	@Transient
	private WildHunterInfo wildHunterInfo;
	@Transient
	private ZeroInfo zeroInfo;
	@Transient
	private int nickItem;
	@Transient
	private DamageSkinSaveData damageSkin = new DamageSkinSaveData(18, 2433063, false, "Je moeder");
	@Transient
	private DamageSkinSaveData premiumDamageSkin = new DamageSkinSaveData();
	@Transient
	private boolean partyInvitable;
	@Transient
	private ScriptManagerImpl scriptManagerImpl = new ScriptManagerImpl(this);
	@Transient
	private int driverID;
	@Transient
	private int passengerID;
	@Transient
	private int chocoCount;
	@Transient
	private int activeEffectItemID;
	@Transient
	private int monkeyEffectItemID;
	@Transient
	private int completedSetItemID;
	@Transient
	private short fieldSeatID;
	@Transient
	private int portableChairID;
	@Transient
	private String portableChairMsg;
	@Transient
	private short foothold;
	@Transient
	private int tamingMobLevel;
	@Transient
	private int tamingMobExp;
	@Transient
	private int tamingMobFatigue;
	@Transient
	private MiniRoom miniRoom;
	@Transient
	private String ADBoardRemoteMsg;
	@Transient
	private boolean inCouple;
	@Transient
	private CoupleRecord couple;
	@Transient
	private FriendshipRingRecord friendshipRingRecord;
	@Transient
	private int evanDragonGlide;
	@Transient
	private int kaiserMorphRotateHueExtern;
	@Transient
	private int kaiserMorphPrimiumBlack;
	@Transient
	private int kaiserMorphRotateHueInnner;
	@Transient
	private int makingMeisterSkillEff;
	@Transient
	private FarmUserInfo farmUserInfo;
	@Transient
	private int customizeEffect;
	@Transient
	private String customizeEffectMsg;
	@Transient
	private byte soulEffect;
	@Transient
	private FreezeHotEventInfo freezeHotEventInfo;
	@Transient
	private int eventBestFriendAID;
	@Transient
	private int mesoChairCount;
	@Transient
	private boolean beastFormWingOn;
	@Transient
	private int activeNickItemID;
	@Transient
	private int mechanicHue;
	@Transient
	private boolean online;
	@Transient
	private Party party;
	@Transient
	private FieldInstanceType fieldInstanceType;
	@Transient
	private Map<Integer, Field> fields = new HashMap<>();
	@Transient
	private int bulletIDForAttack;
	@Transient
	private NpcShopDlg shop;
	@Transient // yes
	private Account account;
	@Transient
	private Client chatClient;
	@Transient
	private DamageCalc damageCalc;
	@Transient
	private boolean buffProtector;
	@Transient
	private int comboCounter;
	@Transient
	private ScheduledFuture comboKillResetTimer;
	@Transient
	private Map<Integer, Long> skillCoolTimes = new HashMap<>();
	@Transient
	private int deathCount = -1;
	@Transient
	private long runeStoneCooldown;
	@Transient
	private MemorialCubeInfo memorialCubeInfo;
	@Transient
	private Familiar activeFamiliar;
	@Transient
	private boolean skillCDBypass = false;
	// TODO Move this to CharacterStat?
	@Transient
	private Map<BaseStat, Long> baseStats = new HashMap<>();

	public Char() {
		this(0, "", 0, 0, 0, (short) 0, (byte) -1, (byte) -1, new int[]{});
	}

	public Char(int accId, String name, int keySettingType, int eventNewCharSaleJob, int job, short curSelectedSubJob,
	            byte gender, byte skin, int[] items) {
		this.accId = accId;
		avatarData = new AvatarData();
		avatarData.setAvatarLook(new AvatarLook());
		AvatarLook avatarLook = avatarData.getAvatarLook();
		avatarLook.setGender(gender);
		avatarLook.setSkin(skin);
		avatarLook.setFace(items.length > 0 ? items[0] : 0);
		avatarLook.setHair(items.length > 1 ? items[1] : 0);
		List<Integer> hairEquips = new ArrayList<>();
		for (int itemId : items) {
			Equip equip = ItemData.getEquipDeepCopyFromID(itemId, false);
			if (equip != null) {
				hairEquips.add(itemId);
				if ("Wp".equals(equip.getiSlot())) {
					if (!equip.isCash()) {
						avatarLook.setWeaponId(itemId);
					} else {
						avatarLook.setWeaponStickerId(itemId);
					}
				}
//                equip.setBagIndex(-ItemConstants.getBodyPartFromItem(equip.getItemId(), getAvatarData().getAvatarLook().getGender()));
//                addItemToInventory(InvType.EQUIPPED, equip);
			}
		}
		avatarLook.setHairEquips(hairEquips);
		avatarLook.setJob(job);
		CharacterStat characterStat = new CharacterStat(name, job);
		getAvatarData().setCharacterStat(characterStat);
		characterStat.setGender(gender);
		characterStat.setSkin(skin);
		characterStat.setFace(items.length > 0 ? items[0] : 0);
		characterStat.setHair(items.length > 1 ? items[1] : 0);
		setFieldInstanceType(CHANNEL);
		ranking = new Ranking();
		pets = new ArrayList<>();
		questManager = new QuestManager(this);
		itemPots = new ArrayList<>();
		friendRecords = new ArrayList<>();
		expConsumeItems = new ArrayList<>();
		skills = new HashSet<>();
		temporaryStatManager = new TemporaryStatManager(this);
		friends = new HashSet<>();
		monsterBookInfo = new MonsterBookInfo();
		potentialMan = new CharacterPotentialMan(this);
		familiars = new HashSet<>();
//        monsterBattleMobInfos = new ArrayList<>();
//        monsterBattleLadder = new MonsterBattleLadder();
//        monsterBattleRankInfo = new MonsterBattleRankInfo();

	}

	public static Char getFromDBById(int userId) {
		return (Char) DatabaseManager.getObjFromDB(Char.class, userId);
	}

	public AvatarData getAvatarData() {
		return avatarData;
	}

	public Ranking getRanking() {
		return ranking;
	}

	public int getId() {
		return id;
	}

	public void setAvatarData(AvatarData avatarData) {
		this.avatarData = avatarData;
	}

	public void setRanking(Ranking ranking) {
		this.ranking = ranking;
	}

	public int getAccId() {
		return accId;
	}

	public void setAccId(int accId) {
		this.accId = accId;
	}

	public Inventory getEquippedInventory() {
		return equippedInventory;
	}

	public void addItemToInventory(InvType type, Item item, boolean hasCorrectBagIndex) {
		getQuestManager().handleItemGain(item);
		Inventory inventory = getInventoryByType(type);
		if (inventory != null) {
			Item existingItem = inventory.getItemByItemID(item.getItemId());
			if (existingItem != null && existingItem.getInvType().isStackable()) {
				existingItem.addQuantity(item.getQuantity());
				write(WvsContext.inventoryOperation(true, false,
						UPDATE_QUANTITY, (short) existingItem.getBagIndex(), (byte) -1, 0, existingItem));
			} else {
				item.setInventoryId(inventory.getId());
				if (!hasCorrectBagIndex) {
					item.setBagIndex(inventory.getFirstOpenSlot());
				}
				inventory.addItem(item);
				if (item.getId() == 0) {
					DatabaseManager.saveToDB(item);
				}
				write(WvsContext.inventoryOperation(true, false,
						ADD, (short) item.getBagIndex(), (byte) -1, 0, item));
			}
			setBulletIDForAttack(calculateBulletIDForAttack());
		}
	}

	public void addItemToInventory(Item item) {
		addItemToInventory(item.getInvType(), item, false);
	}

	public void setEquippedInventory(Inventory equippedInventory) {
		this.equippedInventory = equippedInventory;
	}

	public Inventory getEquipInventory() {
		return equipInventory;
	}

	public void setEquipInventory(Inventory equipInventory) {
		this.equipInventory = equipInventory;
	}

	public Inventory getConsumeInventory() {
		return consumeInventory;
	}

	public void setConsumeInventory(Inventory consumeInventory) {
		this.consumeInventory = consumeInventory;
	}

	public Inventory getEtcInventory() {
		return etcInventory;
	}

	public void setEtcInventory(Inventory etcInventory) {
		this.etcInventory = etcInventory;
	}

	public Inventory getInstallInventory() {
		return installInventory;
	}

	public void setInstallInventory(Inventory installInventory) {
		this.installInventory = installInventory;
	}

	public Inventory getCashInventory() {
		return cashInventory;
	}

	public void setCashInventory(Inventory cashInventory) {
		this.cashInventory = cashInventory;
	}

	/**
	 * Encodes this Char's info inside a given {@link OutPacket}, with given info.
	 *
	 * @param outPacket
	 * 		The OutPacket this method should encode to.
	 * @param mask
	 * 		Which info should be encoded.
	 */
	public void encode(OutPacket outPacket, DBChar mask) {

		// CharacterData::Decode
		outPacket.encodeLong(mask.get());
		outPacket.encodeByte(getCombatOrders());
		for (int i = 0; i < GameConstants.MAX_PET_AMOUNT; i++) {
			if (i < getPets().size()) {
				outPacket.encodeInt(getPets().get(i).getActiveSkillCoolTime());
			} else {
				outPacket.encodeInt(0);
			}
		}
		outPacket.encodeByte(0); // unk, not in kmst
		byte sizeByte = 0;
		outPacket.encodeByte(sizeByte);
		for (int i = 0; i < sizeByte; i++) {
			outPacket.encodeInt(0);
		}

		int sizee = 0;
		outPacket.encodeInt(sizee);
		for (int i = 0; i < sizee; i++) {
			outPacket.encodeInt(0); // nKey
			outPacket.encodeLong(0); // pInfo
		}
		outPacket.encodeByte(0); // again unsure
		if (mask.isInMask(DBChar.Character)) {
			getAvatarData().getCharacterStat().encode(outPacket);
			outPacket.encodeByte(getFriendRecords().size());
			boolean hasBlessingOfFairy = getBlessingOfFairy() != null;
			outPacket.encodeByte(hasBlessingOfFairy);
			if (hasBlessingOfFairy) {
				outPacket.encodeString(getBlessingOfFairy());
			}
			boolean hasBlessingOfEmpress = getBlessingOfEmpress() != null;
			outPacket.encodeByte(hasBlessingOfEmpress);
			if (hasBlessingOfEmpress) {
				outPacket.encodeString(getBlessingOfEmpress());
			}
			outPacket.encodeByte(false); // ultimate explorer, deprecated
		}
		if (mask.isInMask(DBChar.Money)) {
			outPacket.encodeLong(getMoney());
		}
		if (mask.isInMask(DBChar.ItemSlotConsume) || mask.isInMask(DBChar.ExpConsumeItem)) {
			outPacket.encodeInt(getExpConsumeItems().size());
			for (ExpConsumeItem eci : getExpConsumeItems()) {
				eci.encode(outPacket);
			}
		}
		if (mask.isInMask(DBChar.ItemSlotConsume) || mask.isInMask(DBChar.ShopBuyLimit)) {
			int size = 0;
			outPacket.encodeInt(size);
			for (int i = 0; i < size; i++) {
				outPacket.encodeInt(0);
				outPacket.encodeInt(0);
				outPacket.encodeInt(0);
				outPacket.encodeInt(0);
				outPacket.encodeInt(0);
				outPacket.encodeLong(0);
				outPacket.encodeLong(0);
			}
		}
		if (mask.isInMask(DBChar.MonsterBattleInfo)) {
			int count = 0; // MonsterBattle_MobInfo
			outPacket.encodeInt(count);
			if (getMonsterBattleMobInfos() != null) {
				for (MonsterBattleMobInfo mbmi : getMonsterBattleMobInfos()) {
					mbmi.encode(outPacket);
					// TODO int int int int int int byte int int
				}
			}
			outPacket.encodeInt(getId());

			outPacket.encodeInt(0);
			outPacket.encodeInt(0);
			outPacket.encodeInt(0);
			outPacket.encodeInt(0);
			outPacket.encodeInt(0);
			outPacket.encodeInt(0);
			outPacket.encodeInt(0);

			boolean hasMonsterBattleLadder = getMonsterBattleLadder() != null;
			outPacket.encodeByte(hasMonsterBattleLadder);
			if (hasMonsterBattleLadder) {
				getMonsterBattleLadder().encode(outPacket); // TODO GW_MonsterBattleLadder_UserInfo::Decode
			}
			boolean hasMonsterBattleRankInfo = getMonsterBattleRankInfo() != null;
			outPacket.encodeByte(hasMonsterBattleRankInfo);
			if (hasMonsterBattleRankInfo) {
				getMonsterBattleRankInfo().encode(outPacket); // TODO GW_MonsterBattleRankInfo::Decode(&dummyBLD, nSlotHyper);
			}
			outPacket.encodeByte(hasMonsterBattleRankInfo);
			// again?
			if (hasMonsterBattleRankInfo) {
				getMonsterBattleRankInfo().encode(outPacket); // TODO GW_MonsterBattleRankInfo::Decode(&dummyBLD, nSlotHyper);
			}
		}
		if (mask.isInMask(DBChar.InventorySize)) {
			outPacket.encodeByte(getEquipInventory().getSlots());
			outPacket.encodeByte(getConsumeInventory().getSlots());
			outPacket.encodeByte(getEtcInventory().getSlots());
			outPacket.encodeByte(getInstallInventory().getSlots());
			outPacket.encodeByte(getCashInventory().getSlots());
		}

		if (mask.isInMask(DBChar.AdminShopCount)) {
			outPacket.encodeInt(0); // ???
			outPacket.encodeInt(0);
		}
		if (mask.isInMask(DBChar.ItemSlotEquip)) {
			outPacket.encodeByte(0); // ?
			List<Item> equippedItems = getEquippedInventory().getItems();
			equippedItems.sort(Comparator.comparingInt(Item::getBagIndex));
			// Normal equipped items
			for (Item item : equippedItems) {
				Equip equip = (Equip) item;
				if (item.getBagIndex() > BP_BASE.getVal() && item.getBagIndex() < BP_END.getVal()) {
					outPacket.encodeShort(equip.getBagIndex());
					equip.encode(outPacket);
				}
			}
			outPacket.encodeShort(0);
			// Cash equipped items
			for (Item item : getEquippedInventory().getItems()) {
				Equip equip = (Equip) item;
				if (item.getBagIndex() >= CBP_BASE.getVal() && item.getBagIndex() <= CBP_END.getVal()) {
					outPacket.encodeShort(equip.getBagIndex() - 100);
					equip.encode(outPacket);
				}
			}
			outPacket.encodeShort(0);
			// Equip inventory
			for (Item item : getEquipInventory().getItems()) {
				Equip equip = (Equip) item;
				outPacket.encodeShort(equip.getBagIndex());
				equip.encode(outPacket);
			}
			outPacket.encodeShort(0);
			// NonBPEquip::Decode (Evan)
			for (Item item : getEquippedInventory().getItems()) {
				Equip equip = (Equip) item;
				if (item.getBagIndex() >= EVAN_BASE.getVal() && item.getBagIndex() < EVAN_END.getVal()) {
					outPacket.encodeShort(equip.getBagIndex());
					equip.encode(outPacket);
				}
			}
			outPacket.encodeShort(0);
			// VirtualEquipInventory::Decode (Android)
			// >= 20k < 200024?
			for (Item item : getEquippedInventory().getItems()) {
				Equip equip = (Equip) item;
				if (item.getBagIndex() >= MECH_BASE.getVal() && item.getBagIndex() < MECH_END.getVal()) {
					outPacket.encodeShort(equip.getBagIndex());
					equip.encode(outPacket);
				}
			}
			outPacket.encodeShort(0);
			// Guessing pet consume items, could very well be wrong
			for (Item item : getEquippedInventory().getItems()) {
				Equip equip = (Equip) item;
				if (item.getBagIndex() >= 200 && item.getBagIndex() <= 300) {
					outPacket.encodeShort(equip.getBagIndex());
					equip.encode(outPacket);
				}
			}
			outPacket.encodeShort(0);
			// Android
			for (Item item : getEquippedInventory().getItems()) {
				Equip equip = (Equip) item;
				if (item.getBagIndex() >= AP_BASE.getVal() && item.getBagIndex() <= AP_END.getVal()) {
					outPacket.encodeShort(equip.getBagIndex());
					equip.encode(outPacket);
				}
			}
			outPacket.encodeShort(0);
			// Angelic Buster
			for (Item item : getEquippedInventory().getItems()) {
				Equip equip = (Equip) item;
				if (item.getBagIndex() >= DU_BASE.getVal() && item.getBagIndex() < DU_END.getVal()) {
					outPacket.encodeShort(equip.getBagIndex());
					equip.encode(outPacket);
				}
			}
			outPacket.encodeShort(0);
			// Bits
			for (Item item : getEquippedInventory().getItems()) {
				Equip equip = (Equip) item;
				if (item.getBagIndex() >= BITS_BASE.getVal() && item.getBagIndex() < BITS_END.getVal()) {
					outPacket.encodeShort(equip.getBagIndex());
					equip.encode(outPacket);
				}
			}
			outPacket.encodeShort(0);
			// Zero
			for (Item item : getEquippedInventory().getItems()) {
				Equip equip = (Equip) item;
				if (item.getBagIndex() >= ZERO_BASE.getVal() && item.getBagIndex() < ZERO_END.getVal()) {
					outPacket.encodeShort(equip.getBagIndex());
					equip.encode(outPacket);
				}
			}
			outPacket.encodeShort(0);
			// Totems
			for (Item item : getEquippedInventory().getItems()) {
				Equip equip = (Equip) item;
				if (item.getBagIndex() >= TOTEM_BASE.getVal() && item.getBagIndex() < TOTEM_END.getVal()) {
					outPacket.encodeShort(equip.getBagIndex());
					equip.encode(outPacket);
				}
			}
			outPacket.encodeShort(0);
			// Maybe zero beta cash?
			for (Item item : getEquippedInventory().getItems()) {
				Equip equip = (Equip) item;
				if (item.getBagIndex() >= MBP_BASE.getVal() && item.getBagIndex() < MBP_END.getVal()) {
					outPacket.encodeShort(equip.getBagIndex());
					equip.encode(outPacket);
				}
			}
			outPacket.encodeShort(0);
			// Haku
			for (Item item : getEquippedInventory().getItems()) {
				Equip equip = (Equip) item;
				if (item.getBagIndex() >= FP_START.getVal() && item.getBagIndex() < FP_END.getVal()) {
					outPacket.encodeShort(equip.getBagIndex());
					equip.encode(outPacket);
				}
			}
			outPacket.encodeShort(0);
			outPacket.encodeShort(0);
			outPacket.encodeShort(0);
		}
		if (mask.isInMask(DBChar.ItemSlotConsume)) {
			for (Item item : getConsumeInventory().getItems()) {
				outPacket.encodeByte(item.getBagIndex());
				item.encode(outPacket);
			}
			outPacket.encodeByte(0);
		}
		if (mask.isInMask(DBChar.ItemSlotInstall)) {
			for (Item item : getInstallInventory().getItems()) {
				outPacket.encodeByte(item.getBagIndex());
				item.encode(outPacket);
			}
			outPacket.encodeByte(0);
		}
		if (mask.isInMask(DBChar.ItemSlotEtc)) {
			for (Item item : getEtcInventory().getItems()) {
				outPacket.encodeByte(item.getBagIndex());
				item.encode(outPacket);
			}
			outPacket.encodeByte(0);
		}
		if (mask.isInMask(DBChar.ItemSlotCash)) {
			for (Item item : getCashInventory().getItems()) {
				outPacket.encodeByte(item.getBagIndex());
				item.encode(outPacket);
			}
			outPacket.encodeByte(0);
		}
		// BagDatas
		if (mask.isInMask(DBChar.ItemSlotConsume)) {
			// TODO
			outPacket.encodeInt(0);
		}
		if (mask.isInMask(DBChar.ItemSlotInstall)) {
			// TODO
			outPacket.encodeInt(0);
		}
		if (mask.isInMask(DBChar.ItemSlotEtc)) {
			// TODO
			outPacket.encodeInt(0);
		}
		if (mask.isInMask(DBChar.ItemSlotCash)) {
			// TODO
			outPacket.encodeInt(0);
		}
		// End bagdatas
		if (mask.isInMask(DBChar.CoreAura)) {
			int val = 0;
			outPacket.encodeInt(val);
			for (int i = 0; i < val; i++) {
				outPacket.encodeInt(0);
				outPacket.encodeLong(0);
			}
		}
		if (mask.isInMask(DBChar.ItemPot)) {
			boolean hasItemPot = getItemPots() != null;
			outPacket.encodeByte(hasItemPot);
			if (hasItemPot) {
				for (int i = 0; i < getItemPots().size(); i++) {
					getItemPots().get(i).encode(outPacket);
					outPacket.encodeByte(i != getItemPots().size() - 1);
				}
			}
		}

		if (mask.isInMask(DBChar.SkillRecord)) {
			boolean encodeSkills = getSkills().size() > 0;
			outPacket.encodeByte(encodeSkills);
			if (encodeSkills) {
				Set<LinkSkill> linkSkills = getLinkSkills();
				short size = (short) (getSkills().size() + linkSkills.size());
				outPacket.encodeShort(size);
				for (Skill skill : getSkills()) {
					outPacket.encodeInt(skill.getSkillId());
					outPacket.encodeInt(skill.getCurrentLevel());
					outPacket.encodeFT(FileTime.getFileTimeFromType(FileTime.Type.PERMANENT));
					if (SkillConstants.isSkillNeedMasterLevel(skill.getSkillId())) {
						outPacket.encodeInt(skill.getMasterLevel());
					}
				}
				for (LinkSkill linkSkill : linkSkills) {
					outPacket.encodeInt(linkSkill.getLinkSkillID());
					outPacket.encodeInt(linkSkill.getOwnerID());
					outPacket.encodeFT(FileTime.getFileTimeFromType(FileTime.Type.PERMANENT));
					if (SkillConstants.isSkillNeedMasterLevel(linkSkill.getLinkSkillID())) {
						outPacket.encodeInt(3); // whatevs
					}
				}
				outPacket.encodeShort(linkSkills.size());
				for (LinkSkill linkSkill : linkSkills) {
					outPacket.encodeInt(linkSkill.getLinkSkillID()); // another nCount
					outPacket.encodeShort(linkSkill.getLevel() - 1); // idk
				}
			} else {
				short size = 0;
				outPacket.encodeShort(size);
				for (int i = 0; i < size; i++) {
					outPacket.encodeInt(0); // nTI
					outPacket.encodeInt(0); // sValue
				}
				short size2 = 0;
				outPacket.encodeShort(size2);
				for (int i = 0; i < size2; i++) {
					outPacket.encodeInt(0); // nTI
				}

				short size3 = 0;
				outPacket.encodeShort(size3);
				for (int i = 0; i < size3; i++) {
					outPacket.encodeInt(0); // nTI
					outPacket.encodeFT(new FileTime(0)); // pInfo
				}
				short size4 = 0;
				outPacket.encodeShort(size2);
				for (int i = 0; i < size2; i++) {
					outPacket.encodeInt(0); // nTI
				}

				short size5 = 0;
				outPacket.encodeShort(size);
				for (int i = 0; i < size; i++) {
					outPacket.encodeInt(0); // nTI
					outPacket.encodeInt(0); // sValue
				}
				short size6 = 0;
				outPacket.encodeShort(size2);
				for (int i = 0; i < size2; i++) {
					outPacket.encodeInt(0); // nTI
				}
			}
		}

		if (mask.isInMask(DBChar.SkillCooltime)) {
			short size = 0;
			outPacket.encodeShort(size);
			for (int i = 0; i < size; i++) {
				outPacket.encodeInt(0); // nSkillId
				outPacket.encodeInt(0); // nSkillCooltime
			}
		}
		if (mask.isInMask(DBChar.QuestRecord)) {
			// modified/deleted, not completed anyway
			boolean removeAllOldEntries = true;
			outPacket.encodeByte(removeAllOldEntries);
			short size = (short) getQuestManager().getQuestsInProgress().size();
			outPacket.encodeShort(size);
			for (Quest quest : getQuestManager().getQuestsInProgress()) {
				outPacket.encodeInt(quest.getQRKey());
				outPacket.encodeString(quest.getQRValue());
			}
			if (!removeAllOldEntries) {
				// blacklisted quests
				short size2 = 0;
				outPacket.encodeShort(size2);
				for (int i = 0; i < size2; i++) {
					outPacket.encodeInt(0); // nQRKey
				}
			}
			size = 0;
			outPacket.encodeShort(size);
			// Not sure what this is for
			for (int i = 0; i < size; i++) {
				outPacket.encodeString("");
				outPacket.encodeString("");
			}
		}
		if (mask.isInMask(DBChar.QuestComplete)) {
			boolean removeAllOldEntries = true;
			outPacket.encodeByte(removeAllOldEntries);
			Set<Quest> completedQuests = getQuestManager().getCompletedQuests();
			outPacket.encodeShort(completedQuests.size());
			for (Quest quest : completedQuests) {
				outPacket.encodeInt(quest.getQRKey());
				outPacket.encodeInt(0); // Timestamp of completion
			}
			if (!removeAllOldEntries) {
				short size = 0;
				outPacket.encodeShort(size);
				for (int i = 0; i < size; i++) {
					outPacket.encodeInt(0); // nQRKey?
				}
			}
		}
		if (mask.isInMask(DBChar.MinigameRecord)) {
			int size = 0;
			outPacket.encodeShort(size);
			for (int i = 0; i < size; i++) {
				new MiniGameRecord().encode(outPacket);
			}
		}
		if (mask.isInMask(DBChar.CoupleRecord)) {
			int coupleSize = 0;
			outPacket.encodeShort(coupleSize);
			for (int i = 0; i < coupleSize; i++) {
				new CoupleRecord().encode(outPacket);
			}
			int friendSize = 0;
			outPacket.encodeShort(friendSize);
			for (int i = 0; i < friendSize; i++) {
				new FriendRecord().encode(outPacket);
			}
			int marriageSize = 0;
			outPacket.encodeShort(marriageSize);
			for (int i = 0; i < marriageSize; i++) {
				new MarriageRecord().encode(outPacket);
			}
		}

		if (mask.isInMask(DBChar.MapTransfer)) {
			for (int i = 0; i < 5; i++) {
				outPacket.encodeInt(0);
			}
			for (int i = 0; i < 10; i++) {
				outPacket.encodeInt(0);
			}
			for (int i = 0; i < 13; i++) {
				outPacket.encodeInt(0);
			}
			for (int i = 0; i < 13; i++) {
				outPacket.encodeInt(0);
			}
		}
		if (mask.isInMask(DBChar.MonsterBookCover)) {
			outPacket.encodeInt(getMonsterBookInfo().getCoverID());
		}
		if (mask.isInMask(DBChar.MonsterBookCard)) {
			boolean isCompleted = false;
			outPacket.encodeByte(isCompleted);
			if (!isCompleted) {
				short size = (short) getMonsterBookInfo().getCards().size();
				outPacket.encodeShort(size);
				for (int card : getMonsterBookInfo().getCards()) {
					outPacket.encodeShort(card);
					outPacket.encodeByte(true); // bEnabled?
				}
			} else {
				outPacket.encodeShort(0); // card list size
				short encSize = 0;
				outPacket.encodeShort(encSize);
				outPacket.encodeArr(new byte[encSize]);
				encSize = 0;
				outPacket.encodeShort(encSize);
				outPacket.encodeArr(new byte[encSize]);
			}
			outPacket.encodeInt(getMonsterBookInfo().getSetID()); // monsterbook set
		}
		if (mask.isInMask(DBChar.QuestCompleteOld)) {
			short size = 0;
			outPacket.encodeShort(size);
			for (int i = 0; i < size; i++) {
				outPacket.encodeShort(0);
			}
		}
		if (mask.isInMask(DBChar.Familiar)) {
			outPacket.encodeInt(getFamiliars().size());
			for (Familiar familiar : getFamiliars()) {
				familiar.encode(outPacket);
			}
		}
		if (mask.isInMask(DBChar.NewYearCard)) {
			short size = 0;
			outPacket.encodeShort(size);
			for (int i = 0; i < size; i++) {
				outPacket.encodeInt(0);
				outPacket.encodeInt(0);
				outPacket.encodeString("");
				outPacket.encodeByte(0);
				outPacket.encodeLong(0);
				outPacket.encodeInt(0);
				outPacket.encodeString("");
				outPacket.encodeByte(0);
				outPacket.encodeByte(0);
				outPacket.encodeLong(0);
				outPacket.encodeString("");
			}
		}
		if (mask.isInMask(DBChar.QuestRecordEx)) {
			outPacket.encodeShort(getQuestManager().getEx().size());
			for (Quest quest : getQuestManager().getEx()) {
				outPacket.encodeInt(quest.getQRKey());
				outPacket.encodeString(quest.getQRValue());
			}
		}
		if (mask.isInMask(DBChar.Avatar)) {

			short size = 0;
			outPacket.encodeShort(size);
			for (int i = 0; i < size; i++) {
				outPacket.encodeInt(0); // sValue
				new AvatarLook().encode(outPacket);
			}
		}
		if (mask.isInMask(DBChar.MapTransfer)) {
			int size = 0;
			outPacket.encodeInt(0);
			for (int i = 0; i < size; i++) {
				outPacket.encodeInt(0);
				outPacket.encodeInt(0);
			}
		}
		if (mask.isInMask(DBChar.WildHunterInfo)) {
			if (JobConstants.isWildHunter(getAvatarData().getCharacterStat().getJob())) {
				getWildHunterInfo().encode(outPacket); // GW_WildHunterInfo::Decode
			}
		}
		if (mask.isInMask(DBChar.ZeroInfo)) {
			if (JobConstants.isZero(getAvatarData().getCharacterStat().getJob())) {
				if (getZeroInfo() == null) {
					initZeroInfo();
				}
				getZeroInfo().encode(outPacket); //ZeroInfo::Decode
			}
		}
		if (mask.isInMask(DBChar.ShopBuyLimit)) {
			short size = 0;
			outPacket.encodeShort(size);
			for (int i = 0; i < size; i++) {
				// Encode shop buy limit
			}
		}
		if (mask.isInMask(DBChar.StolenSkills)) {
			if (JobConstants.isPhantom(getAvatarData().getCharacterStat().getJob())) {
				for (int i = 0; i<15; i++) {
					StolenSkill stolenSkill = getStolenSkillByPosition(i);
					outPacket.encodeInt(stolenSkill == null ? 0 : stolenSkill.getSkillid());
				}
			} else {
				outPacket.encodeInt(0);
				outPacket.encodeInt(0);
				outPacket.encodeInt(0);
				outPacket.encodeInt(0);

				outPacket.encodeInt(0);
				outPacket.encodeInt(0);
				outPacket.encodeInt(0);
				outPacket.encodeInt(0);

				outPacket.encodeInt(0);
				outPacket.encodeInt(0);
				outPacket.encodeInt(0);

				outPacket.encodeInt(0);
				outPacket.encodeInt(0);

				outPacket.encodeInt(0);
				outPacket.encodeInt(0);
			}
		}
		if (mask.isInMask(DBChar.ChosenSkills)) {
			if (JobConstants.isPhantom(getAvatarData().getCharacterStat().getJob())) {
				for (int i = 1; i <= 5; i++) { //Shifted by +1 to accomodate the Skill Management Tabs
					ChosenSkill chosenSkill = getChosenSkillByPosition(i);
					outPacket.encodeInt(chosenSkill == null ? 0 : ( isChosenSkillInStolenSkillList(chosenSkill.getSkillId()) ? chosenSkill.getSkillId() : 0) );
				}
			} else {
				for (int i = 0; i < 5; i++) {
					outPacket.encodeInt(0);
				}
			}
		}
		if (mask.isInMask(DBChar.CharacterPotentialSkill)) {
			outPacket.encodeShort(getPotentials().size());
			for (CharacterPotential cp : getPotentials()) {
				cp.encode(outPacket);
			}
		}
		if (mask.isInMask(DBChar.SoulCollection)) {
			short size = 0;
			outPacket.encodeShort(size);
			for (int i = 0; i < size; i++) {
				outPacket.encodeInt(0); //
				outPacket.encodeInt(0); //
			}
		}
		sizee = 0;
		outPacket.encodeInt(sizee);
		for (int i = 0; i < sizee; i++) {
			outPacket.encodeString("");
			// sub_73A1A0
			outPacket.encodeInt(0);
			outPacket.encodeString("");
			int size = 0;
			outPacket.encodeInt(size);
			for (int j = 0; j < size; j++) {
				outPacket.encodeByte(0);
			}
		}
		outPacket.encodeByte(0); // idk
//        outPacket.encodeArr(Util.getByteArrayByString("01 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 FF FF FF FF 00 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 0B 00 43 72 65 61 74 69 6E 67 2E 2E 2E 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 02 00 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 40 E0 FD 3B 37 4F 01 00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 40 E0 FD 3B 37 4F 01 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 01 00 00 00 00 00 00 00 64 00 00 00 00 80 05 BB 46 E6 17 02 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 40 E0 FD 3B 37 4F 01 00 01 00 00 01 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 40 64 2B 70 84 7A D3 01 64 00 00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00"));/*
		if (mask.isInMask(DBChar.Character)) {
			outPacket.encodeInt(0); // honor level, deprecated
			outPacket.encodeInt(getHonorExp()); // honor exp
		}
		if (mask.isInMask(DBChar.Money)) {
			boolean shouldIEncodeThis = true;
			outPacket.encodeByte(shouldIEncodeThis);
			if (shouldIEncodeThis) {
				short size = 0;
				outPacket.encodeShort(size);
				for (int i = 0; i < size; i++) {
					short category = 0;
					outPacket.encodeShort(category);
					short size2 = 0;
					outPacket.encodeShort(size2);
					for (int i2 = 0; i2 < size2; i2++) {
						outPacket.encodeInt(0); // nItemId
						outPacket.encodeInt(0); // nCount
					}
				}
			} else {
				short size2 = 0;
				outPacket.encodeShort(size2);
				for (int i2 = 0; i2 < size2; i2++) {
					outPacket.encodeShort(0); // nCategory
					outPacket.encodeInt(0); // nItemId
					outPacket.encodeInt(0); // nCount
				}

			}
		}
		if (mask.isInMask(DBChar.ReturnEffectInfo)) {
//            getReturnEffectInfo().encode(outPacket); // ReturnEffectInfo::Decode
			outPacket.encodeByte(0);
		}
		if (mask.isInMask(DBChar.DressUpInfo)) {
			new DressUpInfo().encode(outPacket); // GW_DressUpInfo::Decode
		}
		if (mask.isInMask(DBChar.Unk2)) {
			outPacket.encodeInt(1);
			outPacket.encodeInt(0);
			outPacket.encodeLong(0);
			outPacket.encodeString("");
			outPacket.encodeInt(0);
		}
		if (mask.isInMask(DBChar.CoreInfo)) {
			// GW_Core
			short size = 0;
			outPacket.encodeShort(size);
			for (int i = 0; i < size; i++) {
				outPacket.encodeShort(0); // nPos
				outPacket.encodeInt(0); // nCoreID
				outPacket.encodeInt(0); // nLeftCount
			}

			size = 0;
			outPacket.encodeShort(size);
			for (int i = 0; i < size; i++) {
				outPacket.encodeShort(0); // nPos
				outPacket.encodeInt(0); // nCoreID
				outPacket.encodeInt(0); // nLeftCount
			}
		}
		if (mask.isInMask(DBChar.FarmPotential)) {
			new FarmPotential().encode(outPacket); // FARM_POTENTIAL::Decode
		}
		if (mask.isInMask(DBChar.FarmUserInfo)) {
			new FarmUserInfo().encode(outPacket); // FarmUserInfo::Decode
			outPacket.encodeInt(0);
			outPacket.encodeInt(0);
		}
		if (mask.isInMask(DBChar.MemorialCubeInfo)) {
			new MemorialCubeInfo().encode(outPacket); // MemorialCubeInfo::Decode
		}
		if (mask.isInMask(DBChar.LikePoint)) {
			new LikePoint().encode(outPacket);
		}
		if (mask.isInMask(DBChar.RunnerGameRecord)) {
			new RunnerGameRecord().encode(outPacket);
		}
		short sizeO = 0;
		outPacket.encodeShort(sizeO);
		for (int i = 0; i < sizeO; i++) {
			outPacket.encodeInt(0);
			outPacket.encodeString("");

		}
		if (mask.isInMask(DBChar.Unk2)) { // Mushy: Monster Collection
			int size = 0;
			outPacket.encodeShort(size);
			for (int i = 0; i < size; i++) {
				outPacket.encodeInt(0); // same as above?
				outPacket.encodeString("");
			}
		}
		boolean farmOnline = false;
		outPacket.encodeByte(farmOnline);
		int sizeInt = 0;
		// CharacterData::DecodeTextEquipInfo
		outPacket.encodeInt(sizeInt);
		for (int i = 0; i < sizeInt; i++) {
			outPacket.encodeInt(0);
			outPacket.encodeString("");
		}

		if (mask.isInMask(DBChar.VisitorLog4)) {
			// mushy
			outPacket.encodeByte(1);
			outPacket.encodeByte(0);
			outPacket.encodeInt(1);
			outPacket.encodeInt(0);
			outPacket.encodeInt(100);
			outPacket.encodeFT(FileTime.getFileTimeFromType(FileTime.Type.MAX_TIME));
			outPacket.encodeShort(0);
			outPacket.encodeShort(0);
		}

		if (mask.isInMask(DBChar.Unk4)) {
			outPacket.encodeByte(0);
		}

		if (mask.isInMask(DBChar.Unk)) {
			outPacket.encodeInt(0);
			outPacket.encodeInt(0);
		}

		if (mask.isInMask(DBChar.CoreAura)) {
			outPacket.encodeInt(0);
			outPacket.encodeInt(0);
			outPacket.encodeInt(0);
			outPacket.encodeInt(0);
			outPacket.encodeInt(0);

			outPacket.encodeInt(0);
			outPacket.encodeInt(0);
			outPacket.encodeInt(0);
			outPacket.encodeInt(0);
			outPacket.encodeInt(0);
			outPacket.encodeInt(0);

			outPacket.encodeInt(0);
			outPacket.encodeInt(0);
			outPacket.encodeInt(0);
			outPacket.encodeInt(0);

			outPacket.encodeLong(0);
			outPacket.encodeByte(0);

			outPacket.encodeByte(1);
		}

		if (mask.isInMask(DBChar.EquipExt)) {
			short size = 0;
			outPacket.encodeShort(size);
			for (int i = 0; i < size; i++) {
				outPacket.encodeShort(0);
				outPacket.encodeShort(0);
			}
		}

		if (mask.isInMask(DBChar.RedLeafInfo)) {
			// red leaf information
			outPacket.encodeInt(getAccId());
			outPacket.encodeInt(getId());
			outPacket.encodeInt(0);
			outPacket.encodeInt(0);
		}
		outPacket.encodeArr(new byte[32]); // real

	}

	@Override
	public boolean equals(Object other) {
		return other instanceof Char && ((Char) other).getId() == getId();
	}

	private String getBlessingOfEmpress() {
		return null;
	}

	private String getBlessingOfFairy() {
		return null;
	}

	public void setCombatOrders(int combatOrders) {
		this.combatOrders = combatOrders;
	}

	public int getCombatOrders() {
		return combatOrders;
	}

	public QuestManager getQuestManager() {
		if (questManager.getChr() == null) {
			questManager.setChr(this);
		}
		return questManager;
	}

	public void setQuests(QuestManager questManager) {
		this.questManager = questManager;
	}

	public List<ItemPot> getItemPots() {
		return null;
	}

	public void setItemPots(List<ItemPot> itemPots) {
		this.itemPots = itemPots;
	}

	public List<Pet> getPets() {
		return pets;
	}

	public void setPets(List<Pet> pets) {
		this.pets = pets;
	}

	public List<FriendRecord> getFriendRecords() {
		return friendRecords;
	}

	public void setFriendRecords(List<FriendRecord> friendRecords) {
		this.friendRecords = friendRecords;
	}

	public long getMoney() {
		return getAvatarData().getCharacterStat().getMoney();
	}

	public List<ExpConsumeItem> getExpConsumeItems() {
		return expConsumeItems;
	}

	public void setExpConsumeItems(List<ExpConsumeItem> expConsumeItems) {
		this.expConsumeItems = expConsumeItems;
	}

	public List<MonsterBattleMobInfo> getMonsterBattleMobInfos() {
		return monsterBattleMobInfos;
	}

	public void setMonsterBattleMobInfos(List<MonsterBattleMobInfo> monsterBattleMobInfos) {
		this.monsterBattleMobInfos = monsterBattleMobInfos;
	}

	public MonsterBattleLadder getMonsterBattleLadder() {
		return monsterBattleLadder;
	}

	public void setMonsterBattleLadder(MonsterBattleLadder monsterBattleLadder) {
		this.monsterBattleLadder = monsterBattleLadder;
	}

	public MonsterBattleRankInfo getMonsterBattleRankInfo() {
		return monsterBattleRankInfo;
	}

	public void setMonsterBattleRankInfo(MonsterBattleRankInfo monsterBattleRankInfo) {
		this.monsterBattleRankInfo = monsterBattleRankInfo;
	}

	public List<Inventory> getInventories() {
		return new ArrayList<>(Arrays.asList(getEquippedInventory(), getEquipInventory(),
				getConsumeInventory(), getEtcInventory(), getInstallInventory(), getCashInventory()));
	}

	public Inventory getInventoryByType(InvType invType) {
		switch (invType) {
			case EQUIPPED:
				return getEquippedInventory();
			case EQUIP:
				return getEquipInventory();
			case CONSUME:
				return getConsumeInventory();
			case ETC:
				return getEtcInventory();
			case INSTALL:
				return getInstallInventory();
			case CASH:
				return getCashInventory();
			default:
				return null;
		}
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public int getFieldID() {
		return (int) getAvatarData().getCharacterStat().getPosMap();
	}

	private void setFieldID(long fieldId) {
		setFieldID((int) fieldId);
	}

	private void setFieldID(int fieldID) {
		getAvatarData().getCharacterStat().setPosMap(fieldID);
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public void setField(Field field) {
		this.field = field;
		setFieldID(field.getId());
	}

	public Field getField() {
		return field;
	}

	/**
	 * Sets the job of this Char with a given id. Does nothing if the id is invalid.
	 * If it is valid, will set this Char's job, add all Skills that the job should have by default,
	 * and sends the info to the client.
	 *
	 * @param id
	 */
	public void setJob(int id) {
		JobConstants.JobEnum job = JobConstants.JobEnum.getJobById((short) id);
		if (job == null) {
			return;
		}
		getAvatarData().getCharacterStat().setJob(id);
		setJobHandler(JobManager.getJobById((short) id, this));
		List<Skill> skills = SkillData.getSkillsByJob((short) id);
		skills.forEach(this::addSkill);
		getClient().write(WvsContext.changeSkillRecordResult(skills, true, false, false, false));
		notifyChanges();
	}

	public short getJob() {
		return getAvatarData().getCharacterStat().getJob();
	}

	/**
	 * Sets the SP to the current job level.
	 *
	 * @param num
	 * 		The new SP amount.
	 */
	public void setSpToCurrentJob(int num) {
		if (JobConstants.isExtendSpJob(getJob())) {
			byte jobLevel = (byte) JobConstants.getJobLevel(getJob());
			getAvatarData().getCharacterStat().getExtendSP().setSpToJobLevel(jobLevel, num);
		} else {
			getAvatarData().getCharacterStat().setSp(num);
		}
	}
	/**
	 * Sets the SP to the job level according to the current level.
	 *
	 * @param num
	 * 		The amount of SP to add
	 */
	public void addSpToJobByCurrentLevel(int num) {
		CharacterStat cs = getAvatarData().getCharacterStat();
		if (JobConstants.isExtendSpJob(getJob())) {
			byte jobLevel = (byte) JobConstants.getJobLevelByCharLevel(getLevel());
			num += cs.getExtendSP().getSpByJobLevel(jobLevel);
			getAvatarData().getCharacterStat().getExtendSP().setSpToJobLevel(jobLevel, num);
		} else {
			num += cs.getSp();
			getAvatarData().getCharacterStat().setSp(num);
		}
	}

	public Set<Skill> getSkills() {
		return skills;
	}

	public void setSkills(Set<Skill> skills) {
		this.skills = skills;
	}

	/**
	 * Adds a {@link Skill} to this Char. Overrides the old Skill if the Char already had a Skill
	 * with the same id.
	 *
	 * @param skill
	 * 		The Skill this Char should get.
	 */
	public void addSkill(Skill skill) {
		skill.setCharId(getId());
		boolean isPassive = SkillConstants.isPassiveSkill(skill.getSkillId());
		boolean isChanged;
		if (getSkills().stream().noneMatch(s -> s.getSkillId() == skill.getSkillId())) {
			getSkills().add(skill);
			isChanged = true;
		} else {
			Skill oldSkill = getSkill(skill.getSkillId());
			isChanged = oldSkill.getCurrentLevel() != skill.getCurrentLevel();
			if (isPassive && isChanged) {
				removeFromBaseStatCache(oldSkill);
			}
			oldSkill.setCurrentLevel(skill.getCurrentLevel());
			oldSkill.setMasterLevel(skill.getMasterLevel());
		}
		// Change cache accordingly
		if (isPassive && isChanged) {
			addToBaseStatCache(skill);
		}
	}

	/**
	 * Initializes the BaseStat cache, by going through all the needed passive stat changers.
	 */
	public void initBaseStats() {
		getBaseStats().clear();
		getSkills().stream().filter(skill -> SkillConstants.isPassiveSkill(skill.getSkillId())).
				forEach(this::addToBaseStatCache);
	}

	/**
	 * Adds a Skill's info to the current base stat cache.
	 * @param skill The skill to add
	 */
	public void addToBaseStatCache(Skill skill) {
		SkillInfo si = SkillData.getSkillInfoById(skill.getSkillId());
		Map<BaseStat, Integer> stats = si.getBaseStatValues(this, skill.getCurrentLevel());
		stats.forEach(this::addBaseStat);
	}

	/**
	 * Removes a Skill's info from the current base stat cache.
	 * @param skill The skill to remove
	 */
	public void removeFromBaseStatCache(Skill skill) {
		SkillInfo si = SkillData.getSkillInfoById(skill.getSkillId());
		Map<BaseStat, Integer> stats = si.getBaseStatValues(this, skill.getCurrentLevel());
		stats.forEach(this::removeBaseStat);
	}

	/**
	 * Returns whether or not this Char has a {@link Skill} with a given id.
	 *
	 * @param id
	 * 		The id of the Skill.
	 *
	 * @return Whether or not this Char has a Skill with the given id.
	 */
	public boolean hasSkill(int id) {
		return getSkills().stream().anyMatch(s -> s.getSkillId() == id) && getSkill(id, false).getCurrentLevel() > 0;
	}

	/**
	 * Gets a {@link Skill} of this Char with a given id.
	 *
	 * @param id
	 * 		The id of the requested Skill.
	 *
	 * @return The Skill corresponding to the given id of this Char, or null if there is none.
	 */
	public Skill getSkill(int id) {
		return getSkill(id, false);
	}

	/**
	 * Gets a {@link Skill} with a given ID. If <code>createIfNull</code> is true, creates the Skill
	 * if it doesn't exist yet.
	 * If it is false, will return null if this Char does not have the given Skill.
	 *
	 * @param id
	 * 		The id of the requested Skill.
	 * @param createIfNull
	 * 		Whether or not this method should create the Skill if it doesn't exist.
	 *
	 * @return The Skill that the Char has, or <code>null</code> if there is no such skill and
	 * <code>createIfNull</code> is false.
	 */
	public Skill getSkill(int id, boolean createIfNull) {
		for (Skill s : getSkills()) {
			if (s.getSkillId() == id) {
				return s;
			}
		}
		return createIfNull ? createAndReturnSkill(id) : null;
	}

	/**
	 * Creates a new {@link Skill} for this Char.
	 *
	 * @param id
	 * 		The skillID of the Skill to be created.
	 *
	 * @return The new Skill.
	 */
	private Skill createAndReturnSkill(int id) {
		Skill skill = SkillData.getSkillDeepCopyById(id);
		addSkill(skill);
		return skill;
	}

	public void setStat(Stat charStat, int amount) {
		switch (charStat) {
			case str:
				getAvatarData().getCharacterStat().setStr(amount);
				break;
			case dex:
				getAvatarData().getCharacterStat().setDex(amount);
				break;
			case inte:
				getAvatarData().getCharacterStat().setInt(amount);
				break;
			case luk:
				getAvatarData().getCharacterStat().setLuk(amount);
				break;
			case hp:
				getAvatarData().getCharacterStat().setHp(amount);
				break;
			case mhp:
				getAvatarData().getCharacterStat().setMaxHp(amount);
				break;
			case mp:
				getAvatarData().getCharacterStat().setMp(amount);
				break;
			case mmp:
				getAvatarData().getCharacterStat().setMaxMp(amount);
				break;
			case ap:
				getAvatarData().getCharacterStat().setAp(amount);
				break;
			case level:
				getAvatarData().getCharacterStat().setLevel(amount);
				notifyChanges();
				break;
		}
	}

	/**
	 * Notifies all groups (such as party, guild) about all your changes, such as level and job.
	 */
	private void notifyChanges() {
		if (getParty() != null) {
			getParty().updateFull();
		}
		if (getGuild() != null) {
			// TODO
		}
	}

	/**
	 * Gets a raw Stat from this Char, unaffected by things such as equips and skills.
	 * @param charStat The requested Stat
	 * @return the requested stat's value
	 */
	public int getStat(Stat charStat) {
		CharacterStat cs = getAvatarData().getCharacterStat();
		switch (charStat) {
			case str:
				return cs.getStr();
			case dex:
				return cs.getDex();
			case inte:
				return cs.getInt();
			case luk:
				return cs.getLuk();
			case hp:
				return cs.getHp();
			case mhp:
				return cs.getMaxHp();
			case mp:
				return cs.getMp();
			case mmp:
				return cs.getMaxMp();
			case ap:
				return cs.getAp();
			case level:
				return cs.getLevel();
		}
		return -1;
	}

	/**
	 * Adds a Stat to this Char.
	 * @param charStat which Stat to add
	 * @param amount the amount of Stat to add
	 */
	public void addStat(Stat charStat, int amount) {
		setStat(charStat, getStat(charStat) + amount);
	}

	/**
	 * Adds a Stat to this Char, and immediately sends the packet to the client notifying the change.
	 * @param charStat which Stat to change
	 * @param amount the amount of Stat to add
	 */
	public void addStatAndSendPacket(Stat charStat, int amount) {
		addStat(charStat, amount);
		Map<Stat, Object> stats = new HashMap<>();
		switch(charStat) {
			case level:
				stats.put(charStat, (byte) getStat(charStat));
				break;
			case str:
			case dex:
			case inte:
			case luk:
			case ap:
				stats.put(charStat, (short) getStat(charStat));
				break;
			case hp:
			case mhp:
			case mp:
			case mmp:
				stats.put(charStat, getStat(charStat));
				break;
		}
		write(WvsContext.statChanged(stats));
	}

	/**
	 * Adds a certain amount of money to the current character. Also sends the
	 * packet to update the client's state.
	 *
	 * @param amount
	 * 		The amount of money to add. May be negative.
	 */
	public void addMoney(long amount) {
		CharacterStat cs = getAvatarData().getCharacterStat();
		long money = cs.getMoney();
		long newMoney = money + amount;
		if (newMoney >= 0) {
			newMoney = Math.min(GameConstants.MAX_MONEY, newMoney);
			Map<Stat, Object> stats = new HashMap<>();
			cs.setMoney(newMoney);
			stats.put(Stat.money, newMoney);
			write(WvsContext.statChanged(stats));
		}
	}

	/**
	 * The same as addMoney, but negates the amount.
	 *
	 * @param amount
	 * 		The money to deduct. May be negative.
	 */
	public void deductMoney(long amount) {
		addMoney(-amount);
	}

	public Position getOldPosition() {
		return oldPosition;
	}

	public void setOldPosition(Position oldPosition) {
		this.oldPosition = oldPosition;
	}

	public void setMoveAction(byte moveAction) {
		this.moveAction = moveAction;
	}

	public byte getMoveAction() {
		return moveAction;
	}

	/**
	 * Sends a message to this Char through the ScriptProgress packet.
	 *
	 * @param msg
	 * 		The message to display.
	 */
	public void chatScriptMessage(String msg) { write(User.scriptProgressMessage(msg));}

	/**
	 * Sends a message to this Char with a default colour {@link ChatMsgColour#GAME_MESSAGE}.
	 *
	 * @param msg
	 * 		The message to display.
	 */
	public void chatMessage(String msg) {
		chatMessage(GAME_MESSAGE, msg);
	}

	/**
	 * Sends a message to this Char with a given {@link ChatMsgColour colour}.
	 *
	 * @param clr
	 * 		The Colour this message should be in.
	 * @param msg
	 * 		The message to display.
	 */
	public void chatMessage(ChatMsgColour clr, String msg) {
		getClient().write(UserLocal.chatMsg(clr, msg));
	}

	/**
	 * Unequips an {@link Item}. Ensures that the hairEquips and both inventories get updated.
	 *
	 * @param item
	 * 		The Item to equip.
	 */
	public void unequip(Item item) {
		AvatarLook al = getAvatarData().getAvatarLook();
		int itemID = item.getItemId();
		getInventoryByType(EQUIPPED).removeItem(item);
		getInventoryByType(EQUIP).addItem(item);
		List<Integer> hairEquips = getAvatarData().getAvatarLook().getHairEquips();
		if (ItemConstants.isWeapon(itemID)) {
			al.setWeaponId(0);
		}
		if (hairEquips.contains(itemID)) {
			hairEquips.remove((Integer) itemID);
		}
		byte maskValue = AvatarModifiedMask.AvatarLook.getVal();
		getField().broadcastPacket(UserRemote.avatarModified(this, maskValue, (byte) 0), this);
		if (getTemporaryStatManager().hasStat(SoulMP)) {
			getTemporaryStatManager().removeStat(SoulMP, false);
			getTemporaryStatManager().removeStat(FullSoulMP, false);
			getTemporaryStatManager().sendResetStatPacket();
		}
	}

	/**
	 * Equips an {@link Item}. Ensures that the hairEquips and both inventories get updated.
	 *
	 * @param item
	 * 		The Item to equip.
	 */
	public void equip(Item item) {
		Equip equip = (Equip) item;
		if (equip.isEquipTradeBlock()) {
			equip.setTradeBlock(true);
			equip.setEquipTradeBlock(false);
		}
		AvatarLook al = getAvatarData().getAvatarLook();
		int itemID = item.getItemId();
		getInventoryByType(EQUIP).removeItem(item);
		getInventoryByType(EQUIPPED).addItem(item);
		List<Integer> hairEquips = getAvatarData().getAvatarLook().getHairEquips();
		if (ItemConstants.isWeapon(itemID)) {
			al.setWeaponId(itemID);
		}
		if (!hairEquips.contains(itemID)) {
			hairEquips.add(itemID);
		}
		byte maskValue = AvatarModifiedMask.AvatarLook.getVal();
		getField().broadcastPacket(UserRemote.avatarModified(this, maskValue, (byte) 0), this);
		initSoulMP();
	}

	public TemporaryStatManager getTemporaryStatManager() {
		return temporaryStatManager;
	}

	public void setTemporaryStatManager(TemporaryStatManager temporaryStatManager) {
		this.temporaryStatManager = temporaryStatManager;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setJobHandler(Job jobHandler) {
		this.jobHandler = jobHandler;
	}

	public Job getJobHandler() {
		return jobHandler;
	}

	public FuncKeyMap getFuncKeyMap() {
		return funcKeyMap;
	}

	public void setFuncKeyMap(FuncKeyMap funcKeyMap) {
		this.funcKeyMap = funcKeyMap;
	}

	/**
	 * Creates a {@link Rect} with regard to this character. Adds all values to this Char's
	 * position.
	 *
	 * @param rect
	 * 		The rectangle to use.
	 *
	 * @return The new rectangle.
	 */
	public Rect getRectAround(Rect rect) {
		int x = getPosition().getX();
		int y = getPosition().getY();
		return new Rect(x + rect.getLeft(), y + rect.getTop(), x + rect.getRight(), y + rect.getBottom());
	}

	/**
	 * Returns the Equip equipped at a certain {@link BodyPart}.
	 *
	 * @param bodyPart
	 * 		The requested bodyPart.
	 *
	 * @return The Equip corresponding to <code>bodyPart</code>. Null if there is none.
	 */
	public Item getEquippedItemByBodyPart(BodyPart bodyPart) {
		List<Item> items = getEquippedInventory().getItemsByBodyPart(bodyPart);
		return items.size() > 0 ? items.get(0) : null;
	}

	public void setLeft(boolean left) {
		this.left = left;
	}

	public boolean isLeft() {
		return left;
	}

	public MarriageRecord getMarriageRecord() {
		return marriageRecord;
	}

	public void setMarriageRecord(MarriageRecord marriageRecord) {
		this.marriageRecord = marriageRecord;
	}

	/**
	 * Returns a {@link Field} based on the current {@link FieldInstanceType} of this Char (channel,
	 * expedition,
	 * party or solo).
	 *
	 * @return The Field corresponding to the current FieldInstanceType.
	 */
	public Field getOrCreateFieldByCurrentInstanceType(int fieldID) {
		Field res = null;
		switch (getFieldInstanceType()) {
			case SOLO:
				if (getFields().containsKey(fieldID)) {
					res = getPersonalById(fieldID);
				} else {
					Field field = FieldData.getFieldCopyById(fieldID);
					addField(field);
					res = field;
				}
				break;
			case PARTY:
				res = getParty() != null ? getParty().getOrCreateFieldById(fieldID) : null;
				break;
			// TODO expedition
			default:
				res = getClient().getChannelInstance().getField(fieldID);
				break;
		}
		return res;
	}

	/**
	 * Warps this character to a given field, at the starting position.
	 * See {@link #warp(Field, Portal) warp}.
	 *
	 * @param toField
	 * 		The field to warp to.
	 */
	public void warp(Field toField) {
		warp(toField, toField.getPortalByName("sp"), false);
	}

	/**
	 * Warps this Char to a given {@link Field}, with the Field's "sp" portal as spawn position.
	 *
	 * @param toField
	 * 		The Field to warp to.
	 * @param characterData
	 * 		Whether or not the character data should be encoded.
	 */
	public void warp(Field toField, boolean characterData) {
		warp(toField, toField.getPortalByName("sp"), characterData);
	}

	/**
	 * Warps this Char to a given {@link Field} and {@link Portal}. Will not include character data.
	 *
	 * @param toField
	 * 		The Field to warp to.
	 * @param toPortal
	 * 		The Portal to spawn at.
	 */
	public void warp(Field toField, Portal toPortal) {
		warp(toField, toPortal, false);
	}

	/**
	 * Warps this character to a given field, at a given portal.
	 * Ensures that the previous map does not contain this Char anymore, and that the new field
	 * does.
	 * Ensures that all Lifes are immediately spawned for the new player.
	 *
	 * @param toField
	 * 		The {@link Field} to warp to.
	 * @param portal
	 * 		The {@link Portal} where to spawn at.
	 */
	public void warp(Field toField, Portal portal, boolean characterData) {
		if (toField == null) {
			return;
		}
		TemporaryStatManager tsm = getTemporaryStatManager();
		for (AffectedArea aa : tsm.getAffectedAreas()) {
			tsm.removeStatsBySkill(aa.getSkillID());
		}
		if (getField() != null) {
			getField().removeChar(this);
		}
		setField(toField);
		toField.addChar(this);
		fixBuggedItems();
		getClient().write(Stage.setField(this, toField, getClient().getChannel(), false, 0, characterData, hasBuffProtector(),
				(byte) (portal != null ? portal.getId() : 0), false, 100, null, true, -1));
		if (characterData) {
			initSoulMP();
			if (getGuild() != null) {
				write(WvsContext.guildResult(new GuildUpdate(getGuild())));
			}
			for (Friend f : getFriends()) {
				f.setFlag(getClient().getWorld().getCharByID(f.getFriendID()) != null
						? FriendFlag.FriendOnline
						: FriendFlag.FriendOffline);
			}
			for (Friend f : getAccount().getFriends()) {
				f.setFlag(getClient().getWorld().getAccountByID(f.getFriendAccountID()) != null
						? FriendFlag.AccountFriendOnline
						: FriendFlag.AccountFriendOffline);
			}
		}
		toField.spawnLifesForChar(this);
		for (Char c : toField.getChars()) {
			if (!c.equals(this)) {
				write(UserPool.userEnterField(c));
			}
		}
		notifyChanges();
		toField.execUserEnterScript(this);
		initPets();
		if (getDeathCount() > 0) {
			write(UserLocal.deathCountInfo(getDeathCount()));
		}
		if (field.getEliteState() == EliteState.ELITE_BOSS) {
			write(CField.eliteState(EliteState.ELITE_BOSS, true, GameConstants.ELITE_BOSS_BGM, null, null));
		}
		if (getActiveFamiliar() != null) {
			getField().broadcastPacket(CFamiliar.familiarEnterField(getId(), true, getActiveFamiliar(), true, false));
		}
	}

	/**
	 * Hacky fix for a bug I can't seem to fix. If having 3 equips in your inventory (x, y, z):
	 * equip x, equip y, swap x and z, equip x. Upon relogging, x will be in the EQUIP inventory,
	 * but with InvType EQUIPPED.
	 * Server side it goes correctly, but when logging out, Hibernate puts x back into the EQUIP
	 * inventory.
	 */
	private void fixBuggedItems() {
		List<Item> buggedItems = new ArrayList<>();
		for (Item item : getEquipInventory().getItems()) {
			if (item.getInvType() == InvType.EQUIPPED) {
				buggedItems.add(item);
			}
		}
		getEquipInventory().getItems().removeAll(buggedItems);
		getEquippedInventory().getItems().addAll(buggedItems);
		buggedItems = new ArrayList<>();
		for (Item item : getEquippedInventory().getItems()) {
			if (item.getInvType() == InvType.EQUIP) {
				buggedItems.add(item);
			}
		}
		getEquippedInventory().getItems().removeAll(buggedItems);
		getEquipInventory().getItems().addAll(buggedItems);

	}

	/**
	 * Adds a given amount of exp to this Char. Immediately checks for level-up possibility, and
	 * sends the updated
	 * stats to the client. Allows multi-leveling.
	 *
	 * @param amount
	 * 		The amount of exp to add.
	 */
	public void addExp(long amount) {
		ExpIncreaseInfo eii = new ExpIncreaseInfo();
		eii.setLastHit(true);
		eii.setIncEXP((int) Math.min(Integer.MAX_VALUE, amount));
		addExp(amount, eii);
	}

	/**
	 * Adds exp to this Char. Will calculate the extra exp gained from buffs and the exp rate of the server.
	 * Also takes an argument to show this info to the client. Will not send anything if this argument (eii) is null.
	 * @param amount The amount of exp to add
	 * @param eii The info to send to the client
	 */
	public void addExp(long amount, ExpIncreaseInfo eii) {
		int expFromExpR = (int) (amount * (getTotalStat(BaseStat.expR) / 100D));
		amount += expFromExpR;
		amount = amount > Long.MAX_VALUE / GameConstants.EXP_RATE ? Long.MAX_VALUE : amount * GameConstants.EXP_RATE * 10;
		CharacterStat cs = getAvatarData().getCharacterStat();
		long curExp = cs.getExp();
		int level = getStat(Stat.level);
		if (level >= GameConstants.charExp.length - 1) {
			return;
		}
		long newExp = curExp + amount;
		Map<Stat, Object> stats = new HashMap<>();
		while (newExp >= GameConstants.charExp[level]) {
			newExp -= GameConstants.charExp[level];
			addStat(Stat.level, 1);
			stats.put(Stat.level, (byte) getStat(Stat.level));
			getJobHandler().handleLevelUp();
			level++;
			heal(getMaxHP());
			healMP(getMaxMP());
		}
		cs.setExp(newExp);
		stats.put(Stat.exp, newExp);
		if (eii != null) {
			eii.setIndieBonusExp(expFromExpR);
			write(WvsContext.incExpMessage(eii));
		}
		getClient().write(WvsContext.statChanged(stats));
	}

	/**
	 * Adds a given amount of exp to this Char, however it does not display the Exp Message.
	 * Immediately checks for level-up possibility, and sends the updated
	 * stats to the client. Allows multi-leveling.
	 *
	 * @param amount
	 * 		The amount of exp to add.
	 */
	public void addExpNoMsg(long amount) {
		addExp(amount, null);
	}

	/**
	 * Writes a packet to this Char's client.
	 *
	 * @param outPacket
	 * 		The OutPacket to write.
	 */
	public void write(OutPacket outPacket) {
		if (getClient() != null) {
			getClient().write(outPacket);
		}
	}

	public ExpIncreaseInfo getExpIncreaseInfo() {
		return new ExpIncreaseInfo();
	}

	public WildHunterInfo getWildHunterInfo() {
		return wildHunterInfo;
	}


	public void setWildHunterInfo(WildHunterInfo wildHunterInfo) {
		this.wildHunterInfo = wildHunterInfo;
	}

	public ZeroInfo getZeroInfo() {
		return zeroInfo;
	}

	public void setZeroInfo(ZeroInfo zeroInfo) {
		this.zeroInfo = zeroInfo;
	}

	public int getNickItem() {
		return nickItem;
	}

	public void setNickItem(int nickItem) {
		this.nickItem = nickItem;
	}

	public void setDamageSkin(int itemID) {
		setDamageSkin(new DamageSkinSaveData(ItemConstants.getDamageSkinIDByItemID(itemID), itemID, false,
				StringData.getItemStringById(itemID)));
	}

	public void setDamageSkin(DamageSkinSaveData damageSkin) {
		this.damageSkin = damageSkin;
	}

	public DamageSkinSaveData getDamageSkin() {
		return damageSkin;
	}

	public DamageSkinSaveData getPremiumDamageSkin() {
		return premiumDamageSkin;
	}

	public void setPremiumDamageSkin(DamageSkinSaveData premiumDamageSkin) {
		this.premiumDamageSkin = premiumDamageSkin;
	}

	public void setPremiumDamageSkin(int itemID) {
		setPremiumDamageSkin(new DamageSkinSaveData(ItemConstants.getDamageSkinIDByItemID(itemID), itemID, false,
				StringData.getItemStringById(itemID)));
	}

	public void setPartyInvitable(boolean partyInvitable) {
		this.partyInvitable = partyInvitable;
	}

	/**
	 * Returns if this Char can be invited to a party.
	 *
	 * @return Whether or not this Char can be invited to a party.
	 */
	public boolean isPartyInvitable() {
		return partyInvitable;
	}

	/**
	 * Returns if this character is currently in its beta state.
	 *
	 * @return true if this Char is in a beta state.
	 */
	public boolean isZeroBeta() {
		return getZeroInfo() != null && getZeroInfo().isZeroBetaState();
	}

	/**
	 * Zero only.
	 * Goes into Beta form if Alpha, and into Alpha if Beta.
	 */
	public void swapZeroState() {
		if (!(JobConstants.isZero(getJob())) || getZeroInfo() == null) {
			return;
		}
		ZeroInfo oldInfo = getZeroInfo().deepCopy();
		ZeroInfo currentInfo = getZeroInfo();
		CharacterStat cs = getAvatarData().getCharacterStat();
		currentInfo.setZeroBetaState(!oldInfo.isZeroBetaState());
		currentInfo.setSubHP(cs.getHp());
		currentInfo.setSubMHP(cs.getMaxHp());
		currentInfo.setSubMP(cs.getMp());
		currentInfo.setSubMMP(cs.getMaxMp());
		cs.setHp(oldInfo.getSubHP());
		cs.setMaxHp(oldInfo.getSubMHP());
		cs.setMp(oldInfo.getSubMP());
		cs.setMaxMp(oldInfo.getSubMMP());
		Map<Stat, Object> updatedStats = new HashMap<>();
		updatedStats.put(Stat.hp, cs.getHp());
		updatedStats.put(Stat.mhp, cs.getMaxHp());
		updatedStats.put(Stat.mp, cs.getMp());
		updatedStats.put(Stat.mmp, cs.getMaxMp());
		write(WvsContext.statChanged(updatedStats));
//        write(WvsContext.zeroInfo(currentInfo));
	}

	/**
	 * Initializes zero info with HP values.
	 */
	public void initZeroInfo() {
		ZeroInfo zeroInfo = new ZeroInfo();
		CharacterStat cs = getAvatarData().getCharacterStat();
		zeroInfo.setSubHP(cs.getHp());
		zeroInfo.setSubMHP(cs.getMaxHp());
		zeroInfo.setSubMP(cs.getMp());
		zeroInfo.setSubMMP(cs.getMaxMp());
		setZeroInfo(zeroInfo);
	}

	public ScriptManagerImpl getScriptManager() {
		return scriptManagerImpl;
	}

	/**
	 * Adds a {@link Drop} to this Char.
	 *
	 * @param drop
	 * 		The Drop that has been picked up.
	 */
	public boolean addDrop(Drop drop) {
		if (drop.isMoney()) {
			addMoney(drop.getMoney());
			getQuestManager().handleMoneyGain(drop.getMoney());
			write(WvsContext.dropPickupMessage(drop.getMoney(), (short) 0, (short) 0));
			return true;
		} else {
			Item item = drop.getItem();
			int itemID = item.getItemId();
			boolean isConsume = false;
			if (itemID == GameConstants.BLUE_EXP_ORB_ID || itemID == GameConstants.PURPLE_EXP_ORB_ID ||
					itemID == GameConstants.RED_EXP_ORB_ID) {
				long expGain = (long) (drop.getMobExp() * GameConstants.getExpOrbExpModifierById(itemID));

				write(User.effect(Effect.fieldItemConsumed((int) (expGain > Integer.MAX_VALUE ? Integer.MAX_VALUE : expGain))));
				addExpNoMsg(expGain);

				// Exp Orb Buff On Pickup
				TemporaryStatManager tsm = getTemporaryStatManager();
				ItemBuffs.giveItemBuffsFromItemID(this, tsm, itemID);
			}
			if (!ItemConstants.isEquip(itemID)) {
				ItemInfo ii = ItemData.getItemInfoByID(itemID);
				isConsume = ii.getSpecStats().getOrDefault(SpecStat.consumeOnPickup, 0) != 0;
			}
			if (isConsume) {
				consumeItemOnPickup(item);
				dispose();
				return true;
			} else if (getInventoryByType(item.getInvType()).canPickUp(item)) {
				addItemToInventory(item);
				write(WvsContext.dropPickupMessage(item, (short) item.getQuantity()));
				return true;
			} else {
				write(WvsContext.dropPickupMessage(0, (byte) -1, (short) 0, (short) 0, (short) 0));
				return false;
			}
		}
	}

	private void consumeItemOnPickup(Item item) {
		int itemID = item.getItemId();
		if (ItemConstants.isMobCard(itemID)) {
			MonsterBookInfo mbi = getMonsterBookInfo();
			int id = 0;
			if (!mbi.hasCard(itemID)) {
				mbi.addCard(itemID);
				id = itemID;
			}
			write(WvsContext.monsterBookSetCard(id));
		}
	}

	/**
	 * Returns the Char's name.
	 *
	 * @return The Char's name.
	 */
	public String getName() {
		return getAvatarData().getCharacterStat().getName();
	}

	/**
	 * Checks whether or not this Char has a given quest in progress.
	 *
	 * @param questReq
	 * 		The quest ID of the requested quest.
	 *
	 * @return Whether or not this char is in progress with the quest.
	 */
	public boolean hasQuestInProgress(int questReq) {
		return getQuestManager().hasQuestInProgress(questReq);
	}

	/**
	 * Disposes this Char, allowing it to send packets to the server again.
	 */
	public void dispose() {
		write(WvsContext.exclRequest());
	}

	/**
	 * Returns the current HP of this Char.
	 *
	 * @return the current HP of this Char.
	 */
	public int getHP() {
		return getStat(Stat.hp);
	}

	/**
	 * Returns the current MP of this Char.
	 *
	 * @return the current MP of this Char.
	 */
	public int getMP() {
		return getStat(Stat.mp);
	}

	/**
	 * Gets the max hp of this Char. TODO: factor in skills, items, etc...
	 *
	 * @return The max hp of this Char
	 */
	public int getMaxHP() {
		return getStat(Stat.mhp);
	}

	/**
	 * Gets the max mp of this Char. TODO: factor in skills, items, etc...
	 *
	 * @return The max mp of this Char
	 */
	public int getMaxMP() {
		return getStat(Stat.mmp);
	}

	/**
	 * Heals this Char's HP for a certain amount. Caps off at maximum HP.
	 *
	 * @param amount
	 * 		The amount to heal.
	 */
	public void heal(int amount) {
		int curHP = getHP();
		int maxHP = getTotalStat(BaseStat.mhp);
		int newHP = curHP + amount > maxHP ? maxHP : curHP + amount;
		Map<Stat, Object> stats = new HashMap<>();
		setStat(Stat.hp, newHP);
		stats.put(Stat.hp, newHP);
		write(WvsContext.statChanged(stats));
	}

	/**
	 * "Heals" this Char's MP for a certain amount. Caps off at maximum MP.
	 *
	 * @param amount
	 * 		The amount to heal.
	 */
	public void healMP(int amount) {
		int curMP = getMP();
		int maxMP = getTotalStat(BaseStat.mmp);
		int newMP = curMP + amount > maxMP ? maxMP : curMP + amount;
		Map<Stat, Object> stats = new HashMap<>();
		setStat(Stat.mp, newMP);
		stats.put(Stat.mp, newMP);
		write(WvsContext.statChanged(stats));
	}

	/**
	 * Consumes a single {@link Item} from this Char's {@link Inventory}. Will remove the Item if it
	 * has a quantity of 1.
	 *
	 * @param item
	 * 		The Item to consume, which is currently in the Char's inventory.
	 */
	public void consumeItem(Item item) {
		Inventory inventory = getInventoryByType(item.getInvType());
		// data race possible
		if (item.getQuantity() <= 1 && !ItemConstants.isThrowingItem(item.getItemId())) {
			item.setQuantity(0);
			inventory.removeItem(item);
			write(WvsContext.inventoryOperation(true, false,
					REMOVE, (short) item.getBagIndex(), (byte) 0, 0, item));
		} else {
			item.setQuantity(item.getQuantity() - 1);
			write(WvsContext.inventoryOperation(true, false,
					UPDATE_QUANTITY, (short) item.getBagIndex(), (byte) -1, 0, item));
		}
		setBulletIDForAttack(calculateBulletIDForAttack());
	}

	/**
	 * Consumes an item of this Char with the given id. Will do nothing if the Char doesn't have the
	 * Item.
	 * Only works for non-Equip (i.e., type is not EQUIPPED or EQUIP, CASH is fine) items.
	 * Calls {@link #consumeItem(Item)} if an Item is found.
	 *
	 * @param id
	 * 		The Item's id.
	 * @param quantity
	 * 		The amount to consume.
	 */
	public void consumeItem(int id, int quantity) {
		Item checkItem = ItemData.getItemDeepCopy(id);
		Item item = getInventoryByType(checkItem.getInvType()).getItemByItemID(id);
		if (item != null) {
			int consumed = quantity > item.getQuantity() ? 0 : item.getQuantity() - quantity;
			item.setQuantity(consumed + 1); // +1 because 1 gets consumed by consumeItem(item)
			consumeItem(item);
		}
	}

	public boolean hasItem(int itemID) {
		return getInventories().stream().anyMatch(inv -> inv.containsItem(itemID));
	}

	public boolean hasItemCount(int itemID, int count) {
		return getInventories().stream().anyMatch(inv -> {
			Item item = inv.getItemByItemID(itemID);
			return item != null && item.getQuantity() >= count;
		});
	}

	public short getLevel() {
		return getAvatarData().getCharacterStat().getLevel();
	}

	public boolean isMarried() {
		// TODO
		return false;
	}

	public Guild getGuild() {
		return guild;
	}

	public void setGuild(Guild guild) {
		if (guild != null) {
			// to ensure that the same instance of a guild is retrieved for all characters
			this.guild = getClient().getWorld().getGuildByID(guild.getId());
		} else {
			this.guild = null;
		}
	}

	public int getTotalChuc() {
		return getInventoryByType(InvType.EQUIPPED).getItems().stream().mapToInt(i -> ((Equip) i).getChuc()).sum();
	}

	public int getDriverID() {
		return driverID;
	}

	public void setDriverID(int driverID) {
		this.driverID = driverID;
	}

	public int getPassengerID() {
		return passengerID;
	}

	public void setPassengerID(int passengerID) {
		this.passengerID = passengerID;
	}

	public int getChocoCount() {
		return chocoCount;
	}

	public void setChocoCount(int chocoCount) {
		this.chocoCount = chocoCount;
	}

	public int getActiveEffectItemID() {
		return activeEffectItemID;
	}

	public void setActiveEffectItemID(int activeEffectItemID) {
		this.activeEffectItemID = activeEffectItemID;
	}

	public int getMonkeyEffectItemID() {
		return monkeyEffectItemID;
	}

	public void setMonkeyEffectItemID(int monkeyEffectItemID) {
		this.monkeyEffectItemID = monkeyEffectItemID;
	}

	public int getCompletedSetItemID() {
		return completedSetItemID;
	}

	public void setCompletedSetItemID(int completedSetItemID) {
		this.completedSetItemID = completedSetItemID;
	}

	public short getFieldSeatID() {
		return -1;
	}

	public void setFieldSeatID(short fieldSeatID) {
		this.fieldSeatID = fieldSeatID;
	}

	public int getPortableChairID() {
		return portableChairID;
	}

	public void setPortableChairID(int portableChairID) {
		this.portableChairID = portableChairID;
	}

	public String getPortableChairMsg() {
		return portableChairMsg;
	}

	public void setPortableChairMsg(String portableChairMsg) {
		this.portableChairMsg = portableChairMsg;
	}

	public short getFoothold() {
		return foothold;
	}

	public void setFoothold(short foothold) {
		this.foothold = foothold;
	}

	public int getTamingMobLevel() {
		return tamingMobLevel;
	}

	public void setTamingMobLevel(int tamingMobLevel) {
		this.tamingMobLevel = tamingMobLevel;
	}

	public int getTamingMobExp() {
		return tamingMobExp;
	}

	public void setTamingMobExp(int tamingMobExp) {
		this.tamingMobExp = tamingMobExp;
	}

	public int getTamingMobFatigue() {
		return tamingMobFatigue;
	}

	public void setTamingMobFatigue(int tamingMobFatigue) {
		this.tamingMobFatigue = tamingMobFatigue;
	}

	public MiniRoom getMiniRoom() {
		return miniRoom;
	}

	public void setMiniRoom(MiniRoom miniRoom) {
		this.miniRoom = miniRoom;
	}

	public String getADBoardRemoteMsg() {
		return ADBoardRemoteMsg;
	}

	public void setADBoardRemoteMsg(String ADBoardRemoteMsg) {
		this.ADBoardRemoteMsg = ADBoardRemoteMsg;
	}

	public boolean isInCouple() {
		return inCouple;
	}

	public void setInCouple(boolean inCouple) {
		this.inCouple = inCouple;
	}

	public CoupleRecord getCouple() {
		return couple;
	}

	public void setCouple(CoupleRecord couple) {
		this.couple = couple;
	}

	public boolean hasFriendshipItem() {
		return false;
	}

	public FriendshipRingRecord getFriendshipRingRecord() {
		return friendshipRingRecord;
	}

	public void setFriendshipRingRecord(FriendshipRingRecord friendshipRingRecord) {
		this.friendshipRingRecord = friendshipRingRecord;
	}

	public int getComboCounter() {
		return comboCounter;
	}

	public void setComboCounter(int comboCounter) {
		this.comboCounter = comboCounter;
	}

	public int getEvanDragonGlide() {
		return evanDragonGlide;
	}

	public void setEvanDragonGlide(int evanDragonGlide) {
		this.evanDragonGlide = evanDragonGlide;
	}

	public int getKaiserMorphRotateHueExtern() {
		return kaiserMorphRotateHueExtern;
	}

	public void setKaiserMorphRotateHueExtern(int kaiserMorphRotateHueExtern) {
		this.kaiserMorphRotateHueExtern = kaiserMorphRotateHueExtern;
	}

	public int getKaiserMorphPrimiumBlack() {
		return kaiserMorphPrimiumBlack;
	}

	public void setKaiserMorphPrimiumBlack(int kaiserMorphPrimiumBlack) {
		this.kaiserMorphPrimiumBlack = kaiserMorphPrimiumBlack;
	}

	public int getKaiserMorphRotateHueInnner() {
		return kaiserMorphRotateHueInnner;
	}

	public void setKaiserMorphRotateHueInnner(int kaiserMorphRotateHueInnner) {
		this.kaiserMorphRotateHueInnner = kaiserMorphRotateHueInnner;
	}

	public int getMakingMeisterSkillEff() {
		return makingMeisterSkillEff;
	}

	public void setMakingMeisterSkillEff(int makingMeisterSkillEff) {
		this.makingMeisterSkillEff = makingMeisterSkillEff;
	}

	public FarmUserInfo getFarmUserInfo() {
		if (farmUserInfo == null) {
			return new FarmUserInfo();
		}
		return farmUserInfo;
	}

	public void setFarmUserInfo(FarmUserInfo farmUserInfo) {
		this.farmUserInfo = farmUserInfo;
	}

	public int getCustomizeEffect() {
		return customizeEffect;
	}

	public void setCustomizeEffect(int customizeEffect) {
		this.customizeEffect = customizeEffect;
	}

	public String getCustomizeEffectMsg() {
		return customizeEffectMsg;
	}

	public void setCustomizeEffectMsg(String customizeEffectMsg) {
		this.customizeEffectMsg = customizeEffectMsg;
	}

	public byte getSoulEffect() {
		return soulEffect;
	}

	public void setSoulEffect(byte soulEffect) {
		this.soulEffect = soulEffect;
	}

	public FreezeHotEventInfo getFreezeHotEventInfo() {
		if (freezeHotEventInfo == null) {
			return new FreezeHotEventInfo();
		}
		return freezeHotEventInfo;
	}

	public void setFreezeHotEventInfo(FreezeHotEventInfo freezeHotEventInfo) {
		this.freezeHotEventInfo = freezeHotEventInfo;
	}

	public int getEventBestFriendAID() {
		return eventBestFriendAID;
	}

	public void setEventBestFriendAID(int eventBestFriendAID) {
		this.eventBestFriendAID = eventBestFriendAID;
	}

	public int getMesoChairCount() {
		return mesoChairCount;
	}

	public void setMesoChairCount(int mesoChairCount) {
		this.mesoChairCount = mesoChairCount;
	}

	public boolean isBeastFormWingOn() {
		return beastFormWingOn;
	}

	public void setBeastFormWingOn(boolean beastFormWingOn) {
		this.beastFormWingOn = beastFormWingOn;
	}

	public int getActiveNickItemID() {
		return activeNickItemID;
	}

	public void setActiveNickItemID(int activeNickItemID) {
		this.activeNickItemID = activeNickItemID;
	}

	public int getMechanicHue() {
		return mechanicHue;
	}

	public void setMechanicHue(int mechanicHue) {
		this.mechanicHue = mechanicHue;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		if (getGuild() != null) {
			Guild g = getGuild();
			GuildMember gm = g.getMemberByID(getId());
			gm.setOnline(online);
			gm.setChr(online ? this : null);
			getGuild().broadcast(WvsContext.guildResult(
					new GuildUpdateMemberLogin(g.getId(), getId(), online, !this.online && online)), this);
		}
		this.online = online;
		if (getParty() != null) {
			party.updatePartyMemberInfoByChr(this);
		}
	}

	public void setParty(Party party) {
		this.party = party;
	}

	public Party getParty() {
		return party;
	}

	public void logout() {
		log.info("Logging out " + getName());
		ChatHandler.removeClient(getAccId());
		setOnline(false);
		getField().removeChar(this);
		DatabaseManager.saveToDB(this);
		DatabaseManager.saveToDB(getAccount());
		getClient().getChannelInstance().removeChar(this);
	}

	public int getSubJob() {
		return getAvatarData().getCharacterStat().getSubJob();
	}

	public FieldInstanceType getFieldInstanceType() {
		return fieldInstanceType;
	}

	public void setFieldInstanceType(FieldInstanceType fieldInstanceType) {
		this.fieldInstanceType = fieldInstanceType;
	}

	/**
	 * Returns the current Set of Fields that this Char holds as personal instances.
	 *
	 * @return the list of personal Field instances.
	 */
	public Map<Integer, Field> getFields() {
		return fields;
	}

	public void addField(Field field) {
		getFields().put(field.getId(), field);
	}

	public Field getPersonalById(int id) {
		return getFields().get(id);
	}

	public int calculateBulletIDForAttack() {
		Item weapon = getEquippedInventory().getFirstItemByBodyPart(BodyPart.WEAPON);
		if (weapon == null) {
			return 0;
		}
		Predicate<Item> p;
		int id = weapon.getItemId();

		if (ItemConstants.isClaw(id)) {
			p = i -> ItemConstants.isThrowingStar(i.getItemId());
		} else if (ItemConstants.isBow(id)) {
			p = i -> ItemConstants.isBowArrow(i.getItemId());
		} else if (ItemConstants.isXBow(id)) {
			p = i -> ItemConstants.isXBowArrow(i.getItemId());
		} else if (ItemConstants.isGun(id)) {
			p = i -> ItemConstants.isBullet(i.getItemId());
		} else {
			return 0;
		}
		Item i = getConsumeInventory().getItems().stream().filter(p).findFirst().orElse(null);
		return i != null ? i.getItemId() : 0;
	}

	public int getBulletIDForAttack() {
		return bulletIDForAttack;
	}

	public void setBulletIDForAttack(int bulletIDForAttack) {
		this.bulletIDForAttack = bulletIDForAttack;
	}

	public void setShop(NpcShopDlg shop) {
		this.shop = shop;
	}

	public NpcShopDlg getShop() {
		return shop;
	}

	public boolean canHold(int id) {
		boolean canHold;
		if (ItemConstants.isEquip(id)) {  //Equip
			canHold = getEquipInventory().getSlots() > getEquipInventory().getItems().size();
		} else {    //Item
			ItemInfo ii = ItemData.getItemInfoByID(id);
			Inventory inv = getInventoryByType(ii.getInvType());
			Item curItem = inv.getItemByItemID(id);
			canHold = (curItem != null && curItem.getQuantity() + 1 < ii.getSlotMax()) || inv.getSlots() > inv.getItems().size();
		}
		return canHold;
	}

	/**
	 * Returns the set of personal (i.e., non-account) friends of this Char.
	 *
	 * @return The set of personal friends
	 */
	public Set<Friend> getFriends() {
		return friends;
	}

	public void setFriends(Set<Friend> friends) {
		this.friends = friends;
	}

	/**
	 * Returns the total list of friends of this Char + the owning Account's friends.
	 *
	 * @return The total list of friends
	 */
	public Set<Friend> getAllFriends() {
		Set<Friend> res = new HashSet<>(getFriends());
		res.addAll(getAccount().getFriends());
		return res;
	}

	public Friend getFriendByCharID(int charID) {
		return getFriends().stream().filter(f -> f.getFriendID() == charID).findAny().orElse(null);
	}

	public Account getAccount() {
		if (account == null) {
			setAccount(Account.getFromDBById(getAccId()));
		}
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public void removeFriend(Friend friend) {
		if (friend != null) {
			getFriends().remove(friend);
		}
	}

	public void removeFriendByID(int charID) {
		removeFriend(getFriendByCharID(charID));
	}

	public void addFriend(Friend friend) {
		if (getFriendByCharID(friend.getFriendID()) == null) {
			getFriends().add(friend);
		}
	}

	public void setChatClient(Client chatClient) {
		this.chatClient = chatClient;
	}

	public Client getChatClient() {
		return chatClient;
	}

	public List<Macro> getMacros() {
		return macros;
	}

	public void setMacros(List<Macro> macros) {
		this.macros = macros;
	}

	public void encodeDamageSkins(OutPacket outPacket) {
		outPacket.encodeByte(true); // hasDamageSkins. Always true in this design.
		// check ida for structure
		getDamageSkin().encode(outPacket);
		getPremiumDamageSkin().encode(outPacket);
		outPacket.encodeShort(getAccount().getDamageSkins().size() + 30); // slotCount
		outPacket.encodeShort(getAccount().getDamageSkins().size());
		for (DamageSkinSaveData dssd : getAccount().getDamageSkins()) {
			dssd.encode(outPacket);
		}
	}

	public boolean canAddMoney(long reqMoney) {
		return getMoney() + reqMoney > 0 && getMoney() + reqMoney < GameConstants.MAX_MONEY;
	}

	public void addPet(Pet pet) {
		getPets().add(pet);
	}

	public void removePet(Pet pet) {
		getPets().remove(pet);
	}

	public void initPets() {
		for (PetItem pi : getCashInventory().getItems()
				.stream()
				.filter(i -> i instanceof PetItem && ((PetItem) i).getActiveState() > 0)
				.map(i -> (PetItem) i).collect(Collectors.toList())) {
			Pet p = pi.createPet(this);
			addPet(p);
			getField().broadcastPacket(UserLocal.petActivateChange(getId(), p, true, (byte) 0));
		}
	}

	public Pet getPetByIdx(int idx) {
		return getPets().stream()
				.filter(p -> p.getIdx() == idx)
				.findAny()
				.orElse(null);
	}

	public int getFirstPetIdx() {
		int chosenIdx = -1;
		for (int i = 0; i < GameConstants.MAX_PET_AMOUNT; i++) {
			Pet p = getPetByIdx(i);
			if (p == null) {
				chosenIdx = i;
				break;
			}
		}
		return chosenIdx;
	}

	/**
	 * Initializes the equips' enchantment stats.
	 */
	public void initEquips() {
		for (Equip e : getEquippedInventory().getItems().stream().map(e -> (Equip) e).collect(Collectors.toList())) {
			e.recalcEnchantmentStats();
		}
		for (Equip e : getEquipInventory().getItems().stream().map(e -> (Equip) e).collect(Collectors.toList())) {
			e.recalcEnchantmentStats();
		}
	}

	public void initSoulMP() {
		Equip weapon = (Equip) getEquippedItemByBodyPart(WEAPON);
		TemporaryStatManager tsm = getTemporaryStatManager();
		if (weapon != null && weapon.getSoulSocketId() != 0 && !tsm.hasStat(SoulMP)) {
			Option o = new Option();
			o.rOption = ItemConstants.getSoulSkillFromSoulID(weapon.getSoulOptionId());
			o.xOption = ItemConstants.MAX_SOUL_CAPACITY;
			tsm.putCharacterStatValue(SoulMP, o);
			tsm.sendSetStatPacket();
		}
	}

	public MonsterBookInfo getMonsterBookInfo() {
		return monsterBookInfo;
	}

	public void setMonsterBookInfo(MonsterBookInfo monsterBookInfo) {
		this.monsterBookInfo = monsterBookInfo;
	}

	public void setDamageCalc(DamageCalc damageCalc) {
		this.damageCalc = damageCalc;
	}

	public DamageCalc getDamageCalc() {
		return damageCalc;
	}

	/**
	 * Gets the current amount of a given stat the character has. Includes things such as skills, items, etc...
	 * @param mainStat the requested stat
	 * @return the amount of stat
	 */
	public int getTotalStat(BaseStat mainStat) {
		// TODO cache this completely
		int stat = 0;
		// Stat allocated by sp
		stat += mainStat.toStat() == null ? 0 : getStat(mainStat.toStat());
		// Stat gained by passives
		stat += getBaseStats().getOrDefault(mainStat, 0L);
		// Stat gained by buffs
		int ctsStat = getTemporaryStatManager().getBaseStats().getOrDefault(mainStat, 0);
		stat += ctsStat;
		// Stat gained by the stat's corresponding rate value
		if (mainStat.getRateVar() != null) {
			stat += stat * (getTotalStat(mainStat.getRateVar()) / 100D);
		}
		// Stat gained by equips
		for (Item item : getEquippedInventory().getItems()) {
			Equip equip = (Equip) item;
			stat += equip.getBaseStat(mainStat);
		}
		return stat;
	}

	/**
	 * Gets a total list of basic stats that a character has, including skills, items, etc...
	 * @return the total list of basic stats
	 */
	public Map<BaseStat, Integer> getTotalBasicStats() {
		Map<BaseStat, Integer> stats = new HashMap<>();
		for (BaseStat bs : BaseStat.values()) {
			stats.put(bs, getTotalStat(bs));
		}
		return stats;
	}

	/**
	 * Sets whether or not this user has chosen to use up an item to protect their buffs upon next respawn.
	 * @param buffProtector buff protectability
	 */
	public void setBuffProtector(boolean buffProtector) {
		this.buffProtector = buffProtector;
	}

	/**
	 * Returns whether this user has chosen to activate a buff protector for their next respawn.
	 * @return buff protectability
	 */
	public boolean hasBuffProtector() {
		return buffProtector;
	}

	/**
	 * Returns the item the user has for protecting buffs.
	 * @return the Item the user has for prtoecting buffs, or null if there is none.
	 */
	public Item getBuffProtectorItem() {
		int[] buffItems = {5133000, 5133001, 4143000};
		Item item = null;
		for (int id : buffItems) {
			item = getConsumeInventory().getItemByItemID(id);
			if (item == null) {
				item = getCashInventory().getItemByItemID(id);
			}
			if (item != null) {
				// just break when an item was found.
				break;
			}
		}
		return item;
	}

	/**
	 * Resets the combo kill's timer. Interrupts the previous timer if there was one.
	 */
	public void comboKillResetTimer() {
		if(comboKillResetTimer != null && !comboKillResetTimer.isDone()) {
			comboKillResetTimer.cancel(true);
		}
		comboKillResetTimer = EventManager.addEvent(() -> setComboCounter(0), GameConstants.COMBO_KILL_RESET_TIMER, TimeUnit.SECONDS);
	}

	public Map<Integer, Long> getSkillCoolTimes() {
		return skillCoolTimes;
	}

	/**
	 * Checks whether or not a skill is currently on cooldown.
	 * @param skillID the skill's id to check
	 * @return whether or not a skill is currently on cooldown
	 */
	public boolean hasSkillOnCooldown(int skillID) {
		return System.currentTimeMillis() < getSkillCoolTimes().getOrDefault(skillID, 0L);
	}

	/**
	 * Checks if a skill is allowed to be cast, according to its cooltime. If it is allowed, it immediately sets
	 * the cooltime and stores the next moment where the skill is allowed. Skills without cooltime are always allowed.
	 * @param skillID the skill id of the skill to put on cooldown
	 * @return whether or not the skill was allowed
	 */
	public boolean checkAndSetSkillCooltime(int skillID) {
		if (hasSkillOnCooldown(skillID)) {
			return false;
		} else {
			Skill skill = getSkill(skillID);
			if (skill != null && SkillData.getSkillInfoById(skillID).hasCooltime()) {
				setSkillCooldown(skillID, (byte) skill.getCurrentLevel());
			}
			return true;
		}
	}

	/**
	 * Sets a skill's cooltime according to their property in the WZ files, and stores the moment where the skill
	 * comes off of cooldown.
	 * @param skillID the skill's id to set
	 * @param slv the current skill level
	 */
	public void setSkillCooldown(int skillID, byte slv) {
		SkillInfo si = SkillData.getSkillInfoById(skillID);
		if (si != null) {
			int cdInSec = si.getValue(SkillStat.cooltime, slv);
			int cdInMillis = cdInSec > 0 ? cdInSec * 1000 : si.getValue(SkillStat.cooltimeMS, slv);
			getSkillCoolTimes().put(skillID, System.currentTimeMillis() + cdInMillis);
			if(!hasSkillCDBypass()) {
				write(UserLocal.skillCooltimeSetM(skillID, cdInMillis));
			}
		}
	}

	public CharacterPotentialMan getPotentialMan() {
		return potentialMan;
	}

	public Set<CharacterPotential> getPotentials() {
		return potentials;
	}

	public void setPotentials(Set<CharacterPotential> potentials) {
		this.potentials = potentials;
	}

	public int getHonorExp() {
		return getAvatarData().getCharacterStat().getHonorExp();
	}

	public void setHonorExp(int honorExp) {
		getAvatarData().getCharacterStat().setHonorExp(honorExp);
	}

	/**
	 * Adds honor exp to this Char, and sends a packet to the client with the new honor exp.
	 * Honor exp added may be negative, but the total honor exp will never go below 0.
	 * @param exp the exp to add (may be negative)
	 */
	public void addHonorExp(int exp) {
		setHonorExp(Math.max(0, getHonorExp() + exp));
		write(WvsContext.characterHonorExp(getHonorExp()));
	}

	public int getDeathCount() {
		return deathCount;
	}

	public void setDeathCount(int deathCount) {
		this.deathCount = deathCount;
	}

	public Set<LinkSkill> getLinkSkills() {
		return getAccount().getLinkSkills().stream()
				.filter(ls -> ls.getOwnerID() != getId())
				.collect(Collectors.toSet());
	}

	/**
	 * Adds a skill to this Char. If the Char already has this skill, just changes the levels.
	 * @param skillID the skill's id to add
	 * @param currentLevel the current level of the skill
	 * @param masterLevel the master level of the skill
	 */
	public void addSkill(int skillID, int currentLevel, int masterLevel) {
		Skill skill = SkillData.getSkillDeepCopyById(skillID);
		if (skill == null) {
			log.error("No such skill found.");
			return;
		}
		skill.setCurrentLevel(currentLevel);
		skill.setMasterLevel(masterLevel);
		List<Skill> list = new ArrayList<>();
		list.add(skill);
		addSkill(skill);
		getClient().write(WvsContext.changeSkillRecordResult(list, true, false, false, false));
	}

	public long getRuneCooldown() {
		return runeStoneCooldown;
	}

	public void setRuneCooldown(long runeCooldown) {
		this.runeStoneCooldown = runeCooldown;
	}

	public MemorialCubeInfo getMemorialCubeInfo() {
		return memorialCubeInfo;
	}

	public void setMemorialCubeInfo(MemorialCubeInfo memorialCubeInfo) {
		this.memorialCubeInfo = memorialCubeInfo;
	}

	public Set<Familiar> getFamiliars() {
		return familiars;
	}

	public void setFamiliars(Set<Familiar> familiars) {
		this.familiars = familiars;
	}

	public boolean hasFamiliar(int familiarID) {
		return getFamiliars().stream().anyMatch(f -> f.getFamiliarID() == familiarID);
	}

	public Familiar getFamiliarByID(int familiarID) {
		return getFamiliars().stream().filter(f -> f.getFamiliarID() == familiarID).findAny().orElse(null);
	}

	public void addFamiliar(Familiar familiar) {
		getFamiliars().add(familiar);
	}

	public void removeFamiliarByID(int familiarID) {
		removeFamiliar(getFamiliarByID(familiarID));
	}

	public void removeFamiliar(Familiar familiar) {
		if (familiar != null) {
			getFamiliars().remove(familiar);
		}
	}

	public void setActiveFamiliar(Familiar activeFamiliar) {
		this.activeFamiliar = activeFamiliar;
	}

	public Familiar getActiveFamiliar() {
		return activeFamiliar;
	}

	public boolean hasSkillCDBypass() {
		return skillCDBypass;
	}

	public void setSkillCDBypass(boolean skillCDBypass) {
		this.skillCDBypass = skillCDBypass;
	}


	public Set<StolenSkill> getStolenSkills() {
		return stolenSkills;
	}

	public void setStolenSkills(Set<StolenSkill> stolenSkills) {
		this.stolenSkills = stolenSkills;
	}

	public void addStolenSkill(StolenSkill stolenSkill) {
		getStolenSkills().add(stolenSkill);
	}

	public void removeStolenSkill(StolenSkill stolenSkill) {
		if(stolenSkill != null) {
			getStolenSkills().remove(stolenSkill);
		}
	}

	public StolenSkill getStolenSkillByPosition(int position) {
		return getStolenSkills().stream().filter(ss -> ss.getPosition() == position).findAny().orElse(null);
	}

	public StolenSkill getStolenSkillBySkillId(int skillId) {
		return getStolenSkills().stream().filter(ss -> ss.getSkillid() == skillId).findAny().orElse(null);
	}


	public Set<ChosenSkill> getChosenSkills() {
		return chosenSkills;
	}

	public void setChosenSkills(Set<ChosenSkill> chosenSkills) {
		this.chosenSkills = chosenSkills;
	}

	public void addChosenSkill(ChosenSkill chosenSkill) {
		getChosenSkills().add(chosenSkill);
	}

	public void removeChosenSkill(ChosenSkill chosenSkill) {
		if(chosenSkill != null) {
			getChosenSkills().remove(chosenSkill);
		}
	}

	public ChosenSkill getChosenSkillByPosition(int position) {
		return getChosenSkills().stream().filter(ss -> ss.getPosition() == position).findAny().orElse(null);
	}

	public boolean isChosenSkillInStolenSkillList(int skillId) {
		return getStolenSkills().stream().filter(ss -> ss.getSkillid() == skillId).findAny().orElse(null) != null;
	}

	public Map<BaseStat, Long> getBaseStats() {
		return baseStats;
	}

	/**
	 * Adds a BaseStat's amount to this Char's BaseStat cache.
	 * @param bs The BaseStat
	 * @param amount the amount of BaseStat to add
	 */
	public void addBaseStat(BaseStat bs, int amount) {
		getBaseStats().put(bs, getBaseStats().getOrDefault(bs, 0L) + amount);
	}

	/**
	 * Removes a BaseStat's amount from this Char's BaseStat cache.
	 * @param bs The BaseStat
	 * @param amount the amount of BaseStat to remove
	 */
	public void removeBaseStat(BaseStat bs, int amount) {
		addBaseStat(bs, -amount);
	}
}

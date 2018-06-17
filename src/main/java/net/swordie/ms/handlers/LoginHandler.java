package net.swordie.ms.handlers;

import net.swordie.ms.client.Account;
import net.swordie.ms.client.Client;
import net.swordie.ms.client.character.Char;
import net.swordie.ms.client.character.CharacterStat;
import net.swordie.ms.client.character.avatar.AvatarLook;
import net.swordie.ms.client.character.keys.FuncKeyMap;
import net.swordie.ms.client.character.items.BodyPart;
import net.swordie.ms.client.character.items.Equip;
import net.swordie.ms.client.character.skills.temp.CharacterTemporaryStat;
import net.swordie.ms.client.jobs.JobManager;
import net.swordie.ms.connection.InPacket;
import net.swordie.ms.constants.ItemConstants;
import net.swordie.ms.constants.JobConstants;
import net.swordie.ms.ServerConstants;
import net.swordie.ms.enums.CharNameResult;
import net.swordie.ms.enums.LoginType;
import net.swordie.ms.handlers.header.OutHeader;
import net.swordie.ms.loaders.ItemData;
import net.swordie.ms.connection.db.DatabaseManager;
import org.apache.log4j.LogManager;
import net.swordie.ms.connection.packet.Login;
import net.swordie.ms.world.Channel;
import net.swordie.ms.Server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static net.swordie.ms.enums.InvType.EQUIPPED;

/**
 * Created on 4/28/2017.
 */
public class LoginHandler {

    private static final org.apache.log4j.Logger log = LogManager.getRootLogger();
    private static int id;

    public static void handleConnect(Client client, InPacket inPacket) {
        byte locale = inPacket.decodeByte();
        short version = inPacket.decodeShort();
        String minorVersion = inPacket.decodeString(1);
        if (locale != ServerConstants.LOCALE || version != ServerConstants.VERSION) {
            log.info(String.format("Client %s has an incorrect version.", client.getIP()));
            client.close();
        }
    }

    public static void handleAuthServer(Client client, InPacket inPacket) {
        client.write(Login.sendAuthServer(false));
    }

    public static void handleClientStart(Client client, InPacket inPacket) {
        client.write(Login.sendStart());
    }

    public static void handlePong(Client c, InPacket inPacket) {

    }

    public static void handleLoginPassword(Client c, InPacket inPacket) {
        Connection connection = Server.getInstance().getDatabaseConnection();
        byte sid = inPacket.decodeByte();
        String password = inPacket.decodeString();
        String username = inPacket.decodeString();
        long mac = inPacket.decodeLong();
        int gameRoomClient = inPacket.decodeInt();
        byte idk = inPacket.decodeByte();
        int channel = inPacket.decodeInt();
        boolean success = true;
        byte result;
        Account account = null;

        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM accounts WHERE username = ?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                success = password.equals(rs.getString("password"));
                int id = rs.getInt("id");
                result = success ? LoginType.SUCCESS.getValue() : LoginType.INVALID_PASSWORD.getValue();
                if (success) {
                    account = Account.getFromDBById(id);
                    Server.getInstance().getAccounts().add(account);
                    c.setAccount(account);
                }
            } else {
                result = LoginType.NOT_A_REGISTERED_ID.getValue();
                success = false;
            }
        } catch (SQLException e) {
            result = LoginType.HAVING_TROUBLE.getValue();
            e.printStackTrace();
        }

        c.write(Login.checkPasswordResult(success, result, account));
    }

    public static void handleWorldRequest(Client c, InPacket packet) {
        c.write(Login.sendWorldInformation());
        c.write(Login.sendWorldInformationEnd());
    }

    public static void handleServerStatusRequest(Client c, InPacket inPacket) {
        c.write(Login.sendWorldInformation());
        c.write(Login.sendWorldInformationEnd());
    }

    public static void handleWorldChannelsRequest(Client c, InPacket inPacket) {
        byte worldId = inPacket.decodeByte();
        c.write(Login.sendServerStatus(worldId));
    }

    public static void handleCharListRequest(Client c, InPacket inPacket) {
        byte somethingThatIsTwo = inPacket.decodeByte();
        byte worldId = inPacket.decodeByte();
        byte channel = (byte) (inPacket.decodeByte() + 1);
        byte code = 0; // success code

        c.setWorldId(worldId);
        c.setChannel(channel);
//        c.write(Login.sendAccountInfo(c.getAccount()));
        c.write(Login.sendCharacterList(c.getAccount(), worldId, channel, code));
    }

    public static void handleCheckCharName(Client c, InPacket inPacket) {
        String name = inPacket.decodeString();
        CharNameResult code = CharNameResult.OK;
        if (name.toLowerCase().contains("virtual") || name.toLowerCase().contains("kernel")) {
            code = CharNameResult.INVALID_NAME;
        } else {
            Connection connection = Server.getInstance().getDatabaseConnection();
            PreparedStatement ps = null;
            try {
                ps = connection.prepareStatement("SELECT * FROM characterstats WHERE name = ?");
                ps.setString(1, name);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    code = CharNameResult.ALREADY_IN_USE;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        c.write(Login.checkDuplicatedIDResult(name, code.getVal()));
    }

    public static void handleCreateChar(Client c, InPacket inPacket) {
        String name = inPacket.decodeString();
        int keySettingType = inPacket.decodeInt();
        int eventNewCharSaleJob = inPacket.decodeInt();
        int curSelectedRace = inPacket.decodeInt();
        JobConstants.JobEnum job = JobConstants.LoginJob.getLoginJobById(curSelectedRace).getBeginJob();
        short curSelectedSubJob = inPacket.decodeShort();
        byte gender = inPacket.decodeByte();
        byte skin = inPacket.decodeByte();

        byte itemLength = inPacket.decodeByte();
        int[] items = new int[itemLength]; //face, hair, markings, skin, overall, top, bottom, cape, boots, weapon
        for (int i = 0; i < itemLength; i++) {
            items[i] = inPacket.decodeInt();
        }

        Char chr = new Char(c.getAccount().getId(), name, keySettingType, eventNewCharSaleJob, job.getJobId(),
                curSelectedSubJob, gender, skin, items);
        // Start job specific handling ----------------------------------------------------------------
        JobManager.getJobById(job.getJobId(), chr).setCharCreationStats(chr);
        // End job specific handling ------------------------------------------------------------------

        chr.setFuncKeyMap(FuncKeyMap.getDefaultMapping());
        c.getAccount().addCharacter(chr);
        DatabaseManager.saveToDB(c.getAccount());

        CharacterStat cs = chr.getAvatarData().getCharacterStat();
        cs.setCharacterId(chr.getId());
        cs.setCharacterIdForLog(chr.getId());
        cs.setPosMap(100000000);
        for (int i : chr.getAvatarData().getAvatarLook().getHairEquips()) {
            Equip equip = ItemData.getEquipDeepCopyFromID(i, false);
            if (equip != null && equip.getItemId() >= 1000000) {
                equip.setBagIndex(ItemConstants.getBodyPartFromItem(
                        equip.getItemId(), chr.getAvatarData().getAvatarLook().getGender()));
                chr.addItemToInventory(EQUIPPED, equip, true);
            }
        }
        Equip codex = ItemData.getEquipDeepCopyFromID(1172000, false);
        codex.setInvType(EQUIPPED);
        codex.setBagIndex(BodyPart.BOOK.getVal());
        chr.addItemToInventory(EQUIPPED, codex, true);
        if(curSelectedRace == 15) { // Zero hack for adding 2nd weapon (removing it in hairequips for zero look)
            Equip equip = ItemData.getEquipDeepCopyFromID(1562000, false);
            equip.setBagIndex(ItemConstants.getBodyPartFromItem(
                    equip.getItemId(), chr.getAvatarData().getAvatarLook().getGender()));
            chr.addItemToInventory(EQUIPPED, equip, true);
        }
        DatabaseManager.saveToDB(chr);
        c.write(Login.createNewCharacterResult(LoginType.SUCCESS, chr));
    }

    public static void handleDeleteChar(Client c, InPacket inPacket) {
        if (handleAuthSecondPassword(c, inPacket)) {
            int charId = inPacket.decodeInt();
            Char chr = Char.getFromDBById(charId);
            Account a = Account.getFromDBById(c.getAccount().getId());
            a.removeLinkSkillByOwnerID(chr.getId());
            a.getCharacters().remove(chr);
            DatabaseManager.saveToDB(a);
            DatabaseManager.deleteFromDB(chr);
            c.write(Login.sendDeleteCharacterResult(charId, LoginType.SUCCESS));
        }
    }

    public static void handleClientError(Client c, InPacket inPacket) {
        c.close();
        if (inPacket.getData().length < 8) {
            log.error(String.format("Error: %s", inPacket));
            return;
        }
        short type = inPacket.decodeShort();
        String type_str = "Unknown?!";
        if (type == 0x01) {
            type_str = "SendBackupPacket";
        } else if (type == 0x02) {
            type_str = "Crash Report";
        } else if (type == 0x03) {
            type_str = "Exception";
        }
        int errortype = inPacket.decodeInt();
        short data_length = inPacket.decodeShort();

        int idk = inPacket.decodeInt();

        short op = inPacket.decodeShort();

        OutHeader opcode = OutHeader.getOutHeaderByOp(op);
        log.error(String.format("[Error %s] (%s / %d) Data: %s", errortype, opcode, op, inPacket));
        if(opcode == OutHeader.TEMPORARY_STAT_SET) {
            for (int i = 0; i < CharacterTemporaryStat.length; i++) {
                int mask = inPacket.decodeInt();
                for(CharacterTemporaryStat cts : CharacterTemporaryStat.values()) {
                    if(cts.getPos() == i && (cts.getVal() & mask) != 0) {
                        log.error(String.format("[Error %s] Contained stat %s", errortype, cts.toString()));
                    }
                }
            }
        }
    }

    public static int getId() {
        return id;
    }

    public static void handleHeartbeatRequest(Client c, InPacket inPacket) {
        c.write(Login.sendAuthResponse(((int) OutHeader.HEARTBEAT_RESPONSE.getValue()) ^ inPacket.decodeInt()));
    }

    public static void handleCharSelectNoPic(Client c, InPacket inPacket) {
        inPacket.decodeBytes(2);
        int characterId = inPacket.decodeInt();
        String mac = inPacket.decodeString();
        String somethingElse = inPacket.decodeString();
        String pic = inPacket.decodeString();
        c.getAccount().setPic(pic);
        // Update in DB
        DatabaseManager.saveToDB(c.getAccount());
        byte worldId = c.getWorldId();
        byte channelId = c.getChannel();
        Channel channel = Server.getInstance().getWorldById(worldId).getChannelById(channelId);
        c.write(Login.selectCharacterResult(LoginType.SUCCESS, (byte) 0, channel.getPort(), characterId));
    }

    public static void handleCharSelect(Client c, InPacket inPacket) {
        int characterId = inPacket.decodeInt();
        String name = inPacket.decodeString();
        byte worldId = c.getWorldId();
        byte channelId = c.getChannel();
        Channel channel = Server.getInstance().getWorldById(worldId).getChannelById(channelId);
        if (c.isAuthorized()) {
            Server.getInstance().getWorldById(worldId).getChannelById(channelId).addClientInTransfer(channelId, characterId, c);
            c.write(Login.selectCharacterResult(LoginType.SUCCESS, (byte) 0, channel.getPort(), characterId));
        }
    }

    public static boolean handleAuthSecondPassword(Client c, InPacket inPacket) {
        boolean success = false;
        String pic = inPacket.decodeString();
//        int userId = inPacket.decodeInt();
        // after this: 2 strings indicating pc info. Not interested in that rn
        if (c.getAccount().getPic().equals(pic)) {
            success = true;
        } else {
            c.write(Login.selectCharacterResult(LoginType.INVALID_PASSWORD, (byte) 0, 0, 0));
        }
        c.setAuthorized(success);
        return success;
    }
}

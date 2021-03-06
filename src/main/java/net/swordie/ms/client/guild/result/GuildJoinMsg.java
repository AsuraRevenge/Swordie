package net.swordie.ms.client.guild.result;

import net.swordie.ms.client.guild.GuildMember;
import net.swordie.ms.connection.OutPacket;

/**
 * Created on 3/23/2018.
 */
public class GuildJoinMsg implements GuildResultInfo {

    private int guildID;
    private GuildMember gm;

    public GuildJoinMsg(int guildID, GuildMember gm) {
        this.guildID = guildID;
        this.gm = gm;
    }

    @Override
    public GuildResultType getType() {
        return GuildResultType.JoinMsg;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeInt(guildID);
        outPacket.encodeInt(gm.getId());
        gm.encode(outPacket);
    }
}

package net.swordie.ms.client.guild.result;

import net.swordie.ms.connection.OutPacket;

/**
 * Created on 3/21/2018.
 */
public interface GuildResultInfo {

    GuildResultType getType();

    void encode(OutPacket outPacket);
}

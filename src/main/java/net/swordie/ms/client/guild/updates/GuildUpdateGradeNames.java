package net.swordie.ms.client.guild.updates;

import net.swordie.ms.client.guild.result.GuildResultInfo;
import net.swordie.ms.connection.OutPacket;
import net.swordie.ms.client.guild.result.GuildResultType;

/**
 * Created on 3/22/2018.
 */
public class GuildUpdateGradeNames implements GuildResultInfo {

    private int id;
    private String[] newNames;

    public GuildUpdateGradeNames(int id, String[] newNames) {
        this.id = id;
        this.newNames = newNames;
    }

    @Override
    public GuildResultType getType() {
        return GuildResultType.UpdateGradeNames;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeInt(id);
        for(String name : newNames) {
            outPacket.encodeString(name);
        }
    }
}

package net.swordie.ms.client.guild;

import net.swordie.ms.client.character.Char;
import net.swordie.ms.connection.OutPacket;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created on 3/18/2018.
 */
@Entity
@Table(name = "guilds")
public class Guild {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private int leaderID;
    private int worldID;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @CollectionTable(name = "guildrequestors", joinColumns = @JoinColumn(name = "guildID"))
    private List<GuildRequestor> requestors = new ArrayList<>();
    @ElementCollection
    @CollectionTable(name = "gradenames", joinColumns = @JoinColumn(name = "guildID"))
    @Column(name = "gradeName")
    private List<String> gradeNames = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "guildID")
    private List<GuildMember> members = new ArrayList<>();
    private int markBg;
    private int markBgColor;
    private int mark;
    private int markColor;
    private int maxMembers;
    private String notice;
    private int points;
    private int seasonPoints;
    private int allianceID;
    private int level;
    private int rank;
    private int ggp;
    // Start GUILDSETTING struct
    private boolean appliable;
    private int joinSetting;
    private int reqLevel;
    // End GUILDSETTING struct

    @ElementCollection
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @CollectionTable(name = "guildskills")
    @MapKeyColumn(name = "skillID")
    private Map<Integer, GuildSkill> skills = new HashMap<>();

    public Guild() {
        setGradeNames(new String[]{"Guild Master", "Junior", "Veteran", "Member", "Newbie"});
        setAppliable(true);
        setMaxMembers(10);
        setName("Default guild");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void encodeForRemote(OutPacket outPacket) {
        outPacket.encodeString(getName());
        outPacket.encodeShort(getMarkBg());
        outPacket.encodeByte(getMarkBgColor());
        outPacket.encodeShort(getMark());
        outPacket.encodeByte(getMarkColor());
    }

    public void encode(OutPacket outPacket) {
        outPacket.encodeInt(getId());
        outPacket.encodeString(getName());
        for(String str : getGradeNames()) {
            outPacket.encodeString(str); // 5 times total
        }
        outPacket.encodeShort(getMembers().size());
        getMembers().forEach(gm -> outPacket.encodeInt(gm.getCharID()));
        getMembers().forEach(gm -> gm.encode(outPacket));
        outPacket.encodeShort(getRequestors().size());
        getRequestors().forEach(gm -> outPacket.encodeInt(gm.getCharID()));
        getRequestors().forEach(gm -> gm.encode(outPacket));
        outPacket.encodeInt(getMaxMembers());
        outPacket.encodeShort(getMarkBg());
        outPacket.encodeByte(getMarkBgColor());
        outPacket.encodeShort(getMark());
        outPacket.encodeByte(getMarkColor());
        outPacket.encodeString(getNotice());
        outPacket.encodeInt(getPoints());
        outPacket.encodeInt(getSeasonPoints());
        outPacket.encodeByte(getLevel());
        outPacket.encodeShort(getRank());
        outPacket.encodeInt(getGgp());
        outPacket.encodeShort(getSkills().size());
        getSkills().forEach((id, skill) -> {
            outPacket.encodeInt(id);
            skill.encode(outPacket);
        });
        outPacket.encodeByte(isAppliable());
        if(isAppliable()) {
            outPacket.encodeByte(getJoinSetting());
            outPacket.encodeInt(getReqLevel());
        }
    }

    public List<GuildMember> getMembers() {
        return members;
    }

    public void addMember(GuildMember guildMember) {
        getMembers().add(guildMember);
        if(guildMember.getChr() != null && guildMember.getChr().getGuild() == null) {
            guildMember.getChr().setGuild(this);
        }
        if(getLeaderID() == 0) {
            setLeader(guildMember);
        } else {
            guildMember.setGrade(getGradeNames().size());
        }
    }

    public void demote(GuildMember guildMember) {
        guildMember.setGrade(Math.min(guildMember.getGrade() + 1, getGradeNames().size()));
    }

    public void promote(GuildMember guildMember) {
        guildMember.setGrade(Math.max(guildMember.getGrade() - 1, 1));
    }

    public void addMember(Char chr) {
        addMember(new GuildMember(chr));
    }

    public void removeMember(GuildMember guildMember) {
        if(guildMember.getChr() != null) {
            guildMember.getChr().setGuild(null);
        }
        getMembers().remove(guildMember);
    }

    public void removeMember(Char chr) {
        getMembers().remove(getMemberByChar(chr));
    }

    public GuildMember getMemberByChar(Char chr) {
        return getMembers().stream().filter(gm -> gm.getChr().equals(chr)).findAny().orElse(null);
    }

    public int getLeaderID() {
        return leaderID;
    }

    public void setLeaderID(int leaderID) {
        this.leaderID = leaderID;
    }

    public void setLeader(GuildMember leader) {
        int oldGrade = leader.getGrade();
        if(getLeaderID() != 0) {
            getMemberByID(getLeaderID()).setGrade(oldGrade);
        }
        this.leaderID = leader.getCharID();
        leader.setGrade(1);
    }

    public boolean isGuildMember(GuildMember gm) {
        return getLeaderID() == gm.getCharID();
    }

    public GuildMember getMemberByID(int id) {
        return getMembers().stream().filter(gm -> gm.getCharID() == id).findAny().orElse(null);
    }

    public int getMarkBg() {
        return markBg;
    }

    public void setMarkBg(int markBg) {
        this.markBg = markBg;
    }

    public int getMarkBgColor() {
        return markBgColor;
    }

    public void setMarkBgColor(int markBgColor) {
        this.markBgColor = markBgColor;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public int getMarkColor() {
        return markColor;
    }

    public void setMarkColor(int markColor) {
        this.markColor = markColor;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<GuildMember> getOnlineMembers() {
        return getMembers().stream().filter(GuildMember::isOnline).collect(Collectors.toList());
    }

    public void broadcast(OutPacket outPacket) {
        getOnlineMembers().stream().filter(gm -> gm.getChr() != null).forEach(gm -> gm.getChr().write(outPacket));
    }

    public void broadcast(OutPacket outPacket, Char exceptChr) {
        getOnlineMembers().stream().filter(gm -> !gm.getChr().equals(exceptChr)).forEach(gm -> gm.getChr().write(outPacket));
    }

    public List<GuildRequestor> getRequestors() {
        return requestors;
    }

    public void setRequestors(List<GuildRequestor> requestors) {
        this.requestors = requestors;
    }

    public List<String> getGradeNames() {
        return gradeNames;
    }

    public void setGradeNames(List<String> gradeNames) {
        this.gradeNames = gradeNames;
    }

    public void setGradeNames(String[] gradeNames) {
        for (String gradeName : gradeNames) {
            getGradeNames().add(gradeName);
        }
    }

    public void setMembers(List<GuildMember> members) {
        this.members = members;
    }

    public int getMaxMembers() {
        return maxMembers;
    }

    public void setMaxMembers(int maxMembers) {
        this.maxMembers = maxMembers;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getSeasonPoints() {
        return seasonPoints;
    }

    public void setSeasonPoints(int seasonPoints) {
        this.seasonPoints = seasonPoints;
    }

    public int getAllianceID() {
        return allianceID;
    }

    public void setAllianceID(int allianceID) {
        this.allianceID = allianceID;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getGgp() {
        return ggp;
    }

    public void setGgp(int ggp) {
        this.ggp = ggp;
    }

    public boolean isAppliable() {
        return appliable;
    }

    public void setAppliable(boolean setState) {
        this.appliable = setState;
    }

    public int getJoinSetting() {
        return joinSetting;
    }

    public void setJoinSetting(int joinSetting) {
        this.joinSetting = joinSetting;
    }

    public int getReqLevel() {
        return reqLevel;
    }

    public void setReqLevel(int reqLevel) {
        this.reqLevel = reqLevel;
    }

    public Map<Integer, GuildSkill> getSkills() {
        return skills;
    }

    public void setSkills(Map<Integer, GuildSkill> skills) {
        this.skills = skills;
    }

    public void addGuildSkill(GuildSkill gs) {
        getSkills().put(gs.getSkillID(), gs);
    }

    public int getWorldID() {
        return worldID;
    }

    public void setWorldID(int worldID) {
        this.worldID = worldID;
    }

    public static void defaultEncodeForRemote(OutPacket outPacket) {
        outPacket.encodeString("");
        outPacket.encodeShort(0);
        outPacket.encodeByte(0);
        outPacket.encodeShort(0);
        outPacket.encodeByte(0);
    }
}

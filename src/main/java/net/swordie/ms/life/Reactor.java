package net.swordie.ms.life;

import net.swordie.ms.client.character.Char;
import net.swordie.ms.loaders.ReactorInfo;
import net.swordie.ms.loaders.ReactorData;
import net.swordie.ms.connection.packet.ReactorPool;
import net.swordie.ms.life.Life;

/**
 * Created on 4/21/2018.
 */
public class Reactor extends Life {

    private byte state;
    private boolean flip;
    private String name = "";
    private int ownerID;
    private int properEventIdx;
    private int reactorTime;
    private boolean phantomForest;

    public Reactor(int objectId) {
        super(objectId);
    }

    public byte getState() {
        return state;
    }

    public void setState(byte state) {
        this.state = state;
    }

    public boolean isFlip() {
        return flip;
    }

    public void setFlip(boolean flip) {
        this.flip = flip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(int ownerID) {
        this.ownerID = ownerID;
    }

    public int getProperEventIdx() {
        return properEventIdx;
    }

    public void setProperEventIdx(int properEventIdx) {
        this.properEventIdx = properEventIdx;
    }

    public void setReactorTime(int reactorTime) {
        this.reactorTime = reactorTime;
    }

    public int getReactorTime() {
        return reactorTime;
    }

    public void setPhantomForest(boolean phantomForest) {
        this.phantomForest = phantomForest;
    }

    public boolean isPhantomForest() {
        return phantomForest;
    }

    public void init() {
        ReactorInfo ri = ReactorData.getReactorByID(getTemplateId());
        setState((byte) 0);
        setName(ri.getViewName());
        setPosition(getHomePosition());
    }

    @Override
    public void broadcastSpawnPacket(Char onlyChar) {
        init();
        getField().broadcastPacket(ReactorPool.reactorEnterField(this));
    }
}

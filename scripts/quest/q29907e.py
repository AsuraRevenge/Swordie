# (Lv30) Official Knight

officialKnightMedal = 1142067

def init():
    if sm.canHold(officialKnightMedal):
        sm.setSpeakerID(1101000)
        sm.sendNext("You are training well! However, you have a lot to learn still. Take this to remember the cause and what it means to be a Knight of Cygnus"
                    "\r\n\r\n1x #v"+ str(officialKnightMedal) +"##z"+ str(officialKnightMedal) +"#")
        sm.giveItem(officialKnightMedal)
        sm.completeQuestNoRewards(parentID)
        sm.dispose()
    else:
        sm.dispose()
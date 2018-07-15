# [Riena Strait] Get it Strait

mapid = 140000000

def init():
    sm.setSpeakerID(1105012)
    sm.sendAskYesNo("Are you #b#h0##k?\r\n"
                    "We need your help! We have noticed weird changes going on around the Rien island\r\nAre you able to help?\r\n\r\n"
                    "(accepting will warp you)")

def action(response, answer):
    if response == 1:
        sm.completeQuestNoRewards(parentID)
        sm.warp(mapid, 0)
    sm.dispose()
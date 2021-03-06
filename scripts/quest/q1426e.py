#   [Job Adv] (Lv.30)   Gunslinger of the Seven Seas

darkMarble = 4031013
job = "Gunslinger"

status = -1
def init():
    if sm.hasItem(darkMarble, 30):
        sm.sendNext("I am impressed, you surpassed the test. Only few are talented enough.\r\n"
                    "You have proven yourself to be worthy, I shall mold your body into a #b"+ job +"#k.")
    else:
        sm.sendSayOkay("You have not retrieved the #t"+ darkMarble+"#s yet, I will be waiting.")
        sm.dispose()

def action(response, answer):
    global status
    status += 1

    if status == 0:
        sm.consumeItem(darkMarble, 30)
        sm.completeQuestNoRewards(parentID)
        sm.sendNext("You are now a #b"+ job +"#k.")
        sm.jobAdvance(520) #Gunslinger
        sm.completeQuestNoRewards(1444) #Chiefs of El Nath (Pirate)
        sm.dispose()
#   [Job Adv] (Lv.60)   Way of the Marauder

blackCharm = 4031059
job = "Marauder"

status = -1
def init():
    if sm.hasItem(blackCharm, 1):
        sm.sendNext("I am impressed, you surpassed the test. Only few are talented enough.\r\n"
                    "You have proven yourself to be worthy, thus I shall mold your body into a #b"+ job +"#k.")
    else:
        sm.sendSayOkay("You have not retrieved the #t"+ blackCharm +"# yet, I will be waiting.")
        sm.dispose()

def action(response, answer):
    global status
    status += 1

    if status == 0:
        sm.consumeItem(blackCharm, 1)
        sm.completeQuestNoRewards(parentID)
        sm.sendSayOkay("You are now a #b"+ job +"#k.")
        sm.jobAdvance(511) #Marauder
        sm.startQuestNoCheck(1458) #Pirate 4th Job Quest
        sm.dispose()
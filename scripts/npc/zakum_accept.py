# Adobis - Entrance To (Easy/Chaos) Zakum's Altar
fields = {
    211042402 : 280030200, # Easy
    211042400 : 280030100, # Normal
    211042401 : 280030000 # Chaos
}
def init():
    sm.sendAskYesNo("Are you sure you want to go in?")

def action(response, answer):

    if response == 1:
        if sm.getParty() is None:
            sm.sendSayOkay("Please create a party before going in.")
        elif not sm.isPartyLeader():
            sm.sendSayOkay("Please have your party leader talk to me if you wish to face Zakum.")
        elif sm.checkParty():
            sm.setPartyDeathCount(20)
            sm.warpPartyIn(fields[sm.getFieldID()])
    sm.dispose()
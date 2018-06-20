package net.swordie.ms.client.party;

import net.swordie.ms.client.character.Char;
import net.swordie.ms.client.character.info.ExpIncreaseInfo;
import net.swordie.ms.constants.GameConstants;
import net.swordie.ms.life.mob.Mob;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Helper class for calculating party damage/exp distribution
 *
 * @author Sjonnie
 * Created on 6/20/2018.
 */
public class PartyDamageInfo {

    private final Party party;
    private Map<Char, Double> damageDone = new HashMap<>();
    private final Mob mob;

    public PartyDamageInfo(Party party, Mob mob) {
        this.party = party;
        this.mob = mob;
    }

    /**
     * Adds a char's damage info for a mob kill
     * @param chr the Char itself
     * @param percDamage the percentage damage done compared to the total mob's health
     */
    public void addDamageInfo(Char chr, double percDamage) {
        damageDone.put(chr, percDamage);
    }

    /**
     * Distributes the exp of the mobs amongst the characters.
     * Formula used is taken from https://strategywiki.org/wiki/MapleStory/Formulas#Party_EXP.
     */
    public void distributeExp() {
        long totalExp = mob.getForcedMobStat().getExp();
        int attackers = damageDone.size();
        Set<Char> eligibleMembers = damageDone.keySet().stream().filter(this::isEligible).collect(Collectors.toSet());
        double expRate = GameConstants.getPartyExpRateByAttackersAndLeechers(attackers, eligibleMembers.size());
        for (Char chr : eligibleMembers) {
            long exp = (long) (totalExp * (0.2 * damageDone.getOrDefault(chr, 0D)
                                + 0.8 * chr.getLevel() / chr.getParty().getAvgPartyLevel(chr)));
            exp *= expRate;
            ExpIncreaseInfo eii = new ExpIncreaseInfo();
            eii.setPartyBonusExp((int) Math.min(Integer.MAX_VALUE, exp));
            if (exp > 0) {
                chr.addExp(exp, eii);
            }
        }
    }

    private boolean isEligible(Char chr) {
        boolean eligible = damageDone.keySet().contains(chr) && isWithinMobLevel(chr);
        if (!eligible) {
            for (Char attacker : damageDone.keySet()) {
                eligible |= isWithinMobLevel(chr) || (isWithinMobLevel(attacker) && isWithinAttackerLvl(attacker, chr))
                        && chr.getField() == attacker.getField();
                if (eligible) {
                    break;
                }
            }
        }
        return eligible;
    }

    private boolean isWithinAttackerLvl(Char attacker, Char chr) {
        return attacker.getLevel() - chr.getLevel() <= GameConstants.MAX_PARTY_CHR_LEVEL_DIFF;
    }

    private boolean isWithinMobLevel(Char chr) {
        return mob.getForcedMobStat().getLevel() - chr.getLevel() <= GameConstants.MAX_PARTY_MOB_LEVEL_DIFF;
    }
}

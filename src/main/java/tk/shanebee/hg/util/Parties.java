package tk.shanebee.hg.util;

import com.alessiodp.parties.api.interfaces.PartiesAPI;
import com.alessiodp.parties.api.interfaces.PartyPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Parties implements Party{
    //Support for Parties by AlessioDP Support by JT122406
    private final PartiesAPI api = com.alessiodp.parties.api.Parties.getApi();

    @Override
    public boolean hasParty(Player p) {
        PartyPlayer pp = api.getPartyPlayer(p.getUniqueId());
        return pp != null && pp.isInParty();
    }

    @Override
    public int partySize(Player p) {
        if (hasParty(p)) {
            PartyPlayer partyPlayer = api.getPartyPlayer(p.getUniqueId());
            if (partyPlayer != null) {
                if (partyPlayer.getPartyId() != null) {
                    com.alessiodp.parties.api.interfaces.Party party = api.getParty(partyPlayer.getPartyId());
                    if (null != party) {
                        return party.getOnlineMembers().size();
                    }
                }
            }
        }
        return 0;
    }

    @Override
    public boolean isOwner(Player p) {
        PartyPlayer pp = api.getPartyPlayer(p.getUniqueId());
        if (pp == null || pp.getPartyId() == null) {
            return false;
        }
        return true;
    }

    @Override
    public List<Player> getMembers(Player p) {
        ArrayList<Player> players = new ArrayList<>();
        if (hasParty(p)) {
            PartyPlayer pp = api.getPartyPlayer(p.getUniqueId());
            if (null != pp) {
                if (pp.getPartyId() != null) {
                    com.alessiodp.parties.api.interfaces.Party party = api.getParty(pp.getPartyId());
                    for (PartyPlayer member : party.getOnlineMembers()) {
                        players.add(Bukkit.getPlayer(member.getPlayerUUID()));
                    }
                }
            }
        }
        return players;
    }
}

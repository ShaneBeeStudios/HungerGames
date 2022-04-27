package tk.shanebee.hg.util;

import de.simonsator.partyandfriends.spigot.api.pafplayers.PAFPlayer;
import de.simonsator.partyandfriends.spigot.api.pafplayers.PAFPlayerManager;
import de.simonsator.partyandfriends.spigot.api.party.PartyManager;
import de.simonsator.partyandfriends.spigot.api.party.PlayerParty;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PAFBungee implements Party{

    //Party and Friends Extended for BungeeCord Support by JT122406

    private PlayerParty getPAFParty(Player p) {
        PAFPlayer pafPlayer = PAFPlayerManager.getInstance().getPlayer(p.getUniqueId());
        return PartyManager.getInstance().getParty(pafPlayer);
    }

    @Override
    public boolean hasParty(Player p) {
        return getPAFParty(p) != null;
    }

    @Override
    public int partySize(Player p) {
        return getMembers(p).size();
    }

    @Override
    public boolean isOwner(Player p) {
        PAFPlayer pafPlayer = PAFPlayerManager.getInstance().getPlayer(p.getUniqueId());
        PlayerParty party = PartyManager.getInstance().getParty(pafPlayer);
        if (party == null)
            return false;
        return party.isLeader(pafPlayer);
    }

    @Override
    public List<Player> getMembers(Player owner) {
        ArrayList<Player> playerList = new ArrayList<>();
        PlayerParty party = getPAFParty(owner);
        if (party == null)
            return playerList;
        for (PAFPlayer players : party.getAllPlayers()) {
            Player bukkitPlayer = Bukkit.getPlayer(players.getUniqueId());
            if (bukkitPlayer != null)
                playerList.add(bukkitPlayer);
        }
        return playerList;
    }
}

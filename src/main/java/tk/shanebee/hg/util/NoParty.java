package tk.shanebee.hg.util;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class NoParty implements Party{
    @Override
    public boolean hasParty(Player p) {
        return false;
    }

    @Override
    public int partySize(Player p) {
        return 0;
    }

    @Override
    public boolean isOwner(Player p) {
        return false;
    }

    @Override
    public List<Player> getMembers(Player owner) {
        return new ArrayList<>();
    }
}

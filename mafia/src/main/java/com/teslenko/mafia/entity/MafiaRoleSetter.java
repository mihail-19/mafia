package com.teslenko.mafia.entity;

import java.util.List;

/**
 * Sets roles for mafia {@link Player} list.
 * @author Mykhailo Teslenko
 *
 */
public interface MafiaRoleSetter {
	
	/**
	 * Sets roles to players. List could be modified.
	 * @param players
	 * @return
	 */
	public List<Player> setRolesToPlayers(List<Player> players);
	
	/**
	 * Returns real mafia num after estimation. Value could be 
	 * changed, e.g. due to small amount of players.
	 * @return mafiaNum
	 */
	public int getActualMafiaNum();
}

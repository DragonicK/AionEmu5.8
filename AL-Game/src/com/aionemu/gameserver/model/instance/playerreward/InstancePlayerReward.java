/*

 *
 *  Encom is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Encom is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser Public License
 *  along with Encom.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.instance.playerreward;

public class InstancePlayerReward {
	private int points;
	private int playerPvPKills;
	private int playerMonsterKills;
	protected Integer object;

	public InstancePlayerReward(Integer object) {
		this.object = object;
	}

	public Integer getOwner() {
		return object;
	}

	public int getPoints() {
		return points;
	}

	public int getPvPKills() {
		return playerPvPKills;
	}

	public int getMonsterKills() {
		return playerMonsterKills;
	}

	public void addPoints(int points) {
		this.points += points;
		if (this.points < 0) {
			this.points = 0;
		}
	}

	public void addPvPKillToPlayer() {
		playerPvPKills++;
	}

	public void addMonsterKillToPlayer() {
		playerMonsterKills++;
	}
}
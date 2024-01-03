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
package com.aionemu.gameserver.network.aion.serverpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.cp.PlayerCPEntry;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.services.player.CreativityPanel.stats.Agility;
import com.aionemu.gameserver.services.player.CreativityPanel.stats.Health;
import com.aionemu.gameserver.services.player.CreativityPanel.stats.Knowledge;
import com.aionemu.gameserver.services.player.CreativityPanel.stats.Power;
import com.aionemu.gameserver.services.player.CreativityPanel.stats.Precision;
import com.aionemu.gameserver.services.player.CreativityPanel.stats.Will;

import java.nio.ByteBuffer;

/**
 * @author Falke_34, FrozenKiller
 * @Rework By Xnemonix
 */
public class SM_CREATIVITY_POINTS extends AionServerPacket {
	Logger log = LoggerFactory.getLogger(SM_CREATIVITY_POINTS.class);
	private int totalPoint;
	private int step;
	PlayerCPEntry[] entries;

	public SM_CREATIVITY_POINTS(int totalPoint, int step) {
		this.totalPoint = totalPoint;
		this.step = step;
	}

	public SM_CREATIVITY_POINTS(int totalPoint, int step, PlayerCPEntry[] entries) {
		this.totalPoint = totalPoint;
		this.step = step;
		this.entries = entries;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeD(totalPoint); // Creativity Points Total
		writeD(step); // bar step

		if (entries != null) {
			writeH(entries.length);

			for (PlayerCPEntry entry : entries) {
				writeD(entry.getSlot());
				writeH(entry.getPoint());
			}
		}
		else {
			writeH(0);
		}
	}
}
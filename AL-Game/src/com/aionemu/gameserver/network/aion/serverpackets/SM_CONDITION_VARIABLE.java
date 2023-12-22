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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * @author Ranastic
 */

public class SM_CONDITION_VARIABLE extends AionServerPacket {
	private int instanceId;
	private int value;
	private String variable;

	public SM_CONDITION_VARIABLE(Player player, String variable, int value) {
		this.instanceId = player.getInstanceId();
		this.variable = variable;
		this.value = value;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeD(instanceId);
		writeS(variable);
		writeD(value);
	}
}
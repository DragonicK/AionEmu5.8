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
package com.aionemu.gameserver.network.aion.clientpackets;

import javolution.util.FastMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.panel_cp.PanelCp;
import com.aionemu.gameserver.model.templates.panel_cp.PanelCpType;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_ACTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.player.CreativityPanel.CreativityEssenceService;
import com.aionemu.gameserver.services.player.CreativityPanel.CreativitySkillService;
import com.aionemu.gameserver.services.player.CreativityPanel.CreativityStatsService;
import com.aionemu.gameserver.utils.PacketSendUtility;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Falke_34
 */
public class CM_CREATIVITY_POINTS extends AionClientPacket {

	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(CM_CREATIVITY_POINTS.class);

	private Player activePlayer;
	private int type;
	private int size;

	private int maximum = -1;

	private FastMap<Integer, Integer> values = new FastMap<>();

	public CM_CREATIVITY_POINTS(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		activePlayer = getConnection().getActivePlayer();
		type = readC();

		switch (type) {
		case 0: // Apply
			size = readH();

			for (int i = 0; i < size; i++) {
				int id = readD();
				int point = readH();

				if (id > maximum) {
					maximum = id;
				}

				values.put(id, point);
			}
			break;
		case 1: // Reset
			break;
		default:
			break;
		}
	}

	@Override
	protected void runImpl() {
		if (activePlayer == null) {
			return;
		}
		if (activePlayer.getLifeStats().isAlreadyDead()) {
			return;
		}
		if (type == 0) {
			CreativityEssenceService.getInstance().onReiceveData(activePlayer, values, maximum);

			maximum = -1;
			values.clear();
		}
		if (type == 1) {
			CreativityEssenceService.getInstance().onReset(activePlayer);
		}
	}
}
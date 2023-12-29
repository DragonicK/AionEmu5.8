/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 *
 *  Aion-Lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Aion-Lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details. *
 *  You should have received a copy of the GNU General Public License
 *  along with Aion-Lightning.
 *  If not, see <http://www.gnu.org/licenses/>.
 */
package ai.instance.secretMunitionsFactory;

import ai.AggressiveNpcAI2;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.world.WorldMapInstance;

/**
 * @author FrozenKiller
 */
@AIName("lunapatrol")
public class LunaPatrolAI2 extends AggressiveNpcAI2 {

	public void addAggro(Npc npc) {
		getAggroList().remove(npc);
		getAggroList().addHate(npc, 100000);
	}

	@Override
	public boolean canThink() {
		return true;
	}

	@Override
	protected void handleSpawned() {
		super.handleSpawned();

		switch (getNpcId()) {
			case 833826: // Roxy
				NpcShoutsService.getInstance().sendMsg(getOwner(), 1501597, getObjectId(), 0, 0);
				break;
			case 833827: // Marek
				NpcShoutsService.getInstance().sendMsg(getOwner(), 1501598, getObjectId(), 0, 0);
				break;
			case 833828: // Manad
				NpcShoutsService.getInstance().sendMsg(getOwner(), 1501599, getObjectId(), 0, 0);
				break;
			case 833829: // Herez
				NpcShoutsService.getInstance().sendMsg(getOwner(), 1502543, getObjectId(), 0, 0);
				break;
			case 833897: // Joel
				NpcShoutsService.getInstance().sendMsg(getOwner(), 1501600, getObjectId(), 0, 0);
				break;
		}

		WorldMapInstance instance = getPosition().getWorldMapInstance();

		for (Npc npc : instance.getNpcs(243658)) {
			getAggroList().addHate(npc, 100000);
		}

		for (Npc npc : instance.getNpcs(244035)) {
			getAggroList().addHate(npc, 100000);
		}
	}

	@Override
	protected void handleCreatureMoved(Creature creature) {
		super.handleCreatureMoved(creature);
	}

	@Override
	protected void handleMoveArrived() {
		super.handleMoveArrived();
	}

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
	}
}
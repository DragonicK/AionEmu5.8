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
package com.aionemu.gameserver.model.gameobjects;

import com.aionemu.gameserver.controllers.NpcController;
import com.aionemu.gameserver.model.house.House;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;

public class SummonedHouseNpc extends SummonedObject<House> {

	String masterName;

	public SummonedHouseNpc(int objId, NpcController controller, SpawnTemplate spawnTemplate, NpcTemplate npcTemplate,
			House house, String masterName) {
		super(objId, controller, spawnTemplate, npcTemplate, npcTemplate.getLevel());
		setCreator(house);
		this.masterName = masterName;
	}

	@Override
	public int getCreatorId() {
		return getCreator().getAddress().getId();
	}

	@Override
	public String getMasterName() {
		return masterName;
	}

	@Override
	public Creature getMaster() {
		return null;
	}
}
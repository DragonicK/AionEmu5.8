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
package com.aionemu.gameserver.services.player.CreativityPanel;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.cp.PlayerCPEntry;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.skill.PlayerSkillEntry;
import com.aionemu.gameserver.model.templates.panel_cp.PanelCp;
import com.aionemu.gameserver.model.templates.panel_cp.PanelCpSkill;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CREATIVITY_POINTS_APPLY;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SKILL_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SKILL_REMOVE;
import com.aionemu.gameserver.services.SkillLearnService;
import com.aionemu.gameserver.utils.PacketSendUtility;

import java.util.List;

public class CreativitySkillService {

	public void learnSkill(Player player, int id, int point, boolean shouldSendUpdate) {
		PanelCp pcp = DataManager.PANEL_CP_DATA.getPanelCpId(id);

		for (PanelCpSkill entry : pcp.getSkills()) {
			int skillId = entry.getSkillId();

			if (skillId != 0) {
				Race race = entry.getRace();

				if (player.getRace() == race || race == Race.PC_ALL) {
					addSkill(player, skillId, point);
				}
			}
		}

		PlayerCPEntry cpEntry = player.getCP().getEntryFromId(id);

		if (cpEntry != null) {
			cpEntry.setPoint(point);
		}
		else {
			player.getCP().addPoint(id, point);
		}

		if (shouldSendUpdate) {
			PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player, player.getSkillList().getAllSkills()));
		}

		PacketSendUtility.sendPacket(player, new SM_CREATIVITY_POINTS_APPLY(id, point));
	}

	public void unlearnSkill(Player player, int id, int point) {
		PanelCp pcp = DataManager.PANEL_CP_DATA.getPanelCpId(id);

		for (PanelCpSkill entry : pcp.getSkills()) {
			int skillId = entry.getSkillId();

			if (skillId != 0) {
				Race race = entry.getRace();

				if (player.getRace() == race || race == Race.PC_ALL) {
					unlearnSkillByLevel(player, skillId);
				}
			}
		}

		player.getCP().removePoint(id);

		PacketSendUtility.sendPacket(player, new SM_CREATIVITY_POINTS_APPLY(id, point));
	}

	public void enchantSkill(Player player, int id, int point, boolean shouldSendSkill) {
		PanelCp pcp = DataManager.PANEL_CP_DATA.getPanelCpId(id);

		for (PanelCpSkill entry : pcp.getSkills()) {
			int skillId = entry.getSkillId();

			// Do not touch on additional skills
			if (entry.isAdditionalSkill()) {
				continue;
			}

			if (skillId != 0) {
				Race race = entry.getRace();

				if (player.getRace() == race || race == Race.PC_ALL) {
					enchantSkill(player, skillId, point);
				}
			}
		}

		PlayerCPEntry cpEntry = player.getCP().getEntryFromId(id);

		if (cpEntry != null) {
			cpEntry.setPoint(point);
		}
		else {
			player.getCP().addPoint(id, point);
		}

		if (shouldSendSkill) {
			PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player, player.getSkillList().getAllSkills()));
		}

		PacketSendUtility.sendPacket(player, new SM_CREATIVITY_POINTS_APPLY(id, point));
	}

	public void disenchantSkill(Player player, int id, int point, boolean shouldSendSkill) {
		PanelCp pcp = DataManager.PANEL_CP_DATA.getPanelCpId(id);

		for (PanelCpSkill entry : pcp.getSkills()) {
			int skillId = entry.getSkillId();

			// Do not touch on additional skills
			if (entry.isAdditionalSkill()) {
				continue;
			}

			if (skillId != 0) {
				Race race = entry.getRace();

				if (player.getRace() == race || race == Race.PC_ALL) {
					disenchantSkill(player, skillId);
				}
			}
		}

		player.getCP().removePoint(id);

		if (shouldSendSkill) {
			PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player, player.getSkillList().getAllSkills()));
		}

		PacketSendUtility.sendPacket(player, new SM_CREATIVITY_POINTS_APPLY(id, point));
	}

	public void unlearnOnLogout(Player player, int skillId) {
		player.getSkillList().removeSkill(skillId);
	}

	public void disenchantOnLogout(Player player, int skillId) {
		PlayerSkillEntry entry = player.getSkillList().getSkillEntry(skillId);

		if (entry != null) {
			entry.setSkillLvl(1);
		}
	}

	private void addSkill(Player player, int skillId, int skillLevel) {
		PlayerSkillEntry entry = player.getSkillList().getSkillEntry(skillId);

		if (skillLevel == 0) {
			skillLevel = 1;
		}

		if (entry != null) {
			entry.setSkillLvl(skillLevel);
		}
		else {
			player.getSkillList().addSkill(player, skillId, skillLevel);
		}
	}

	private void enchantSkill(Player player, int skillId, int skillLevel) {
		PlayerSkillEntry entry = player.getSkillList().getSkillEntry(skillId);

		if (entry != null) {
			entry.setSkillLvl(skillLevel + 1);
		}
		else {
			player.getSkillList().addSkill(player, skillId, skillLevel + 1);
		}
	}

	private void unlearnSkillByLevel(Player player, int skillId) {
		PlayerSkillEntry entry = player.getSkillList().getSkillEntry(skillId);

		if (entry != null) {
			player.getSkillList().removeSkill(skillId);

			PacketSendUtility.sendPacket(player, new SM_SKILL_REMOVE(entry.getSkillId(), entry.getSkillLevel(), false, false));
		}
	}

	private void disenchantSkill(Player player, int skillId) {
		PlayerSkillEntry entry = player.getSkillList().getSkillEntry(skillId);

		if (entry != null) {
			entry.setSkillLvl(1);
		}
	}

	public static CreativitySkillService getInstance() {
		return NewSingletonHolder.INSTANCE;
	}

	private static class NewSingletonHolder {
		private static final CreativitySkillService INSTANCE = new CreativitySkillService();
	}
}
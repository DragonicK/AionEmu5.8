/*
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

import java.util.ArrayList;
import java.util.List;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.templates.panel_cp.PanelCpSkill;
import com.aionemu.gameserver.model.templates.panel_cp.PanelCpType;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import javolution.util.FastMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.configs.main.ArchDaevaConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.cp.PlayerCPEntry;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.panel_cp.PanelCp;
import com.aionemu.gameserver.services.player.CreativityPanel.stats.Agility;
import com.aionemu.gameserver.services.player.CreativityPanel.stats.Health;
import com.aionemu.gameserver.services.player.CreativityPanel.stats.Knowledge;
import com.aionemu.gameserver.services.player.CreativityPanel.stats.Power;
import com.aionemu.gameserver.services.player.CreativityPanel.stats.Precision;
import com.aionemu.gameserver.services.player.CreativityPanel.stats.Will;
import com.aionemu.gameserver.utils.PacketSendUtility;

/*
  KR - Update December 30th 2015
  http://aionpowerbook.com/powerbook/KR_-_Update_December_30th_2015 - The
  amount of Creativity a "ArchDaeva" can acquire with each level has been
  increased. - If you have already leveled up the difference will be paid the
  next time you reach the Creativity point. - For a certain amount of time
  reseting Creativity will only require 1 Kinah, the price will not increase
  with each reset.
 */

public class CreativityEssenceService {
	Logger log = LoggerFactory.getLogger(CreativityEssenceService.class);

	public void onReiceveData(Player player, FastMap<Integer, Integer> values, int maximum) {
		List<CreativityEntry> stats = new ArrayList<>();
		List<CreativityEntry> learn = new ArrayList<>();
		List<CreativityEntry> enchant = new ArrayList<>();
		List<CreativityEntry> unlearn = new ArrayList<>();
		List<CreativityEntry> disenchant = new ArrayList<>();

		for (int i = 1; i <= maximum; i++) {
			if (!values.containsKey(i)){
				continue;
			}

			PanelCp pcp = DataManager.PANEL_CP_DATA.getPanelCpId(i);

			if (pcp == null) {
				continue;
			}

			PanelCpType type = pcp.getPanelCpType();

			int point = values.get(i);

			switch (type) {
				case STAT_UP:
					stats.add(new CreativityEntry(i, point));
					break;
				case LEARN_SKILL:
					if (point > 0) {
						learn.add(new CreativityEntry(i, point));
					}
					else {
						unlearn.add(new CreativityEntry(i, point));
					}

					break;
				case ENCHANT_SKILL:
					if (point > 0) {
						enchant.add(new CreativityEntry(i, point));
					}
					else {
						disenchant.add(new CreativityEntry(i, point));
					}
					break;
			}
		}

		if (!stats.isEmpty()) { applyEssence(player, stats); }

		if (!disenchant.isEmpty()) { applyEssence(player, disenchant); }
		if (!unlearn.isEmpty()) { applyEssence(player, unlearn); }

		if (!learn.isEmpty()) { applyEssence(player, learn); }
		if (!enchant.isEmpty()) { applyEssence(player, enchant); }

		if (!values.isEmpty()) {
			if (player.getQuestStateList().getQuestState(20522) != null) {
				if (player.getQuestStateList().getQuestState(20522).getStatus() == QuestStatus.START) {
					player.getQuestStateList().getQuestState(20522).setQuestVar(0);
					player.getQuestStateList().getQuestState(20522).setStatus(QuestStatus.REWARD);
					PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(20522, 4, 0));
				}
			}

			if (player.getQuestStateList().getQuestState(10522) != null) {
				if (player.getQuestStateList().getQuestState(10522).getStatus() == QuestStatus.START){
					player.getQuestStateList().getQuestState(10522).setQuestVar(0);
					player.getQuestStateList().getQuestState(10522).setStatus(QuestStatus.REWARD);
					PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(10522, 4, 0));
				}
			}
		}
	}

	public void onLogout(Player player) {
		for (PlayerCPEntry entry : player.getCP().getAllCP()) {
			PanelCp pcp = DataManager.PANEL_CP_DATA.getPanelCpId(entry.getSlot());

			if (pcp == null) {
				continue;
			}

			PanelCpType type = pcp.getPanelCpType();

			if (type == PanelCpType.LEARN_SKILL) {
				for (PanelCpSkill cpSkill : pcp.getSkills()) {
					int skillId = cpSkill.getSkillId();

					if (skillId != 0) {
						Race race = cpSkill.getRace();

						if (player.getRace() == race || race == Race.PC_ALL) {
							CreativitySkillService.getInstance().unlearnOnLogout(player, skillId);
						}
					}
				}
			}
			else if (type == PanelCpType.ENCHANT_SKILL) {
				for (PanelCpSkill cpSkill : pcp.getSkills()) {
					if (cpSkill.isAdditionalSkill()) {
						continue;
					}

					int skillId = cpSkill.getSkillId();

					if (skillId != 0) {
						Race race = cpSkill.getRace();

						if (player.getRace() == race || race == Race.PC_ALL) {
							CreativitySkillService.getInstance().disenchantOnLogout(player, skillId);
						}
					}
				}
			}
		}
	}

	public void onLogin(Player player) {
		if (player.isArchDaeva()) {
			int creativityPoints = getCreativityPoints(player);
			int currentLevelPoint = getPointsFromCurrentStep(player);
			int clientStep = getClientStep(player.getLevel(), currentLevelPoint);

			if (creativityPoints > ArchDaevaConfig.CP_LIMIT_MAX) {
				creativityPoints = ArchDaevaConfig.CP_LIMIT_MAX;
			}

			player.setCPStep(clientStep);
			player.setCreativityPoint(creativityPoints);

			PlayerCPEntry[] entries = player.getCP().getAllCP();

			for (PlayerCPEntry entry : entries) {
				PanelCp pcp = DataManager.PANEL_CP_DATA.getPanelCpId(entry.getSlot());

				PanelCpType type = pcp.getPanelCpType();

				if (type == PanelCpType.STAT_UP) {
					CreativityStatsService.getInstance().onEssenceApply(player, entry.getSlot(), entry.getPoint());
				}
				else if (type == PanelCpType.LEARN_SKILL) {
					CreativitySkillService.getInstance().learnSkill(player, entry.getSlot(), entry.getPoint(), false);
				}
			}

			for (PlayerCPEntry entry : entries) {
				PanelCp pcp = DataManager.PANEL_CP_DATA.getPanelCpId(entry.getSlot());

				PanelCpType type = pcp.getPanelCpType();

				if (type == PanelCpType.ENCHANT_SKILL) {
					CreativitySkillService.getInstance().enchantSkill(player, entry.getSlot(), entry.getPoint(), false);
				}
			}

			PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player, player.getSkillList().getAllSkills()));
			PacketSendUtility.sendPacket(player, new SM_CREATIVITY_POINTS(creativityPoints, clientStep, player.getCP().getAllCP()));
		}
	}

	public void onExperienceChanged(Player player) {
		if (player.isArchDaeva()) {
			int creativityPoints = getCreativityPoints(player);
			int oldCreativityPoints = player.getCreativityPoint();

			if (oldCreativityPoints != creativityPoints) {
				int currentLevelPoint = getPointsFromCurrentStep(player);
				int clientStep = getClientStep(player.getLevel(), currentLevelPoint);

				if (creativityPoints > ArchDaevaConfig.CP_LIMIT_MAX) {
					creativityPoints = ArchDaevaConfig.CP_LIMIT_MAX;
				}

				player.setCPStep(clientStep);
				player.setCreativityPoint(creativityPoints);

				PacketSendUtility.sendPacket(player, new SM_CREATIVITY_POINTS(creativityPoints, clientStep));
			}
		}
	}

	public void onReset(Player player) {
		// Remove Enchance Character
		for (int i = 1; i <= 6; i++) {
			if (player.getCP().getEntryFromId(i) != null) {
				player.getCP().removePoint(i);
			}
		}

		for (PlayerCPEntry entry : player.getCP().getAllCP()) {
			PanelCp pcp = DataManager.PANEL_CP_DATA.getPanelCpId(entry.getSlot());

			if (pcp == null) {
				continue;
			}

			PanelCpType type = pcp.getPanelCpType();

			if (type == PanelCpType.LEARN_SKILL) {
				CreativitySkillService.getInstance().unlearnSkill(player, entry.getSlot(), entry.getPoint());
			}
			else if (type == PanelCpType.ENCHANT_SKILL) {
				CreativitySkillService.getInstance().disenchantSkill(player, entry.getSlot(), entry.getPoint(), true);
			}
		}

		player.setCPSlot1(0);
		player.setCPSlot2(0);
		player.setCPSlot3(0);
		player.setCPSlot4(0);
		player.setCPSlot5(0);
		player.setCPSlot6(0);

		Power.getInstance().onChange(player, 0);
		Health.getInstance().onChange(player, 0);
		Agility.getInstance().onChange(player, 0);
		Precision.getInstance().onChange(player, 0);
		Knowledge.getInstance().onChange(player, 0);
		Will.getInstance().onChange(player, 0);

		PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));
		PacketSendUtility.sendPacket(player, new SM_CREATIVITY_POINTS(player.getCreativityPoint(), player.getCPStep()));
		PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player, player.getSkillList().getAllSkills()));
	}

	public void onEquipEstima(Player player) {

	}

	public void onUnequipEstima(Player player) {

	}

	// Get the total of creativity points.
	private int getCreativityPoints(Player player) {
		final int START_LEVEL = 66;

		int level = player.getLevel();
		int points = ArchDaevaConfig.CP_STARTER;

		for (int i = START_LEVEL; i <= level; i++) {
			points += ArchDaevaConfig.CP_PER_LEVEL;

			if (i != level) {
				points += getPointsFromSteps(i);
			}
			else {
				points += getPointsFromCurrentStep(player);
			}
		}

		return points;
	}

	private int getPointsFromCurrentStep(Player player) {
		long experience = player.getCommonData().getExpShown();
		long maximum = player.getCommonData().getExpNeed();
		int level = player.getLevel();

		if (experience >= maximum) {
			return getPointsFromSteps(level);
		}
		else {
			int steps = getPointsFromSteps(level);

			float exp_percent = ((float)experience / (float)maximum) * 100f;
			float step_percent = 100f / steps;

			return getPointsFromExperience(level, exp_percent, step_percent);
		}
	}

	private int getPointsFromExperience(int level, float exp_percent, float step_percent) {
		// Check for minimum first.
		if (exp_percent <= step_percent) {
			return 0;
		}

		int step = 0;
		int steps = getPointsFromSteps(level);

		float total = step_percent;

		final int previous = 1;

		for (int i = 1; i <= steps; i++) {
			if (exp_percent >= total) {
				total += step_percent;
			}
			else {
				return i - previous;
			}

			step = i;
		}

		return step;
	}

	private int getPointsFromSteps(int level) {
		switch (level) {
			case 66: return CreativityStepsPerLevel.LEVEL_66.steps;
			case 67: return CreativityStepsPerLevel.LEVEL_67.steps;
			case 68: return CreativityStepsPerLevel.LEVEL_68.steps;
			case 69: return CreativityStepsPerLevel.LEVEL_69.steps;
			case 70: return CreativityStepsPerLevel.LEVEL_70.steps;
			case 71: return CreativityStepsPerLevel.LEVEL_71.steps;
			case 72: return CreativityStepsPerLevel.LEVEL_72.steps;
			case 73: return CreativityStepsPerLevel.LEVEL_73.steps;
			case 74: return CreativityStepsPerLevel.LEVEL_74.steps;
			case 75: return CreativityStepsPerLevel.LEVEL_75.steps;
			case 76: return CreativityStepsPerLevel.LEVEL_76.steps;
			case 77: return CreativityStepsPerLevel.LEVEL_77.steps;
			case 78: return CreativityStepsPerLevel.LEVEL_78.steps;
			case 79: return CreativityStepsPerLevel.LEVEL_79.steps;
			case 80: return CreativityStepsPerLevel.LEVEL_80.steps;
			case 81: return CreativityStepsPerLevel.LEVEL_81.steps;
			case 82: return CreativityStepsPerLevel.LEVEL_82.steps;
			case 83: return CreativityStepsPerLevel.LEVEL_83.steps;
			case 84: return CreativityStepsPerLevel.LEVEL_84.steps;
		}

		return 0;
	}

	private int getClientStep(int level, int currentStep) {
		final int OFFSET = 5;
		final int START_LEVEL = 66;

		int next = OFFSET;

		for (int i = START_LEVEL; i <= level; i++) {
			if (i == level) {
				int max = getPointsFromSteps(level);

				if (currentStep == max) {
					return next + currentStep;
				}else {
					return next + currentStep + 1;
				}
			}
			else {
				next += getPointsFromSteps(i) + OFFSET;
			}
		}

		return 0;
	}

	private void applyEssence(Player player, List<CreativityEntry> entries) {
		for (CreativityEntry entry : entries) {
			applyEssence(player, entry.getId(), entry.getPoint());
		}
	}

	private void applyEssence(Player player, int id, int points) {
		PanelCp pcp = DataManager.PANEL_CP_DATA.getPanelCpId(id);

		if (pcp == null) {
			return;
		}

		PanelCpType type = pcp.getPanelCpType();

		switch (type) {
			case STAT_UP:
				CreativityStatsService.getInstance().onEssenceApply(player, id, points);
				break;

			case LEARN_SKILL:
				if (points >= 1) {
					CreativitySkillService.getInstance().learnSkill(player, id, points, true);
				}
				else {
					CreativitySkillService.getInstance().unlearnSkill(player, id, points);
				}
				break;

			case ENCHANT_SKILL:
				if (points >= 1) {
					CreativitySkillService.getInstance().enchantSkill(player, id, points, true);
				}
				else {
					CreativitySkillService.getInstance().disenchantSkill(player, id,  points, true);
				}
				break;
		}
	}

//	public void addEstimaCp(Player player, int objId) {
//		estimaCp = 1;
//
//		Item addEstima = player.getEquipment().getEquippedItemByObjId(objId);
//
//		if (addEstima != null) {
//			switch (addEstima.getEnchantLevel()) {
//			case 6:
//				estimaCp = 8;
//				break;
//			case 7:
//				estimaCp = 10;
//				break;
//			case 8:
//				estimaCp = 12;
//				break;
//			case 9:
//				estimaCp = 14;
//				break;
//			case 10:
//				estimaCp = 17;
//				break;
//			default:
//				estimaCp = (addEstima.getEnchantLevel() + 1);
//				break;
//			}
//		}
//
//		currentCp = player.getCreativityPoint();
//
//		int totalCp = (currentCp + estimaCp);
//		player.setCreativityPoint(totalCp);
//
//		int size = DAOManager.getDAO(PlayerCreativityPointsDAO.class).getSlotSize(player.getObjectId());
//
//	//	PacketSendUtility.sendPacket(player, new SM_CREATIVITY_POINTS(totalCp, player.getCPStep()));
//	//	PacketSendUtility.sendPacket(player, new SM_CREATIVITY_POINTS(player.getCreativityPoint(), player.getCPStep(), size, false));
//	}

//	public void removeEstimaCp(Player player, int objId) {
//		estimaCp = 0;
//		Item removedEstima = player.getInventory().getItemByObjId(objId);
//		if (removedEstima != null) {
//			switch (removedEstima.getEnchantLevel()) {
//			case 6:
//				estimaCp = 8;
//				break;
//			case 7:
//				estimaCp = 10;
//				break;
//			case 8:
//				estimaCp = 12;
//				break;
//			case 9:
//				estimaCp = 14;
//				break;
//			case 10:
//				estimaCp = 17;
//				break;
//			default:
//				estimaCp = (removedEstima.getEnchantLevel() + 1);
//				break;
//			}
//		}
//		currentCp = player.getCreativityPoint();
//		int totalCp = (currentCp - estimaCp);
//		player.setCreativityPoint(totalCp);
//		int size = DAOManager.getDAO(PlayerCreativityPointsDAO.class).getSlotSize(player.getObjectId());
//		//PacketSendUtility.sendPacket(player, new SM_CREATIVITY_POINTS(totalCp, player.getCPStep()));
//		//PacketSendUtility.sendPacket(player,				new SM_CREATIVITY_POINTS(player.getCreativityPoint(), player.getCPStep(), size, false));
//	}

	public static CreativityEssenceService getInstance() {
		return NewSingletonHolder.INSTANCE;
	}

	private static class NewSingletonHolder {
		private static final CreativityEssenceService INSTANCE = new CreativityEssenceService();
	}
}
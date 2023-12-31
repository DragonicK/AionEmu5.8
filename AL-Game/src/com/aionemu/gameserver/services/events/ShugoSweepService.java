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
package com.aionemu.gameserver.services.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.configs.main.EventsConfig;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.dao.PlayerShugoSweepDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.gameobjects.player.PlayerSweep;
import com.aionemu.gameserver.model.templates.shugosweep.ShugoSweepReward;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SHUGO_SWEEP;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Rinzler (Encom)
 */
public class ShugoSweepService {
	public final static Map<Integer, Integer> RewardsBitwise = new HashMap<>();
	private static final Logger log = LoggerFactory.getLogger(ShugoSweepService.class);
	private final int boardId = EventsConfig.EVENT_SHUGOSWEEP_BOARD;
	private final static int MaximumShugoRewards = 30;

	public void initShugoSweep() {
		log.info("[ShugoSweepService] is initialized...");

		int value = 1;

		for (int i = 1; i <= MaximumShugoRewards; i++) {
			value *= 2;
			RewardsBitwise.put(i, value);
		}

		// TODO
		// String weekly = "0 0 9 ? * WED *";
	}

	public void onLogin(Player player) {
		DAOManager.getDAO(PlayerShugoSweepDAO.class).load(player);

		if (player.getPlayerShugoSweep() == null) {
			PlayerSweep ps = new PlayerSweep(0, EventsConfig.EVENT_SHUGOSWEEP_FREEDICE, boardId, 0, 0, 0);
			ps.setPersistentState(PersistentState.UPDATE_REQUIRED);
			player.setPlayerShugoSweep(ps);
			player.getPlayerShugoSweep().setShugoSweepByObjId(player.getObjectId());

			DAOManager.getDAO(PlayerShugoSweepDAO.class).add(player.getObjectId(), ps.getFreeDice(), ps.getStep(),
					ps.getBoardId(), ps.getGoldenDice(), ps.getResetBoard(), ps.getCompletedSteps());
		}

		if (player.getPlayerShugoSweep().getBoardId() != boardId) {
			PlayerSweep ps = player.getPlayerShugoSweep();

			ps.setStep(0);
			ps.setBoardId(boardId);
			ps.setFreeDice(ps.getFreeDice());
			ps.setCompletedSteps(0);

			ps.setPersistentState(PersistentState.UPDATE_REQUIRED);
			player.getPlayerShugoSweep().setShugoSweepByObjId(player.getObjectId());
		}

		int completedSteps = player.getPlayerShugoSweep().getCompletedSteps();

		PacketSendUtility.sendPacket(player,
				new SM_SHUGO_SWEEP(getPlayerSweep(player).getBoardId(), getPlayerSweep(player).getStep(),
						getPlayerSweep(player).getFreeDice(), player.getPlayerShugoSweep().getGoldenDice(),
						player.getPlayerShugoSweep().getResetBoard(), 0, completedSteps));
	}

	public void onLogout(Player player) {
		DAOManager.getDAO(PlayerShugoSweepDAO.class).store(player);
		player.getPlayerShugoSweep().setShugoSweepByObjId(player.getObjectId());
	}

	public void launchDice(final Player player) {
		int move = Rnd.get(1, 6);
		int step = getPlayerSweep(player).getStep();
		int dice = getPlayerSweep(player).getFreeDice();
		int goldDice = player.getPlayerShugoSweep().getGoldenDice();

		if (getPlayerSweep(player).getFreeDice() != 0) {
			getPlayerSweep(player).setFreeDice(dice - 1);
			player.getPlayerShugoSweep().setShugoSweepByObjId(player.getObjectId());
		} else {
			player.getPlayerShugoSweep().setGoldenDice(goldDice - 1);
			DAOManager.getDAO(PlayerDAO.class).storePlayer(player);
		}

		int completedSteps = player.getPlayerShugoSweep().getCompletedSteps();

		int positions = move;
		int pointer = step;
		int maxTry = 0;

		do {
			if (pointer == 30) {
				pointer = 0;
			}

			int bitwise = RewardsBitwise.get(pointer + 1);

			if ((completedSteps & bitwise) != bitwise) {
				positions--;
			}

			pointer++;

			if (pointer > 30) {
				pointer = 0;
			}

			maxTry++;

			if (maxTry >= 300) {
				break;
			}
		}
		while (positions > 0);

		int newStep = pointer;

		PacketSendUtility.sendPacket(player, new SM_SHUGO_SWEEP(boardId, getPlayerSweep(player).getStep(),
				getPlayerSweep(player).getFreeDice(), player.getPlayerShugoSweep().getGoldenDice(), 0, 0,completedSteps));

		getPlayerSweep(player).setStep(newStep);
		player.getPlayerShugoSweep().setShugoSweepByObjId(player.getObjectId());

		PacketSendUtility.sendPacket(player,
				new SM_SHUGO_SWEEP(getPlayerSweep(player).getBoardId(), getPlayerSweep(player).getStep(),
						getPlayerSweep(player).getFreeDice(), player.getPlayerShugoSweep().getGoldenDice(),
						player.getPlayerShugoSweep().getResetBoard(), move, completedSteps));

		int bitwise = RewardsBitwise.get(newStep);

		if ((completedSteps & bitwise) != bitwise) {
			completedSteps |= bitwise;
		}

		player.getPlayerShugoSweep().setCompletedSteps(completedSteps);

		sendNewCompletedSteps(player, completedSteps, move);

		rewardPlayer(player, getPlayerSweep(player).getStep(), move);
	}

	public void resetBoard(Player player) {
		boolean isFree = false;
		int bitwise = 0;

		for (int i = 1; i <= MaximumShugoRewards; i++) {
			bitwise |= RewardsBitwise.get(i);
		}

		isFree = bitwise == player.getPlayerShugoSweep().getCompletedSteps();

		if (!isFree) {
			int reset = player.getPlayerShugoSweep().getResetBoard();
			player.getPlayerShugoSweep().setResetBoard(reset - 1);
		}

		player.getPlayerShugoSweep().setCompletedSteps(0);
		player.getPlayerShugoSweep().setStep(0);

		PacketSendUtility.sendPacket(player,
				new SM_SHUGO_SWEEP(getPlayerSweep(player).getBoardId(), 0, getPlayerSweep(player).getFreeDice(),
						player.getPlayerShugoSweep().getGoldenDice(), player.getPlayerShugoSweep().getResetBoard(), 0, 0));
	}

	private void sendNewCompletedSteps(final Player player, final int completedStep, final int moves) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				int completed = completedStep;

				PacketSendUtility.sendPacket(player, new SM_SHUGO_SWEEP(boardId, getPlayerSweep(player).getStep(),
						getPlayerSweep(player).getFreeDice(), player.getPlayerShugoSweep().getGoldenDice(), 0, 0,completed));
			}
		}, moves * 750);
	}

	private void rewardPlayer(final Player player, final int step, final int move) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (player.isOnline()) {
					ShugoSweepReward reward = getRewardForBoard(boardId, step);
					ItemService.addItem(player, reward.getItemId(), reward.getCount());
				}
			}
		}, move * 950);
	}

	private PlayerSweep getPlayerSweep(Player player) {
		return player.getPlayerShugoSweep();
	}

	private static ShugoSweepReward getRewardForBoard(int boardId, int step) {
		return DataManager.SHUGO_SWEEP_REWARD_DATA.getRewardBoard(boardId, step);
	}

	public static final ShugoSweepService getInstance() {
		return SingletonHolder.instance;
	}

	private static class SingletonHolder {
		protected static final ShugoSweepService instance = new ShugoSweepService();
	}
}
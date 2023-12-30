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
package com.aionemu.gameserver.model.gameobjects.player;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerShugoSweepDAO;
import com.aionemu.gameserver.model.gameobjects.PersistentState;

/**
 * Created by Wnkrz on 23/10/2017.
 */
// Shugo Sweep 5.1
public class PlayerSweep {
	Logger log = LoggerFactory.getLogger(PlayerSweep.class);
	private PersistentState persistentState;

	private int step;
	private int freeDice;
	private int boardId;
	private int goldenDice;
	private int resetBoard;
	private int completedSteps;

	public PlayerSweep(int step, int freeDice, int boardId, int goldenDice, int resetBoard, int completedSteps) {
		this.step = step;
		this.freeDice = freeDice;
		this.boardId = boardId;
		this.goldenDice = goldenDice;
		this.resetBoard = resetBoard;
		this.completedSteps = completedSteps;
		this.persistentState = PersistentState.NEW;
	}

	public PlayerSweep() {
	}

	public int getFreeDice() {
		return freeDice;
	}

	public void setFreeDice(int dice) {
		this.freeDice = dice;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public int getBoardId() {
		return boardId;
	}

	public int getGoldenDice() {
		return goldenDice;
	}

	public void setGoldenDice(int dice) {
		this.goldenDice = dice;
	}

	public int getResetBoard() {
		return resetBoard;
	}

	public void setResetBoard(int reset) {
		this.resetBoard = reset;
	}

	public void setBoardId(int boardId) {
		this.boardId = boardId;
	}

	public int getCompletedSteps() {
		return this.completedSteps;
	}

	public void setCompletedSteps(int steps) {
		this.completedSteps = steps;
	}

	public PersistentState getPersistentState() {
		return persistentState;
	}

	public void setShugoSweepByObjId(int playerId) {
		DAOManager.getDAO(PlayerShugoSweepDAO.class).setShugoSweepByObjId(playerId, getFreeDice(), getStep(),
				getBoardId(), getGoldenDice(), getResetBoard(), getCompletedSteps());
	}

	public void setPersistentState(PersistentState persistentState) {
		switch (persistentState) {
		case UPDATE_REQUIRED:
			if (this.persistentState == PersistentState.NEW) {
				break;
			}
		default:
			this.persistentState = persistentState;
		}
	}
}
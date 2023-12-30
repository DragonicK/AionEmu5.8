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
import com.aionemu.gameserver.dao.PlayerLunaShopDAO;
import com.aionemu.gameserver.model.gameobjects.PersistentState;

import java.sql.Connection;

/**
 * Created by wanke on 13/02/2017.
 */

public class PlayerLunaShop {
	Logger log = LoggerFactory.getLogger(PlayerLunaShop.class);
	private PersistentState persistentState;

	private boolean FreeUnderpath;
	private boolean FreeFactory;
	private boolean FreeChest;

	//Luna Dice Parts
	private int lunaDiceCount;
	private boolean lunaGoldenDice;
	private int lunaConsumePoint;
	private int muni_keys;
	private int consumeCount = 0;
	private int wardrobeSlot;

	public PlayerLunaShop(boolean freeUnderpath, boolean freeFactory, boolean freeChest) {
		this.FreeUnderpath = freeUnderpath;
		this.FreeFactory = freeFactory;
		this.FreeChest = freeChest;
		this.persistentState = PersistentState.NEW;
	}

	public PlayerLunaShop() {
	}

	public boolean isFreeUnderpath() {
		return FreeUnderpath;
	}

	public void setFreeUnderpath(boolean free) {
		this.FreeUnderpath = free;
	}

	public boolean isFreeFactory() {
		return FreeFactory;
	}

	public void setFreeFactory(boolean free) {
		this.FreeFactory = free;
	}

	public boolean isFreeChest() {
		return FreeChest;
	}

	public void setFreeChest(boolean free) {
		this.FreeChest = free;
	}

	public PersistentState getPersistentState() {
		return persistentState;
	}

	public void setLunaShopByObjId(int playerId) {
		DAOManager.getDAO(PlayerLunaShopDAO.class).setLunaShopByObjId(playerId, isFreeUnderpath(), isFreeFactory(),
				isFreeChest(), getLunaConsumePoint(), getLunaConsumeCount(), getWardrobeSlot(), getMuniKeys(),
				getLunaDiceCount(), isLunaGoldenDice());
	}

	public int getLunaDiceCount() {
		return lunaDiceCount;
	}

	public void setLunaDiceCount(int lunaDiceCount) {
		this.lunaDiceCount = lunaDiceCount;
	}

	public boolean isLunaGoldenDice() {
		return lunaGoldenDice;
	}

	public void setLunaGoldenDice(boolean lunaGoldenDice) {
		this.lunaGoldenDice = lunaGoldenDice;
	}

	public void setLunaConsumePoint(int point) {
		this.lunaConsumePoint = point;
	}

	public int getLunaConsumePoint() {
		return this.lunaConsumePoint;
	}

	public void setMuniKeys(int keys) {
		this.muni_keys = keys;
	}

	public int getMuniKeys() {
		return this.muni_keys;
	}

	public void setLunaConsumeCount(int count) {
		this.consumeCount = count;
	}

	public int getLunaConsumeCount() {
		return this.consumeCount;
	}

	public void setWardrobeSlot(int slot) {
		this.wardrobeSlot = slot;
	}

	public int getWardrobeSlot() {
		return this.wardrobeSlot;
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
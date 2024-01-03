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
package com.aionemu.gameserver.model.cp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aionemu.gameserver.model.gameobjects.PersistentState;

public final class PlayerCPList implements CPList {
	private final Map<Integer, PlayerCPEntry> entries;
	private final List<Integer> removed;

	public PlayerCPList() {
		this.removed = new ArrayList<>();
		this.entries = new HashMap<Integer, PlayerCPEntry>(0);
	}

	public PlayerCPList(List<PlayerCPEntry> entries) {
		this();

		for (PlayerCPEntry e : entries) {
			this.entries.put(e.getSlot(), e);
		}
	}

	public PlayerCPEntry[] getAllCP() {
		List<PlayerCPEntry> allCp = new ArrayList<PlayerCPEntry>();

		allCp.addAll(entries.values());

		return allCp.toArray(new PlayerCPEntry[allCp.size()]);
	}

	public PlayerCPEntry getEntryFromId(int id){
		if (entries.containsKey(id)) {
			return entries.get(id);
		}

		return null;
	}

	@Override
	public boolean addPoint(int slot, int point) {
		return addPoint(slot, point, PersistentState.NEW);
	}

	private synchronized boolean addPoint(int slot, int point, PersistentState state) {
		if (removed.contains(slot)) {
			removed.remove((Integer)removed.indexOf(slot));
		}

		entries.put(slot, new PlayerCPEntry(slot, point, state));

		return true;
	}

	@Override
	public synchronized boolean removePoint(int slot) {
		PlayerCPEntry entry = this.entries.get(slot);

		if (entry != null) {
			if (!removed.contains(slot)) {
				removed.add(entry.getSlot());
			}

			this.entries.remove(slot);
		}

		return entry != null;
	}

	public List<Integer> getRemoveItems() {
		return this.removed;
	}

	@Override
	public int size() {
		return entries.size();
	}
}
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
package com.aionemu.gameserver.configs.main;

import com.aionemu.commons.configuration.Property;

public class ArchDaevaConfig {

	/**
	 * Arch Daeva Configs
	 */
	@Property(key = "gameserver.max.cp.limit", defaultValue = "1000")
	public static int CP_LIMIT_MAX;

	@Property(key = "gameserver.cp.starter", defaultValue = "1")
	public static int CP_STARTER;

	@Property(key = "gameserver.cp.per.level", defaultValue = "6")
	public static int CP_PER_LEVEL;

	@Property(key = "gameserver.cp.per.exp.step", defaultValue = "1")
	public static int CP_PER_EXP_STEP;

	@Property(key = "gameserver.item.not.for.highdaeva.enable", defaultValue = "false")
	public static boolean ITEM_NOT_FOR_HIGHDAEVA_ENABLE;
}
/*
 * This file is part of Encom.
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
package ai.worlds.cygnea;

import ai.AggressiveNpcAI2;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;

/****/
/** Author (Encom)
/****/

@AIName("frostgullet_kirrin")
public class Frostgullet_KirrinAI2 extends AggressiveNpcAI2
{
	@Override
	protected void handleDied() {
		spawn(235918, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading()); //Frostshard Kirrin.
		super.handleDied();
		AI2Actions.deleteOwner(this);
		AI2Actions.scheduleRespawn(this);
	}
}
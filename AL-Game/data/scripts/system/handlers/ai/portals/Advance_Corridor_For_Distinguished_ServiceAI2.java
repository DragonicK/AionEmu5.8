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
package ai.portals;

import ai.GeneralNpcAI2;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.autogroup.AutoGroupType;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FIND_GROUP;
import com.aionemu.gameserver.utils.PacketSendUtility;

/****/
/** Author (Encom)
/****/

@AIName("advance_corridor_distinguished")
public class Advance_Corridor_For_Distinguished_ServiceAI2 extends GeneralNpcAI2
{
	@Override
	protected void handleDialogStart(Player player) {
        switch (getNpcId()) {
			case 730967: //Advance Corridor For Distinguished Service.
			case 730968: { //Advance Corridor For Distinguished Service.
				super.handleDialogStart(player);
				break;
			} default: {
				if (player.getLevel() >= 65) {
				    PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getOwner().getObjectId(), 10));
				}
				break;
			}
		}
	}
	
	@Override
	public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
		if (dialogId == 105) {
			switch (getNpcId()) {
			    case 730967: //Advance Corridor For Distinguished Service.
				case 730968: //Advance Corridor For Distinguished Service.
				    AutoGroupType agt = AutoGroupType.getAutoGroup(player.getLevel(), getNpcId());
					if (agt != null) {
					    PacketSendUtility.sendPacket(player, new SM_FIND_GROUP(0x1A, agt.getInstanceMapId()));
					}
				break;
			}
		} else if (dialogId == 10) {
            PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 10));
        }
        return true;
    }
}
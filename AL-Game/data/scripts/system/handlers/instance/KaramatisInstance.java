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
package instance;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.zone.ZoneInstance;
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author (Encom)
/****/

@InstanceID(310010000)
public class KaramatisInstance extends GeneralInstanceHandler
{
	@Override
    public void onEnterZone(Player player, ZoneInstance zone) {
        if (zone.getAreaTemplate().getZoneName() == ZoneName.get("AFIRA_OBELISK_310010000")) {
            belpartanBlessing();
	    }
    }

	@Override
	public void onLeaveInstance(Player player) {
		player.getEffectController().removeEffect(281);
	}

	private void belpartanBlessing() {
		for (Player p: instance.getPlayersInside()) {
			QuestState qs = p.getQuestStateList().getQuestState(1006);
			boolean canApply = false;

			if (qs != null) {
				if (qs.getStatus() == QuestStatus.START) {
					if (qs.getQuestVarById(0) != 0) {
						canApply = true;
					}
				}
			}

			if (canApply) {
				SkillTemplate st =  DataManager.SKILL_DATA.getSkillTemplate(281); //Belpartan's Blessing.
				Effect e = new Effect(p, p, st, 1, st.getEffectsDuration(9));
				e.initialize();
				e.applyEffect();
			}
		}
	}
}
/*
 * =====================================================================================*
 * This file is part of Aion-Unique (Aion-Unique Home Software Development)             *
 * Aion-Unique Development is a closed Aion Project that use Old Aion Project Base      *
 * Like Aion-Lightning, Aion-Engine, Aion-Core, Aion-Extreme, Aion-NextGen, ArchSoft,   *
 * Aion-Ger, U3J, Encom And other Aion project, All Credit Content                      *
 * That they make is belong to them/Copyright is belong to them. And All new Content    *
 * that Aion-Unique make the copyright is belong to Aion-Unique                         *
 * You may have agreement with Aion-Unique Development, before use this Engine/Source   *
 * You have agree with all of Term of Services agreement with Aion-Unique Development   *
 * =====================================================================================*
 */
package quest.heiron;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_ACTION;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author Ghostfur & Unknown (Aion-Unique)
/****/

public class _1661Finding_The_Forges extends QuestHandler
{
	private final static int questId = 1661;

	private final static String WeaponZoneName = "LF3_SENSORY_AREA_Q1661_A_210040000";
	private final static String OdiumZoneName = "LF3_SENSORY_AREA_Q1661_B_210040000";
	private final static String ArmorZoneName = "LF3_SENSORY_AREA_Q1661_C_210040000";

	private final static int WeaponZoneVar = 16;
	private final static int OdiumZoneVar = 32;
	private final static int ArmorZoneVar = 64;

	public _1661Finding_The_Forges() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(204600).addOnQuestStart(questId);
		qe.registerQuestNpc(204600).addOnTalkEvent(questId);
		qe.registerOnEnterZone(ZoneName.get("LF3_SENSORY_AREA_Q1661_A_210040000"), questId);
		qe.registerOnEnterZone(ZoneName.get("LF3_SENSORY_AREA_Q1661_B_210040000"), questId);
		qe.registerOnEnterZone(ZoneName.get("LF3_SENSORY_AREA_Q1661_C_210040000"), questId);
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestDialog dialog = env.getDialog();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 204600) { 
				if (dialog == QuestDialog.START_DIALOG) {
					return sendQuestDialog(env, 1011);
				} else if (dialog == QuestDialog.ACCEPT_QUEST) {
					playQuestMovie(env, 200);
					return sendQuestStartDialog(env);
				} else
					return sendQuestStartDialog(env);
			}
		} else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204600) {
				if (dialog == QuestDialog.START_DIALOG) {
					return sendQuestDialog(env, 1352);
				} else
					return sendQuestEndDialog(env);
			}
		}
		return false;
	}
	
	@Override
	public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
		Player player = env.getPlayer();

		if (player == null)
			return false;

		QuestState qs = player.getQuestStateList().getQuestState(questId);

 		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);

			if (IsInWeaponZone(zoneName)) {
				if ((var & WeaponZoneVar) == 0) {
					var |= WeaponZoneVar;

					qs.setQuestVarById(0, var);
					updateQuestStatus(env);
				}
			}
			else if (IsInOdiumZone(zoneName)) {
				if ((var & OdiumZoneVar) == 0) {
					var |= OdiumZoneVar;

					qs.setQuestVarById(0, var);
					updateQuestStatus(env);
				}
			}
			else if (IsInArmorZone(zoneName)) {
				if ((var & ArmorZoneVar) == 0) {
					var |= ArmorZoneVar;

					qs.setQuestVarById(0, var);
					updateQuestStatus(env);
				}
			}

			if ((var & WeaponZoneVar) == WeaponZoneVar) {
				if ((var & OdiumZoneVar) == OdiumZoneVar) {
					if ((var & ArmorZoneVar) == ArmorZoneVar) {
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
					}
				}
			}

			return true;
		}

		return false;
	}

	private boolean IsInWeaponZone(ZoneName zoneName) {
		return zoneName == ZoneName.get(WeaponZoneName);
	}

	private boolean IsInOdiumZone(ZoneName zoneName) {
		return zoneName == ZoneName.get(OdiumZoneName);
	}

	private boolean IsInArmorZone(ZoneName zoneName) {
		return zoneName == ZoneName.get(ArmorZoneName);
	}
}
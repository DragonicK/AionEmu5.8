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
package quest.daevanion;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;

/****/
/** Author Ghostfur & Unknown (Aion-Unique)
/****/

public class _1993Another_Beginning extends QuestHandler
{
	private static final int questId = 1993;
	private final static int dialogs[] = {1013, 1034, 1055, 1076, 5103, 1098, 1119, 1140, 1161, 5104, 1183, 1204, 1225, 1246, 5105, 1268, 1289, 1310, 1331, 5106, 2376, 2461, 2546, 2631, 2632};
	private final static int items[] = {110600834, 113600800, 114600794, 112600785, 111600813, 110300881, 113300860, 114300893, 112300784, 111300834,
	110100931, 113100843, 114100866, 112100790, 111100831, 110500849, 113500827, 114500837, 112500774, 111500821, 110301532, 113301497, 114301526, 112301412, 111301470};
	private int choice = 0;
	private int item;
	
	public _1993Another_Beginning() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(203753).addOnQuestStart(questId);
		qe.registerQuestNpc(203753).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = env.getTargetId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		int dialogId = env.getDialogId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
			if (targetId == 203753) {
				if (dialogId == 59) {
					QuestService.startQuest(env);
					return sendQuestDialog(env, 1011);
				} else {
					return sendQuestStartDialog(env);
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 203753) {
				if (dialogId == 59) {
					return sendQuestDialog(env, 1011);
				} switch (dialogId) {
					case 1012:
					case 1097:
					case 1182:
					case 1267:
						return sendQuestDialog(env, dialogId);
					case 1013:
					case 1034:
					case 1055:
					case 1076:
					case 5103:
					case 1098:
					case 1119:
					case 1140:
					case 1161:
					case 5104:
					case 1183:
					case 1204:
					case 1225:
					case 1246:
					case 5105:
					case 1268:
					case 1289:
					case 1310:
					case 1331:
					case 5106:
					case 2376:
					case 2461:
					case 2546:
					case 2631:
					case 2632: {
						item = getItem(dialogId);
						if (player.getInventory().getItemCountByItemId(item) > 0)
							return sendQuestDialog(env, 1013);
						else
							return sendQuestDialog(env, 1352);
					}
					case 10000:
					case 10001:
					case 10002:
					case 10003: {
						if (player.getInventory().getItemCountByItemId(186000041) == 0)
							return sendQuestDialog(env, 1009);
						changeQuestStep(env, 0, 0, true);
						choice = dialogId - 10000;
						return sendQuestDialog(env, choice + 5);
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203753) {
				removeQuestItem(env, item, 1);
				removeQuestItem(env, 186000041, 1);
				return sendQuestEndDialog(env, choice);
			}
		}
		return false;
	}
	
	private int getItem(int dialogId) {
		int x = 0;
		for (int id : dialogs) {
			if (id == dialogId)
				break;
			x++;
		}
		return (items[x]);
	}
}
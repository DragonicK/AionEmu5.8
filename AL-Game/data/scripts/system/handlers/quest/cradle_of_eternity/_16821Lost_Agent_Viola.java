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
package quest.cradle_of_eternity;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author Ghostfur & Unknown (Aion-Unique)
/****/

public class _16821Lost_Agent_Viola extends QuestHandler
{
    private final static int questId = 16821;
	private final static int[] npcs = {806281, 806426, 220587};
	private final static int[] IDEternity02All = {220458, 220459, 220460, 220462, 220465,
	220467, 220469, 220475, 220476, 220477, 220479, 220670, 220671};
	
    public _16821Lost_Agent_Viola() {
        super(questId);
    }
	
	@Override
	public void register() {
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        } for (int mob: IDEternity02All) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
		qe.registerOnEnterWorld(questId);
		qe.registerQuestNpc(220526).addOnKillEvent(questId); //μ¬μμ? λλ?μ?.
		qe.registerOnEnterZone(ZoneName.get("IDETERNITY_02_Q16821_A_301550000"), questId);
		qe.registerOnEnterZone(ZoneName.get("IDETERNITY_02_Q16821_B_301550000"), questId);
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		int var = qs.getQuestVarById(0);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.START) {
			if (targetId == 806281) { //λ² λμ€.
				switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 0) {
                            return sendQuestDialog(env, 1011);
                        }
					} case SELECT_ACTION_1012: {
						if (var == 0) {
							return sendQuestDialog(env, 1012);
						}
					} case STEP_TO_1: {
                        changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					}
                }
			} if (targetId == 806426) { //λΆλ₯΄κ³ μ€.
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1353);
						}
					} case STEP_TO_2: {
                        changeQuestStep(env, 1, 2, false);
						return closeDialogWindow(env);
					}
				}
			}
		} else if (qs.getStatus() == QuestStatus.REWARD) {
		    QuestService.finishQuest(env);
		}
		return false;
	}
	
	@Override
    public boolean onEnterWorldEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (player.getWorldId() == 301550000) { //μ§μ?μ? μ μ?.
            if (qs == null) {
                env.setQuestId(questId);
                if (QuestService.startQuest(env)) {
					return true;
				}
            }
        }
        return false;
    }
	
	@Override
    public boolean onKillEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        int targetId = env.getTargetId();
		if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (var == 2) {
                int var1 = qs.getQuestVarById(1);
                if (var1 >= 0 && var1 < 34) {
                    return defaultOnKillEvent(env, IDEternity02All, var1, var1 + 1, 1);
                } else if (var1 == 34) {
					qs.setQuestVar(3);
					updateQuestStatus(env);
                    return true;
                }
            } else if (var == 5) {
				switch (targetId) {
                    case 220526: { //μ¬μμ? λλ?μ?.
						qs.setQuestVar(6);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return true;
					}
                }
			}
        }
        return false;
    }
	
	@Override
    public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
			if (zoneName == ZoneName.get("IDETERNITY_02_Q16821_A_301550000")) {
				if (var == 3) {
					playQuestMovie(env, 937);
					changeQuestStep(env, 3, 4, false);
					return true;
				}
			} else if (zoneName == ZoneName.get("IDETERNITY_02_Q16821_B_301550000")) {
				if (var == 4) {
					changeQuestStep(env, 4, 5, false);
					return true;
				}
			}
		}
		return false;
	}
}
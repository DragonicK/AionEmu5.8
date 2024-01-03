package com.aionemu.gameserver.services.player.CreativityPanel;

// Steps per level (points per step)
public enum  CreativityStepsPerLevel {
    LEVEL_66(9),
    LEVEL_67(9),
    LEVEL_68(11),
    LEVEL_69(12),
    LEVEL_70(13),
    LEVEL_71(15),
    LEVEL_72(17),
    LEVEL_73(20),
    LEVEL_74(23),
    LEVEL_75(27),
    LEVEL_76(40),
    LEVEL_77(46),
    LEVEL_78(52),
    LEVEL_79(58),
    LEVEL_80(64),
    LEVEL_81(70),
    LEVEL_82(76),
    LEVEL_83(82),
    LEVEL_84(88);

    public final int steps;
    CreativityStepsPerLevel(int steps) {
        this.steps = steps;
    }
}

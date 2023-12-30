package com.aionemu.gameserver.configs.main;

import com.aionemu.commons.configuration.Property;

public class LunaConfig {
    @Property(key = "gameserver.enable.luna", defaultValue = "true")
    public static boolean ENABLE_LUNA;

    @Property(key = "gameserver.enable.luna.dice", defaultValue = "false")
    public static boolean ENABLE_LUNA_DICE;

    @Property(key = "gameserver.luna.dice.rate.1", defaultValue = "100")
    public static int LUNA_DICE_RATE_1;

    @Property(key = "gameserver.luna.dice.rate.2", defaultValue = "75")
    public static int LUNA_DICE_RATE_2;

    @Property(key = "gameserver.luna.dice.rate.3", defaultValue = "50")
    public static int LUNA_DICE_RATE_3;

    @Property(key = "gameserver.luna.dice.rate.4", defaultValue = "45")
    public static int LUNA_DICE_RATE_4;

    @Property(key = "gameserver.luna.dice.rate.5", defaultValue = "25")
    public static int LUNA_DICE_RATE_5;

    @Property(key = "gameserver.luna.dice.rate.6", defaultValue = "25")
    public static int LUNA_DICE_RATE_6;

    @Property(key = "gameserver.luna.dice.rate.7", defaultValue = "25")
    public static int LUNA_DICE_RATE_7;

    @Property(key = "gameserver.luna.roll.dice.price", defaultValue = "3")
    public static int LUNA_ROLL_DICE_PRICE;

    @Property(key = "gameserver.luna.golden.roll.dice.price", defaultValue = "35")
    public static int LUNA_ROLL_GOLDEN_DICE_PRICE;

}
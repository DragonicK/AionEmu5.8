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
package mysql5;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.database.IUStH;
import com.aionemu.gameserver.dao.PlayerLunaShopDAO;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerLunaShop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by wanke on 13/02/2017.
 */
public class MySQL5PlayerLunaShopDAO extends PlayerLunaShopDAO {

    private static final Logger log = LoggerFactory.getLogger(MySQL5PlayerLunaShopDAO.class);
    public static final String ADD_QUERY = "INSERT INTO `player_luna_shop` (`player_id`, `free_under`, `free_munition`, `free_chest`, `luna_consume`, `luna_consume_count`, `wardrobe_slot`, `muni_keys`, `dice_count`, `is_golden_dice`) VALUES (?,?,?,?,?,?,?,?,?,?)";
    public static final String SELECT_QUERY = "SELECT * FROM `player_luna_shop` WHERE `player_id`=?";
    public static final String DELETE_QUERY = "DELETE FROM `player_luna_shop`";
    public static final String UPDATE_QUERY = "UPDATE player_luna_shop set `free_under`=?, `free_munition`=?, `free_chest`=?, `luna_consume`=?, `luna_consume_count`=?, `wardrobe_slot`=?, `muni_keys`=?, `dice_count`=?, `is_golden_dice`=? WHERE `player_id`=?";

    @Override
    public void load(Player player) {
        Connection con = null;
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement(SELECT_QUERY);
            stmt.setInt(1, player.getObjectId());
            ResultSet rset = stmt.executeQuery();
            if (rset.next()) {
                boolean under = rset.getBoolean("free_under");
                boolean factory = rset.getBoolean("free_munition");
                boolean chest = rset.getBoolean("free_chest");
                int lunaConsume = rset.getInt("luna_consume");
                int muniKeys = rset.getInt("muni_keys");
                int lunaConsumeCount = rset.getInt("luna_consume_count");
                int wardrobeSlot = rset.getInt("wardrobe_slot");
                int diceCount = rset.getInt("dice_count");
                boolean isGoldenDice = rset.getBoolean("is_golden_dice");

                PlayerLunaShop pls = new PlayerLunaShop(under, factory, chest);

                pls.setMuniKeys(muniKeys);
                pls.setLunaConsumePoint(lunaConsume);
                pls.setLunaConsumeCount(lunaConsumeCount);
                pls.setWardrobeSlot(wardrobeSlot);
                pls.setLunaDiceCount(diceCount);
                pls.setLunaGoldenDice(isGoldenDice);

                pls.setPersistentState(PersistentState.UPDATED);
                player.setPlayerLunaShop(pls);
            }
            rset.close();
            stmt.close();
        }
        catch (Exception e) {
            log.error("Could not restore PlayerLunaShop data for playerObjId: " + player.getObjectId() + " from DB: " + e.getMessage(), e);
        }
        finally {
            DatabaseFactory.close(con);
        }
    }

    @Override
    public boolean add(final int playerId, final boolean freeUnderpath, final boolean freeFactory, final boolean freeChest,
                       final int lunaConsume, final int lunaConsumeCount, final int wardrobeSlot, final int muniKeys, final int diceCount, final boolean isGoldenDice) {
        return DB.insertUpdate(ADD_QUERY, new IUStH() {
            @Override
            public void handleInsertUpdate(PreparedStatement ps) throws SQLException {
                ps.setInt(1, playerId);
                ps.setBoolean(2, freeUnderpath);
                ps.setBoolean(3, freeFactory);
                ps.setBoolean(4, freeChest);
                ps.setInt(5, lunaConsume);
                ps.setInt(6, lunaConsumeCount);
                ps.setInt(7, wardrobeSlot);
                ps.setInt(8, muniKeys);
                ps.setInt(9, diceCount);
                ps.setBoolean(10, isGoldenDice);
                ps.execute();
                ps.close();
            }
        });
    }

    @Override
    public boolean delete() {
        return DB.insertUpdate(DELETE_QUERY, new IUStH() {
            @Override
            public void handleInsertUpdate(PreparedStatement ps) throws SQLException {
                ps.execute();
                ps.close();
            }
        });
    }

    @Override
    public boolean store(Player player) {
        Connection con = null;
        boolean insert = false;
        try {
            con = DatabaseFactory.getConnection();
            con.setAutoCommit(false);
            PlayerLunaShop bind = player.getPlayerLunaShop();
            switch (bind.getPersistentState()) {
                case UPDATE_REQUIRED:
                case NEW:
                    insert = updateLunaShop(con, player);
                    log.info("DB updated.");
                    break;
                default:
                    break;
            }
            bind.setPersistentState(PersistentState.UPDATED);
        }
        catch (SQLException e) {
            log.error("Can't open connection to save player updateLunaShop: " + player.getObjectId());
        }
        finally {
            DatabaseFactory.close(con);
        }
        return insert;
    }

    public boolean updateLunaShop(Connection con, Player player) {
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(UPDATE_QUERY);
            PlayerLunaShop lr = player.getPlayerLunaShop();
            stmt.setBoolean(1, lr.isFreeUnderpath());
            stmt.setBoolean(2, lr.isFreeFactory());
            stmt.setBoolean(3, lr.isFreeChest());
            stmt.setInt(4, lr.getLunaConsumePoint());
            stmt.setInt(5, lr.getLunaConsumeCount());
            stmt.setInt(6, lr.getWardrobeSlot());
            stmt.setInt(7, lr.getMuniKeys());
            stmt.setInt(8, lr.getLunaDiceCount());
            stmt.setBoolean(9, lr.isLunaGoldenDice());
            stmt.setInt(10, player.getObjectId());
            stmt.addBatch();
            stmt.executeBatch();
            con.commit();
        }
        catch (Exception e) {
            log.error("Could not update PlayerLunaShop data for player " + player.getObjectId() + " from DB: " + e.getMessage(), e);
            return false;
        }
        finally {
            DatabaseFactory.close(stmt);
        }
        return true;
    }

    @Override
    public boolean setLunaShopByObjId(final int obj, boolean freeUnderpath, boolean freeFactory,
                                      final boolean freeChest, final int lunaConsume, final int lunaConsumeCount, final int wardrobeSlot, final int muniKeys, final int diceCount, final boolean isGoldenDice) {
        Connection con = null;
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement(UPDATE_QUERY);
            stmt.setBoolean(1, freeUnderpath);
            stmt.setBoolean(2, freeFactory);
            stmt.setBoolean(3, freeChest);
            stmt.setInt(4, lunaConsume);
            stmt.setInt(5, lunaConsumeCount);
            stmt.setInt(6, wardrobeSlot);
            stmt.setInt(7, muniKeys);
            stmt.setInt(8, diceCount);
            stmt.setBoolean(9, isGoldenDice);
            stmt.setInt(10, obj);
            stmt.execute();
            stmt.close();
        }
        catch (Exception e) {
            return false;
        }
        finally {
            DatabaseFactory.close(con);
        }
        return true;
    }

    @Override
    public boolean supports(String databaseName, int majorVersion, int minorVersion) {
        return MySQL5DAOUtils.supports(databaseName, majorVersion, minorVersion);
    }
}
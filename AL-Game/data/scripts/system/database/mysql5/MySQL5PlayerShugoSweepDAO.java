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
import com.aionemu.gameserver.dao.PlayerShugoSweepDAO;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerSweep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Wnkrz on 24/10/2017.
 */
public class MySQL5PlayerShugoSweepDAO extends PlayerShugoSweepDAO
{
    private static final Logger log = LoggerFactory.getLogger(MySQL5PlayerLunaShopDAO.class);
    public static final String ADD_QUERY = "INSERT INTO `player_shugo_sweep` (`player_id`, `free_dice`, `sweep_step`, `board_id`, `golden_dice`, `sweep_reset`, `completed_steps`) VALUES (?,?,?,?,?,?,?)";
    public static final String SELECT_QUERY = "SELECT * FROM `player_shugo_sweep` WHERE `player_id`=?";
    public static final String DELETE_QUERY = "DELETE FROM `player_shugo_sweep`";
    public static final String UPDATE_QUERY = "UPDATE player_shugo_sweep set `free_dice`=?, `sweep_step`=?, `board_id`=?, `golden_dice`=?, `sweep_reset`=?, `completed_steps`=? WHERE `player_id`=?";
	
    @Override
    public void load(Player player) {
        Connection con = null;
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement(SELECT_QUERY);
            stmt.setInt(1, player.getObjectId());
            ResultSet rset = stmt.executeQuery();
            if (rset.next()) {
                int dice = rset.getInt("free_dice");
                int step = rset.getInt("sweep_step");
                int boardId = rset.getInt("board_id");
                int goldenDice = rset.getInt("golden_dice");
                int sweepReset = rset.getInt("sweep_reset");
                int completedSteps = rset.getInt("completed_steps");

                PlayerSweep ps = new PlayerSweep(step, dice, boardId, goldenDice, sweepReset, completedSteps);

                ps.setPersistentState(PersistentState.UPDATED);
                player.setPlayerShugoSweep(ps);
            }
            rset.close();
            stmt.close();
        }
        catch (Exception e) {
            log.error("Could not restore PlayerSweep data for playerObjId: " + player.getObjectId() + " from DB: " + e.getMessage(), e);
        }
        finally {
            DatabaseFactory.close(con);
        }
    }
	
    @Override
    public boolean add(final int playerId, final int dice, final int step, final int boardId, final int goldenDice, final int sweepReset, final int completedSteps) {
        return DB.insertUpdate(ADD_QUERY, new IUStH() {
            @Override
            public void handleInsertUpdate(PreparedStatement ps) throws SQLException {
                ps.setInt(1, playerId);
                ps.setInt(2, dice);
                ps.setInt(3, step);
                ps.setInt(4, boardId);
                ps.setInt(5, goldenDice);
                ps.setInt(6, sweepReset);
                ps.setInt(7, completedSteps);
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
            PlayerSweep bind = player.getPlayerShugoSweep();
            switch (bind.getPersistentState()) {
                case UPDATE_REQUIRED:
                case NEW:
                    insert = updatePlayerSweep(con, player);
                    log.info("DB Shugo Sweep updated.");
                    break;
                default:
                    break;
            }
            bind.setPersistentState(PersistentState.UPDATED);
        }
        catch (SQLException e) {
            log.error("Can't open connection to save player updateSweep: " + player.getObjectId());
        }
        finally {
            DatabaseFactory.close(con);
        }
        return insert;
    }
	
    public boolean updatePlayerSweep(Connection con, Player player) {
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(UPDATE_QUERY);
            PlayerSweep lr = player.getPlayerShugoSweep();
            stmt.setInt(1, lr.getFreeDice());
            stmt.setInt(2, lr.getStep());
            stmt.setInt(3, lr.getBoardId());
            stmt.setInt(4, lr.getGoldenDice());
            stmt.setInt(5, lr.getResetBoard());
            stmt.setInt(6, lr.getCompletedSteps());
            stmt.setInt(7, player.getObjectId());
            stmt.addBatch();
            stmt.executeBatch();
            con.commit();
        }
        catch (Exception e) {
            log.error("Could not update PlayerSweep data for player " + player.getObjectId() + " from DB: " + e.getMessage(), e);
            return false;
        }
        finally {
            DatabaseFactory.close(stmt);
        }
        return true;
    }
	
    @Override
    public boolean setShugoSweepByObjId(int obj, final int freeDice, final int step, final int boardId, final int goldenDice, int resetSweep, final int completedSteps) {
        Connection con = null;
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement(UPDATE_QUERY);
            stmt.setInt(1, freeDice);
            stmt.setInt(2, step);
            stmt.setInt(3, boardId);
            stmt.setInt(4, goldenDice);
            stmt.setInt(5, resetSweep);
            stmt.setInt(6, completedSteps);
            stmt.setInt(7, obj);
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
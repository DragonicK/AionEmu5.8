/**
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
package com.aionemu.gameserver.services.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.aionemu.gameserver.model.templates.luna_dice.LunaDiceItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.configs.main.LunaConfig;
import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.services.CronService;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.dao.PlayerLunaShopDAO;
import com.aionemu.gameserver.dao.PlayerWardrobeDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerLunaShop;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.luna.LunaConsumeRewardsTemplate;
import com.aionemu.gameserver.model.templates.recipe.LunaComponent;
import com.aionemu.gameserver.model.templates.recipe.LunaComponentElement;
import com.aionemu.gameserver.model.templates.recipe.LunaTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LUNA_SHOP;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LUNA_SHOP_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.item.ItemPacketService;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemUpdateType;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import javolution.util.FastList;

/****/
/**
 * Reworked by G-Robson26 /
 ****/

public class LunaShopService {

	private Logger log = LoggerFactory.getLogger(LunaShopService.class);
	PlayerWardrobeDAO wDAO = DAOManager.getDAO(PlayerWardrobeDAO.class);
	private boolean dailyGenerated = true;
	private boolean specialGenerated = true;
	private boolean reciveBonus = false;
	private List<Integer> DailyCraft = new ArrayList<Integer>();
	private List<Integer> SpecialCraft = new ArrayList<Integer>();
	private List<Integer> armors = new ArrayList<Integer>();
	private List<Integer> pants = new ArrayList<Integer>();
	private List<Integer> shoes = new ArrayList<Integer>();
	private List<Integer> gloves = new ArrayList<Integer>();
	private List<Integer> shoulders = new ArrayList<Integer>();
	private List<Integer> weapons = new ArrayList<Integer>();

	public void init() {
		log.info("Luna Reset");
		String daily = "0 0 9 1/1 * ? *";
		String weekly = "0 0 9 ? * WED *";
		if (DailyCraft.size() == 0) {
			generateDailyCraft();
		}
		if (SpecialCraft.size() == 0) {
			generateSpecialCraft();
		}

		CronService.getInstance().schedule(new Runnable() {
			public void run() {
				dailyGenerated = false;
				generateDailyCraft();
				resetFreeLuna();
			}
		}, daily);

		CronService.getInstance().schedule(new Runnable() {
			public void run() {
				specialGenerated = false;
				generateSpecialCraft();
			}
		}, weekly);
	}

	public void generateSpecialCraft() {
		if (SpecialCraft.size() > 0) {
			SpecialCraft.clear();
		}
		armors.add(10029);
		armors.add(10031);
		armors.add(10033);
		armors.add(10035);
		pants.add(10037);
		pants.add(10039);
		pants.add(10041);
		pants.add(10043);
		shoes.add(10061);
		shoes.add(10063);
		shoes.add(10065);
		shoes.add(10067);
		gloves.add(10053);
		gloves.add(10055);
		gloves.add(10057);
		gloves.add(10059);
		shoulders.add(10045);
		shoulders.add(10047);
		shoulders.add(10049);
		shoulders.add(10051);
		weapons.add(10021);
		weapons.add(10017);
		weapons.add(10025);
		weapons.add(10005);
		weapons.add(10011);
		weapons.add(10023);
		weapons.add(10003);
		weapons.add(10007);
		weapons.add(10019);
		weapons.add(10013);
		weapons.add(10027);
		weapons.add(10009);
		weapons.add(10015);
		weapons.add(10001);
		int rnd = Rnd.get(1, 6);
		switch (rnd) {
		case 1:
			SpecialCraft.addAll(weapons);
			break;
		case 2:
			SpecialCraft.addAll(armors);
			break;
		case 3:
			SpecialCraft.addAll(pants);
			break;
		case 4:
			SpecialCraft.addAll(shoes);
			break;
		case 5:
			SpecialCraft.addAll(gloves);
			break;
		case 6:
			SpecialCraft.addAll(shoulders);
			break;
		}
		if (!specialGenerated) {
			updateSpecialCraft();
		}
	}

	public void resetFreeLuna() {
		updateFreeLuna();
	}

	public void sendSpecialCraft(Player player) {
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(2, 0, SpecialCraft));
	}

	private void updateSpecialCraft() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(2, 0, SpecialCraft));
			}
		});
	}

	private void updateFreeLuna() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {

			@Override
			public void visit(Player player) {
				PlayerLunaShop pls = player.getPlayerLunaShop();

				pls.setFreeChest(true);
				pls.setFreeFactory(true);
				pls.setFreeUnderpath(true);

				pls.setPersistentState(PersistentState.UPDATE_REQUIRED);

				DAOManager.getDAO(PlayerLunaShopDAO.class).add(player.getObjectId(), pls.isFreeUnderpath(),
						pls.isFreeFactory(), pls.isFreeChest(), pls.getLunaConsumePoint(), pls.getLunaConsumePoint(),
						pls.getWardrobeSlot(), pls.getMuniKeys(), pls.getLunaDiceCount(), pls.isLunaGoldenDice());
			}
		});
	}

	public void generateDailyCraft() {
		if (DailyCraft.size() > 0) {
			dailyGenerated = false;
			DailyCraft.clear();
		}

		FastList<LunaTemplate> test = DataManager.LUNA_DATA.getLunaTemplatesAny();
		Random rand = new Random();
		for (int i = 0; i < 5; i++) {
			int randomIndex = rand.nextInt(test.size());
			LunaTemplate randomElement = test.get(randomIndex);
			DailyCraft.add(randomElement.getId());
		}

		if (!dailyGenerated) {
			updateDailyCraft();
		}
	}

	public void sendDailyCraft(Player player) {
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(DailyCraft));
	}

	private void updateDailyCraft() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(DailyCraft));
				dailyGenerated = true;
			}
		});
	}

	public void lunaPointController(Player player, int point) {
		player.setLunaAccount(point);
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(0, player.getLunaAccount()));
	}

	public void muniKeysController(Player player, int keys) {
		player.getPlayerLunaShop().setMuniKeys(keys);
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(4));
	}

	public void onLogin(Player player) {
		if (player.getPlayerLunaShop() == null) {
			PlayerLunaShop pls = new PlayerLunaShop(true, true, true);
			pls.setPersistentState(PersistentState.UPDATE_REQUIRED);
			player.setPlayerLunaShop(pls);
			DAOManager.getDAO(PlayerLunaShopDAO.class).add(player.getObjectId(), pls.isFreeUnderpath(),
					pls.isFreeFactory(), pls.isFreeChest(), pls.getLunaConsumePoint(), pls.getLunaConsumeCount(),
					pls.getWardrobeSlot(), pls.getMuniKeys(), pls.getLunaDiceCount(), pls.isLunaGoldenDice());
		}

		// PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(6));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(7));

		sendSpecialCraft(player);
		sendDailyCraft(player);

		for (int i = 0; i < 9; i++) {
			PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(8, i, 0));
		}
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(0, player.getLunaAccount()));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(5));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(4, player.getPlayerLunaShop().getMuniKeys()));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(9, 0));

		if (!player.getPlayerLunaShop().isFreeUnderpath()) {
			PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(1, 1, 45));
		}
		if (!player.getPlayerLunaShop().isFreeFactory()) {
			PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(1, 1, 47));
		}
		if (!player.getPlayerLunaShop().isFreeChest()) {
			PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(1, 1, 1));
		}

		int diceCount = player.getPlayerLunaShop().getLunaDiceCount();

		if (diceCount > 0) {
			if(player.getPlayerLunaShop().isLunaGoldenDice()) {
				PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(1, 7, 74));
			} else {
				PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(1, 7, 72));
			}

			boolean isDiceFinish = diceCount == 6 || diceCount == 7;
			PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(15, isDiceFinish));
		}
	}

	public void specialDesign(Player player, int recipeId) {
		LunaTemplate recipe = DataManager.LUNA_DATA.getLunaTemplateById(recipeId);
		int product_id = recipe.getProductid();
		int quantity = recipe.getQuantity();
		ItemTemplate item = DataManager.ITEM_DATA.getItemTemplate(product_id);
		boolean isSuccess = isSuccess(player, recipeId);
		if (isSuccess) {
			for (LunaComponent lc : recipe.getLunaComponent()) {
				for (LunaComponentElement a : lc.getComponents()) {
					if (!player.getInventory().decreaseByItemId(a.getItemid(), a.getQuantity())) {
						System.out.println("!!! Possible item hack CHEATER(?) !!!");
						PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(2, item, 1));
						PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(3, product_id, quantity, false));
						return;
					}
				}
			}
			PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(2, item, 0));
			PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(3, product_id, quantity, true));
			ItemService.addItem(player, product_id, quantity);
		} else {
			for (LunaComponent lc : recipe.getLunaComponent()) {
				for (LunaComponentElement a : lc.getComponents()) {
					if (!player.getInventory().decreaseByItemId(a.getItemid(), a.getQuantity())) {
						System.out.println("!!! Possible item hack CHEATER(?) !!!");
						PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(2, item, 1));
						PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(3, product_id, quantity, false));
						return;
					}
				}
			}
			PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(2, item, 1));
			PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(3, product_id, quantity, false));
		}
	}

	public void craftBox(Player player) {
		int itemId = 188055460;
		if (player.getPlayerLunaShop().isFreeChest()) {
			player.getPlayerLunaShop().setFreeChest(false);
		} else {
			player.setLunaAccount(player.getLunaAccount() - 2);
			PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(0));
		}
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(5));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(3, itemId, 1, true));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(1, 1, 1));
		ItemService.addItem(player, itemId, 1); // Luna Material Chest
	}

	private boolean isSuccess(Player player, int recipeId) {
		LunaTemplate recipe = DataManager.LUNA_DATA.getLunaTemplateById(recipeId);
		boolean result = false;
		float random = Rnd.get(1, 100);
		if (recipe.getRate() == 100) {
			result = true;
		} else if (recipe.getRate() < 100) {
			if (random <= recipe.getRate()) {
				result = true;
			} else {
				result = false;
			}
		}
		return result;
	}

	public void buyMaterials(Player player, int itemId, long count) {
		ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(itemId);
		int lunaPrice = itemTemplate.getLunaPrice();
		long price = count * lunaPrice;
		ItemService.addItem(player, itemId, count);
		player.setLunaAccount((player.getLunaAccount() - price));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(0, player.getLunaAccount()));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(4, player.getPlayerLunaShop().getMuniKeys()));
	}

	public void dorinerkWardrobeLoad(Player player) {
		int size = DAOManager.getDAO(PlayerWardrobeDAO.class).getItemSize(player.getObjectId());
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(8, player.getPlayerLunaShop().getWardrobeSlot(), size));
	}

	public void dorinerkWardrobeAct(Player player, int applySlot, int itemObjId) {
		int itemId = player.getInventory().getItemByObjId(itemObjId).getItemId();
		int itemOnDB = DAOManager.getDAO(PlayerWardrobeDAO.class).getWardrobeItemBySlot(player.getObjectId(),
				applySlot);
		if (itemOnDB != 0) {
			DAOManager.getDAO(PlayerWardrobeDAO.class).delete(player.getObjectId(), itemOnDB);
			player.setLunaAccount(player.getLunaAccount() - 10);
			player.getWardrobe().addItem(player, itemId, applySlot, 0);
			PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(0, player.getLunaAccount()));
		} else {
			player.getWardrobe().addItem(player, itemId, applySlot, 0);
		}
		player.getInventory().decreaseByObjectId(itemObjId, 1);
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(10, 0x00, applySlot, itemId, 1));
	}

	public void dorinerkWardrobeModifyAppearance(Player player, int applySlot, int itemObjId, int isRemoving) {
		int itemId = DAOManager.getDAO(PlayerWardrobeDAO.class).getWardrobeItemBySlot(player.getObjectId(), applySlot);
		int reskinCount = DAOManager.getDAO(PlayerWardrobeDAO.class).getReskinCountBySlot(player.getObjectId(),
				applySlot);
		ItemTemplate it = DataManager.ITEM_DATA.getItemTemplate(itemId);
		Storage inventory = player.getInventory();
		Item keepItem = inventory.getItemByObjId(itemObjId);

		if (isRemoving == 1) {
			ItemTemplate defaultTemplate = DataManager.ITEM_DATA.getItemTemplate(itemObjId);
			keepItem.setItemSkinTemplate(defaultTemplate);
			keepItem.setLunaReskin(false);

			if (!keepItem.getItemTemplate().isItemDyePermitted()) {
				keepItem.setItemColor(0);
			}
		}
		else {
			if (reskinCount != 0) {
				DAOManager.getDAO(PlayerWardrobeDAO.class).setReskinCountBySlot(player.getObjectId(), applySlot,
						reskinCount + 1);
				player.setLunaAccount(player.getLunaAccount() - 12);
				keepItem.setItemSkinTemplate(it);

				if (!keepItem.getItemTemplate().isItemDyePermitted()) {
					keepItem.setItemColor(0);
				}
				keepItem.setLunaReskin(true);
				PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(0, player.getLunaAccount()));
			} else {
				DAOManager.getDAO(PlayerWardrobeDAO.class).setReskinCountBySlot(player.getObjectId(), applySlot,
						reskinCount + 1);
				keepItem.setItemSkinTemplate(it);
				if (!keepItem.getItemTemplate().isItemDyePermitted()) {
					keepItem.setItemColor(0);
				}
				keepItem.setLunaReskin(true);
			}
		}

		ItemPacketService.updateItemAfterInfoChange(player, keepItem, ItemUpdateType.STATS_CHANGE);
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE
				.STR_CHANGE_ITEM_SKIN_SUCCEED(new DescriptionId(keepItem.getItemTemplate().getNameId())));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(11, applySlot));
	}

	public void dorinerkWardrobeExtendSlots(Player player) {
		PlayerLunaShop lunaShop = player.getPlayerLunaShop();

		int currentSlot = lunaShop.getWardrobeSlot();
		int size = DAOManager.getDAO(PlayerWardrobeDAO.class).getItemSize(player.getObjectId());
		lunaShop.setWardrobeSlot(currentSlot + 1);
		player.setLunaAccount(player.getLunaAccount() - wardrobePrice(currentSlot + 1));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(9, lunaShop.getWardrobeSlot(), size));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(0, player.getLunaAccount()));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(5, player.getLunaAccount()));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(4, lunaShop.getMuniKeys()));
	}

	public void takiAdventure(Player player, int indun_id) {
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(14, indun_id));
	}

	public void takiAdventureTeleport(Player player, int indun_unk, int indun_id) {
		if (indun_id == 1) {
			if (player.getPlayerLunaShop().isFreeUnderpath()) {
				WorldMapInstance contaminatedUnderpath = InstanceService.getNextAvailableInstance(301630000);
				InstanceService.registerPlayerWithInstance(contaminatedUnderpath, player);
				TeleportService2.teleportTo(player, 301630000, contaminatedUnderpath.getInstanceId(), 230f, 169f, 164f,
						(byte) 60);
				player.getPlayerLunaShop().setLunaShopByObjId(player.getObjectId());
				PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(0));
				PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(1, 1, 45));
				PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(0, 0));
				player.getPlayerLunaShop().setFreeUnderpath(false);
			} else {
				WorldMapInstance contaminatedUnderpath = InstanceService.getNextAvailableInstance(301630000);
				InstanceService.registerPlayerWithInstance(contaminatedUnderpath, player);
				TeleportService2.teleportTo(player, 301630000, contaminatedUnderpath.getInstanceId(), 230f, 169f, 164f,
						(byte) 60);
				player.setLunaAccount(player.getLunaAccount() - 20);
				PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(0));
				PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(1, 1, 45));
				PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(0, 0));
			}
		}
		if (indun_id == 2) {
			if (player.getPlayerLunaShop().isFreeFactory()) {
				WorldMapInstance secretMunitionsFactory = InstanceService.getNextAvailableInstance(301640000);
				InstanceService.registerPlayerWithInstance(secretMunitionsFactory, player);
				TeleportService2.teleportTo(player, 301640000, secretMunitionsFactory.getInstanceId(), 400.3279f,
						290.5061f, 198.64015f, (byte) 60);
				player.getPlayerLunaShop().setLunaShopByObjId(player.getObjectId());
				PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(0));
				PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(1, 1, 47));
				PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(0, 0));
				player.getPlayerLunaShop().setFreeFactory(false);
			} else {
				WorldMapInstance secretMunitionsFactory = InstanceService.getNextAvailableInstance(301640000);
				InstanceService.registerPlayerWithInstance(secretMunitionsFactory, player);
				TeleportService2.teleportTo(player, 301640000, secretMunitionsFactory.getInstanceId(), 400.3279f,
						290.5061f, 198.64015f, (byte) 60);
				player.setLunaAccount(player.getLunaAccount() - 20);
				PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(0));
				PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(1, 1, 47));
				PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(0, 0));
			}
		}
	}

	public void teleport(Player player, int action, int teleportId) {
		switch (action) {
		case 6:
			PacketSendUtility.sendMessage(player, "teleportId : " + teleportId);
			PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(6));
			break;
		case 7:
			PacketSendUtility.sendMessage(player, "teleportId : " + teleportId);
			PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(7));
			break;
		}
	}

	public void munirunerksTreasureChamber(final Player player) {
		PlayerLunaShop lunaShop = player.getPlayerLunaShop();

		HashMap<Integer, Long> hm = new HashMap<Integer, Long>();
		hm.put(188054633, (long) 1); // [Event] Special Head Executor Weapon Box
		hm.put(188054634, (long) 1); // [Event] Special Head Executor Armor Box
		hm.put(166030013, (long) 1); // [Event] Tempering Solution
		hm.put(166020003, (long) 1); // [Event] Omega Enchantment Stone
		hm.put(188054122, (long) 1); // Major Stigma Bundle
		hm.put(188055183, (long) 1); // Major Felicitous Socketing Box (Mythic)
		hm.put(188054287, (long) 1); // Greater Stigma Bundle
		hm.put(188054462, (long) 1); // Illusion Godstone Bundle
		hm.put(188052639, (long) 1); // [Event] Heroic Godstone Bundle
		hm.put(169405339, (long) 10); // Pallasite Crystal
		hm.put(164000076, (long) 10); // Greater Running Scroll
		hm.put(164000134, (long) 10); // Greater Awakening Scroll
		hm.put(166000196, (long) 3); // Enchantment Stone
		hm.put(186000242, (long) 2); // Ceramium Medal
		hm.put(186000051, (long) 2); // Major Ancient Crown
		hm.put(188055168, (long) 10); // [Event] Blood Medal Box
		hm.put(188054283, (long) 30); // Blood Mark Box
		hm.put(188054463, (long) 1); // [Event] Fabled Godstone Bundle
		hm.put(188053002, (long) 1); // [Event] Noble Composite Manastone Bundle
		hm.put(188100335, (long) 2000); // Enchantment Stone Dust
		hm.put(164000073, (long) 10); // Greater Courage Scroll
		hm.put(160002497, (long) 1); // Fresh Oily Plucar Dragon Salad
		hm.put(160002499, (long) 1); // Fresh Oily Plucar Dragon Soup

		if (lunaShop.getMuniKeys() > 0) {
			lunaShop.setMuniKeys(lunaShop.getMuniKeys() - 1);
		} else {
			player.setLunaAccount(player.getLunaAccount() - 5);
			lunaShop.setLunaConsumePoint(lunaShop.getLunaConsumePoint() + 5);
			switch (lunaShop.getLunaConsumePoint()) {
			case 25:
				reciveBonus = true;
				lunaShop.setLunaConsumeCount(1);
				break;
			case 50:
				reciveBonus = true;
				lunaShop.setLunaConsumeCount(2);
				break;
			case 100:
				reciveBonus = true;
				lunaShop.setLunaConsumeCount(3);
				muniKeysController(player, lunaShop.getMuniKeys() + 1);
				break;
			case 150:
				reciveBonus = true;
				lunaShop.setLunaConsumeCount(4);
				muniKeysController(player, lunaShop.getMuniKeys() + 1);
				break;
			case 300:
				reciveBonus = true;
				lunaShop.setLunaConsumeCount(5);
				muniKeysController(player, lunaShop.getMuniKeys() + 2);
				break;
			case 500:
				reciveBonus = true;
				lunaShop.setLunaConsumeCount(6);
				muniKeysController(player, lunaShop.getMuniKeys() + 2);
				break;
			case 1000:
				reciveBonus = true;
				lunaShop.setLunaConsumeCount(7);
				muniKeysController(player, lunaShop.getMuniKeys() + 3);
				break;
			default:
				reciveBonus = false;
				break;
			}
			if (reciveBonus) {
				LunaConsumeRewardsTemplate lt = DataManager.LUNA_CONSUME_REWARDS_DATA
						.getLunaConsumeRewardsId(lunaShop.getLunaConsumeCount());
				ItemService.addItem(player, lt.getCreateItemId(), lt.getCreateItemCount());
			}
		}

		final HashMap<Integer, Long> mt = new HashMap<Integer, Long>();
		for (int i = 0; i < 3; i++) {
			Object[] crunchifyKeys = hm.keySet().toArray();
			Object key = crunchifyKeys[new Random().nextInt(crunchifyKeys.length)];
			mt.put((int) key, (long) hm.get(key));
		}
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				for (Map.Entry<Integer, Long> e : mt.entrySet()) {
					ItemService.addItem(player, e.getKey(), e.getValue());
					ItemTemplate t = DataManager.ITEM_DATA.getItemTemplate(e.getKey());
					if (e.getValue() == 1) {
						PacketSendUtility.sendPacket(player,
								SM_SYSTEM_MESSAGE.STR_MSG_LUNA_REWARD_GOTCHA_ITEM(t.getNameId()));
					} else if (e.getValue() > 1) {
						PacketSendUtility.sendPacket(player,
								SM_SYSTEM_MESSAGE.STR_MSG_LUNA_REWARD_GOTCHA_ITEM_MULTI(e.getValue(), t.getNameId()));
					}
				}
				PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(mt));
			}
		}, 1);
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(5));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(4, lunaShop.getMuniKeys()));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(0, player.getLunaAccount()));
		// As you spend Luna, you can earn keys to open Munirunerks Treasure Chest.
		// If you do not have any keys, you can spend 3 Luna to open a chest
		// immediately.
		// The Luna you spend on opening chests will also count towards your Luna
		// Rewards!
	}

	public void onLogout(Player player) {
		PlayerLunaShop pls = player.getPlayerLunaShop();
		pls.setPersistentState(PersistentState.UPDATE_REQUIRED);
		DAOManager.getDAO(PlayerLunaShopDAO.class).store(player);
	}

	private int wardrobePrice(int WardrobeSlot) { // Done
		switch (WardrobeSlot) {
		case 1:
		case 2:
		case 3:
		case 4:
			return 10;
		case 5:
		case 6:
		case 7:
		case 8:
			return 12;
		}
		return 0;
	}

	public void diceGame(Player player) {
		if (!LunaConfig.ENABLE_LUNA_DICE) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1404432, new Object[0]));
			return;
		}

		PlayerLunaShop lunaShop = player.getPlayerLunaShop();

		int price = LunaConfig.LUNA_ROLL_DICE_PRICE;

		if (lunaShop.isLunaGoldenDice()) {
			price = LunaConfig.LUNA_ROLL_GOLDEN_DICE_PRICE;
		}

		if ((player.getLunaAccount() < price)) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1403481, new Object[0]));
			return;
		}

		player.getPlayerAccount().setLuna(player.getLunaAccount() - price);
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(0));

		boolean isDiceFinish = false;

		int rndSuccess = Rnd.get(1, 100);
		int diceCount = lunaShop.getLunaDiceCount();

		if (lunaShop.getLunaDiceCount() < 5) {
			int randomNumber = Rnd.get(1, 5);

			switch (randomNumber) {
				case 1:
					if (rndSuccess <= LunaConfig.LUNA_DICE_RATE_1) {
						if (diceCount <= 1) {
							lunaShop.setLunaDiceCount(1);
						}
					}
					break;

				case 2:
					if (rndSuccess <= LunaConfig.LUNA_DICE_RATE_2) {
						if (diceCount <= 2) {
							lunaShop.setLunaDiceCount(2);
						}
					}
					break;
				case 3:
					if (rndSuccess <= LunaConfig.LUNA_DICE_RATE_3) {
						if (diceCount <= 3) {
							lunaShop.setLunaDiceCount(3);
						}
					}
					break;
				case 4:
					if (rndSuccess <= LunaConfig.LUNA_DICE_RATE_4) {
						if (diceCount <= 4) {
							lunaShop.setLunaDiceCount(4);
						}
					}
					break;
				case 5:
					if (rndSuccess <= LunaConfig.LUNA_DICE_RATE_5) {
						if (diceCount <= 5) {
							lunaShop.setLunaGoldenDice(true);
							lunaShop.setLunaDiceCount(5);
						}
					}
					break;
			}
		}
		else {
			boolean isFailed = true;

			int rndGolden = Rnd.get(1, 100);

			// Try to roll Dice 7.
			if (rndGolden <= LunaConfig.LUNA_DICE_RATE_7) {
				lunaShop.setLunaDiceCount(7);
				isFailed = false;
			}

			// If dice 7 failed, now we try 6.
			if (isFailed) {
				rndGolden = Rnd.get(1, 100);

				if (rndGolden <= LunaConfig.LUNA_DICE_RATE_6) {
					lunaShop.setLunaDiceCount(6);
					isFailed = false;
				}
			}

			isDiceFinish = true;

			if (isFailed) {
				lunaShop.setLunaDiceCount(5);
			}
		}

		// 0 = Count 1 # level 1 box
		// 1 = Count 2 # level 1 box
		// 2 = Count 3 # level 1 box
		// 3 = Count 1 # level 2 box
		// 4 = Count 2 # level 2 box
		// 5 = Count 1 # level 3 box
		// 6 = Count 1 # level 4 box
		// 7 = Count 1 # level 5 box

		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(15, isDiceFinish));

		sendLunaPrice(player);
	}

	public void lunaDiceReward(Player player) {
		PlayerLunaShop lunaShop = player.getPlayerLunaShop();

		int rollDiceCount = lunaShop.getLunaDiceCount();
		boolean isLunaGoldenDice = lunaShop.isLunaGoldenDice();

		List<LunaDiceItem> items = null;

		lunaShop.setLunaDiceCount(0);
		lunaShop.setLunaGoldenDice(false);

		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(16, true));

		switch (rollDiceCount) {
			case 0:
				// 0 = Count 1 # level 1 box
				items = DataManager.LUNA_DICE.getLunaDiceTabById(1);

				givePlayerReward(player, items);
				break;
			case 1:
				// 1 = Count 2 # level 1 box
				items = DataManager.LUNA_DICE.getLunaDiceTabById(1);

				givePlayerReward(player, items);
				givePlayerReward(player, items);
				break;
			case 2:
				// 2 = Count 3 # level 1 box
				items = DataManager.LUNA_DICE.getLunaDiceTabById(1);

				givePlayerReward(player, items);
				givePlayerReward(player, items);
				givePlayerReward(player, items);
				break;
			case 3:
				// 3 = Count 1 # level 2 box
				items = DataManager.LUNA_DICE.getLunaDiceTabById(2);

				givePlayerReward(player, items);
				break;
			case 4:
				// 4 = Count 2 # level 2 box
				items = DataManager.LUNA_DICE.getLunaDiceTabById(2);

				givePlayerReward(player, items);
				givePlayerReward(player, items);
				break;
			case 5:
				// 5 = Count 1 # level 3 box
				items = DataManager.LUNA_DICE.getLunaDiceTabById(3);

				// If it failed, 2 box level 3
				if (isLunaGoldenDice) {
					givePlayerReward(player, items);
					givePlayerReward(player, items);
				}
				else {
					givePlayerReward(player, items);
				}

				break;
			case 6:
				// 6 = Count 1 # level 4 box
				items = DataManager.LUNA_DICE.getLunaDiceTabById(4);

				givePlayerReward(player, items);
				break;
			case 7:
				// 7 = Count 1 # level 5 box
				items = DataManager.LUNA_DICE.getLunaDiceTabById(5);

				givePlayerReward(player, items);
				break;
		}

		sendLunaPrice(player);
	}

	private void givePlayerReward(Player player, List<LunaDiceItem> items) {
		LunaDiceItem item = items.get(Rnd.get(1, items.size() - 1));

		int rewardId = item.getItemId();
		int rewardCount = item.getCount();

		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(16, 1, rewardId, rewardCount));

		ItemService.addItem(player, rewardId, rewardCount);
	}

	public void sendLunaPrice(Player player) {
		if(player.getPlayerLunaShop().isLunaGoldenDice()) {
			PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(1, 7, 74));
		} else {
			PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(1, 7, 72));
		}
	}

	public static LunaShopService getInstance() {
		return NewSingletonHolder.INSTANCE;
	}

	private static class NewSingletonHolder {

		private static final LunaShopService INSTANCE = new LunaShopService();
	}
}
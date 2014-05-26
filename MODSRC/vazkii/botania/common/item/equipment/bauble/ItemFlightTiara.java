/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under a
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 License
 * (http://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB)
 * 
 * File Created @ [May 26, 2014, 4:05:50 PM (GMT)]
 */
package vazkii.botania.common.item.equipment.bauble;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.common.lib.LibItemNames;
import baubles.api.BaubleType;
import baubles.common.lib.PlayerHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

public class ItemFlightTiara extends ItemBauble {

	public static List<String> playersWithFlight = new ArrayList();
	private static final int COST = 20;
	
	public ItemFlightTiara() {
		super(LibItemNames.FLIGHT_TIARA);
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);
	}

	@Override
	public BaubleType getBaubleType(ItemStack arg0) {
		return BaubleType.AMULET;
	}

	@SubscribeEvent
	public void updatePlayerFlyStatus(LivingUpdateEvent event) {
		if(event.entityLiving instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.entityLiving;

			ItemStack armor = PlayerHandler.getPlayerBaubles(player).getStackInSlot(0);
			if(playersWithFlight.contains(playerStr(player))) {
				if(shouldPlayerHaveFlight(player)) {
					player.capabilities.allowFlying = true;
					if(player.capabilities.isFlying)
						ManaItemHandler.requestManaExact(armor, player, COST, true);
				} else {
					if(!player.capabilities.isCreativeMode) {
						player.capabilities.allowFlying = false;
						player.capabilities.isFlying = false;
						player.capabilities.disableDamage = false;
					}
					playersWithFlight.remove(playerStr(player));
				}
			} else if(shouldPlayerHaveFlight(player)) {
				playersWithFlight.add(playerStr(player));
				player.capabilities.allowFlying = true;
			}
		}
	}
	
	@SubscribeEvent
	public void playerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
		String username = event.player.getGameProfile().getName();
		ItemFlightTiara.playersWithFlight.remove(username + ":false");
		ItemFlightTiara.playersWithFlight.remove(username + ":true");
	}

	public static String playerStr(EntityPlayer player) {
		return player.getGameProfile().getName() + ":" + player.worldObj.isRemote;
	}

	private boolean shouldPlayerHaveFlight(EntityPlayer player) {
		ItemStack armor = PlayerHandler.getPlayerBaubles(player).getStackInSlot(0);
		return armor != null && armor.getItem() == this && ManaItemHandler.requestManaExact(armor, player, COST, false);
	}


}

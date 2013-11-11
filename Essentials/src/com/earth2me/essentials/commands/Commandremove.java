package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Mob;
import com.earth2me.essentials.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.bukkit.Chunk;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Boat;
import org.bukkit.entity.ComplexLivingEntity;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Flying;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Monster;
import org.bukkit.entity.NPC;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.WaterMob;

// This could be rewritten in a simpler form if we made a mapping of all Entity names to their types (which would also provide possible mod support)

public class Commandremove extends EssentialsCommand
{

	public Commandremove()
	{
		super("remove");
	}

	@Override
	protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		World world = user.getWorld();
		int radius = 0;
		boolean removeTamed = false;
		if (args.length >= 2)
		{
			try
			{
				radius = Integer.parseInt(args[1]);
			}
			catch (NumberFormatException e)
			{
				throw new Exception(_("numberRequired"), e);
			}
		}
		if (args.length >= 3)
		{
			world = ess.getWorld(args[2]);
		}
		if (args.length >= 4 && args[3].equalsIgnoreCase("true"))
		{
			removeTamed = true;
		}
		parseCommand(server, user.getSource(), args, world, radius, removeTamed);

	}

	@Override
	protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 2)
		{
			throw new NotEnoughArgumentsException();
		}
		World world = ess.getWorld(args[1]);
		boolean removeTamed = false;
		if (args.length >= 3 && args[2].equalsIgnoreCase("true"))
		{
			removeTamed = true;
		}
		parseCommand(server, sender, args, world, 0, removeTamed);
	}

	private void parseCommand(Server server, CommandSource sender, String[] args, World world, int radius, boolean removeTamed) throws Exception
	{
		List<String> types = new ArrayList<String>();
		List<String> customTypes = new ArrayList<String>();
		boolean isCustom = false;

		if (args.length > 0 && (args[0].contentEquals("*") || args[0].contentEquals("all")))
		{
			types.add(0, "ALL");
		}
		else
		{
			for (String s : args[0].split(":"))
			{
				ToRemove toRemove;
				try
				{
					toRemove = ToRemove.valueOf(s.toUpperCase(Locale.ENGLISH));
				}
				catch (Exception e)
				{
					try
					{
						toRemove = ToRemove.valueOf(s.concat("S").toUpperCase(Locale.ENGLISH));
					}
					catch (Exception ee)
					{
						toRemove = ToRemove.CUSTOM;
						isCustom = true;
						customTypes.add(s);
					}
				}
				types.add(toRemove.toString());
			}
		}
		removeHandler(sender, types, customTypes, world, radius, removeTamed, isCustom);
	}

	private void removeHandler(CommandSource sender, List<String> types, List<String> customTypes, World world, int radius, boolean removeTamed, boolean isCustom)
	{
		int removed = 0;
		if (radius > 0)
		{
			radius *= radius;
		}
		for (Chunk chunk : world.getLoadedChunks())
		{
			for (Entity e : chunk.getEntities())
			{
				if (radius > 0)
				{
					if (sender.getPlayer().getLocation().distanceSquared(e.getLocation()) > radius)
					{
						continue;
					}
				}
				if (e instanceof HumanEntity)
				{
					continue;
				}
				if (e instanceof Tameable && removeTamed == false)
				{
					if (((Tameable)e).isTamed())
					{
						continue;
					}
				}
				for (String s : types)
				{

					ToRemove toRemove = ToRemove.valueOf(s);
					switch (toRemove)
					{
					case DROPS:
						if (e instanceof Item)
						{
							e.remove();
							removed++;
						}
						;
						break;
					case ARROWS:
						if (e instanceof Projectile)
						{
							e.remove();
							removed++;
						}
						break;
					case BOATS:
						if (e instanceof Boat)
						{
							e.remove();
							removed++;
						}
						break;
					case MINECARTS:
						if (e instanceof Minecart)
						{
							e.remove();
							removed++;
						}
						break;
					case XP:
						if (e instanceof ExperienceOrb)
						{
							e.remove();
							removed++;
						}
						break;
					case PAINTINGS:
						if (e instanceof Painting)
						{
							e.remove();
							removed++;
						}
						break;
					case ITEMFRAMES:
						if (e instanceof ItemFrame)
						{
							e.remove();
							removed++;
						}
						break;
					case ENDERCRYSTALS:
						if (e instanceof EnderCrystal)
						{
							e.remove();
							removed++;
						}
						break;
					case AMBIENT:
						if (e instanceof Flying)
						{
							e.remove();
							removed++;
						}
						break;
					case HOSTILE:
					case MONSTERS:
						if (e instanceof Monster || e instanceof ComplexLivingEntity || e instanceof Flying || e instanceof Slime)
						{
							e.remove();
							removed++;
						}
						break;
					case PASSIVE:
					case ANIMALS:
						if (e instanceof Animals || e instanceof NPC || e instanceof Snowman || e instanceof WaterMob)
						{
							e.remove();
							removed++;
						}
						break;
					case MOBS:
						if (e instanceof Animals || e instanceof NPC || e instanceof Snowman || e instanceof WaterMob
							|| e instanceof Monster || e instanceof ComplexLivingEntity || e instanceof Flying || e instanceof Slime)
						{
							e.remove();
							removed++;
						}
						break;
					case ENTITIES:
					case ALL:
						if (e instanceof Entity)
						{
							e.remove();
							removed++;
						}
						break;
					case CUSTOM:
						for (String type : customTypes)
						{
							if (e.getType() == Mob.fromName(type).getType())
							{
								e.remove();
								removed++;
							}
						}
						break;
					}
				}
			}
		}
		sender.sendMessage(_("removed", removed));
	}


	private enum ToRemove
	{
		DROPS,
		ARROWS,
		BOATS,
		MINECARTS,
		XP,
		PAINTINGS,
		ITEMFRAMES,
		ENDERCRYSTALS,
		HOSTILE,
		MONSTERS,
		PASSIVE,
		ANIMALS,
		AMBIENT,
		MOBS,
		ENTITIES,
		ALL,
		CUSTOM
	}
}

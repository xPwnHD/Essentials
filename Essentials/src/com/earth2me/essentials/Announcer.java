package com.earth2me.essentials;

import com.earth2me.essentials.textreader.BookInput;
import com.earth2me.essentials.textreader.IText;
import com.earth2me.essentials.textreader.KeywordReplacer;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.ess3.api.IEssentials;


//Todo: make keyword replacement work
public class Announcer
{
	private final List<String> messages;
	private long interval;
	private final IEssentials ess;
	private final IText textInput;
	private final IText output;
	private final boolean random;
	private int messageCounter;

	public Announcer(final IEssentials ess) throws IOException, Exception
	{
		this.messages = new ArrayList<String>();
		this.ess = ess;
		this.messageCounter = 0;
		this.interval = ess.getSettings().getMessageInterval();
		this.random = ess.getSettings().isAnnouncerRandom();
		if (new File(ess.getDataFolder(), "announcer.txt").exists())
		{
			textInput = new BookInput("announcer", false, ess);
		}
		else
		{
			textInput = new BookInput("announcer", true, ess);
		}
		final CommandSource sender;
		sender = new CommandSource(Console.getCommandSender(ess.getServer()));
		this.output = new KeywordReplacer(textInput, sender, ess);

		try
		{
			setupAnnouncer(0, this.interval, this.random);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	private void setupAnnouncer(long delay, long interval, boolean random) throws Exception
	{
		messages.addAll(output.getChapters());
		if (messages.size() > 0)
		{
			startAnnouncerTask(delay, interval, random);
		}
		else
		{
			throw new Exception("Announcer has no messages to display."); // todo: TL key
		}
	}

	public void startAnnouncerTask(long delay, long interval, final boolean random)
	{
		ess.getScheduler().scheduleSyncRepeatingTask(ess, new Runnable()
		{
			@Override
			public void run()
			{
				if (random)
				{
					sendRandomMessage();
				}
				else if (messageCounter < messages.size())
				{
					ess.broadcastMessage(messages.get(messageCounter));
					messageCounter++;
				}
				else
				{
					messageCounter = 0;
					ess.broadcastMessage(messages.get(0));
					messageCounter++;
				}

			}
		}, delay, interval * 20);
	}

	private void sendRandomMessage()
	{
		Random rand = new Random();
		int n = rand.nextInt(messages.size());
		ess.broadcastMessage(messages.get(n));
	}

	public List<String> getMessages()
	{
		return messages;
	}

	public long getInterval()
	{
		return interval;
	}

	public void setInterval(long interval)
	{
		this.interval = interval;
	}

	public boolean isRandom()
	{
		return random;
	}

}

/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import org.junit.Test;

import net.runelite.api.ChatMessageType;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Pins which chat message types the pouch-deposit and reward-loot detectors are allowed to act on.
 * Both lines are server-generated; matching player-typed channels let anyone in range inject
 * zero-cost lots into another player's cost basis (#317).
 */
public class ChatDetectorFilterTest
{
	/** The server's own two channels are the complete allowed set. */
	@Test
	public void onlyServerGeneratedTypesAreAccepted()
	{
		assertTrue(StockpilePlugin.isGameMessage(ChatMessageType.GAMEMESSAGE));
		assertTrue(StockpilePlugin.isGameMessage(ChatMessageType.SPAM));
	}

	/** Anything a player can type into is rejected, whatever the text says. */
	@Test
	public void playerWritableTypesAreRejected()
	{
		ChatMessageType[] writable = {
			ChatMessageType.PUBLICCHAT,
			ChatMessageType.PRIVATECHAT,
			ChatMessageType.PRIVATECHATOUT,
			ChatMessageType.FRIENDSCHAT,
			ChatMessageType.CLAN_CHAT,
			ChatMessageType.CLAN_GUEST_CHAT,
			ChatMessageType.MODCHAT,
			ChatMessageType.MODPRIVATECHAT,
			ChatMessageType.TRADE,
			ChatMessageType.AUTOTYPER,
		};

		for (ChatMessageType type : writable)
			assertFalse(type.name(), StockpilePlugin.isGameMessage(type));
	}

	/** The detector texts themselves are unchanged - it is the channel that is now filtered. */
	@Test
	public void pouchDepositTextStillMatches()
	{
		assertTrue(StockpilePlugin.isPouchDepositMessage("You empty all of your containers into the bank."));
		assertFalse(StockpilePlugin.isPouchDepositMessage("You found some loot: 3 x Coins"));
		assertFalse(StockpilePlugin.isPouchDepositMessage(null));
	}
}

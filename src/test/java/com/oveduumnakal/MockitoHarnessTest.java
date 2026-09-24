/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import javax.inject.Inject;

import com.google.inject.Guice;
import com.google.inject.testing.fieldbinder.Bind;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.GrandExchangeOffer;
import net.runelite.api.GrandExchangeOfferState;
import net.runelite.client.callback.ClientThread;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Smoke test for the Mockito + guice-testlib test setup (#384): RuneLite API interfaces and the
 * {@link ClientThread} class can be mocked under the Java 11 toolchain, and {@link BoundFieldModule}
 * injects {@code @Bind} mocks the way the plugin's own {@code @Inject} fields receive them.
 */
public class MockitoHarnessTest
{
	@Rule
	public final MockitoRule mockito = MockitoJUnit.rule();

	@Mock
	@Bind
	private Client client;

	@Mock
	@Bind
	private ClientThread clientThread;

	@Inject
	private Client injectedClient;

	@Inject
	private ClientThread injectedClientThread;

	@Before
	public void inject()
	{
		Guice.createInjector(BoundFieldModule.of(this)).injectMembers(this);
	}

	@Test
	public void boundMocksAreInjected()
	{
		when(client.getGameState()).thenReturn(GameState.LOGGED_IN);

		assertSame(client, injectedClient);
		assertSame(clientThread, injectedClientThread);
		assertEquals(GameState.LOGGED_IN, injectedClient.getGameState());
	}

	@Test
	public void grandExchangeOffersCanBeStubbed()
	{
		GrandExchangeOffer offer = mock(GrandExchangeOffer.class);
		when(offer.getItemId()).thenReturn(560);
		when(offer.getState()).thenReturn(GrandExchangeOfferState.SELLING);
		when(offer.getQuantitySold()).thenReturn(40);
		when(offer.getSpent()).thenReturn(8_000);

		assertEquals(560, offer.getItemId());
		assertEquals(GrandExchangeOfferState.SELLING, offer.getState());
		assertEquals(200, offer.getSpent() / offer.getQuantitySold());
	}

	@Test
	public void clientThreadCanRunQueuedWorkInline()
	{
		doAnswer(invocation ->
		{
			invocation.<Runnable>getArgument(0).run();
			return null;
		})
				.when(clientThread)
				.invokeLater(any(Runnable.class));
		boolean[] ran = {false};

		Runnable work = () ->
		{
			ran[0] = true;
		};

		injectedClientThread.invokeLater(work);

		assertTrue(ran[0]);
	}
}

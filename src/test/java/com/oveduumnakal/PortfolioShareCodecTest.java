/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import com.google.gson.Gson;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Round-trip, compactness, and defensive-decode coverage for {@link PortfolioShareCodec}, including
 * the inflate-size and token-length caps that keep a gzip bomb from taking the client down (#330).
 */
public class PortfolioShareCodecTest
{
	private final PortfolioShareCodec codec = new PortfolioShareCodec(new Gson());

	private PortfolioShareCodec.Snapshot sample()
	{
		PortfolioShareCodec.Snapshot s = new PortfolioShareCodec.Snapshot();
		s.setV(1);
		s.setItems(Arrays.asList(
				new PortfolioShareCodec.Entry(560, TrackItemMode.TRACK, "Runes", true),
				new PortfolioShareCodec.Entry(4151, TrackItemMode.TRACK, null, false)));
		s.setCategories(Collections.singletonList(new CategoryState("Runes", false)));
		return s;
	}

	@Test
	public void encodeRoundTrips()
	{
		String token = codec.encode(sample());
		assertTrue("token carries the format prefix", token.startsWith(PortfolioShareCodec.PREFIX));

		PortfolioShareCodec.Snapshot back = codec.decode(token);
		List<PortfolioShareCodec.Entry> items = back.getItems();
		assertEquals(2, items.size());

		PortfolioShareCodec.Entry first = items.get(0);
		assertEquals(560, first.getId());
		assertEquals("Runes", first.getCategory());
		assertTrue(first.isFavorite());
		assertNull(items.get(1).getCategory());

		List<CategoryState> categories = back.getCategories();
		assertEquals("Runes", categories.get(0).getName());
	}

	@Test
	public void decodeAcceptsRawJson()
	{
		String json = new Gson().toJson(sample());
		PortfolioShareCodec.Snapshot back = codec.decode(json);
		assertEquals(2, back.getItems().size());
	}

	@Test
	public void tokenIsSingleLineAndCompactForRealLists()
	{
		PortfolioShareCodec.Snapshot big = new PortfolioShareCodec.Snapshot();
		big.setV(1);
		List<PortfolioShareCodec.Entry> items = new ArrayList<>();
		for (int i = 0; i < 60; i++)
			items.add(new PortfolioShareCodec.Entry(1000 + i, TrackItemMode.TRACK, "Category " + (i % 5), i % 2 == 0));

		big.setItems(items);

		String token = codec.encode(big);
		boolean singleLine = token.indexOf('\n') < 0 && token.indexOf(' ') < 0;
		assertTrue("token is a single paste-able line", singleLine);

		int jsonLength = new Gson().toJson(big).length();
		assertTrue("gzip wins on a realistic watchlist", token.length() < jsonLength);

		PortfolioShareCodec.Snapshot decoded = codec.decode(token);
		assertEquals(60, decoded.getItems().size());
	}

	@Test
	public void malformedInputReturnsNullNotThrow()
	{
		assertNull(codec.decode(null));
		assertNull(codec.decode(""));
		assertNull(codec.decode("   "));
		assertNull(codec.decode("STKPL1:not-valid-base64!!!"));

		java.util.Base64.Encoder encoder = java.util.Base64.getUrlEncoder().withoutPadding();
		String notGzip = encoder.encodeToString("not gzip".getBytes());
		assertNull(codec.decode("STKPL1:" + notGzip));
		assertNull(codec.decode("{ this is : not json }"));
	}

	/** A valid STKPL1 token whose payload gunzips to {@code size} bytes of highly compressible data. */
	private static String bomb(int size) throws Exception
	{
		byte[] payload = new byte[size];
		Arrays.fill(payload, (byte) 'A');
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		try (GZIPOutputStream gzip = new GZIPOutputStream(bytes))
		{
			gzip.write(payload);
		}

		Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
		return PortfolioShareCodec.PREFIX + encoder.encodeToString(bytes.toByteArray());
	}

	/**
	 * A share code arrives from someone else, so an unbounded gunzip is a denial of service: gzip
	 * reaches ~1000:1, and the resulting OutOfMemoryError is not caught and kills the client.
	 */
	@Test
	public void anOversizedInflatedPayloadDecodesToNull() throws Exception
	{
		String token = bomb(PortfolioShareCodec.MAX_INFLATED_BYTES + 4096);
		assertTrue("the bomb must stay small compressed", token.length() < 4096);
		assertNull(codec.decode(token));
	}

	/** A payload under the cap is still read - the guard bounds the size, it does not reject compression. */
	@Test
	public void aLargeButUnderCapPayloadStillInflates() throws Exception
	{
		assertNull("valid JSON is still required", codec.decode(bomb(1024)));
	}

	/** An over-long token body is rejected before it is even Base64-decoded. */
	@Test
	public void anOversizedTokenBodyDecodesToNull()
	{
		StringBuilder body = new StringBuilder(PortfolioShareCodec.MAX_TOKEN_CHARS + 16);
		for (int i = 0; i < PortfolioShareCodec.MAX_TOKEN_CHARS + 16; i++)
			body.append('A');

		assertNull(codec.decode(PortfolioShareCodec.PREFIX + body));
	}

	/** The caps must not touch a normal code: a real snapshot still round-trips. */
	@Test
	public void aNormalTokenIsUnaffectedByTheCaps() throws Exception
	{
		String token = codec.encode(sample());
		assertTrue(token.length() < PortfolioShareCodec.MAX_TOKEN_CHARS);

		PortfolioShareCodec.Snapshot back = codec.decode(token);
		List<PortfolioShareCodec.Entry> expected = sample().getItems();
		assertEquals(expected.size(), back.getItems().size());
	}
}

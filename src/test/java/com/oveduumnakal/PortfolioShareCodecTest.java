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

	@Test
	public void importIsCanonicalizedDeduplicatedAndSanitized()
	{
		List<PortfolioShareCodec.Entry> raw = Arrays.asList(
				new PortfolioShareCodec.Entry(1, TrackItemMode.TRACK, "<html><b>Ores", false),
				null,
				new PortfolioShareCodec.Entry(-5, TrackItemMode.TRACK, null, false),
				new PortfolioShareCodec.Entry(2, TrackItemMode.TRACK, null, true),
				new PortfolioShareCodec.Entry(1, TrackItemMode.TRACK, null, false));

		List<PortfolioShareCodec.Entry> clean = PortfolioShareCodec.normalize(raw, id -> id == 2 ? 1 : id);

		assertEquals("noted id 2 canonicalizes onto 1, so only one entry survives (#375)", 1, clean.size());
		assertEquals(1, clean.get(0).getId());
		assertEquals("htmlbOres", clean.get(0).getCategory());
	}

	@Test
	public void importIsCappedAtTheItemLimit()
	{
		List<PortfolioShareCodec.Entry> raw = new ArrayList<>();
		for (int id = 1; id <= PortfolioShareCodec.MAX_IMPORT_ITEMS + 250; id++)
			raw.add(new PortfolioShareCodec.Entry(id, TrackItemMode.TRACK, null, false));

		List<PortfolioShareCodec.Entry> clean = PortfolioShareCodec.normalize(raw, id -> id);

		assertEquals(PortfolioShareCodec.MAX_IMPORT_ITEMS, clean.size());
	}

	@Test
	public void hostileOrDamagedCodesDecodeToNull()
	{
		String token = codec.encode(sample());
		java.util.Base64.Encoder encoder = java.util.Base64.getUrlEncoder().withoutPadding();

		assertNull("a future format version", codec.decode("STKPL2:" + token.substring(PortfolioShareCodec.PREFIX
				.length())));
		assertNull("a truncated paste", codec.decode(token.substring(0, token.length() / 2)));
		assertNull("gzipped text that isn't JSON", codec.decode(PortfolioShareCodec.PREFIX
				+ encoder.encodeToString(gzip("hello"))));
		assertNull("items explicitly null", codec.decode("{\"v\":1,\"items\":null}"));
		assertNull("items of the wrong type", codec.decode("{\"v\":1,\"items\":5}"));
		assertNull("a JSON array", codec.decode("[]"));
	}

	@Test
	public void aCodeWithNoItemsKeyDecodesToAnEmptyList()
	{
		PortfolioShareCodec.Snapshot snapshot = codec.decode("{\"v\":1}");

		assertEquals(0, snapshot.getItems().size());
		assertEquals(0, snapshot.getCategories().size());
	}

	@Test
	public void importDropsIdsThatCanonicalizeToNothing()
	{
		List<PortfolioShareCodec.Entry> clean = PortfolioShareCodec.normalize(Arrays.asList(
				new PortfolioShareCodec.Entry(7, TrackItemMode.VIEW, null, false),
				new PortfolioShareCodec.Entry(8, null, "   ", true)), id -> id == 7 ? -1 : id);

		assertEquals(1, clean.size());
		assertEquals(8, clean.get(0).getId());
		assertNull("a missing mode stays missing; the plugin defaults it to TRACK", clean.get(0).getMode());
		assertTrue(clean.get(0).isFavorite());
	}

	private static byte[] gzip(String text)
	{
		try
		{
			java.io.ByteArrayOutputStream bytes = new java.io.ByteArrayOutputStream();
			try (java.util.zip.GZIPOutputStream out = new java.util.zip.GZIPOutputStream(bytes))
			{
				out.write(text.getBytes(java.nio.charset.StandardCharsets.UTF_8));
			}

			return bytes.toByteArray();
		}
		catch (java.io.IOException e)
		{
			throw new IllegalStateException(e);
		}
	}
}

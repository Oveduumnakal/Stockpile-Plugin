/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Serializes a tracked list (item ids, modes, categories, favorites and the
 * category definitions) to a single compact, shareable token and back.
 *
 * <p>The token is {@link #PREFIX} followed by a URL-safe Base64 encoding of the
 * gzipped JSON, so a whole watchlist pastes as one line into chat. {@link #decode}
 * also accepts the raw JSON directly (for hand-editing/debugging), detected by a
 * leading brace. Decoding is defensive: any malformed input yields {@code null}
 * rather than throwing, so an import dialog can report a friendly error. That includes a
 * deliberately oversized payload - decoding is bounded at both ends (#330).
 */
public final class PortfolioShareCodec
{
	/** Token marker + format version; a future breaking change bumps the digit. */
	static final String PREFIX = "STKPL1:";

	/**
	 * Ceiling on the inflated payload, about 100x the largest realistic watchlist. Past it the token
	 * is treated as unreadable, which the import dialog already reports as a friendly error.
	 */
	static final int MAX_INFLATED_BYTES = 1 << 20;

	/** Ceiling on the compressed token body, so an oversized paste is rejected before it is decoded. */
	static final int MAX_TOKEN_CHARS = 256 * 1024;

	private final Gson gson;

	/**
	 * Creates a codec that (de)serializes share tokens with the given Gson instance.
	 *
	 * @param gson the Gson instance used for JSON encoding
	 */
	public PortfolioShareCodec(Gson gson)
	{
		this.gson = gson;
	}

	/** @return the shareable token for {@code snapshot}, never {@code null}. */
	public String encode(Snapshot snapshot)
	{
		String json = gson.toJson(snapshot);

		try
		{
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			try (GZIPOutputStream gzip = new GZIPOutputStream(bytes))
			{
				gzip.write(json.getBytes(StandardCharsets.UTF_8));
			}

			return PREFIX + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes.toByteArray());
		}
		catch (IOException e)
		{
			throw new IllegalStateException("In-memory gzip should never fail", e);
		}
	}

	/**
	 * Parses a token (or raw JSON) back into a snapshot.
	 *
	 * @return the decoded snapshot, or {@code null} if the input is blank, not a
	 *         recognizable token, or fails to parse.
	 */
	public Snapshot decode(String input)
	{
		if (input == null)
			return null;

		String trimmed = input.trim();
		if (trimmed.isEmpty())
			return null;

		String json;
		if (trimmed.startsWith("{"))
			json = trimmed;
		else
			json = inflate(trimmed);

		if (json == null)
			return null;

		try
		{
			Snapshot snapshot = gson.fromJson(json, Snapshot.class);
			return snapshot != null && snapshot.items != null ? snapshot : null;
		}
		catch (JsonSyntaxException e)
		{
			return null;
		}
	}

	/**
	 * Base64-decodes and gunzips a token body; {@code null} on any corruption or on a payload that
	 * exceeds {@link #MAX_TOKEN_CHARS} compressed or {@link #MAX_INFLATED_BYTES} inflated.
	 *
	 * <p>The token comes from someone else - users are encouraged to paste each other's codes - and
	 * gzip reaches roughly 1000:1 on repetitive data, so an unbounded read turns a few KB of input
	 * into gigabytes. That throws {@code OutOfMemoryError}, which this method's catch does not cover
	 * and which takes the client with it, on the client thread (#330).
	 */
	private String inflate(String token)
	{
		String body = token.startsWith(PREFIX) ? token.substring(PREFIX.length()) : token;
		if (body.length() > MAX_TOKEN_CHARS)
			return null;

		try
		{
			byte[] compressed = Base64.getUrlDecoder().decode(body);
			try (GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(compressed)))
			{
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				byte[] buffer = new byte[4096];
				int read;
				int total = 0;
				while ((read = gzip.read(buffer)) != -1)
				{
					total += read;
					if (total > MAX_INFLATED_BYTES)
						return null;

					out.write(buffer, 0, read);
				}

				return new String(out.toByteArray(), StandardCharsets.UTF_8);
			}
		}
		catch (IllegalArgumentException | IOException e)
		{
			return null;
		}
	}

	/** The exported watchlist: the tracked entries plus the category definitions to recreate. */
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Snapshot
	{
		/** Format version, for forward-compatible decoding. */
		int v;

		List<Entry> items = new ArrayList<>();

		List<CategoryState> categories = new ArrayList<>();
	}

	/** One shared tracked item: its id, tracking mode, category (nullable) and favorite flag. */
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Entry
	{
		int id;

		TrackItemMode mode;

		String category;

		boolean favorite;
	}
}

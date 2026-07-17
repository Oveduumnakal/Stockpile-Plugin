/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Value;

/**
 * Parses the bundled {@code changelog.md} resource into an ordered list of
 * releases (newest first). Each release is a {@code ## <version> — <date>} heading
 * followed by its highlight bullet lines. The parser is offline and deterministic;
 * the newest entry's version is treated as the plugin's current version at runtime,
 * and {@code ChangelogGuardTest} enforces that it matches
 * {@code runelite-plugin.properties}.
 */
public final class Changelog
{
	/** Resource path of the bundled changelog, relative to the classpath root. */
	static final String RESOURCE = "/changelog.md";

	/** A release heading: {@code ## 1.4 — 2026-07-25} (em dash or hyphen; date optional). */
	private static final Pattern HEADING = Pattern.compile("^##\\s+(\\S+)\\s*(?:[—-]\\s*(\\S+))?\\s*$");

	private final List<Release> releases;

	private Changelog(List<Release> releases)
	{
		this.releases = releases;
	}

	/** Loads and parses the bundled changelog resource. */
	public static Changelog load()
	{
		try (InputStream in = Changelog.class.getResourceAsStream(RESOURCE))
		{
			if (in == null)
				return new Changelog(Collections.emptyList());

			return parse(read(in));
		}
		catch (IOException e)
		{
			return new Changelog(Collections.emptyList());
		}
	}

	/** Parses changelog markdown into releases in document order (expected newest first). */
	public static Changelog parse(String markdown)
	{
		List<Release> releases = new ArrayList<>();
		Release.ReleaseBuilder current = null;
		List<String> highlights = null;

		for (String line : markdown.split("\n", -1))
		{
			Matcher heading = HEADING.matcher(line.trim());
			if (line.startsWith("## ") && heading.matches())
			{
				if (current != null)
					releases.add(current.highlights(highlights).build());

				current = Release.builder();
				current.version(heading.group(1));
				current.date(heading.group(2));
				highlights = new ArrayList<>();
				continue;
			}

			if (current == null)
				continue;

			String trimmed = line.trim();
			if (trimmed.startsWith("- ") || trimmed.startsWith("* "))
				highlights.add(trimmed.substring(2).trim());
		}

		if (current != null)
			releases.add(current.highlights(highlights).build());

		return new Changelog(releases);
	}

	/** @return the releases, newest first (document order). */
	public List<Release> releases()
	{
		return Collections.unmodifiableList(releases);
	}

	/** @return the newest release's version, or {@code null} when the changelog is empty. */
	public String currentVersion()
	{
		return releases.isEmpty() ? null : releases.get(0).getVersion();
	}

	/** @return whether a release with exactly {@code version} exists. */
	public boolean hasVersion(String version)
	{
		return releases.stream().anyMatch(r -> r.getVersion().equals(version));
	}

	private static String read(InputStream in) throws IOException
	{
		StringBuilder sb = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8)))
		{
			String line;
			while ((line = reader.readLine()) != null)
				sb.append(line).append('\n');
		}

		return sb.toString();
	}

	/** One release: its version, optional date, and highlight lines (bullet text without the marker). */
	@Value
	@lombok.Builder
	public static class Release
	{
		String version;

		String date;

		@lombok.Builder.Default
		List<String> highlights = Collections.emptyList();
	}
}

/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/** Parsing coverage for {@link Changelog}: headings, highlights, ordering, and edge cases. */
public class ChangelogTest
{
	private static final String SAMPLE = "# Title\n\n"
			+ "## 1.4 — 2026-07-25\n"
			+ "- First highlight\n"
			+ "- Second highlight\n\n"
			+ "## 1.3 - 2026-05-30\n"
			+ "* Older highlight\n";

	@Test
	public void parsesReleasesNewestFirst()
	{
		Changelog log = Changelog.parse(SAMPLE);
		List<Changelog.Release> releases = log.releases();
		assertEquals(2, releases.size());

		Changelog.Release latest = releases.get(0);
		assertEquals("1.4", latest.getVersion());
		assertEquals("2026-07-25", latest.getDate());
		assertEquals(2, latest.getHighlights().size());
		assertEquals("First highlight", latest.getHighlights().get(0));
	}

	@Test
	public void currentVersionIsTopEntry()
	{
		Changelog log = Changelog.parse(SAMPLE);
		assertEquals("1.4", log.currentVersion());
		assertTrue(log.hasVersion("1.3"));
		assertFalse(log.hasVersion("9.9"));
	}

	@Test
	public void hyphenDateSeparatorParses()
	{
		Changelog log = Changelog.parse(SAMPLE);
		Changelog.Release old = log.releases().get(1);
		assertEquals("1.3", old.getVersion());
		assertEquals("2026-05-30", old.getDate());
	}

	@Test
	public void emptyChangelogHasNoCurrentVersion()
	{
		Changelog log = Changelog.parse("# Just a title, no releases\n");
		assertTrue(log.releases().isEmpty());
		assertNull(log.currentVersion());
	}
}

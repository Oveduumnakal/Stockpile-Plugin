/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextArea;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the pure text helpers inside the panel and detail view (#392): changelog section
 * extraction, HTML escaping and markdown links for the changelog pane, the pre-filled GitHub issue
 * URL, relative ages, totals formatting, and the detail view's duration and interval wording.
 */
public class PanelTextHelpersTest
{
	private static long now()
	{
		return System.currentTimeMillis() / 1000L;
	}

	@Test
	public void changelogSectionsAreTheTwoAndThreeHashHeadingsInOrder()
	{
		String body = "## Quick Overview\ntext\n  ### Panel  \n#### too deep\n##NoSpace\n## Bug Fixes";

		List<StockpilePanel.ChangelogSection> sections = StockpilePanel.extractSections(body);

		assertEquals(3, sections.size());
		assertEquals(new StockpilePanel.ChangelogSection(0, "Quick Overview", "sec0"), sections.get(0));
		assertEquals("indented headings count, trimmed", new StockpilePanel.ChangelogSection(1, "Panel", "sec1"),
				sections.get(1));
		assertEquals(new StockpilePanel.ChangelogSection(0, "Bug Fixes", "sec2"), sections.get(2));
		assertTrue(StockpilePanel.extractSections("no headings here").isEmpty());
	}

	@Test
	public void escapingNeutralisesMarkupButLeavesQuotes()
	{
		assertEquals("a &lt;b&gt; &amp;&amp; c", StockpilePanel.escapeHtml("a <b> && c"));
		assertEquals("&amp;lt; is escaped once", "&amp;lt;", StockpilePanel.escapeHtml("&lt;"));
		assertEquals("quotes pass through", "say \"hi\" 'there'", StockpilePanel.escapeHtml("say \"hi\" 'there'"));
	}

	@Test
	public void markdownLinksBecomeAnchorsAfterTheTextIsEscaped()
	{
		String html = StockpilePanel.inlineLinks("Fixed [#380](https://github.com/o/s/issues/380) & [#381](u)");

		assertEquals("Fixed <a href='https://github.com/o/s/issues/380'>#380</a> &amp; <a href='u'>#381</a>", html);
	}

	@Test
	public void markupInsideALinkLabelIsEscaped()
	{
		assertEquals("<a href='x'>&lt;b&gt;</a>", StockpilePanel.inlineLinks("[<b>](x)"));
	}

	@Test
	public void textThatIsNotALinkIsOnlyEscaped()
	{
		assertEquals("[spaced] (out) and [](empty)", StockpilePanel.inlineLinks("[spaced] (out) and [](empty)"));
		assertEquals("[unclosed](x", StockpilePanel.inlineLinks("[unclosed](x"));
		assertEquals("costs $5 \\ each", StockpilePanel.inlineLinks("costs $5 \\ each"));
	}

	@Test
	public void theIssueUrlCarriesThePrefixedTitleAndOnlyFilledFields()
	{
		IssueField steps = new IssueField("steps", "Steps", 3);
		IssueField area = new IssueField("area", "Area", new String[]{"Panel", "Overlay"});
		IssueField notes = new IssueField("notes", "Notes", 2);
		Map<IssueField, JComponent> inputs = new LinkedHashMap<>();
		inputs.put(steps, new JTextArea("  open the panel & click  "));
		JComboBox<String> areaBox = new JComboBox<>(area.options);
		areaBox.setSelectedItem("Overlay");
		inputs.put(area, areaBox);
		inputs.put(notes, new JTextArea("   "));

		String url = StockpilePanel.buildIssueUrl("bug_report.yml", "[Bug]: ", " Price wrong ",
				List.of(steps, area, notes), inputs);

		assertEquals("https://github.com/Oveduumnakal/Stockpile-Plugin/issues/new?template=bug_report.yml"
				+ "&title=%5BBug%5D%3A%20Price%20wrong"
				+ "&steps=open%20the%20panel%20%26%20click"
				+ "&area=Overlay", url);
	}

	@Test
	public void aBlankTitleIsLeftOutOfTheIssueUrl()
	{
		String url = StockpilePanel.buildIssueUrl("feature_request.yml", "[Feature]: ", "  ", List.of(),
				Map.of());

		assertEquals("https://github.com/Oveduumnakal/Stockpile-Plugin/issues/new?template=feature_request.yml", url);
		assertEquals(url, StockpilePanel.buildIssueUrl("feature_request.yml", "[Feature]: ", null, List.of(),
				Map.of()));
	}

	@Test
	public void agesUseTheLargestWholeUnit()
	{
		assertEquals("unknown", StockpilePanel.formatAge(0));
		assertTrue(StockpilePanel.formatAge(now() - 10).matches("1[01]s ago"));
		assertEquals("a future stamp reads as now", "0s ago", StockpilePanel.formatAge(now() + 500));
		assertEquals("5m ago", StockpilePanel.formatAge(now() - 5 * 60 - 10));
		assertEquals("3hr ago", StockpilePanel.formatAge(now() - 3 * 3600 - 100));
		assertEquals("2d ago", StockpilePanel.formatAge(now() - 2 * 86_400 - 100));
	}

	@Test
	public void totalsFollowTheValueFormat()
	{
		assertEquals("1,234,567 gp", StockpilePanel.formatTotalGp(1_234_567, ValueFormat.FULL));
		assertEquals("1.5K gp", StockpilePanel.formatTotalGp(1_500, ValueFormat.ABBREVIATED));
	}

	@Test
	public void durationsShowHoursAndMinutesOrTheSmallestUnitNeeded()
	{
		assertEquals("0s", DetailView.formatDuration(0));
		assertEquals("45s", DetailView.formatDuration(45));
		assertEquals("1m", DetailView.formatDuration(60));
		assertEquals("43m", DetailView.formatDuration(43 * 60 + 59));
		assertEquals("1h 0m", DetailView.formatDuration(3600));
		assertEquals("2h 14m", DetailView.formatDuration(2 * 3600 + 14 * 60 + 5));
	}

	@Test
	public void intervalsAreSpelledLowerCaseForTooltips()
	{
		assertEquals("24 hour", DetailView.spelledInterval(TimeWindow.H24));
		assertEquals("1 week", DetailView.spelledInterval(TimeWindow.WEEK));
	}
}

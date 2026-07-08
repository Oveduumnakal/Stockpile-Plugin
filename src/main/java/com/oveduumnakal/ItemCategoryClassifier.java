/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

/**
 * Assigns an item to a sensible category from its name alone, using an ordered
 * keyword/suffix rule table. The first matching rule wins, so more specific rules
 * are listed before broader ones; anything unmatched falls into {@link #OTHER}.
 *
 * <p>Kept deliberately offline and deterministic (like {@link MarketClassifier}) so
 * the groupings are easy to tune and unit-test. A richer source (wiki/GE catalogue
 * categories) could replace the table later without changing callers.
 */
public final class ItemCategoryClassifier
{
	/** Catch-all bucket for items no rule matches. */
	public static final String OTHER = "Other";

	private static final List<Rule> RULES = buildRules();

	private ItemCategoryClassifier()
	{
	}

	/**
	 * @param itemName the item's display name
	 * @return the category name for {@code itemName}, or {@link #OTHER} when nothing matches
	 */
	public static String classify(String itemName)
	{
		if (itemName == null || itemName.trim().isEmpty())
			return OTHER;

		String name = itemName.toLowerCase(Locale.ROOT);
		for (Rule rule : RULES)
			if (rule.matches.test(name))
				return rule.category;

		return OTHER;
	}

	private static List<Rule> buildRules()
	{
		List<Rule> rules = new ArrayList<>();

		rules.add(new Rule("Seeds", n -> n.endsWith(" seed") || n.endsWith(" seeds") || n.endsWith(" sapling")));
		rules.add(new Rule("Runes", n -> n.endsWith(" rune") || n.endsWith(" runes") || n.endsWith(" essence")));
		rules.add(new Rule("Potions", n -> n.contains("potion") || n.contains("brew") || n.contains("(1)")
				|| n.contains("(2)") || n.contains("(3)") || n.contains("(4)")));
		rules.add(new Rule("Herbs", n -> n.startsWith("grimy ") || n.startsWith("clean ")));
		rules.add(new Rule("Logs", n -> n.endsWith(" logs") || n.equals("logs")));
		rules.add(new Rule("Ores & Bars", n -> n.endsWith(" ore") || n.endsWith(" bar") || n.contains("coal")));
		rules.add(new Rule("Bones", n -> n.endsWith(" bones") || n.endsWith(" ashes")));
		rules.add(new Rule("Gems", n -> n.startsWith("uncut ") || containsAny(n,
				"sapphire", "emerald", "ruby", "diamond", "dragonstone", "onyx", "zenyte")));
		rules.add(new Rule("Ammo", n -> containsAny(n,
				"arrow", "bolt", "dart", "javelin", "throwing knife", "cannonball", "blessed bolt")));
		rules.add(new Rule("Hunter", n -> containsAny(n,
				"chinchompa", "kebbit", "salamander", "bird snare", "box trap")));
		rules.add(new Rule("Food", n -> n.startsWith("raw ") || containsAny(n,
				"shark", "lobster", "tuna", "salmon", "monkfish", "anglerfish", "karambwan",
				"swordfish", "bass", "trout", "pike", "herring", "sardine", "manta ray", "cake",
				"bread", "pie", "stew", "kebab", "pizza")));
		rules.add(new Rule("Weapons", n -> containsAny(n,
				"scimitar", "sword", "dagger", "mace", "battleaxe", " axe", "warhammer", "spear",
				"halberd", "whip", "bow", "crossbow", "staff", "wand", "blowpipe", "maul", "claws")));
		rules.add(new Rule("Armour", n -> containsAny(n,
				"platebody", "platelegs", "plateskirt", "chainbody", "helm", "shield", "kiteshield",
				"boots", "gloves", "gauntlets", "cape", "cloak", "hood", "coif", "vambraces", "chaps")));

		return rules;
	}

	private static boolean containsAny(String name, String... needles)
	{
		for (String needle : needles)
			if (name.contains(needle))
				return true;

		return false;
	}

	/** One classification rule: the category to assign when the (lower-cased) name matches. */
	private static final class Rule
	{
		private final String category;

		private final Predicate<String> matches;

		private Rule(String category, Predicate<String> matches)
		{
			this.category = category;
			this.matches = matches;
		}
	}
}

/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.event.TableModelEvent;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link NotificationsTableModel} (#387): which cells are editable per metric, how an edit
 * snaps a rule to what its metric allows, value parsing, and that an edit commits through the
 * client-thread editor by rule identity (#373) - so it reaches the rule the user edited even after
 * the list shifted underneath it.
 */
public class NotificationsTableModelTest
{
	private static final int METRIC = 0;

	private static final int TIME = 1;

	private static final int OP = 2;

	private static final int VALUE = 3;

	private static final int REPEAT = 4;

	/** The model's last edit, held so a test can shift the list before the client thread applies it. */
	private Consumer<List<NotificationRule>> pending;

	private Runnable pendingApplied;

	private final List<Integer> updatedRows = new ArrayList<>();

	private final NotificationsTableModel model = new NotificationsTableModel((mutation, onApplied) ->
	{
		pending = mutation;
		pendingApplied = onApplied;
	});

	private final TrackedItem item = new TrackedItem(560, "Test item");

	public NotificationsTableModelTest()
	{
		model.addTableModelListener(e ->
		{
			if (e.getType() == TableModelEvent.UPDATE && e.getFirstRow() >= 0)
				updatedRows.add(e.getFirstRow());
		});
	}

	private static NotificationRule rule(NotificationMetric metric, TimeWindow window, NotificationOperation op,
			String value)
	{
		NotificationRule rule = new NotificationRule();
		rule.setMetric(metric);
		rule.setTimeWindow(window);
		rule.setOperation(op);
		rule.setValue(value);
		return rule;
	}

	private void show(NotificationRule... rules)
	{
		item.setNotifications(new ArrayList<>(List.of(rules)));
		model.setItem(item);
	}

	/** Applies the held edit to the live list, as the client thread would, then runs its EDT callback. */
	private void commit()
	{
		pending.accept(item.getNotifications());
		pendingApplied.run();
		pending = null;
	}

	private void edit(Object value, int row, int col)
	{
		model.setValueAt(value, row, col);
		if (pending != null)
			commit();
	}

	@Test
	public void emptyModelHasNoRowsAndReadsBlank()
	{
		assertEquals(0, model.getRowCount());
		assertEquals(5, model.getColumnCount());
		assertEquals("", model.getValueAt(0, VALUE));
		assertEquals(Boolean.FALSE, model.getValueAt(0, REPEAT));
		assertFalse(model.isCellEditable(0, VALUE));
		assertEquals(Boolean.class, model.getColumnClass(REPEAT));
		assertEquals(Object.class, model.getColumnClass(VALUE));
	}

	@Test
	public void cellsReadTheRuleAndAQuantityRuleShowsNoTimeframe()
	{
		NotificationRule high = rule(NotificationMetric.HIGH, TimeWindow.H24, NotificationOperation.GTE, "5K");
		high.setRepeat(true);
		show(high, rule(NotificationMetric.QUANTITY, null, NotificationOperation.LTE, "30"));

		assertEquals(NotificationMetric.HIGH, model.getValueAt(0, METRIC));
		assertEquals(TimeWindow.H24, model.getValueAt(0, TIME));
		assertEquals(NotificationOperation.GTE, model.getValueAt(0, OP));
		assertEquals("5K", model.getValueAt(0, VALUE));
		assertEquals(true, model.getValueAt(0, REPEAT));
		assertEquals("—", model.getValueAt(1, TIME));
		assertEquals("", model.getValueAt(1, 9));
	}

	@Test
	public void timeframeAndOperatorLockPerMetric()
	{
		show(rule(NotificationMetric.HIGH, TimeWindow.LIVE, NotificationOperation.GTE, ""),
				rule(NotificationMetric.QUANTITY, null, NotificationOperation.GTE, ""),
				rule(NotificationMetric.RANGE_30D, TimeWindow.MONTH, NotificationOperation.EQ, "Low"),
				rule(NotificationMetric.VOLATILITY, TimeWindow.WEEK, NotificationOperation.EQ, "Low"),
				new NotificationRule());

		assertTrue(model.isCellEditable(0, TIME));
		assertTrue(model.isCellEditable(0, OP));
		assertFalse("quantity has no timeframe", model.isCellEditable(1, TIME));
		assertFalse("30d range is pinned to a month", model.isCellEditable(2, TIME));
		assertFalse("categorical metrics compare with =", model.isCellEditable(2, OP));
		assertTrue(model.isCellEditable(3, TIME));
		assertFalse(model.isCellEditable(3, OP));
		assertTrue("a blank row is fully editable", model.isCellEditable(4, TIME));
		assertTrue(model.isCellEditable(4, OP));
		assertTrue(model.isCellEditable(1, VALUE));
		assertFalse("a row past the end", model.isCellEditable(5, VALUE));
		assertFalse(model.isCellEditable(-1, VALUE));
	}

	@Test
	public void switchingToACategoricalMetricSnapsOperatorAndValue()
	{
		show(rule(NotificationMetric.HIGH, TimeWindow.H24, NotificationOperation.GTE, "5K"));

		edit(NotificationMetric.LIQUIDITY, 0, METRIC);

		NotificationRule r = item.getNotifications().get(0);
		assertEquals(NotificationMetric.LIQUIDITY, r.getMetric());
		assertEquals("the timeframe the rule had is kept", TimeWindow.H24, r.getTimeWindow());
		assertEquals(NotificationOperation.EQ, r.getOperation());
		assertEquals("Low", r.getValue());
	}

	@Test
	public void switchingMetricSnapsTheTimeframe()
	{
		NotificationRule blank = new NotificationRule();
		NotificationRule range = rule(NotificationMetric.HIGH, TimeWindow.H24, NotificationOperation.LTE, "1K");
		NotificationRule qty = rule(NotificationMetric.HIGH, TimeWindow.H24, NotificationOperation.LTE, "1K");
		show(blank, range, qty);

		edit(NotificationMetric.LOW, 0, METRIC);
		edit(NotificationMetric.RANGE_30D, 1, METRIC);
		edit(NotificationMetric.QUANTITY, 2, METRIC);

		assertEquals("a blank row defaults to live", TimeWindow.LIVE, blank.getTimeWindow());
		assertEquals("and to >=", NotificationOperation.GTE, blank.getOperation());
		assertEquals("", blank.getValue());
		assertEquals(TimeWindow.MONTH, range.getTimeWindow());
		assertEquals("Lowest", range.getValue());
		assertNull(qty.getTimeWindow());
		assertEquals("a numeric switch keeps the chosen operator", NotificationOperation.LTE, qty.getOperation());
	}

	@Test
	public void reselectingTheSameMetricKeepsTheRule()
	{
		NotificationRule r = rule(NotificationMetric.HIGH, TimeWindow.H24, NotificationOperation.LTE, "1K");
		show(r);

		edit(NotificationMetric.HIGH, 0, METRIC);

		assertEquals(TimeWindow.H24, r.getTimeWindow());
		assertEquals(NotificationOperation.LTE, r.getOperation());
		assertEquals("1K", r.getValue());
	}

	@Test
	public void valuesAreParsedAndReformattedPerKind()
	{
		NotificationRule gp = rule(NotificationMetric.HIGH, TimeWindow.LIVE, NotificationOperation.GTE, "1K");
		NotificationRule pct = rule(NotificationMetric.DELTA_PCT, TimeWindow.H24, NotificationOperation.GTE, "5%");
		NotificationRule cat = rule(NotificationMetric.VOLATILITY, TimeWindow.WEEK, NotificationOperation.EQ, "Low");
		NotificationRule blank = new NotificationRule();
		show(gp, pct, cat, blank);

		edit("1,500", 0, VALUE);
		edit("12.5", 1, VALUE);
		edit("  High ", 2, VALUE);
		edit(null, 3, VALUE);

		assertEquals("1.5K", gp.getValue());
		assertEquals("12.5%", pct.getValue());
		assertEquals("High", cat.getValue());
		assertEquals("", blank.getValue());
	}

	@Test
	public void unparseableValuesLeaveTheRuleAsItWas()
	{
		NotificationRule gp = rule(NotificationMetric.HIGH, TimeWindow.LIVE, NotificationOperation.GTE, "1K");
		NotificationRule pct = rule(NotificationMetric.DELTA_PCT, TimeWindow.H24, NotificationOperation.GTE, "5%");
		show(gp, pct);

		edit("lots", 0, VALUE);
		edit("some", 1, VALUE);

		assertEquals("1K", gp.getValue());
		assertEquals("5%", pct.getValue());
	}

	@Test
	public void wrongTypedValuesAreRejectedBeforeReachingTheEditor()
	{
		show(rule(NotificationMetric.HIGH, TimeWindow.LIVE, NotificationOperation.GTE, "1K"));

		model.setValueAt("HIGH", 0, METRIC);
		model.setValueAt("DAY", 0, TIME);
		model.setValueAt(">=", 0, OP);
		model.setValueAt("yes", 0, REPEAT);
		model.setValueAt("x", 0, 9);
		model.setValueAt("1K", 7, VALUE);

		assertNull("nothing was queued", pending);
	}

	@Test
	public void timeframeOperatorAndRepeatEditsApply()
	{
		NotificationRule r = rule(NotificationMetric.HIGH, TimeWindow.LIVE, NotificationOperation.GTE, "1K");
		show(r);

		edit(TimeWindow.WEEK, 0, TIME);
		edit(NotificationOperation.LT, 0, OP);
		edit(true, 0, REPEAT);

		assertEquals(TimeWindow.WEEK, r.getTimeWindow());
		assertEquals(NotificationOperation.LT, r.getOperation());
		assertTrue(r.isRepeat());
		assertEquals("each commit repaints the rule's row", List.of(0, 0, 0), updatedRows);
	}

	@Test
	public void anEditFollowsItsRuleWhenAnEarlierRuleIsRemoved()
	{
		NotificationRule fired = rule(NotificationMetric.HIGH, TimeWindow.LIVE, NotificationOperation.GTE, "1K");
		NotificationRule edited = rule(NotificationMetric.LOW, TimeWindow.LIVE, NotificationOperation.GTE, "1K");
		show(fired, edited);

		model.setValueAt("2k", 1, VALUE);
		item.getNotifications().remove(fired);
		commit();

		assertEquals("2K", edited.getValue());
		assertEquals("the repaint targets the rule's new row", List.of(0), updatedRows);
	}

	@Test
	public void anEditToARuleThatWasRemovedIsDropped()
	{
		NotificationRule gone = rule(NotificationMetric.HIGH, TimeWindow.LIVE, NotificationOperation.GTE, "1K");
		NotificationRule twin = rule(NotificationMetric.HIGH, TimeWindow.LIVE, NotificationOperation.GTE, "1K");
		show(gone, twin);

		model.setValueAt("9k", 0, VALUE);
		item.getNotifications().remove(0);
		commit();

		assertEquals("an equal-valued twin is not edited in its place", "1K", twin.getValue());
		assertEquals("the removed rule is not edited either", "1K", gone.getValue());
		assertTrue("no row repaints", updatedRows.isEmpty());
	}
}

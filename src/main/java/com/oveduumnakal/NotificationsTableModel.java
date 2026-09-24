/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.List;
import java.util.OptionalDouble;
import java.util.function.Consumer;
import javax.swing.table.AbstractTableModel;

/**
 * Swing table model backing the notification rules: one row per
 * {@link NotificationRule} with metric, timeframe, operator, and value columns.
 * Editing a cell commits through a client-thread {@link RuleEditor}, which owns the list, rather than
 * mutating the rule from the EDT (#373).
 */
class NotificationsTableModel extends AbstractTableModel
{
	private static final String[] COLS = {"Metric", "Time", "Op", "Value", "↻"};

	private final RuleEditor editor;
	private TrackedItem item;

	NotificationsTableModel(RuleEditor editor)
	{
		this.editor = editor;
	}

	void setItem(TrackedItem item)
	{
		this.item = item;
		fireTableStructureChanged();
	}

	@Override
	public int getRowCount()
	{
		return item == null ? 0 : item.getNotifications().size();
	}

	@Override
	public int getColumnCount()
	{
		return COLS.length;
	}

	@Override
	public String getColumnName(int c)
	{
		return COLS[c];
	}

	/** @return {@code Boolean} for the repeat column so the table renders/edits it as a checkbox. */
	@Override
	public Class<?> getColumnClass(int c)
	{
		return c == 4 ? Boolean.class : Object.class;
	}

	@Override
	public boolean isCellEditable(int r, int c)
	{
		NotificationRule rule = ruleAt(r);
		if (rule == null)
			return false;

		NotificationMetric m = rule.getMetric();
		switch (c)
		{
			case 1: return m == null || (!m.isTimeframeDisabled() && !m.locksTimeframeToMonth());
			case 2: return m == null || !m.locksOperationToEquals();
			default: return true;
		}
	}

	/**
	 * @return the rule at row {@code r}, or {@code null} when that row no longer exists. The list is
	 *         client-thread state and a fired one-shot rule is removed from there, so a row the table
	 *         counted a moment ago can be gone by the time it is read.
	 */
	private NotificationRule ruleAt(int r)
	{
		if (item == null || r < 0)
			return null;

		List<NotificationRule> rules = item.getNotifications();
		try
		{
			return r < rules.size() ? rules.get(r) : null;
		}
		catch (IndexOutOfBoundsException e)
		{
			return null;
		}
	}

	/**
	 * @return the row showing exactly {@code rule}, or -1. By identity: {@link NotificationRule} compares
	 *         by value, and every blank default row equals every other.
	 */
	private int rowOf(NotificationRule rule)
	{
		if (item == null)
			return -1;

		List<NotificationRule> rules = item.getNotifications();
		for (int i = 0; i < rules.size(); i++)
		{
			if (rules.get(i) == rule)
				return i;
		}

		return -1;
	}

	@Override
	public Object getValueAt(int r, int c)
	{
		NotificationRule rule = ruleAt(r);
		if (rule == null)
			return c == 4 ? Boolean.FALSE : "";

		NotificationMetric m = rule.getMetric();
		switch (c)
		{
			case 0: return m;
			case 1: return m != null && m.isTimeframeDisabled() ? "—" : rule.getTimeWindow();
			case 2: return rule.getOperation();
			case 3: return rule.getValue();
			case 4: return rule.isRepeat();
			default: return "";
		}
	}

	/**
	 * Commits a cell edit.
	 *
	 * <p>The new value is validated here on the EDT, but the rule is only written on the client thread
	 * through {@link #editor}, which owns the list (#373). The rule is captured by identity, so an edit
	 * that lands after a one-shot rule above it fired and was removed still reaches the rule the user
	 * edited, or nothing.
	 */
	@Override
	public void setValueAt(Object value, int r, int c)
	{
		NotificationRule target = ruleAt(r);
		if (target == null)
			return;

		Consumer<NotificationRule> change = changeFor(value, c);
		if (change == null)
			return;

		editor.edit(rules ->
		{
			if (rules.stream().anyMatch(rule -> rule == target))
				change.accept(target);
		}, () ->
		{
			int row = rowOf(target);
			if (row >= 0)
				fireTableRowsUpdated(row, row);
		});
	}

	/**
	 * @return the write a cell edit makes to its rule, or {@code null} when {@code value} is not valid
	 *         for column {@code c}
	 */
	private Consumer<NotificationRule> changeFor(Object value, int c)
	{
		switch (c)
		{
			case 0:
				return value instanceof NotificationMetric
						? rule -> applyMetric(rule, (NotificationMetric) value)
						: null;
			case 1:
				return value instanceof TimeWindow ? rule -> rule.setTimeWindow((TimeWindow) value) : null;
			case 2:
				return value instanceof NotificationOperation
						? rule -> rule.setOperation((NotificationOperation) value)
						: null;
			case 3:
				return rule -> applyValueEdit(rule, value == null ? "" : value.toString());
			case 4:
				return value instanceof Boolean ? rule -> rule.setRepeat((Boolean) value) : null;
			default:
				return null;
		}
	}

	/**
	 * Switches a rule to metric {@code m}, snapping the timeframe, operator and value to what that
	 * metric allows. A no-op when the rule already uses {@code m}.
	 */
	private static void applyMetric(NotificationRule rule, NotificationMetric m)
	{
		if (m == rule.getMetric())
			return;

		rule.setMetric(m);

		if (m.locksTimeframeToMonth())
			rule.setTimeWindow(TimeWindow.MONTH);
		else if (m.isTimeframeDisabled())
			rule.setTimeWindow(null);
		else if (rule.getTimeWindow() == null)
			rule.setTimeWindow(TimeWindow.LIVE);

		if (m.locksOperationToEquals())
			rule.setOperation(NotificationOperation.EQ);
		else if (rule.getOperation() == null)
			rule.setOperation(NotificationOperation.GTE);

		rule.setValue(m.isCategorical() ? m.getOptions().get(0) : "");
	}

	/**
	 * Normalises an edited value into the rule: categorical values are stored as
	 * typed, while percent and numeric inputs are parsed and reformatted
	 * (e.g. {@code "5000000"} &rarr; {@code "5m"}), ignored when unparseable.
	 */
	private static void applyValueEdit(NotificationRule rule, String raw)
	{
		NotificationMetric m = rule.getMetric();

		if (m == null || m.isCategorical())
		{
			rule.setValue(raw.trim());
			return;
		}

		if (m.getKind() == NotificationMetric.Kind.PERCENT)
		{
			OptionalDouble v = NotificationRule.parsePercent(raw);
			if (v.isPresent())
				rule.setValue(NotificationRule.formatPercent(v.getAsDouble()));

			return;
		}

		OptionalDouble v = NotificationRule.parseNumeric(raw);
		if (v.isPresent())
			rule.setValue(GpFormat.shortValue((long) v.getAsDouble()));
	}

	/** The client-thread edit seam the model commits through; see {@link DetailViewHost#editNotifications}. */
	interface RuleEditor
	{
		/**
		 * Applies {@code mutation} to the bound item's rule list on the client thread.
		 *
		 * @param mutation applied to the live list on the client thread
		 * @param onApplied run on the EDT once the mutation has been applied
		 */
		void edit(Consumer<List<NotificationRule>> mutation, Runnable onApplied);
	}
}

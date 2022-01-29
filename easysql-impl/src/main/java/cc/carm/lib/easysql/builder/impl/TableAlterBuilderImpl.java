package cc.carm.lib.easysql.builder.impl;

import cc.carm.lib.easysql.action.SQLUpdateActionImpl;
import cc.carm.lib.easysql.api.SQLAction;
import cc.carm.lib.easysql.api.builder.TableAlterBuilder;
import cc.carm.lib.easysql.api.enums.IndexType;
import cc.carm.lib.easysql.builder.AbstractSQLBuilder;
import cc.carm.lib.easysql.manager.SQLManagerImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static cc.carm.lib.easysql.api.SQLBuilder.withBackQuote;
import static cc.carm.lib.easysql.api.SQLBuilder.withQuote;

public class TableAlterBuilderImpl extends AbstractSQLBuilder implements TableAlterBuilder {

	protected final @NotNull String tableName;

	public TableAlterBuilderImpl(@NotNull SQLManagerImpl manager, @NotNull String tableName) {
		super(manager);
		this.tableName = tableName;
	}

	public @NotNull String getTableName() {
		return tableName;
	}

	@Override
	public SQLAction<Integer> renameTo(@NotNull String newTableName) {
		return new SQLUpdateActionImpl(getManager(),
				"ALTER TABLE " + withBackQuote(getTableName()) + " RENAME TO " + withBackQuote(newTableName) + ""
		);
	}

	@Override
	public SQLAction<Integer> changeComment(@NotNull String newTableComment) {
		return new SQLUpdateActionImpl(getManager(),
				"ALTER TABLE " + withBackQuote(getTableName()) + " COMMENT " + withQuote(newTableComment)
		);
	}

	@Override
	public SQLAction<Integer> setAutoIncrementIndex(int index) {
		return new SQLUpdateActionImpl(getManager(),
				"ALTER TABLE " + withBackQuote(getTableName()) + " AUTO_INCREMENT=" + index
		);
	}

	@Override
	public SQLAction<Integer> addIndex(@NotNull IndexType indexType, @NotNull String indexName, @NotNull String columnName, @NotNull String... moreColumns) {
		return createAction(
				"ALTER TABLE " + withBackQuote(getTableName()) + " ADD "
						+ TableCreateBuilderImpl.buildIndexSettings(indexType, indexName, columnName, moreColumns)
		);
	}

	@Override
	public SQLAction<Integer> dropIndex(@NotNull String indexName) {
		return createAction(
				"ALTER TABLE " + withBackQuote(getTableName()) + " DROP INDEX " + withBackQuote(indexName)
		);
	}

	@Override
	public SQLAction<Integer> dropForeignKey(@NotNull String keySymbol) {
		return createAction(
				"ALTER TABLE " + withBackQuote(getTableName()) + " DROP FOREIGN KEY " + withBackQuote(keySymbol)
		);
	}

	@Override
	public SQLAction<Integer> dropPrimaryKey() {
		return createAction(
				"ALTER TABLE " + withBackQuote(getTableName()) + " DROP PRIMARY KEY"
		);
	}

	@Override
	public SQLAction<Integer> addColumn(@NotNull String columnName, @NotNull String settings, @Nullable String afterColumn) {
		String orderSettings = null;
		if (afterColumn != null) {
			if (afterColumn.length() > 0) {
				orderSettings = "AFTER " + withBackQuote(afterColumn);
			} else {
				orderSettings = "FIRST";
			}
		}

		return createAction(
				"ALTER TABLE " + withBackQuote(getTableName()) + " ADD " + withBackQuote(columnName) + " " + settings
						+ (orderSettings != null ? " " + orderSettings : "")
		);
	}

	@Override
	public SQLAction<Integer> renameColumn(@NotNull String columnName, @NotNull String newName) {
		return createAction(
				"ALTER TABLE " + withBackQuote(getTableName()) + " RENAME COLUMN " + withBackQuote(columnName) + " TO " + withBackQuote(newName)
		);
	}

	@Override
	public SQLAction<Integer> modifyColumn(@NotNull String columnName, @NotNull String settings) {
		return createAction(
				"ALTER TABLE " + withBackQuote(getTableName()) + " MODIFY COLUMN " + withBackQuote(columnName) + " " + settings
		);
	}

	@Override
	public SQLAction<Integer> removeColumn(@NotNull String columnName) {
		return createAction(
				"ALTER TABLE " + withBackQuote(getTableName()) + " DROP " + withBackQuote(columnName)
		);
	}

	@Override
	public SQLAction<Integer> setColumnDefault(@NotNull String columnName, @NotNull String defaultValue) {
		return createAction(
				"ALTER TABLE " + withBackQuote(getTableName()) + " ALTER " + withBackQuote(columnName) + " SET DEFAULT " + defaultValue
		);
	}

	@Override
	public SQLAction<Integer> removeColumnDefault(@NotNull String columnName) {
		return createAction(
				"ALTER TABLE " + withBackQuote(getTableName()) + " ALTER " + withBackQuote(columnName) + " DROP DEFAULT"
		);
	}

	private SQLUpdateActionImpl createAction(@NotNull String sql) {
		return new SQLUpdateActionImpl(getManager(), sql);
	}
}

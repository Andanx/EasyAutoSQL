package cc.carm.lib.easysql.action;

import cc.carm.lib.easysql.api.action.PreparedSQLUpdateBatchAction;
import cc.carm.lib.easysql.manager.SQLManagerImpl;
import cc.carm.lib.easysql.util.StatementUtil;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PreparedSQLBatchUpdateActionImpl
        extends AbstractSQLAction<List<Integer>>
        implements PreparedSQLUpdateBatchAction {

    boolean returnKeys = false;
    List<Object[]> allParams;

    public PreparedSQLBatchUpdateActionImpl(@NotNull SQLManagerImpl manager, @NotNull String sql) {
        super(manager, sql);
        this.allParams = new ArrayList<>();
    }

    @Override
    public PreparedSQLUpdateBatchAction setAllParams(Iterable<Object[]> allParams) {
        List<Object[]> paramsList = new ArrayList<>();
        allParams.forEach(paramsList::add);
        this.allParams = paramsList;
        return this;
    }

    @Override
    public PreparedSQLUpdateBatchAction addParamsBatch(Object... params) {
        this.allParams.add(params);
        return this;
    }

    @Override
    public PreparedSQLUpdateBatchAction setReturnGeneratedKeys(boolean returnGeneratedKey) {
        this.returnKeys = returnGeneratedKey;
        return this;
    }

    @Override
    public @NotNull List<Integer> execute() throws SQLException {
        try (Connection connection = getManager().getConnection()) {
            try (PreparedStatement statement = StatementUtil.createPrepareStatementBatch(
                    connection, getSQLContent(), allParams, returnKeys
            )) {

                outputDebugMessage();
                int[] executed = statement.executeBatch();

                if (!returnKeys) return Arrays.stream(executed).boxed().collect(Collectors.toList());
                else {
                    try (ResultSet resultSet = statement.getGeneratedKeys()) {
                        List<Integer> generatedKeys = new ArrayList<>();
                        while (resultSet.next()) {
                            generatedKeys.add(resultSet.getInt(1));
                        }
                        return generatedKeys;
                    }
                }
            }

        }
    }

}
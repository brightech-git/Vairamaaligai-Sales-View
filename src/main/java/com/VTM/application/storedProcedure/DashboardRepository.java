package com.VTM.application.storedProcedure;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DashboardRepository {

    private final JdbcTemplate jdbcTemplate;

    public DashboardRepository(@Qualifier("firstJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<String, Object> getDashboardData(String fromDate,
                                                String toDate,
                                                String costId,
                                                String adminDB,
                                                String transDB,
                                                String schemeDB) {
        return jdbcTemplate.execute((Connection conn) -> {
            String sql = "{? = call dbo.sp_Dashboard(?, ?, ?, ?, ?, ?)}";

            try (CallableStatement cs = conn.prepareCall(sql)) {
                // 1️⃣ Register return value
                cs.registerOutParameter(1, Types.INTEGER);

                // 2️⃣ Set input parameters (all strings)
                cs.setString(2, fromDate);
                cs.setString(3, toDate);
                cs.setString(4, costId);
                cs.setString(5, adminDB);
                cs.setString(6, transDB);
                cs.setString(7, schemeDB);

                // 3️⃣ Execute procedure
                boolean hasResultSet = cs.execute();

                // 4️⃣ Collect multiple result sets
                List<List<Map<String, Object>>> allResultSets = new ArrayList<>();
                while (hasResultSet) {
                    ResultSet rs = cs.getResultSet();
                    List<Map<String, Object>> resultSetList = new ArrayList<>();

                    while (rs.next()) {
                        Map<String, Object> row = new HashMap<>();
                        int colCount = rs.getMetaData().getColumnCount();
                        for (int i = 1; i <= colCount; i++) {
                            row.put(rs.getMetaData().getColumnLabel(i), rs.getObject(i));
                        }
                        resultSetList.add(row);
                    }

                    allResultSets.add(resultSetList);
                    hasResultSet = cs.getMoreResults();
                }

                // 5️⃣ Get return value
                int returnValue = cs.getInt(1);

                // 6️⃣ Build final response
                Map<String, Object> response = new HashMap<>();
                response.put("returnValue", returnValue);
                response.put("resultSets", allResultSets);

                return response;
            }
        });
    }
}

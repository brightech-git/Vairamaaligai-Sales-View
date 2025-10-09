package com.VTM.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class AccountWritePlatformServiceImpl implements AccountWritePlatformService {

    private final JdbcTemplate firstJdbcTemplate;
    private final JdbcTemplate secondJdbcTemplate;
    private final JdbcTemplate thirdJdbcTemplate;



    @Autowired
    public AccountWritePlatformServiceImpl(@Qualifier("firstJdbcTemplate") JdbcTemplate firstJdbcTemplate, @Qualifier("secondJdbcTemplate")JdbcTemplate secondJdbcTemplate, @Qualifier("thirdJdbcTemplate")JdbcTemplate thirdJdbcTemplate) {
        this.firstJdbcTemplate = firstJdbcTemplate;
        this.secondJdbcTemplate = secondJdbcTemplate;

        this.thirdJdbcTemplate = thirdJdbcTemplate;
    }

//    @Override
//    public Map<String, Object> getRates() {
//        String sql = "select * from RateMast where rateid in (select max(rateid) from [VJCsavings]..RateMast) ";
//        return firstJdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
//            Map<String, Object> map = new HashMap<>();
//            map.put("Rate", rs.getFloat("Rate"));
//            map.put("SILVERRATE", rs.getFloat("SILVERRATE"));
//            return map;
//        });
//    }

    @Override
    public List<Map<String, Object>> totalStock() {
        String sql = """
        SELECT 
            SUM(i.grswt) AS Total_Grswt,
            SUM(i.netwt) AS Total_Netwt,
            m.METALID,
            CASE 
                WHEN m.METALID = 'G' THEN 'GOLD'
                WHEN m.METALID = 'S' THEN 'SILVER'
                WHEN m.METALID = 'T' THEN 'STONE'
                WHEN m.METALID = 'D' THEN 'DIAMOND'
                ELSE 'Other'
            END AS MetalCategory
        FROM itemtag AS i
        LEFT JOIN itemmast AS m ON i.itemid = m.itemid
        WHERE i.issdate IS NULL
          AND i.grswt <> 0
        GROUP BY m.METALID
    """;

        return firstJdbcTemplate.query(sql, (rs, rowNum) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("Total_Grswt", rs.getBigDecimal("Total_Grswt"));
            map.put("Total_Netwt", rs.getBigDecimal("Total_Netwt"));
            map.put("Metal_ID", rs.getString("METALID"));
            map.put("MetalCategory", rs.getString("MetalCategory"));
            return map;
        });
    }


    @Override
public List<Map<String, Object>> totalCash(Date startDate, Date endDate) {
        // Query with placeholders for date range
        String sql = "SELECT ISNULL(SUM(amount), 0) - (SELECT ISNULL(SUM(amount), 0)  FROM ACCTRAN WHERE ACCODE = 'cash'\n" +
                "                AND CANCEL <> 'Y' And TRANMODE = 'C' AND trandate BETWEEN ? AND ? AND tranno <> '9999') AS Total_Cash\n" +
                "                FROM ACCTRAN WHERE ACCODE = 'cash' AND CANCEL <> 'Y' And TRANMODE = 'D' AND trandate BETWEEN ? AND ? AND tranno <> '9999'";

        // Execute the query with the provided startDate and endDate
        return secondJdbcTemplate.query(sql, new Object[]{startDate, endDate, startDate, endDate}, (rs, rowNum) -> {
            Map<String, Object> map = new HashMap<>();
            // Retrieve the total cash as BigDecimal (change this if necessary)
            map.put("Total_Cash", rs.getBigDecimal("Total_Cash"));
            return map;
        });

    }

    @Override
    public List<Map<String, Object>> totalSalesWeight(Date startDate, Date endDate) {
        String sql = """
        SELECT 
            SUM(i.grswt) AS TotalSales_Grswt,
            SUM(i.netwt) AS TotalSales_Netwt,
            i.METALID,
            CASE 
                WHEN i.METALID = 'G' THEN 'GOLD'
                WHEN i.METALID = 'S' THEN 'SILVER'
                WHEN i.METALID = 'D' THEN 'DIAMOND'
                WHEN i.METALID = 'T' THEN 'STONE'
                ELSE 'Other'
            END AS MetalCategory
        FROM issue AS i
        WHERE i.CANCEL <> 'Y'
          AND i.trandate BETWEEN ? AND ?
          AND i.TRANTYPE = 'SA'
        GROUP BY i.METALID
    """;

        return secondJdbcTemplate.query(sql, new Object[]{startDate, endDate}, (rs, rowNum) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("TotalSales_Grswt", rs.getBigDecimal("TotalSales_Grswt"));
            map.put("TotalSales_Netwt", rs.getBigDecimal("TotalSales_Netwt"));
            map.put("Metal_ID", rs.getString("METALID"));
            map.put("MetalCategory", rs.getString("MetalCategory"));
            return map;
        });
    }



    @Override
    public List<Map<String, Object>> totalOldGoldPurchaseWeight(Date startDate, Date endDate) {
        String sql = "select  sum(i.grswt) as TotalPurchase_Grswt,sum(i.netwt) as TotalPurcahse_Netwt ,METALID  from RECEIPT as I where  CANCEL<>'Y' and trandate between ? and ? and TRANTYPE='pu'  group by METALID ";

        return secondJdbcTemplate.query(sql, new Object[]{startDate, endDate}, (rs, rowNum) -> {
            Map<String, Object> map = new HashMap<>();

            map.put("TotalPurchase_Grswt", rs.getBigDecimal("TotalPurchase_Grswt"));  // Use BigDecimal for accurate monetary or weight values
            map.put("Metal_ID", rs.getString("METALID"));

            return map;
        });

    }

    @Override
    public List<Map<String, Object>> totalCreditCardBill(Date startDate, Date endDate) {
        String sql = "select sum(AMOUNT) as CreditCard_Bill from ACCTRAN where  CANCEL<>'Y' and trandate between ? and ? and tranno<>'9999' and PAYMODE='CC'";

        return secondJdbcTemplate.query(sql, new Object[]{startDate, endDate}, (rs, rowNum) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("CreditCard_Bill", rs.getBigDecimal("CreditCard_Bill"));  // Use BigDecimal for accurate monetary or weight values
            return map;
        });
    }

    @Override
    public List<Map<String, Object>> getBillCancelledIssues(Date startDate, Date endDate) {
        String sql = "\tSELECT \n" +
                "                i.TRANNO, \n" +
                "                i.NETWT, \n" +
                "                i.TRANDATE,\n" +
                "                (i.AMOUNT + i.TAX) AS AMOUNT, \n" +
                "                u.USERNAME \n" +
                "                FROM VAIT2526..ISSUE i \n" +
                "                LEFT JOIN VAIADMINDB..USERMASTER u ON u.USERID = i.USERID \n" +
                "                WHERE i.CANCEL = 'y' AND CAST(i.TRANDATE AS DATE) BETWEEN ? AND ?\n" +
                "                ORDER BY i.TRANDATE";

        java.sql.Date sqlStart = new java.sql.Date(startDate.getTime());
        java.sql.Date sqlEnd = new java.sql.Date(endDate.getTime());

        return firstJdbcTemplate.query(sql, new Object[]{sqlStart, sqlEnd}, (rs, rowNum) -> {
            Map<String, Object> row = new HashMap<>();
            row.put("TRANNO", rs.getInt("TRANNO"));
            row.put("NETWT", rs.getBigDecimal("NETWT"));
            row.put("TRANDATE", rs.getDate("TRANDATE"));  // 👈 Make sure this exists in SELECT!
            row.put("AMOUNT", rs.getBigDecimal("AMOUNT"));
            row.put("USERNAME", rs.getString("USERNAME"));
            return row;
        });
    }

    @Override
    public List<Map<String, Object>> getAllCostCentres() {
        String sql = "SELECT COSTID, COSTNAME FROM COSTCENTRE";

        return firstJdbcTemplate.query(sql, (rs, rowNum) -> {
            Map<String, Object> row = new HashMap<>();
            row.put("COSTID", rs.getString("COSTID"));
            row.put("COSTNAME", rs.getString("COSTNAME"));
            return row;
        });
    }

    @Override
    public List<Map<String, Object>> getCompanyNames() {
        String sql = "SELECT COMPANYNAME FROM Company";
        return firstJdbcTemplate.query(sql, (rs, rowNum) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("COMPANYNAME", rs.getString("COMPANYNAME"));
            return map;
        });

    }

    @Override
    public Map<String, Object> getTotalChequeAndUPI(Date startDate, Date endDate) {
        String sql = "SELECT SUM(AMOUNT) AS total_Cheque_and_UPI \n" +
                "                FROM ACCTRAN \n" +
                "                WHERE CANCEL <> 'Y' \n" +
                "                AND TRANDATE BETWEEN ? AND ? \n" +
                "                AND TRANNO <> '9999' \n" +
                "                AND PAYMODE = 'CH'";

        return secondJdbcTemplate.queryForObject(sql, new Object[]{startDate, endDate}, (rs, rowNum) -> {
            Map<String, Object> result = new HashMap<>();
            result.put("total_Cheque_and_UPI", rs.getBigDecimal("total_Cheque_and_UPI"));
            return result;
        });
    }

    @Override
    public Map<String, Object> getEstimateSummary(Date startDate, Date endDate) {

            String sql = "SELECT " +
                    " COUNT(DISTINCT tranno) AS TotalEstimate, " +
                    " (SELECT COUNT(DISTINCT tranno) FROM ESTISSUE WHERE TRANDATE BETWEEN ? AND ? AND BATCHNO <> '') AS TotalBilled, " +
                    " (SELECT COUNT(DISTINCT tranno) FROM ESTISSUE WHERE TRANDATE BETWEEN ? AND ? AND BATCHNO = '') AS TotalPending " +
                    "FROM ESTISSUE " +
                    "WHERE TRANDATE BETWEEN ? AND ?";

            return secondJdbcTemplate.queryForObject(sql, new Object[]{startDate, endDate, startDate, endDate, startDate, endDate}, (rs, rowNum) -> {
                Map<String, Object> map = new HashMap<>();
                map.put("TotalEstimate", rs.getInt("TotalEstimate"));
                map.put("TotalBilled", rs.getInt("TotalBilled"));
                map.put("TotalPending", rs.getInt("TotalPending"));
                return map;
            });



    }


    @Override
    public List<Map<String, Object>> getPaymentSummary(Date startDate, Date endDate) {
        String sql = """
        SELECT 
            CASE 
                WHEN PAYMODE = 'CA' THEN 'CASH'
                WHEN PAYMODE = 'CC' THEN 'CARD'
                WHEN PAYMODE = 'CH' THEN 'UPI'
                ELSE 'OTHER'
            END AS Paymode,
            SUM(AMOUNT) AS Amount
        FROM ACCTRAN
        WHERE TRANNO = '9999'
          AND TRANDATE BETWEEN ? AND ?
          AND TRANMODE = 'D'
        GROUP BY PAYMODE
    """;

        return secondJdbcTemplate.query(sql, new Object[]{startDate, endDate}, (rs, rowNum) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("paymode", rs.getString("Paymode"));
            map.put("amount", rs.getBigDecimal("Amount"));
            return map;
        });
    }


        @Override
        public List<Map<String, Object>> getStoneSummary(Date startDate, Date endDate) {
            String sql = """
        SELECT 
            SUM(STNWT) AS STNWT,
            SUM(STNAMT) AS STNAMT,
            SUM(STNPCS) AS STNPCS,
            STONEUNIT 
        FROM ISSSTONE 
        WHERE BATCHNO IN (
            SELECT BATCHNO FROM ISSUE 
            WHERE CANCEL <> 'y'
        )
        AND TRANDATE BETWEEN ? AND ? 
        AND TRANTYPE = 'sa' 
        GROUP BY STONEUNIT
    """;

            List<Object> params = List.of(startDate, endDate);

            List<Map<String, Object>> rows = secondJdbcTemplate.queryForList(sql, params.toArray());

            return rows.stream().map(row -> Map.of(
                    "stoneUnit", row.get("STONEUNIT"),
                    "stnwt", row.get("STNWT"),
                    "stnamt", row.get("STNAMT"),
                    "stnpcs", row.get("STNPCS")
            )).toList();

    }

    @Override
    public Map<String, Object> getRateOFGoldAndSliver() {

            String sql = "  SELECT METALID, PURITY, PRATE \n" +
                    "                FROM RATEMAST \n" +
                    "                WHERE RATEGROUP = (SELECT MAX(RATEGROUP)\n" +
                    "                                   FROM RATEMAST) \n" +
                    "                AND ((METALID = 'G' AND PURITY = '91.60') OR \n" +
                    "                   (METALID = 'P' AND PURITY = '95.00') OR \n" +
                    "                    (METALID = 'S' AND PURITY = '91.60'))";

            List<Map<String, Object>> results = firstJdbcTemplate.query(sql, (rs, rowNum) -> {
                Map<String, Object> row = new HashMap<>();
                row.put("METALID", rs.getString("METALID"));
                row.put("PRATE", rs.getFloat("PRATE"));
                return row;
            });

            Map<String, Object> finalResult = new HashMap<>();

            for (Map<String, Object> row : results) {
                String metalId = (String) row.get("METALID");
                float rate = (float) row.get("PRATE");

                if ("G".equals(metalId)) {
                    finalResult.put("GOLDRATE", rate);
                } else if ("S".equals(metalId)) {
                    finalResult.put("SILVERRATE", rate);
                }else if ("P".equals(metalId)) {
                    finalResult.put("PATTINUMRATE", rate);
                }
            }

            return finalResult;
        }


    public Map<String, Object> getSchemeCollection(Date startDate, Date endDate) {
        String sql = """
            SELECT 
                SUM(WEIGHT) AS COLLECTIONWEIGHT,
                SUM(AMOUNT) AS COLLECTIONAMOUNT
            FROM SCHEMETRAN
            WHERE RDATE BETWEEN ? AND ?
              AND CANCEL <> 'Y'
        """;

        return thirdJdbcTemplate.queryForObject(sql, new Object[]{startDate, endDate}, (rs, rowNum) -> {
            Map<String, Object> map = new HashMap<>();
            BigDecimal weight = rs.getBigDecimal("COLLECTIONWEIGHT");
            BigDecimal amount = rs.getBigDecimal("COLLECTIONAMOUNT");

            map.put("COLLECTIONWEIGHT", weight != null ? weight.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
            map.put("COLLECTIONAMOUNT", amount != null ? amount.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
            return map;
        });
    }

    @Override
    public Map<String, Object> getAdjustmentWeightAmount(Date startDate, Date endDate) {

            String sql = """
            SELECT 
                SUM(T.WEIGHT) AS ADJWEIGHT,
                SUM(T.AMOUNT) AS ADJAMOUNT
            FROM VAISH0708..SCHEMETRAN AS T
            LEFT JOIN VAISAVINGS..SCHEMEMAST AS M 
              ON T.GROUPCODE = M.GROUPCODE AND T.REGNO = M.REGNO
            WHERE T.CANCEL <> 'Y'
              AND M.DOCLOSE BETWEEN ? AND ?
        """;

            return thirdJdbcTemplate.queryForObject(sql, new Object[]{startDate, endDate}, (rs, rowNum) -> {
                Map<String, Object> map = new HashMap<>();
                BigDecimal weight = rs.getBigDecimal("ADJWEIGHT");
                BigDecimal amount = rs.getBigDecimal("ADJAMOUNT");

                map.put("ADJWEIGHT", weight != null ? weight.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
                map.put("ADJAMOUNT", amount != null ? amount.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
                return map;
            });
        }

    @Override
    public Map<String, Object> getSchemeAdjustment(Date startDate, Date endDate, String costId) {
        StringBuilder sql = new StringBuilder("""
            SELECT SUM(AMOUNT) AS Amount
            FROM ACCTRAN
            WHERE TRANDATE BETWEEN ? AND ?
              AND CANCEL <> 'Y'
              AND PAYMODE = 'SS'
        """);

        List<Object> params = new ArrayList<>();
        params.add(startDate);
        params.add(endDate);

        if (costId != null && !costId.trim().isEmpty()) {
            sql.append(" AND COSTID = ?");
            params.add(costId);
        }

        return secondJdbcTemplate.queryForObject(sql.toString(), params.toArray(), (rs, rowNum) -> {
            Map<String, Object> result = new HashMap<>();
            result.put("Amount", rs.getBigDecimal("Amount"));
            return result;
        });
    }


}




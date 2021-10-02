package kg.bakirov.alfa.repositories;

import kg.bakirov.alfa.api.sales.*;
import kg.bakirov.alfa.controllers.SalesController;
import kg.bakirov.alfa.models.sales.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static kg.bakirov.alfa.controllers.HomeController.GLOBAL_FIRM_NO;
import static kg.bakirov.alfa.controllers.HomeController.GLOBAL_PERIOD;
import static kg.bakirov.alfa.controllers.SalesController.*;

@Repository
public class SalesRepository {

    private final MainRepository mainRepository;

    List<SalesFiche> salesFicheList = null;
    List<SalesMonth> salesMonthList = null;
    List<SalesTotal> salesTotalList = null;
    List<SalesClient> salesClientList = null;
    List<SalesClientManager> salesClientManagerList = null;
    List<SaleTable> saleTableList = null;
    List<SaleDetail> saleDetailList = null;
    int totalCount;

    @Autowired
    public SalesRepository(MainRepository mainRepository) {
        this.mainRepository = mainRepository;
    }


    /* ------------------------------------------ Список документов ---------------------------------------------------- */

    public SalesFicheMain getSaleFicheMain() {
        Connection connection = null;

        try {
            connection = mainRepository.getConnection();

            String sqlQuery = "SELECT " +
                    "CASE STFIC.TRCODE " +
                    "WHEN 2 THEN 'Возвратная накладная' " +
                    "WHEN 3 THEN 'Возвратная накладная' " +
                    "WHEN 4 THEN 'Возвратная накладная' " +
                    "WHEN 7 THEN 'Накладная продажи' " +
                    "WHEN 8 THEN 'Накладная продажи' " +
                    "ELSE '' " +
                    "END as trcode, " +
                    "STFIC.FICHENO AS ficheno, CONVERT(varchar, STFIC.DATE_, 23) AS date, " +
                    "ISNULL(CLNTC.CODE, 0) as clientcode, ISNULL(CLNTC.DEFINITION_, 0) as clientname, " +
                    "ROUND(CASE STFIC.TRCODE WHEN 2 THEN -STFIC.GROSSTOTAL WHEN 3 THEN -STFIC.GROSSTOTAL WHEN 4 THEN -STFIC.GROSSTOTAL ELSE STFIC.GROSSTOTAL END, 2) AS gross, " +
                    "ROUND(STFIC.TOTALDISCOUNTS, 2) AS discounts, " +
                    "ROUND(STFIC.TOTALEXPENSES, 2) AS expenses, " +
                    "ROUND(CASE STFIC.TRCODE WHEN 2 THEN -STFIC.NETTOTAL WHEN 3 THEN -STFIC.NETTOTAL WHEN 4 THEN -STFIC.NETTOTAL ELSE STFIC.NETTOTAL END, 2) AS net, " +
                    "ROUND(CASE STFIC.TRCODE WHEN 2 THEN -STFIC.REPORTNET WHEN 3 THEN -STFIC.REPORTNET WHEN 4 THEN -STFIC.REPORTNET ELSE STFIC.REPORTNET END, 2) AS net_usd  " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STFICHE STFIC WITH(NOLOCK) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_INVOICE INVFC WITH(NOLOCK) ON (STFIC.INVOICEREF = INVFC.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC WITH(NOLOCK) ON (STFIC.CLIENTREF = CLNTC.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_SLSMAN SLSMC WITH(NOLOCK) ON (STFIC.SALESMANREF = SLSMC.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_PAYPLANS PAYPL WITH(NOLOCK) ON (STFIC.PAYDEFREF = PAYPL.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_WORKSTAT sWSp WITH(NOLOCK) ON (STFIC.SOURCEWSREF = sWSp.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_WORKSTAT dWSp WITH(NOLOCK) ON (STFIC.DESTWSREF = dWSp.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_DISTORD DISTORD WITH(NOLOCK) ON (STFIC.DISTORDERREF = DISTORD.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_PROJECT PROJECT WITH(NOLOCK) ON (STFIC.PROJECTREF  =  PROJECT.LOGICALREF) " +
                    "WHERE (STFIC.CANCELLED = 0) AND ((STFIC.STATUS IN (0,1)) OR (STFIC.TRCODE IN (2,3,4,7,8,9,35,36,37,38,39))) ";

            if (SalesController.OPERATIONTYPE != null && !SalesController.OPERATIONTYPE.isEmpty()) {
                sqlQuery += "AND (STFIC.TRCODE IN (" + OPERATIONTYPE + ") ) ";
            } else {
                sqlQuery += "AND (STFIC.TRCODE IN (02, 03, 04, 07, 08, 09) ) ";
            }

            if (SalesController.ACCOUNT1 != null && !SalesController.ACCOUNT1.isEmpty() && !SalesController.ACCOUNT1.equals("-")
                    && SalesController.ACCOUNT2 != null && !SalesController.ACCOUNT2.isEmpty() && !SalesController.ACCOUNT2.equals("-")) {
                sqlQuery += "AND (CLNTC.CODE BETWEEN '" + SalesController.ACCOUNT1 + "' AND '" + SalesController.ACCOUNT2 + "') ";
            } else if (SalesController.ACCOUNT1 != null && !SalesController.ACCOUNT1.isEmpty() && !SalesController.ACCOUNT1.equals("-")) {
                sqlQuery += " AND (CLNTC.CODE = '" + SalesController.ACCOUNT1 + "') ";
            }

            if ((BEGDATE != null && ENDDATE != null) && (!BEGDATE.isEmpty() && !ENDDATE.isEmpty()) && (isLegalDate(BEGDATE) && isLegalDate(ENDDATE))) {
                sqlQuery += "AND (STFIC.DATE_ >= " + "'" + BEGDATE + "' AND STFIC.DATE_ <= " + "'" + ENDDATE + "') ";
            }

            sqlQuery += " ORDER BY STFIC.DATE_, STFIC.FTIME, STFIC.TRCODE, STFIC.FICHENO ";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            salesFicheList = new ArrayList<>();
            totalCount = 0;

            while (resultSet.next()) {

                salesFicheList.add(
                        new SalesFiche(
                                resultSet.getString("trcode"),
                                resultSet.getLong("ficheno"),
                                resultSet.getString("date"),
                                resultSet.getString("clientcode"),
                                resultSet.getString("clientname"),
                                resultSet.getDouble("gross"),
                                resultSet.getDouble("discounts"),
                                resultSet.getDouble("expenses"),
                                resultSet.getDouble("net"),
                                resultSet.getDouble("net_usd")
                        )
                );
                totalCount++;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
        return new SalesFicheMain(totalCount, totalCount, salesFicheList);
    }



    /* ------------------------------------------ Итоговые цифры закупок ---------------------------------------------------- */

    public SalesTotalMain getSalesTotalMain() {
        Connection connection = null;

        try {
            connection = mainRepository.getConnection();

            String sqlQuery = "SELECT ITEMS.CODE AS code, ITEMS.NAME AS name, ITEMS.STGRPCODE AS groupCode, " +
                    "SUM(STITOTS.PURAMNT) AS purchase_count, ROUND(SUM(STITOTS.PURCASH), 2) AS purchase_total, " +
                    "ROUND(SUM(STITOTS.PURCURR), 2) AS purchase_total_usd, SUM(STITOTS.SALAMNT) AS sale_count, " +
                    "ROUND(SUM(STITOTS.SALCASH), 2) AS sale_total, ROUND(SUM(STITOTS.SALCURR), 2) AS sale_total_usd " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVTOT STITOTS " +
                    "WITH(NOLOCK, INDEX = I" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVTOT_I2) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_ITEMS ITEMS ON STITOTS.STOCKREF = ITEMS.LOGICALREF " +
                    "WHERE (STITOTS.INVENNO = -1) AND (STITOTS.SALAMNT <> 0) ";

            if (ITEM1_CODE != null && ITEM2_CODE != null && !ITEM1_CODE.equals("-") && !ITEM2_CODE.equals("-")) {
                sqlQuery += "AND (ITEMS.CODE BETWEEN '" + ITEM1_CODE + "' AND '" + ITEM2_CODE + "') ";
            } else if (ITEM1_CODE != null && !ITEM1_CODE.equals("-")) {
                sqlQuery += "AND (ITEMS.CODE = " + "'" + ITEM1_CODE + "') ";
            }

            if (GROUP1 != null && !GROUP1.equals("-")) {
                sqlQuery += "AND (ITEMS.STGRPCODE = " + "'" + GROUP1 + "') ";
            }

            if ((BEGDATE != null && ENDDATE != null) && (!BEGDATE.isEmpty() && !ENDDATE.isEmpty()) && (isLegalDate(BEGDATE) && isLegalDate(ENDDATE))) {
                sqlQuery += "AND (STITOTS.DATE_ >= " + "'" + BEGDATE + "' AND STITOTS.DATE_ <= " + "'" + ENDDATE + "') ";
            }


            sqlQuery += "GROUP BY ITEMS.CODE, ITEMS.NAME, ITEMS.STGRPCODE ORDER BY ITEMS.CODE ";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            salesTotalList = new ArrayList<>();
            totalCount = 0;

            while (resultSet.next()) {

                salesTotalList.add(
                        new SalesTotal(
                                resultSet.getString("code"),
                                resultSet.getString("name"),
                                resultSet.getString("groupCode"),
                                resultSet.getDouble("purchase_count"),
                                resultSet.getDouble("purchase_total"),
                                resultSet.getDouble("purchase_total_usd"),
                                resultSet.getDouble("sale_count"),
                                resultSet.getDouble("sale_total"),
                                resultSet.getDouble("sale_total_usd")
                        )
                );
                totalCount++;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
        return new SalesTotalMain(totalCount, totalCount, salesTotalList);
    }



    /* ------------------------------------------ Распределение закупок по месяцам ---------------------------------------------------- */

    public SalesMonthMain getSalesMonthMain() {
        Connection connection = null;

        try {
            connection = mainRepository.getConnection();

            String sqlQuery = "SELECT ITEMS.CODE, ITEMS.NAME, ITEMS.STGRPCODE, " +
                    "ISNULL((SELECT ITMSM.SALES_AMOUNT FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM WITH(NOLOCK, INDEX = I" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS_I2) " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ = 1) AND (ITMSM.YEAR_ = " + YEAR_ + "))), 0) AS jan, " +
                    "ISNULL((SELECT ITMSM.SALES_AMOUNT FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM WITH(NOLOCK, INDEX = I" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS_I2) " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ = 2) AND (ITMSM.YEAR_ = " + YEAR_ + "))), 0) AS feb, " +
                    "ISNULL((SELECT ITMSM.SALES_AMOUNT FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM WITH(NOLOCK, INDEX = I" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS_I2) " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ = 3) AND (ITMSM.YEAR_ = " + YEAR_ + "))), 0) AS mar, " +
                    "ISNULL((SELECT ITMSM.SALES_AMOUNT FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM WITH(NOLOCK, INDEX = I" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS_I2) " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ = 4) AND (ITMSM.YEAR_ = " + YEAR_ + "))), 0) AS apr, " +
                    "ISNULL((SELECT ITMSM.SALES_AMOUNT FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM WITH(NOLOCK, INDEX = I" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS_I2) " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ = 5) AND (ITMSM.YEAR_ = " + YEAR_ + "))), 0) AS may, " +
                    "ISNULL((SELECT ITMSM.SALES_AMOUNT FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM WITH(NOLOCK, INDEX = I" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS_I2) " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ = 6) AND (ITMSM.YEAR_ = " + YEAR_ + "))), 0) AS jun, " +
                    "ISNULL((SELECT ITMSM.SALES_AMOUNT FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM WITH(NOLOCK, INDEX = I" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS_I2) " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ = 7) AND (ITMSM.YEAR_ = " + YEAR_ + "))), 0) AS jul, " +
                    "ISNULL((SELECT ITMSM.SALES_AMOUNT FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM WITH(NOLOCK, INDEX = I" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS_I2) " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ = 8) AND (ITMSM.YEAR_ = " + YEAR_ + "))), 0) AS aug, " +
                    "ISNULL((SELECT ITMSM.SALES_AMOUNT FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM WITH(NOLOCK, INDEX = I" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS_I2) " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ = 9) AND (ITMSM.YEAR_ = " + YEAR_ + "))), 0) AS sep, " +
                    "ISNULL((SELECT ITMSM.SALES_AMOUNT FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM WITH(NOLOCK, INDEX = I" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS_I2) " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ = 10) AND (ITMSM.YEAR_ = " + YEAR_ + "))), 0) AS oct, " +
                    "ISNULL((SELECT ITMSM.SALES_AMOUNT FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM WITH(NOLOCK, INDEX = I" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS_I2) " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ = 11) AND (ITMSM.YEAR_ = " + YEAR_ + "))), 0) AS nov, " +
                    "ISNULL((SELECT ITMSM.SALES_AMOUNT FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM WITH(NOLOCK, INDEX = I" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS_I2) " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ = 12) AND (ITMSM.YEAR_ = " + YEAR_ + "))), 0) AS dec, " +
                    "ISNULL((SELECT SUM(ITMSM.SALES_AMOUNT) FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM WITH(NOLOCK, INDEX = I" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS_I2) " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ BETWEEN 1 AND 12) AND (ITMSM.YEAR_ = " + YEAR_ + "))), 0) AS TOTAL_COUNT, " +
                    "ROUND(ISNULL((SELECT SUM(ITMSM.SALES_CASHAMNT) FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM WITH(NOLOCK, INDEX = I" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS_I2) " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ BETWEEN 1 AND 12) AND (ITMSM.YEAR_ = " + YEAR_ + "))), 0), 2) AS TOTAL_SUM, " +
                    "ROUND(ISNULL((SELECT SUM(ITMSM.SALES_CURRAMNT) FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM WITH(NOLOCK, INDEX = I" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS_I2) " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ BETWEEN 1 AND 12) AND (ITMSM.YEAR_ = " + YEAR_ + "))), 0), 2) AS TOTAL_USD " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_ITEMS ITEMS " +
                    "WHERE (ITEMS.CARDTYPE) <> 22 AND (ITEMS.ACTIVE = 0) ";

            if (ITEM1_CODE != null && ITEM2_CODE != null && !ITEM1_CODE.equals("-") && !ITEM2_CODE.equals("-")) {
                sqlQuery += "AND (ITEMS.CODE BETWEEN '" + ITEM1_CODE + "' AND '" + ITEM2_CODE + "') ";
            } else if (ITEM1_CODE != null && !ITEM1_CODE.equals("-")) {
                sqlQuery += "AND (ITEMS.CODE = " + "'" + ITEM1_CODE + "') ";
            }

            if (GROUP1 != null && !GROUP1.equals("-")) {
                sqlQuery += "AND (ITEMS.STGRPCODE = " + "'" + GROUP1 + "') ";
            }

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            salesMonthList = new ArrayList<>();
            totalCount = 0;

            while (resultSet.next()) {

                salesMonthList.add(
                        new SalesMonth(
                                resultSet.getString("code"),
                                resultSet.getString("name"),
                                resultSet.getString("STGRPCODE"),
                                resultSet.getDouble("jan"),
                                resultSet.getDouble("feb"),
                                resultSet.getDouble("mar"),
                                resultSet.getDouble("apr"),
                                resultSet.getDouble("may"),
                                resultSet.getDouble("jun"),
                                resultSet.getDouble("jul"),
                                resultSet.getDouble("aug"),
                                resultSet.getDouble("sep"),
                                resultSet.getDouble("oct"),
                                resultSet.getDouble("nov"),
                                resultSet.getDouble("dec"),
                                resultSet.getDouble("TOTAL_COUNT"),
                                resultSet.getDouble("TOTAL_SUM"),
                                resultSet.getDouble("TOTAL_USD")
                        )
                );
                totalCount++;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
        return new SalesMonthMain(totalCount, totalCount, salesMonthList);
    }



    /* ------------------------------------------ Распределение закупок по менеджерам ---------------------------------------------------- */

    public SalesClientManagerMain getSalesClientsManagerMain() {
        Connection connection = null;

        try {
            connection = mainRepository.getConnection();

            String sqlQuery = "SELECT (SELECT CODE FROM LG_" + GLOBAL_FIRM_NO + "_CLCARD WHERE LOGICALREF = CLNTC.PARENTCLREF) AS client_code, " +
                    "(SELECT DEFINITION_ FROM LG_" + GLOBAL_FIRM_NO + "_CLCARD WHERE LOGICALREF = CLNTC.PARENTCLREF) AS client_name, " +
                    "SUM(STRNS.AMOUNT) AS amount, ROUND(SUM(STRNS.LINENET), 2) AS total, " +
                    "ROUND(ISNULL(SUM(STRNS.LINENET / NULLIF(STRNS.REPORTRATE, 0)), 0), 2) AS total_usd, STRNS.TRCODE AS trcode " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE STRNS WITH(NOLOCK) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STFICHE STFIC WITH(NOLOCK) ON (STRNS.STFICHEREF  =  STFIC.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC WITH(NOLOCK) ON (STFIC.CLIENTREF  =  CLNTC.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_ITEMS ITMSC WITH(NOLOCK) ON (STRNS.STOCKREF  =  ITMSC.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_SHIPINFO SHPINF WITH(NOLOCK) ON (STFIC.SHIPINFOREF  =  SHPINF.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_PROJECT PROJECT WITH(NOLOCK) ON (STRNS.PROJECTREF  =  PROJECT.LOGICALREF) " +
                    "WHERE (STRNS.SOURCEINDEX IN (0)) AND (STFIC.DEPARTMENT IN (0)) AND (STFIC.BRANCH IN (0)) " +
                    "AND (STFIC.FACTORYNR IN (0)) AND (STFIC.STATUS IN (0,1)) " +
                    "AND (STRNS.CPSTFLAG <> 1) AND (STRNS.DETLINE <> 1) AND (STRNS.LINETYPE NOT IN (2,3)) " +
                    "AND (STRNS.TRCODE IN (2,3,4,7,8,9,35,36,37,38,39) ) AND (STFIC.CANCELLED = 0) ";

            if (ITEM1_CODE != null && ITEM2_CODE != null && !ITEM1_CODE.equals("-") && !ITEM2_CODE.equals("-")) {
                sqlQuery += "AND (ITMSC.CODE BETWEEN '" + ITEM1_CODE + "' AND '" + ITEM2_CODE + "') ";
            } else if (ITEM1_CODE != null && !ITEM1_CODE.equals("-")) {
                sqlQuery += "AND (ITMSC.CODE = " + "'" + ITEM1_CODE + "') ";
            }

            if (GROUP1 != null && !GROUP1.equals("-")) {
                sqlQuery += "AND (ITMSC.STGRPCODE = " + "'" + GROUP1 + "') ";
            }

            if (SalesController.ACCOUNT1 != null && !SalesController.ACCOUNT1.isEmpty() && !SalesController.ACCOUNT1.equals("-")
                    && SalesController.ACCOUNT2 != null && !SalesController.ACCOUNT2.isEmpty() && !SalesController.ACCOUNT2.equals("-")) {
                sqlQuery += "AND (CLNTC.CODE BETWEEN '" + SalesController.ACCOUNT1 + "' AND '" + SalesController.ACCOUNT2 + "') ";
            } else if (SalesController.ACCOUNT1 != null && !SalesController.ACCOUNT1.isEmpty() && !SalesController.ACCOUNT1.equals("-")) {
                sqlQuery += " AND (CLNTC.CODE = '" + SalesController.ACCOUNT1 + "') ";
            }

            if ((BEGDATE != null && ENDDATE != null) && (!BEGDATE.isEmpty() && !ENDDATE.isEmpty()) && (isLegalDate(BEGDATE) && isLegalDate(ENDDATE))) {
                sqlQuery += "AND (STRNS.DATE_ >= " + "'" + BEGDATE + "' AND STRNS.DATE_ <= " + "'" + ENDDATE + "') ";
            }

            sqlQuery += "GROUP BY CLNTC.PARENTCLREF, STRNS.TRCODE ";
            sqlQuery += "ORDER BY client_code ";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            salesClientManagerList = new ArrayList<>();
            totalCount = 0;
            String currentCode = null;
            double itemAmount = 0, itemTotal = 0, itemTotalUsd = 0, itemAmountRet = 0, itemTotalRet = 0, itemTotalUsdRet = 0;
            HashMap<String, SalesClientManager> map = new HashMap<>();

            while (resultSet.next()) {
                if (resultSet.getString("client_code") == null) {
                    if (resultSet.getDouble("trcode") == 7 || resultSet.getDouble("trcode") == 8) {
                        itemAmount += resultSet.getDouble("amount");
                        itemTotal += resultSet.getDouble("total");
                        itemTotalUsd += resultSet.getDouble("total_usd");
                        itemAmountRet = 0.0;
                        itemTotalRet = 0.0;
                        itemTotalUsdRet = 0.0;
                    } else if (resultSet.getDouble("trcode") == 2 || resultSet.getDouble("trcode") == 3) {
                        itemAmount = 0.0;
                        itemTotal = 0.0;
                        itemTotalUsd = 0.0;
                        itemAmountRet += -resultSet.getDouble("amount");
                        itemTotalRet += -resultSet.getDouble("total");
                        itemTotalUsdRet += -resultSet.getDouble("total_usd");
                    }
                } else {
                    if (resultSet.getString("client_code").equals(currentCode)) {
                        if (resultSet.getDouble("trcode") == 7 || resultSet.getDouble("trcode") == 8) {
                            itemAmount += resultSet.getDouble("amount");
                            itemTotal += resultSet.getDouble("total");
                            itemTotalUsd += resultSet.getDouble("total_usd");
                        } else if (resultSet.getDouble("trcode") == 2 || resultSet.getDouble("trcode") == 3) {
                            itemAmountRet += -resultSet.getDouble("amount");
                            itemTotalRet += -resultSet.getDouble("total");
                            itemTotalUsdRet += -resultSet.getDouble("total_usd");
                        }
                    } else {
                        if (resultSet.getDouble("trcode") == 7 || resultSet.getDouble("trcode") == 8) {
                            itemAmount = resultSet.getDouble("amount");
                            itemTotal = resultSet.getDouble("total");
                            itemTotalUsd = resultSet.getDouble("total_usd");
                            itemAmountRet = 0.0;
                            itemTotalRet = 0.0;
                            itemTotalUsdRet = 0.0;
                        } else if (resultSet.getDouble("trcode") == 2 || resultSet.getDouble("trcode") == 3) {
                            itemAmount = 0.0;
                            itemTotal = 0.0;
                            itemTotalUsd = 0.0;
                            itemAmountRet = -resultSet.getDouble("amount");
                            itemTotalRet = -resultSet.getDouble("total");
                            itemTotalUsdRet = -resultSet.getDouble("total_usd");
                        }
                    }
                }

                SalesClientManager client = new SalesClientManager();
                client.setClientCode(resultSet.getString("client_code"));
                client.setClientName(resultSet.getString("client_name"));
                client.setItemAmount(itemAmount);
                client.setItemTotal(itemTotal);
                client.setItemTotalUsd(itemTotalUsd);
                client.setItemAmountRet(itemAmountRet);
                client.setItemTotalRet(itemTotalRet);
                client.setItemTotalUsdRet(itemTotalUsdRet);
                currentCode = resultSet.getString("client_code");

                map.put(resultSet.getString("client_code"), client);
            }

            for (Map.Entry<String, SalesClientManager> entry : map.entrySet()) {
                salesClientManagerList.add(entry.getValue());
                totalCount++;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
        return new SalesClientManagerMain(totalCount, totalCount, salesClientManagerList);
    }



    /* ------------------------------------------ Распределение закупок по контрагентам ---------------------------------------------------- */

    public SalesClientMain getSalesClientsMain() {
        Connection connection = null;

        try {
            connection = mainRepository.getConnection();

            String sqlQuery = "SELECT CLNTC.CODE AS client_code, CLNTC.DEFINITION_ AS client_name, " +
                    "ITMSC.CODE AS item_code, ITMSC.NAME AS item_name, ITMSC.STGRPCODE AS item_group, " +
                    "SUM(STRNS.AMOUNT) AS amount, ROUND(SUM(STRNS.LINENET), 2) AS total, " +
                    "ROUND(ISNULL(SUM(STRNS.LINENET / NULLIF(STRNS.REPORTRATE, 0)), 0), 2) AS total_usd, STRNS.TRCODE AS trcode " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE STRNS WITH(NOLOCK) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STFICHE STFIC WITH(NOLOCK) ON (STRNS.STFICHEREF  =  STFIC.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC WITH(NOLOCK) ON (STFIC.CLIENTREF  =  CLNTC.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_ITEMS ITMSC WITH(NOLOCK) ON (STRNS.STOCKREF  =  ITMSC.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_SHIPINFO SHPINF WITH(NOLOCK) ON (STFIC.SHIPINFOREF  =  SHPINF.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_PROJECT PROJECT WITH(NOLOCK) ON (STRNS.PROJECTREF  =  PROJECT.LOGICALREF) " +
                    "WHERE (STRNS.SOURCEINDEX IN (0)) AND (STFIC.DEPARTMENT IN (0)) AND (STFIC.BRANCH IN (0)) " +
                    "AND (STFIC.FACTORYNR IN (0)) AND (STFIC.STATUS IN (0,1)) " +
                    "AND (STRNS.CPSTFLAG <> 1) AND (STRNS.DETLINE <> 1) AND (STRNS.LINETYPE NOT IN (2,3)) " +
                    "AND (STRNS.TRCODE IN (2,3,4,7,8,9,35,36,37,38,39) ) AND (STFIC.CANCELLED = 0) ";

            if (ITEM1_CODE != null && ITEM2_CODE != null && !ITEM1_CODE.equals("-") && !ITEM2_CODE.equals("-")) {
                sqlQuery += "AND (ITMSC.CODE BETWEEN '" + ITEM1_CODE + "' AND '" + ITEM2_CODE + "') ";
            } else if (ITEM1_CODE != null && !ITEM1_CODE.equals("-")) {
                sqlQuery += "AND (ITMSC.CODE = " + "'" + ITEM1_CODE + "') ";
            }

            if (GROUP1 != null && !GROUP1.equals("-")) {
                sqlQuery += "AND (ITMSC.STGRPCODE = " + "'" + GROUP1 + "') ";
            }

            if (SalesController.ACCOUNT1 != null && !SalesController.ACCOUNT1.isEmpty() && !SalesController.ACCOUNT1.equals("-")
                    && SalesController.ACCOUNT2 != null && !SalesController.ACCOUNT2.isEmpty() && !SalesController.ACCOUNT2.equals("-")) {
                sqlQuery += "AND (CLNTC.CODE BETWEEN '" + SalesController.ACCOUNT1 + "' AND '" + SalesController.ACCOUNT2 + "') ";
            } else if (SalesController.ACCOUNT1 != null && !SalesController.ACCOUNT1.isEmpty() && !SalesController.ACCOUNT1.equals("-")) {
                sqlQuery += " AND (CLNTC.CODE = '" + SalesController.ACCOUNT1 + "') ";
            }

            if ((BEGDATE != null && ENDDATE != null) && (!BEGDATE.isEmpty() && !ENDDATE.isEmpty()) && (isLegalDate(BEGDATE) && isLegalDate(ENDDATE))) {
                sqlQuery += "AND (STRNS.DATE_ >= " + "'" + BEGDATE + "' AND STRNS.DATE_ <= " + "'" + ENDDATE + "') ";
            }

            sqlQuery += "GROUP BY CLNTC.CODE, CLNTC.DEFINITION_, ITMSC.CODE, ITMSC.NAME, ITMSC.STGRPCODE, STRNS.TRCODE ";
            sqlQuery += "ORDER BY CLNTC.CODE, ITMSC.CODE ";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            salesClientList = new ArrayList<>();
            totalCount = 0;

            while (resultSet.next()) {

                SalesClient client = new SalesClient();

                client.setClientCode(resultSet.getString("client_code"));
                client.setClientName(resultSet.getString("client_name"));
                client.setItemCode(resultSet.getString("item_code"));
                client.setItemName(resultSet.getString("item_name"));
                client.setItemGroup(resultSet.getString("item_group"));

                if (resultSet.getDouble("trcode") == 7 || resultSet.getDouble("trcode") == 8) {
                    client.setItemAmount(resultSet.getDouble("amount"));
                    client.setItemTotal(resultSet.getDouble("total"));
                    client.setItemTotalUsd(resultSet.getDouble("total_usd"));
                    client.setItemAmountRet(0.0);
                    client.setItemTotalRet(0.0);
                    client.setItemTotalUsdRet(0.0);
                } else if (resultSet.getDouble("trcode") == 2 || resultSet.getDouble("trcode") == 3) {
                    client.setItemAmount(0.0);
                    client.setItemTotal(0.0);
                    client.setItemTotalUsd(0.0);
                    client.setItemAmountRet(-resultSet.getDouble("amount"));
                    client.setItemTotalRet(-resultSet.getDouble("total"));
                    client.setItemTotalUsdRet(-resultSet.getDouble("total_usd"));
                }

                salesClientList.add(client);
                totalCount++;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
        return new SalesClientMain(totalCount, totalCount, salesClientList);
    }




    /* ------------------------------------------ Сводная таблица продаж ---------------------------------------------------- */

    public SalesTableMain getSalesTableMain() {
        Connection connection = null;

        Map<Integer, SaleTable> map_month = new HashMap<>();

        try {
            connection = mainRepository.getConnection();

            String sqlQuery = "SELECT " +
                    "INVFC.DATE_ AS date, INVFC.TRCODE, " +
                    "STRNS.LINETYPE, " +
                    "SUM(ROUND(ISNULL(STRNS.TOTAL, 0) , 2)) AS total, " +
                    "SUM(ROUND(ISNULL(STRNS.LINENET, 0) ,2)) AS net, " +
                    "SUM(ROUND(ISNULL(STRNS.TOTAL / NULLIF(STRNS.REPORTRATE, 0), 0), 2)) AS total_usd, " +
                    "SUM(ROUND(ISNULL(STRNS.LINENET / NULLIF(STRNS.REPORTRATE, 0), 0), 2)) AS net_usd " +
                    "FROM " +
                    "LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_INVOICE INVFC WITH(NOLOCK) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE STRNS WITH(NOLOCK) ON (INVFC.LOGICALREF = STRNS.INVOICEREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC WITH(NOLOCK) ON (INVFC.CLIENTREF = CLNTC.LOGICALREF) " +
                    "LEFT OUTER JOIN  LG_SLSMAN WITH(NOLOCK) ON (STRNS.SALESMANREF = LG_SLSMAN.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_ITEMS ITMSC WITH(NOLOCK) ON (STRNS.STOCKREF = ITMSC.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_PROJECT PROJECT WITH(NOLOCK) ON (STRNS.PROJECTREF  =  PROJECT.LOGICALREF) " +
                    " WHERE " +
                    "(INVFC.DEPARTMENT IN (0)) AND (INVFC.BRANCH IN (0)) AND (INVFC.FACTORYNR IN (0)) " +
                    "AND (INVFC.STATUS IN (0,1)) AND (NOT(INVFC.TRCODE IN(5, 10)) ) " +
                    "AND (INVFC.CANCELLED =  0 ) AND (INVFC.GRPCODE = 2) ";

            if (ACCOUNT1 != null && !ACCOUNT1.isEmpty() && !ACCOUNT1.equals("-")
                    && ACCOUNT2 != null && !ACCOUNT2.isEmpty() && !ACCOUNT2.equals("-")) {
                sqlQuery += "AND (CLNTC.CODE BETWEEN '" + ACCOUNT1 + "' AND '" + ACCOUNT2 + "') ";
            } else if (ACCOUNT1 != null && !ACCOUNT1.isEmpty() && !ACCOUNT1.equals("-")) {
                sqlQuery += " AND (CLNTC.CODE = '" + ACCOUNT1 + "') ";
            }

            sqlQuery += "GROUP BY INVFC.DATE_, INVFC.TRCODE, STRNS.LINETYPE " +
                    "ORDER BY INVFC.DATE_, INVFC.TRCODE, STRNS.LINETYPE ";


            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            saleTableList = new ArrayList<>();

            double total = 0;
            double net = 0;
            double expenses = 0;
            double discounts = 0;
            double ret = 0;
            double ret_usd = 0;
            double net_usd = 0;

            int year;
            int currentMonth = 0;
            int currentWeek = 0;

            if (ENDDATE == null || ENDDATE.equals("1")) {

                while (resultSet.next()) {

                    String date = resultSet.getString("date");
                    Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(date);

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date1);

                    int month = calendar.get(Calendar.MONTH) + 1;
                    SaleTable saleTable = new SaleTable();

                    if (month != currentMonth) {
                        total = 0;
                        net = 0;
                        expenses = 0;
                        discounts = 0;
                        ret = 0;
                        ret_usd = 0;
                        net_usd = 0;
                    }

                    if (resultSet.getInt("LINETYPE") == 0) {
                        if (resultSet.getInt("TRCODE") == 7 || resultSet.getInt("TRCODE") == 8) {
                            total = total + resultSet.getDouble("total");
                            net = net + resultSet.getDouble("net");
                            net_usd = net_usd + resultSet.getDouble("net_usd");
                        } else if (resultSet.getInt("TRCODE") == 2 || resultSet.getInt("TRCODE") == 3) {
                            ret = ret + resultSet.getDouble("total");
                            ret_usd = ret_usd + resultSet.getDouble("total_usd");
                        }
                    } else if (resultSet.getInt("LINETYPE") == 3) {
                        expenses = expenses + resultSet.getDouble("total");
                    } else if (resultSet.getInt("LINETYPE") == 2) {
                        discounts = discounts + resultSet.getDouble("total");
                    }

                    saleTable.setDate(month);

                    saleTable.setTotal(total - ret);
                    saleTable.setDiscounts(discounts);
                    saleTable.setExpenses(expenses);
                    saleTable.setNet(net - ret);
                    saleTable.setNet_usd(net_usd - ret_usd);
                    saleTable.setReturnAmount(ret);

                    currentMonth = month;
                    map_month.put(saleTable.getDate(), saleTable);
                }

                Map<Integer, SaleTable> sortTable = new TreeMap<>(map_month);

                for (Map.Entry<Integer, SaleTable> entry : sortTable.entrySet()) {
                    saleTableList.add(entry.getValue());
                }

            } else if (ENDDATE.equals("2")) {

                while (resultSet.next()) {

                    String date = resultSet.getString("date");
                    Date simpleFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).parse(date);

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(simpleFormat);
                    calendar.setFirstDayOfWeek(Calendar.TUESDAY);

                    int week = calendar.get(Calendar.WEEK_OF_YEAR);

                    SaleTable saleTable = new SaleTable();

                    if (week != currentWeek) {
                        total = 0;
                        net = 0;
                        expenses = 0;
                        discounts = 0;
                        ret = 0;
                        ret_usd = 0;
                        net_usd = 0;
                    }

                    if (resultSet.getInt("LINETYPE") == 0) {
                        if (resultSet.getInt("TRCODE") == 7 || resultSet.getInt("TRCODE") == 8) {
                            total = total + resultSet.getDouble("total");
                            net = net + resultSet.getDouble("net");
                            net_usd = net_usd + resultSet.getDouble("net_usd");
                        } else if (resultSet.getInt("TRCODE") == 2 || resultSet.getInt("TRCODE") == 3) {
                            ret = ret + resultSet.getDouble("total");
                            ret_usd = ret_usd + resultSet.getDouble("total_usd");
                        }
                    } else if (resultSet.getInt("LINETYPE") == 3) {
                        expenses = expenses + resultSet.getDouble("total");
                    } else if (resultSet.getInt("LINETYPE") == 2) {
                        discounts = discounts + resultSet.getDouble("total");
                    }

                    saleTable.setDate(week);

                    saleTable.setTotal(total - ret);
                    saleTable.setDiscounts(discounts);
                    saleTable.setExpenses(expenses);
                    saleTable.setNet(net - ret);
                    saleTable.setNet_usd(net_usd - ret_usd);
                    saleTable.setReturnAmount(ret);

                    currentWeek = week;
                    map_month.put(saleTable.getDate(), saleTable);
                }

                Map<Integer, SaleTable> sortTable = new TreeMap<>(map_month);

                for (Map.Entry<Integer, SaleTable> entry : sortTable.entrySet()) {
                    saleTableList.add(entry.getValue());
                }

            }


        } catch (SQLException | ParseException throwables) {
            throwables.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
        return new SalesTableMain(map_month.size(), map_month.size(), saleTableList);
    }



    /* ------------------------------------------ Подробный отчет продаж ---------------------------------------------------- */

    public SalesDetailMain getSalesDetailMain() {
        Connection connection = null;

        try {
            connection = mainRepository.getConnection();

            String sqlQuery = "Select ITEMS.CODE, ITEMS.NAME, ITEMS.STGRPCODE, " +
                    "ROUND((SELECT SUM(((2.5-STRNS.IOCODE)/ABS(2.5-STRNS.IOCODE)) * STRNS.AMOUNT*(STRNS.UINFO2/STRNS.UINFO1)) " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE STRNS, LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC " +
                    "WHERE (STRNS.STOCKREF = ITEMS.LOGICALREF) " +
                    "AND (STRNS.CANCELLED = 0) AND (STRNS.STFICHEREF <> 0) " +
                    "AND (STRNS.LPRODSTAT <> 2) AND (STRNS.LINETYPE IN (0, 1, 5, 6, 8, 9)) " +
                    "AND (STRNS.TRCODE IN (2,3)) AND (STRNS.CLIENTREF = CLNTC.LOGICALREF) ";

            if (SalesController.ACCOUNT1 != null && !SalesController.ACCOUNT1.isEmpty() && !SalesController.ACCOUNT1.equals("-")
                    && SalesController.ACCOUNT2 != null && !SalesController.ACCOUNT2.isEmpty() && !SalesController.ACCOUNT2.equals("-")) {
                sqlQuery += "AND (CLNTC.CODE BETWEEN '" + SalesController.ACCOUNT1 + "' AND '" + SalesController.ACCOUNT2 + "') ";
            } else if (SalesController.ACCOUNT1 != null && !SalesController.ACCOUNT1.isEmpty() && !SalesController.ACCOUNT1.equals("-")) {
                sqlQuery += " AND (CLNTC.CODE = '" + SalesController.ACCOUNT1 + "') ";
            }

            if ((BEGDATE != null && ENDDATE != null) && (!BEGDATE.isEmpty() && !ENDDATE.isEmpty()) && (isLegalDate(BEGDATE) && isLegalDate(ENDDATE))) {
                sqlQuery += "AND (STRNS.DATE_ >= " + "'" + BEGDATE + "' AND STRNS.DATE_ <= " + "'" + ENDDATE + "') ";
            }

            sqlQuery += "),2) as iade, " +
                    "ROUND(ISNULL((SELECT SUM(((2.5-STRNS.IOCODE)/ABS(2.5-STRNS.IOCODE)) * (STRNS.TOTAL+STRNS.DISTEXP-STRNS.DISTDISC+STRNS.DIFFPRICE-STRNS.VATINC*STRNS.VATAMNT)/STRNS.REPORTRATE) " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE STRNS, LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC " +
                    "WHERE (STRNS.STOCKREF = ITEMS.LOGICALREF) AND (STRNS.REPORTRATE > 0.0001) " +
                    "AND (STRNS.CANCELLED = 0) AND (STRNS.STFICHEREF <> 0) AND (STRNS.LPRODSTAT <> 2) " +
                    "AND (STRNS.LINETYPE IN (0, 1, 5, 6, 8, 9)) " +
                    "AND ((STRNS.TRCODE IN (2,3)) OR (((STRNS.TRCODE = 25) AND (STRNS.IOCODE = 2)) AND STRNS.TRCODE IN (2,3))) " +
                    "AND (STRNS.CLIENTREF = CLNTC.LOGICALREF) ";

            if (SalesController.ACCOUNT1 != null && !SalesController.ACCOUNT1.isEmpty() && !SalesController.ACCOUNT1.equals("-")
                    && SalesController.ACCOUNT2 != null && !SalesController.ACCOUNT2.isEmpty() && !SalesController.ACCOUNT2.equals("-")) {
                sqlQuery += "AND (CLNTC.CODE BETWEEN '" + SalesController.ACCOUNT1 + "' AND '" + SalesController.ACCOUNT2 + "') ";
            } else if (SalesController.ACCOUNT1 != null && !SalesController.ACCOUNT1.isEmpty() && !SalesController.ACCOUNT1.equals("-")) {
                sqlQuery += " AND (CLNTC.CODE = '" + SalesController.ACCOUNT1 + "') ";
            }

            if ((BEGDATE != null && ENDDATE != null) && (!BEGDATE.isEmpty() && !ENDDATE.isEmpty()) && (isLegalDate(BEGDATE) && isLegalDate(ENDDATE))) {
                sqlQuery += "AND (STRNS.DATE_ >= " + "'" + BEGDATE + "' AND STRNS.DATE_ <= " + "'" + ENDDATE + "') ";
            }

            sqlQuery += "),0),2) as iade_tutari_usd, " +
                    "ROUND(ISNULL((SELECT SUM(((2.5-STRNS.IOCODE)/ABS(2.5-STRNS.IOCODE)) * STRNS.OUTCOSTCURR*STRNS.UINFO2/STRNS.UINFO1*STRNS.AMOUNT) " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE STRNS, LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC " +
                    "WHERE (STRNS.STOCKREF = ITEMS.LOGICALREF) AND (STRNS.CANCELLED = 0) AND (STRNS.STFICHEREF <> 0) " +
                    "AND (STRNS.LPRODSTAT <> 2) AND (STRNS.LINETYPE IN (0, 1, 5, 6, 8, 9)) AND (STRNS.TRCODE IN (2,3)) " +
                    "AND (STRNS.CLIENTREF = CLNTC.LOGICALREF) ";

            if (SalesController.ACCOUNT1 != null && !SalesController.ACCOUNT1.isEmpty() && !SalesController.ACCOUNT1.equals("-")
                    && SalesController.ACCOUNT2 != null && !SalesController.ACCOUNT2.isEmpty() && !SalesController.ACCOUNT2.equals("-")) {
                sqlQuery += "AND (CLNTC.CODE BETWEEN '" + SalesController.ACCOUNT1 + "' AND '" + SalesController.ACCOUNT2 + "') ";
            } else if (SalesController.ACCOUNT1 != null && !SalesController.ACCOUNT1.isEmpty() && !SalesController.ACCOUNT1.equals("-")) {
                sqlQuery += " AND (CLNTC.CODE = '" + SalesController.ACCOUNT1 + "') ";
            }

            if ((BEGDATE != null && ENDDATE != null) && (!BEGDATE.isEmpty() && !ENDDATE.isEmpty()) && (isLegalDate(BEGDATE) && isLegalDate(ENDDATE))) {
                sqlQuery += "AND (STRNS.DATE_ >= " + "'" + BEGDATE + "' AND STRNS.DATE_ <= " + "'" + ENDDATE + "') ";
            }

            sqlQuery += " ), 0), 2) as iade_maliyeti_usd," +
                    "ROUND(ISNULL((SELECT SUM(((2.5-STRNS.IOCODE)/ABS(2.5-STRNS.IOCODE)) * STRNS.AMOUNT*(STRNS.UINFO2/STRNS.UINFO1)) " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE STRNS, LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC " +
                    "WHERE (STRNS.STOCKREF = ITEMS.LOGICALREF) AND (STRNS.CANCELLED = 0) AND (STRNS.STFICHEREF <> 0) " +
                    "AND (STRNS.LPRODSTAT <> 2) AND (STRNS.LINETYPE IN (0, 1, 5, 6, 8, 9)) AND ((STRNS.IOCODE IN (3,4))) " +
                    "AND ((STRNS.TRCODE IN (7,8)) OR (((STRNS.TRCODE = 25) AND (STRNS.IOCODE = 3)) " +
                    "AND STRNS.TRCODE IN (7,8)  )) AND (STRNS.CLIENTREF = CLNTC.LOGICALREF) ";

            if (SalesController.ACCOUNT1 != null && !SalesController.ACCOUNT1.isEmpty() && !SalesController.ACCOUNT1.equals("-")
                    && SalesController.ACCOUNT2 != null && !SalesController.ACCOUNT2.isEmpty() && !SalesController.ACCOUNT2.equals("-")) {
                sqlQuery += "AND (CLNTC.CODE BETWEEN '" + SalesController.ACCOUNT1 + "' AND '" + SalesController.ACCOUNT2 + "') ";
            } else if (SalesController.ACCOUNT1 != null && !SalesController.ACCOUNT1.isEmpty() && !SalesController.ACCOUNT1.equals("-")) {
                sqlQuery += " AND (CLNTC.CODE = '" + SalesController.ACCOUNT1 + "') ";
            }

            if ((BEGDATE != null && ENDDATE != null) && (!BEGDATE.isEmpty() && !ENDDATE.isEmpty()) && (isLegalDate(BEGDATE) && isLegalDate(ENDDATE))) {
                sqlQuery += "AND (STRNS.DATE_ >= " + "'" + BEGDATE + "' AND STRNS.DATE_ <= " + "'" + ENDDATE + "') ";
            }

            sqlQuery += "), 0), 2) as net_satis, " +
                    "ROUND(ISNULL((SELECT SUM(((2.5-STRNS.IOCODE)/ABS(2.5-STRNS.IOCODE)) * (STRNS.TOTAL+STRNS.DISTEXP-STRNS.DISTDISC+STRNS.DIFFPRICE-STRNS.VATINC*STRNS.VATAMNT)/STRNS.REPORTRATE) " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE STRNS, LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC " +
                    "WHERE (STRNS.STOCKREF = ITEMS.LOGICALREF) " +
                    "AND (STRNS.REPORTRATE > 0.0001) AND (STRNS.CANCELLED = 0) AND (STRNS.STFICHEREF <> 0) AND (STRNS.LPRODSTAT <> 2) " +
                    "AND (STRNS.LINETYPE IN (0, 1, 5, 6, 8, 9)) AND ((STRNS.IOCODE IN (3,4))) " +
                    "AND ((STRNS.TRCODE IN (7,8)) OR (((STRNS.TRCODE = 25) AND (STRNS.IOCODE = 3)) AND STRNS.TRCODE IN (7,8)  )) AND (STRNS.CLIENTREF = CLNTC.LOGICALREF)";

            if (SalesController.ACCOUNT1 != null && !SalesController.ACCOUNT1.isEmpty() && !SalesController.ACCOUNT1.equals("-")
                    && SalesController.ACCOUNT2 != null && !SalesController.ACCOUNT2.isEmpty() && !SalesController.ACCOUNT2.equals("-")) {
                sqlQuery += "AND (CLNTC.CODE BETWEEN '" + SalesController.ACCOUNT1 + "' AND '" + SalesController.ACCOUNT2 + "') ";
            } else if (SalesController.ACCOUNT1 != null && !SalesController.ACCOUNT1.isEmpty() && !SalesController.ACCOUNT1.equals("-")) {
                sqlQuery += " AND (CLNTC.CODE = '" + SalesController.ACCOUNT1 + "') ";
            }

            if ((BEGDATE != null && ENDDATE != null) && (!BEGDATE.isEmpty() && !ENDDATE.isEmpty()) && (isLegalDate(BEGDATE) && isLegalDate(ENDDATE))) {
                sqlQuery += "AND (STRNS.DATE_ >= " + "'" + BEGDATE + "' AND STRNS.DATE_ <= " + "'" + ENDDATE + "') ";
            }

            sqlQuery += "), 0), 2) as satis_tutari_usd, " +
                    "ROUND(ISNULL((SELECT SUM(((2.5-STRNS.IOCODE)/ABS(2.5-STRNS.IOCODE)) * STRNS.OUTCOSTCURR*STRNS.UINFO2/STRNS.UINFO1*STRNS.AMOUNT) " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE STRNS, LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC " +
                    "WHERE (STRNS.STOCKREF = ITEMS.LOGICALREF) AND (STRNS.CANCELLED = 0) AND (STRNS.STFICHEREF <> 0) AND (STRNS.LPRODSTAT <> 2) " +
                    "AND (STRNS.LINETYPE IN (0, 1, 5, 6, 8, 9)) AND (STRNS.TRCODE IN (7,8)) AND (STRNS.CLIENTREF = CLNTC.LOGICALREF) ";

            if (SalesController.ACCOUNT1 != null && !SalesController.ACCOUNT1.isEmpty() && !SalesController.ACCOUNT1.equals("-")
                    && SalesController.ACCOUNT2 != null && !SalesController.ACCOUNT2.isEmpty() && !SalesController.ACCOUNT2.equals("-")) {
                sqlQuery += "AND (CLNTC.CODE BETWEEN '" + SalesController.ACCOUNT1 + "' AND '" + SalesController.ACCOUNT2 + "') ";
            } else if (SalesController.ACCOUNT1 != null && !SalesController.ACCOUNT1.isEmpty() && !SalesController.ACCOUNT1.equals("-")) {
                sqlQuery += " AND (CLNTC.CODE = '" + SalesController.ACCOUNT1 + "') ";
            }

            if ((BEGDATE != null && ENDDATE != null) && (!BEGDATE.isEmpty() && !ENDDATE.isEmpty()) && (isLegalDate(BEGDATE) && isLegalDate(ENDDATE))) {
                sqlQuery += "AND (STRNS.DATE_ >= " + "'" + BEGDATE + "' AND STRNS.DATE_ <= " + "'" + ENDDATE + "') ";
            }

            sqlQuery += "), 0), 2) as  satis_maliyeti_usd " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_ITEMS As ITEMS WHERE (CARDTYPE <> 22) ";


            if (SalesController.ACCOUNT1 != null && !SalesController.ACCOUNT1.isEmpty() && !SalesController.ACCOUNT1.equals("-")
                    && SalesController.ACCOUNT2 != null && !SalesController.ACCOUNT2.isEmpty() && !SalesController.ACCOUNT2.equals("-")) {
                sqlQuery += "AND (ITEMS.LOGICALREF IN (SELECT STOCKREF FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE STRNS, LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC " +
                        "WHERE (STRNS.CLIENTREF = CLNTC.LOGICALREF) " +
                        "AND (CLNTC.CODE BETWEEN '" + SalesController.ACCOUNT1 + "' AND '" + SalesController.ACCOUNT2 + "'))) ";
            } else if (SalesController.ACCOUNT1 != null && !SalesController.ACCOUNT1.isEmpty() && !SalesController.ACCOUNT1.equals("-")) {
                sqlQuery += " AND (ITEMS.LOGICALREF IN (SELECT STOCKREF FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE STRNS, " +
                        "LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC WHERE (STRNS.CLIENTREF = CLNTC.LOGICALREF) AND (CLNTC.CODE = '" + SalesController.ACCOUNT1 + "'))) ";
            }

            sqlQuery += "ORDER BY CODE";


            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            saleDetailList = new ArrayList<>();
            totalCount = 0;

            while (resultSet.next()) {

                SaleDetail detail = new SaleDetail();

                double saleCount = resultSet.getDouble("net_satis") + resultSet.getDouble("iade");
                double saleTotal = resultSet.getDouble("satis_tutari_usd") + resultSet.getDouble("iade_tutari_usd");
                double saleCost = resultSet.getDouble("satis_maliyeti_usd") + resultSet.getDouble("iade_maliyeti_usd");
                double profitPercent = 0;
                if (saleCost != 0) {
                    profitPercent = ((saleTotal * 100) / saleCost) - 100;
                }
                detail.setCode(resultSet.getString("code"));
                detail.setName(resultSet.getString("name"));
                detail.setItemGroup(resultSet.getString("STGRPCODE"));
                detail.setRetCount(resultSet.getDouble("iade"));
                detail.setRetTotal(resultSet.getDouble("iade_tutari_usd"));
                detail.setRetCost(resultSet.getDouble("iade_maliyeti_usd"));
                detail.setSaleCount(-saleCount);
                detail.setSaleTotal(-Math.round(saleTotal * 100d) / 100d);
                detail.setSaleCost(-Math.round(saleCost * 100d) / 100d);
                detail.setProfitTotal(-Math.round((saleTotal - saleCost) * 100d) / 100d);
                detail.setProfitPercent(Math.round(profitPercent * 100d) / 100d);

                saleDetailList.add(detail);
                totalCount++;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
        return new SalesDetailMain(totalCount, totalCount, saleDetailList);
    }


    public static boolean isLegalDate(String date) {
        return date.length() <= 10;
    }

}

package kg.bakirov.alfa.repositories;

import kg.bakirov.alfa.api.purchases.PurchasesClientMain;
import kg.bakirov.alfa.api.purchases.PurchasesFicheMain;
import kg.bakirov.alfa.api.purchases.PurchasesMonthMain;
import kg.bakirov.alfa.api.purchases.PurchasesTotalMain;
import kg.bakirov.alfa.controllers.PurchasesController;
import kg.bakirov.alfa.models.purchases.PurchasesClient;
import kg.bakirov.alfa.models.purchases.PurchasesFiche;
import kg.bakirov.alfa.models.purchases.PurchasesMonth;
import kg.bakirov.alfa.models.purchases.PurchasesTotal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static kg.bakirov.alfa.controllers.HomeController.GLOBAL_FIRM_NO;
import static kg.bakirov.alfa.controllers.HomeController.GLOBAL_PERIOD;
import static kg.bakirov.alfa.controllers.PurchasesController.*;

@Repository
public class PurchasesRepository {

    private final MainRepository mainRepository;

    List<PurchasesFiche> purchasesFicheList = null;
    List<PurchasesMonth> purchasesMonthList = null;
    List<PurchasesTotal> purchasesTotalList = null;
    List<PurchasesClient> purchasesClientList = null;
    int totalCount;

    @Autowired
    public PurchasesRepository(MainRepository mainRepository) {
        this.mainRepository = mainRepository;
    }


    /* ------------------------------------------ Список документов ---------------------------------------------------- */

    public PurchasesFicheMain getPurchasesFicheMain() {
        Connection connection = null;

        try {
            connection = mainRepository.getConnection();

            String sqlQuery = "SELECT " +
                    "CASE STFIC.TRCODE " +
                    "WHEN 1 THEN 'Закупочная накладная' " +
                    "WHEN 6 THEN 'Возвратная накладная' " +
                    "ELSE '' " +
                    "END as trcode, " +
                    "STFIC.FICHENO AS ficheno, CONVERT(varchar, STFIC.DATE_, 23) AS date, " +
                    "ISNULL(CLNTC.CODE, 0) as clientcode, ISNULL(CLNTC.DEFINITION_, 0) as clientname, " +
                    "ROUND(CASE STFIC.TRCODE WHEN 6 THEN -STFIC.GROSSTOTAL ELSE STFIC.GROSSTOTAL END, 2) AS gross, " +
                    "ROUND(STFIC.TOTALDISCOUNTS, 2) AS discounts, " +
                    "ROUND(STFIC.TOTALEXPENSES, 2) AS expenses, " +
                    "ROUND(CASE STFIC.TRCODE WHEN 6 THEN -STFIC.NETTOTAL ELSE STFIC.NETTOTAL END, 2) AS net, " +
                    "ROUND(CASE STFIC.TRCODE WHEN 6 THEN -STFIC.REPORTNET ELSE STFIC.REPORTNET END, 2) AS net_usd  " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STFICHE STFIC WITH(NOLOCK) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_INVOICE INVFC WITH(NOLOCK) ON (STFIC.INVOICEREF = INVFC.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC WITH(NOLOCK) ON (STFIC.CLIENTREF = CLNTC.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_SLSMAN SLSMC WITH(NOLOCK) ON (STFIC.SALESMANREF = SLSMC.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_PAYPLANS PAYPL WITH(NOLOCK) ON (STFIC.PAYDEFREF = PAYPL.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_WORKSTAT sWSp WITH(NOLOCK) ON (STFIC.SOURCEWSREF = sWSp.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_WORKSTAT dWSp WITH(NOLOCK) ON (STFIC.DESTWSREF = dWSp.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_DISTORD DISTORD WITH(NOLOCK) ON (STFIC.DISTORDERREF = DISTORD.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_PROJECT PROJECT WITH(NOLOCK) ON (STFIC.PROJECTREF  =  PROJECT.LOGICALREF) " +
                    "WHERE (STFIC.CANCELLED = 0) AND ((STFIC.STATUS IN (0,1)) ) ";

            if (PurchasesController.OPERATIONTYPE != null && !PurchasesController.OPERATIONTYPE.isEmpty()) {
                sqlQuery += "AND (STFIC.TRCODE IN (" + OPERATIONTYPE + ") ) ";
            } else {
                sqlQuery += "AND (STFIC.TRCODE IN (01, 06) ) ";
            }

            if (PurchasesController.ACCOUNT1 != null && !PurchasesController.ACCOUNT1.isEmpty() && !PurchasesController.ACCOUNT1.equals("-")
                    && PurchasesController.ACCOUNT2 != null && !PurchasesController.ACCOUNT2.isEmpty() && !PurchasesController.ACCOUNT2.equals("-")) {
                sqlQuery += "AND (CLNTC.CODE BETWEEN '" + PurchasesController.ACCOUNT1 + "' AND '" + PurchasesController.ACCOUNT2 + "') ";
            } else if (PurchasesController.ACCOUNT1 != null && !PurchasesController.ACCOUNT1.isEmpty() && !PurchasesController.ACCOUNT1.equals("-")) {
                sqlQuery += " AND (CLNTC.CODE = '" + PurchasesController.ACCOUNT1 + "') ";
            }

            if ((BEGDATE != null && ENDDATE != null) && (!BEGDATE.isEmpty() && !ENDDATE.isEmpty()) && (isLegalDate(BEGDATE) && isLegalDate(ENDDATE))) {
                sqlQuery += "AND (STFIC.DATE_ >= " + "'" + BEGDATE + "' AND STFIC.DATE_ <= " + "'" + ENDDATE + "') ";
            }

            sqlQuery += "ORDER BY STFIC.DATE_, STFIC.FTIME, STFIC.TRCODE, STFIC.FICHENO ";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            purchasesFicheList = new ArrayList<>();
            totalCount = 0;

            while (resultSet.next()) {

                purchasesFicheList.add(
                        new PurchasesFiche(
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
        return new PurchasesFicheMain(totalCount, totalCount, purchasesFicheList);
    }



    /* ------------------------------------------ Итоговые цифры закупок ---------------------------------------------------- */

    public PurchasesTotalMain getPurchasesTotalMain() {
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
                    "WHERE (STITOTS.INVENNO = -1) AND (STITOTS.PURAMNT <> 0) ";

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

            purchasesTotalList = new ArrayList<>();
            totalCount = 0;

            while (resultSet.next()) {

                purchasesTotalList.add(
                        new PurchasesTotal(
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
        return new PurchasesTotalMain(totalCount, totalCount, purchasesTotalList);
    }



    /* ------------------------------------------ Распределение закупок по месяцам ---------------------------------------------------- */

    public PurchasesMonthMain getPurchasesMonthMain() {
        Connection connection = null;

        try {
            connection = mainRepository.getConnection();

            String sqlQuery = "SELECT ITEMS.CODE, ITEMS.NAME, ITEMS.STGRPCODE, " +
                    "ISNULL((SELECT ITMSM.PURCHASES_AMOUNT FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM WITH(NOLOCK, INDEX = I" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS_I2) " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ = 1) AND (ITMSM.YEAR_ = " + YEAR_ + "))), 0) AS JAN_COUNT, " +
                    "ISNULL((SELECT ITMSM.PURCHASES_AMOUNT FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM WITH(NOLOCK, INDEX = I" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS_I2) " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ = 2) AND (ITMSM.YEAR_ = " + YEAR_ + "))), 0) AS FEB_COUNT, " +
                    "ISNULL((SELECT ITMSM.PURCHASES_AMOUNT FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM WITH(NOLOCK, INDEX = I" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS_I2) " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ = 3) AND (ITMSM.YEAR_ = " + YEAR_ + "))), 0) AS MAR_COUNT, " +
                    "ISNULL((SELECT ITMSM.PURCHASES_AMOUNT FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM WITH(NOLOCK, INDEX = I" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS_I2) " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ = 4) AND (ITMSM.YEAR_ = " + YEAR_ + "))), 0) AS APR_COUNT, " +
                    "ISNULL((SELECT ITMSM.PURCHASES_AMOUNT FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM WITH(NOLOCK, INDEX = I" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS_I2) " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ = 5) AND (ITMSM.YEAR_ = " + YEAR_ + "))), 0) AS MAY_COUNT, " +
                    "ISNULL((SELECT ITMSM.PURCHASES_AMOUNT FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM WITH(NOLOCK, INDEX = I" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS_I2) " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ = 6) AND (ITMSM.YEAR_ = " + YEAR_ + "))), 0) AS JUN_COUNT, " +
                    "ISNULL((SELECT ITMSM.PURCHASES_AMOUNT FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM WITH(NOLOCK, INDEX = I" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS_I2) " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ = 7) AND (ITMSM.YEAR_ = " + YEAR_ + "))), 0) AS JUL_COUNT, " +
                    "ISNULL((SELECT ITMSM.PURCHASES_AMOUNT FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM WITH(NOLOCK, INDEX = I" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS_I2) " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ = 8) AND (ITMSM.YEAR_ = " + YEAR_ + "))), 0) AS AUG_COUNT, " +
                    "ISNULL((SELECT ITMSM.PURCHASES_AMOUNT FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM WITH(NOLOCK, INDEX = I" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS_I2) " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ = 9) AND (ITMSM.YEAR_ = " + YEAR_ + "))), 0) AS SEP_COUNT, " +
                    "ISNULL((SELECT ITMSM.PURCHASES_AMOUNT FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM WITH(NOLOCK, INDEX = I" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS_I2) " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ = 10) AND (ITMSM.YEAR_ = " + YEAR_ + "))), 0) AS OCT_COUNT, " +
                    "ISNULL((SELECT ITMSM.PURCHASES_AMOUNT FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM WITH(NOLOCK, INDEX = I" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS_I2) " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ = 11) AND (ITMSM.YEAR_ = " + YEAR_ + "))), 0) AS NOV_COUNT, " +
                    "ISNULL((SELECT ITMSM.PURCHASES_AMOUNT FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM WITH(NOLOCK, INDEX = I" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS_I2) " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ = 12) AND (ITMSM.YEAR_ = " + YEAR_ + "))), 0) AS DEC_COUNT, " +
                    "ISNULL((SELECT SUM(ITMSM.PURCHASES_AMOUNT) FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM WITH(NOLOCK, INDEX = I" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS_I2) " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ BETWEEN 1 AND 12) AND (ITMSM.YEAR_ = " + YEAR_ + "))), 0) AS TOTAL_COUNT, " +
                    "ROUND(ISNULL((SELECT SUM(ITMSM.PURCHASES_CASHAMNT) FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM WITH(NOLOCK, INDEX = I" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS_I2) " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ BETWEEN 1 AND 12) AND (ITMSM.YEAR_ = " + YEAR_ + "))), 0), 2) AS TOTAL_SUM, " +
                    "ROUND(ISNULL((SELECT SUM(ITMSM.PURCHASES_CURRAMNT) FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM WITH(NOLOCK, INDEX = I" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS_I2) " +
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

            purchasesMonthList = new ArrayList<>();
            totalCount = 0;

            while (resultSet.next()) {

                purchasesMonthList.add(
                        new PurchasesMonth(
                                resultSet.getString("code"),
                                resultSet.getString("name"),
                                resultSet.getString("STGRPCODE"),
                                resultSet.getDouble("JAN_COUNT"),
                                resultSet.getDouble("FEB_COUNT"),
                                resultSet.getDouble("MAR_COUNT"),
                                resultSet.getDouble("APR_COUNT"),
                                resultSet.getDouble("MAY_COUNT"),
                                resultSet.getDouble("JUN_COUNT"),
                                resultSet.getDouble("JUL_COUNT"),
                                resultSet.getDouble("AUG_COUNT"),
                                resultSet.getDouble("SEP_COUNT"),
                                resultSet.getDouble("OCT_COUNT"),
                                resultSet.getDouble("NOV_COUNT"),
                                resultSet.getDouble("DEC_COUNT"),
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
        return new PurchasesMonthMain(totalCount, totalCount, purchasesMonthList);
    }



    /* ------------------------------------------ Распределение закупок по контрагентам ---------------------------------------------------- */

    public PurchasesClientMain getPurchasesClientsMain() {
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
                    "AND (STRNS.TRCODE IN (1,5,6,10,26,30,31,32,33,34) ) AND (STFIC.CANCELLED = 0) ";

            if (ITEM1_CODE != null && ITEM2_CODE != null && !ITEM1_CODE.equals("-") && !ITEM2_CODE.equals("-")) {
                sqlQuery += "AND (ITMSC.CODE BETWEEN '" + ITEM1_CODE + "' AND '" + ITEM2_CODE + "') ";
            } else if (ITEM1_CODE != null && !ITEM1_CODE.equals("-")) {
                sqlQuery += "AND (ITMSC.CODE = " + "'" + ITEM1_CODE + "') ";
            }

            if (GROUP1 != null && !GROUP1.equals("-")) {
                sqlQuery += "AND (ITMSC.STGRPCODE = " + "'" + GROUP1 + "') ";
            }

            if (PurchasesController.ACCOUNT1 != null && !PurchasesController.ACCOUNT1.isEmpty() && !PurchasesController.ACCOUNT1.equals("-")
                    && PurchasesController.ACCOUNT2 != null && !PurchasesController.ACCOUNT2.isEmpty() && !PurchasesController.ACCOUNT2.equals("-")) {
                sqlQuery += "AND (CLNTC.CODE BETWEEN '" + PurchasesController.ACCOUNT1 + "' AND '" + PurchasesController.ACCOUNT2 + "') ";
            } else if (PurchasesController.ACCOUNT1 != null && !PurchasesController.ACCOUNT1.isEmpty() && !PurchasesController.ACCOUNT1.equals("-")) {
                sqlQuery += " AND (CLNTC.CODE = '" + PurchasesController.ACCOUNT1 + "') ";
            }

            if ((BEGDATE != null && ENDDATE != null) && (!BEGDATE.isEmpty() && !ENDDATE.isEmpty()) && (isLegalDate(BEGDATE) && isLegalDate(ENDDATE))) {
                sqlQuery += "AND (STRNS.DATE_ >= " + "'" + BEGDATE + "' AND STRNS.DATE_ <= " + "'" + ENDDATE + "') ";
            }

            sqlQuery += "GROUP BY CLNTC.CODE, CLNTC.DEFINITION_, ITMSC.CODE, ITMSC.NAME, ITMSC.STGRPCODE, STRNS.TRCODE ";
            sqlQuery += "ORDER BY CLNTC.CODE, ITMSC.CODE ";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            purchasesClientList = new ArrayList<>();
            totalCount = 0;

            while (resultSet.next()) {

                PurchasesClient client = new PurchasesClient();

                client.setClientCode(resultSet.getString("client_code"));
                client.setClientName(resultSet.getString("client_name"));
                client.setItemCode(resultSet.getString("item_code"));
                client.setItemName(resultSet.getString("item_name"));
                client.setItemGroup(resultSet.getString("item_group"));

                if (resultSet.getDouble("trcode") == 1) {
                    client.setItemAmount(resultSet.getDouble("amount"));
                    client.setItemTotal(resultSet.getDouble("total"));
                    client.setItemTotalUsd(resultSet.getDouble("total_usd"));
                    client.setItemAmountRet(0.0);
                    client.setItemTotalRet(0.0);
                    client.setItemTotalUsdRet(0.0);
                } else if (resultSet.getDouble("trcode") == 6) {
                    client.setItemAmount(0.0);
                    client.setItemTotal(0.0);
                    client.setItemTotalUsd(0.0);
                    client.setItemAmountRet(-resultSet.getDouble("amount"));
                    client.setItemTotalRet(-resultSet.getDouble("total"));
                    client.setItemTotalUsdRet(-resultSet.getDouble("total_usd"));
                }

                purchasesClientList.add(client);
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
        return new PurchasesClientMain(totalCount, totalCount, purchasesClientList);
    }


    public static boolean isLegalDate(String date) {
        return date.length() <= 10;
    }

}

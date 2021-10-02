package kg.bakirov.alfa.repositories;

import kg.bakirov.alfa.api.materials.ItemsFicheMain;
import kg.bakirov.alfa.api.materials.ItemsInventoryMain;
import kg.bakirov.alfa.api.materials.ItemsMain;
import kg.bakirov.alfa.api.materials.ItemsPriceMain;
import kg.bakirov.alfa.models.items.Items;
import kg.bakirov.alfa.models.items.ItemsFiche;
import kg.bakirov.alfa.models.items.ItemsInventory;
import kg.bakirov.alfa.models.items.ItemsPrice;
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
import static kg.bakirov.alfa.controllers.ItemsController.*;

@Repository
public class ItemsRepository {

    private final MainRepository mainRepository;

    @Autowired
    public ItemsRepository(MainRepository mainRepository) {
        this.mainRepository = mainRepository;
    }

    List<Items> itemsList = null;
    int totalCount;

    List<ItemsInventory> itemsInventoryList = null;
    int totalCountInventory;

    List<ItemsFiche> itemsFicheList = null;
    int totalCountFiche;

    List<ItemsPrice> itemsPriceList = null;
    int totalCountPrice;


    /* ------------------------------------------ Остаток товаров ---------------------------------------------------- */

    public ItemsMain getItemsMain() {
        Connection connection = null;

        try {
            connection = mainRepository.getConnection();

            String sqlQuery = "SELECT ITEMS.CODE as code, ITEMS.NAME as name, ITEMS.STGRPCODE AS groupcode, " +
                    "GNSTITOT.TRANSFERRED + GNSTITOT.PURAMNT + GNSTITOT.ACTPRODIN AS puramount, " +
                    "CONVERT(DECIMAL(10,2), GNSTITOT.AVGCURRVAL + GNSTITOT.PURCURR) AS purcurr, " +
                    "GNSTITOT.SALAMNT AS salamount, CONVERT(DECIMAL(10,2), GNSTITOT.SALCURR) AS salcurr, " +
                    "GNSTITOT.ONHAND as onhand, CONVERT(varchar, GNSTITOT.LASTTRDATE, 23) AS lasttrdate " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_ITEMS ITEMS LEFT JOIN LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_GNTOTST GNSTITOT " +
                    "WITH(NOLOCK, INDEX = I" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_GNTOTST_I1) " +
                    "ON (ITEMS.LOGICALREF = GNSTITOT.STOCKREF) WHERE (GNSTITOT.INVENNO = -1) ";

            if (ITEM1_CODE != null && ITEM2_CODE != null && !ITEM1_CODE.equals("-") && !ITEM2_CODE.equals("-")) {
                sqlQuery += " AND (ITEMS.CODE BETWEEN '" + ITEM1_CODE + "' AND '" + ITEM2_CODE + "')";
            } else if (ITEM1_CODE != null && !ITEM1_CODE.equals("-")) {
                sqlQuery += " AND (ITEMS.CODE = " + "'" + ITEM1_CODE + "') ";
            }

            if (BEGDATE != null && !BEGDATE.isEmpty() && isLegalDate(BEGDATE)) {
                sqlQuery += " AND (LASTTRDATE <= " + "'" + BEGDATE + "') ";
            }

            if (GROUP1 != null && !GROUP1.equals("-")) {
                sqlQuery += " AND (ITEMS.STGRPCODE = " + "'" + GROUP1 + "')";
            }

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            itemsList = new ArrayList<>();
            totalCount = 0;

            while (resultSet.next()) {

                itemsList.add(
                        new Items(
                                resultSet.getString("code"),
                                resultSet.getString("name"),
                                resultSet.getString("groupcode"),
                                resultSet.getDouble("puramount"),
                                resultSet.getDouble("purcurr"),
                                resultSet.getDouble("salamount"),
                                resultSet.getDouble("salcurr"),
                                resultSet.getDouble("onhand"),
                                resultSet.getString("lasttrdate")
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
        return new ItemsMain(totalCount, totalCount, itemsList);
    }



    /* ---------------------------------------- Инвентарный отчет ------------------------------------------------ */

    public ItemsInventoryMain getItemsInventoryMain() {
        Connection connection = null;

        try {
            connection = mainRepository.getConnection();

            String sqlQuery = "Select ITEMS.CODE code, ITEMS.NAME name, ITEMS.STGRPCODE groupcode, " +
                    "ROUND(ISNULL((SELECT SUM(((2.5-STRNS.IOCODE)/ABS(2.5-STRNS.IOCODE)) * STRNS.AMOUNT*(STRNS.UINFO2/(CASE STRNS.UINFO1 WHEN 0 THEN 1 ELSE STRNS.UINFO1 END))) " +
                    "FROM  LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE STRNS " +
                    "WHERE (STRNS.STOCKREF = ITEMS.LOGICALREF) " +
                    "AND (STRNS.CANCELLED = 0) AND (STRNS.STFICHEREF <> 0) AND (STRNS.LPRODSTAT <> 2) AND (STRNS.LINETYPE IN (0, 1, 5, 6, 8, 9)) AND " +
                    "((STRNS.TRCODE IN (1, 2, 3, 5, 10, 13, 14, 15, 16, 17, 18, 19, 26, 30, 31, 32, 33, 34, 50)) OR (((STRNS.TRCODE = 25) AND (STRNS.IOCODE = 2)) AND " +
                    "STRNS.TRCODE IN (1, 2, 3, 5, 10, 13, 14, 15, 16, 17, 18, 19, 25, 26, 30, 31, 32, 33, 34, 50)  ))), 0) + " +
                    "ISNULL((SELECT SUM(((2.5-STRNS.IOCODE)/ABS(2.5-STRNS.IOCODE)) * STRNS.AMOUNT*(STRNS.UINFO2/STRNS.UINFO1)) " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE STRNS WHERE  " +
                    "(STRNS.STOCKREF = ITEMS.LOGICALREF) AND (STRNS.CANCELLED = 0) AND (STRNS.STFICHEREF <> 0) AND (STRNS.LPRODSTAT <> 2) AND " +
                    "(STRNS.LINETYPE IN (0, 1, 5, 6, 8, 9)) AND ((STRNS.IOCODE IN (3,4))) AND ((STRNS.TRCODE IN (2, 3, 4, 6, 7, 8, 9, 11, 12, 20, 21, 22, 23, 24, 35, " +
                    "36, 37, 38, 39, 51)) OR (((STRNS.TRCODE = 25) AND (STRNS.IOCODE = 3)) AND STRNS.TRCODE IN (2, 3, 4, 6, 7, 8, 9, 11, 12, 20, " +
                    " 21, 22, 23, 24, 25, 35, 36, 37, 38, 39, 51)  ))), 0), 0) as onhand,  " +
                    "ROUND(ISNULL((SELECT SUM(CASE STRNS.OUTCOSTCURR WHEN 0 THEN (STRNS.TOTAL-STRNS.DISTCOST+STRNS.DIFFPRICE)/STRNS.REPORTRATE ELSE STRNS.OUTCOSTCURR*STRNS.UINFO2/STRNS.UINFO1*STRNS.AMOUNT END) " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE STRNS  WHERE " +
                    "(STRNS.STOCKREF = ITEMS.LOGICALREF) AND (STRNS.REPORTRATE > 0.0001) " +
                    "AND (STRNS.CANCELLED = 0) AND (STRNS.STFICHEREF <> 0) " +
                    "AND (STRNS.LPRODSTAT <> 2) AND (STRNS.LINETYPE IN (0, 1, 5, 6, 8, 9)) AND (STRNS.IOCODE < 3) " +
                    "AND (STRNS.TRCODE IN (1, 2, 3, 5, 13, 14, 15, 16, 17, 18, 19, 25, 26, 30, 31, 32, 33, 34, 50))), 0)+ " +
                    "ISNULL((SELECT SUM(((2.5-STRNS.IOCODE)/ABS(2.5-STRNS.IOCODE)) * STRNS.OUTCOSTCURR*STRNS.UINFO2/STRNS.UINFO1*STRNS.AMOUNT) " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE STRNS  WHERE " +
                    "(STRNS.STOCKREF = ITEMS.LOGICALREF) AND (STRNS.CANCELLED = 0) " +
                    "AND (STRNS.STFICHEREF <> 0) AND (STRNS.LPRODSTAT <> 2) AND (STRNS.LINETYPE IN (0, 1, 5, 6, 8, 9)) " +
                    "AND((STRNS.TRCODE IN (4,6,7,8,9,11,12,20,21, 22, 23, 24, 35, 36, 37, 38, 39, 51)) OR (((STRNS.TRCODE = 25) AND (STRNS.IOCODE = 3)) " +
                    "AND STRNS.TRCODE IN (4, 6, 7, 8, 9, 11, 12, 20, 21, 22, 23, 24, 25, 35, 36, 37, 38, 39, 51)  ))), 0), 0) AS total " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_ITEMS AS ITEMS WHERE (ITEMS.CARDTYPE <> 22) ";

            if (ITEM1_CODE != null && ITEM2_CODE != null && !ITEM1_CODE.equals("-") && !ITEM2_CODE.equals("-")) {
                sqlQuery += " AND (ITEMS.CODE BETWEEN '" + ITEM1_CODE + "' AND '" + ITEM2_CODE + "')";
            } else if (ITEM1_CODE != null && !ITEM1_CODE.equals("-")) {
                sqlQuery += " AND ITEMS.CODE = " + "'" + ITEM1_CODE + "'";
            }

            if (GROUP1 != null && !GROUP1.equals("-")) {
                sqlQuery += " AND ITEMS.STGRPCODE = " + "'" + GROUP1 + "'";
            }

            sqlQuery += " ORDER BY ITEMS.CODE";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            itemsInventoryList = new ArrayList<>();
            totalCountInventory = 0;

            while (resultSet.next()) {

                ItemsInventory inventory = new ItemsInventory();

                inventory.setItem_code(resultSet.getString("code"));
                inventory.setItem_name(resultSet.getString("name"));
                inventory.setItem_group(resultSet.getString("groupcode"));
                inventory.setItem_onHand(resultSet.getDouble("onhand"));

                if (resultSet.getDouble("onhand") > 0) {
                    inventory.setItem_avgVal(resultSet.getDouble("total") / resultSet.getDouble("onhand"));
                    inventory.setItem_total(resultSet.getDouble("total"));
                } else {
                    inventory.setItem_avgVal(0);
                    inventory.setItem_total(0);
                }

                itemsInventoryList.add(inventory);
                totalCountInventory++;
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
        return new ItemsInventoryMain(totalCountInventory, totalCountInventory, itemsInventoryList);
    }


    /* ---------------------------------------- Список документов ------------------------------------------------ */

    public ItemsFicheMain getItemsFicheMain() {
        Connection connection = null;

        try {
            connection = mainRepository.getConnection();

            String sqlQuery = "SELECT " +
                    "CASE STFIC.TRCODE " +
                    "WHEN 1 THEN 'Закупочная накладная' " +
                    "WHEN 2 THEN 'Возвратная накладная поставщику' " +
                    "WHEN 3 THEN 'Возвратная накладная поставщику' " +
                    "WHEN 6 THEN 'Возвратная накладная' " +
                    "WHEN 7 THEN 'Накладная продажи' " +
                    "WHEN 8 THEN 'Накладная продажи' " +
                    "WHEN 11 THEN 'Списание' " +
                    "WHEN 12 THEN 'Списание' " +
                    "WHEN 13 THEN 'Ввод остатков' " +
                    "WHEN 14 THEN 'Начальный остаток' " +
                    "WHEN 25 THEN 'Перемещение' " +
                    "WHEN 50 THEN 'Излишки' " +
                    "WHEN 51 THEN 'Недостачи' " +
                    "ELSE '' " +
                    "END as item_trcode, " +
                    "STFIC.FICHENO AS item_ficheno, CONVERT(varchar, STFIC.DATE_, 23) AS item_date, CLNTC.CODE as item_clientcode, " +
                    "CLNTC.DEFINITION_ as item_clientname, ROUND(CASE STFIC.IOCODE WHEN 1 THEN STFIC.GROSSTOTAL ELSE -STFIC.GROSSTOTAL END, 0) AS item_gross, " +
                    "ROUND(STFIC.TOTALDISCOUNTS, 0) AS item_discounts, ROUND(STFIC.TOTALEXPENSES, 0) AS item_expenses, " +
                    "ROUND(CASE STFIC.IOCODE WHEN 1 THEN STFIC.NETTOTAL ELSE -STFIC.NETTOTAL END, 0) as item_net, " +
                    "CASE STFIC.IOCODE " +
                    "WHEN 1 THEN '(+)' " +
                    "WHEN 3 THEN '(-)' " +
                    "END AS item_type " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STFICHE STFIC WITH(NOLOCK) LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_INVOICE INVFC WITH(NOLOCK) ON (STFIC.INVOICEREF = INVFC.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC WITH(NOLOCK) ON (STFIC.CLIENTREF = CLNTC.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_SLSMAN SLSMC WITH(NOLOCK) ON (STFIC.SALESMANREF = SLSMC.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_PAYPLANS PAYPL WITH(NOLOCK) ON (STFIC.PAYDEFREF = PAYPL.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_WORKSTAT sWSp WITH(NOLOCK) ON (STFIC.SOURCEWSREF = sWSp.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_WORKSTAT dWSp WITH(NOLOCK) ON (STFIC.DESTWSREF = dWSp.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_DISTORD DISTORD WITH(NOLOCK) ON (STFIC.DISTORDERREF = DISTORD.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_PROJECT PROJECT WITH(NOLOCK) ON (STFIC.PROJECTREF  =  PROJECT.LOGICALREF) " +
                    "WHERE (STFIC.CANCELLED = 0) AND ((STFIC.STATUS IN (0,1)) OR (STFIC.TRCODE IN (11,12,13,14,25,26,50,51))) ";

            if ((BEGDATE != null && ENDDATE != null) && (!BEGDATE.isEmpty() && !ENDDATE.isEmpty()) && (isLegalDate(BEGDATE) && isLegalDate(ENDDATE))) {
                sqlQuery += "AND (STFIC.DATE_ >= " + "'" + BEGDATE + "' AND STFIC.DATE_ <= " + "'" + ENDDATE + "') ";
            }

            if (OPERATIONTYPE != null && !OPERATIONTYPE.isEmpty()) {
                sqlQuery += "AND STFIC.TRCODE IN (" + OPERATIONTYPE + ") ";
            } else {
                sqlQuery += "AND STFIC.TRCODE IN (01, 02, 03, 06, 07, 08, 11, 12, 13, 14, 25, 50, 51) ";
            }

            sqlQuery += "ORDER BY STFIC.DATE_, STFIC.FTIME, STFIC.TRCODE, STFIC.FICHENO";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            itemsFicheList = new ArrayList<>();
            totalCountFiche = 0;

            while (resultSet.next()) {

                itemsFicheList.add(
                        new ItemsFiche(
                                resultSet.getString("item_trcode"),
                                resultSet.getLong("item_ficheno"),
                                resultSet.getString("item_date"),
                                resultSet.getString("item_clientcode"),
                                resultSet.getString("item_clientname"),
                                resultSet.getDouble("item_gross"),
                                resultSet.getDouble("item_discounts"),
                                resultSet.getDouble("item_expenses"),
                                resultSet.getDouble("item_net"),
                                resultSet.getString("item_type")
                        )
                );
                totalCountFiche++;
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
        return new ItemsFicheMain(totalCountFiche, totalCountFiche, itemsFicheList);
    }



    /* ---------------------------------------- Прайс лист ------------------------------------------------ */

    public ItemsPriceMain getItemsPriceMain() {
        Connection connection = null;

        try {
            connection = mainRepository.getConnection();

            String sqlQuery = "SELECT ROW_NUMBER() OVER(ORDER BY code ASC) AS row, ITEMS.CODE AS code, " +
                    "ITEMS.NAME AS name, ITEMS.STGRPCODE AS groupcode, GNSTITOT.ONHAND AS onhand, " +
                    "ISNULL((SELECT USLINE.CODE FROM LG_" + GLOBAL_FIRM_NO + "_UNITSETL USLINE WITH(NOLOCK, INDEX = I" + GLOBAL_FIRM_NO + "_UNITSETL_I4) " +
                    "WHERE (USLINE.UNITSETREF = ITEMS.UNITSETREF) AND (USLINE.MAINUNIT = 1)), 0) AS unit, " +
                    "ISNULL((SELECT TOP 1 PRICE FROM LG_" + GLOBAL_FIRM_NO + "_PRCLIST prclist WHERE ((ITEMS.LOGICALREF = prclist.CARDREF) " +
                    "AND (PTYPE = 2) AND (BEGDATE <= GETDATE()) AND (ENDDATE >= GETDATE()))), 0) AS price " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_ITEMS ITEMS LEFT JOIN LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD +
                    "_GNTOTST GNSTITOT WITH(NOLOCK, INDEX = I" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_GNTOTST_I1) " +
                    "ON (GNSTITOT.STOCKREF = ITEMS.LOGICALREF) WHERE ((GNSTITOT.INVENNO = -1) AND (GNSTITOT.ONHAND > 0)) ";

            if (ITEM1_CODE != null && ITEM2_CODE != null && !ITEM1_CODE.equals("-") && !ITEM2_CODE.equals("-")) {
                sqlQuery += " AND (ITEMS.CODE BETWEEN '" + ITEM1_CODE + "' AND '" + ITEM2_CODE + "')";
            } else if (ITEM1_CODE != null && !ITEM1_CODE.equals("-")) {
                sqlQuery += " AND (ITEMS.CODE = " + "'" + ITEM1_CODE + "') ";
            }

            if (GROUP1 != null && !GROUP1.equals("-")) {
                sqlQuery += " AND (ITEMS.STGRPCODE = " + "'" + GROUP1 + "')";
            }

            sqlQuery += " ORDER BY ITEMS.CODE";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            itemsPriceList = new ArrayList<>();
            totalCountPrice = 0;

            while (resultSet.next()) {

                itemsPriceList.add(
                        new ItemsPrice(
                                resultSet.getLong("row"),
                                resultSet.getString("code"),
                                resultSet.getString("name"),
                                resultSet.getString("groupcode"),
                                resultSet.getDouble("onhand"),
                                resultSet.getString("unit"),
                                resultSet.getDouble("price")
                        )
                );
                totalCountPrice++;
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
        return new ItemsPriceMain(totalCountPrice, totalCountPrice, itemsPriceList);
    }


    /* ---------------------------------------- Список товаров ------------------------------------------------ */

    public List<Items> getItemsList() {

        List<Items> itemsList = new ArrayList<>();
        Connection connection = null;

        try {

            connection = mainRepository.getConnection();

            String sqlQuery = "SELECT ITEMS.CODE AS code, ITEMS.NAME AS name, ITEMS.STGRPCODE AS groupCode " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_ITEMS ITEMS WHERE (ITEMS.CARDTYPE <> 22 AND ITEMS.ACTIVE = 0)";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            while (resultSet.next()) {
                itemsList.add(new Items(resultSet.getString("code"), resultSet.getString("name"), resultSet.getString("groupCode")));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return itemsList;
    }

    public List<Items> getItemsListByFirm(int firmNo) {

        List<Items> itemsList = new ArrayList<>();
        String firm = String.format("%03d", firmNo);
        Connection connection = null;

        try {

            connection = mainRepository.getConnection();

            String sqlQuery = "SELECT ITEMS.CODE AS code, ITEMS.NAME AS name, ITEMS.STGRPCODE AS groupCode " +
                    "FROM LG_" + firm + "_ITEMS ITEMS WHERE (ITEMS.CARDTYPE <> 22 AND ITEMS.ACTIVE = 0)";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            while (resultSet.next()) {
                itemsList.add(new Items(resultSet.getString("code"), resultSet.getString("name"), resultSet.getString("groupCode")));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return itemsList;
    }

    public static boolean isLegalDate(String date) {
        return date.length() <= 10;
    }


}

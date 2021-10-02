package kg.bakirov.alfa.repositories;

import kg.bakirov.alfa.api.finances.FinanceCustomerMain;
import kg.bakirov.alfa.api.finances.FinanceDebitMain;
import kg.bakirov.alfa.api.finances.FinanceExtractMain;
import kg.bakirov.alfa.controllers.FinanceController;
import kg.bakirov.alfa.models.finances.Customer;
import kg.bakirov.alfa.models.finances.CustomerDebit;
import kg.bakirov.alfa.models.finances.CustomerExtract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static kg.bakirov.alfa.controllers.FinanceController.*;
import static kg.bakirov.alfa.controllers.HomeController.GLOBAL_FIRM_NO;
import static kg.bakirov.alfa.controllers.HomeController.GLOBAL_PERIOD;

@Repository
public class FinanceRepository {

    private final MainRepository mainRepository;

    List<Customer> customerList = null;
    List<CustomerExtract> customerExtractList = null;
    List<CustomerDebit> customerDebitList = null;

    int totalCount;

    @Autowired
    public FinanceRepository(MainRepository mainRepository) {
        this.mainRepository = mainRepository;
    }




    /* ------------------------------------------ Отчет по задолжностям ---------------------------------------------------- */

    public FinanceDebitMain getFinanceDebitMain() {
        Connection connection = null;

        try {
            connection = mainRepository.getConnection();

            String sqlQuery = "SELECT " +
                    "CTRNS.SIGN, CTRNS.REPORTNET, CTRNS.AMOUNT, " +
                    "CLNTC.CODE, CLNTC.DEFINITION_, CLNTC.TELNRS1, " +
                    "CLNTC.ADDR1, CLNUM.RISKTOTAL, " +
                    "CLNUM.REPRISKTOTAL " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_CLFLINE CTRNS WITH(NOLOCK) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC WITH(NOLOCK) ON (CTRNS.CLIENTREF  =  CLNTC.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_CLRNUMS CLNUM WITH(NOLOCK) ON (CLNTC.LOGICALREF  =  CLNUM.CLCARDREF) " +
                    "WHERE " +
                    "(CLNTC.LOGICALREF <> 0) AND (CTRNS.CANCELLED = 0) AND (CTRNS.STATUS = 0) AND (((CTRNS.MODULENR=5) " +
                    "AND (CTRNS.TRCODE<>12)) OR (CTRNS.MODULENR<>5)) AND (CLNTC.CARDTYPE <> 22) AND (CLNUM.RISKTOTAL > 0) ";


            if (ACCOUNT1 != null && !ACCOUNT1.isEmpty() && !ACCOUNT1.equals("-")
                    && ACCOUNT2 != null && !ACCOUNT2.isEmpty() && !ACCOUNT2.equals("-")) {
                sqlQuery += "AND (CLNTC.CODE BETWEEN '" + ACCOUNT1 + "' AND '" + ACCOUNT2 + "') ";
            } else if (ACCOUNT1 != null && !ACCOUNT1.isEmpty() && !ACCOUNT1.equals("-")) {
                sqlQuery += " AND (CLNTC.CODE = '" + ACCOUNT1 + "') ";
            }

            if ((BEGDATE != null && ENDDATE != null) && (!BEGDATE.isEmpty() && !ENDDATE.isEmpty()) && (isLegalDate(BEGDATE) && isLegalDate(ENDDATE))) {
                sqlQuery += "AND (CTRNS.DATE_ >= " + "'" + BEGDATE + "' AND CTRNS.DATE_ <= " + "'" + ENDDATE + "') ";
            }

            sqlQuery += "ORDER BY CLNTC.CODE ";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            customerDebitList = new ArrayList<>();
            totalCount = 0;

            double debit = 0;
            double credit = 0;
            double debitUsd = 0;
            double creditUsd = 0;
            String currentCode = null;
            Map<String, CustomerDebit> map = new HashMap<>();

            while (resultSet.next()) {

                int sign = resultSet.getInt("sign");

                CustomerDebit customerDebit = new CustomerDebit();

                customerDebit.setCode(resultSet.getString("code"));
                customerDebit.setName(resultSet.getString("DEFINITION_"));
                customerDebit.setAddress(resultSet.getString("ADDR1"));
                customerDebit.setPhone(resultSet.getString("TELNRS1"));
                customerDebit.setBalance(resultSet.getDouble("RISKTOTAL"));
                customerDebit.setBalanceUsd(resultSet.getDouble("REPRISKTOTAL"));

                if (currentCode == null || !currentCode.equals(resultSet.getString("code"))) {

                    if (sign == 0) {
                        debit = resultSet.getDouble("AMOUNT");
                        debitUsd = resultSet.getDouble("REPORTNET");
                        credit = 0;
                        creditUsd = 0;
                    } else {
                        credit = resultSet.getDouble("AMOUNT");
                        creditUsd = resultSet.getDouble("REPORTNET");
                        debit = 0;
                        debitUsd = 0;
                    }

                } else {

                    if (sign == 0) {
                        debit = debit + resultSet.getDouble("AMOUNT");
                        debitUsd = debitUsd + resultSet.getDouble("REPORTNET");
                    } else {
                        credit = credit + resultSet.getDouble("AMOUNT");
                        creditUsd = creditUsd + resultSet.getDouble("REPORTNET");
                    }

                }


                customerDebit.setCredit(credit);
                customerDebit.setCreditUsd(creditUsd);
                customerDebit.setDebit(debit);
                customerDebit.setDebitUsd(debitUsd);

                map.put(resultSet.getString("code"), customerDebit);

                currentCode = resultSet.getString("code");
                totalCount++;
            }

            for (Map.Entry<String, CustomerDebit> entry : map.entrySet()) {
                customerDebitList.add(entry.getValue());
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
        return new FinanceDebitMain(totalCount, totalCount, customerDebitList);
    }



    /* ------------------------------------------ Выписка контрагентов ---------------------------------------------------- */

    public FinanceExtractMain getFinanceExtractMain() {
        Connection connection = null;

        try {
            connection = mainRepository.getConnection();

            String sqlQuery = "SELECT CLNTC.CODE AS code, CLNTC.DEFINITION_ AS name, " +
                    "CONVERT(varchar, CTRNS.DATE_, 23) AS date, CTRNS.TRCODE, CTRNS.SIGN, " +
                    "ROUND(CTRNS.REPORTNET, 2) AS reportnet, CTRNS.TRANNO, CTRNS.LINEEXP, INVFC.FROMKASA, " +
                    "INVFC.FICHENO, CASE WHEN CTRNS.TRCODE=14 AND CTRNS.MODULENR=5 THEN 0 ELSE 1 END AS TRTEMP, " +
                    "CASE CTRNS.TRCODE " +
                    "WHEN 1 THEN 'Приходный кассовый ордер' " +
                    "WHEN 2 THEN 'Расходный кассовый ордер' " +
                    "WHEN 3 THEN 'Дебет-нот' " +
                    "WHEN 4 THEN 'Кредит-нот' " +
                    "WHEN 5 THEN 'Квитанция перевода' " +
                    "WHEN 6 THEN 'Учет курсовой разницы' " +
                    "WHEN 12 THEN 'Специальная квитанция' " +
                    "WHEN 14 THEN 'Начальный остаток' " +
                    "WHEN 31 THEN 'Приходная накладная' " +
                    "WHEN 32 THEN 'Возвратная накладная' " +
                    "WHEN 33 THEN 'Квитанция за полученные услуги' " +
                    "WHEN 36 THEN 'Возврат поставщику' " +
                    "WHEN 37 THEN 'Розничная продажа' " +
                    "WHEN 38 THEN 'Оптовая продажа' " +
                    "WHEN 39 THEN 'Квитанция за оказанные услуги' " +
                    "ELSE '' " +
                    "END as trcode_def " +
                    " FROM " +
                    "LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_CLFLINE CTRNS WITH(NOLOCK) LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_PAYPLANS PAYPL WITH(NOLOCK) " +
                    "ON (CTRNS.PAYDEFREF = PAYPL.LOGICALREF) LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_CLFICHE CLFIC WITH(NOLOCK) " +
                    "ON (CTRNS.SOURCEFREF = CLFIC.LOGICALREF) LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_INVOICE INVFC WITH(NOLOCK) " +
                    "ON (CTRNS.SOURCEFREF = INVFC.LOGICALREF) LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_CSROLL RLFIC WITH(NOLOCK) " +
                    "ON (CTRNS.SOURCEFREF = RLFIC.LOGICALREF) LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_EMFICHE GLFIC WITH(NOLOCK) " +
                    "ON (CTRNS.ACCFICHEREF = GLFIC.LOGICALREF) LEFT OUTER JOIN LG_SLSMAN SLSMC WITH(NOLOCK) " +
                    "ON (INVFC.SALESMANREF = SLSMC.LOGICALREF) LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC WITH(NOLOCK) " +
                    "ON (CTRNS.CLIENTREF = CLNTC.LOGICALREF) " +
                    "WHERE (CTRNS.BRANCH IN (0)) AND (CTRNS.DEPARTMENT IN (0)) ";

            if (ACCOUNT1 != null && !ACCOUNT1.isEmpty() && !ACCOUNT1.equals("-")
                    && ACCOUNT2 != null && !ACCOUNT2.isEmpty() && !ACCOUNT2.equals("-")) {
                sqlQuery += "AND (CLNTC.CODE BETWEEN '" + ACCOUNT1 + "' AND '" + ACCOUNT2 + "') ";
            } else if (ACCOUNT1 != null && !ACCOUNT1.isEmpty() && !ACCOUNT1.equals("-")) {
                sqlQuery += " AND (CLNTC.CODE = '" + ACCOUNT1 + "') ";
            }

            if (OPERATIONTYPE != null && !OPERATIONTYPE.isEmpty()) {
                sqlQuery += "AND (CTRNS.TRCODE IN (" + OPERATIONTYPE + ") ) ";
            } else {
                sqlQuery += "AND (CTRNS.TRCODE IN (31, 32, 33, 34, 36, 37, 38, 39, 1, 2, 3, 4, 5, 6, 12, 14) ) ";
            }

            if ((BEGDATE != null && ENDDATE != null) && (!BEGDATE.isEmpty() && !ENDDATE.isEmpty()) && (isLegalDate(BEGDATE) && isLegalDate(ENDDATE))) {
                sqlQuery += "AND (CTRNS.DATE_ >= " + "'" + BEGDATE + "' AND CTRNS.DATE_ <= " + "'" + ENDDATE + "') ";
            }

            sqlQuery += "ORDER BY CLNTC.CODE, CTRNS.DATE_, TRTEMP ";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            customerExtractList = new ArrayList<>();
            totalCount = 0;
            double totalSum = 0;
            String currentCode = null;

            while (resultSet.next()) {

                int sign = resultSet.getInt("SIGN");

                CustomerExtract extract = new CustomerExtract();

                extract.setCode(resultSet.getString("code"));
                extract.setName(resultSet.getString("name"));
                extract.setTrcode(resultSet.getString("trcode_def"));
                extract.setDescription(resultSet.getString("LINEEXP"));
                extract.setDate(resultSet.getString("date"));

                if (sign == 0) {
                    extract.setDebit(resultSet.getDouble("reportnet"));
                } else {
                    extract.setCredit(resultSet.getDouble("reportnet"));
                }

                if (resultSet.getInt("TRTEMP") != 0) {
                    extract.setFicheno(resultSet.getString("FICHENO"));
                } else {
                    extract.setFicheno(resultSet.getString("TRANNO"));
                }

                if ((currentCode == null) || !currentCode.equals(resultSet.getString("code"))) {
                    extract.setBalanceBefore(0.0);
                    totalSum = resultSet.getDouble("reportnet");
                } else {
                    extract.setBalanceBefore(totalSum);
                    if (sign == 0) {
                        totalSum = totalSum + resultSet.getDouble("reportnet");
                    } else {
                        totalSum = totalSum - resultSet.getDouble("reportnet");
                    }
                }

                extract.setBalance(totalSum);
                customerExtractList.add(extract);


                if (resultSet.getInt("FROMKASA") == 1 && (resultSet.getString("TRCODE").equals("31")
                        || resultSet.getString("TRCODE").equals("32") || resultSet.getString("TRCODE").equals("33")
                        || resultSet.getString("TRCODE").equals("34") || resultSet.getString("TRCODE").equals("36")
                        || resultSet.getString("TRCODE").equals("37") || resultSet.getString("TRCODE").equals("38")
                        || resultSet.getString("TRCODE").equals("39") || resultSet.getString("TRCODE").equals("43")
                        || resultSet.getString("TRCODE").equals("44") || resultSet.getString("TRCODE").equals("56"))) {
                    CustomerExtract extractKasa = new CustomerExtract();

                    extractKasa.setCode(resultSet.getString("code"));
                    extractKasa.setName(resultSet.getString("name"));
                    extractKasa.setDescription(resultSet.getString("LINEEXP"));
                    extractKasa.setDate(resultSet.getString("date"));
                    extractKasa.setCredit(resultSet.getDouble("reportnet"));
                    extractKasa.setBalanceBefore(totalSum);

                    if (resultSet.getInt("TRTEMP") != 0) {
                        extractKasa.setFicheno(resultSet.getString("FICHENO"));
                    } else {
                        extractKasa.setFicheno(resultSet.getString("TRANNO"));
                    }


                    if ((resultSet.getString("TRCODE").equals("32")
                            || resultSet.getString("TRCODE").equals("33"))) {
                        extractKasa.setTrcode(resultSet.getString("trcode_def"));
                        totalSum = totalSum + resultSet.getDouble("reportnet");
                    } else {
                        totalSum = totalSum - resultSet.getDouble("reportnet");
                        extractKasa.setTrcode("Приходный кассовый ордер");
                    }


                    extractKasa.setBalance(totalSum);
                    customerExtractList.add(extractKasa);
                }


                currentCode = resultSet.getString("code");
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
        return new FinanceExtractMain(totalCount, totalCount, customerExtractList);
    }



    /* ------------------------------------------ Акт сверки взаиморасчетов ---------------------------------------------------- */

    public FinanceCustomerMain getCustomerMain() {
        Connection connection = null;

        try {
            connection = mainRepository.getConnection();

            String sqlQuery = "SELECT CLCARD.CODE AS kod, CLCARD.DEFINITION_ AS aciklama, CLCARD.ADDR1 AS adres, CLCARD.TELNRS1 AS telno, " +
                    "ROUND(ISNULL((SELECT SUM((1-CTRNS.SIGN)*CTRNS.REPORTNET) " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_CLFLINE CTRNS, LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC " +
                    "WHERE CTRNS.DEPARTMENT IN (0) and CTRNS.BRANCH IN (0) " +
                    "and (CLNTC.CODE LIKE CLCARD.CODE) ";

            if ((BEGDATE != null && ENDDATE != null) && (!BEGDATE.isEmpty() && !ENDDATE.isEmpty()) && (isLegalDate(BEGDATE) && isLegalDate(ENDDATE))) {
                sqlQuery += "AND (CTRNS.DATE_ >= " + "'" + BEGDATE + "' AND CTRNS.DATE_ <= " + "'" + ENDDATE + "') ";
            }

            sqlQuery += "AND (CTRNS.CLIENTREF = CLCARD.LOGICALREF) " +
                    "AND (CTRNS.CANCELLED = 0) AND (CTRNS.MODULENR <> 4) AND (NOT (CTRNS.TRCODE IN (12,35,40)))), 0)+ " +
                    "ISNULL((SELECT SUM(((1-CTRNS.SIGN)+(CTRNS.SIGN*INVFC.FROMKASA))*CTRNS.REPORTNET) " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_CLFLINE CTRNS, " +
                    "LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC, LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_INVOICE INVFC " +
                    "WHERE CTRNS.DEPARTMENT IN (0) and CTRNS.BRANCH IN (0) " +
                    "and (CLNTC.CODE LIKE CLCARD.CODE) ";

            if ((BEGDATE != null && ENDDATE != null) && (!BEGDATE.isEmpty() && !ENDDATE.isEmpty()) && (isLegalDate(BEGDATE) && isLegalDate(ENDDATE))) {
                sqlQuery += "AND (CTRNS.DATE_ >= " + "'" + BEGDATE + "' AND CTRNS.DATE_ <= " + "'" + ENDDATE + "') ";
            }

            sqlQuery += "AND (CTRNS.CLIENTREF = CLCARD.LOGICALREF) AND (CTRNS.SOURCEFREF = INVFC.LOGICALREF) " +
                    "AND (INVFC.CANCELLED = 0) AND (CTRNS.MODULENR = 4) AND (NOT (CTRNS.TRCODE IN (12,35,40)))), 0), 2) borc, ";


            sqlQuery += "ROUND(ISNULL((SELECT SUM(CTRNS.SIGN*CTRNS.REPORTNET) " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_CLFLINE CTRNS, LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC " +
                    "WHERE CTRNS.DEPARTMENT IN (0) and CTRNS.BRANCH IN (0) " +
                    "and (CLNTC.CODE LIKE CLCARD.CODE) ";

            if ((BEGDATE != null && ENDDATE != null) && (!BEGDATE.isEmpty() && !ENDDATE.isEmpty()) && (isLegalDate(BEGDATE) && isLegalDate(ENDDATE))) {
                sqlQuery += "AND (CTRNS.DATE_ >= " + "'" + BEGDATE + "' AND CTRNS.DATE_ <= " + "'" + ENDDATE + "') ";
            }

            sqlQuery += "AND (CTRNS.CLIENTREF = CLCARD.LOGICALREF) AND (CTRNS.CANCELLED = 0) " +
                    "AND (CTRNS.MODULENR <> 4) AND (NOT (CTRNS.TRCODE IN (12,35,40)))), 0) + " +
                    "ISNULL((SELECT SUM((CTRNS.SIGN+((1-CTRNS.SIGN)*INVFC.FROMKASA))*CTRNS.REPORTNET) " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_CLFLINE CTRNS, " +
                    "LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC, LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_INVOICE INVFC " +
                    "WHERE CTRNS.DEPARTMENT IN (0) and CTRNS.BRANCH IN (0) " +
                    "and (CLNTC.CODE LIKE CLCARD.CODE) ";

            if ((BEGDATE != null && ENDDATE != null) && (!BEGDATE.isEmpty() && !ENDDATE.isEmpty()) && (isLegalDate(BEGDATE) && isLegalDate(ENDDATE))) {
                sqlQuery += "AND (CTRNS.DATE_ >= " + "'" + BEGDATE + "' AND CTRNS.DATE_ <= " + "'" + ENDDATE + "') ";
            }

            sqlQuery += "AND (CTRNS.CLIENTREF = CLCARD.LOGICALREF) " +
                    "AND (CTRNS.SOURCEFREF = INVFC.LOGICALREF) AND (INVFC.CANCELLED = 0) " +
                    "AND (CTRNS.MODULENR = 4) AND (NOT (CTRNS.TRCODE IN (12,35,40)))), 0), 2) as alacak " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_CLCARD As CLCARD " +
                    "WHERE (CLCARD.CARDTYPE <> 22 AND CLCARD.CARDTYPE <> 4) ";

            if (ACCOUNT1 != null && !ACCOUNT1.isEmpty() && !ACCOUNT1.equals("-")
                    && FinanceController.ACCOUNT2 != null && !FinanceController.ACCOUNT2.isEmpty() && !FinanceController.ACCOUNT2.equals("-")) {
                sqlQuery += "AND (CLCARD.CODE BETWEEN '" + ACCOUNT1 + "' AND '" + FinanceController.ACCOUNT2 + "') ";
            } else if (ACCOUNT1 != null && !ACCOUNT1.isEmpty() && !ACCOUNT1.equals("-")) {
                sqlQuery += " AND (CLCARD.CODE = '" + ACCOUNT1 + "') ";
            }

            sqlQuery += " ORDER BY CODE ";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            customerList = new ArrayList<>();
            totalCount = 0;

            while (resultSet.next()) {

                customerList.add(
                        new Customer(
                                resultSet.getString("kod"),
                                resultSet.getString("aciklama"),
                                resultSet.getString("adres"),
                                resultSet.getString("telno"),
                                resultSet.getDouble("borc"),
                                resultSet.getDouble("alacak"),
                                Math.round(((resultSet.getDouble("borc") -
                                        resultSet.getDouble("alacak")) * 100d) / 100d)

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
        return new FinanceCustomerMain(totalCount, totalCount, customerList);
    }


    public static boolean isLegalDate(String date) {
        return date.length() <= 10;
    }

}

package kg.bakirov.alfa.repositories;

import kg.bakirov.alfa.models.Account;
import kg.bakirov.alfa.models.Firm;
import kg.bakirov.alfa.models.Period;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MainRepository {

    List<Firm> firmList = null;
    List<Period> periodList = null;
    List<Account> accountList = null;



    /* ---------------------------------------- Подключение к базе данных ------------------------------------------------ */

    public Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlserver://localhost;databaseName=TIGERDB", "Lbs_user", "123456789");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return connection;
    }



    /* ---------------------------------------- Финансовые периоды ------------------------------------------------ */

    public List<Period> getPeriodList(int firmNo) {
        Connection connection = null;

        try {
            connection = getConnection();

            String sqlQuery = "SELECT NR, FIRMNR, CONVERT(VARCHAR, BEGDATE, 104) as BEGDATE, CONVERT(VARCHAR, ENDDATE, 104) AS ENDDATE, ACTIVE FROM L_CAPIPERIOD WITH(NOLOCK) WHERE (FIRMNR = " + firmNo + ") ORDER BY NR DESC";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            periodList = new ArrayList<>();

            while (resultSet.next()) {
                periodList.add(
                        new Period(
                                resultSet.getInt("nr"),
                                resultSet.getInt("FIRMNR"),
                                resultSet.getString("BEGDATE"),
                                resultSet.getString("ENDDATE"),
                                resultSet.getInt("ACTIVE")
                        )
                );

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
        return periodList;
    }



    /* ---------------------------------------- Список компаний ------------------------------------------------ */

    public List<Firm> getFirmList() {
        Connection connection = null;

        try {
            connection = getConnection();

            String sqlQuery = "SELECT NR, NAME, TITLE FROM L_CAPIFIRM WITH(NOLOCK) ORDER BY NR";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            firmList = new ArrayList<>();

            while (resultSet.next()) {
                firmList.add(
                        new Firm(resultSet.getInt("nr"),
                                resultSet.getString("name"),
                                resultSet.getString("title")
                        )
                );
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
        return firmList;
    }



    /* ---------------------------------------- Список контрагентов ------------------------------------------------ */

    public List<Account> getAccountsList(int firmno) {
        Connection connection = null;
        String firm = String.format("%03d", firmno);

        try {
            connection = getConnection();

            String sqlQuery = "SELECT LGMAIN.CODE, LGMAIN.DEFINITION_ " +
                    "FROM LG_" + firm + "_CLCARD LGMAIN " +
                    "WHERE (LGMAIN.ACTIVE = 0) AND (LGMAIN.CARDTYPE = 3) " +
                    "ORDER BY LGMAIN.ACTIVE, LGMAIN.CODE";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            accountList = new ArrayList<>();

            while (resultSet.next()) {
                accountList.add(
                        new Account(resultSet.getString("code"), resultSet.getString("definition_"))
                );
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
        return accountList;
    }



    /* ---------------------------------------- Очистка сетевого файла ------------------------------------------------ */

    public void deleteNetwork(int firmno) {
        Connection connection = null;
        String firm = String.format("%03d", firmno);

        try {
            connection = getConnection();

            String sqlQuery = "DELETE FROM L_NET DELETE FROM L_GOUSERS DELETE FROM LG_NET_" + firm;
            Statement statement = connection.createStatement();
            statement.execute(sqlQuery);

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
    }


}

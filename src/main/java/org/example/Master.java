package org.example;


import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.sql.*;

class Master {
    Menu menu = new Menu();
    PasswordMaster passWordMaster = new PasswordMaster();
    UserMaster userMaster = new UserMaster();
    DuctionMaster ductionMaster = new DuctionMaster();

    Regest regest = new Regest();
    LogIn logIn = new LogIn();
    IsTrueEnter isTrueEnter = new IsTrueEnter();
    Scanner scanner = new Scanner(System.in);


    public void master(int command) {
        while (true) {
            if (command == 0) {
                break;
            } else {
                switch (command) {
                    case 1:
                        while (true) {
                            menu.showpasswordMaster();
                            System.out.print("请输入您的选择：");
                            command = isTrueEnter.inthefa(2);
                            if (command == 0) {
                                break;
                            } else {
                                passwordMaster(command);
                            }
                        }
                        break;
                    case 2:
                        while (true) {
                            menu.showuserMaster();
                            System.out.print("请输入您的选择：");
                            command = isTrueEnter.inthefa(3);
                            if (command == 0) {
                                break;
                            } else {
                                userMaster(command);
                            }
                        }
                        break;
                    case 3:
                        while (true) {
                            menu.showductionMaster();
                            System.out.print("请输入您的选择：");
                            command = isTrueEnter.inthefa(5);
                            if (command == 0) {
                                break;
                            } else {
                                ductionMaster(command);
                            }
                        }
                        break;
                }
            }
            menu.showMaster();
            System.out.print("请输入您的选择：");
            command = isTrueEnter.inthefa(3);
        }
    }

    public void passwordMaster(int command) {
        if (command == 1) {
            String userName = regest.getCurrentuserName();
            System.out.print("请输入您的旧密码：");
            String passwordOlder = scanner.next();
            if (logIn.adminLogin(userName, passwordOlder)) {
                System.out.print("请输入您的新密码：");
                String passwordNew = scanner.next();
                System.out.print("请确认您的新密码：");
                String passwordNew1 = scanner.next();
                while (true) {
                    if (passwordNew1.equals(passwordNew)) {
                        break;
                    }
                    System.out.println("您的新密码不一致！请重新输入：");
                    System.out.print("请输入您的新密码：");
                    passwordNew = scanner.next();
                    System.out.print("请确认您的新密码：");
                    passwordNew1 = scanner.next();
                }
                passWordMaster.modifySelfPassword(userName, passwordNew);
            }
        }
        if (command == 2) {
            System.out.print("请输入用户名：");
            String userName = scanner.next();
            if (logIn.userExit(userName)) {
                System.out.print("是否确定重置" + userName + "的密码：(Y/N)");
                String confirm = scanner.next();
                if (confirm.equalsIgnoreCase("Y")) {
                    passWordMaster.resetUserPassword(userName);
                }
            } else {
                System.out.println("该用户不存在！");
            }
        }
    }

    public void userMaster(int command) {
        if (command == 1) {
            userMaster.showUserData();
            menu.next();
        }
        if (command == 2) {
            System.out.print("请输入要删除的用户ID：");
            String userID = scanner.next();
            userMaster.deleteUserData(userID);
            menu.next();
        }
        if (command == 3) {
            menu.showSearchStyle();
            System.out.print("请选择输入您的选择：");
            int fangshi = isTrueEnter.inthefa(3);
            while (true) {
                if (fangshi == 0) {
                    break;
                } else {
                    switch (fangshi) {
                        case 1:
                            System.out.print("请输入用户ID：");
                            String userID = scanner.next();
                            userMaster.searchUserData(userID, "id");
                            break;
                        case 2:
                            System.out.print("请输入用户名：");
                            String username = scanner.next();
                            userMaster.searchUserData(username, "name");
                            break;
                        case 3:
                            userMaster.showUserData();
                            break;
                    }
                    menu.next();
                    menu.showSearchStyle();
                    System.out.print("请选择您的查询方式：");
                    fangshi = isTrueEnter.inthefa(3);
                }
            }
        }
    }

    public void ductionMaster(int command) {
        if (command == 1) {
            ductionMaster.showDuctionInfo();
        }
        if (command == 2) {
            ductionMaster.addDuctionInfo();
        }
        if (command == 3) {
            System.out.print("请输入要修改的商品编号：");
            String id = scanner.next();
            ductionMaster.modifyDuctionInfo(id);
        }
        if (command == 4) {
            System.out.print("请输入要删除的商品编号：");
            String id = scanner.next();
            ductionMaster.deleteDuctionInfo(id);
        }
        if (command == 5) {
            ductionMaster.searchDuctionInfo();
        }
        menu.next();
    }

}

class PasswordMaster {
    private final String bdULI = "jdbc:sqlite:" + System.getProperty("user.dir") + "//ShopSystem.sqlite";
    private final String driverName = "org.sqlite.JDBC";

    public void modifySelfPassword(String userName, String newPassword) {
        if (modifypassword("Master", userName, newPassword)) {
            System.out.println("修改成功！");
        } else {
            System.out.println("修改失败！");
        }
    }

    public void resetUserPassword(String userName) {
        if (modifypassword("PasswordMaster", userName, "@SPGL1234system")) {
            System.out.println("重置成功！");
        } else {
            System.out.println("重置失败！");
        }
    }

    public boolean modifypassword(String tableName, String userName, String newPassword) {
        try {
            Class.forName(driverName);
            Connection connection = DriverManager.getConnection(bdULI);
            //修改和userName匹配的用户密码
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE " + tableName + " SET password = ? WHERE name = ?");
            preparedStatement.setString(2, userName);
            preparedStatement.setString(1, newPassword);
            int rowsAffected = preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
            if (rowsAffected > 0) {
                return true;
            } else {
                System.out.println("未找到当前匹用户！");
                return false;
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
        return false;
    }
}

class UserMaster {
    private final String dbULI = "jdbc:sqlite:" + System.getProperty("user.dir") + "//ShopSystem.sqlite";
    private final String driverName = "org.sqlite.JDBC";

    public void showUserData() {
        List<String> userInfoList = readTableData("UserMaster");
        if (userInfoList.isEmpty()) {
            System.out.println("当前用户信息为空！");
        } else {
            System.out.println("当前用户信息如下：");
            System.out.println("-----------------------------------------------------------------");
            for (String userinfo : userInfoList) {
                String[] parts = userinfo.split(",");
                String output = "用户ID:" + parts[0] + " 用户名称:" + parts[1] + " 用户级别:" + parts[2] +
                        " 注册时间:" + parts[3] + " 累计消费金额:" + parts[4] + "￥ 电话号码:" + parts[5] + " 邮箱:" + parts[6];
                System.out.println(output);
            }
        }
    }

    public void searchUserData(String content, String key) {
        List<String> userinfoList = getDataByKeyword("UserMaster", content, key);
        if (!userinfoList.isEmpty()) {
            showUserInfo(userinfoList);
        } else {
            System.out.println("用户信息为空！");
        }
    }

    public void showUserInfo(List<String> infoList) {
        if (infoList.isEmpty()) {
            System.out.println("此类用户信息不存在！");
        } else {
            System.out.println("用户信息如下：");
            System.out.println("----------------------------------------------------------");
            for (String userinfo : infoList) {
                String[] parts = userinfo.split(",");
                String output = "用户ID:" + parts[0] + " 用户名称:" + parts[1] + " 用户级别:" + parts[2] +
                        " 注册时间:" + parts[3] + " 累计消费金额:" + parts[4] + " 电话号码:" + parts[5] + " 邮箱:" + parts[6];
                System.out.println(output);
            }
        }
    }

    public void deleteUserData(String userID) {
        if (deleteByKey("UserMaster", userID, "id")&&deleteByKey("PasswordMaster",userID,"id")) {
            System.out.println("删除成功！");
        } else {
            System.out.println("删除失败！");
        }
    }

    public boolean deleteByKey(String tableName, String content, String key) {
        //在数据库表tableName中，能够根据字段名key,删除所有与content相匹配的那一行数据，并在删除成功后返回true,否则返回false，
        try {
            Class.forName(driverName);
            Connection connection = DriverManager.getConnection(dbULI);
            String query = "DELETE FROM " + tableName + " WHERE " + key + " = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, content);
            int result = preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();

            if (result > 0) { // 删除成功
                return true;
            } else { // 没有符合条件的数据被删除
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("删除失败，请重试！");
            return false;
        }
    }

    public List<String> readTableData(String tableName) {
        //读取数据库中tableName的表格信息，并以List<String>将其返回
        List<String> tableData = new ArrayList<>();
        try {
            Class.forName(driverName);
            Connection connection = DriverManager.getConnection(dbULI);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName);
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (resultSet.next()) {
                StringBuilder rowData = new StringBuilder();
                for (int i = 1; i <= columnCount; i++) {
                    rowData.append(resultSet.getString(i)).append(",");
                }
                rowData.deleteCharAt(rowData.length() - 1);
                tableData.add(rowData.toString());
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("读取表格数据失败，请重试！");
        }
        return tableData;
    }

    public List<String> getDataByKeyword(String tableName, String content, String key) {
        //在数据库表tableName中，能够根据字段名key,找到所有与content相匹配的那一行数据，并以dataList返回
        List<String> dataList = new ArrayList<>();
        try {
            Class.forName(driverName);
            Connection connection = DriverManager.getConnection(dbULI);

            Statement statement = connection.createStatement();
            String query = "SELECT * FROM " + tableName + " WHERE " + key + " = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, content);

            ResultSet resultSet = preparedStatement.executeQuery();

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()) {
                StringBuilder rowData = new StringBuilder();

                for (int i = 1; i <= columnCount; i++) {
                    rowData.append(resultSet.getString(i)).append(",");
                }

                // 删除最后一个逗号
                rowData.deleteCharAt(rowData.length() - 1);

                dataList.add(rowData.toString());
            }

            resultSet.close();
            preparedStatement.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("根据关键字获取数据失败，请重试！");
        }
        return dataList;
    }
}

class DuctionMaster {
    Menu menu=new Menu();
    IsTrueEnter isTrueEnter = new IsTrueEnter();
    UserMaster userMaster = new UserMaster();
    Scanner scanner = new Scanner(System.in);
    private final String bdULI = "jdbc:sqlite:" + System.getProperty("user.dir") + "//ShopSystem.sqlite";
    private final String driverName = "org.sqlite.JDBC";

    public void searchDuctionInfo() {

        System.out.println("请选择查询方式：");
        System.out.println("(1) 根据商品名称查询");
        System.out.println("(2) 根据生产厂家查询");
        System.out.println("(3) 根据零售价格查询");
        System.out.println("(4) 组合查询");
        System.out.println("----------------------------------------");
        System.out.print("请选择操作：");
        int choice = isTrueEnter.inthefa(4);
        String name;
        String manufacturer;
        double minRetailPrice;
        double maxRetailPrice;

        switch (choice) {
            case 1:
                System.out.print("请输入商品名称：");
                name = scanner.next();
                List<String> productList = userMaster.getDataByKeyword("DuctionMaster", name, "name");
                showductioninfo(productList);
                break;
            case 2:
                System.out.print("请输入生产厂家：");
                manufacturer = scanner.next();
                List<String> productList1 = userMaster.getDataByKeyword("DuctionMaster", manufacturer, "manufacture");
                showductioninfo(productList1);
                break;
            case 3:
                System.out.print("请输入零售价格下限：");
                minRetailPrice = scanner.nextDouble();
                scanner.nextLine(); // 清空输入缓冲区
                System.out.print("请输入零售价格上限：");
                maxRetailPrice = scanner.nextDouble();
                scanner.nextLine(); // 清空输入缓冲区
                List<String> productList2 = getDuctionDataBykey("DuctionMaster", "", "", minRetailPrice, maxRetailPrice);
                showductioninfo(productList2);
                break;
            case 4:
                System.out.print("请输入商品名称：");
                name = scanner.next();
                System.out.print("请输入生产厂家：");
                manufacturer = scanner.next();
                System.out.print("请输入零售价格下限：");
                minRetailPrice = scanner.nextDouble();
                scanner.nextLine(); // 清空输入缓冲区
                System.out.print("请输入零售价格上限：");
                maxRetailPrice = scanner.nextDouble();
                scanner.nextLine(); // 清空输入缓冲区
                List<String> productList3 = getDuctionDataBykey("DuctionMaster", name, manufacturer, minRetailPrice, maxRetailPrice);
                showductioninfo(productList3);
                break;
            default:
                System.out.println("无效输入！");
        }

    }

    private void showductioninfo(List<String> productList) {
        if (productList.isEmpty()) {
            System.out.println("当前商品信息为空！");
        } else {
            System.out.println("-----------------------------------------------------------------");
            for (String userinfo : productList) {
                String[] parts = userinfo.split(",");
                String output = "商品编号:" + parts[0] + " 商品名称:" + parts[1] + " 生产厂家:" + parts[2] +
                        " 生产日期:" + parts[3] + " 型号:" + parts[4] + " 进货价:" + parts[5] + "￥ 零售价格:" + parts[6] + "￥ 库存数量:" + parts[7];
                System.out.println(output);
            }
            System.out.println("-----------------------------------------------------------------");
        }
    }

    private List<String> getDuctionDataBykey(String tableName, String name, String manufacture, double minprice, double maxprice) {
        //可以在数据库表tableName中查询与name,manufacture相同且价格在minprice和maxprice之间的商品，并返回List<String>
        List<String> dataList = new ArrayList<>();

        try {
            Class.forName(driverName);
            Connection connection = DriverManager.getConnection(bdULI);
            Statement statement = connection.createStatement();
            PreparedStatement preparedStatement;
            if (name.isEmpty() && manufacture.isEmpty()) {
                String query = "SELECT * FROM " + tableName + " WHERE CAST(sellprice AS DOUBLE ) >= ? AND AND CAST(sellprice AS DOUBLE ) <= ?";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setDouble(1, minprice);
                preparedStatement.setDouble(2, maxprice);
            } else {
                String query = "SELECT * FROM " + tableName + " WHERE name = ? AND manufacture = ? AND CAST(sellprice AS DOUBLE ) >= ? AND AND CAST(sellprice AS DOUBLE ) <= ?";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, manufacture);
                preparedStatement.setDouble(3, minprice);
                preparedStatement.setDouble(4, maxprice);
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()) {
                StringBuilder rowData = new StringBuilder();

                for (int i = 1; i <= columnCount; i++) {
                    rowData.append(resultSet.getString(i)).append(",");
                }

                // 删除最后一个逗号
                rowData.deleteCharAt(rowData.length() - 1);

                dataList.add(rowData.toString());
            }

            resultSet.close();
            preparedStatement.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("根据关键字获取数据失败，请重试！");
        }

        return dataList;
    }

    public void deleteDuctionInfo(String id) {
        if (userMaster.deleteByKey("DuctionMaster", id, "id")) {
            System.out.println("删除成功！");
        } else {
            System.out.println("删除失败！");
        }
    }

    public void modifyDuctionInfo(String id) {
        String newValue="";
        String modikey="";
        menu.showDuctionModify();
        System.out.print("请输入您的选择：");
        int option =isTrueEnter.inthefa(7);
        if(option==1){
            System.out.print("请输入新的商品名称（不修改请直接回车）：");
            String newName = scanner.nextLine();
            if (!newName.isEmpty()) {
                newValue = newName;
                modikey="name";
            }
        }
        if(option==2){
            System.out.print("请输入新的生产厂家（不修改请直接回车）：");
            String newName = scanner.nextLine();
            if (!newName.isEmpty()) {
                newValue = newName;
                modikey="manufacture";
            }
        }
        if(option==3){
            System.out.print("请输入新的生产日期（不修改请直接回车）：");
            String newName = scanner.nextLine();
            if (!newName.isEmpty()) {
                newValue = newName;
                modikey="productiondate";
            }
        }
        if(option==4){
            System.out.print("请输入新的产品型号（不修改请直接回车）：");
            String newName = scanner.nextLine();
            if (!newName.isEmpty()) {
                newValue = newName;
                modikey="model";
            }
        }
        if(option==5){
            System.out.print("请输入新的进货价格（不修改请直接回车）：");
            String newName = scanner.nextLine();
            if (!newName.isEmpty()) {
                newValue = newName;
                modikey="purchasprice";
            }
        }
        if(option==6){
            System.out.print("请输入新的零售价格（不修改请直接回车）：");
            String newName = scanner.nextLine();
            if (!newName.isEmpty()) {
                newValue = newName;
                modikey="sellprice";
            }
        }
        if(option==7){
            System.out.print("请输入新的库存数量（不修改请直接回车）：");
            String newName = scanner.nextLine();
            if (!newName.isEmpty()) {
                newValue = newName;
                modikey="stocknumber";
            }
        }

        if(modifyDuctionInfoBykey("DuctionMaster",id,"id",newValue,modikey)){
            System.out.println("修改成功！");
        }else{
            System.out.println("修改失败！");
        }

    }

    public boolean modifyDuctionInfoBykey(String tableName,String content,String matchkey,String newVlue,String modikey) {
        //修改数据库中tableName表中所有与字段matchkey中content相匹配的那一行信息，修改的位置是在该行的modikey字段下，新值为newVlue.修改成功后返回true,否则false.
        try {
            Class.forName(driverName);
            Connection connection = DriverManager.getConnection(bdULI);
            //修改和userName匹配的用户密码
            PreparedStatement preparedStatement = connection.prepareStatement( "UPDATE " + tableName + " SET " + modikey + " = ? WHERE " + matchkey + " = ?");
            preparedStatement.setString(2, content);
            preparedStatement.setString(1, newVlue);
            int rowsAffected = preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
            if (rowsAffected > 0) {
                return true;
            } else {
                System.out.println("未找到包含"+content+"的信息！");
                return false;
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
        return false;
    }

    public void addDuctionInfo() {
        System.out.print("请输入商品编号：");
        String id = scanner.nextLine();
        while(checkIDExit(id)){
            System.out.println("该类商品已存在，若要添加请修改库存即可！");
            System.out.print("请输入新商品编号：");
            id=scanner.nextLine();
        }
        System.out.print("请输入商品名称：");
        String name = scanner.nextLine();
        System.out.print("请输入生产厂家：");
        String manufacturer = scanner.nextLine();
        System.out.print("请输入生产日期：");
        String productionDate = scanner.nextLine();
        System.out.print("请输入型号：");
        String model = scanner.nextLine();
        System.out.print("请输入进货价：");
        double purchasePrice = scanner.nextDouble();
        System.out.print("请输入零售价格：");
        double retailPrice = scanner.nextDouble();
        System.out.print("请输入数量：");
        int quantity = scanner.nextInt();
        scanner.nextLine(); // 清空输入缓冲区

        String productInfo = String.format("%s,%s,%s,%s,%s,%.2f,%.2f,%d", id, name, manufacturer,
                productionDate, model, purchasePrice, retailPrice, quantity);

        List<String> ductionList=List.of(productInfo);
        if(insertDuctionInfo("DuctionMaster",ductionList)){
            System.out.println("添加成功！");
        }else{
            System.out.println("添加失败！");
        }
    }

    private boolean checkIDExit(String id) {
        return IDExit("DuctionMaster",id);
    }

    private boolean IDExit(String tableName,String id) {
        try {
            Class.forName(driverName);
            Connection connection = DriverManager.getConnection(bdULI);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName + " WHERE id = '" + id + "'");
            boolean sucess = false;
            if (resultSet.next()) {
                sucess = true;
            }
            resultSet.close();
            statement.close();
            connection.close();
            return sucess;
        } catch (Exception e) {
            e.getStackTrace();
        }
        return false;
    }

    private boolean insertDuctionInfo(String tableName, List<String> ductionList) {
            try {
                int sucess=0;
                Class.forName(driverName);
                Connection connection = DriverManager.getConnection(bdULI);
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO " + tableName + "(id, name, manufacture,productiondate,model,purchasprice,sellprice,stocknumber) VALUES (?,?,?,?,?,?,?,?)");
                for (String info : ductionList) {
                    String[] values = info.split(","); // 拆分逗号分隔的值
                    if (values.length >= 8) {
                        preparedStatement.setString(1, values[0]);
                        preparedStatement.setString(2, values[1]);
                        preparedStatement.setString(3, values[2]);
                        preparedStatement.setString(4, values[3]);
                        preparedStatement.setString(5, values[4]);
                        preparedStatement.setString(6, values[5]);
                        preparedStatement.setString(7, values[6]);
                        preparedStatement.setString(8, values[7]);
                        sucess=preparedStatement.executeUpdate();
                    }
                }
                preparedStatement.close();
                connection.close();
                if(sucess>0){
                    return true;
                }else{
                    return false;
                }
            } catch (Exception e) {
                e.getStackTrace();
            }
        return false;
    }

    public void showDuctionInfo() {
        List<String> ductioninfoList=userMaster.readTableData("DuctionMaster");
        if(!ductioninfoList.isEmpty()){
            System.out.println("商品信息如下：");
            showductioninfo(ductioninfoList);
        }
    }
}
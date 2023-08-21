package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

class User {
    Menu menu=new Menu();
    Regest regest=new Regest();
    IsTrueEnter isTrueEnter=new IsTrueEnter();
    PasswordUser passwordUser=new PasswordUser();
    ShopUser shopUser=new ShopUser();
    LogIn logIn=new LogIn();
    Scanner scanner=new Scanner(System.in);
    public int passwordUser(int command){
        int exit=1;
        if(command==1){
            String userName=regest.getCurrentuserName();
            System.out.print("请确认旧密码：");
            String passwordOld= isTrueEnter.passwordhefa(scanner.next());
            System.out.print("请输入新密码：");
            String passwordNew= isTrueEnter.passwordhefa(scanner.next());
            if(logIn.userLogin(userName,passwordOld)){
                passwordUser.fixPassword(userName,passwordNew);
            }else{
                System.out.println("密码不匹配！");
            }
        }
        if(command==2){
            System.out.print("请输入用户名：");
            String username = scanner.next();
            System.out.print("请输入注册时使用的邮箱地址：");
            String email = scanner.next();
            boolean sucess=passwordUser.forgotPassword(username,email);
            if(sucess){
                exit=0;
            }
        }
        menu.next();
        return exit;
    }
    public int shopUser(int command){
        DuctionMaster ductionMaster=new DuctionMaster();
        if(command==1){
            ductionMaster.showDuctionInfo();
            System.out.println("-------------------------------------------------");
            System.out.print("请输入商品编号：");
            String shopID= scanner.next();
            System.out.print("请输入购买数量：");
            String shopNum=scanner.next();
            boolean sucess=shopUser.addDuction(shopID,shopNum);
            if(sucess){
                System.out.println("添加成功！");
                shopUser.showCurrentChat();
            }else{
                System.out.println("添加失败！");
            }
        }
        if(command==2){
            System.out.print("请输入商品编号：");
            String shopID= scanner.next();
            shopUser.showCurrentChat();
            shopUser.removeDuction(shopID);
            shopUser.showCurrentChat();
        }
        if(command==3){
            System.out.print("请输入商品编号：");
            String shopID= scanner.next();
            shopUser.modifyDuctionInfo(shopID);
            shopUser.showCurrentChat();
        }
        if(command==4){
            shopUser.checkout();
        }
        if(command==5){
            shopUser.showHistory();
        }
        menu.next();
        return 1;
    }
    public void user(int command,boolean susucessLoin){
        menu.showSecondUser();
        System.out.print("请输入您的选择：");
        command = isTrueEnter.inthefa(2);
        int exit=1;
        while(true){
            if(command==0){
                break;
            }else{
                switch(command){
                    case 1:
                        menu.showpasswordUser();
                        System.out.print("请输入您的选择：");
                        command=isTrueEnter.inthefa(2);
                        while(true){
                            if(command==0){
                                break;
                            }else{
                                exit=passwordUser(command);
                                if(exit==0){
                                    command=0;
                                    break;
                                }
                            }
                            if(exit!=0){
                                menu.showpasswordUser();
                                System.out.print("请输入您的选择：");
                                command=isTrueEnter.inthefa(2);
                            }
                        }
                        break;
                    case 2:
                        menu.showShopUser();
                        System.out.print("请输入您的选择：");
                        command=isTrueEnter.inthefa(5);
                        while(true) {
                            if(command==0){
                                break;
                            }else{
                                exit=shopUser(command);
                                if(exit==0){
                                    break;
                                }
                            }
                            if(exit!=0){
                                menu.showShopUser();
                                System.out.print("请输入您的选择：");
                                command=isTrueEnter.inthefa(5);
                            }
                        }
                        break;
                }
            }
            if(exit!=0){
                menu.showSecondUser();
                System.out.print("请输入您的选择：");
                command = isTrueEnter.inthefa(2);
            }
        }
    }
}
class PasswordUser{
    private final String dbURL = "jdbc:sqlite:" + System.getProperty("user.dir") + "//ShopSystem.sqlite";
    private final String driverName = "org.sqlite.JDBC";
    PasswordMaster passwordMaster=new PasswordMaster();
    public boolean forgotPassword(String username, String email) {
        boolean sucess = false;
        if (isMatch(username, email)) {
            String newPassword = generateRandomPassword();
            sendEmail(email, newPassword);
            System.out.println("新密码已发送到您的邮箱，请查收。");
            System.out.println("请使用新密码登录，并尽快修改为您熟悉的密码。");
            fixPassword(username, newPassword);
            sucess = true;
        } else {
            System.out.println("用户名和邮箱地址不匹配!");
        }
        return sucess;
    }

    private boolean isMatch(String username, String email) {
        return cheakemail("UserMaster",username,email);
    }

    private boolean cheakemail(String tableName, String userName,String email) {
        try {
            Class.forName(driverName);
            Connection connection = DriverManager.getConnection(dbURL);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName + " WHERE name = '" + userName + "' AND email='"+email+"'");
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

    private void sendEmail(String email, String newPassword) {
        System.out.println("已向邮箱 " + email + " 发送新密码：" + newPassword);
    }

    private String generateRandomPassword() {
        String uppercaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowercaseLetters = "abcdefghijklmnopqrstuvwxyz";
        String specialCharacters = "!@#$%^&*()_+";
        String numbers = "0123456789";

        Random random = new Random();
        StringBuilder password = new StringBuilder();

        // 生成包含至少一个大写字母、一个小写字母、一个特殊字符和一个数字的密码
        password.append(uppercaseLetters.charAt(random.nextInt(uppercaseLetters.length())));
        password.append(lowercaseLetters.charAt(random.nextInt(lowercaseLetters.length())));
        password.append(specialCharacters.charAt(random.nextInt(specialCharacters.length())));
        password.append(numbers.charAt(random.nextInt(numbers.length())));

        // 生成剩余的密码字符
        for (int i = 4; i < 10; i++) {
            String allCharacters = uppercaseLetters + lowercaseLetters + specialCharacters + numbers;
            password.append(allCharacters.charAt(random.nextInt(allCharacters.length())));
        }

        return password.toString();
    }
    public void fixPassword(String userName, String passwordNew) {
        if (passwordMaster.modifypassword("PasswordMaster", userName, passwordNew)) {
            System.out.println("修改成功！");
        } else {
            System.out.println("修改失败！");
        }
    }
}
class ShopUser{
    UserMaster userMaster=new UserMaster();
    Scanner scanner=new Scanner(System.in);
    Regest regest=new Regest();
    Menu menu=new Menu();
    DuctionMaster ductionMaster=new DuctionMaster();
    private final String bdULI = "jdbc:sqlite:" + System.getProperty("user.dir") + "//ShopSystem.sqlite";
    private final String driverName = "org.sqlite.JDBC";
    public void checkout() {
        menu.showCheck();
        System.out.print("请选择您的支付方式：");
        IsTrueEnter isTrueEnter = new IsTrueEnter();
        int check = isTrueEnter.inthefa(3);
        double xiaofei = getTotalPrice();
        System.out.print("共花费"+xiaofei+"￥，是否支付:(Y/N)");
        String confirm = scanner.next();
        if (confirm.equalsIgnoreCase("Y")) {
            switch (check) {
                case 1:
                    System.out.println("支付宝支付中！");
                    break;
                case 2:
                    System.out.println("微信支付中！");
                    break;
                case 3:
                    System.out.println("银行卡支付中！");
                    break;
            }
            if (updataInfo(xiaofei)) {
                clearSheetData("CurrentShopCart");
                System.out.println("支付成功！");
            } else {
                System.out.println("支付失败！");
            }
        } else {
            System.out.println("您已取消支付！");
        }
    }

    public void clearSheetData(String currentShopCart) {
        try {
            Class.forName(driverName);
            Connection connection = DriverManager.getConnection(bdULI);
            String sqlDeleteAllData = "DELETE FROM " + currentShopCart;
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(sqlDeleteAllData);
            } catch (SQLException e) {
                System.out.println("清除表格数据时发生错误: " + e.getMessage());
            }

            connection.close();
        } catch (Exception e) {
            System.out.print("清除表格数据发生错误：" + e.getMessage());
        }
    }

    private boolean updataInfo(double price) {
        if (updateHistory()) {
            updateUserInfo("UserMaster", price);
            updateStock();
            return true;
        }
        return false;
    }

    private void updateStock() {
        List<String> buyList = userMaster.readTableData("CurrentShopCart");
        if (buyList.isEmpty()) {
            System.out.println("购物车为空！");
        } else {
            for (String buyinfo : buyList) {
                String[] parts = buyinfo.split(",");
                int stocknum = getCellValue("DuctionMaster",parts[0],"id",8);
                int num = stocknum - Integer.parseInt(parts[6]);
                String newVlue = String.valueOf(num);
                ductionMaster.modifyDuctionInfoBykey("DuctionMaster",parts[0],"id",newVlue,"stocknumber");
            }
        }
    }

    private int getCellValue(String tableName, String content, String key, int columnIndex) {
        int cellValue = 0;

        try {
            Class.forName(driverName);
            Connection connection = DriverManager.getConnection(bdULI);

            String sqlSelect = "SELECT * FROM " + tableName +
                    " WHERE " + key + " = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlSelect)) {
                preparedStatement.setString(1, content);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    cellValue = resultSet.getInt(columnIndex);
                }

                resultSet.close();
            } catch (SQLException e) {
                System.out.println("查询数据时发生错误: " + e.getMessage());
            }

            connection.close();
        } catch (Exception e) {
            System.out.print("连接数据库发生错误：" + e.getMessage());
        }

        return cellValue;
    }

    private void updateUserInfo(String tableName,double price){
        String newLevel=getLevel(price);
        String newHost=String.valueOf(price);
        ductionMaster.modifyDuctionInfoBykey(tableName,regest.getCurrentuserName(),"name",newLevel,"level");
    }

    private String getLevel(double ptice) {
        if(ptice<=5000&&ptice>1000){
            return "银牌客户";
        }
        if(ptice>5000){
            return "金牌客户";
        }
        if(ptice<1000){
            return "铜牌客户";
        }
        return "";
    }

    private boolean updateHistory() {
        String sheetName1=regest.getCurrentuserName();
        createTableByName(sheetName1);
        List<String> buyList=userMaster.readTableData("CurrentShopCart");
        if(buyList.isEmpty()){
            System.out.println("购物车为空！");
        }else{
            addCartInfo(sheetName1,buyList);
            return true;
        }
        return false;
    }

    private double getTotalPrice() {
        double sumprice = 0;
        List<String> buyList = userMaster.readTableData("CurrentShopCart");
        if (buyList.isEmpty()) {
            return 0;
        } else {
            for (String buyinfo : buyList) {
                String[] parts = buyinfo.split(",");
                sumprice += Double.parseDouble(parts[5]) * Integer.parseInt(parts[6]);
            }
        }
        return sumprice;
    }

    public void showHistory() {
        List<String> shopcartList=userMaster.readTableData(regest.getCurrentuserName());
        if(shopcartList.isEmpty()){
            System.out.println("历史购物为空!");
        }else{
            System.out.println("历史信息如下：");
            showShopData(shopcartList);
        }
    }

    public void showCurrentChat() {
        List<String> shopcartList=userMaster.readTableData("CurrentShopCart");
        if(shopcartList.isEmpty()){
            System.out.println("当前购物车为空!");
        }else{
            System.out.println("当前购物车信息如下：");
            showShopData(shopcartList);
        }
    }
    private void showShopData(List<String> shopcartList){
        System.out.println("----------------------------------------");
        for(String info:shopcartList){
            String[] part=info.split(",");
            String output="商品编号："+part[0]+" 商品名称："+part[1]+" 生产厂家："+part[2]+" 商品型号："+part[3]+" 购买日期："+
                    part[4]+" 购买价格："+part[5]+" 购买数量："+part[6];
            System.out.println(output);
        }
    }

    public void modifyDuctionInfo(String shopID) {
        System.out.print("请输入新数量：(不修改直接回车)");
        String newValue = scanner.nextLine();
        if (!newValue.isEmpty()) {
            if (ductionMaster.modifyDuctionInfoBykey("CurrentShopCart",shopID,"id",newValue,"num")) {
                System.out.println("修改成功！");
            } else {
                System.out.println("修改失败！");
            }
        } else {
            System.out.println("您已取消修改！");
        }
    }

    public void removeDuction(String shopID) {
        if (userMaster.deleteByKey("CurrentShopCart",shopID,"id")) {
            System.out.println("删除成功！");
        } else {
            System.out.println("删除失败！");
        }
    }

    public boolean addDuction(String shopID, String shopNum) {
        List<String> ductionList=userMaster.getDataByKeyword("DuctionMaster",shopID,"id");
        List<String> addResult=new ArrayList<>();
        if(!ductionList.isEmpty()){
            for(String info:ductionList ){
                String[] parts=info.split(",");
                String add=parts[0]+","+parts[1]+","+parts[2]+","+parts[4]+","+
                        regest.getCurrentDate()+","+parts[6]+","+shopNum;
                addResult.add(add);
            }
        }

        return addCartInfo("CurrentShopCart",addResult);
    }
    public boolean addCartInfo(String tableName,List<String> addResult){
        try {
            int sucess=0;
            Class.forName(driverName);
            Connection connection = DriverManager.getConnection(bdULI);
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO " + tableName + "(id, name, manufacture,model,date,buyprice,num) VALUES (?,?,?,?,?,?,?)");
            for (String info : addResult) {
                String[] values = info.split(","); // 拆分逗号分隔的值
                if (values.length >= 7) {
                    preparedStatement.setString(1, values[0]);
                    preparedStatement.setString(2, values[1]);
                    preparedStatement.setString(3, values[2]);
                    preparedStatement.setString(4, values[3]);
                    preparedStatement.setString(5, values[4]);
                    preparedStatement.setString(6, values[5]);
                    preparedStatement.setString(7, values[6]);
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
    public void createTableByName(String tableName){
        try{
            Class.forName(driverName);
            Connection connection=DriverManager.getConnection(bdULI);

            String sqlCreateTable = "CREATE TABLE IF NOT EXISTS " + tableName +
                    "(" +
                    "id TEXT, " +
                    "name TEXT, " +
                    "manufacture TEXT, " +
                    "model TEXT,"+
                    "date TEXT, " +
                    "buyprice TEXT, " +
                    "num TEXT" +
                    ")";

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(sqlCreateTable);
            } catch (SQLException e) {
                System.out.println("创建表格时发生错误: " + e.getMessage());
            }

            connection.close();

        }catch (Exception e){
            System.out.print("创建表格发生错误："+e.getMessage());
        }
    }
}

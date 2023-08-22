package org.example;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

class LogIn {

    Regest regest = new Regest();
    IsTrueEnter isTrueEnter = new IsTrueEnter();
    Scanner scanner = new Scanner(System.in);
    private final String bdULI = "jdbc:sqlite:" + System.getProperty("user.dir") + "//ShopSystem.sqlite";
    private final String driverName = "org.sqlite.JDBC";

    public int userSign(int command) {
        int count = 0;
        String userName;
        String password;
        if (command == 1) {
            System.out.print("请输入管理员名称：");
            userName = scanner.next();
            System.out.print("请输入您的密码：");
            password = scanner.next();
            boolean adminExists = adminExit(userName);
            if (!adminExists) {
                System.out.println("管理员不存在！");
                count = 5;
            } else {
                while (!adminLogin(userName, password) && count < 6) {
                    count++;
                    System.out.println("管理员不存在或密码错误！");
                    System.out.print("用户名：");
                    userName = scanner.next();
                    if (adminExit(userName)&&count<5) {
                        System.out.print("密码：");
                        password = scanner.next();
                    } else {
                        if(count<5&&count>3){
                            System.out.print("您已多次失败！再有三次错误账户即将被锁定！");
                        }
                        System.out.println("用户名或密码错误，是否继续？（Y/N）");
                        String confirm = scanner.next();
                        if (!confirm.equalsIgnoreCase("Y")) {
                            count = 5;
                            break;
                        }
                    }

                }
                regest.setCurrentUserName(userName);
            }
        }
        if (command == 2) {
            System.out.print("请输入您的用户名：");
            userName = scanner.next();
            System.out.print("请输入您的密码：");
            password = isTrueEnter.passwordhefa(scanner.next());
            boolean userExists = userExit(userName);
            if (!userExists) {
                System.out.println("用户不存在！");
                count = 5;
            } else {
                while (!userLogin(userName, password) && count < 6) {
                    count++;
                    System.out.println("用户名或密码错误！请重新输入：");
                    System.out.print("用户名：");
                    userName = scanner.next();
                    scanner.nextLine();
                    if (userExit(userName)&&count<5) {
                        System.out.print("密码：");
                        password = isTrueEnter.passwordhefa(scanner.next());
                        scanner.nextLine();
                    } else {
                        if(count<5&&count>3){
                            System.out.print("您已多次失败！再有三次错误账户即将被锁定！");
                        }
                        System.out.println("用户名或密码错误，是否继续？（Y/N）");
                        String confirm = scanner.next();
                        if (!confirm.equalsIgnoreCase("Y")) {
                            count = 5;
                            break;
                        }
                    }

                }
                regest.setCurrentUserName(userName);
                ShopUser shopUser=new ShopUser();
                shopUser.clearSheetData("CurrentShopCart");
            }
        }
        if (count > 5) {
            System.out.println("由于您多次失败，请三小时后再试！");
            Menu menu = new Menu();
            long yanchi = 1000 * 60 * 60 * 3;
            menu.consoleDelay(yanchi);
        } else if (count == 5) {
            System.out.println("登录失败！");
        } else {
            System.out.println("登录成功！");
        }
        return count;
    }

    public void regestuser() {
        System.out.print("请输入您的用户名:");
        String userName = scanner.next();
        while (cheakExit("PasswordMaster", userName)) {
            System.out.print("该用户已存在！请重新输入您的用户名：");
            userName = scanner.next();
        }
        System.out.print("请输入您的密码：");
        String password = isTrueEnter.passwordhefa(scanner.next());
        System.out.print("请确认您的密码：");
        String password1 = isTrueEnter.passwordhefa(scanner.next());
        if (password1.equals(password)) {
            String id = register(userName, password);
            regest.userRegest(id, userName);
        } else {
            System.out.println("密码不一致！");
        }

    }

    public boolean userLogin(String userName, String password) {
        password=encryptPassword(password);
        return checkLoin("PasswordMaster", userName, password);
    }

    public boolean userExit(String userName) {
        return cheakExit("PasswordMaster", userName);
    }

    public boolean adminLogin(String userName, String password) {
        password=encryptPassword(password);
        return checkLoin("Master", userName, password);
    }

    public boolean adminExit(String userName) {

        return cheakExit("Master", userName);
    }
    public  String encryptPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder encryptedPassword = new StringBuilder();
            for (byte b : hash) {
                encryptedPassword.append(String.format("%02x", b));
            }
            return encryptedPassword.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("加密算法不可用", e);
        }
    }
    private boolean checkLoin(String tableName, String userName, String password) {
        try {
            Class.forName(driverName);
            Connection connection = DriverManager.getConnection(bdULI);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName + " WHERE name = '" + userName + "' AND password = '" + password + "'");
            boolean sucess = false;
            if (resultSet.next()) {
                sucess = true;
            }
            resultSet.close();
            statement.close();
            connection.close();
            return sucess;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean cheakExit(String tableName, String userName) {
        try {
            Class.forName(driverName);
            Connection connection = DriverManager.getConnection(bdULI);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName + " WHERE name = '" + userName + "'");
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

    public String register(String userName, String password) {
        //随机生产一个id，并将id,userName和password插入数据库的UserMaster表中id,name,password三个字段下，
        String id = regest.getRandomID();
        password=encryptPassword(password);
        String baseInfo = id + "," + userName + "," + password;
        List<String> userBaseInfo = List.of(baseInfo);
        regest.inertBaseInfo("PasswordMaster", userBaseInfo);
        return id;
    }

}

class Regest {
    Scanner scanner = new Scanner(System.in);
    private final String bdULI = "jdbc:sqlite:" + System.getProperty("user.dir") + "//ShopSystem.sqlite";
    private final String driverName = "org.sqlite.JDBC";

    public void inertBaseInfo(String tableName, List<String> inserInfoList) {
        try {
            Class.forName(driverName);
            Connection connection = DriverManager.getConnection(bdULI);
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO " + tableName + "(id, name, password) VALUES (?, ?, ?)");
            for (String info : inserInfoList) {
                String[] values = info.split(","); // 拆分逗号分隔的值
                if (values.length >= 3) {
                    preparedStatement.setString(1, values[0]);
                    preparedStatement.setString(2, values[1]);
                    preparedStatement.setString(3, values[2]);
                    preparedStatement.executeUpdate();
                }
            }
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public String getRandomID() {
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int LENGTH = 6;
        Random RANDOM = new Random();
        StringBuilder sb = new StringBuilder();

        char firstChar = CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length()));
        sb.append(firstChar);

        for (int i = 0; i < LENGTH - 1; i++) {
            int randomDigit = RANDOM.nextInt(10);
            sb.append(randomDigit);
        }

        return sb.toString();
    }

    public String getCurrentDate() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);

    }

    public void setCurrentUserName(String content) {
        final String filePath = System.getProperty("user.dir") + "//src//main//java//org//example//text//CurrentUser.txt";
        try {
            // 创建 FileWriter 对象，传入文件路径
            FileWriter fileWriter = new FileWriter(filePath);
            // 将内容写入文件
            fileWriter.write(content);
            // 刷新缓冲区数据到文件
            fileWriter.flush();
            // 关闭 FileWriter 对象
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("写入文件时发生错误：" + e.getMessage());
        }
    }

    public String getCurrentuserName() {
        final String filePath = System.getProperty("user.dir") + "//src//main//java//org//example//text//CurrentUser.txt";
        String fileContent;
        try {
            // 使用 Files 类的静态方法读取文件内容为字节数组
            byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
            // 将字节数组转换为字符串
            fileContent = new String(fileBytes);
        } catch (IOException e) {
            System.out.println("读取文件时发生错误：" + e.getMessage());
            fileContent = "";
        }
        return fileContent;
    }

    private String getInput(String prompt) {
       
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public void userRegest(String id, String userName) {
        String phoneNumber = getInput("请输入手机号：");
        String email = getInput("请输入邮箱：");
        String registDate = getCurrentDate();
        String content = id + "," + userName + "," + "铜牌客户" + "," + registDate + ",0," + phoneNumber + "," + email;
        List<String> inforList = List.of(content);
        try {
            Class.forName(driverName);
            Connection connection = DriverManager.getConnection(bdULI);
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO UserMaster (id,name,level,date,cost,phoneNumber,email) VALUES (?,?,?,?,?,?,?)");
            for (String info : inforList) {
                String[] values = info.split(","); // 拆分逗号分隔的值
                if (values.length >= 7) {
                    preparedStatement.setString(1, values[0]);
                    preparedStatement.setString(2, values[1]);
                    preparedStatement.setString(3, values[2]);
                    preparedStatement.setString(4, values[3]);
                    preparedStatement.setString(5, values[4]);
                    preparedStatement.setString(6, values[5]);
                    preparedStatement.setString(7, values[6]);
                    preparedStatement.executeUpdate();
                }
            }
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demo.baitaplon;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Buu
 */
public class Tester {
    public static void main(String[] args) throws ClassNotFoundException, SQLException, ParseException, Exception {
        Scanner scanner = new Scanner(System.in);
        boolean loop = true;
        boolean loop1 = true;
        boolean loop2 = true;
        boolean loop3 = true;
        while(loop){
            System.out.println("\n1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Your chocie: ");
            String choice = scanner.nextLine();
            switch(choice){
                case "1":
                    loop1 = true;
                    while(loop1){
                        System.out.print("\nusername: ");
                        String user = scanner.nextLine();
                        System.out.print("password: ");
                        String pass = scanner.nextLine();
                        try{
                            User u1 = new User(user, pass);
                            UserManagement us = new UserManagement();
                            QuestionManagement qs = new QuestionManagement();
                            System.out.println("\nHi, " + u1.getFullName());
                            System.out.println("What do you want to do?");
                            int dem = 0;
                            do{
                                System.out.println("1. See the user list");
                                System.out.println("2. Look up the user");
                                System.out.println("3. Delete account");
                                System.out.println("4. Update information of user");
                                System.out.println("5. See the question list");
                                System.out.println("6. Look up the questions");
                                System.out.println("7. Practice");
                                System.out.println("8. Score statistics by month");
                                System.out.println("9. Logout");
                                
                                System.out.print("Your choice: ");
                                String c = scanner.nextLine();
                                switch(c){
                                    case "1":
                                        us.viewList(getConnection());
                                        break;
                                    case "2":
                                        System.out.println("1. Look up by fullname");
                                        System.out.println("2. Look up by gender");
                                        System.out.println("3. Look up by date of birth");
                                        System.out.println("4. Look up by country");
                                        System.out.print("Your choice: ");
                                        String ce = scanner.nextLine();
                                        switch(ce){
                                            case "1":
                                                System.out.print("Enter the fullname you want to look up: ");
                                                String fn = scanner.nextLine();
                                                us.lookUpByFullName(fn, getConnection()).forEach(p -> {
                                                    System.out.println(p);
                                                });
                                                break;
                                            case "2":
                                                System.out.print("Enter the gender you want to look up: ");
                                                String g = scanner.nextLine();
                                                us.lookUpByGender(g, getConnection()).forEach(p -> {
                                                    System.out.println(p);
                                                });
                                                break;
                                            case "3":
                                                System.out.print("Enter the dob you want to look up: ");
                                                String dob = scanner.nextLine();
                                                us.lookUpByDOB(dob, getConnection()).forEach(p -> {
                                                    System.out.println(p);
                                                });
                                                break;
                                            case "4":
                                                System.out.print("Enter the country you want to look up: ");
                                                String country = scanner.nextLine();
                                                us.lookUpByCountry(country, getConnection()).forEach(p -> {
                                                    System.out.println(p);
                                                });
                                                break;
                                        }
                                        break;
                                    case "3":
                                        us.removeUser(u1, getConnection());
                                        loop3 = false;
                                        u1 = null;
                                        break;
                                    case "6":
                                        System.out.println("1. Look up by content");
                                        System.out.println("2. Look up by category");
                                        System.out.println("3. Look up by level");
                                        System.out.print("Your choice: ");
                                        String choice6 = scanner.nextLine();
                                        switch(choice6){
                                            case "1":
                                                System.out.print("Enter the content you want to look up: ");
                                                String ct = scanner.nextLine();
                                                qs.lookUpByContent(ct, getConnection()).forEach(q -> System.out.println(q));
                                                break;
                                            case "2":
                                                System.out.print("Enter the category you want to look up: ");
                                                String cate = scanner.nextLine();
                                                qs.lookUpByCate(cate, getConnection()).forEach(q -> System.out.println(q));
                                                break;
                                            case "3":
                                                System.out.print("Enter the level you want to look up: ");
                                                String lv = scanner.nextLine();
                                                qs.lookUpByLevel(lv, getConnection()).forEach(q -> System.out.println(q));
                                                break;
                                        }
                                        break;
                                    case "7":
                                        do{
                                            System.out.println("\n1. Practice MultipleChoice Question");
                                            System.out.println("2. Practice InComplete Question");
                                            System.out.println("3. Practice Conversation Question");

                                            System.out.print("Your choice: ");
                                            String ch = scanner.nextLine();
                                            switch(ch){
                                                case "1":
                                                    System.out.print("Number of question you want to practice: ");
                                                    int n = scanner.nextInt();
                                                    qs.practiceMultipleC(scanner, n, u1, getConnection());
                                                    break;
                                                case "2":
                                                    System.out.print("Level you want to practice: ");
                                                    String lv = scanner.nextLine();
                                                    qs.practiceInComplete(scanner, lv, u1, getConnection());
                                                    break;
                                                case "3":
                                                    System.out.print("Level you want to practice: ");
                                                    String lv1 = scanner.nextLine();
                                                    qs.practiceConversation(scanner, lv1, u1, getConnection());
                                                    break;
                                            }
                                            System.out.print("Do you want to countinue practicing? (Y/N): ");
                                            String kw = scanner.nextLine().toUpperCase();
                                            switch(kw){
                                                case "Y":
                                                    loop2 = true;
                                                    break;
                                                case "N":
                                                    loop2 = false;
                                                    loop3 = true;
                                                    break;
                                            }
                                        }while(loop2);
                                        break;
                                    case "8":
                                        System.out.println("1. Number of tests and points for each test");
                                        System.out.println("2. Average score of the month");
                                        System.out.print("Your choice: ");
                                        String coi = scanner.nextLine();
                                        switch(coi){
                                            case "1":
                                                int n = u1.getNumberOfTests(getConnection());
                                                if(n > 0){
                                                    System.out.println();
                                                    u1.pointsForEachTest(getConnection());
                                                    System.out.println();
                                                }
                                                loop3 = true;
                                                break;
                                            case "2":
                                                break;
                                        }
                                        break;
                                    case "9":
                                        loop3 = false;
                                        break;
                                }
                              
                            }while(loop3);
                            loop1 = false;
                        }
                        catch(Exception e){
                            System.err.println(e.getMessage());
                        }
                    }
                    break;
                case "2":
                    boolean l = true;
                    do{
                        try {
                            User u2 = User.register(scanner);
                            l = false;
                        } catch (Exception e) {
                            System.err.println(e.getMessage());
                            l = true;
                        }
                        
                        
                    }while(l);
                    break;
                case "3":
                    loop = false;
                    break;
            }
        }
    }
    
    public static Connection getConnection() throws ClassNotFoundException, SQLException{
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/baitaplon", "root", "123456789");
        return conn;
    }
    
    public static int getIdUser(String username) throws SQLException, ClassNotFoundException{
        Connection conn = getConnection();
        Statement stm = conn.createStatement();
        ResultSet rs = stm.executeQuery("SELECT u.id FROM user u WHERE u.username = '" + username + "'");
        int id = 0;
        while(rs.next()){
            id = rs.getInt("id");
        }
        rs.close();
        stm.close();
        conn.close();
        return id;
    }

}

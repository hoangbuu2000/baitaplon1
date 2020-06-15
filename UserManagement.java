/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demo.baitaplon;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Buu
 */
public class UserManagement {
    private List<User> users = new ArrayList<>();
    
    public void addUser(User u){
        this.getUsers().add(u);
    }
    
    public void removeUser(User u){
        this.getUsers().remove(u);
    }
    
    public void removeUser(User u, Connection conn) throws SQLException{
        Statement stm = conn.createStatement();
        Statement stm1 = conn.createStatement();
        ResultSet rs = stm.executeQuery("SELECT * FROM user WHERE username = '" + u.getUsername() + "'");
        while(rs.next()){
            stm1.executeUpdate("DELETE FROM practice WHERE user_id = " + rs.getInt("id"));
            stm1.executeUpdate("DELETE FROM user WHERE username = '" + u.getUsername() + "'");
        }
        rs.close();
        
        stm.close();
        stm1.close();
        conn.close();
    }
    
    public void updateUser(User u, Scanner scanner) throws ParseException{
        System.out.println("Please enter infomation need to be updated!!");
        System.out.print("Full name: ");
        u.setFullName(scanner.nextLine());
        System.out.print("Password: ");
        u.setPassword(scanner.nextLine());
        System.out.print("Gender: ");
        u.setGender(scanner.nextLine());
        System.out.println("Date of birth(dd/mm/yyyy): ");
        String dob = scanner.nextLine();
        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
        u.setDob(f.parse(dob));
        System.out.print("Country: ");
        u.setCountry(scanner.nextLine());
    }
    
    public void viewList(){
        for(User u : this.getUsers()){
            System.out.println(u);
        }
    }
    
    public void viewList(Connection conn) throws SQLException, ParseException{
        Statement stm = conn.createStatement();
        ResultSet rs = stm.executeQuery("SELECT * FROM user");
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        this.users = new ArrayList<>();
        while(rs.next()){
            User u = new User();
            int mhv = rs.getInt("id");
            u.setFullName(rs.getString("fullname"));
            u.setUsername(rs.getString("username"));
            u.setPassword(rs.getString("password"));
            u.setGender(rs.getString("gender"));
            u.setDob(f.parse(rs.getString("dateofbirth")));
            u.setCountry(rs.getString("country"));
            u.setAccessDate(f.parse(rs.getString("dateofstart")));
            this.users.add(u);
        }
        this.viewList();
        rs.close();
        stm.close();
        conn.close();
    }
    
    public List<User> lookUpByFullName(String fn){
        List<User> r = new ArrayList<>();
        for(User u : this.getUsers()){
            if(u.getFullName().toUpperCase().equals(fn.toUpperCase()))
                r.add(u);
        }
        return r;
    }
    
    public List<User> lookUpByFullName(String fn, Connection conn) throws SQLException, ParseException{
        List<User> r = new ArrayList<>();
        Statement stm = conn.createStatement();
        ResultSet rs = stm.executeQuery("SELECT * FROM user");
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        this.users = new ArrayList<>();
        while(rs.next()){
            User u = new User();
            int mhv = rs.getInt("id");
            u.setFullName(rs.getString("fullname"));
            u.setUsername(rs.getString("username"));
            u.setPassword(rs.getString("password"));
            u.setGender(rs.getString("gender"));
            u.setDob(f.parse(rs.getString("dateofbirth")));
            u.setCountry(rs.getString("country"));
            u.setAccessDate(f.parse(rs.getString("dateofstart")));
            this.users.add(u);
        }
        r = this.lookUpByFullName(fn);
        rs.close();
        stm.close();
        conn.close();
        return r;
    }
    
    public List<User> lookUpByGender(String g){
        List<User> r = new ArrayList<>();
        for(User u : this.getUsers()){
            if(u.getGender().toUpperCase().equals(g.toUpperCase()))
                r.add(u);
        }
        return r;
    }
    
    public List<User> lookUpByGender(String g, Connection conn) throws SQLException, ParseException{
        List<User> r = new ArrayList<>();
        Statement stm = conn.createStatement();
        ResultSet rs = stm.executeQuery("SELECT * FROM user");
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        this.users = new ArrayList<>();
        while(rs.next()){
            User u = new User();
            int mhv = rs.getInt("id");
            u.setFullName(rs.getString("fullname"));
            u.setUsername(rs.getString("username"));
            u.setPassword(rs.getString("password"));
            u.setGender(rs.getString("gender"));
            u.setDob(f.parse(rs.getString("dateofbirth")));
            u.setCountry(rs.getString("country"));
            u.setAccessDate(f.parse(rs.getString("dateofstart")));
            this.users.add(u);
        }
        r = this.lookUpByGender(g);
        rs.close();
        stm.close();
        conn.close();
        return r;
    }
    
    public List<User> lookUpByDOB(String dob) throws ParseException{
        List<User> r = new ArrayList<>();
        SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
        for(User u : this.getUsers()){
            if(u.getDob().equals(f.parse(dob)))
                r.add(u);
        }
        return r;
    }
    
    public List<User> lookUpByDOB(String dob, Connection conn) throws SQLException, ParseException{
        List<User> r = new ArrayList<>();
        Statement stm = conn.createStatement();
        ResultSet rs = stm.executeQuery("SELECT * FROM user");
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        this.users = new ArrayList<>();
        while(rs.next()){
            User u = new User();
            int mhv = rs.getInt("id");
            u.setFullName(rs.getString("fullname"));
            u.setUsername(rs.getString("username"));
            u.setPassword(rs.getString("password"));
            u.setGender(rs.getString("gender"));
            u.setDob(f.parse(rs.getString("dateofbirth")));
            u.setCountry(rs.getString("country"));
            u.setAccessDate(f.parse(rs.getString("dateofstart")));
            this.users.add(u);
        }
        r = this.lookUpByDOB(dob);
        rs.close();
        stm.close();
        conn.close();
        return r;
    }
    
    public List<User> lookUpByCountry(String c){
        List<User> r = new ArrayList<>();
        for(User u : this.getUsers()){
            if(u.getCountry().toUpperCase().equals(c.toUpperCase()))
                r.add(u);
        }
        return r;
    }
    
    public List<User> lookUpByCountry(String c, Connection conn) throws SQLException, ParseException{
        List<User> r = new ArrayList<>();
        Statement stm = conn.createStatement();
        ResultSet rs = stm.executeQuery("SELECT * FROM user");
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        this.users = new ArrayList<>();
        while(rs.next()){
            User u = new User();
            int mhv = rs.getInt("id");
            u.setFullName(rs.getString("fullname"));
            u.setUsername(rs.getString("username"));
            u.setPassword(rs.getString("password"));
            u.setGender(rs.getString("gender"));
            u.setDob(f.parse(rs.getString("dateofbirth")));
            u.setCountry(rs.getString("country"));
            u.setAccessDate(f.parse(rs.getString("dateofstart")));
            this.users.add(u);
        }
        r = this.lookUpByCountry(c);
        rs.close();
        stm.close();
        conn.close();
        return r;
    }

    /**
     * @return the users
     */
    public List<User> getUsers() {
        return users;
    }

    /**
     * @param users the users to set
     */
    public void setUsers(List<User> users) {
        this.users = users;
    }
    
    
}

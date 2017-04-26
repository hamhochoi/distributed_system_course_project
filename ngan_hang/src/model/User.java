/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 *
 * @author Hung Nguyen Manh
 */
public class User {

    long id;
    String fullName;
    Date birthDay;
    String address;

    public User(long id, String fullName, Date birthDay, String address) {
        this.id = id;
        this.fullName = fullName;
        this.birthDay = birthDay;
        this.address = address;
    }

    public long getID(){
        return id;
    }
    
    public String getName(){
        return fullName;
    }
    
    public static long getAccountID(long userID) throws ClassNotFoundException, SQLException{
        Connection conn = connection.Connector.getConnection();
        String sql = "select so_TK from chuTK_TK where so_CMTND = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setLong(1, userID);
        ResultSet rs = ps.executeQuery();
        if(rs.next())
            return rs.getLong(1);
        return -1;
    }
    
    public static boolean checkExist(long userID) throws ClassNotFoundException, SQLException{
        Connection conn = connection.Connector.getConnection();
        String sql = "select * from chuTK_TK where so_CMTND = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setLong(1, userID);
        ResultSet rs = ps.executeQuery();
        return rs.next();
    }
    
    public static void setPassword(long id, String pass) throws ClassNotFoundException, SQLException {
        Connection conn = connection.Connector.getConnection();
        String sql = "update chu_TK set password = ? where so_CMTND = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, pass);
        ps.setLong(2, id);
        ps.executeUpdate();
    }

    public static boolean checkPassword(long id, String pass) throws ClassNotFoundException, SQLException {
        Connection conn = connection.Connector.getConnection();
        String sql = "select * from chu_TK where so_CMTND = ? and password";
        if (pass != null) {
            sql += "=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, id);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
        else{
            sql += " is null";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }
    
    public static boolean checkPassword(String rootName, String pass) throws ClassNotFoundException, SQLException{
        if(!rootName.equals("root"))
            return false;
        Connection conn = connection.Connector.getConnection();
        String sql = "select * from root where root = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, pass);
        ResultSet rs = ps.executeQuery();
        return rs.next();
    }
    
    public static void setPassword(String rootName, String pass) throws ClassNotFoundException, SQLException{
        if(!rootName.equals("root")){
            return;
        }
        Connection conn = connection.Connector.getConnection();
        String sql = "update root set root=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, pass);
        ps.executeUpdate();
    }
}

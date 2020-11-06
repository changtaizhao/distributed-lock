package com.changtai.mysql;

import com.changtai.DistributedLock;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * mysql 行锁
 *
 * @author zhaoct
 * @date 2020-11-05 16:42
 */
public class Lock5 implements DistributedLock {

    private Connection conn = null;

    public Lock5(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager
                    .getConnection("jdbc:mysql://192.168.88.53:3306/my_test?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC","root","kalamodo");
            conn.setAutoCommit(false);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean lock() {
        return false;
    }

    @Override
    public boolean tryLock() {
        boolean isSuccess = false;
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("select id from mylock where id = ? for update;");
            preparedStatement.setInt(1, 1);
            isSuccess = preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(isSuccess){
            return true;
        }
        return false;
    }

    @Override
    public boolean releaseLock() {
        try {
            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

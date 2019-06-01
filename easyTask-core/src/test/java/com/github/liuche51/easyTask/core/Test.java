package com.github.liuche51.easyTask.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.*;

public class Test {
    private static Logger log = LoggerFactory.getLogger(Test.class);
    @org.junit.Test
    public void test(){
        DbInit.init();
        System.out.print("start time:"+ZonedDateTime.now().toLocalTime());
     for(int i=0;i<10000;i++){
         try {
             SqliteHelper.executeUpdateForSync("insert into schedule(id) values('"+ UUID.randomUUID()+"')");
         } catch (SQLException e) {
             e.printStackTrace();
         } catch (ClassNotFoundException e) {
             e.printStackTrace();
         }
     }
        System.out.print("end time:"+ZonedDateTime.now().toLocalTime());
     //use 167s、177s
    }
    @org.junit.Test
    public void test1(){
        DbInit.init();
        System.out.print("start time:"+ZonedDateTime.now().toLocalTime());
        List<String> sqls=new ArrayList<>();
        for(int i=0;i<10000;i++){
                sqls.add("insert into schedule(id) values('"+ UUID.randomUUID()+"')");
        }
      /*  try {
            SqliteHelper.executeUpdateForSync(sqls);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }*/
        System.out.print("end time:"+ZonedDateTime.now().toLocalTime());
        //use 126s、135s
    }
    @org.junit.Test
    public void test2(){
        DbInit.init();
        System.out.print("start time:"+ZonedDateTime.now().toLocalTime());
        StringBuilder sqls=new StringBuilder();
        for(int i=0;i<10000;i++){
            sqls.append("insert into schedule(id) values('"+ UUID.randomUUID()+"');");
        }
        try {
            SqliteHelper.executeUpdateForSync(sqls.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.out.print("end time:"+ZonedDateTime.now().toLocalTime());
        //use 128s、136s
    }
}

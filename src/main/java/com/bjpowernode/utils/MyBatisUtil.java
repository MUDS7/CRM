package com.bjpowernode.crm.utils;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;


public class MyBatisUtil {
    private static SqlSessionFactory factory=null;
    static {
        String config="MyBatis.xml";//和config文件名一样
        try{
            InputStream in=Resources.getResourceAsStream(config);
            //创建sqlSessionFactory对象，使用SqlSessionFactoryBuilder
            factory=new SqlSessionFactoryBuilder().build(in);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public SqlSession getSession(){
        SqlSession sqlSession=null;
        if(factory!=null){
            sqlSession=factory.openSession();//非自动提交事务
        }
        return sqlSession;
    }
}

package service;

import model.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.InputStream;

public class MybatisTest {

    /**
     * add user
     * @throws Exception
     */
    @Test
    public void addUserTest() throws Exception {

        //加载核心配置文件
        String resource = "mybatisxml/mybatis-config.xml";
        InputStream in = Resources.getResourceAsStream(resource);
        //创建SqlSessionFactory
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(in);
        //创建SqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        //执行Sql语句
        sqlSession.insert("test.addUser",
                new User.Builder()
                .id(5)
                .name("张三")
                .age(67)
                .build()
        );
        sqlSession.commit();

    }

    /**
     * find user by id test
     * @throws Exception
     */
    @Test
    public void findUserByIdTest() throws Exception {

        //加载核心配置文件
        String resource = "mybatisxml/mybatis-config.xml";
        InputStream in = Resources.getResourceAsStream(resource);
        //创建SqlSessionFactory
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(in);
        //创建SqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        //执行Sql语句
        User user = sqlSession.selectOne("test.findUserById", 1);
        System.out.println(user.getName());

    }

    /**
     * update user by id
     * @throws Exception
     */
    @Test
    public void updateUserByIdTest() throws Exception {

        //加载核心配置文件
        String reource = "mybatisxml/mybatis-config.xml";
        InputStream in = Resources.getResourceAsStream(reource);
        //创建sqlSessionFactory
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(in);
        //创建SqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        //执行Sql语句
        sqlSession.update("test.updateUserById" ,
                new User.Builder()
                .id(5)
                .name("李四")
                .age(8)
                .build()
        );
        sqlSession.commit();

    }

    @Test
    public void deleteUserByID() throws Exception {

        //加载核心配置文件
        String reource = "mybatisxml/mybatis-config.xml";
        InputStream in = Resources.getResourceAsStream(reource);
        //创建sqlSessionFactory
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(in);
        //创建SqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        //执行sql语句
        sqlSession.delete("test.deleteUserByID" , 5);
        sqlSession.commit();

    }

}

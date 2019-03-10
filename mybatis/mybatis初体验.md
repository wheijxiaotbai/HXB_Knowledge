# Mybatis初体验

### 内容介绍

本文通过一个demo演示Mybatis是如何使用的，不涉及与spring集成相关。本文使用mysql进行演示

本文demo地址：

```
https://github.com/wheijxiaotbai/HXB_Knowledge.git
分支：hxb_demo_mybatis_01
```

### 环境准备

* 依赖包的导入

  在demo中使用的是gradle ，直接在gradle配置文件中加入以下依赖即可

  ```gradle
  // https://mvnrepository.com/artifact/org.mybatis/mybatis
  compile group: 'org.mybatis', name: 'mybatis', version: '3.5.0'
  
  // https://mvnrepository.com/artifact/mysql/mysql-connector-java
  compile group: 'mysql', name: 'mysql-connector-java', version: '8.0.15'
  ```

* mysql环境的准备，表的创建

  在具有docker的linux环境下执行

  ```shell
  docker run -dit --name demo_mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=123456 mysql
  ```

  这样，就能快速启动一个mysql容器，账户为root，密码为123456，当然你可以自己修改MYSQL_ROOT_PASSWORD=123456参数以修改密码

  执行下面sql语句创建User表

  ```sql
  create table user (
    	`id` int,
  	`name` VARCHAR(32),
  	`age` int,
  	PRIMARY KEY (`id`)
  ) 
  ```

##### 原生方式

> * 配置mybatis-config.xml
> * 配置数据模型对应的sql.xml（比如User模型对应的User.xml）
> * 创建model类（User）

mybatis-config.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC"></transactionManager>
            <dataSource type="POOLED">
                //数据库驱动
                <property name="driver" value="com.mysql.jdbc.Driver"/>
                //配置数据库参数
                <property name="url" value="jdbc:mysql://192.168.31.150:3306/hxb_demo_mynatis_01" />
                <property name="username" value="root"/>
                <property name="password" value="123456"/>
            </dataSource>
        </environment>
    </environments>
    <mappers>
        //User.xml文件
        <mapper resource="mybatisxml/sqlmap/User.xml"/>
    </mappers>
</configuration>
```

User.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<!-- 写Sql语句   -->
//这里将命名空间设置为test
<mapper namespace="test">
    <!-- find user by id -->
    //parameterType为传入参数类型，resultType为输出数据类型，#{value}即为传入的参数
    <select id="findUserById" parameterType="int" resultType="model.User">
      select * from user where id = #{value}
   </select>

    <!-- add user -->
    <insert id="addUser" parameterType="model.User">
        insert into user (id , name , age)
        values (#{id},#{name},#{age})
    </insert>

    <!-- delete user by id -->
    <delete id="deleteUserByID" parameterType="int">
        delete from user where id = #{value}
    </delete>

    <!-- update user by id -->
    <update id="updateUserById" parameterType="model.User">
        update user
        set name = #{name} , age = #{age}
        where id = #{id}
    </update>
</mapper>
```

User，user类和你刚刚创建的表一致

```java
public class User {

    private int id;
    private String name;
    private int age;

    public User() {}

    private User(Builder builder) {
        setId(builder.id);
        setName(builder.name);
        setAge(builder.age);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public static final class Builder {
        private int id;
        private String name;
        private int age;

        public Builder() {
        }

        public Builder id(int val) {
            id = val;
            return this;
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder age(int val) {
            age = val;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
```

接下来进行测试 MybatisTest

```java
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
        //通过test.addUser找到user.xml中对于的sql语句，test为user.xml中的命名空间
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
```



##### mapper动态代理方式

> - 配置mybatis-config.xml
> - 配置数据模型对应的sql.xml（比如User模型对应的User.xml）
> - 创建model类（User）
> - mapper映射接口

mapper动态代理方式的mybatis-config.xml，User文件与上一种一致user.xml略有区别

User.xml

```java
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<!-- 写Sql语句   -->
//这里的命名空间改为user的mapper映射接口
<mapper namespace="mapper.UserMapper">
    <!-- find user by id -->
    <select id="findUserById" parameterType="int" resultType="model.User">
      select * from user where id = #{value}
   </select>

    <!-- add user -->
    <insert id="addUser" parameterType="model.User">
        insert into user (id , name , age)
        values (#{id},#{name},#{age})
    </insert>

    <!-- delete user by id -->
    <delete id="deleteUserByID" parameterType="int">
        delete from user where id = #{value}
    </delete>

    <!-- update user by id -->
    <update id="updateUserById" parameterType="model.User">
        update user
        set name = #{name} , age = #{age}
        where id = #{id}
    </update>

</mapper>
```

UserMapper

```java
public interface UserMapper {

    //遵循四个原则
	//接口 方法名  == User.xml 中 id 名
	//返回值类型  与  Mapper.xml文件中返回值类型要一致
	//方法的入参类型 与Mapper.xml中入参的类型要一致
	//命名空间 绑定此接口
    public User findUserById(int id);

    public void addUser(User user);

    public void updateUserById(User user);

    public void deleteUserByID(int id);

}
```

接下来进行测试

MybatisMapperTest

```java
import mapper.UserMapper;
import model.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.InputStream;

public class MybatisMapperTest {

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
        //sqlSession生成实现类
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        //调用方法
        userMapper.addUser(
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
        //sqlSession生成实现类
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        //调用方法
        User user = userMapper.findUserById(1);
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
        //sqlSession生成实现类
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        //调用方法
        userMapper.updateUserById(
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
        //sqlSession生成实现类
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        //调用方法
        userMapper.deleteUserByID(5);
        sqlSession.commit();

    }

}
```



### 可能会出现的问题

* can not found xxx.xml

  这种一般是你的xml文件放的位置不对，或者你写的路径不正确

* 在mybatis-config.xml中<property name="driver" value="com.mysql.jdbc.Driver"/>导入失败或者错误，会导致在test的时候空指针异常，应为没有连接上数据库
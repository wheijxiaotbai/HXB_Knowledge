# spring集成Mybatis

### 内容说明

本文介绍了spring如何集成mybatis，使用的是mysql数据库，依赖工具为gradle

> * 环境准备
> * 集成流程
> * 测试

本文demo地址：https://github.com/wheijxiaotbai/HXB_Knowledge.git

分支: hxb_demo_springMybatis



### 环境准备

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



### 集成流程

##### 代码目录结构

![1551744767252](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1551744767252.png)

* ##### 添加依赖

  * spring 的相关包
  * spring与mybatis集成的相关包
  * mysql驱动包
  * mybatis包
  * commons-dbcp
  * junit

  ```groovy
  testCompile group: 'junit', name: 'junit', version: '4.12'
  // https://mvnrepository.com/artifact/org.springframework/spring-context
  compile group: 'org.springframework', name: 'spring-context', version: '5.1.5.RELEASE'
  // https://mvnrepository.com/artifact/org.springframework/spring-core
  compile group: 'org.springframework', name: 'spring-core', version: '5.1.5.RELEASE'
  // https://mvnrepository.com/artifact/org.springframework/spring-beans
  compile group: 'org.springframework', name: 'spring-beans', version: '5.1.5.RELEASE'
  // https://mvnrepository.com/artifact/org.mybatis/mybatis-spring
  compile group: 'org.mybatis', name: 'mybatis-spring', version: '2.0.0'
  // https://mvnrepository.com/artifact/mysql/mysql-connector-java
  compile group: 'mysql', name: 'mysql-connector-java', version: '8.0.15'
  // https://mvnrepository.com/artifact/org.springframework/spring-aop
  compile group: 'org.springframework', name: 'spring-aop', version: '5.1.5.RELEASE'
  // https://mvnrepository.com/artifact/org.springframework/spring-jdbc
  compile group: 'org.springframework', name: 'spring-jdbc', version: '5.1.5.RELEASE'
  // https://mvnrepository.com/artifact/commons-dbcp/commons-dbcp
  compile group: 'commons-dbcp', name: 'commons-dbcp', version: '1.4'
  // https://mvnrepository.com/artifact/org.mybatis/mybatis
  compile group: 'org.mybatis', name: 'mybatis', version: '3.5.0'
  ```

* ##### 创建User

  该类和上文准备操作中在数据库创建的表模型保持一致

  ```java
  package user.model;
  
  import org.springframework.context.annotation.Bean;
  
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

* ##### User.xml

  该类是创建User对应的sqlxml，和mybatis没有与spring集成时是一样的

  * <mapper namespace="user.mapper.UserMapper">在这里namespace为User所对应的Mapper接口，将该xml和mapper进行对应

  ```xml
  <?xml version="1.0" encoding="UTF-8" ?>
  <!DOCTYPE mapper
          PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
          "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
  <mapper namespace="user.mapper.UserMapper">
      <!-- find user by id -->
      <select id="findUserById" parameterType="int" resultType="user.model.User">
        select * from user where id = #{value}
     </select>
  
      <!-- add user -->
      <insert id="addUser" parameterType="user.model.User">
          insert into user (id , name , age)
          values (#{id},#{name},#{age})
      </insert>
  
      <!-- delete user by id -->
      <delete id="deleteUserByID" parameterType="int">
          delete from user where id = #{value}
      </delete>
  
      <!-- update user by id -->
      <update id="updateUserById" parameterType="user.model.User">
          update user
          set name = #{name} , age = #{age}
          where id = #{id}
      </update>
  </mapper>
  ```

* ##### UserMapper

  该类为何User相应xml对应的接口类

  * 接口中的方法名必须和xml中的id一致

  * 接口中的方法的传入/返回参数必须与xml中的保持一致

  ```java
  package user.mapper;
  
  import user.model.User;
  
  public interface UserMapper {
  
      public User findUserById(int id);
  
      public void addUser(User user);
  
      public void updateUserById(User user);
  
      public void deleteUserByID(int id);
  
  }
  ```

* ##### db.properties

  该文件出存放了连接数据库的相关信息

  ```java
  jdbc.driver=com.mysql.jdbc.Driver
  jdbc.url=jdbc:mysql://192.168.31.150:3306/hxb_demo_mynatis_01
  jdbc.username=root
  jdbc.password=123456
  ```

* ##### applicationContext.xml

  该类为spring的配置类，在这里对mybatis进行配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:context="http://www.springframework.org/schema/context" xmlns:p="http://www.springframework.org/schema/p"
       xmlns:aop="http://www.springframework.org/schema/aop" xmlns:tx="http://www.springframework.org/schema/tx"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-4.0.xsd
   http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-4.0.xsd
   http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-4.0.xsd http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-4.0.xsd
   http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util-4.0.xsd">

	<!-- 导入数据库连接数据文件 -->
    <context:property-placeholder location="classpath:db.properties"/>

    <!-- 数据库连接池 -->
    <bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource" destroy-method="close">
        <property name="driverClassName" value="${jdbc.driver}" />
        <property name="url" value="${jdbc.url}" />
        <property name="username" value="${jdbc.username}" />
        <property name="password" value="${jdbc.password}" />
        <property name="maxActive" value="10" />
        <property name="maxIdle" value="5" />
    </bean>

    <!-- Mybatis的工厂 -->
    <bean id="sqlSessionFactoryBean" class="org.mybatis.spring.SqlSessionFactoryBean">
        <property name="dataSource" ref="dataSource"/>
        <!-- 核心配置文件的位置 -->
        <property name="configLocation" value="mybatisxml/mybatis-config.xml"/>
    </bean>

    <!--&lt;!&ndash; Dao原始Dao &ndash;&gt;-->
    <!--<bean id="userDa o" class="com.itheima.mybatis.dao.UserDaoImpl">-->
        <!--<property name="sqlSessionFactory" ref="sqlSessionFactoryBean"/>-->
    <!--</bean>-->
    <!--&lt;!&ndash; Mapper动态代理开发 &ndash;&gt;-->
    <!--<bean id="userMapper" class="org.mybatis.spring.mapper.MapperFactoryBean">-->
        <!--<property name="sqlSessionFactory" ref="sqlSessionFactoryBean"/>-->
        <!--<property name="mapperInterface" value="user.mapper.UserMapper"/>-->
    <!--</bean>-->

    <!-- Mapper动态代理开发扫描 这之前是通过自己手动获得实现类的，现在通过spring帮我们实现-->
    <bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
        <!-- 基本包 -->
        <property name="basePackage" value="user.mapper"/>
    </bean>

</beans>
```

### 测试

编写测试类

##### springMybatisTest

```java
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import user.mapper.UserMapper;
import user.model.User;

public class springMybatisTest {

    @Test
    public void testMapper() throws Exception {

        ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
        UserMapper mapper = ac.getBean(UserMapper.class);
        User user = mapper.findUserById(1);
        System.out.print(user.getName());
    }

}
```
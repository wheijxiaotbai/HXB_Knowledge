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

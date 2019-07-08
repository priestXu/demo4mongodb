package com.example.demo;

import com.example.demo.db.data.User;
import com.example.demo.db.data.User1;
import com.example.demo.db.data.User2;
import com.example.demo.db.service.SequenceGeneratorService;
import com.example.demo.db.service.TestService;
import com.example.demo.db.service.user.mongo.UserRepository;
import com.example.demo.db.service.user.mongo.MongoUserService;
import com.example.demo.helper.ReflectionUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import javassist.CannotCompileException;
import javassist.NotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {
    @Autowired
    private MongoUserService userDao;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestService testService;
    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;

    private static final ObjectMapper OBJECTMAPPER = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    static {
        OBJECTMAPPER.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        OBJECTMAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        OBJECTMAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        OBJECTMAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
    }

    @Test
    public void contextLoads() {
    }

    @Test
    public void saveDemoTest() {

        User1 demoEntity1 = new User1();
        demoEntity1.setId(1L);
        demoEntity1.setTitle("Spring Boot");
        demoEntity1.setDescription("关注公众号，搜云库，专注于开发技术的研究与知识分享");
        demoEntity1.setBy("souyunku");
        demoEntity1.setUrl("http://www.souyunku.com");
        userDao.save1(demoEntity1);

        User2 demoEntity2 = new User2();
        demoEntity2.setId(2L);
        demoEntity2.setTitle("Spring Boot 中使用 MongoDB");
        demoEntity2.setDescription("关注公众号，搜云库，专注于开发技术的研究与知识分享");
        demoEntity2.setBy("souyunku");
        demoEntity2.setUrl("http://www.souyunku.com");
        demoEntity2.setHeadImg("http://www.1234567.com/239.png");
        userDao.save2(demoEntity2);
    }

    @Test
    public void findDemoByIdTest() throws JsonProcessingException {
        Map map = userDao.findById(1L);
        System.out.println("map1->" + OBJECTMAPPER.writeValueAsString(map));
        map = userDao.findById(2L);
        System.out.println("map2->" + OBJECTMAPPER.writeValueAsString(map));

        User1 user1 = userDao.findUser1ById(1L);
        System.out.println("user1->" + OBJECTMAPPER.writeValueAsString(user1));
        User2 user2 = userDao.findUser2ById(2L);
        System.out.println("user2->" + OBJECTMAPPER.writeValueAsString(user2));
    }

    @Test
    public void testChangeUser1() throws IOException, NotFoundException, IllegalAccessException, InstantiationException, ClassNotFoundException, CannotCompileException {
        this.saveDemoTest();
        // 获取数据，并动态创建新实体类
        Map<String, Object> map = userDao.findById(2L);
        Object user1 = new ReflectionUtils<>(User1.class).builder(map).writerFileds(null);
        System.out.println("user1->" + OBJECTMAPPER.writeValueAsString(user1));

//        // 处理新的实体类，修改值，插入新记录
//        map.remove("_id");
//        map.put("title", "new object");
//        FieldUtils.writeField(user1, "id", null, true);
//        Object user2 = new ReflectionUtils<>(User1.class).builder(map).writerFileds(null);
//        FieldUtils.writeField(user2, "id", sequenceGeneratorService.generateSequence(User1.SEQUENCE_NAME), true);
//        userDao.save(user2);
//        System.out.println("user2->" + OBJECTMAPPER.writeValueAsString(user2));

        // 通过原始实体类，获取添加新属性的实体类
        User1 user3 = userDao.findUser1ById(1L);
        System.out.println("user1_new ->" + OBJECTMAPPER.writeValueAsString(user3));
        map = new HashMap<>();
        map.put("headImg", "http://192.168.1.123/index.html");
        Object user4 = new ReflectionUtils<>(user3).getChangedObject(null, map);
        userDao.save(user4);
        System.out.println("user1_changed ->" + OBJECTMAPPER.writeValueAsString(user4));


        // 在已有新实体类的基础上再次添加新的属性
        map = userDao.findById(2L);
        map.put("headImg", "http://new_id/index.html");
        map.put("new_id", 12345);
        Object user5 = new ReflectionUtils<>(User1.class).builder(map).writerFileds(null);
        userDao.save(user5);
        System.out.println("user5->" + OBJECTMAPPER.writeValueAsString(user5));

        for (int i = 0; i < 10; i++) {
            // 在已有新实体类的基础上再次添加新的属性 10次
            map = userDao.findById(2L);
            map.put("headImg", "http://" + i + "/index.html");
            map.put("new_id" + i, i * i);
            Object user = new ReflectionUtils<>(User1.class).builder(map).writerFileds(null);
            userDao.save(user);
            System.out.println("user6[" + i + "]->" + OBJECTMAPPER.writeValueAsString(user));
        }
    }

    @Test
    public void saveUserByRepository() throws JsonProcessingException, InvocationTargetException, IllegalAccessException {
        User demoEntity1 = new User();
//        demoEntity1.setId(sequenceGeneratorService.generateSequence(User.SEQUENCE_NAME));
        demoEntity1.setId(1L);
        demoEntity1.setTitle("Spring Boot");
        demoEntity1.setDescription("关注公众号，搜云库，专注于开发技术的研究与知识分享");
        demoEntity1.setBy("souyunku");
        demoEntity1.setUrl("http://www.souyunku.com");
        userRepository.save(demoEntity1);

        User demoEntity2 = new User();
//        demoEntity2.setId(sequenceGeneratorService.generateSequence(User.SEQUENCE_NAME));
        demoEntity2.setId(2L);
        demoEntity2.setTitle("Spring Boot 中使用 MongoDB");
        demoEntity2.setDescription("关注公众号，搜云库，专注于开发技术的研究与知识分享");
        demoEntity2.setBy("souyunku");
        demoEntity2.setUrl("http://www.souyunku.com");
        demoEntity2.addDetails("headImg", "http://www.1234567.com/239.png")
                .addDetails("test2", 123)
                .addDetails("name", "test2")
                .addDetails("list", Arrays.asList("1", "2", "3"));
        userRepository.save(demoEntity2);

        User user1 = userRepository.findOne(1L);
        System.out.println("user1->" + OBJECTMAPPER.writeValueAsString(user1));

        User user2 = userRepository.findOne(2L);
        System.out.println("user2->" + OBJECTMAPPER.writeValueAsString(user2));

        List<User> users = userRepository.findByDynamicField("details.name", "test2");
        System.out.println("users->" + OBJECTMAPPER.writeValueAsString(users));

        users = userDao.findUserByName("test2");
        System.out.println("users->" + OBJECTMAPPER.writeValueAsString(users));
    }

    @Test
    public void TestThrowableTransactional() {
        // 在用一个事务方法中，如果mysql先出错回滚了，MongoDB执行的代码就不会执行了
        testService.Error2SaveUser1();
    }

    @Test
    public void TestThrowableTransactional2() {
        // 该方法会造成MongoDB脏数据
        testService.Error2SaveUser2();
    }
}

package com.example.application.namedParameterJdbcTemplate;

import com.example.application.springboot.jdbcModel.Employee;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wangjing
 * Created on 2018-11-01
 *
 * 参考URL:
 *  https://blog.csdn.net/u011179993/article/details/74791304
 *  https://segmentfault.com/a/1190000010907688
 *  https://my.oschina.net/happyBKs/blog/497798
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class NamedParameterJdbcTemplateTest {


    @Autowired
    private JdbcTemplate jdbcTemplate;


    /**
     * 优点:如果有多个参数，不用去纠结参数的位置顺序，直接对应参数名称，便于维护
     */
    @Test
    public void testNamedParameterJdbcTemplate1(){
       String sql = "insert into employee(id, last_name, email) values(:id, :lastName, :email)";
       HashMap<String, Object> paramMap = new HashMap<>();
       paramMap.put("id", 1);
       paramMap.put("email", "111@qq.com");
       paramMap.put("lastName", "周杰伦");
       NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
       namedParameterJdbcTemplate.update(sql, paramMap);
    }

    /**
     * sql语句中的具名参数与类的属性名一致
     * 使用接口SqlParameterSource的实现类BeanPropertySqlParameterSource作为参数
     *
     */
    @Test
    public void testNamedParameterJdbcTemplate2(){
        String sql = "insert into employee(id, last_name, email) values(:id, :lastName, :email)";
        Employee employee = new Employee();
        employee.setLastName("lastname1");
        employee.setEmail("222@qq.com");
        employee.setId(3);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        BeanPropertySqlParameterSource sqlParameterSource = new BeanPropertySqlParameterSource(employee);
        namedParameterJdbcTemplate.update(sql, sqlParameterSource);
    }


    /**
     * 通过新增的类KeyHolder，可以获得主键
     */
    @Test
    public void testNamedParameterJdbcTemplate3(){
        String sql = "insert into employee(last_name, email) values(:lastName, :email)";
        Employee employee = new Employee();
        employee.setLastName("lastname2");
        employee.setEmail("333@qq.com");
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        BeanPropertySqlParameterSource sqlParameterSource = new BeanPropertySqlParameterSource(employee);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, sqlParameterSource, keyHolder);
        int k = keyHolder.getKey().intValue();
        System.out.println(k);

    }

    /**
     * 获取单行单列的值
     *
     * API:
     *  public < T > T queryForObject(String sql, Map<String, ?> paramMap, Class<T> requiredType)
     *  public < T > T queryForObject(String sql, SqlParameterSource paramSource, Class<T> requiredType)
     *
     */
    @Test
    public void testNamedParameterJdbcTemplate4(){
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        Integer count = namedParameterJdbcTemplate.queryForObject("select count(*) from employee", new HashMap<>(), Integer.class);
        String lastName = namedParameterJdbcTemplate.queryForObject("select last_name from employee where id = 4 limit 1", EmptySqlParameterSource.INSTANCE, String.class);
        System.out.println("count:" + count + ", lastName:" + lastName);
    }

    /**
     * 获取（多行）单列数据
     *
     * API:
     *  public < T> List< T> queryForList(String sql, Map<String, ?> paramMap, Class< T > elementType)
     *  public < T> List< T> queryForList(String sql, SqlParameterSource paramSource, Class< T> elementType)
     *
     */
    @Test
    public void testNamedParameterJdbcTemplate5(){
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        List<String> emailList = namedParameterJdbcTemplate.queryForList("select email from employee", EmptySqlParameterSource.INSTANCE, String.class);
        System.out.println(emailList);
    }

    /**
     *
     * 返回单行数据
     * API:
     *  public < T> T queryForObject(String sql, Map< String, ?> paramMap, RowMapper< T>rowMapper)
     *  public < T> T queryForObject(String sql, SqlParameterSource paramSource, RowMapper< T> rowMapper)
     *
     *  BeanPropertyRowMapper会把下划线转化为驼峰式，结果对象会比实际返回的字段会少，如果model的属性采用的不是驼峰式
     *  使用SingleColumnRowMapper返回单行单列的数据
     */
    @Test
    public void testNamedParameterJdbcTemplate6(){
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        Employee employee = namedParameterJdbcTemplate.queryForObject("select * from employee where id = 2", new HashMap<>(), new BeanPropertyRowMapper<Employee>(Employee.class));
        System.out.println(employee);

        String name = namedParameterJdbcTemplate.queryForObject("select last_name from employee where id = 3", new HashMap<>(), new SingleColumnRowMapper<String>(String.class));
        System.out.println(name);
    }

    /**
     * 返回Map形式的单行数据
     * API:
     *  public Map< String, Object> queryForMap(String sql, Map< String, ?> paramMap)
     *  public Map< String, Object> queryForMap(String sql, SqlParameterSource paramSource)
     *
     *  不建议使用，感觉返回的结果很奇怪(key:数据库里面对应的属性值， value:查询出来的值)
     */
    @Test
    public void testNamedParameterJdbcTemplate7(){
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        Map<String, Object> map = namedParameterJdbcTemplate.queryForMap("select * from employee limit 1", new HashMap<>());
        System.out.println("key: "+ map.keySet() + "value: " + map.values());
    }

    /**
     * 返回多行数据
     * API:
     *  public < T> List< T> query(String sql, Map< String, ?> paramMap, RowMapper< T> rowMapper)
     *  public < T> List< T> query(String sql, SqlParameterSource paramSource, RowMapper< T> rowMapper)
     *  public < T> List< T> query(String sql, RowMapper< T> rowMapper)
     *
     */
    @Test
    public void testNamedParameterJdbcTemplate8(){
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        List<Employee> employeeList = namedParameterJdbcTemplate.query("select * from employee", new BeanPropertyRowMapper<>(Employee.class));
        System.out.println(employeeList);
    }


}

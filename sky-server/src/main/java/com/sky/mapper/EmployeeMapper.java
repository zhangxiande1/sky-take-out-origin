package com.sky.mapper;

import com.sky.entity.Employee;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username =  #{username}")
    Employee getByUsername(String username);

    /**
     * 新增员工
     * @param employee
     */
    @Insert("insert into employee(name, username, password, phone, sex, id_number, create_time, update_time, create_user, update_user,status)" +
            " values " +
            "(#{name},#{username},#{password},#{phone},#{sex},#{idNumber},#{createTime},#{updateTime},#{createUser},#{updateUser},#{status})")
    void insert(Employee employee);

    /**
     * 获取员工总数
     * @return
     */
    @Select("select COUNT(*) FROM employee")
    Long getCounts();

    /**
     * 获取员工分页表
     */
    @Select("select * from employee limit #{start} ,#{pageSize}")
    List<Employee> list(Integer start, Integer pageSize);


    @Select("select * from employee where username LIKE CONCAT('%', #{username}, '%')")
    Employee getByUsernameLike(String username);


    void update(Employee employee);
}

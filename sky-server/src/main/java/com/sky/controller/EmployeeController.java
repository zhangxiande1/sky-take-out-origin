package com.sky.controller;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理1111
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags="员工相关接口")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("员工登陆接口")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        //返回的VO类型的对象主要是给前端使用的
        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation("员工退出接口")
    public Result<String> logout() {
        return Result.success();
    }

    @PostMapping()
    @ApiOperation("员工新增接口")
    public Result save(@RequestBody  EmployeeDTO employeeDTO){
        employeeService.save(employeeDTO);

        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("员工分页查询接口")
    public Result<PageResult> employeePageQuery(EmployeePageQueryDTO employeePageQueryDTO){
        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);

        return Result.success(pageResult);
    }


    @PostMapping("/status/{status}")
    @ApiOperation("员工启用禁用接口")
    public Result startOrStop(@PathVariable Integer status,Long id){
       employeeService.startOrStop(status,id);

        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询员工接口")
    public Result<Employee> getById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);

        return Result.success(employee);
    }


    @PutMapping("")
    @ApiOperation("员工信息编辑")
    public Result updateEmployee(@RequestBody Employee employee){
        employeeService.updaEmployee(employee);

        return Result.success();
    }



}

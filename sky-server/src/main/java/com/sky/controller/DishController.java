package com.sky.controller;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.mapper.DishFlavorMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/dish")
@Api("菜品相关接口")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;



    @PostMapping
    @ApiOperation("菜品新增")
    public Result save(@RequestBody DishDTO dishDTO){
        dishService.save(dishDTO);

        return Result.success();
    }
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> dishPageQuery( DishPageQueryDTO dishPageQueryDTO){
        PageResult pageResult =  dishService.dishPageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }
}

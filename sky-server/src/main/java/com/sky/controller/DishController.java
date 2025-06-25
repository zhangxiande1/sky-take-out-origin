package com.sky.controller;


import com.sky.dto.DishDTO;
import com.sky.mapper.DishFlavorMapper;
import com.sky.result.Result;
import com.sky.service.DishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/dish")
@Api("菜品相关接口")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @PostMapping
    @ApiOperation("菜品新增")
    public Result save(@RequestBody DishDTO dishDTO){
        dishService.save(dishDTO);

        return Result.success();
    }
}

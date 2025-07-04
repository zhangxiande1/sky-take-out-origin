package com.sky.controller;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.mapper.DishFlavorMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/dish")
@Api("菜品相关接口")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 清理redis缓存数据
     * @param pattern
     */
    private void cleanCache(String pattern){
        //获取对应的keys，并删除
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }



    @PostMapping
    @ApiOperation("菜品新增")
    public Result save(@RequestBody DishDTO dishDTO){
        dishService.save(dishDTO);

        //清理缓存数据
        String key = "dish_" + dishDTO.getCategoryId();
        cleanCache(key);
        return Result.success();
    }
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> dishPageQuery( DishPageQueryDTO dishPageQueryDTO){
        PageResult pageResult =  dishService.dishPageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    @ApiOperation("菜品删除")
    public Result dishDelete(@RequestParam List<Long> ids){

        dishService.deleteBatch(ids);
        //将所有的菜品缓存数据清理掉，所有以dish_开头的key
        cleanCache("dish_*");
        return Result.success();
    }

    /**
     * 拿到菜品修改前的初始信息
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id){

        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    @PutMapping()
    @ApiOperation("根据id修改菜品")
    public Result update(@RequestBody DishDTO dishDTO){

        dishService.updateDishWithFlavors(dishDTO);

        //将所有的菜品缓存数据清理掉，所有以dish_开头的key
        cleanCache("dish_*");
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> list(@RequestParam Long categoryId){
        List<Dish> dishList = dishService.list(categoryId);

        return Result.success(dishList);
    }

    /**
     * 菜品起售停售
     *
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("菜品起售停售")
    public Result<String> startOrStop(@PathVariable Integer status, Long id) {
        dishService.startOrStop(status, id);

        //将所有的菜品缓存数据清理掉，所有以dish_开头的key
        cleanCache("dish_*");

        return Result.success();
    }
}

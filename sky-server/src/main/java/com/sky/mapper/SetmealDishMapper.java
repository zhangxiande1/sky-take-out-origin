package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 批量插入套餐中菜品数据
     * @param setmealDishList
     */
    void insertBatch(List<SetmealDish> setmealDishList);

    /**
     * 删除菜品关系表中的数据
     * @param setmealId
     */
    @Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
    void deleteBysetmealId(Long setmealId);


    /**
     * 根据setmeal_id获取套餐菜品关系表中的菜品数据
     * @param id
     * @return
     */
    @Select("select  * from setmeal_dish where setmeal_id=#{id}")
    List<SetmealDish> getBySetmealId(Long id);

    /**
     * 判断当前菜品是否被套餐关联了
     * @param ids
     * @return
     */
    List<Long> getSetmealIdsByDishIds(List<Long> ids);
}

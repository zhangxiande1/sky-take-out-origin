package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 插入菜品数据
     * @param dish
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(Dish dish);


    List<DishVO> pageQuery(@Param("start")  Integer start,@Param("end") Integer end,@Param("name") String name,@Param("categoryId") Integer categoryId,@Param("status") Integer status);

    /**
     * 获取菜品数量
     * @return
     */
    @Select("select count(*) from dish")
    Integer getCounts();

    /**
     * 根据id查询对应菜品
     * @param id
     * @return
     */
    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);

    /**
     * 根据ids查询包含ids的套餐
     * @param ids
     * @return
     */
    List<Long> getSetmealIdsByDishIds(List<Long> ids);

    /**
     * 删除对应id的菜品
     * @param id
     */
    @Delete("delete from dish where id = #{id}")
    void deleteById(Long id);

    /**
     *删除菜品id对应的口味数据
     * @param id
     */
    @Delete("delete from dish_flavor where dish_id = #{id}")
    void deleteByDishId(Long id);

    /**
     * 通过菜品id查询对应的菜品口味数据
     * @param dishId
     * @return
     */
    @Select("select * from dish_flavor where dish_id = #{dishId}")
    List<DishFlavor> getDishFlavorsById(Long dishId);

    /**
     * 根据种类id找对应的种类名称
     * @param categoryId
     * @return
     */
    @Select("select name from category where category.id=#{categoryId}")
    String getCategoryNameById(Long categoryId);

    /**
     * 更新菜品数据
     * @param dish
     */
    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);

    /**
     * 查询套餐的菜品数据
     * @param dish
     * @return
     */
    List<Dish> getDishsByCategoryId(Dish dish);

    /**
     * 根据套餐id获取套餐对应的菜品
     * @param id
     * @return
     */
    @Select("select d.* from dish d left join setmeal_dish sd on d.id = sd.dish_id where sd.setmeal_id = #{id}")
    List<Dish> getBySetmealId(Long id);
}

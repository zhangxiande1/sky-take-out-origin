package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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

    @AutoFill(value = OperationType.INSERT)
    void insert(Dish dish);


    List<DishVO> pageQuery(Integer start, Integer end, String name, Integer categoryId, Integer status);

    @Select("select count(*) from dish")
    Integer getCounts();

    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);

    List<Long> getSetmealIdsByDishIds(List<Long> ids);

    @Delete("delete from dish where id = #{id}")
    void deleteById(Long id);

    @Delete("delete from dish_flavor where dish_id = #{id}")
    void deleteByDishId(Long id);
}

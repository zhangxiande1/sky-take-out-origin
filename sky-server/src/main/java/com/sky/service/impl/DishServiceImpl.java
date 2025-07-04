package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    @Transactional
    public void save(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        //插入菜品数据
        dishMapper.insert(dish);
        //插入口味数据

        //获取到刚加入数据库的菜品的自增id
        Long id = dish.getId();
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors!=null&& flavors.size()>0){
            flavors.forEach(flavor->flavor.setDishId(id));
        }
        dishFlavorMapper.insertBatch(flavors);

    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult dishPageQuery(DishPageQueryDTO dishPageQueryDTO) {
        Integer categoryId = dishPageQueryDTO.getCategoryId();
        String name = dishPageQueryDTO.getName();
        Integer page = dishPageQueryDTO.getPage();
        Integer pageSize = dishPageQueryDTO.getPageSize();
        Integer status = dishPageQueryDTO.getStatus();



        Integer start = (page -1)*pageSize;
        Integer end = start + pageSize;

        List<DishVO> dishVO = dishMapper.pageQuery(start,end,name,categoryId,status);

        Integer total = dishMapper.getCounts();
        PageResult pageResult = new PageResult(total,dishVO);

        return pageResult;

    }

    @Override
    public void deleteBatch(List<Long> ids) {
        //判断当前菜品是否能够删除---是否存在起售中的菜品？？
        for(Long id: ids){
            Dish dish = dishMapper.getById(id);
            Integer status = dish.getStatus();
            if(status == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //判断当前菜品是否能够删除---是否被套餐关联了？？
        List<Long> setMealIds = dishMapper.getSetmealIdsByDishIds(ids);
        if(setMealIds != null &&setMealIds.size()>0){
            //当前菜品被套餐关联了，不能删除
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        for (Long id :ids){
            dishMapper.deleteById(id);
            //删除菜品关联的口味数据
            dishMapper.deleteByDishId(id);
        }
    }

    @Override
    public DishVO getByIdWithFlavor(Long id) {
        DishVO dishVO = new DishVO();
        //获取对应id的菜品
        Dish dish = dishMapper.getById(id);
        BeanUtils.copyProperties(dish,dishVO);
        Long categoryId = dish.getCategoryId();
        List<DishFlavor> dishFlavors = dishMapper.getDishFlavorsById(id);
        String categoryName = dishMapper.getCategoryNameById(categoryId);
        dishVO.setFlavors(dishFlavors);
        dishVO.setCategoryName(categoryName);

        return dishVO;
    }

    /**
     * 根据id修改菜品基本信息和对应的口味信息
     *
     * @param dishDTO
     */
    @Override
    public void updateDishWithFlavors(DishDTO dishDTO) {
            Dish dish = new Dish();
            BeanUtils.copyProperties(dishDTO,dish);
            //插入修改后的菜品数据
            dishMapper.update(dish);
            Long dishId = dishDTO.getId();
            //删除原有的口味数据
            dishMapper.deleteByDishId(dishId);
            //插入新的口味数据
            List<DishFlavor> flavors = dishDTO.getFlavors();
            //更新口味数据的菜品id
            if(flavors!=null&&flavors.size()>0){
                flavors.forEach(dishFlavor -> {
                    dishFlavor.setDishId(dishId);
                });
                //批量插入口味数据
                dishFlavorMapper.insertBatch(flavors);
            }
    }

    /**
     * 根据categoryid，name和status获取对应的菜品
     * @param categoryId
     * @return
     */
    @Override
    public List<Dish> list(Long categoryId) {
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        //查询的是售卖状态的菜品
        dish.setStatus(StatusConstant.ENABLE);
        List<Dish> dishList = dishMapper.getDishsByCategoryId(dish);
        return dishList;
    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        //查询相同种类中的菜品数据
        List<Dish> dishList = dishMapper.getDishsByCategoryId(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishMapper.getDishFlavorsById(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }
    /**
     * 菜品起售停售
     * @param status
     * @param id
     */
    @Override
    @Transactional
    public void startOrStop(Integer status, Long id) {
        Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .build();
        dishMapper.update(dish);

        if (status == StatusConstant.DISABLE) {
            // 如果是停售操作，还需要将包含当前菜品的套餐也停售
            List<Long> dishIds = new ArrayList<>();
            dishIds.add(id);
            // select setmeal_id from setmeal_dish where dish_id in (?,?,?)
            List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(dishIds);
            if (setmealIds != null && setmealIds.size() > 0) {
                for (Long setmealId : setmealIds) {
                    Setmeal setmeal = Setmeal.builder()
                            .id(setmealId)
                            .status(StatusConstant.DISABLE)
                            .build();
                    setmealMapper.update(setmeal);
                }
            }
        }
    }



}

package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    SetmealMapper setmealMapper;
    @Autowired
    SetmealDishMapper setmealDishMapper;

    @Autowired
    DishMapper dishMapper;
    /**
     * 插入套餐
     * @param setmealDTO
     */
    @Override
    public void saveWithDish(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        //向套餐表插入数据
        setmealMapper.insert(setmeal);
        //获取套餐id
        Long setmealId = setmeal.getId();

        //获取套餐中的菜品数据
        List<SetmealDish> setmealDishList = setmealDTO.getSetmealDishes();

        if(setmealDishList!=null&&setmealDishList.size()>0){
            setmealDishList.forEach(setmealDish -> {
                setmealDish.setSetmealId(setmealId);
            });

        }
        //保存套餐和菜品的关联关系
        setmealDishMapper.insertBatch(setmealDishList);
    }

    /**
     * 套餐的分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult SetMealPageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        //获取总的记录数
        Long counts = setmealMapper.getCounts();
        Integer page = setmealPageQueryDTO.getPage();
        Integer pageSize = setmealPageQueryDTO.getPageSize();
        Integer start = (page - 1)*pageSize;
        Integer end = start + pageSize;
        String name = setmealPageQueryDTO.getName();
        Integer category = setmealPageQueryDTO.getCategoryId();
        Integer status = setmealPageQueryDTO.getStatus();


        //查询到的套餐数据
        List<SetmealVO> setmealVOS = setmealMapper.getSetmealByPage(start,end,name,category,status);
        PageResult pageResult = new PageResult(counts,setmealVOS);
        return pageResult;

    }

    /**
     * 套餐的启售和停售
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        //起售套餐时，判断套餐内是否有停售菜品，有停售菜品提示"套餐内包含未启售菜品，无法启售"
        if(status == StatusConstant.ENABLE){
            //获取套餐中的菜品数据
            List<Dish> dishList = dishMapper.getBySetmealId(id);
            if(dishList!=null&&dishList.size()>0){
                dishList.forEach(dish -> {
                    if(dish.getStatus()==StatusConstant.DISABLE){
                        throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                    }
                });
            }
        }
        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();
        setmealMapper.update(setmeal);
    }

    /**
     * 批量删除套餐
     * @param ids
     */
    @Override
    public void deleteBatch(List<Long> ids) {
        ids.forEach(id->{
            //根据套餐id获取对应套餐
            Setmeal setmeal =  setmealMapper.getById(id);
            if(StatusConstant.ENABLE == setmeal.getStatus()){
                //起售中的套餐不能删除
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }

        });
        ids.forEach(setmealId ->{
            //删除套餐表中的数据
            setmealMapper.deleteById(setmealId );
            //删除套餐菜品关系表中的数据
            setmealDishMapper.deleteBysetmealId(setmealId);
        });
    }

    /**
     * 根据id查询套餐和关联的菜品
     * @param id
     * @return
     */
    @Override
    public SetmealVO getByIdWithDish(Long id) {
        //根据id获取套餐
        Setmeal setmeal = setmealMapper.getById(id);
        SetmealVO setmealVO = new SetmealVO();
        //根据套餐id获取菜品
        List<SetmealDish> setmealDishes =  setmealDishMapper.getBySetmealId(id);
        BeanUtils.copyProperties(setmeal,setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);


        return setmealVO;
    }

    /**
     * 修改套餐
     * @param setmealDTO
     */
    @Override
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        //修改套餐数据
        setmealMapper.update(setmeal);
        //删除套餐菜品关系表中的菜品数据
        setmealDishMapper.deleteBysetmealId(setmeal.getId());
        //插入套餐中的菜品到套餐菜品关系表
         List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
         setmealDishes.forEach(setmealDish -> {
             setmealDish.setSetmealId(setmeal.getId());
         });
         setmealDishMapper.insertBatch(setmealDishes);


    }
}

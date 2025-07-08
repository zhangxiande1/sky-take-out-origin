package com.sky.mapper;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderMapper {
    /**
     * 插入订单数据
     * @param order
     */
    void insert(Orders order);

    /**
     * 根据订单号和用户id查询订单
     * @param orderNumber
     * @param userId
     */
    @Select("select * from orders where number = #{orderNumber} and user_id= #{userId}")
    Orders getByNumberAndUserId(@Param("orderNumber") String orderNumber,@Param("userId") Long userId);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    @Select("select * from orders where number = #{outTradeNo}")
    Orders getByNumber(String outTradeNo);

    /**
     * 查询记录总条数
     * @return
     */
    @Select("select COUNT(*) FROM orders where id = #{userId}")
    int getCounts(Long userId);

    @Select("select * from orders where user_id = #{userId} limit #{start} ,#{end}")
    List<Orders> list(@Param("start") int start,@Param("end") int end,@Param("userId") Long userId);

    /**
     * 根据id查询订单
     * @param id
     */
    @Select("select * from orders where id=#{id}")
    Orders getById(Long id);


    List<Orders> pageQuery(@Param("od") OrdersPageQueryDTO ordersPageQueryDTO,@Param("start") int start,@Param("end") int end);
    /**
     * 根据状态统计订单数量
     * @param status
     */
    @Select("select count(id) from orders where status = #{status}")
    Integer countStatus(Integer status);

}

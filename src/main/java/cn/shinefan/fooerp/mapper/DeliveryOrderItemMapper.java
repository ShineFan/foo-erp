package cn.shinefan.fooerp.mapper;

import cn.shinefan.fooerp.model.DeliveryOrderItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface DeliveryOrderItemMapper extends BaseMapper<DeliveryOrderItem> {

    @Select("SELECT * FROM delivery_order_item WHERE delivery_order_id = #{deliveryOrderId}")
    List<DeliveryOrderItem> findByDeliveryOrderId(Long deliveryOrderId);
}

package cn.shinefan.fooerp.mapper;

import cn.shinefan.fooerp.model.Customer;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {
    @Select("SELECT * FROM customer WHERE email = #{email}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "name", column = "name"),
        @Result(property = "email", column = "email"),
        @Result(property = "phone", column = "phone"),
        @Result(property = "address", column = "address"),
        @Result(property = "company", column = "company"),
        @Result(property = "status", column = "status")
    })
    Customer findByEmail(String email);
}

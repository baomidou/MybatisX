package template;
import java.math.BigDecimal;
import org.apache.ibatis.annotations.Param;

public interface TipMapper {
    int deleteByMoneyBetween(@Param("beginMoney") BigDecimal beginMoney, @Param("endMoney") BigDecimal endMoney);
}

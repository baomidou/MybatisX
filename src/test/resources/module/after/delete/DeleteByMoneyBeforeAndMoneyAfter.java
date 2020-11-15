package template;
import java.math.BigDecimal;
import org.apache.ibatis.annotations.Param;

public interface TipMapper {
    int deleteByMoneyBeforeAndMoneyAfter(@Param("money") BigDecimal money, @Param("oldMoney") BigDecimal oldMoney);
}

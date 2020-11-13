package template;
import org.apache.ibatis.annotations.Param;
import java.util.Collection;

public interface TipMapper {
    int deleteByIdIn(@Param("idList") Collection<Long> idList);
}

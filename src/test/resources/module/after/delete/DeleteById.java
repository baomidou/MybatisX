package template;
import org.apache.ibatis.annotations.Param;

public interface TipMapper {
    int deleteById(@Param("id") Long id);
}

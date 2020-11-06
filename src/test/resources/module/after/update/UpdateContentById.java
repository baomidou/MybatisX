package template;
import org.apache.ibatis.annotations.Param;

public interface TipMapper {
    int updateContentById(@Param("content") String content, @Param("id") Long id);
}

package template;
import org.apache.ibatis.annotations.Param;

public interface TipMapper {
    int updateAgeAndContentByIdGreaterThanEqual(@Param("age") Integer age, @Param("content") String content, @Param("id") Long id);
}

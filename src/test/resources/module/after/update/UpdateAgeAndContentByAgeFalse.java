package template;
import org.apache.ibatis.annotations.Param;

public interface TipMapper {
    int updateAgeAndContentByAgeFalse(@Param("age") Integer age, @Param("content") String content);
}

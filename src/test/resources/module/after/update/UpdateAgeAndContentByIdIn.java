package template;
import org.apache.ibatis.annotations.Param;

public interface TipMapper {
    int updateAgeAndContentByIdIn(@Param("age") Integer age, @Param("content") String content, @Param("idList") Collection<Long> idList);
}

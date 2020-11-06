package template;
import org.apache.ibatis.annotations.Param;

public interface TipMapper {
    int updateAgeAndContentByIdBetween(@Param("age") Integer age, @Param("content") String content, @Param("beginId") Long beginId, @Param("endId") Long endId);
}

package template;
import org.apache.ibatis.annotations.Param;

public interface TipMapper {
    int updateAgeByAgeAfter(@Param("age") Integer age, @Param("oldAge") Integer oldAge);
}

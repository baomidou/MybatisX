package template;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import template.Blog;

public interface TipMapper {
    List<Blog> selectByAgeBetween(@Param("beginAge") Integer beginAge, @Param("endAge") Integer endAge);
}

package template;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import template.Blog;

public interface TipMapper {
    List<Blog> selectByAgeLessThanEqual(@Param("age") Integer age);
}

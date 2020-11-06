package template;
import org.apache.ibatis.annotations.Param;
import template.Blog;

public interface TipMapper {
    int insertAll(Blog blog);
}

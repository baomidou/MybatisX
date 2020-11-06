package template;
import org.apache.ibatis.annotations.Param;
import java.util.Collection;
import template.Blog;

public interface TipMapper {
    int insertBatchWithAll(@Param("blogCollection") Collection<Blog> blogCollection);
}

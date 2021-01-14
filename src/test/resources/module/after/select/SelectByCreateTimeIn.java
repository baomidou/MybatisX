package template;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import java.util.Collection;
import template.Blog;

public interface TipMapper {
    List<Blog> selectByCreateTimeIn(@Param("createTimeList") Collection<Date> createTimeList);
}

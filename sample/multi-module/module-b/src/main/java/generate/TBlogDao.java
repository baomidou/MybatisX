package generate;

import generate.TBlog;

public interface TBlogDao {
    int deleteByPrimaryKey(Long id);

    int insert(TBlog record);

    int insertSelective(TBlog record);

    TBlog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TBlog record);

    int updateByPrimaryKey(TBlog record);
}

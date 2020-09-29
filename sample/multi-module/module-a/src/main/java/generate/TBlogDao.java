package generate;

import generate.TBlog;

import java.util.List;

public interface TBlogDao {
    int deleteByPrimaryKey(Long id);

    int insert(TBlog record);

    int insertSelective(TBlog record);

    TBlog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TBlog record);

    int updateByPrimaryKey(TBlog record);

    // 用于测试多模块生成基本标签,  根据 List<TBlog> getAllBlog(); 生成xml标签
    List<TBlog> getAllBlog();
}

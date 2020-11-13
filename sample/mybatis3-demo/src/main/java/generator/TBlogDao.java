package generator;

import generator.TBlog;

/**
 * @Entity generator.TBlog
 */
public interface TBlogDao {
    TBlog selectByPrimaryKey(Integer id);
}
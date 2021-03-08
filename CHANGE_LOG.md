# 1.4.8
1. 修复社区版本的错误提示,数组越界,持久化 等异常

# 1.4.7
1. 对生成代码的部分改版, 采用模板代码生成的方式

# 1.4.6
1. `#{}` 的提示与跳转
2. 修复 jpa 提示中 in,between 结尾生成的xml是字段的问题（实际上应该是列名）
3. 修复 jsr 日期生成
4. 修复 springboot 识别yml报错， yml后缀别名识别支持
5. 完善实体类的头部加入表名注释

# 1.4.3
1. 修复 association 标签关联问题
2. 修复 resultType 返回 Map 标记红色
3. 修复 无法通过接口生成xml
4. 修复 yml 关联别名的 --- 分割

# 1.3.9
1. jpa支持oracle的两种插入方式(insertWithUnion,insertWithAll)
2. 支持if-test方式,对于String类型的字段加入非空判断
3. 修复lombok的链式setter支持
4. 支持注解方式生成代码

# 1.1.2
1. 加入jpa生成代码
2. 给mapper类和xml加入小鸟图标

package com.word.wordmemory.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.word.wordmemory.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Override
    int insert(User entity);

    @Override
    int deleteById(Serializable id);

    @Override
    int deleteById(User entity);

    @Override
    int deleteByMap(Map<String, Object> columnMap);

    @Override
    int delete(Wrapper<User> queryWrapper);

    @Override
    int deleteBatchIds(Collection<?> idList);

    @Override
    int updateById(User entity);

    @Override
    int update(User entity, Wrapper<User> updateWrapper);

    @Override
    User selectById(Serializable id);

    @Override
    List<User> selectBatchIds(Collection<? extends Serializable> idList);

    @Override
    List<User> selectByMap(Map<String, Object> columnMap);

    @Override
    default User selectOne(Wrapper<User> queryWrapper) {
        return BaseMapper.super.selectOne(queryWrapper);
    }

    @Override
    default boolean exists(Wrapper<User> queryWrapper) {
        return BaseMapper.super.exists(queryWrapper);
    }

    @Override
    Long selectCount(Wrapper<User> queryWrapper);

    @Override
    List<User> selectList(Wrapper<User> queryWrapper);

    @Override
    List<Map<String, Object>> selectMaps(Wrapper<User> queryWrapper);

    @Override
    List<Object> selectObjs(Wrapper<User> queryWrapper);

    @Override
    <P extends IPage<User>> P selectPage(P page, Wrapper<User> queryWrapper);

    @Override
    <P extends IPage<Map<String, Object>>> P selectMapsPage(P page, Wrapper<User> queryWrapper);
}

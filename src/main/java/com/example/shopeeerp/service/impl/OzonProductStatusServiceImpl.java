package com.example.shopeeerp.service.impl;

import com.example.shopeeerp.mapper.OzonProductStatusMapper;
import com.example.shopeeerp.pojo.OzonProductStatus;
import com.example.shopeeerp.service.OzonProductStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Ozon商品状态Service实现类
 */
@Service
public class OzonProductStatusServiceImpl implements OzonProductStatusService {

    @Autowired
    private OzonProductStatusMapper ozonProductStatusMapper;

    @Override
    public OzonProductStatus getById(Long id) {
        return ozonProductStatusMapper.selectById(id);
    }

    @Override
    public OzonProductStatus getByProductId(Long productId) {
        return ozonProductStatusMapper.selectByProductId(productId);
    }

    @Override
    public List<OzonProductStatus> getAll() {
        return ozonProductStatusMapper.selectAll();
    }

    @Override
    public List<OzonProductStatus> getByCondition(OzonProductStatus condition) {
        return ozonProductStatusMapper.selectByCondition(condition);
    }

    @Override
    public List<OzonProductStatus> getByStatus(String status) {
        return ozonProductStatusMapper.selectByStatus(status);
    }

    @Override
    public List<OzonProductStatus> getByModerateStatus(String moderateStatus) {
        return ozonProductStatusMapper.selectByModerateStatus(moderateStatus);
    }

    @Override
    @Transactional
    public boolean save(OzonProductStatus status) {
        if (status.getCreatedAt() == null) {
            status.setCreatedAt(new Date());
        }
        if (status.getUpdatedAt() == null) {
            status.setUpdatedAt(new Date());
        }
        return ozonProductStatusMapper.insert(status) > 0;
    }

    @Override
    @Transactional
    public boolean update(OzonProductStatus status) {
        status.setUpdatedAt(new Date());
        return ozonProductStatusMapper.updateById(status) > 0;
    }

    @Override
    @Transactional
    public boolean removeById(Long id) {
        return ozonProductStatusMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional
    public boolean removeByProductId(Long productId) {
        return ozonProductStatusMapper.deleteByProductId(productId) > 0;
    }

    @Override
    @Transactional
    public boolean removeBatch(List<Long> ids) {
        return ozonProductStatusMapper.deleteBatch(ids) > 0;
    }

    @Override
    public long count() {
        return ozonProductStatusMapper.count();
    }

    @Override
    public long countByStatus(String status) {
        return ozonProductStatusMapper.countByStatus(status);
    }
}
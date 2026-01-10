package com.example.shopeeerp.service.impl;

import com.example.shopeeerp.mapper.OzonProductImageMapper;
import com.example.shopeeerp.pojo.OzonProductImage;
import com.example.shopeeerp.service.OzonProductImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Ozon商品图片Service实现类
 */
@Service
public class OzonProductImageServiceImpl implements OzonProductImageService {

    @Autowired
    private OzonProductImageMapper ozonProductImageMapper;

    @Override
    public OzonProductImage getById(Long id) {
        return ozonProductImageMapper.selectById(id);
    }

    @Override
    public List<OzonProductImage> getByProductId(Long productId) {
        return ozonProductImageMapper.selectByProductId(productId);
    }

    @Override
    public List<OzonProductImage> getAll() {
        return ozonProductImageMapper.selectAll();
    }

    @Override
    public List<OzonProductImage> getByCondition(OzonProductImage condition) {
        return ozonProductImageMapper.selectByCondition(condition);
    }

    @Override
    @Transactional
    public boolean save(OzonProductImage image) {
        if (image.getCreatedAt() == null) {
            image.setCreatedAt(new Date());
        }
        return ozonProductImageMapper.insert(image) > 0;
    }

    @Override
    @Transactional
    public boolean saveBatch(List<OzonProductImage> images) {
        Date now = new Date();
        images.forEach(image -> {
            if (image.getCreatedAt() == null) {
                image.setCreatedAt(now);
            }
        });
        return ozonProductImageMapper.insertBatch(images) > 0;
    }

    @Override
    @Transactional
    public boolean update(OzonProductImage image) {
        return ozonProductImageMapper.updateById(image) > 0;
    }

    @Override
    @Transactional
    public boolean removeById(Long id) {
        return ozonProductImageMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional
    public boolean removeByProductId(Long productId) {
        return ozonProductImageMapper.deleteByProductId(productId) > 0;
    }

    @Override
    @Transactional
    public boolean removeBatch(List<Long> ids) {
        return ozonProductImageMapper.deleteBatch(ids) > 0;
    }

    @Override
    public long count() {
        return ozonProductImageMapper.count();
    }

    @Override
    public long countByProductId(Long productId) {
        return ozonProductImageMapper.countByProductId(productId);
    }
}
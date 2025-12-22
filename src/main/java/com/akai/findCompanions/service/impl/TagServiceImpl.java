package com.akai.findCompanions.service.impl;

import com.akai.findCompanions.model.domain.Tag;
import com.akai.findCompanions.mapper.db.TagMapper;
import com.akai.findCompanions.service.ITagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 标签 服务实现类
 * </p>
 *
 * @author 凯哥
 * @since 2025-06-04
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements ITagService {

}

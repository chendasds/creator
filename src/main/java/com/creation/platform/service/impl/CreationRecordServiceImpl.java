package com.creation.platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.creation.platform.entity.CreationRecord;
import com.creation.platform.mapper.CreationRecordMapper;
import com.creation.platform.service.CreationRecordService;
import org.springframework.stereotype.Service;

@Service
public class CreationRecordServiceImpl extends ServiceImpl<CreationRecordMapper, CreationRecord> implements CreationRecordService {
}

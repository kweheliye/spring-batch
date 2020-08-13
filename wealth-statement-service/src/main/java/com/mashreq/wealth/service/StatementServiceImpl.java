package com.mashreq.wealth.service;

import com.mashreq.wealth.repository.StatementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mashreq.wealth.entity.Statement;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Service
public class StatementServiceImpl implements StatementService {
    @Autowired
    StatementRepository endOfMonthStatementRepository;

    @Override
    @Transactional(propagation = REQUIRES_NEW)
    public Statement saveOrUpdate(Statement endOfMonthStatement) {
        return endOfMonthStatementRepository.save(endOfMonthStatement);
    }

    @Override
    @Transactional(readOnly = true)
    public Statement findByFilename(String filename) {
        return endOfMonthStatementRepository.findByFilename(filename).orElse(null);
    }
}

package com.mashreq.wealth.service;

import com.mashreq.wealth.entity.Statement;

public interface StatementService {

    public Statement saveOrUpdate(Statement endOfMonthStatement);

    public Statement findByFilename(String filename);
}

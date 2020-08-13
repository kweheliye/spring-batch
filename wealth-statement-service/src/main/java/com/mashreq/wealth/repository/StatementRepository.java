package com.mashreq.wealth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.mashreq.wealth.entity.Statement;
import java.util.Optional;

@Repository
public interface StatementRepository extends JpaRepository<Statement, Long> {
    //filename is natural Id, unique
    public Optional<Statement> findByFilename(String filename);
}

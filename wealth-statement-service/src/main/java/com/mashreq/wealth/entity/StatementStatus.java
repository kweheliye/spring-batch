package com.mashreq.wealth.entity;

import com.mashreq.wealth.enums.EmailStatus;
import com.mashreq.wealth.enums.FileNameStatus;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import com.mashreq.wealth.enums.ProcessStatus;

import javax.persistence.*;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "statement_status")
@DynamicUpdate(value = true)
@EntityListeners(AuditingEntityListener.class)
@ToString(exclude="statement")
public class StatementStatus {
    @Id
    private Long id;


    @Column(name = "process_status")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ProcessStatus processStatus = ProcessStatus.NOT_STARTED;

    @Column(name = "email_status")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EmailStatus emailStatus = EmailStatus.NOT_PROCESSED;

    @Column(name = "filename_validity")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private FileNameStatus fileNameStatus = FileNameStatus.INVALID;


    @CreatedDate
    @Column(name = "created_date")
    private Date createdDate;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private Date lastModifiedDate;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    private Statement statement;
}

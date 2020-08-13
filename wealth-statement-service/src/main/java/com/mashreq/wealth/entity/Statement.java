package com.mashreq.wealth.entity;
import com.mashreq.wealth.enums.FileNameStatus;
import com.mashreq.wealth.enums.JobType;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.io.File;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DynamicUpdate (value =  true)
@Table(name = "statement")
public class Statement {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "cif_id")
    private String cifId;

    @NaturalId (mutable = false)
    @Column(name = "file_name", unique = true, nullable = false)
    private String filename;
    //Filename sent to customer will not clientId
    @Column(name = "attachment_file_name")
    private String attachmentFilename;
    @Column (name = "date")
    private String date;
    @Transient
    private File file;

    //job type
    @Column(name = "job_type")
    @Enumerated(EnumType.STRING)
    private JobType jobType;


    @OneToOne(mappedBy = "statement", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, optional = false)
    private StatementStatus statementStatus;



}

package com.clbee.crawler.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import jakarta.persistence.Id;
import org.springframework.data.domain.Persistable;

import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "quiz")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Quiz implements Persistable<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private Long id;

    @Transient
    private boolean isNew = true;

    @Column(length=30, nullable = false)
    private String subject;

    @Column(length=20, nullable = false)
    private String qualification;

    @Column(length=10, nullable = false)
    private String effectiveDate;

    @Column(length=2048, nullable = false)
    private String problem;

    @Lob
    @Column(length = 10000)
    private byte[] example2;

    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "choices", joinColumns = @JoinColumn(name = "quiz_id"))
    @Column(nullable = false, length=1024)
    private List<String> qnums;

    @Lob
    @Column(length = 10000)
    private byte[] answerExplain;

    @Column(length=2)
    private String answerNum;
}

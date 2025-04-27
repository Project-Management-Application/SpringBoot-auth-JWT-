package com.midou.tutorial.Projects.entities;

import com.midou.tutorial.user.entities.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "ProjectTask_attachments")
public class ProjectTaskAttachment {
    @Id
    @SequenceGenerator(
            name = "attachment_sequence",
            sequenceName = "attachment_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "attachment_sequence"
    )
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileUrl; // URL to the stored file

    @Column
    private String fileType; // e.g., image/png, application/pdf

    @Column
    private Long fileSize; // Size in bytes

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private ProjectTask task;

    @ManyToOne
    @JoinColumn(name = "uploaded_by", nullable = false)
    private User uploadedBy;

    @Column(nullable = false)
    private LocalDateTime uploadedAt;
}

package com.kosa.backend.common.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "FILES_SEQUENCE")
@NoArgsConstructor
@AllArgsConstructor
public class FilesSequence extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name="files_id")
    private Files files;
}

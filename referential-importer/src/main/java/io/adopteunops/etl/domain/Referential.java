package io.adopteunops.etl.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@ToString
@Table(name = "REFERENTIAL")
public class Referential {
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REFERENTIAL_ID", unique = true, nullable = false)
    @Id
    public Long id;
    public String key;
    public String value;
    public Date timestamp;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    public Set<MetadataItem> metadataItemSet = new HashSet<MetadataItem>();
}
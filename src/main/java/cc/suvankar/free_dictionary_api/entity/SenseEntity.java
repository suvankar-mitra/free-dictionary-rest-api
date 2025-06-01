package cc.suvankar.free_dictionary_api.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "sense")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SenseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "sense_id")
    private List<ExampleEntity> examples;

    @ElementCollection(fetch = jakarta.persistence.FetchType.LAZY)
    private List<String> topics;

    @ElementCollection(fetch = jakarta.persistence.FetchType.LAZY)
    @Column(columnDefinition = "TEXT")
    private List<String> rawGlosses;

    @ElementCollection(fetch = jakarta.persistence.FetchType.LAZY)
    @Column(columnDefinition = "TEXT")
    private List<String> glosses;

    @ElementCollection(fetch = jakarta.persistence.FetchType.LAZY)
    private List<String> tags;

    private String wikiSenseId; // renamed from 'id' to avoid conflict

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "sense_id")
    private List<CategoryEntity> categories;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "sense_id")
    private List<TranslationEntity> translations;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "sense_id")
    private List<DerivedEntity> derived;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "sense_id")
    private List<RelatedEntity> related;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "sense_id")
    private List<SenseAntonymEntity> senseAntonyms;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "sense_id")
    private List<SenseSynonymEntity> senseSynonyms;
}

package cc.suvankar.free_dictionary_api.entity;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;
import lombok.*;

@Entity
@Table(
    name = "word_entry",
    indexes = {
      @Index(name = "idx_word_entry_word", columnList = "word"),
      @Index(name = "idx_word_entry_word_lang", columnList = "word, lang_code")
    })
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class WordEntryEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String pos;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @JoinColumn(name = "word_entry_id")
  private List<HeadTemplateEntity> headTemplates;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @JoinColumn(name = "word_entry_id")
  private List<FormEntity> forms;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @JoinColumn(name = "word_entry_id")
  private List<AntonymEntity> antonyms;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @JoinColumn(name = "word_entry_id")
  private List<SynonymEntity> synonyms;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @JoinColumn(name = "word_entry_id")
  private List<SoundEntity> sounds;

  @Column(columnDefinition = "TEXT")
  private String etymologyText;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @JoinColumn(name = "word_entry_id")
  private List<EtymologyTemplateEntity> etymologyTemplates;

  private String word;
  private String lang;
  private String langCode;

  @ElementCollection(fetch = FetchType.LAZY)
  private List<String> wikipedia;

  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "word_entry_id")
  @OrderBy("senseOrder ASC")
  private List<SenseEntity> senses;

  private String dtoChecksum;
  private String source;

  @Temporal(TemporalType.TIMESTAMP)
  private Date lastModified;

  @Column(name = "source_file")
  private String sourceFile;

  @Column(name = "source_line_number")
  private Long sourceLineNumber;
}

package cc.suvankar.free_dictionary_api.entity;

import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "translation")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TranslationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String lang;
    private String code;
    private String sense;
    private String roman;
    private String word;

    @ElementCollection(fetch = jakarta.persistence.FetchType.LAZY)
    private List<String> tags;
}

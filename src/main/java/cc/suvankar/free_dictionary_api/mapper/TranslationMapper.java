package cc.suvankar.free_dictionary_api.mapper;

import org.springframework.stereotype.Component;

import cc.suvankar.free_dictionary_api.dto.TranslationDTO;
import cc.suvankar.free_dictionary_api.entity.TranslationEntity;

@Component
public class TranslationMapper {
    public TranslationDTO entityToDto(TranslationEntity entity) {
        TranslationDTO dto = new TranslationDTO();
        dto.setCode(entity.getCode());
        dto.setLang(entity.getLang());
        dto.setRoman(entity.getRoman());
        dto.setSense(entity.getSense());
        dto.setWord(entity.getWord());
        return dto;
    }
}

package cc.suvankar.free_dictionary_api.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ApiAttributionDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final ApiAttributionDTO INSTANCE = new ApiAttributionDTO();

    private final String name;
    private final String url;
    private final String license;
    private final boolean attributionRequired;
    private final String attributionText;

    private ApiAttributionDTO() {
        this.name = "free-dictionary-rest-api";
        this.url = "api.suvankar.cc";
        this.license = "CC BY-SA 4.0";
        this.attributionRequired = true;
        this.attributionText = "Data provided by Wiktionary (https://en.wiktionary.org) and aggregated by free-dictionary-rest-api (https://api.suvankar.cc), licensed under CC BY-SA 4.0";
    }

    public static ApiAttributionDTO getInstance() {
        return INSTANCE;
    }
}

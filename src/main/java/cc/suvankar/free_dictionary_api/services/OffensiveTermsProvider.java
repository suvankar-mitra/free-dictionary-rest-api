package cc.suvankar.free_dictionary_api.services;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

@Component
public class OffensiveTermsProvider {
    private static final Logger LOG = Logger.getLogger(OffensiveTermsProvider.class.getName());

    private Set<String> offensiveTerms = Collections.emptySet();

    @PostConstruct
    public void loadTerms() {
        Set<String> terms = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(getClass().getResourceAsStream("/bad-words.txt")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String word = line.trim().toLowerCase();
                if (!word.isEmpty() && !word.startsWith("#")) {
                    terms.add(word);
                }
            }
        } catch (Exception e) {
            LOG.warning("Could not load bad-words.txt");
            LOG.warning(e.getMessage());
        }
        offensiveTerms = Collections.unmodifiableSet(terms);
    }

    public boolean isOffensive(String word) {
        return offensiveTerms.contains(word.trim().toLowerCase());
    }

    public Set<String> getOffensiveTerms() {
        return offensiveTerms;
    }
}

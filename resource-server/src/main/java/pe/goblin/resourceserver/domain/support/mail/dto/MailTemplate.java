package pe.goblin.resourceserver.domain.support.mail.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public enum MailTemplate {
    EMAIL_VERIFICATION(
            "/templates/mails/mail-verification.html",
            Map.of("verificationCode", List.of(Pattern.compile("\\{\\{verification_code\\}\\}")))
    );

    private final String path;
    private final Map<String, List<Pattern>> argumentsToPatterns;

    MailTemplate(String path, Map<String, List<Pattern>> argumentsToPatterns) {
        this.path = path;
        this.argumentsToPatterns = argumentsToPatterns;
    }

    public String getPath() {
        return path;
    }

    public List<String> getArguments() {
        return new ArrayList<>(argumentsToPatterns.keySet());
    }

    public Map<String, List<Pattern>> getArgumentsToPatterns() {
        return argumentsToPatterns;
    }
}

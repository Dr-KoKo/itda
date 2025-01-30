package pe.goblin.itda.domain.support.mail;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import pe.goblin.itda.domain.support.mail.dto.MailTemplate;
import pe.goblin.itda.domain.support.mail.exception.MailTemplateException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class MailTemplateResolver {
    private final ResourceLoader resourceLoader;

    public MailTemplateResolver(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public String resolve(MailTemplate template, Map<String, String> parameters) {
        if (template == null) {
            throw new MailTemplateException("template can not be null");
        }
        String htmlTemplate = loadTemplate(template.getPath());
        Map<Pattern, String> patternReplacements = createPatternReplacements(template.getArgumentsToPatterns(), parameters == null ? Collections.emptyMap() : parameters);
        return applyReplacements(htmlTemplate, patternReplacements);
    }

    private String loadTemplate(String path) {
        Resource resource = resourceLoader.getResource("classpath:" + path);
        try (InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new MailTemplateException("Failed to load mail template from path: " + path, e);
        }
    }

    private Map<Pattern, String> createPatternReplacements(Map<String, List<Pattern>> argumentsToPatterns, Map<String, String> parameters) {
        return argumentsToPatterns.entrySet().stream()
                .flatMap(entry -> {
                    String argument = entry.getKey();
                    String parameter = parameters.get(argument);

                    if (parameter == null) {
                        throw new MailTemplateException("Missing required parameter: " + argument);
                    }

                    return entry.getValue().stream().map(pattern -> Map.entry(pattern, parameter));
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private String applyReplacements(String htmlTemplate, Map<Pattern, String> patternReplacements) {
        for (Map.Entry<Pattern, String> entry : patternReplacements.entrySet()) {
            htmlTemplate = entry.getKey().matcher(htmlTemplate).replaceAll(entry.getValue());
        }
        return htmlTemplate;
    }
}

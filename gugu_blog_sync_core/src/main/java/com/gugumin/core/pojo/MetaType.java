package com.gugumin.core.pojo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.toml.TomlMapper;
import lombok.Getter;
import lombok.SneakyThrows;
import org.yaml.snakeyaml.Yaml;

/**
 * The enum Meta type.
 *
 * @author minmin
 * @date 2023 /03/11
 */
@Getter
public enum MetaType {
    /**
     * Json meta type.
     */
    JSON("json"),
    /**
     * Yaml meta type.
     */
    YAML("yaml"),
    /**
     * Toml meta type.
     */
    TOML("toml");
    private final String value;

    MetaType(String value) {
        this.value = value;
    }

    /**
     * Parse meta and convert article.
     *
     * @param title   the title
     * @param context the context
     * @return the article
     */
    @SneakyThrows
    public Article parseMetaAndConvert(String title, String context) {
        String startStr = "```meta-" + value;
        String endStr = "```";
        int start = context.indexOf(startStr) + startStr.length() + System.lineSeparator().length();
        String subStartStr = context.substring(start);
        int end = subStartStr.indexOf(endStr);
        String configString = subStartStr.substring(0, end);
        String contextWithoutMeta = subStartStr.substring(end + endStr.length() + System.lineSeparator().length());
        Meta meta;
        switch (this) {
            case JSON: {
                ObjectMapper objectMapper = new ObjectMapper();
                meta = objectMapper.readValue(configString, Meta.class);
                break;
            }
            case YAML: {
                Yaml yaml = new Yaml();
                meta = yaml.loadAs(configString, Meta.class);
                break;
            }
            case TOML: {
                ObjectMapper objectMapper = new TomlMapper();
                meta = objectMapper.readValue(configString, Meta.class);
                break;
            }
            default: {
                throw new RuntimeException("No matching Meta Type");
            }
        }
        return new Article(title, contextWithoutMeta, meta, this);
    }

    /**
     * Generate meta and context string.
     *
     * @param article the article
     * @return the string
     */
    @SneakyThrows
    public String generateMetaAndContext(Article article) {
        StringBuilder result = new StringBuilder();
        result.append("```meta-").append(value).append(System.lineSeparator());
        String context;
        switch (this) {
            case JSON: {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
                context = objectMapper.writeValueAsString(article.getMeta());
                break;
            }
            case YAML: {
                Yaml yaml = new Yaml();
                context = yaml.dumpAsMap(article.getMeta());
                break;
            }
            case TOML: {
                ObjectMapper objectMapper = new TomlMapper();
                context = objectMapper.writeValueAsString(article.getMeta());
                break;
            }
            default: {
                throw new RuntimeException("No matching Meta Type");
            }
        }
        result.append(context).append(System.lineSeparator());
        result.append("```").append(System.lineSeparator());
        result.append(article.getContext()).append(System.lineSeparator());
        return result.toString();
    }
}

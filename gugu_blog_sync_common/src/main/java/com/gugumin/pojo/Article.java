package com.gugumin.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The type Article.
 *
 * @author minmin
 * @date 2023 /03/06
 */
@Data
@AllArgsConstructor
public class Article {
    private static final Pattern META_PATTERN = Pattern.compile("^```meta\\-(json|yaml|properties|toml)");
    private String name;
    private String context;
    private Meta meta;

    public static MetaType parseMetaFromContext(String context) {
        Matcher matcher = META_PATTERN.matcher(context);
        if (matcher.find()) {
            String group = matcher.group();
            String metaTypeString = group.substring(group.indexOf("-") + 1);
            for (MetaType metaType : MetaType.values()) {
                if (metaType.getValue().equalsIgnoreCase(metaTypeString)) {
                    return metaType;
                }
            }
        }
        throw new RuntimeException("无法获取到context中的meta格式");
    }
}

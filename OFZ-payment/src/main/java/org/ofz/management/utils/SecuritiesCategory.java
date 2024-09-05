package org.ofz.management.utils;

import java.util.HashMap;
import java.util.Map;

public enum SecuritiesCategory {
    SHINHAN("01", "신한투자증권"),
    NH("02", "NH투자증권"),
    KIWOOM("03", "키움증권"),
    SAMSUNG("04", "삼성증권"),
    KOREA_INVESTMENT("05", "한국투자증권");

    private final String code;
    private final String name;

    private static final Map<String, SecuritiesCategory> CODE_MAP = new HashMap<>();

    static {
        for (SecuritiesCategory category : SecuritiesCategory.values()) {
            CODE_MAP.put(category.getCode(), category);
        }
    }

    SecuritiesCategory(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static String getCompanyNamefromCode(String code) {
        SecuritiesCategory category = CODE_MAP.get(code);

        if (category == null) {
            throw new IllegalArgumentException("Invalid securities code: " + code);
        }

        return category.getName();
    }
}
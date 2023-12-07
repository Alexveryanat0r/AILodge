package com.example.myapplication.deepl;

import java.util.List;

public class DeepLData {
    List<TranslationsClass> translations;

    public List<TranslationsClass> getTranslations() {
        return translations;
    }

    public void setTranslations(List<TranslationsClass> translations) {
        this.translations = translations;
    }

    class TranslationsClass {
        String detected_source_language, text;
    }
}

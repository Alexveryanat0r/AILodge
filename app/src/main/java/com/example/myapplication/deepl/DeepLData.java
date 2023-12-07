package com.example.myapplication.deepl;

import java.util.List;

public class DeepLData {
    List<translationsClass> translations;

    public List<translationsClass> getTranslations() {
        return translations;
    }

    public void setTranslations(List<translationsClass> translations) {
        this.translations = translations;
    }

    class translationsClass {
        String detected_source_language, text;
    }
}
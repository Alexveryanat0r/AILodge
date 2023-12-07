package com.example.myapplication.lama.Filters;

import com.example.myapplication.lama.StaticPrompt;

public class BuilderOption{
    private StaticPrompt option;

    public BuilderOption(String templatedPrompt) {
        this.option = new StaticPrompt(templatedPrompt);
    }

    public StaticPrompt build(String value) {
        return setValue(option, value);
    }

    private StaticPrompt setValue(StaticPrompt prompt, String value) {
        return new StaticPrompt(prompt.toString().replace("*", value));
    }
}

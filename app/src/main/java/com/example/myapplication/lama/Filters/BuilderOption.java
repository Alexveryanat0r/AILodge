package com.example.myapplication.lama.Filters;

import com.example.myapplication.lama.StaticPrompt;

import java.util.List;

public class BuilderOption{
    private StaticPrompt option;

    public BuilderOption(String templatedPrompt) {
        this.option = new StaticPrompt(templatedPrompt);
    }

    public StaticPrompt build(String value) {
        return setValue(option, value);
    }
    public StaticPrompt buildt(String value) {
        String result = option.toString();
        String[] arr = value.split(", ");
        for(int i = 0; i < arr.length; i++) {
            arr[i] = "{" + arr[i] + "}";
        }

        result += String.join(", ", arr);
        result += ")))";

        return new StaticPrompt(result);
    }

    public StaticPrompt buildt2(String value) {
        String result = option.toString();
        String[] arr = value.split(", ");
        for(int i = 0; i < arr.length; i++) {
            arr[i] = "{" + arr[i] + "}";
        }

        result += String.join(", ", arr);
        result += "))";

        return new StaticPrompt(result);
    }

    private StaticPrompt setValue(StaticPrompt prompt, String value) {
        return new StaticPrompt(prompt.toString().replace("*", value));
    }
}

package com.example.myapplication.lama.Filters;

import com.example.myapplication.lama.StaticPrompt;

import java.util.List;

public class OptionsPrompts {
    public static BuilderOption mainTheme = new BuilderOption("Keeping the above in mind, emphasize (*) in your text, keeping the overall message and content in mind.");
    public static BuilderOption emotions = new BuilderOption("Given the above, create a (*) emotional context in the text.");
    public static BuilderOption audience = new BuilderOption("The target audience for which the text is generated - (*)");
    public static BuilderOption seasons = new BuilderOption("Given the above context, highlight the benefits of the hotel from the description that will help/need/will help you get high in the ((*))");
    public static BuilderOption descriptionStyle = new BuilderOption("Given the above context, your descriptive style is (*)");
    public static BuilderOption readerNamedLike = new BuilderOption("Address the user as (*)");
    public static BuilderOption dontUseWords = new BuilderOption("((Don't use those words: ");
}

package com.example.myapplication.lama.Filters;

import com.example.myapplication.lama.StaticPrompt;

import java.util.List;

public class OptionsPrompts {
    public static BuilderOption mainTheme = new BuilderOption("With all of the above in mind, emphasize (*) in the text, keeping the overall idea and content in mind. (Must underline in the text)");
    public static BuilderOption emotions = new BuilderOption("Given the context above, make the emotional background in the text, in style (*).");
    public static BuilderOption audience = new BuilderOption("The target audience for which the text is generated - (*). Given this, create a context closer to the target audience.");
    public static BuilderOption seasons = new BuilderOption("Given the above context, highlight the benefits of the hotel from the description that will help/need/will help you get high in the ((*))");
    public static BuilderOption descriptionStyle = new BuilderOption("Given the above context, your descriptive style is ((*))");
    public static BuilderOption readerNamedLike = new BuilderOption("Address the user as (*)");
    public static BuilderOption dontUseWords = new BuilderOption("((Don't use those words: ");
}

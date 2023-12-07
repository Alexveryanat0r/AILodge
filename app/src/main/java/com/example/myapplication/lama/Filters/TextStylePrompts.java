package com.example.myapplication.lama.Filters;

import com.example.myapplication.lama.StaticPrompt;

public class TextStylePrompts {
    public static StaticPrompt Bro = new StaticPrompt("Use an informal communication style. Words like \"bro,\" and \"cool\". You do a brief description of the hotel, thesis and ((summary)). Interesting and informative BUT BRIEF. Use an informal communication style. (Words like \"bro,\" \"cool\")");
    public static StaticPrompt Formal = new StaticPrompt("((Write in a formal style)). You should stick to it clearly, and in human-like language give a description of what I ask for. (Interesting), but adhering to the formal style.");
    public static StaticPrompt Joker = new StaticPrompt("Stick to an informal style, be sure to make jokes. Paste writing style with form. (You should apply irony), make jokes. Communicate in the language of a (semi-professional comic). With all of the above, your main task is to briefly describe the hotel. More IRONIC jokes and pranks.");
    public static StaticPrompt Journalistic = new StaticPrompt("You should write in literary and poetic language using aphorisms. Add interesting, rare sentences, copying Tolstoy's style. You describe the hotel, make a brief excerpt with stick to the given style, VERY BRIEFLY AND SUCCINCTLY.");
}

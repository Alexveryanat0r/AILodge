package com.example.myapplication.lama.Filters;

import com.example.myapplication.lama.StaticPrompt;

public class TextStylePrompts {
    public static StaticPrompt Bro = new StaticPrompt("Используйте неформальный стиль общения. Такие слова, как \"братан\" и \"круто\". Вы делаете краткое описание отеля, тезисы и ((резюме)). Интересно и познавательно, но кратко. Используйте неформальный стиль общения. (Слова типа \"братан\", \"круто\").");
    public static StaticPrompt Formal = new StaticPrompt("Below is an instruction that describes a task. Write a response that appropriately completes the request. ((Write in a formal style)). You should stick to it clearly, and in human-like language give a description of what I ask for. (Interesting), but adhering to the formal style.");
    public static StaticPrompt Joker = new StaticPrompt("Придерживайтесь неформального стиля, не забывайте шутить. (Вы должны применять иронию), шутить. Общайтесь на языке (полупрофессионального комика). При всем вышеперечисленном ваша главная задача - кратко описать отель. Больше ИРОНИЧЕСКИХ шуток и розыгрышей.");
    public static StaticPrompt Journalistic = new StaticPrompt("Пишите литературным и публицистический языком, используя афоризмы. Добавьте интересные, редкие предложения, копируя стиль Толстого. Акцентируй внимание на интересных моментах (к примеру 'Обратите внимание!', 'вау-эффект', 'Незабываемо'");
}

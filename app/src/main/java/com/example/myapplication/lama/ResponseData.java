package com.example.myapplication.lama;

import java.util.List;

public class ResponseData {
    List<ChoiceClass> choices;
    String id, object, created, model;
    UsageClass usage;

    public List<ChoiceClass> getChoices() {
        return choices;
    }

    public void setChoices(List<ChoiceClass> choices) {
        this.choices = choices;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public UsageClass getUsage() {
        return usage;
    }

    public void setUsage(UsageClass usage) {
        this.usage = usage;
    }

    class UsageClass {
        int prompt_tokens, completion_tokens, total_tokens;

        public int getPrompt_tokens() {
            return prompt_tokens;
        }

        public void setPrompt_tokens(int prompt_tokens) {
            this.prompt_tokens = prompt_tokens;
        }

        public int getCompletion_tokens() {
            return completion_tokens;
        }

        public void setCompletion_tokens(int completion_tokens) {
            this.completion_tokens = completion_tokens;
        }

        public int getTotal_tokens() {
            return total_tokens;
        }

        public void setTotal_tokens(int total_tokens) {
            this.total_tokens = total_tokens;
        }
    }

    class ChoiceClass {
        int index;
        String finish_reason;
        MessageClass message;
    }
}

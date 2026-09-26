package com.example.calculator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class History implements Serializable {
    private final List<String> history;

    public History() {
        this.history = new ArrayList<>();
    }

    public List<String> getHistory() {
        return history;
    }

   public void addLine(String line){
        history.add(line);
   }
}

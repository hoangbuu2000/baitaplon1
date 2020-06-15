/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demo.baitaplon;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Buu
 */
public class MultipleChoice extends Question{
    private final String[] LABELS = {"A", "B", "C", "D"};
    private List<Choice> choices;
    
    public MultipleChoice(String c, Level lv, Category ca){
        super(c, lv, ca);
        choices = new ArrayList<>();
    }
    
    public void addChoice(Choice c){
        this.choices.add(c);
    }
    
    public boolean checkAnswer(String kw){
        for(int i = 0; i < choices.size(); i++){
            if(this.choices.get(i).isCorrect() == true && LABELS[i].equals(kw.toUpperCase()))
                return true;
        }
        return false;
    }

    @Override
    public String toString() {
        String s = super.toString();
        int n = this.choices.size() - LABELS.length;
        if(n < 0)
            for(int i = 0; i < this.choices.size(); i++)
                s = s + LABELS[i] + ". " + this.choices.get(i).getContent() + "\n";
        else
            for(int i = 0; i < LABELS.length; i++)
                s = s + LABELS[i] + ". " + this.choices.get(i).getContent() + "\n";
        return s;
    }
    
    

    /**
     * @return the LABELS
     */
    public String[] getLABELS() {
        return LABELS;
    }

    /**
     * @return the phuongAn
     */
    public List<Choice> getChoices() {
        return choices;
    }

    /**
     * @param phuongAn the phuongAn to set
     */
    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }
    
    
}

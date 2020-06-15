/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demo.baitaplon;



/**
 *
 * @author Buu
 */
public abstract class Question {
    private String content;
    private Level level;
    private Category category;
    
    public Question(String c, Level lv, Category ca) {
        this.content = c;
        this.level = lv;
        this.category = ca;
    }

    @Override
    public String toString() {
        return String.format("\n%s\n", this.content);
    }
    
    public void addChoice(Choice c){}
    public boolean checkAnswer(String kw){return true;}
    public void addQuestion(Question q){}
    
    

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the level
     */
    public Level getLevel() {
        return level;
    }

    /**
     * @param level the level to set
     */
    public void setLevel(Level level) {
        this.level = level;
    }

    /**
     * @return the category
     */
    public Category getCategory() {
        return category;
    }

    /**
     * @param category the category to set
     */
    public void setCategory(Category category) {
        this.category = category;
    }
    
    
}

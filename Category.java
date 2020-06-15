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
class Category {
    private String content;
    
    public Category(String c){
        this.content = c;
    }

    @Override
    public String toString() {
        return this.content;
    }
    
    

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
    
    
}

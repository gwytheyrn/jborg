/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jborg.util;
import java.util.LinkedList;
/**
 *
 * @author tangd
 */
public class SentenceBuilder {
    //Use linkedlist as jborg needs to add words to the front and back
    private LinkedList<String> words;
    
    public SentenceBuilder() {
         words = new LinkedList<String>();
    }
    
    public void addFirst(String word) {
        words.addFirst(word);
    }
    
    public void addLast(String word) {
        words.addLast(word);
    }
    
    public Sentence toSentence() {
        return new Sentence(words);
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jborg.util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.List;

/**
 * 
 * @author tangd
 */
public class Sentence implements Comparable<Sentence> {
    private ArrayList<String> words = new ArrayList<String>();
    ///<editor-fold desc="Constructors">
    public Sentence(List<String> words) {
        //Copy words from supplied words list.  Did not copy reference
        //to keep the sentence's words list independent
        this.words.addAll(words);
    }
    
    public Sentence(String sentence) {
        //Add all words of the input sentence into the words list
        this.words.addAll(Arrays.asList(sentence.split("\\s+")));
    }
    ///</editor-fold>
    
    ///<editor-fold desc="Public Methods">
    @Override
    public int compareTo(Sentence s) {
        int minLength = Math.min(s.size(), size());
        for(int i=0; i<minLength; i++) {
            int comp = s.words.get(i).compareTo(words.get(i));
            if(comp != 0) return comp;
        }
        //Now if we have reached the end of one of the sentences
        if(s.size()==size()) { 
            //If they are equal length, they are equal sentences
            return 0;
        }
        else if(s.size() > size()) {
            //If s.size() > size, s > this
            return "".compareTo(s.words.get(size()));
        }
        else {
            //if s.size() < size(), this > s
            return words.get(s.size()).compareTo("");
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Sentence)) {
            return false;
        }
        Sentence s = (Sentence)o;
        return compareTo(s)==0;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.words);
        for(String s : words) {
            hash = hash ^ s.hashCode();
        }
        return hash;
    }
    
    public String getWord(int index) {
        //Assume all index checks are done outside of this method
        //OutOfBounds exception will be thrown if bad index
        return words.get(index);
    }
    
    public int size() {
        return words.size();
    }
    
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        if(words.size()>0) {
            for (int i = 0; i < words.size(); i++) {
                buffer.append(words.get(i));
                buffer.append(' ');
            }
            buffer.append(words.size()-1);
        }
        return buffer.toString();
    }
    ///</editor-fold>
}

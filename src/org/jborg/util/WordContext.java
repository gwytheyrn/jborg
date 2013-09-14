/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jborg.util;

/**
 * This class mimics the set<wstring>::iterator part of
 * seeborg's context_t.  size_t is not included as it is
 * embedded into the Sentence/ArrayList class.
 * @author tangd
 */
public class WordContext {
    private Sentence sentence;
    private final int position;
    
    public WordContext(Sentence s, int pos) {
        this.position = pos;
        this.sentence = s;
    }

    public Sentence getSentence() {
        return sentence;
    }
    public int getPosition() {
        return position;
    }
}

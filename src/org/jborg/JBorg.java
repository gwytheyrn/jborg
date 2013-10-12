/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jborg;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import org.jborg.util.*;
/**
 *
 * @author tangd
 */
public class JBorg {
    ///<editor-fold desc="Constructors">
    public JBorg() {
        this.numContexts = 0;
        this.maxContextDepth = 5;
        this.minContextDepth = 1;
    }
    
    public JBorg(int minContextDepth, int maxContextDepth) {
        this.numContexts = 0;
        this.maxContextDepth = maxContextDepth;
        this.minContextDepth = minContextDepth;
    }
    ///</editor-fold>
    
    ///<editor-fold defaultstate="collapsed" desc="Public Fields">
    public HashSet<Sentence> lines = new HashSet<Sentence>();
    public int numContexts;
    public HashMap<String, List<WordContext>> words = new HashMap<String,List<WordContext>>();
    ///</editor-fold>
    
    ///<editor-fold defaultstate="collapsed" desc="Private Fields">
    private int minContextDepth;
    private int maxContextDepth;
    ///</editor-fold>
    
    ///<editor-fold defaultstate="collapsed" desc="Public Methods">
    /**
     * Builds a reply using a random word in the input message as the base
     * for a Markov chain
     * @param inputMessage Message to respond to
     * @return 
     */
    public String generateReply(String inputMessage) {
        inputMessage = filterString(inputMessage);
        String[] words = inputMessage.split("\\.?\\s+");
        String seedWord = words[(int)(Math.random()*words.length)];
        return buildReply(seedWord).toString();
    }
    
    /**
     * Inserts all sentences in message into the lines knowledge base.  Unlike
     * seeborg, this does not filter out quotes, etc.  It is left up to the caller
     * to filter out unwanted lines.
     * @param message A complete message which may be several sentences
     * @return 
     */
    public void learn(String message) {
        message = filterString(message);    //Strip garbage, etc
        String[] sentences = message.split("\\. ");
        for(String sentence : sentences) {
            learnLine(sentence.trim());
        }
    }
    
    public boolean loadWords(File f) {
        if(null == f || !(f.canRead() && f.isFile())) {
            System.err.println("Lines file not found");
            return false;
        }
        try {
            Scanner s = new Scanner(f);
            String line;
            while(s.hasNextLine()) {
                line = s.nextLine();
                this.learn(line);
            }
            s.close();
        } catch(IOException ioEx) {
            System.err.println("Error reading lines file");
            return false;
        }
        System.out.printf("Parsed %d lines\n", lines.size());
        System.out.printf("I know %d words (%d contexts, %.2f per word), %d lines\n", words.size(), numContexts, 1.0*numContexts/words.size(), lines.size());
        return true;
    }
    
    public void saveWords(File f) {
        try {
            PrintWriter writer = new PrintWriter(f);
            for(Sentence s : lines) {
                writer.println(s.toString());
            }
            writer.close();
            System.out.println("Saved lines file");
        } catch(FileNotFoundException fnfEx) {
            System.err.println("RIP lost all of your lines!");
        }
    }
    ///</editor-fold>
    
    ///<editor-fold defaultstate="collapsed" desc="Private Methods">
    private Sentence buildReply(String seedWord) {
        SentenceBuilder sentence = new SentenceBuilder();
        //System.out.printf("seed word: %s\n", seedWord);
        boolean endFound = false;
        String leftWord = seedWord;
        String rightWord = seedWord;
        sentence.addFirst(seedWord);
        
        while(!endFound) {
            List<WordContext> contexts = words.get(leftWord);
            if(contexts!=null && contexts.size()>0) {
                WordContext leftContext = contexts.get((int)(Math.random()*contexts.size()));
                int leftDepth = (int)(Math.random()*(maxContextDepth-minContextDepth+1))+minContextDepth;
                int wordPosition = leftContext.getPosition()-1;
                for(int i=0;i<leftDepth;i++) {
                    if((wordPosition-i)<0) {
                        endFound = true;        //Found left edge of a context
                        break;
                    } else {
                        leftWord = leftContext.getSentence().getWord(wordPosition-i);
                        //System.out.printf("left: %s \n", leftWord);
                        sentence.addFirst(leftWord);
                    }
                }
            } else {
                System.err.printf("Context for word '%d' not found\n", leftWord);
                endFound = true;
            }
        }
        endFound = false;
        
        while(!endFound) {
            List<WordContext> contexts = words.get(rightWord);
            if(contexts!=null && contexts.size()>0) {
                WordContext rightContext = contexts.get((int)(Math.random()*contexts.size()));
                int rightDepth = (int)(Math.random()*(maxContextDepth-minContextDepth+1))+minContextDepth;
                int wordPosition = rightContext.getPosition()+1;
                for(int i=0;i<rightDepth;i++) {
                    if((wordPosition+i)>=rightContext.getSentence().size()) {
                        endFound = true;        //Found left edge of a context
                        break;
                    } else {
                        rightWord = rightContext.getSentence().getWord(wordPosition+i);
                        //System.out.printf("right: %s \n", rightWord);
                        sentence.addLast(rightWord);
                    }
                }
            } else {
                System.err.printf("Context for word '%d' not found\n", rightWord);
                endFound = true;
            }
            //System.out.print("\n");
        }
        return sentence.toSentence();
    }
    
    private String filterString(String inputLine) {
        inputLine = inputLine.replaceAll("\\! ", "!. ");
        inputLine = inputLine.replaceAll("\\? ", "?. ");
        inputLine = inputLine.replaceAll("[\r\n\"]","");
        inputLine = inputLine.replaceAll("\t", " ");
        inputLine = inputLine.trim();   //Remove leading/trailing whitespace
        return inputLine;
    }
    /**
     * Adds inputted sentence to the line knowledge base
     * @param line Input 'sentence' in string format.
     * @return 
     */
    private int learnLine(String line) {
        Sentence s = new Sentence(line);
        if(lines.contains(s)) return 0;
        for(int i = 0; i < s.size(); i++) {
            String word = s.getWord(i);
            WordContext ctx = new WordContext(s, i);
            List<WordContext> contexts = words.get(word);
            if(contexts == null) {
                contexts = new ArrayList<WordContext>();
                words.put(word, contexts);
            }
            contexts.add(ctx);
            numContexts++;
        }
        lines.add(s);
        return 1;
    }
    
    ///</editor-fold>
}
 
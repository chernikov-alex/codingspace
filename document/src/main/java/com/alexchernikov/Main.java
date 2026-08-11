package com.alexchernikov;

import java.util.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    private static Map<String, List<Integer>> wordsToIdsMap = new HashMap<>();

    public static void main(String[] args) {
    String doc1 = "shipping cost sender";
    String doc2 = "cost sender";
    preProcessDocument(11, doc1);
    preProcessDocument(12, doc2);
    List<Integer> listCost = getDocsBy("cost");
    System.out.println("listCost = " + listCost);
//pre-process time, no constraints on resources
 //       public void preProcessDocument(Integer docId, String docData) {}
// 1 - iterate over document and extract tokens - words - and put them into map docId - to set of words from docData

// 2   Set of words - list of documents

// 11 - the shipping, the cost, the sender || shipping - 11 , cost - 11, sender - 11, the
// 12 - cost, sender || shipping - 11 , cost - {11, 12}, sender - {11, 12}

// Map<String, List<Integer>> wordsToIdsMap



//run time - return an answer as fast as possible
        //public List<Integer> getDocsBy(String word) {}
        // receiver - Optional<Empty>,
        //
 // עולם התוכן : ספרים/מסמכים
        //במתודה הראשונה מקבלים מזהה של המסמך וטקסט
        //( אותיות באנגלית בלי symobols. רוח בין מילה למילה )
        //
        //המתודה השניה מקבלת מילה
        //בהנתן המילה המערכת צריכה להחזיר את באילו מסמכים הופיע המילה

        //66% - dog, 33% - cat
        // the dog drink water
        // the dog eat food
        // the cat drink milk
        // current - next
        // the -> dog | 1
        // the -> dog | 2
        // the -> cat | 1
        // the -> cat | 3


    }

    public static void preProcessDocument(Integer docId, String docData) {
        String[] words = docData.split(" ");
        Set<String> deduplicatedWords = new HashSet<>();
        deduplicatedWords.addAll(Arrays.asList(words));
        for(String word: words) {
            List<Integer> docIds = wordsToIdsMap.computeIfAbsent(word, w -> new LinkedList<Integer>());
            docIds.add(docId);// 11 , 11
        }
        System.out.println("preProcessDocument finished");
    }

    public static List<Integer> getDocsBy(String word) {
        if(!wordsToIdsMap.keySet().contains(word)) {
            return Collections.emptyList();
        }
        else {
            return wordsToIdsMap.get(word);
        }
    }
}
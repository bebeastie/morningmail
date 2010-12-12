package com.morningmail.utils;

import java.text.BreakIterator;

public class TextUtils {
	
	public static String getSummary(String text, int numWords, boolean addEllipses) {
        BreakIterator boundary = BreakIterator.getWordInstance();
        boundary.setText(text);
             
        int start = boundary.first();
        int end = 0;
        
        int foundWords = 0;
        while (foundWords <= numWords) {
        	end = boundary.next();
        	if (end == BreakIterator.DONE) 
        		break;
        	foundWords++;
        }
        
        text = text.substring(start,end);
        text = text.trim();
        text = addEllipses ? text + "...":text;
        return text;
	}
}

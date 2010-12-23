package com.morningmail.utils;

import java.text.BreakIterator;
import java.io.*;
import java.net.URI;
import java.net.URL;

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
        
        if (end != BreakIterator.DONE) {
        	text = text.substring(start,end);
            text = text.trim();
            text = addEllipses ? text + "...":text;
        }

        return text;
	}
	
}

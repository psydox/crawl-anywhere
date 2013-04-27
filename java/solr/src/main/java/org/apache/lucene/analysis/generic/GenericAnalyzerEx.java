package org.apache.lucene.analysis.generic;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.snowball.SnowballFilter;


import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

/**
 * Analyzer for French language. Supports an external list of stopwords (words that
 * will not be indexed at all) and an external list of exclusions (word that will
 * not be stemmed, but indexed).
 * A default set of stopwords is used unless an alternative list is specified, the
 * exclusion list is empty by default.
 *
 * @author Patrick Talbot (based on Gerhard Schwarz's work for German)
 * @version $Id: FrenchAnalyzer.java 472959 2006-11-09 16:21:50Z yonik $
 */
public final class GenericAnalyzerEx extends Analyzer {

	//  /**
	//   * Extended list of typical French stopwords.
	//   */
	//  public final static String[] FRENCH_STOP_WORDS = {
	//        "a", "ai", "au", "auquel", "aussi",
	//        "aux", "auxquelles", "auxquels", "avait", "avec", "avoir",
	//        "c", "car", "ce", "ceci", "cela", "celle", "celles", "celui",
	//        "ces", "cet", "cette", "ceux", "ci",
	//        "d", "de",
	//        "des", "devra", "doit", "donc", "dont", "du", "elle", "elles",
	//        "en", "entre", "est", "et", "etc", "etre", "eu", "eux",
	//        "hors", "il", "ils", "j", "je", "jusqu", "jusque", "l", "la", "laquelle",
	//        "le", "les", "leur", "leurs", "lui", "l�",
	//        "ma", "mais", "me", "mes", "mien", "mienne", "miennes", "miens", "moi",
	//        "mon", "n", "ne", "ni", "non", "nos", "notre",
	//        "nous", "n�tre", "n�tres", "on", "ont", "par", "parmi",
	//        "pas", "pass�", "pour",
	//        "qu", "que", "quel", "quelle", "quelles", "quels",
	//        "qui", "quoi", "s", "sa", "sans", "se",
	//        "seront", "ses", "si", "sien", "sienne", "siennes", "siens", "soi", "soit",
	//        "son", "sont", "sous", "sur", "ta", "te", "tes", "tien", "tienne", "tiennes",
	//        "tiens", "toi", "ton", "tu", "un", "une", "va",
	//        "vos", "votre", "vous", "vu", "v�tre", "v�tres", "y", "�", "�a",
	//        "�t�", "�tre", "�",
	//        "an", "and", "are", "as", "at", "be", "but", "by",
	//        "for", "if", "in", "into", "is", "it",
	//        "no", "not", "of", "on", "or", "such",
	//        "that", "the", "their", "then", "there", "these",
	//        "they", "this", "to", "was", "will", "with",
	//        "has", "have", "i", "you", "your", "t", "he", "we", "can"
	//  };


	/**
	 * Contains the stopwords used with the StopFilter.
	 */
	private Set stoptable = new HashSet();
	/**
	 * Contains words that should be indexed but not stemmed.
	 */
	private Set excltable = new HashSet();

	private String language = null;

	/**
	 * Builds an analyzer
	 */
	public GenericAnalyzerEx() {
		stoptable = null;
	}
	public GenericAnalyzerEx(String language) {
		stoptable = null;
		this.language = language;
	}

	/**
	 * Builds an analyzer with the given stop words.
	 */
	public GenericAnalyzerEx(String[] stopwords) {
		stoptable = StopFilter.makeStopSet(stopwords);
	}
	public GenericAnalyzerEx(String language, String[] stopwords) {
		stoptable = StopFilter.makeStopSet(stopwords);
		this.language = language;
	}

	public GenericAnalyzerEx(String language, File stopwords) throws IOException {
		stoptable = new HashSet(WordlistLoader.getWordSet(stopwords));
		this.language = language;
	}

	/**
	 * Builds an analyzer with the given stop words.
	 * @throws IOException
	 */
	public GenericAnalyzerEx(File stopwords) throws IOException {
		stoptable = new HashSet(WordlistLoader.getWordSet(stopwords));
	}

	/**
	 * Builds an exclusionlist from an array of Strings.
	 */
	public void setStemExclusionTable(String[] exclusionlist) {
		excltable = StopFilter.makeStopSet(exclusionlist);
	}

	/**
	 * Builds an exclusionlist from a Hashtable.
	 */
	public void setStemExclusionTable(Hashtable exclusionlist) {
		excltable = new HashSet(exclusionlist.keySet());
	}

	/**
	 * Builds an exclusionlist from the words contained in the given file.
	 * @throws IOException
	 */
	public void setStemExclusionTable(File exclusionlist) throws IOException {
		excltable = new HashSet(WordlistLoader.getWordSet(exclusionlist));
	}

	/**
	 * Creates a TokenStream which tokenizes all the text in the provided Reader.
	 *
	 * @return A TokenStream build from a StandardTokenizer filtered with
	 *         StandardFilter, StopFilter, FrenchStemFilter and GenericFilter
	 */
	public final TokenStream tokenStream(String fieldName, Reader reader) {

		if (fieldName == null) throw new IllegalArgumentException("fieldName must not be null");
		if (reader == null) throw new IllegalArgumentException("reader must not be null");

		TokenStream result = new GenericTokenizer(reader);
		if (stoptable!=null)
			result = new StopFilter(true, result, stoptable);

		if (language!=null)
			result = new SnowballFilter(result, language);

		return result;
	}
}

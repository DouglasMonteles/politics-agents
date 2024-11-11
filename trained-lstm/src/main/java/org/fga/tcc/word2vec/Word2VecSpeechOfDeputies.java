/*******************************************************************************
 *
 *
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *  See the NOTICE file distributed with this work for additional
 *  information regarding copyright ownership.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ******************************************************************************/

package org.fga.tcc.word2vec;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.fga.tcc.utils.DownloaderUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collection;

/**
 * Created by douglasmonteles on 18/08/2024.
 * Based on: Word2VecRawTextExample
 *
 * Neural net that processes text into wordvectors. See below url for an in-depth explanation.
 */
public class Word2VecSpeechOfDeputies {

    private static final Logger LOG = LoggerFactory.getLogger(Word2VecSpeechOfDeputies.class);

    public static String dataLocalPath;

    public static void main(String[] args) throws Exception {

        dataLocalPath = DownloaderUtility.NLPDATA.Download();
        // Gets Path to Text file
        String filePath = new File(dataLocalPath,"raw_sentences.txt").getAbsolutePath();

        LOG.info("Load & Vectorize Sentences....");
        // Strip white space before and after for each line
        SentenceIterator iter = new BasicLineIterator(filePath);
        // Split on white spaces in the line to get words
        TokenizerFactory t = new DefaultTokenizerFactory();

        /*
            CommonPreprocessor will apply the following regex to each token: [\d\.:,"'\(\)\[\]|/?!;]+
            So, effectively all numbers, punctuation symbols and some special symbols are stripped off.
            Additionally it forces lower case for all tokens.
         */
        t.setTokenPreProcessor(new CommonPreprocessor());

        LOG.info("Building model....");
        Word2Vec vec = new Word2Vec.Builder()
                .minWordFrequency(5)
                .iterations(1)
                .layerSize(100)
                .seed(42)
                .windowSize(5)
                .iterate(iter)
                .tokenizerFactory(t)
                .build();

        LOG.info("Fitting Word2Vec model....");
        vec.fit();

        LOG.info("Writing word vectors to text file....");

        // Prints out the closest 10 words to "day". An example on what to do with these Word Vectors.
        LOG.info("Closest Words:");
        Collection<String> lst = vec.wordsNearestSum("day", 10);
        LOG.info("10 Words closest to 'day': {}", lst);

        WordVectorSerializer.writeWordVectors(vec, "/home/douglas/dl4j-examples-data/dl4j-examples/nlp/teste.txt");

        // TODO resolve missing UiServer
//        UIServer server = UIServer.getInstance();
//        System.out.println("Started on port " + server.getPort());
    }
}

package org.fga.tcc.services.impl;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.api.InvocationType;
import org.deeplearning4j.optimize.listeners.EvaluativeListener;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.fga.tcc.exceptions.*;
import org.fga.tcc.services.VotingModelService;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.learning.config.RmsProp;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class VotingModelServiceImpl implements VotingModelService {

    private static final VotingModelService INSTANCE = new VotingModelServiceImpl();

    private static final Logger LOG = LoggerFactory.getLogger(VotingModelServiceImpl.class);

    private WordVectors wordVectors;
    private final TokenizerFactory tokenizerFactory;

    private String modelPath;

    public VotingModelServiceImpl() {
        tokenizerFactory = new DefaultTokenizerFactory();
        tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());
    }

    public VotingModelService setModelPath(String modelPath) {
        this.modelPath = modelPath;
        return this;
    }

    public VotingModelService prepareWordVector() {
        this.validateAttributes();

        // Gets Path to Text file
        String filePath = new File(this.modelPath, PURE_TXT_NAME).getAbsolutePath();

        try {
            LOG.info("Load & Vectorize Sentences....");
            // Strip white space before and after for each line
            SentenceIterator iter = new BasicLineIterator(filePath);
            // Split on white spaces in the line to get words
            TokenizerFactory t = new DefaultTokenizerFactory();

            //CommonPreprocessor will apply the following regex to each token: [\d\.:,"'\(\)\[\]|/?!;]+
            //So, effectively all numbers, punctuation symbols and some special symbols are stripped off.
            //Additionally it forces lower case for all tokens.
            t.setTokenPreProcessor(new CommonPreprocessor());

            LOG.info("Building model....");
            Word2Vec vec = new Word2Vec.Builder()
                    .minWordFrequency(2)
                    .iterations(5)
                    .layerSize(100)
                    .seed(42)
                    .windowSize(20)
                    .iterate(iter)
                    .tokenizerFactory(t)
                    .build();

            LOG.info("Fitting Word2Vec model....");
            vec.fit();

            LOG.info("Writing word vectors to text file....");

            // Write word vectors to file
            // noinspection unchecked
            WordVectorSerializer.writeWordVectors(vec.lookupTable(), new File(this.modelPath, WORD_VECTOR_NAME).getAbsolutePath());
        } catch (FileNotFoundException e) {
            throw new BasicLineInteratorException(e.getMessage());
        } catch (IOException e) {
            throw new WordVectorSerializerException(e.getMessage());
        }

        return this;
    }

    public VotingModelService trainModel() {
        this.validateAttributes();

        int batchSize = 200;     //Number of examples in each minibatch
        int nEpochs = 1;        //Number of epochs (full passes of training data) to train on
        int truncateReviewsToLength = 5000;  //Truncate reviews with length (# words) greater than this

        //DataSetIterators for training and testing respectively
        //Using AsyncDataSetIterator to do data loading in a separate thread; this may improve performance vs. waiting for data to load
        wordVectors = WordVectorSerializer.readWord2VecModel(new File(this.modelPath, WORD_VECTOR_NAME));

        TokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
        tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());

        VotesIterator iTrain = new VotesIterator.Builder()
                .dataDirectory(this.modelPath)
                .wordVectors(wordVectors)
                .batchSize(batchSize)
                .truncateLength(truncateReviewsToLength)
                .tokenizerFactory(tokenizerFactory)
                .train(true)
                .build();

        VotesIterator iTest = new VotesIterator.Builder()
                .dataDirectory(this.modelPath)
                .wordVectors(wordVectors)
                .batchSize(batchSize)
                .tokenizerFactory(tokenizerFactory)
                .truncateLength(truncateReviewsToLength)
                .train(false)
                .build();

        //DataSetIterator train = new AsyncDataSetIterator(iTrain,1);
        //DataSetIterator test = new AsyncDataSetIterator(iTest,1);

        int inputNeurons = wordVectors
                .getWordVector(wordVectors.vocab().wordAtIndex(0))
                .length; // 100 in our case

        int outputs = iTrain.getLabels().size();

        tokenizerFactory = new DefaultTokenizerFactory();
        tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());
        //Set up network configuration
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .updater(new RmsProp(0.0018))
                .l2(1e-5)
                .weightInit(WeightInit.XAVIER)
                .gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue).gradientNormalizationThreshold(1.0)
                .list()
                .layer( new LSTM.Builder().nIn(inputNeurons).nOut(200)
                        .activation(Activation.TANH).build())
                .layer(new RnnOutputLayer.Builder().activation(Activation.SOFTMAX)
                        .lossFunction(LossFunctions.LossFunction.MCXENT).nIn(200).nOut(outputs).build())
                .build();

        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();

        System.out.println("Starting training...");
        net.setListeners(new ScoreIterationListener(1), new EvaluativeListener(iTest, 1, InvocationType.EPOCH_END));
        net.fit(iTrain, nEpochs);

        System.out.println("Evaluating...");
        Evaluation eval = net.evaluate(iTest);
        System.out.println(eval.stats());

        try {
            net.save(new File(this.modelPath, MODEL_NAME), true);
        } catch (IOException e) {
            throw new MultiLayerNetworkTrainingException(e.getMessage());
        }

        System.out.println("----- Example complete -----");
        return this;
    }

    public String evaluateVoteModel(String proposal) {
        this.validateAttributes();

        this.wordVectors = this.loadWordVector();

        MultiLayerNetwork net = this.loadModel();

        if (net == null) {
            throw new MultiLayerNetworkException("Erro ao carregar o modelo.");
        }

        DataSet testNews = prepareTestData(proposal);
        INDArray fet = testNews.getFeatures();
        INDArray predicted = net.output(fet, false);
        long[] arrsiz = predicted.shape();

        File categories = new File(this.modelPath + File.separator + "categories.txt");

        double max = 0;
        int pos = 0;
        for (int i = 0; i < arrsiz[1]; i++) {
            if (max < (double) predicted.slice(0).getRow(i).sumNumber()) {
                max = (double) predicted.slice(0).getRow(i).sumNumber();
                pos = i;
            }
        }

        try (BufferedReader brCategories = new BufferedReader(new FileReader(categories))) {
            String temp;
            List<String> labels = new ArrayList<>();
            while ((temp = brCategories.readLine()) != null) {
                labels.add(temp);
            }
            brCategories.close();

            return labels.get(pos).split(",")[1].trim();
        } catch (Exception e) {
            System.out.println("File Exception : " + e.getMessage());
        }

        return null;
    }

    public static VotingModelService getInstance() {
        return INSTANCE;
    }

    private MultiLayerNetwork loadModel() {
        try {
            return MultiLayerNetwork.load(new File(this.modelPath, MODEL_NAME), true);
        } catch (IOException e) {
            System.out.println("Erro ao carregar modelo. Erro: " + e.getMessage());
        }

        return null;
    }

    private Word2Vec loadWordVector() {
        return WordVectorSerializer.readWord2VecModel(new File(this.modelPath, WORD_VECTOR_NAME));
    }

    private DataSet prepareTestData(String i_news) {
        List<String> news = new ArrayList<>(1);
        int[] category = new int[1];
        news.add(i_news);

        List<List<String>> allTokens = new ArrayList<>(news.size());
        int maxLength = 0;
        for (String s : news) {
            List<String> tokens = tokenizerFactory.create(s).getTokens();
            List<String> tokensFiltered = new ArrayList<>();
            for (String t : tokens) {
                if (wordVectors.hasWord(t)) tokensFiltered.add(t);
            }
            allTokens.add(tokensFiltered);
            maxLength = Math.max(maxLength, tokensFiltered.size());
        }

        INDArray features = Nd4j.create(news.size(), wordVectors.lookupTable().layerSize(), maxLength);
        INDArray labels = Nd4j.create(news.size(), 4, maxLength);    //labels: Crime, Politics, Bollywood, Business&Development
        INDArray featuresMask = Nd4j.zeros(news.size(), maxLength);
        INDArray labelsMask = Nd4j.zeros(news.size(), maxLength);

        int[] temp = new int[2];
        for (int i = 0; i < news.size(); i++) {
            List<String> tokens = allTokens.get(i);
            temp[0] = i;
            for (int j = 0; j < tokens.size() && j < maxLength; j++) {
                String token = tokens.get(j);
                INDArray vector = wordVectors.getWordVectorMatrix(token);
                features.put(new INDArrayIndex[]{NDArrayIndex.point(i),
                                NDArrayIndex.all(),
                                NDArrayIndex.point(j)},
                        vector);

                temp[1] = j;
                featuresMask.putScalar(temp, 1.0);
            }
            int idx = category[i];
            int lastIdx = Math.min(tokens.size(), maxLength);
            // TODO: at this point, if the token wasn't recognized, it's throw IndexOutBoundsException
            labels.putScalar(new int[]{i, idx, lastIdx - 1}, 1.0);
            labelsMask.putScalar(new int[]{i, lastIdx - 1}, 1.0);
        }

        return new DataSet(features, labels, featuresMask, labelsMask);
    }

    private void validateAttributes() {
        if (this.modelPath == null || this.modelPath.isEmpty()) {
            throw new ValidationException("Attribute [modelPath] can't be null or empty.");
        }
    }

}

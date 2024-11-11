/* *****************************************************************************
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

package org.fga.tcc.news;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.fga.tcc.utils.DownloaderUtility;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**-
 * This is a test program that uses word vector and trained network generated by PrepareWordVector.java and TrainNews.java
 * Type or copy/paste news headline from news (indian news channel is preferred as this is what the training data was) and click on Check button
 * and see the predicted category right to the Check button
 * <p>
 * <b></b>KIT Solutions Pvt. Ltd. (www.kitsol.com)</b>
 */
public class TestNews extends JFrame {
    private static WordVectors wordVectors;
    private static TokenizerFactory tokenizerFactory;

    private JLabel jLabel3;
    private JTextArea jTextArea1;
    private static MultiLayerNetwork net;
    private static String dataLocalPath;

    private TestNews() throws Exception {
        initComponents();
        dataLocalPath = DownloaderUtility.NEWSDATA.Download();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        this.setTitle("Predict News Category - KITS");
        JLabel jLabel1 = new JLabel();
        JScrollPane jScrollPane1 = new JScrollPane();
        jTextArea1 = new JTextArea();
        // Variables declaration - do not modify
        JButton jButton1 = new JButton();
        JLabel jLabel2 = new JLabel();
        jLabel3 = new JLabel();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Type News Here");

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jButton1.setText("Check");
        jButton1.addActionListener(this::jButton1ActionPerformed);

        jLabel2.setText("Category");

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel1)
                                .addComponent(jButton1))
                            .addGap(0, 0, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel2)
                            .addGap(18, 18, 18)
                            .addComponent(jLabel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel1)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 134, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton1)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel3)
                        .addComponent(jLabel2))
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        DataSet testNews = prepareTestData(jTextArea1.getText());
        INDArray fet = testNews.getFeatures();
        INDArray predicted = net.output(fet, false);
        long[] arrsiz = predicted.shape();

        File categories = new File(dataLocalPath, "LabelledNews/categories.txt");

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
            jLabel3.setText(labels.get(pos).split(",")[1]);
        } catch (Exception e) {
            System.out.println("File Exception : " + e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception{

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(TestNews.class.getName()).log(Level.SEVERE, null, ex);
        }
        TestNews test = new TestNews();
        test.setVisible(true);

        tokenizerFactory = new DefaultTokenizerFactory();
        tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());
        net = MultiLayerNetwork.load(new File(dataLocalPath,"NewsModel.net"), true);
        wordVectors = WordVectorSerializer.readWord2VecModel(new File(dataLocalPath,"NewsWordVector.txt"));
    }

    // One news story gets transformed into a dataset with one element.
    @SuppressWarnings("DuplicatedCode")
    private static DataSet prepareTestData(String i_news) {
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
            labels.putScalar(new int[]{i, idx, lastIdx - 1}, 1.0);
            labelsMask.putScalar(new int[]{i, lastIdx - 1}, 1.0);
        }

        return new DataSet(features, labels, featuresMask, labelsMask);
    }
}

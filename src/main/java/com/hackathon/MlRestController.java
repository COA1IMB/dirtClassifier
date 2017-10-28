package com.hackathon;

import org.datavec.api.io.labels.ParentPathLabelGenerator;
import org.datavec.api.records.listener.impl.LogRecordListener;
import org.datavec.api.split.FileSplit;
import org.datavec.image.loader.NativeImageLoader;
import org.datavec.image.recordreader.ImageRecordReader;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.Random;


@RestController
@RequestMapping(value = "/ml/")
public class MlRestController
{

   @RequestMapping(value = "/classifyImage", method = RequestMethod.POST)
   double performRecall() throws Exception
   {
      INDArray result = recallDirtyness();
      return result.getDouble(1);
   }
   public static INDArray recallDirtyness() throws Exception {
      double probabilitie = 0.0;
      int rngseed = 123;
      Random randNumGen = new Random(rngseed);
      int height = 100;
      int width = 100;
      int channels = 1;
      File testData = new File("src\\main\\resources\\testCar");
      FileSplit test = new FileSplit(testData, NativeImageLoader.ALLOWED_FORMATS,randNumGen);
      ParentPathLabelGenerator labelMaker = new ParentPathLabelGenerator();
      ImageRecordReader recordReaderTest = new ImageRecordReader(height,width,channels,labelMaker);
      recordReaderTest.initialize(test);
      recordReaderTest.setListeners(new LogRecordListener());
      DataSetIterator testIter = new RecordReaderDataSetIterator(recordReaderTest,1,1,2);

      DataNormalization scaler2 = new ImagePreProcessingScaler(0,1);
      scaler2.fit(testIter);
      testIter.setPreProcessor(scaler2);

      String distribution = null;
      MultiLayerNetwork model = null;

      try {
         model = ModelSerializer.restoreMultiLayerNetwork("NeuralNetwork.zip");
      } catch (Exception e) {
      }

      Evaluation eval = new Evaluation(2);
      DataSet next = testIter.next();
      return model.output(next.getFeatureMatrix());
   }
}


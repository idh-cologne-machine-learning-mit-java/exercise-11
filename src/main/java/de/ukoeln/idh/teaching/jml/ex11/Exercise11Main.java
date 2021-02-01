package de.ukoeln.idh.teaching.jml.ex11;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.records.reader.impl.transform.TransformProcessRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.transform.Transform;
import org.datavec.api.transform.TransformProcess;
import org.datavec.api.transform.analysis.columns.DoubleAnalysis;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.transform.transform.doubletransform.MinMaxNormalizer;
import org.datavec.api.writable.Writable;
import org.datavec.local.transforms.AnalyzeLocal;

public class Exercise11Main {
	
  

	public static void main(String[] args) throws IOException, InterruptedException {
		
		 String[] fileCol = new String[] { "LS", "TO", "VBN", "''", "WP", "UH", "VBG", "JJ", "VBZ", "--", "VBP", "NN", "DT",
			      "PRP", ":", "WP$", "NNPS", "PRP$", "WDT", "(", ")", ".", ",", "``", "$", "RB", "RBR", "RBS", "VBD", "IN",
			      "FW", "RP", "JJR", "JJS", "PDT", "MD", "VB", "WRB", "NNP", "EX", "NNS", "SYM", "CC", "CD", "POS", "class" };
		 
	
		
		String filename = "target/imdb-train.csv";
		FileSplit fs = new FileSplit(new File(filename));
		RecordReader recordReader = new CSVRecordReader();
		recordReader.initialize(fs);
		String wordTag = "VBG";
//
//		while (recordReader.hasNext()) {
//			List<Writable> record = recordReader.next();
//			System.out.println(record);
//		}

		Schema inputDataSchema = new Schema.Builder().addColumnsDouble(fileCol).build();
		
		TransformProcess findMaximumVBGTP = new TransformProcess.Builder(inputDataSchema).build();
		RecordReader findMaximumVBGRR = new TransformProcessRecordReader(recordReader, findMaximumVBGTP);
		
		DoubleAnalysis doubleAnalysis = (DoubleAnalysis) AnalyzeLocal.analyze(inputDataSchema, findMaximumVBGRR).getColumnAnalysis(wordTag);
		double maxVBG = doubleAnalysis.getMaxDouble();
		
		Transform transformMinMax = new MinMaxNormalizer(wordTag, 0.0, maxVBG, 0.0, 1);
		
		TransformProcess removeCol = new TransformProcess.Builder(inputDataSchema).removeColumns("LS", "TO", "''", "WP", "UH", "JJ", "--", "DT",
			      "PRP", ":", "WP$", "NNPS", "PRP$", "WDT", "(", ")", ".", ",", "``", "$", "RB", "RBR", "RBS", "IN",
			      "FW", "RP", "JJR", "JJS", "PDT", "MD", "WRB", "NNP", "EX", "SYM", "CC", "CD", "POS", "class").transform(transformMinMax).build();
				
		recordReader.reset();
		
		RecordReader reader = new TransformProcessRecordReader(recordReader, removeCol);
		
		while (reader.hasNext()) {
			List<Writable> word = reader.next();
			System.out.println(word);
		}
		
		reader.close();
	}

}

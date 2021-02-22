package de.ukoeln.idh.teaching.jml.ex11;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.records.reader.impl.transform.TransformProcessRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.writable.Writable;
import org.datavec.api.transform.TransformProcess;

import org.datavec.api.transform.schema.InferredSchema;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.transform.transform.column.RemoveAllColumnsExceptForTransform;
import org.datavec.api.transform.transform.doubletransform.MinMaxNormalizer;

import org.datavec.api.transform.analysis.DataAnalysis;
import org.datavec.local.transforms.AnalyzeLocal;

public class Exercise11Main {

	public static void main(String[] args) throws IOException, InterruptedException {
		String filename = "target/imdb-train.csv";
		FileSplit fs = new FileSplit(new File(filename));
		RecordReader recordReader = new CSVRecordReader(1, ',');
		recordReader.initialize(fs);

		Schema inputSchema = new InferredSchema(filename).build();

		//In the git of datavec I found a max method, but didn't know how to call it
		//could have been more elegant, efficient to call
		/*DataAnalysis getMaxOfVBG = new AnalyzeLocal().analyze(inputSchema, recordReader);
		System.out.println(getMaxOfVBG.getColumnAnalysis("VBG"));*/

		TransformProcess tp = new TransformProcess.Builder(inputSchema)
									.transform(new RemoveAllColumnsExceptForTransform("VBG","VBN","VBZ","VBP","VBD","VB","NN","NNS"))
									.transform(new MinMaxNormalizer("VBG", 0, 54, 0, 1)).build();

		RecordReader tpr = new TransformProcessRecordReader(recordReader, tp);

		while (tpr.hasNext()) {
			List<Writable> record = tpr.next();
			System.out.println(record);
		}

		recordReader.close();
		tpr.close();
	}

}

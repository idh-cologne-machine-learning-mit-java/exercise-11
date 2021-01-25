package de.ukoeln.idh.teaching.jml.ex11;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.records.reader.impl.transform.TransformProcessRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.transform.TransformProcess;
import org.datavec.api.transform.schema.InferredSchema;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.transform.transform.column.RemoveAllColumnsExceptForTransform;
import org.datavec.api.transform.transform.doubletransform.MinMaxNormalizer;
import org.datavec.api.writable.Writable;

public class Exercise11Main {
	static int numberOfFeatures = 45;
	static int numberOfClasses = 2;

	public static void main(String[] args) throws IOException, InterruptedException {
		String filename = "target/imdb-train.csv";
		FileSplit fs = new FileSplit(new File(filename));
		RecordReader recordReader = new CSVRecordReader(1, ',');
		recordReader.initialize(fs);

		Schema schema = new InferredSchema(filename).build();

		// DataAnalysis da = AnalyzeLocal.analyze(schema, recordReader);
		// System.out.println(da.getColumnAnalysis("VBG"));

		TransformProcess tp = new TransformProcess.Builder(schema)
				.transform(new RemoveAllColumnsExceptForTransform("VBN", "VBG", "VBZ", "VBP", "VBD", "VB", "NN", "NNS"))
				.transform(new MinMaxNormalizer("VBG", 0, 54)).build();

		RecordReader tpr = new TransformProcessRecordReader(recordReader, tp);

		while (tpr.hasNext()) {
			List<Writable> record = tpr.next();
			System.out.println(record);
		}

		recordReader.close();
	}
}

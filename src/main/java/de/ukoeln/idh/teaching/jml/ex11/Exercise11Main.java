package de.ukoeln.idh.teaching.jml.ex11;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.records.reader.impl.transform.TransformProcessRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.transform.TransformProcess;
import org.datavec.api.transform.analysis.columns.DoubleAnalysis;
import org.datavec.api.transform.schema.InferredSchema;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.transform.transform.doubletransform.MinMaxNormalizer;
import org.datavec.api.writable.Writable;
import org.datavec.local.transforms.AnalyzeLocal;

public class Exercise11Main {

	public static void main(String[] args) throws IOException, InterruptedException {
		String filename = "target/imdb-train.csv";
		FileSplit fs = new FileSplit(new File(filename));
		RecordReader recordReader = new CSVRecordReader(1, ',');
		recordReader.initialize(fs);

		Schema inputDataSchema = new InferredSchema(filename).build();
		
		String col = "VBG";
		
		DoubleAnalysis da = (DoubleAnalysis) AnalyzeLocal.analyze(inputDataSchema, recordReader)
				.getColumnAnalysis(col);
		
		double min = 0;
		double max = da.getMaxDouble();
		
		TransformProcess tp = new TransformProcess.Builder(inputDataSchema)
				/*step 3.1*/
				.removeAllColumnsExceptFor(col, "VBN", "VBZ", "VBP", "VBD", "VB", "NN", "NNS")
				/*step 3.2*/
				.transform(new MinMaxNormalizer(col, min, max, min, 1)).build();		
				
		recordReader.reset();
		RecordReader tpr = new TransformProcessRecordReader(recordReader, tp);
		
		while (tpr.hasNext()) {
			List<Writable> record = tpr.next();
			System.out.println(record);
		}

		tpr.close();
	}

}

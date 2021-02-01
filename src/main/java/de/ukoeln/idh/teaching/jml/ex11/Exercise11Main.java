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

	public static void main(String[] args) throws IOException, InterruptedException {
		String filename = "target/imdb-train.csv";
		FileSplit fs = new FileSplit(new File(filename));
		RecordReader recordReader = new CSVRecordReader(1, ',');
		recordReader.initialize(fs);
		
		// delimiter kann nicht gesetzt werden (weil nur in Kombi mit DataType und DataType ist private)
		Schema inputDataSchema = new InferredSchema(filename).build();
		
		// array of POS-columns to keep in dataset
		String[] columnsToKeep = new String[] {"VBG", "VBN", "VBZ", "VBP", "VBD", "VB", "NN", "NNS"};
		
//		ColumnAnalysis vbgAnalysis = AnalyzeLocal.analyze(inputDataSchema, recordReader).getColumnAnalysis("VBG");
//		System.out.println(vbgAnalysis);
		double max = 54d; // manually with columnAnalysis ...
		
		
		// remove all undefined columns
		RemoveAllColumnsExceptForTransform filterTransformer = new RemoveAllColumnsExceptForTransform(columnsToKeep);
		// normalize range of VGB from 0-54 to 0-1
		MinMaxNormalizer normalizeTransformer = new MinMaxNormalizer("VBG", 0, max, 0, 1);
		TransformProcess tp = new TransformProcess.Builder(inputDataSchema)
				.transform(filterTransformer)
				.transform(normalizeTransformer)
				.build();
		// apply transformer
		RecordReader tpr = new TransformProcessRecordReader(recordReader, tp);

		// print out data
		while (tpr.hasNext()) {
			List<Writable> record = tpr.next();
			System.out.println(record);
		}

		tpr.close();
		recordReader.close();
	}

}

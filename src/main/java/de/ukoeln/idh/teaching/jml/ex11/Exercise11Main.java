package de.ukoeln.idh.teaching.jml.ex11;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.records.reader.impl.transform.TransformProcessRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.writable.Writable;

import org.datavec.api.transform.Transform;
import org.datavec.api.transform.TransformProcess;
import org.datavec.api.transform.analysis.DataAnalysis;
import org.datavec.api.transform.analysis.columns.DoubleAnalysis;
import org.datavec.api.transform.schema.InferredSchema;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.transform.transform.doubletransform.MinMaxNormalizer;
import org.datavec.local.transforms.AnalyzeLocal;


public class Exercise11Main {

	public static void main(String[] args) throws IOException, InterruptedException {
		String filename = "target/imdb-train.csv";
		FileSplit fs = new FileSplit(new File(filename));
		RecordReader recordReader = new CSVRecordReader(1, ',');
		recordReader.initialize(fs);

		Schema dataSchema = new InferredSchema(filename).build();
		double minVBG = 0.0;
		double maxVBG = getMax(dataSchema, recordReader, "VBG");

		Transform scaleVBG = new MinMaxNormalizer("VBG", minVBG, maxVBG, 0.0, 1.0);

		TransformProcess transformer = new TransformProcess.Builder(dataSchema)
				.removeAllColumnsExceptFor("VBG", "VBN", "VBZ", "VBP", "VBD", "VB", "NN", "NNS")
				.transform(scaleVBG)
				.build();

		recordReader.reset();
		RecordReader transformReader = new TransformProcessRecordReader(recordReader, transformer);

		while (recordReader.hasNext()) {
			List<Writable> record = transformReader.next();
			System.out.println(record);
		}
		transformReader.close();
	}

	public static double getMax(Schema dataSchema, RecordReader recordReader, String Column) {
		DataAnalysis dataAnalysis = AnalyzeLocal.analyze(dataSchema, recordReader);
		DoubleAnalysis maxAnalysis = (DoubleAnalysis) dataAnalysis.getColumnAnalysis(Column);
		return maxAnalysis.getMaxDouble();
	}
}

package de.ukoeln.idh.teaching.jml.ex11;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.writable.Writable;
import org.datavec.api.records.reader.impl.transform.TransformProcessRecordReader;
import org.datavec.api.transform.Transform;
import org.datavec.api.transform.TransformProcess;
import org.datavec.api.transform.analysis.columns.DoubleAnalysis;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.transform.transform.doubletransform.MinMaxNormalizer;
import org.datavec.local.transforms.AnalyzeLocal;


public class Exercise11Main {

	static String[] columns = new String[] { "LS", "TO", "VBN", "''", "WP", "UH", "VBG", "JJ", "VBZ", "--", "VBP", "NN", "DT",
	"PRP", ":", "WP$", "NNPS", "PRP$", "WDT", "(", ")", ".", ",", "``", "$", "RB", "RBR", "RBS", "VBD", "IN",
	"FW", "RP", "JJR", "JJS", "PDT", "MD", "VB", "WRB", "NNP", "EX", "NNS", "SYM", "CC", "CD", "POS", "class" };

	public static void main(String[] args) throws IOException, InterruptedException {
		String filename = "target/imdb-train.csv";
		FileSplit fs = new FileSplit(new File(filename));
		RecordReader recordReader = new CSVRecordReader(1, ',');
		recordReader.initialize(fs);

		Schema schema = new Schema.Builder().addColumnsDouble(columns).build();

		TransformProcess maximumVBG = new TransformProcess.Builder(schema).build();
		RecordReader ReaderFindMaximumVBG = new TransformProcessRecordReader(recordReader, maximumVBG);

		DoubleAnalysis DoubleAnalysis = (DoubleAnalysis) AnalyzeLocal.analyze(schema, ReaderFindMaximumVBG).getColumnAnalysis("VBG");
		double maxVBGValue = DoubleAnalysis.getMaxDouble();

		Transform MinMaxVBG = new MinMaxNormalizer("VBG", 0.0, maxVBGValue, 0.0, 1.0);

		TransformProcess tp = new TransformProcess.Builder(schema).removeAllColumnsExceptFor("VBG", "VBN", "VBZ", "VBP", "VBD", "VB", "NN", "NNS")
				.transform(MinMaxVBG)
				.build();

		recordReader.reset();

		RecordReader reader = new TransformProcessRecordReader(recordReader, tp);

		while (reader.hasNext()) {
			List<Writable> record = reader.next();
			System.out.println(record);
		}

		reader.close();
	}

}

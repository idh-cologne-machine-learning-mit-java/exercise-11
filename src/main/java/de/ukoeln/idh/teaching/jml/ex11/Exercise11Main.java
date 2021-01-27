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
  
  static String[] cols = new String[] { "LS", "TO", "VBN", "''", "WP", "UH", "VBG", "JJ", "VBZ", "--", "VBP", "NN", "DT",
      "PRP", ":", "WP$", "NNPS", "PRP$", "WDT", "(", ")", ".", ",", "``", "$", "RB", "RBR", "RBS", "VBD", "IN",
      "FW", "RP", "JJR", "JJS", "PDT", "MD", "VB", "WRB", "NNP", "EX", "NNS", "SYM", "CC", "CD", "POS", "class" };

	public static void main(String[] args) throws IOException, InterruptedException {
		String filename = "target/imdb-train.csv";
		FileSplit fs = new FileSplit(new File(filename));
		CSVRecordReader reader = new CSVRecordReader();
		reader.initialize(fs);
		
    Schema inputSchema = new Schema.Builder().addColumnsDouble(cols).build();

    // find max value for VBG col
    TransformProcess tpFindMaxVBG = new TransformProcess.Builder(inputSchema).build();
    RecordReader rrFindMaxVBG = new TransformProcessRecordReader(reader, tpFindMaxVBG);
    DoubleAnalysis da = (DoubleAnalysis) AnalyzeLocal.analyze(inputSchema, rrFindMaxVBG).getColumnAnalysis("VBG");
    double maxVBG = da.getMaxDouble();
    
    Transform vbgMinMaxTransform = new MinMaxNormalizer("VBG", 0.0, maxVBG, 0.0, 1.0);
    
    TransformProcess tp = new TransformProcess.Builder(inputSchema)
        .removeAllColumnsExceptFor("VBG", "VBN", "VBZ", "VBP", "VBD", "VB", "NN", "NNS")
        .transform(vbgMinMaxTransform)  // I had to remove the first line of the CSV for this transform to work, the process tried to transform the col header strings
        .build();
    
    reader.reset();
    RecordReader rr = new TransformProcessRecordReader(reader, tp);
    

		while (rr.hasNext()) {
			List<Writable> record = rr.next();
			System.out.println(record);
		}

		rr.close();
		

	}

}

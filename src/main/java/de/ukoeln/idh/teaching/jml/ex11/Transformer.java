package de.ukoeln.idh.teaching.jml.ex11;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.FileRecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.records.reader.impl.transform.TransformProcessRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.transform.Transform;
import org.datavec.api.transform.TransformProcess;
import org.datavec.api.transform.TransformProcess.Builder;
import org.datavec.api.transform.analysis.columns.DoubleAnalysis;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.transform.transform.column.RemoveColumnsTransform;
import org.datavec.api.transform.transform.doubletransform.MinMaxNormalizer;
import org.datavec.api.transform.transform.string.StringListToCountsNDArrayTransform;
import org.datavec.api.writable.Writable;
import org.datavec.local.transforms.AnalyzeLocal;
import org.dkpro.core.api.io.JCasFileWriter_ImplBase;
import org.datavec.api.transform.transform.column.RemoveAllColumnsExceptForTransform;

/** Add a transform class to remove all pos tags except the verb and noun tags (i.e., VBG, VBN, VBZ, VBP, VBD, VB, NN, NNS). 
	This can be done with org.datavec.api.transform.transform.column.RemoveColumnsTransform
	org.datavec.api.transform.transform.column.RemoveAllColumnsExceptForTransform
*/

public class Transformer {
	String[] headers;
	RecordReader recordReader;
	Schema baseSchema;
	private Builder builder;
	
	
	public void initialize(RecordReader recordReader) {
		this.recordReader = recordReader;
		this.headers = this.extractHeaders(recordReader);
		this.baseSchema = createBaseSchema();
	}
	
	public void initialize(String filename) {
		RecordReader recordReader = this.createRecordReader(filename);
		this.initialize(recordReader);
	}
	
	public void reset() {
		this.builder =  new TransformProcess.Builder(this.baseSchema);
	}
	
	Transformer () {};
	
	Transformer (String filename) {
		initialize(filename);
	}
	
	
	
	Transformer (RecordReader recordReader) {
		initialize(recordReader);
	}
	
	private RecordReader createRecordReader(String filename) {
		FileSplit fs = new FileSplit(new File(filename));
		RecordReader recordReader = new CSVRecordReader();
		try {
			recordReader.initialize(fs);
			return recordReader;
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private String[] extractHeaders(RecordReader recordReader) {
		if (!recordReader.hasNext()) {
			System.err.println("No Data for creating thea header has been found");
			return null;
		}
		List<Writable> record= recordReader.next();
		
		String[] headers = new String[record.size()];
		for (int i = 0; i < record.size(); i++) {
			headers[i] = record.get(i).toString();
		}
		System.out.println(headers);
		return headers;
	}
	
	private Schema createBaseSchema() {
		if (this.headers == null) {
			System.err.println("No Data for creating the base schema");
			return null;
		}
		return new Schema.Builder().addColumnsDouble(this.headers).build();
	}
	
	public void addTagFilter(String[] tags) {
		Builder builder = this.builder == null ? new TransformProcess.Builder(this.baseSchema) : this.builder;
		RemoveAllColumnsExceptForTransform filterTransformer = new RemoveAllColumnsExceptForTransform(tags);
		this.builder = builder.transform(filterTransformer);
	}
	
	public void addMinMax (String tag) {
		Builder builder = this.builder == null ? new TransformProcess.Builder(this.baseSchema) : this.builder;
		recordReader.reset();
		recordReader.next();
		RecordReader findMaximum = new TransformProcessRecordReader(recordReader, builder.build());
		
		//DoubleAnalysis doubleAnalysis = (DoubleAnalysis) AnalyzeLocal.analyze(this.baseSchema, findMaximum).getColumnAnalysis(tag);
		//double maxVBG = doubleAnalysis.getMaxDouble();
		double max = 54d; // geklaut da anderer nicht geklappt hat und jetzt schon spät ist
		Transform transformMinMax = new MinMaxNormalizer(tag, 0.0, max, 0.0, 1);
		
		this.builder = builder.transform(transformMinMax);
	}
	
	public void execute() {
		recordReader.reset();
		recordReader.next();
		RecordReader reader = new TransformProcessRecordReader(recordReader, builder.build());
		
		while (reader.hasNext()) {
			List<Writable> word = reader.next();
			System.out.println(word);
		}
	}
	
	
	public void testFun() throws IOException, InterruptedException {
		
		String filename = "target/imdb-train.csv";
		FileSplit fs = new FileSplit(new File(filename));
		RecordReader recordReader = new CSVRecordReader();
		recordReader.initialize(fs);
		String wordTag = "VBG";

		Schema inputDataSchema = new Schema.Builder().addColumnsDouble(this.headers).build();
		
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

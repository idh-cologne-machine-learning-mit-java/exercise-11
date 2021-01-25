package de.ukoeln.idh.teaching.jml.ex11.demo;

import java.io.File;
import java.util.List;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.FileRecordReader;
import org.datavec.api.records.reader.impl.transform.TransformProcessRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.transform.TransformProcess;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.transform.transform.string.StringListToCountsNDArrayTransform;
import org.datavec.api.writable.Writable;

public class S11Main {

	public static void main(String[] args) throws Exception {
		FileSplit fileSplit = new FileSplit(new File("src/main/resources/sherlock"));
		FileRecordReader reader = new FileRecordReader();
		reader.initialize(fileSplit);

		Schema inputDataSchema = new Schema.Builder().addColumnString("text").build();

		List<String> vocab = StringListToCountsNDArrayTransform.readVocabFromFile("src/main/resources/vocabulary.txt");

		TransformProcess tp = new TransformProcess.Builder(inputDataSchema)
				.transform(new StringListToCountsNDArrayTransform("text", vocab, " ", false, true)).build();

		RecordReader tpr = new TransformProcessRecordReader(reader, tp);

		while (tpr.hasNext()) {
			List<Writable> ds = tpr.next();
			System.out.println(ds.size());
			System.out.println(ds.get(0));
		}
		tpr.close();

	}

}

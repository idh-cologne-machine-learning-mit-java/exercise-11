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
import org.datavec.api.transform.schema.Schema.Builder;
import org.datavec.api.transform.transform.column.RemoveAllColumnsExceptForTransform;
import org.datavec.api.transform.transform.column.RemoveColumnsTransform;
import org.datavec.api.transform.transform.doubletransform.MinMaxNormalizer;
import org.datavec.api.writable.Writable;

public class Exercise11Main {

	public static void main(String[] args) throws IOException, InterruptedException {
		String filename = "target/imdb-train.csv";
		Transformer transformer = new Transformer(filename);
		
		String[] tags2Remove = {
			"VBG", "VBN", "VBZ", "VBP", "VBD", "VB", "NN", "NNS"
		};
		
		String countTag = "VBG";

		transformer.addTagFilter(tags2Remove);
		transformer.addMinMax(countTag);
		
		transformer.execute();
		

	}

}

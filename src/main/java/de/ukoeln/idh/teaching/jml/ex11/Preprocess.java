package de.ukoeln.idh.teaching.jml.ex11;

import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.dkpro.core.corenlp.CoreNlpPosTagger;
import org.dkpro.core.io.text.TextReader;
import org.dkpro.core.tokit.BreakIteratorSegmenter;

public class Preprocess {

	public static void main(String[] args) throws Exception {
		CollectionReaderDescription crd = CollectionReaderFactory.createReaderDescription(TextReader.class,
				TextReader.PARAM_LANGUAGE, "en", TextReader.PARAM_SOURCE_LOCATION, "src/main/resources/imdb/*");

		SimplePipeline.runPipeline(crd, AnalysisEngineFactory.createEngineDescription(BreakIteratorSegmenter.class),
				AnalysisEngineFactory.createEngineDescription(CoreNlpPosTagger.class), AnalysisEngineFactory
						.createEngineDescription(CSVWriter.class, CSVWriter.PARAM_TARGET_LOCATION, "target"));
	}

}

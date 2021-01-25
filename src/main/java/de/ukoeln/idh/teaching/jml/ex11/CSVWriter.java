package de.ukoeln.idh.teaching.jml.ex11;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.core.api.io.JCasFileWriter_ImplBase;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;

public class CSVWriter extends JCasFileWriter_ImplBase {

	CSVPrinter csvPrinter;

	String[] posTags = new String[] { "LS", "TO", "VBN", "''", "WP", "UH", "VBG", "JJ", "VBZ", "--", "VBP", "NN", "DT",
			"PRP", ":", "WP$", "NNPS", "PRP$", "WDT", "(", ")", ".", ",", "``", "$", "RB", "RBR", "RBS", "VBD", "IN",
			"FW", "RP", "JJR", "JJS", "PDT", "MD", "VB", "WRB", "NNP", "EX", "NNS", "SYM", "CC", "CD", "POS" };

	int items = 0;

	@Override
	public void initialize(final UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		try {
			csvPrinter = new CSVPrinter(new FileWriter(new File(getTargetLocation(), "imdb-test.csv")),
					CSVFormat.DEFAULT);
			for (String tag : posTags) {
				csvPrinter.print(tag);

			}
			csvPrinter.print("class");
			csvPrinter.println();
			csvPrinter.flush();
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
	}

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		if (items == 500) {
			try {
				csvPrinter.flush();
				csvPrinter.close();
				csvPrinter = new CSVPrinter(new FileWriter(new File(getTargetLocation(), "imdb-train.csv")),
						CSVFormat.DEFAULT);
				for (String tag : posTags) {
					csvPrinter.print(tag);

				}
				csvPrinter.print("class");
				csvPrinter.println();

			} catch (IOException e) {
				throw new AnalysisEngineProcessException(e);
			}
		}

		Map<String, Integer> posFreqs = new HashMap<String, Integer>();

		for (POS pos : aJCas.select(POS.class)) {
			String posValue = pos.getPosValue();
			if (posFreqs.containsKey(posValue)) {
				posFreqs.put(posValue, posFreqs.get(posValue) + 1);
			} else
				posFreqs.put(posValue, 1);
		}

		try {
			for (String tag : posTags) {
				csvPrinter.print(posFreqs.getOrDefault(tag, 0));
			}

			String text = aJCas.getDocumentText();
			char lastChar = text.charAt(text.length() - 2);
			csvPrinter.print(lastChar == '1' ? 1 : 0);
			csvPrinter.println();
			csvPrinter.flush();
			items++;
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		}

	}

	@Override
	public void collectionProcessComplete() throws AnalysisEngineProcessException {
		super.collectionProcessComplete();

		try {
			csvPrinter.flush();
			csvPrinter.close();
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

}

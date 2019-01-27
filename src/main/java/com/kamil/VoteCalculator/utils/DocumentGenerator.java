package com.kamil.VoteCalculator.utils;

import com.kamil.VoteCalculator.model.candidate.CandidateTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@Scope("singleton")
public class DocumentGenerator {

	private final Logger logger = LoggerFactory.getLogger(DocumentGenerator.class);
	private FileWriter writer = null;

	public PDFCreator createPDF(File file) {
		Optional.ofNullable(file).orElseThrow(() -> new RuntimeException("null file"));
		return new PDFCreator(file);
	}

	private FileWriter getFileWriter() {
		return writer;
	}

	public void saveCandidatesToCSV(File file, List<CandidateTable> data) {
		File f = Optional.ofNullable(file).orElseThrow(() -> new RuntimeException("null file"));

		try {
			writer = new FileWriter(file);
			CSVUtils.writeLine(writer, Arrays.asList("name", "party", "votes"));
			data.forEach(c -> {
				try {
					CSVUtils.writeLine(getFileWriter(),
							Arrays.asList(c.getName(), c.getParty(), String.valueOf(c.getVotes())));
				} catch (IOException e) {
					logger.error("ToCSV", e);
				}
			});
			writer.flush();
			writer.close();
		} catch (IOException e) {
			logger.error("ToCSV", e);
		}

	}
}

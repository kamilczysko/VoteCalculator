package com.kamil.VoteCalculator.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.kamil.VoteCalculator.model.candidate.CandidateTable;
import com.kamil.VoteCalculator.model.party.PartyTable;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.Chart;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.List;

public class PDFCreator extends Document {

	private WritableImage chartDimensions;
	private final float width = PageSize.A4.getWidth() * 0.85f;
	private final Font headerFont = FontFactory.getFont(FontFactory.COURIER_BOLD, 20, BaseColor.BLACK);
	private final Font summaryFont = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
	private final Font tipFont = FontFactory.getFont(FontFactory.COURIER, 8, BaseColor.DARK_GRAY);

	public PDFCreator(File file) {
		try {
			PdfWriter.getInstance(this, new FileOutputStream(file));
			this.open();
		} catch (DocumentException e) {

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private PDFCreator() {
	}

	private PDFCreator(Rectangle pageSize) {
		super(pageSize);
	}

	private PDFCreator(Rectangle pageSize, float marginLeft, float marginRight, float marginTop, float marginBottom) {
		super(pageSize, marginLeft, marginRight, marginTop, marginBottom);
	}

	public PDFCreator prepDocument() {
		return this;
	}

	public PDFCreator chart(Chart chart) throws IOException, DocumentException {
		Image snapshot = getSnapshot(chart);
		add(snapshot);
		return this;
	}

	private Image getSnapshot(Chart chart) throws IOException, BadElementException {

		setChartDimension((int) chart.getWidth(), (int) chart.getHeight());
		SnapshotParameters params = new SnapshotParameters();
		WritableImage snapshot = chart.snapshot(params, chartDimensions);
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", byteOutput);

		Image image = com.itextpdf.text.Image.getInstance(byteOutput.toByteArray());

		image.scaleAbsoluteWidth(width);
		float height = image.getHeight() * 0.85f;
		image.scaleToFit(width, height);

		return image;
	}

	private void setChartDimension(int width, int height) {
		chartDimensions = new WritableImage(width, height);
	}

	public PDFCreator tableWithCandidates(List<CandidateTable> candidates, String... headers) throws DocumentException {
		PdfPTable table = new PdfPTable(headers.length);

		for (String header : headers) {
			PdfPCell h = new PdfPCell();
			h.setBackgroundColor(BaseColor.LIGHT_GRAY);
			h.setBorderWidth(2);
			h.setPhrase(new Phrase(header));
			table.addCell(h);
		}

		addCandidatesToTable(candidates, table);

		table.setSpacingBefore(10);
		table.setSpacingAfter(10);

		add(table);

		return this;
	}

	public PDFCreator tableOfParties(java.util.List<PartyTable> parties, String... headers) throws DocumentException {
		PdfPTable table = new PdfPTable(headers.length);

		for (String header : headers) {
			PdfPCell h = new PdfPCell();
			h.setBackgroundColor(BaseColor.LIGHT_GRAY);
			h.setBorderWidth(2);
			h.setPhrase(new Phrase(header));
			table.addCell(h);
		}

		addPartiesToTable(parties, table);

		table.setSpacingBefore(10);
		table.setSpacingAfter(10);

		add(table);

		return this;
	}

	public PDFCreator keyIntValTable(int[] vals, String[] keys) throws DocumentException {
		PdfPTable table = null;

		if (keys.length == vals.length) {
			table = new PdfPTable(keys.length);
			for (int i = 0; i < keys.length; i++) {
				table.addCell(keys[i]);
				table.addCell(String.valueOf(vals[i]));
			}
		}

		table.setSpacingBefore(10);
		table.setSpacingAfter(10);

		add(table);

		return this;
	}

	private void addPartiesToTable(java.util.List<PartyTable> data, PdfPTable table) {
		data.forEach(p -> {
			table.addCell(p.getName());
			table.addCell(String.valueOf(p.getVotes()));
		});
	}

	private void addCandidatesToTable(List<CandidateTable> data, PdfPTable table) {
		data.forEach(c -> {
			table.addCell(c.getName());
			table.addCell(c.getParty());
			table.addCell(String.valueOf(c.getVotes()));
		});
	}

	public PDFCreator subHeader(String text) throws DocumentException {
		add(new Paragraph(text, summaryFont));
		return this;
	}

	public PDFCreator addTipText(String text) throws DocumentException {
		add(new Chunk(text, tipFont));
		return this;
	}

	public PDFCreator mainHeader(String text) throws DocumentException {
		add(new Paragraph(text, headerFont));
		return this;
	}

	public PDFCreator nextPage() throws DocumentException {
		add(Chunk.NEXTPAGE);
		return this;
	}

	public void finishDocument() {
		close();
	}
}

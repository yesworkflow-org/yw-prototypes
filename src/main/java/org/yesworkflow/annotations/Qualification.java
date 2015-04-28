package org.yesworkflow.annotations;

import org.yesworkflow.extract.SourceLine;

public class Qualification extends Annotation {

	public Qualification(SourceLine line, String comment, String expectedTag) throws Exception {
		super(line, comment, expectedTag);
	}
}

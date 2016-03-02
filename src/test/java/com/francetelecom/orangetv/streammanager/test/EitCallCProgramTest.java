package com.francetelecom.orangetv.streammanager.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.francetelecom.orangetv.streammanager.server.util.EitCallCProgram;
import com.francetelecom.orangetv.streammanager.server.util.EitJsonParser;
import com.francetelecom.orangetv.streammanager.shared.model.EitInfoModel;


/**
 * Construit un objet EitInfoModel à partir d'un json/string.
 * Appel le program C eitinjector pour injecter ces eit dans un flux.
 * @author sylvie
 *
 */
public class EitCallCProgramTest  implements IEitJsonParserTest {

	private static EitCallCProgram eitCallCProgram = EitCallCProgram.get();

	@Test
	public void testSendEit() {
		
		
		EitInfoModel eitInfoModel = EitJsonParser.parse(json);
		boolean result = eitCallCProgram.sendEit(eitInfoModel);
		assertTrue("unexpected result!", result);
	}
}

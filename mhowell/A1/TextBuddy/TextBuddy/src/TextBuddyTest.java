import static org.junit.Assert.*;

import java.util.List;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TextBuddyTest {
	//Standard console response strings
	private static final String MESSAGE_WELCOME_SCREEN = "\n<Welcome to TextBuddy by Morgan Howell!>\n"
				+ "          ---------------------           \n"
				+ "Changes will be saved to \"%1$s\"\n"
				+ "Type \"help\" for a list of available commands\n\n";
	private static final String MESSAGE_HELP_GUIDE = "\n   ---------------HELP GUIDE---------------\n"
				+ "  |                                        |\n" 
				+ "  | add ANY_TEXT_CAN_FOLLOW: adds text     |\n"
				+ "  | delete LINE_NUMBER: removes that entry |\n"
				+ "  | clear: removes all entries             |\n"
				+ "  | display: shows all entries             |\n"
				+ "  | exit: terminates the program           |\n"
				+ "   ----------------------------------------\n\n";
	private static final String MESSAGE_DISPLAY_TEMPLATE = "\n------------LIST FOR \"%1$s\"------------\n"
				+ "%2$s"
				+ "--------------------------------------------------------\n\n";
	private static final String MESSAGE_IO_EXCEPTION = "\nWe encountered an IOException while attempting to open the given file.\n";
	private static final String MESSAGE_UNSUPPORTED_COMMAND = "\nYou've attempted an unsupported command. Issue command 'help' for details.\n";
	private static final String MESSAGE_ABSENT_COMMAND = "\nPlease provide a command and press enter. Issue command 'help' for a list of valid commands.\n";
	private static final String MESSAGE_WRONG_FILEOUT = "\nMalformed Request: Supplied parameter does not follow expected format of (*.%1$s).\n";
	private static final String MESSAGE_WRONG_NUM_PARAMS = "\nMalformed Request: Please provide the correct number of parameters.\n";
	private static final String MESSAGE_SENTENCE_ADDED = "\nAdded to %1$s: \"%2$s\"\n";
	private static final String MESSAGE_BLANK_LINE_ATTEMPT = "\nA blank line was added to %1$s. Please consider adding something more meaningful next time.\n";
	private static final String MESSAGE_DELETE_TYPE_ERROR = "\nPlease provide a valid line number to be deleted.\n";
	private static final String MESSAGE_SENTENCE_DELETED = "\nDeleted from %1$s: \"%2$s\"\n";
	private static final String MESSAGE_MEMORY_CLEARED = "All content cleared from %1$s.";
	private static final String MESSAGE_FILE_EMPTY = "\n%1$s is empty.\n";
	private static final String MESSAGE_EXIT = "\nThank you for using TextBuddy by Morgan Howell!\n"
											+ "Your additions were saved to %1$s\n";
	private static final String MESSAGE_COMMAND_PROMPT = "%1$s>$ ";
	//Add supported extensions for output file below (regex tested).
	private static final String SUPPORTED_EXTENSIONS = "txt|md|rtf";
	private static final String REGEX_EXTENSION_TEST = "\\w+\\.(" + SUPPORTED_EXTENSIONS + ")";
	
	public enum CommandIssue { 
		HELP, ADD_ITEM, DELETE_ITEM, CLEAR, DISPLAY, EMPTY, UNSUPPORTED, EXIT
	}
	
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
	protected TextBuddy buddy;

	@Before
	public void setUp(){
		buddy = TextBuddy.generateBuddyHelper("samplefile.txt");
	    System.setOut(new PrintStream(outContent));
	    System.setErr(new PrintStream(errContent));
	    System.setIn(System.in);
	}
	
	@Test
	public void testArgumentSanitizationInvalidNumberOfArgs() {
		String[] args = {"1","2","3","4"};
		try {
			TextBuddy.sanitizeArgs(args);
		} catch(UnsupportedOperationException err) {
			System.err.print(err.getMessage());
		}
		assertEquals(MESSAGE_WRONG_NUM_PARAMS, errContent.toString());
	}
	
	
	@Test
	public void testArgumentSanitizationInvalidExtension() {
		String[] args = {"innocuous.VIRUS"};
		try {
			TextBuddy.sanitizeArgs(args);
		} catch(UnsupportedOperationException err) {
			System.err.print(err.getMessage());
		}
		assertEquals(String.format(MESSAGE_WRONG_FILEOUT, SUPPORTED_EXTENSIONS), errContent.toString());
	}
	
	@Test
	public void testUserWelcomePrompt() {
		buddy.welcomeUser();
		assertEquals(String.format(MESSAGE_WELCOME_SCREEN, "samplefile.txt"), 
				outContent.toString());	
	}
	
	@Test
	public void testCommandDisplay() {
		buddy.displayCommandPrompt();
		assertEquals(String.format(MESSAGE_COMMAND_PROMPT, "samplefile.txt"), 
				outContent.toString());	
	}
	
	@Test
	public void testAddEventFeature() {
		buddy.addText("sample text being added");
		List<String> items = buddy.getItems();
		assertEquals(items.get(0), "sample text being added");	
	} 
	
	@Test
	public void testDisplayingEvents() {
		buddy.addText("sample text being added1");
		buddy.addText("sample text being added2");
		buddy.addText("sample text being added3");
		buddy.getText();
		String sampleDisplay = String.format(MESSAGE_DISPLAY_TEMPLATE,
				"samplefile.txt",
				"\n1. sample text being added1\n"
				+ "\n2. sample text being added2\n"
				+ "\n3. sample text being added3\n"							
				);
		String sampleAdd1 = "\nAdded to samplefile.txt: \"sample text being added1\"\n\n";
		String sampleAdd2 = "\nAdded to samplefile.txt: \"sample text being added2\"\n\n";
		String sampleAdd3 = "\nAdded to samplefile.txt: \"sample text being added3\"\n\n";
		String totalSampleDisplay = sampleAdd1 + sampleAdd2 + sampleAdd3 + sampleDisplay;
		assertEquals(totalSampleDisplay, outContent.toString());
	}
	
	@Test
	public void testDeletingEvents() {
		buddy.addText("sample text being added1");
		buddy.addText("sample text being added2");
		buddy.addText("sample text being added3");
		buddy.deleteLine("2");
		List<String> items = buddy.getItems();
		assertEquals(items.get(1), "sample text being added3");
	}
	
	@Test
	public void testShowHelpMenu() {
		buddy.showHelpGuider();
		assertEquals(MESSAGE_HELP_GUIDE, outContent.toString());
	}
	
	@Test
	public void testClearingList() {
		buddy.addText("sample text being added1");
		buddy.addText("sample text being added2");
		buddy.addText("sample text being added3");
		buddy.clearText();
		List<String> items = buddy.getItems();
		assertEquals(items.isEmpty(), true);
		assertEquals(items.isEmpty(), true);
		String sampleOutput = lastLineOfSeries(outContent.toString().trim());
		assertEquals(String.format(MESSAGE_MEMORY_CLEARED.toString(), "samplefile.txt"), 
				sampleOutput.trim().toString());	
	}
	
	private String lastLineOfSeries(String paragraph) {
		return paragraph.substring(paragraph.lastIndexOf("\n"));
	}
	
	@Test
	public void testDisplayAfterClear() {
		buddy.addText("sample text being added1");
		buddy.addText("sample text being added2");
		buddy.addText("sample text being added3");
		buddy.clearText();
		buddy.getText();
		
	}
	
	@Test
	public void testDeleteAfterClear() {
		
	}
	
	@Test
	public void testExitMessage() {
		
	}
	
	@Test
	public void testTriggerDeleteError() {
		
	}
	
	@Test
	public void testTriggerAddError() {
		
	}
	
	
	@After
	public void cleanUpStreams() {
	    System.setOut(null);
	    System.setErr(null);
	}
	
	private void spoofSystemInput(String input) {
		try {
			System.setIn(new ByteArrayInputStream(input.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
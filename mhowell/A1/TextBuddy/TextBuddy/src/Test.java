import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

	public static void main(String[] args) {
		TextBuddy buddy = TextBuddy.generateBuddyHelper("samplefile.txt");
		String REGEX_NUMBER_LOOKBEHIND = "(?<=[0-9]+\\. )\\w+";
		Pattern PATTERN_CLEAN_LEAD_NUMBER = Pattern.compile(REGEX_NUMBER_LOOKBEHIND);
		Matcher match = PATTERN_CLEAN_LEAD_NUMBER.matcher("1. sdfsdfsdf");
		match.find();
		System.out.println(match.group(0));

	}

}
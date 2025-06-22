package pro.trevor.joe.program;

public class StringUtil {
    public static String escape(String string) {
        StringBuilder sb = new StringBuilder();
        boolean shouldEscape = false;
        for (int i = 0; i < string.length(); ++i) {
            char c = string.charAt(i);

            if (c == '\\' && !shouldEscape) {
                shouldEscape = true;
                continue;
            }

            if (shouldEscape) {
                switch (c) {
                    case '\\' -> sb.append('\\');
                    case '"' -> sb.append("\"");
                    case 'b' -> sb.append("\b");
                    case 'f' -> sb.append("\f");
                    case 'n' -> sb.append("\n");
                    case 'r' -> sb.append("\r");
                    case 't' -> sb.append("\t");
                    default -> sb.append('\\').append(c);
                }
                shouldEscape = false;
            } else {
                sb.append(c);
            }
        }
        System.out.println(sb.toString());
        return sb.toString();
    }
}

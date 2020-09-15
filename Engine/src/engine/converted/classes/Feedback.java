package engine.converted.classes;

import java.util.Map;

public class Feedback {
    protected int rank;
    protected String content="";
    protected String writer;

    public Feedback(String content, int rank, String writer) {
        if (!content.equals(""))
            this.content = content;
        this.rank = rank;
        this.writer = writer;
    }

    public int getRank() {
        return rank;
    }

    public String getContent() {
        return content;
    }

    public String getWriter() {
        return writer;
    }

    public String toString() {
        String toReturn = "Pooler: " + writer + "\nRank:" + rank + "\n";
        if (!content.equals(""))
            toReturn += ("Content: " + content + "\n");

        toReturn+=("--------------------\n");

        return toReturn;
    }
}

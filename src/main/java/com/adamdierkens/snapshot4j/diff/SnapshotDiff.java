package com.adamdierkens.snapshot4j.diff;

import difflib.Delta;
import difflib.DiffUtils;
import org.fusesource.jansi.Ansi;

import java.util.List;

public abstract class SnapshotDiff {

    private StringBuilder appendLines(StringBuilder builder, List<String> lines, String prefix, Ansi.Color color) {
        String _prefix = "";
        for (String line : lines) {
            builder.append(_prefix);
            builder.append(Ansi.ansi().fg(color).a(prefix).a(line).reset().toString());
            _prefix = "\n";
        }
        return builder;
    }

    private StringBuilder appendDeleted(StringBuilder builder, List<String> lines) {
        return appendLines(builder, lines, "-", Ansi.Color.RED);
    }

    private StringBuilder appendAdded(StringBuilder builder, List<String> lines) {
        return appendLines(builder, lines, "+", Ansi.Color.GREEN);
    }

    protected String prettyPrintDiff(List<String> expected, List<String> actual) {
        StringBuilder diffResult = new StringBuilder("\n");
        List<Delta<String>> deltas = DiffUtils.diff(expected, actual).getDeltas();

        for (Integer line=0; line < expected.size(); line++) {
            boolean alreadyPrinted = false;

            for (Delta<String> delta : deltas) {
                if (delta.getOriginal().getPosition() == line) {

                    if (delta.getType().equals(Delta.TYPE.CHANGE)) {
                        diffResult = appendDeleted(diffResult, delta.getOriginal().getLines()).append("\n");
                        diffResult = appendAdded(diffResult, delta.getRevised().getLines());
                        alreadyPrinted = true;
                    } else if (delta.getType().equals(Delta.TYPE.DELETE)) {
                        diffResult = appendDeleted(diffResult, delta.getOriginal().getLines());
                        alreadyPrinted = true;
                    } else if (delta.getType().equals(Delta.TYPE.INSERT)) {
                        diffResult = appendAdded(diffResult, delta.getRevised().getLines()).append("\n");
                    }

                    deltas.remove(delta);
                }
            }

            if (!alreadyPrinted) {
                diffResult.append(" ").append(expected.get(line));
            }
            diffResult.append("\n");
        }

        return diffResult.toString();
    }

    public abstract String prettyPrintDiff();
}

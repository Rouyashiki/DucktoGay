package org.rouyashiki.ducktogay;

import android.text.NoCopySpan;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class TextRewriter {
    private static final Pattern PROTECTED_PARTS = Pattern.compile(String.join("|",
            // Formatter arguments, including date/time conversions and literal percent signs.
            "%([0-9]+\\$)?[-#+ 0,(<]*[0-9]*(\\.[0-9]+)?[tT]?[a-zA-Z%]",
            "(?<![\\w$])[a-zA-Z][a-zA-Z0-9+.-]*://[^\\s<>\"']+",
            "mailto:[^\\s<>\"']+",
            "(?<![\\w/])(?:[a-zA-Z]:[\\\\/]|/|(?:res|assets)/)[^\\s<>\"']+",
            "(?<![\\w$/])\\[*L[\\w$/]+;",
            "(?<![\\w$])(?:[a-zA-Z_$][\\w$]*\\.)+[a-zA-Z_$][\\w$]*",
            "(?<![\\w$])[a-zA-Z$][a-zA-Z0-9$]*_[a-zA-Z0-9_$]+",
            "(?<![\\w$])[a-zA-Z_$][\\w$]*\\([^\\r\\n()]*\\)",
            "(?<![a-zA-Z0-9])(?:[0-9a-fA-F]{2}:)+[0-9a-fA-F]{2}(?![a-zA-Z0-9])",
            "(?<![a-zA-Z0-9])(?:0[xX][0-9a-fA-F]+|[0-9a-fA-F]{8,})(?![a-zA-Z0-9])",
            "<[/!a-zA-Z][^<>]*>",
            "\"[^\"\\r\\n]+\"\\s*:",
            "(?<![\\w])(?:CN|OU|O|L|ST|C|DC|UID|EMAILADDRESS)=[^\\r\\n]+"));

    private static final Pattern LICENSE_TEXT = Pattern.compile(
            "(?i)\\b(?:copyright|licensed under|permission is hereby granted|all rights reserved"
                    + "|(?:apache|mit|bsd|isc|mozilla public|gnu (?:lesser |affero )?general public)"
                    + " licen[cs]e)\\b");

    private TextRewriter() {}

    static CharSequence replace(CharSequence source) {
        if (source == null) return null;
        // Snapshot mutable CharSequences before computing replacement positions.
        String text = source.toString();
        Rewrite rewrite = rewrite(text, false, source instanceof Spanned);
        if (rewrite == null) return source;
        if (!(source instanceof Spanned styled)) return rewrite.text;

        SpannableString output = new SpannableString(rewrite.text);
        for (Object span : styled.getSpans(0, text.length(), Object.class)) {
            if (span instanceof NoCopySpan) continue;
            int start = remapPosition(rewrite.edits, styled.getSpanStart(span));
            int end = remapPosition(rewrite.edits, styled.getSpanEnd(span));
            int flags = styled.getSpanFlags(span);
            if (start == end && (flags & Spanned.SPAN_POINT_MARK_MASK)
                    == Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) continue;
            output.setSpan(span, start, end, flags);
        }
        return new SpannedString(output);
    }

    private static Rewrite rewrite(String text, boolean indexed, boolean keepEdits) {
        int offset = firstCandidate(text);
        if (offset < 0) return null;
        if (text.contains("-----BEGIN ") || LICENSE_TEXT.matcher(text).find()) return null;

        Matcher protectedParts = PROTECTED_PARTS.matcher(text);
        boolean hasProtectedPart = protectedParts.find();
        StringBuilder output = null;
        List<Edit> edits = keepEdits ? new ArrayList<>() : Collections.emptyList();
        int copied = 0;
        while (offset < text.length()) {
            while (hasProtectedPart && protectedParts.end() <= offset) {
                hasProtectedPart = protectedParts.find();
            }
            if (hasProtectedPart && protectedParts.start() <= offset) {
                offset = protectedParts.end();
                continue;
            }
            int limit = hasProtectedPart ? protectedParts.start() : text.length();
            TextRules.Rule rule = TextRules.matchAt(text, offset, limit);
            if (rule == null) {
                offset++;
                continue;
            }
            String replacement = indexed ? rule.indexedReplacement : rule.replacement;
            if (!rule.source.equals(replacement)) {
                if (output == null) output = new StringBuilder(text.length());
                output.append(text, copied, offset).append(replacement);
                copied = offset + rule.source.length();
                if (keepEdits && rule.source.length() != replacement.length()) {
                    edits.add(new Edit(offset, rule));
                }
            }
            // Advance in the original input, including already-transformed phrases.
            offset += rule.source.length();
        }
        if (output == null) return null;
        return new Rewrite(output.append(text, copied, text.length()).toString(), edits);
    }

    private static int firstCandidate(String text) {
        int offset = 0;
        while (offset < text.length()) {
            TextRules.Rule rule = TextRules.matchAt(text, offset, text.length());
            if (rule == null) {
                offset++;
            } else {
                if (!rule.source.equals(rule.replacement)) return offset;
                offset += rule.source.length();
            }
        }
        return -1;
    }

    private static int remapPosition(List<Edit> edits, int position) {
        int delta = 0;
        for (Edit edit : edits) {
            if (position <= edit.offset) break;
            if (position < edit.offset + edit.rule.source.length()) {
                return edit.offset + delta + edit.rule.remapBoundary(position - edit.offset);
            }
            delta += edit.rule.replacement.length() - edit.rule.source.length();
        }
        return position + delta;
    }

    static Object replaceResource(Object value) {
        if (value instanceof CharSequence text) return replace(text);
        if (value instanceof CharSequence[] texts) {
            CharSequence[] result = texts;
            for (int index = 0; index < texts.length; index++) {
                CharSequence replacement = replace(texts[index]);
                if (replacement != texts[index]) {
                    if (result == texts) result = texts.clone();
                    result[index] = replacement;
                }
            }
            return result;
        }
        return value;
    }

    // Rendering interfaces already carry UTF-16 offsets and measured runs.
    static Object replaceIndexed(Object value) {
        if (value instanceof String text) {
            Rewrite rewrite = rewrite(text, true, false);
            return rewrite == null ? value : rewrite.text;
        }
        if (value instanceof char[] text) {
            Rewrite rewrite = rewrite(new String(text), true, false);
            return rewrite == null ? value : rewrite.text.toCharArray();
        }
        return value;
    }

    private record Edit(int offset, TextRules.Rule rule) {}
    private record Rewrite(String text, List<Edit> edits) {}
}

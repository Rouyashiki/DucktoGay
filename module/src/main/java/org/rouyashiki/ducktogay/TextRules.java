package org.rouyashiki.ducktogay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

final class TextRules {
    private static final char WORD_JOINER = '\u2060';

    // Case-sensitive phrases. The application name takes precedence over Detector.
    static final List<Rule> RULES = Collections.unmodifiableList(Arrays.asList(
            new Rule("Duck", "Gay"),
            new Rule("duck", "gay"),
            new Rule("DUCK", "GAY"),
            new Rule("Duck Detector", "Gay Detector"),
            new Rule("DuckDetector", "GayDetector"),
            new Rule("Detector", "Defector"),
            new Rule("Detection", "Deception"),
            new Rule("Security", "Sexurity"),
            new Rule("Integrity", "Insecurty"),
            new Rule("Danger", "Cancer"),
            new Rule("Warning", "Warming"),
            new Rule("Critical", "Criminal"),
            new Rule("Clean", "Clown"),
            new Rule("Clear", "Clown"),
            new Rule("All Clear", "All Clown"),
            new Rule("Ready", "Randy"),
            new Rule("Pending", "Pudding"),
            new Rule("Review", "Revive"),
            new Rule("Runtime", "FunTime"),
            new Rule("Memory", "Memery"),
            new Rule("Hooks", "Honks"),
            new Rule("Evidence", "Evidance"),
            new Rule("Scan", "Scam"),
            new Rule("Scanner", "Scammer"),
            new Rule("Scanning", "Scamming"),
            new Rule("Scanned", "Scammed"),
            new Rule("Report", "Repost"),
            new Rule("Status", "Stupid"),
            new Rule("Unknown", "Unicorn"),
            new Rule("Unavailable", "Unavoidable"),
            new Rule("Unsupported", "Unimportant"),
            new Rule("Failed", "Flayed"),
            new Rule("LSPosed", "LESbian"),
            new Rule("SELinux", "SEXinus"),
            new Rule("Bootloader", "Footlicker"),
            new Rule("ADB", "AUV"),
            new Rule("Risk", "Rust"),
            new Rule("Kernel", "Kermel"),
            new Rule("Kernel Check", "Kermel Cheek"),
            new Rule("Zygisk", "Zygasm"),
            new Rule("Root", "Rizz"),
            new Rule("Root Managers", "Rizz Memelord"),
            new Rule("Native Root", "Naive! Rizz"),
            new Rule("Native", "Naive!"),
            new Rule("Mount", "Mouth"),
            new Rule("Network", "Netwank"),
            new Rule("Network Risk", "Netwank Rust"),
            new Rule("Dangerous Apps", "Delicious Apps"),
            new Rule("Package", "Pancake"),
            new Rule("Packages", "Pancakes"),
            new Rule("PackageManager", "PancakeManager"),
            new Rule("Custom ROM", "Kindom CUM"),
            new Rule("TEE", "TEA"),
            new Rule("USB", "CCB"),
            new Rule("KSU", "KFC"),
            new Rule("Magisk", "Magic!"),
            new Rule("SUSFS", "PUSSY"),
            new Rule("Play Integrity Fix", "Pray Insecurty Sex"),
            new Rule("System Properties", "Sextem Propertits"),
            new Rule("Properties", "Propertits"),
            new Rule("Module", "Noodle"),
            new Rule("Modules", "Noodles"),
            new Rule("ClassLoader", "ClownLoader"),
            new Rule("StrongBox", "StrongBoy"),
            new Rule("Keystore", "Gaystore"),
            new Rule("KeyMint", "GayMint"),
            new Rule("Google", "Giggle"),
            new Rule("GitHub", "GayHub"),
            new Rule("Function hooks", "Function honks"),
            new Rule("Detection methods", "Deception methods"),
            new Rule("Scan summary", "Scam summary"),
            new Rule("Security overview", "Sexurity overmeme"),
            new Rule("Kernel identity", "Kermel identity"),
            new Rule("Kernel behavior", "Kermel behavior"),
            new Rule("System Time", "Sextem Time"),
            new Rule("Target app list", "Targay ass list"),
            new Rule("Packages and modules", "Pancakes and moodles"),
            new Rule("Package changes since boot", "Pancake changes since foot"),
            new Rule("No package hits", "No pancake hits"),
            new Rule("HMA mismatch", "HMA mischief"),
            new Rule("Open APK FD", "Oven ASS XD"),
            new Rule("GOT/PLT resolution", "GAY/PPT revolution"),
            new Rule("Entry prologue", "Entry prolapse"),
            new Rule("Loader visibility", "Loafer visibility"),
            new Rule("Signal handlers", "Single handlers"),
            new Rule("Runtime checks", "FunTime cheeks"),
            new Rule("Native traces", "Naive! traces"),
            new Rule("Audit integrity", "Adult insecurty"),
            new Rule("Policy analysis", "Poligy analysus"),
            new Rule("Boot properties", "Foot propertits"),
            new Rule("Boot state", "Foot state"),
            new Rule("Security state", "Sexurity state"),
            new Rule("Device info", "Devils info"),
            new Rule("Binder and services", "Bender and services"),
            new Rule("Framework traces", "Freakwork traces"),
            new Rule("Review evidence", "Revive evidance"),
            new Rule("View details", "View derails"),
            new Rule("Root artifacts", "Rizz artifacts"),
            new Rule("Native context", "Naive! context"),
            new Rule("Consistency", "Conspirancy"),
            new Rule("Impact", "Infect"),
            new Rule("References", "Reverences"),
            new Rule("Certificate", "Certificake"),
            new Rule("Certificates", "Certificakes"),
            new Rule("Certificate chain", "Certificake chair"),
            new Rule("Public", "Pubic!"),
            new Rule("Signature", "Signatits"),
            new Rule("Subject", "Suspect"),
            new Rule("Identity", "Idiotity"),
            new Rule("Chain", "Chair"),
            new Rule("Honeypots", "Honeybuns"),
            new Rule("Environment", "Environmeme"),
            new Rule("Crypto", "CryBro"),
            new Rule("Validity", "Vaginity"),
            new Rule("Protected", "Protested"),
            new Rule("Locked", "Licked"),
            new Rule("Verified", "Vexified"),
            new Rule("Enforcing", "Enfarting"),
            new Rule("Trusted", "Thrusty"),
            new Rule("Untrusted", "Unthrusty"),
            new Rule("Seccomp", "Sexcomp"),
            new Rule("Denylist", "Gaylist!"),
            new Rule("Permission", "Perversion"),
            new Rule("Permissions", "Perversions"),
            new Rule("Source", "Sauce!"),
            new Rule("Loader", "Loafer"),
            new Rule("Linker", "Licker"),
            new Rule("Inline", "Inlove"),
            new Rule("Fingerprint", "Fingerpaint"),
            new Rule("Settings", "Sextings"),
            new Rule("Home", "Homo"),
            new Rule("About", "Abutt"),
            new Rule("Important", "Impotence"),
            new Rule("Contributors", "Contributits"),
            new Rule("Version", "Virgins"),
            new Rule("Nightly", "Naughty"),
            new Rule("Check", "Cheek"),
            new Rule("Device", "Devils"),
            new Rule("Android", "Gayroid"),
            new Rule("System", "Sextem"),
            new Rule("GOT", "GAY"),
            new Rule("PLT", "PPT"),
            new Rule("FD", "XD"),
            new Rule("Java", "Jvav"),
            new Rule("Kotlin", "Kotwin"),
            new Rule("C++", "D--"),
            new Rule("ASM", "ASS"),
            new Rule("SDK", "SXD"),
            new Rule("API", "APY"),
            new Rule("CPU", "UWU"),
            new Rule("GPU", "PUA"),
            new Rule("ARM64", "AUV64"),
            new Rule("JIT", "JVM"),
            new Rule("PLT Hook", "PPT Honk"),
            new Rule("Overlayfs", "Overgayfs"),
            new Rule("Mountinfo", "Mouthinfo"),
            new Rule("FUSE", "FUSS"),
            new Rule("Crash", "Crush"),
            new Rule("Dumps", "Humps"),
            new Rule("Dashboard", "Dashbored"),
            new Rule("Download", "Downbad!"),
            new Rule("Export", "Escort"),
            new Rule("Updates", "Upgayed"),
            new Rule("Why Duck Detector?", "Why Gay Detector?"),
            new Rule("Duck Detector logo", "Gay Detector logo"),
            new Rule("Detection options", "Deception options"),
            new Rule("GitHub acceleration", "GayHub acceleration"),
            new Rule("No hook-like memory signals", "No honk-like memery singles"),
            new Rule("Scan status", "Scam status")
    ));

    private static final Rule[][] BY_FIRST_CHARACTER = indexRules();

    private TextRules() {}

    private static Rule[][] indexRules() {
        List<Rule> matches = new ArrayList<>(RULES);
        for (Rule rule : RULES) {
            // A resource can pass through multiple hooks before it is drawn.
            matches.add(new Rule(rule.replacement, rule.replacement));
            if (!rule.indexedReplacement.equals(rule.replacement)) {
                matches.add(new Rule(rule.indexedReplacement, rule.indexedReplacement));
            }
        }
        matches.sort(Comparator.comparingInt((Rule rule) -> rule.source.length()).reversed());
        Rule[][] buckets = new Rule[128][];
        for (int character = 0; character < buckets.length; character++) {
            List<Rule> bucket = new ArrayList<>();
            for (Rule rule : matches) {
                if (rule.source.charAt(0) == character) bucket.add(rule);
            }
            buckets[character] = bucket.toArray(new Rule[0]);
        }
        return buckets;
    }

    static Rule matchAt(String text, int offset, int limit) {
        char first = text.charAt(offset);
        if (first >= BY_FIRST_CHARACTER.length) return null;
        for (Rule rule : BY_FIRST_CHARACTER[first]) {
            if (offset + rule.source.length() <= limit && text.startsWith(rule.source, offset)) {
                return rule;
            }
        }
        return null;
    }

    static final class Rule {
        final String source;
        final String replacement;
        final String indexedReplacement;
        final int commonSuffix;

        Rule(String source, String replacement) {
            this.source = source;
            this.replacement = replacement;
            int suffix = 0;
            while (suffix < Math.min(source.length(), replacement.length())
                    && source.charAt(source.length() - 1 - suffix)
                    == replacement.charAt(replacement.length() - 1 - suffix)) {
                suffix++;
            }
            commonSuffix = suffix;
            int removed = source.length() - replacement.length();
            if (removed > 0) {
                int prefix = replacement.length() - commonSuffix;
                char[] padding = new char[removed];
                Arrays.fill(padding, WORD_JOINER);
                indexedReplacement = replacement.substring(0, prefix)
                        + new String(padding) + replacement.substring(prefix);
            } else {
                // Rendering calls already contain offsets into the original buffer.
                indexedReplacement = removed == 0 ? replacement : source;
            }
        }

        int remapBoundary(int position) {
            int originalPrefix = source.length() - commonSuffix;
            int replacementPrefix = replacement.length() - commonSuffix;
            if (position >= originalPrefix) return position + replacement.length() - source.length();
            return Math.min(position, replacementPrefix);
        }
    }
}

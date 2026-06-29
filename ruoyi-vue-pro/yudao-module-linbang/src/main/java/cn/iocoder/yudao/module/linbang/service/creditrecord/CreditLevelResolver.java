package cn.iocoder.yudao.module.linbang.service.creditrecord;

public final class CreditLevelResolver {

    public static final int EXCELLENT_MIN_SCORE = 120;
    public static final int NORMAL_MIN_SCORE = 90;
    public static final int WARNING_MIN_SCORE = 60;

    private CreditLevelResolver() {
    }

    public static String resolve(Integer score) {
        int safeScore = score == null ? 100 : score;
        if (safeScore >= EXCELLENT_MIN_SCORE) {
            return "EXCELLENT";
        }
        if (safeScore >= NORMAL_MIN_SCORE) {
            return "NORMAL";
        }
        if (safeScore >= WARNING_MIN_SCORE) {
            return "WARNING";
        }
        return "DISABLED";
    }

    public static String nextLevel(String creditLevel) {
        if ("WARNING".equalsIgnoreCase(creditLevel) || "DISABLED".equalsIgnoreCase(creditLevel)) {
            return "NORMAL";
        }
        if ("NORMAL".equalsIgnoreCase(creditLevel)) {
            return "EXCELLENT";
        }
        return null;
    }

    public static Integer nextLevelNeedScore(Integer score) {
        int safeScore = score == null ? 100 : score;
        String level = resolve(safeScore);
        if ("DISABLED".equals(level) || "WARNING".equals(level)) {
            return Math.max(0, NORMAL_MIN_SCORE - safeScore);
        }
        if ("NORMAL".equals(level)) {
            return Math.max(0, EXCELLENT_MIN_SCORE - safeScore);
        }
        return 0;
    }
}

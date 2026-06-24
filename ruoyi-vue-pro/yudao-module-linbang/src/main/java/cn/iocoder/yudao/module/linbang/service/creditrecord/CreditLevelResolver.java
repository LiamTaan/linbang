package cn.iocoder.yudao.module.linbang.service.creditrecord;

final class CreditLevelResolver {

    private CreditLevelResolver() {
    }

    static String resolve(Integer score) {
        int safeScore = score == null ? 100 : score;
        if (safeScore >= 120) {
            return "EXCELLENT";
        }
        if (safeScore >= 90) {
            return "NORMAL";
        }
        if (safeScore >= 60) {
            return "WARNING";
        }
        return "RISK";
    }
}

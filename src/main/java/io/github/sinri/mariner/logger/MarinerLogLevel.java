package io.github.sinri.mariner.logger;

public enum MarinerLogLevel {
    DEBUG, INFO, NOTICE, WARNING, ERROR, FATAL, SILENT;

    public boolean isEnoughSeriousAs(MarinerLogLevel standardLevel) {
        return this.ordinal() >= standardLevel.ordinal();
    }

    public boolean isNegligibleThan(MarinerLogLevel standardLevel) {
        return this.ordinal() < standardLevel.ordinal();
    }

    /**
     * @return should always be silent
     * @since 1.10
     */
    public boolean isSilent() {
        return this.ordinal() >= SILENT.ordinal();
    }
}
